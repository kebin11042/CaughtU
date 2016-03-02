package com.example.bang.caughtu.activityset;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.bang.caughtu.R;

/**
 * Created by BANG on 2015-07-22.
 */
public class SplashActivity extends Activity {

    //로컬 DB
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //스플래쉬 이후 띄워줄 화면
    private Intent intent;

    //사용자 정보
    private String userEmail;
    private String userPattern;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Init();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1500);


    }

    public void Init(){

        preferences = getSharedPreferences("pref", MODE_PRIVATE);
        editor = preferences.edit();

        userEmail = preferences.getString("EMAIL", "NOT");
        userPattern = preferences.getString("PATTERN", "NOT");

        intent = null;

        //미리 저장된 데이터 판별 여부
        if( !userEmail.equals("NOT") && !userPattern.equals("NOT")){
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        else{
            //미리 저장된 데이터가 없을 경우 첫번째 설정화면으로 액티비티 이동
            intent = new Intent(SplashActivity.this, FirstActivity.class);
        }
    }

    //뒤로가기 버튼을 눌러도 종료되지 않게
    @Override
    public void onBackPressed() {

    }
}
