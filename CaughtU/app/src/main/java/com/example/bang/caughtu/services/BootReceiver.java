package com.example.bang.caughtu.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by BANG on 2015-07-28.
 */
public class BootReceiver extends BroadcastReceiver {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //부팅시 어플 자동실행 클래스
    @Override
    public void onReceive(Context context, Intent intent) {
        //핸드폰 부팅이 완료되면 자동으로 서비스를 실행 해줘야 한다.
        //Log.w("BOOT", "부트 리시버 실행!!! Action : " + intent.getAction());
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intentService = new Intent(context, ScreenReceiver.class);
            context.startService(intentService);
        }
        //이부분이 중요한 내용
        //만약 사용자가 패턴을 올바르게 해제하고 스마트폰을 재부팅 했을 경우
        //"LOCK"에 저장된 데이터는 EXIT 이다. 이러면 핸드폰을 다시 켰을 때 문제가 되는데
        //패턴을 풀지 않아도 홈으로 접근이 가능하다
        //이러한 이유로 핸드폰이 꺼질때 "LOCK"에 EXIT가 저장되어있을 가능성이 크기 때문에
        //꺼지기전 OK로 다시 저장하고 끄게함으로써 재부팅 문제를 해결한다.
        else if(intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
            preferences = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
            editor = preferences.edit();
            editor.putString("LOCK", "OK");
            editor.apply();
        }
    }
}
