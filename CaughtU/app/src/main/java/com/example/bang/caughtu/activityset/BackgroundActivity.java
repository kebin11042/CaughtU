package com.example.bang.caughtu.activityset;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;
import com.example.bang.caughtu.R;

/**
 * Created by BANG on 2015-07-30.
 */
public class BackgroundActivity extends Activity implements View.OnClickListener, PatternView.OnPatternDetectedListener{

    private final int RESULT_SELECT_BACKGROUND = 30;

    private int windowWidth;
    private int windowHeight;

    //로컬 DB
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String userImagePath;

    private ImageView imgvBackground;
    private LinearLayout layLoad, layApply, layDefault;

    //그냥 장식용
    private PatternView ptvBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display disp = getWindowManager().getDefaultDisplay();
        windowWidth = disp.getWidth();
        windowHeight = disp.getHeight();

        preferences = getSharedPreferences("pref", MODE_PRIVATE);
        editor = preferences.edit();

        userImagePath = preferences.getString("IMAGE", "NOT");

        setContentView(R.layout.activity_background);

        Init();
    }

    public void Init(){
        ptvBackground = (PatternView) findViewById(R.id.ptvBackround);

        imgvBackground = (ImageView) findViewById(R.id.imgvBackgroundBackground);
        layLoad = (LinearLayout) findViewById(R.id.layBackgroundLoad);
        layApply = (LinearLayout) findViewById(R.id.layBackgroundApply);
        layDefault = (LinearLayout) findViewById(R.id.layBackgroundDefault);

        ptvBackground.setOnPatternDetectedListener(BackgroundActivity.this);

        layLoad.setOnClickListener(BackgroundActivity.this);
        layApply.setOnClickListener(BackgroundActivity.this);
        layDefault.setOnClickListener(BackgroundActivity.this);

        //기존에 설정한 배경화면이 있다면 그것으로 설정한다.
        if(!userImagePath.equals("NOT")){

            Bitmap bmPhoto = BitmapFactory.decodeFile(userImagePath);
            bmPhoto = isBitmapLarge(bmPhoto);

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

            //비트맵 객체 배경으로 적용
            imgvBackground.setImageBitmap(bmPhoto);
        }
    }

    @Override
    public void onPatternDetected() {
        ptvBackground.clearPattern(180);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == layLoad.getId()){
            //갤러리 인텐트 띄우기
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_SELECT_BACKGROUND);
        }
        else if(view.getId() == layApply.getId()){
            if(userImagePath != null && !userImagePath.equals("NOT")){
                editor.putString("IMAGE", userImagePath);
                editor.apply();
                Toast.makeText(BackgroundActivity.this, "배경이미지를 적용하였습니다", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(BackgroundActivity.this, "먼저 이미지를 선택해 주세요", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.getId() == layDefault.getId()){
            editor.remove("IMAGE");
            editor.apply();

            userImagePath = "NOT";
            imgvBackground.setImageResource(R.drawable.background);
            Toast.makeText(BackgroundActivity.this, "기본 이미지로 적용하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //갤러리 인텐트가 종료되고 반환되는 값 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_SELECT_BACKGROUND){
            if(resultCode == RESULT_OK && data != null){
                final Uri selectImageUri = data.getData();
                final String[] filePathColumn = {MediaStore.Images.Media.DATA};

                final Cursor imageCursor = this.getContentResolver().query(selectImageUri, filePathColumn, null, null, null);
                imageCursor.moveToFirst();

                final int columnIndex = imageCursor.getColumnIndex(filePathColumn[0]);
                userImagePath = imageCursor.getString(columnIndex);
                imageCursor.close();

                Bitmap bmPhoto = BitmapFactory.decodeFile(userImagePath);
                bmPhoto = isBitmapLarge(bmPhoto);
                //비트맵 객체 배경으로 적용
                imgvBackground.setImageBitmap(bmPhoto);

                //Log.w("ratio", "window = " + (float)windowHeight/windowWidth + ", bitmap = " + (float)bmPhoto.getHeight()/bmPhoto.getWidth());

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
}
