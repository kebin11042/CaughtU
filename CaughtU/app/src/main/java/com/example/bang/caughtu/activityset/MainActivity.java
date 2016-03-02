package com.example.bang.caughtu.activityset;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bang.caughtu.R;
import com.example.bang.caughtu.services.LockService;

public class MainActivity extends Activity implements View.OnClickListener{

    private LinearLayout layPattern, layMail, layBackground, layUse;
    private ImageView imgvPattern, imgvMail, imgvBackground, imgvUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //DB에서 기존에 있는 값이 있으면 메뉴화면으로, 없으면 초기 설정 화면으로 띄운다

        setContentView(R.layout.activity_main);

        /** 락패턴 스크린 서비스 실행 **/
        Intent intent = new Intent(MainActivity.this, LockService.class);
        startService(intent);

        //초기화
        Init();
    }

    public void Init(){
        layPattern = (LinearLayout) findViewById(R.id.layMainPattern);
        layMail = (LinearLayout) findViewById(R.id.layMainMail);
        layBackground = (LinearLayout) findViewById(R.id.layMainBackground);
        layUse = (LinearLayout) findViewById(R.id.layMainUse);

        imgvPattern = (ImageView) findViewById(R.id.imgvMainPattern);
        imgvMail = (ImageView) findViewById(R.id.imgvMainMail);
        imgvBackground = (ImageView) findViewById(R.id.imgvMainBackground);
        imgvUse = (ImageView) findViewById(R.id.imgvMainUse);

        layPattern.setOnClickListener(MainActivity.this);
        layMail.setOnClickListener(MainActivity.this);
        layBackground.setOnClickListener(MainActivity.this);
        layUse.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        if(view.getId() == layPattern.getId()){
            //이미지뷰 아이콘 애니메이션
            Drawable[] drawable = new Drawable[2];
            drawable[0] = getResources().getDrawable(R.drawable.icon_pattern_hover);
            drawable[1] = getResources().getDrawable(R.drawable.icon_pattern);

            TransitionDrawable transitionDrawable = new TransitionDrawable(drawable);

            imgvPattern.setImageDrawable(transitionDrawable);
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(182);
            ////////////////

            intent = new Intent(MainActivity.this, PatternsetActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_trans_right_to_left, R.anim.anim_scale_small);
        }
        else if(view.getId() == layMail.getId()){
            //이미지뷰 아이콘 애니메이션
            Drawable[] drawable = new Drawable[2];
            drawable[0] = getResources().getDrawable(R.drawable.icon_mail_hover);
            drawable[1] = getResources().getDrawable(R.drawable.icon_mail);

            TransitionDrawable transitionDrawable = new TransitionDrawable(drawable);

            imgvMail.setImageDrawable(transitionDrawable);
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(182);
            ////////////////

            intent = new Intent(MainActivity.this, MailsetActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_trans_right_to_left, R.anim.anim_scale_small);
        }
        else if(view.getId() == layBackground.getId()){
            //이미지뷰 아이콘 애니메이션
            Drawable[] drawable = new Drawable[2];
            drawable[0] = getResources().getDrawable(R.drawable.icon_back_hover);
            drawable[1] = getResources().getDrawable(R.drawable.icon_back);

            TransitionDrawable transitionDrawable = new TransitionDrawable(drawable);

            imgvBackground.setImageDrawable(transitionDrawable);
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(182);
            ////////////////

            intent = new Intent(MainActivity.this, BackgroundActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_trans_right_to_left, R.anim.anim_scale_small);
        }
        else if(view.getId() == layUse.getId()){
            //이미지뷰 아이콘 애니메이션
            Drawable[] drawable = new Drawable[2];
            drawable[0] = getResources().getDrawable(R.drawable.icon_use_hover);
            drawable[1] = getResources().getDrawable(R.drawable.icon_use);

            TransitionDrawable transitionDrawable = new TransitionDrawable(drawable);

            imgvUse.setImageDrawable(transitionDrawable);
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(182);
            ////////////////

            intent = new Intent(MainActivity.this, UseActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_trans_right_to_left, R.anim.anim_scale_small);
        }
    }
}
