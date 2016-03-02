package com.example.bang.caughtu.services;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by BANG on 2015-07-25.
 */
public class LockService extends Service {

    private ScreenReceiver receiver = null;
    private BootReceiver bootReceiver = null;

    //기존의 잠금화면 앱을 끄기 위해 필요
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("LockService", "onCreate");

        keyguardManager = (KeyguardManager) this.getSystemService(Activity.KEYGUARD_SERVICE);
        if(keyguardManager != null){
            keyguardLock = keyguardManager.newKeyguardLock("test");
            keyguardLock.disableKeyguard();
        }

        receiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, intentFilter);

        bootReceiver = new BootReceiver();
        IntentFilter[] intentFilters = new IntentFilter[2];
        intentFilters[0] = new IntentFilter(Intent.ACTION_SHUTDOWN);
        intentFilters[1] = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);

        registerReceiver(bootReceiver, intentFilters[0]);
        registerReceiver(bootReceiver, intentFilters[1]);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.w("LockService", "onStartCommand");

        if(intent != null){
            if(intent.getAction() == null){
                if(receiver == null){
                    receiver = new ScreenReceiver();
                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(receiver, intentFilter);
                }
            }
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(keyguardLock != null){
            keyguardLock.reenableKeyguard();
        }

        super.onDestroy();

        //Log.w("LockService", "onDestroy");


        if(receiver != null){
            unregisterReceiver(receiver);
        }
    }
}
