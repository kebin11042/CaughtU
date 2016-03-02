package com.example.bang.caughtu.services;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.bang.caughtu.activityset.PatternlockscreenActivity;

/**
 * Created by BANG on 2015-07-25.
 */
public class ScreenReceiver extends BroadcastReceiver {

    //기존의 잠금화면 앱을 끄기 위해 필요
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;
    //전화가 왔을때, 이게 없으면 전화가 와도 잠금화면이 켜지기 때문에 받을 수 없다.
    private TelephonyManager telephonyManager = null;
    private boolean isPhoneIdle = true;     //락 스크린 띄울지 안띄울지

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static Intent intentLock;

    //전화 오는지 판별하는 리스너
    private PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if(state == TelephonyManager.CALL_STATE_IDLE){
                isPhoneIdle = true;
            }
            else if(state == TelephonyManager.CALL_STATE_RINGING){
                isPhoneIdle = false;
            }
            else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
                isPhoneIdle = false;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.w("ScreenReceiver", "onReceive");
        //화면이 꺼졌을 때 발생


        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            if(keyguardManager == null){
                keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            }

            if(keyguardLock == null){
                keyguardLock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
            }

            //전화오는 텔레포니매니져 할당과 리스너 등록
            if(telephonyManager == null){
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }

            if(isPhoneIdle){
                //기본 화면 잠금앱 없애기
                disableKeyguard();

                if(intentLock == null){
                    intentLock = new Intent(context, PatternlockscreenActivity.class);
                    //이거 안하면 에러 남 startActivity를 위해 필요함
                    intentLock.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                //잠금화면을 풀고 다른 어플 실행시 다시 잠금화면이 뜨지 않게 만들기
                preferences = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putString("LOCK", "OK");
                editor.apply();

                context.startActivity(intentLock);
            }
        }
    }

    //기본 화면 잠금앱 없애는 메소드
    public void disableKeyguard(){
        keyguardLock.disableKeyguard();
    }
}
