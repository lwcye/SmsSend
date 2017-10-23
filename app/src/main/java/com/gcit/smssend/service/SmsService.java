package com.gcit.smssend.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.gcit.smssend.receiver.SmsObserver;
import com.gcit.smssend.utils.Logs;
import com.gcit.smssend.utils.keepLive.KeepLiveManager;

public class SmsService extends Service {
    private static final String ACCOUNT_TYPE = "android.accounts.AccountAuthenticator";
    public static SmsService mSmsService;

    public SmsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e(intent);
        Logs.e("flags", flags, "startId", startId);
        register();
        mSmsService = this;
        return Service.START_STICKY;
    }

    /**
     * 注册广播接收者，内容观察者
     */
    private void register() {
        new SmsObserver(new Handler(Looper.myLooper())).registerObserver();
        startService(new Intent(this, InnerService.class));
    }


    public static class InnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            KeepLiveManager.getInstance().setForeGround(mSmsService, this);
            return super.onStartCommand(intent, flags, startId);
        }
    }
}
