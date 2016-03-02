package com.example.bang.caughtu.activityset;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bang.caughtu.R;

/**
 * Created by BANG on 2015-07-24.
 */
public class UseActivity extends Activity implements View.OnClickListener{

    private ImageButton ibtnBack, ibtnMenu;
    private TextView txtvUse;
    private String strUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_use);

        setStrUse();

        Init();
    }

    public void setStrUse(){
        strUse = "▼ 초기 설정\n" +
                "* 반드시 초기 설정을 해주셔야 정상적인 앱 이용이 가능합니다. *\n" +
                "\n" +
                "<홈 화면 설정>\n" +
                "CaughtU로 홈 화면 설정을 해주십시오.\n" +
                "홈 설정은 스마트폰 기종별, 제조사별로 다를 수 있습니다.\n" +
                "\n" +
                "삼성 : 환경설정 → 애플리케이션 → 전체 탭 → 기본홈 선택 → 기본정보지우기 선택 → 기본값으로 사용 체크 → CaughtU 선택\n" +
                "LG : 시스템 설정 → 홈 화면 → 홈 선택 → CaughtU 체크\n" +
                "\n" +
                "<기존 잠금 화면 해제>\n" +
                "정상적인 앱 이용을 위해서, 기존의 잠금 화면을 해제해주십시오.\n\n\n" +
                "▼ 기본 설정 방법\n" +
                "\n" +
                "1. 설치 후 앱을 열어주세요.\n" +
                "2. 메일주소를 입력하시고 '확인' 버튼을 클릭하세요.\n" +
                "3. 패턴을 입력하시고 '확인' 버튼을 클릭하세요.\n" +
                "\n" +
                "\n" +
                "▼ 테스트 방법\n" +
                "\n" +
                "1. 잠금 화면에서 패턴을 5회 틀리게 입력하세요.\n" +
                "2. 메일함에서 CaughtU의 메일을 확인하세요.\n" +
                "3. 시간, 위치정보, 사진을 확인해보세요.\n" +
                "\n" +
                "\n" +
                "▼ 패턴 변경 방법\n" +
                "\n" +
                "1. 앱을 열고 '잠금패턴변경' 버튼을 클릭하세요.\n" +
                "2. 절차에 따라 패턴을 입력하면 변경 완료.\n" +
                "\n" +
                "\n" +
                "▼ 메일 주소 변경 방법\n" +
                "\n" +
                "1. 앱을 열고 '메일주소변경' 버튼을 클릭하세요.\n" +
                "2. 메일 주소를 적고 '확인' 버튼을 누르면 변경 완료.\n" +
                "\n" +
                "\n" +
                "▼ 배경화면 변경 방법\n" +
                "\n" +
                "1. 앱을 열고 '배경화면변경' 버튼을 클릭하세요.\n" +
                "2. '배경화면 불러오기' 버튼을 클릭하여 원하는 이미지를 선택해주세요.\n" +
                "3. '배경화면 적용하기'를 클릭하면 적용 완료.\n" +
                "\n" +
                "\n" +
                "▼ 배경화면 기본으로 되돌리기\n" +
                "\n" +
                "1. 앱을 열고 '배경화면변경' 버튼을 클릭하세요.\n" +
                "2. '기본화면 적용하기' 버튼을 클릭하면 적용완료.\n" +
                "\n" +
                "\n" +
                "▼ 패턴을 분실한 경우\n" +
                "\n" +
                "1. 패턴입력화면에서 '패턴을 잊어버리셨나요?' 버튼을 클릭하세요.\n" +
                "2. 새로운 창에서 '전송'을 클릭하세요.\n" +
                "2. 메일함에서 CaughtU의 메일을 읽고 나의 패턴을 확인하세요.\n" +
                "\n" +
                "\n" +
                "▼ 위치정보 받기를 실패하는 경우\n" +
                "\n" +
                "CaughtU 앱은 위치를 찾기 위해 안드로이드 폰의 최적의 하드웨어 장치를 탐색합니다.\n" +
                "마지막으로 알려진 위치를 전송 받고 변경이 감지될 때마다 새로운 위치를 갱신합니다.\n" +
                "최적의 하드웨어를 찾지 못하고 최근 알려진 위치도 없는 경우, 위치정보 수신을 실패합니다.\n" +
                "\n" +
                "- 핸드폰 설정에서 GPS(위치정보)를 켜주세요.\n" +
                "- 건물 안에서 앱을 시작한 경우 창문이나 야외에서 앱을 실행해주세요.";
    }

    public void Init(){
        ibtnBack = (ImageButton) findViewById(R.id.ibtnUseBack);
        ibtnMenu = (ImageButton) findViewById(R.id.ibtnUseMenu);
        txtvUse = (TextView) findViewById(R.id.txtvUse);

        ibtnBack.setOnClickListener(UseActivity.this);
        ibtnMenu.setOnClickListener(UseActivity.this);

        txtvUse.setText(strUse);
    }

    @Override
    public void onClick(View view) {
        if(ibtnBack.getId() == view.getId() || ibtnMenu.getId() == view.getId()){
            finish();
        }
    }
}
