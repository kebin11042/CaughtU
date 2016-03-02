package com.example.bang.caughtu.activityset;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;
import com.example.bang.caughtu.R;
import com.example.bang.caughtu.customviewset.PatternlostDialog;
import com.example.bang.caughtu.data.MailClass;
import com.example.bang.caughtu.services.LockService;
import com.sun.mail.imap.protocol.BASE64MailboxEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by BANG on 2015-07-25.
 */
public class PatternlockscreenActivity extends Activity implements PatternView.OnPatternDetectedListener,
        View.OnClickListener, SurfaceHolder.Callback, LocationListener{

    //로컬 간단한 DB
    private SharedPreferences preferences;

    //스마트폰 화면 가로, 세로
    private int windowWidth;
    private int windowHeight;

    //MialClass 불러오기
    private static String SenderEmail = "unbox";        //보내는 메일 계정
    private static String SenderPassword = "dbsksl04";                //보내는 메일 패스워드
    private MailClass mail;
    private SendMail task_SendMail;
    private static boolean SendMailOk;

    //위치정보 불러오기 위한 로케이션 메니져, 위치 제공 String
    private LocationManager locationManager;
    private String provider;
    private String strLocation;     //주소 정보
    private double lat, lng;        //위도 경도 정보

    //패턴 에러 횟수
    private int nPatternErr;
    //패턴뷰 추가할 레이아웃
    private LinearLayout layPatternView;
    //패턴뷰
    private PatternView patternView;
    //패턴 전송 버튼
    private Button btnLost;
    //사용자 정보
    private String userPattern, userEmail, userImagePath;
    private String lockOK;  //잠금화면을 띄울지 결정하는 변수
    private boolean bLockOK;

    //카메라
    private Camera camera;
    private Camera.Parameters parameters;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera.PictureCallback pictureCallback;

    //이미지 파일 이름, 경로
    private static String imgFileName, imgFilePath;
    //배경화면 이미지 뷰
    private ImageView imgvBackground;

    //홈 런처 앱 정보
    private String strActivity;
    private String strPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patternlockscreen);

        //SenderEmail = Base64.encodeToString(SenderEmail.getBytes(), 0);
        //SenderPassword = Base64.encodeToString(SenderPassword.getBytes(), 0);

        Display disp = getWindowManager().getDefaultDisplay();
        windowWidth = disp.getWidth();
        windowHeight = disp.getHeight();

        //기존의 잠금화면 보다 더 위로 올라오게 한다, 기본 잠금화면을 없애라고 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        //패턴 에러 횟수는 처음에 0이다
        nPatternErr = 0;

        //데이터 불러오기
        preferences = getSharedPreferences("pref", MODE_PRIVATE);

        userPattern = preferences.getString("PATTERN", "NOT");
        userEmail = preferences.getString("EMAIL", "NOT");
        userImagePath = preferences.getString("IMAGE", "NOT");
        lockOK = preferences.getString("LOCK", "NOT");

        //홈 런처 어플 정보 불러오기
        getDefaultHomeApp();

        //OK가 아니라면 액티비티 종료 -> OK가 아니라는 뜻은 서비스에서 실행되는 패턴락이 아니라는것을 뜻함
        //패턴을 정상적으로 풀었다면 "EXIT"가 들어있었을 것이다
        //NOT일 경우 처음에 리부트 했을때 계속 반복되는 오류를 방지하기 위함
        //Log.w("lockOK", " ======= " + lockOK);
        if(!lockOK.equals("OK")){
            bLockOK = false;

            finish();
        }
        else{

            //이 경우는 거의 없겠지만
            //CaughtU 어플을 설치하고 사용자 정보를 설정하지 않고 종료후에 기본 홈을 CaughtU 어플로 적용하였을 경우
            //사용자 정보가 없기 때문에 잠금 패턴의 의미가 없다. 즉 "오류"
            //이 액티비티를 종료하고 Splash화면을 띄워서 사용자 정보를 설정하도록 유도한다
            if(userPattern.equals("NOT") || userEmail.equals("NOT")){
                Toast.makeText(PatternlockscreenActivity.this, "사용하시기 전에 먼저 사용자 정보를 입력해 주세요", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PatternlockscreenActivity.this, SplashActivity.class);

                startActivity(intent);
            }

            bLockOK = true;
            SendMailOk = true;

            setLocationInit();

            //초기화 메소드
            Init();

            //카메라 세팅 메소드
            setCamera();
        }


        //현재 실행되고 있는 모든 서비스를 알고 만약 CaughtU어플의 LockService가 실행되고 있지 않다면 실행시킨다.
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        boolean isLockServiceRunning = false;
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);
        for(int i=0; i<serviceInfos.size() ;i ++){
            if(serviceInfos.get(i).service.getClassName().equals("com.example.bang.coughtu.services.LockService")){
                isLockServiceRunning = true;
            }
        }

        if(!isLockServiceRunning){
            //락패턴 스크린 실행
            Intent intent = new Intent(PatternlockscreenActivity.this, LockService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(bLockOK){
            //패턴뷰 동적 생성 및 추가, 리스너 등록
            if(patternView == null){
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                patternView = (PatternView) inflater.inflate(R.layout.view_patternlockscreen, null);
                layPatternView.addView(patternView);
                patternView.setOnPatternDetectedListener(PatternlockscreenActivity.this);
            }

            //위치가 갱신 리스너 등록 -> 1000은 1초마다, 1은 거리
            locationManager.requestLocationUpdates(provider, 1000, 1, PatternlockscreenActivity.this);
            surfaceView.setVisibility(View.VISIBLE);
        }

        Toast.makeText(PatternlockscreenActivity.this, SenderEmail + ", " + SenderPassword, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(bLockOK){
            //위치 갱신 리스너 해제
            locationManager.removeUpdates(PatternlockscreenActivity.this);
            //카메라뷰 끄기 -> 베터리 아끼기 위해 화면에서 보이지 않을 때는 잠시 카메라를 해제한다
            surfaceView.setVisibility(View.GONE);
        }
    }

    public void Init(){
        //null로 초기화 -> onCreate에서가 아닌 resume에서 동적으로 생성한다.(부팅시 에러때문)
        patternView = null;

        layPatternView = (LinearLayout) findViewById(R.id.layPatternlockscreen);

        imgvBackground = (ImageView) findViewById(R.id.imgvPatternlockscreenBack);

        //배경화면 설정
        setBackgroundImage();

        surfaceView = (SurfaceView) findViewById(R.id.sufvPatternlockscreen);

        btnLost = (Button) findViewById(R.id.btnPatternlockscreenLost);

        //리스너 등록
        btnLost.setOnClickListener(PatternlockscreenActivity.this);
    }

    //사용자가 설정한 백그라운드로 적용하는 메소드
    public void setBackgroundImage(){
        if(!userImagePath.equals("NOT")){
            Bitmap bmPhoto = BitmapFactory.decodeFile(userImagePath);
            bmPhoto = isBitmapLarge(bmPhoto);

            imgvBackground.setImageBitmap(bmPhoto);

            //비트맵 화면에서 사이지 비율 조절
            if((float)windowHeight/windowWidth >= (float)bmPhoto.getHeight()/bmPhoto.getWidth()){
                FrameLayout.LayoutParams layoutParams =
                        new FrameLayout.LayoutParams((int) (bmPhoto.getWidth() * (float)windowHeight/bmPhoto.getHeight()), windowHeight, Gravity.CENTER);
                imgvBackground.setLayoutParams(layoutParams);
            }
            else{
                FrameLayout.LayoutParams layoutParams =
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                imgvBackground.setLayoutParams(layoutParams);
            }
        }
    }

    //사진 사이즈 측정하여 너무 크면 줄여주는 메소드
    public Bitmap isBitmapLarge(Bitmap bitmap){
        if(bitmap.getWidth() >= 4096 || bitmap.getHeight() >= 4096){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(userImagePath, options);
        }

        return bitmap;
    }

    //뒤로가기 버튼을 눌렀을 때 액티비티가 종료되면 안되기 위해
    @Override
    public void onBackPressed() { }


    public void setLocationInit(){
        //로케이션 메니져 객체 생성
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //위치정보를 받기위한 하드웨어 목록
        Criteria criteria = new Criteria();
        //위치정보를 받기 위한 최적의 하드웨어를 받는다
        provider = null;
        provider = locationManager.getBestProvider(criteria, true);
        //최적의 하드웨어를 못찾을 경우 또는 사용 불가할 경우 위치정보를 제공하는 모든것을 찾는다
        if(provider == null || !locationManager.isProviderEnabled(provider)) {
            List<String> list = locationManager.getAllProviders();

            for (int i = 0; i < list.size(); i++) {
                String pro = list.get(i);

                if (locationManager.isProviderEnabled(pro)) {
                    provider = pro;
                    break;
                }
            }
        }

        //마지막으로 있던 위치 얻기
        Location location = locationManager.getLastKnownLocation(provider);
        if(location == null){
            strLocation = "기기로부터 위치 제공 받기를 실패하였습니다...";
        }
        else{
            onLocationChanged(location);
        }
    }

    //위도, 경도를 주소로 리턴
    public String getAddress(double lat, double lng) {
        String strAddress = "위치 주소 얻기 실패...";

        Geocoder geocoder = new Geocoder(PatternlockscreenActivity.this, Locale.getDefault());

        List<Address> addrList = null;

        try {
            addrList = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //주소 없을 시 null 반환
        if(addrList == null){

        }
        else{
            if(addrList.size() > 0){
                Address address = addrList.get(0);
                //주소를 String으로 얻는다.
                strAddress = address.getAddressLine(0);
            }
        }

        return strAddress;
    }

    //카메라 초기화 메소드
    public void setCamera(){
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(PatternlockscreenActivity.this);

        //허니컴 버전(3.0) 부터는 사용 안해도 된다.
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //전면 카메라 ID 찾는 메소드
    public int findFrontSideCamera(){
        int cameraId = -1;
        int cameraNum = Camera.getNumberOfCameras();

        for(int i=0;i<cameraNum;i++){
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                cameraId = i;
                break;
            }
        }

        return cameraId;
    }


    //위치가 바뀔 때 호출되는 메소드
    @Override
    public void onLocationChanged(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();

        strLocation = getAddress(lat, lng);

        //Log.w("LocationChange", strLocation);
    }

    //나머지 메소드는 사용 안함
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    //패턴 입력후 발생하는 리스너
    @Override
    public void onPatternDetected() {
        patternView.clearPattern(180);

        //패턴이 일치 한다면
        if(patternView.getPatternString().equals(userPattern)){
            //정상적으로 종료되었다면 더이상 띄우지 않게 할려고
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("LOCK", "EXIT");
            editor.apply();

            finish();
        }
        else{
            //5회 이상 틀리면 Camera takepicture
            Toast.makeText(PatternlockscreenActivity.this, "패턴 불일치!!", Toast.LENGTH_SHORT).show();
            if(nPatternErr >= 4){
                if(camera != null && SendMailOk){
                    camera.takePicture(null, null, pictureCallback);
                    task_SendMail = new SendMail(0);
                    task_SendMail.execute("1");
                }
            }
            else{
                ++nPatternErr;
            }
        }
    }


    @Override
    public void onClick(View view) {
        //비밀번호를 잊어 버렸을 때
        if(view.getId() == btnLost.getId()){
            PatternlostDialog dialog = new PatternlostDialog(PatternlockscreenActivity.this);
            dialog.show();
        }
    }

    public void executeTaskPatternLost(){
        task_SendMail = new SendMail(1);
        task_SendMail.execute("");
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.w("surfaceView", "surfaceCreate()");
        int cameraId = findFrontSideCamera();

        if(cameraId == -1){
            camera = Camera.open();
        }
        else{
            camera = Camera.open(cameraId);
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            //camera.setDisplayOrientation(90);
        } catch (IOException e) {

            Toast.makeText(PatternlockscreenActivity.this, "Camera Preview Display 실패..", Toast.LENGTH_SHORT).show();

            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Log.w("surfaceView", "surfaceChanged()");
        parameters = camera.getParameters();

        camera.setParameters(parameters);
        camera.startPreview();

        //카메라로 사진을 찍었을 때 하는 행동
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                //Toast.makeText(PatternlockscreenActivity.this, "찰칵!", Toast.LENGTH_SHORT).show();
                File filePicture = getOutputMediaFile();

                if(filePicture == null){

                }
                else{
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(filePicture);
                        fileOutputStream.write(bytes);
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //카메라 프리뷰 다시 시작
                    camera.startPreview();
                }
            }
        };
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.w("surfaceView", "surfaceDestroyed()");
        camera.stopPreview();
        camera.release();
        camera = null;
    }


    //캡쳐 이미지 파일 객체로 만들기
    private static File getOutputMediaFile() {
        // SD카드가 마운트 되어있는지 먼저 확인해야합니다
        // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "CaughtU");
        // 굳이 이 경로로 하지 않아도 되지만 가장 안전한 경로이므로 추천함.

        // 없는 경로라면 따로 생성한다.
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // 파일명을 적당히 생성. 여기선 시간으로 파일명 중복을 피한다.
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddhhmmss");
        String str = dayTime.format(new Date(time));

        File mediaFile;

        imgFileName = str;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + str + ".jpg");
        imgFilePath = mediaStorageDir.getPath() + File.separator + str + ".jpg";

        return mediaFile;
    }


    //도난 경고 메일보내는 메소드
    public void sendMailPatternErr(){
        //메일 보낼 계정 정보
        String strBody_msg = "CaughtU가 잠금 해제 시도를 감지했습니다.";
        String strBody_time = "";
        String strBody_loc = "";
        String strBody = "";

        //현재 시간 및 날짜 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm");

        strBody_time = "시간 : " + simpleDate.format(date) + " " + simpleTime.format(date);
        strBody_loc = "위치 : " + strLocation;

        //바디 메시지 다 합치기
        strBody = strBody_msg + "\n" + strBody_time + "\n" + strBody_loc;
        //Log.w("strBody", strBody);

        //메일 객체 생성
        mail = new MailClass(SenderEmail, SenderPassword);
        String[] toArr = {userEmail};
        mail.setTo(toArr);
        mail.setFrom(SenderEmail);
        mail.setSubject("[CaughtU] 누군가 귀하의 기기를 잠금해제 하려고 합니다!");
        mail.setBody(strBody);
        try {
            mail.addAttachment(imgFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(mail.send()){
                //Toast.makeText(PatternlockscreenActivity.this, "이메일이 정상적으로 보내졌습니다", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(PatternlockscreenActivity.this, "이메일이 전송 실패....", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //패턴 잊어버린 메일보내는 메소드
    public void sendMailPatternLost(){
        String strBodyContent = "분실하신 패턴을 안내해드립니다.";
        String strBodyNum = "패턴기준\n① ② ③\n④ ⑤ ⑥\n⑦ ⑧ ⑨";
        String strBodyMy = "나의 패턴";
        //메일 객체 생성
        mail = new MailClass(SenderEmail, SenderPassword);
        String[] toArr = {userEmail};
        mail.setTo(toArr);
        mail.setFrom("CaughtU@company.com");
        mail.setSubject("[CaughtU] 분실하신 패턴을 안내해드립니다.");
        //body메세지 합치기
        String strBody = strBodyContent + "\n\n" + strBodyNum + "\n\n" + strBodyMy + "\n" + PatternToString(userPattern);
        mail.setBody(strBody);

        try {
            if(mail.send()){
                //Toast.makeText(PatternlockscreenActivity.this, "이메일이 정상적으로 보내졌습니다", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(PatternlockscreenActivity.this, "이메일 전송 실패....", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //패턴을 숫자식의 String으로 변경(1>3>5>2 이런식으로)
    public String PatternToString(String strPattern){
        //원래의 포맷은 001-000 & 002-002 & 이런식
        // '&'로 자른다 -> 001-000, 002-002 이런식으로
        String ret = "";
        String[] cutPatterns = strPattern.split("&");
        int[] nX = new int[cutPatterns.length];
        int[] nY = new int[cutPatterns.length];
        //'&'로 자른걸 '-'로 잘라서 int형으로 배열로 넣는다 -> 1 0 2 2 x y x y 순서로
        for(int i=0;i<cutPatterns.length;i++){
            String[] nXY = cutPatterns[i].split("-");
            nX[i] = Integer.parseInt(nXY[0]);
            nY[i] = Integer.parseInt(nXY[1]);
        }

        //이부분이 중요한데 x는 하나 증가할수록 3이 증가하고 1->4->7 ,y는 하나 증가할 수록 1증가
        for(int i=0;i<nX.length;i++){
            int number = 1;

            number = number + ( nX[i] * 3 ) + nY[i];

            ret = ret + number + "";
            if(i != nX.length -1 ){
                ret = ret + " > ";
            }
        }

        return ret;
    }

    //이걸 해 주는 이유는 액티비티가 닫혔을 때 surfaveVIew가 아주 잠깐 보이는데 보이면 안되기 때문에
    @Override
    public void finish() {
        if(bLockOK){
            surfaceView.setVisibility(View.GONE);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setClassName(strPackageName, strActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        super.finish();
    }

    //패턴잠금 화면에서 빠른시간내에 패턴을 자꾸 틀리면 카메라와 메일전송이 부담을 느껴 에러가 날 수 있다.
    //이를 쓰레드로 딜레이를 주어 해결하는 메소드이다
    public void DelaySendingMail(){
        //3초 대기

        DelaySendMail task = new DelaySendMail();
        task.execute("1");

    }

    public static class DelaySendMail extends AsyncTask<String, Integer, String> {

        private final int DELAY_TIMES = 3000;

        public DelaySendMail() {
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(DELAY_TIMES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            SendMailOk = true;
        }
    }

    //이메일 보내는 쓰레드 -> 안드로이드에서는 메인 쓰레드를 이용하지 않기 때문에 AsyncTask 이용
    public class SendMail extends AsyncTask<String, Integer, String> {

        //sel이 0이면 패턴오류 전송, 1이면 패턴 분실 전송
        int sel;

        public SendMail(int sel){
            this.sel = sel;
        }

        @Override
        protected String doInBackground(String... strings) {

            //SendMailOk 가 필요한 이유는 사용자가 엄청 빠르게 계속 패턴이 틀리면 카메라가 빨리 사진을 찍고 메일도 빠르게 전송해야 할것이다.
            //이렇게 되면 카메라가 부담을 엄청 느끼게 되어 오류가 날 수 있다.
            //SendMailOk를 통해 메일을 전송을 하고 나서 3초의 딜레이를 주고 보내도록 기능을 구현
            if(SendMailOk) {
                SendMailOk = false;
                if(sel == 0){
                    sendMailPatternErr();
                }
                else{
                    sendMailPatternLost();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            DelaySendingMail();
        }
    }




    public void getDefaultHomeApp() {
        final PackageManager packageManager = this.getApplicationContext().getPackageManager();

        //3사 기본 홈 런처 정보
//        final String strActLg = "com.lge.launcher2.Launcher";
//        final String strPackLG = "com.lge.launcher2";
//        final String strActPantech = "com.pantech.launcher3.Launcher";
//        final String strPackPantech = "com.pantech.launcher3";
//        final String strActSamsung = "com.android.launcher2.Launcher";
//        final String strPackSamsung = "com.sec.android.app.launcher";
//        final String strActEasySamnsung = "com.sec.android.app.easylauncher";
//        final String strPackEasySamsung = "com.sec.android.app.easylauncher.Launcher";


        //Action 이 main이며 category가 HOME, DEFAULT 조건이 홈런처 어플리케이션이다.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        //첫번째로 설정되어 있는 홈 런처 어플로 실행하게 한다.
        strPackageName = list.get(0).activityInfo.packageName;
        strActivity = list.get(0).activityInfo.name;

        for(int i=0;i<list.size();i++){
            String strPack = list.get(i).activityInfo.packageName;
            //삼성은 easylauncher 를 피하기 위해
            if(strPack.contains("sec")){
                if(strPack.contains("easy")){

                }
                else{
                    strPackageName = strPack;
                    strActivity = list.get(i).activityInfo.name;
                }
            }
            //lg 기기를 쓰는 이용자
            else if(strPack.contains("lge")){
                strPackageName = strPack;
                strActivity = list.get(i).activityInfo.name;
            }
            //pantech 기기를 쓰는 이용자
            else if(strPack.contains("pantech")){
                strPackageName = strPack;
                strActivity = list.get(i).activityInfo.name;
            }
        }
    }
}
