package com.gcit.smssend.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.gcit.smssend.receiver.SmsObserver;
import com.gcit.smssend.utils.Logs;

public class SmsService extends Service {
    public SmsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e(intent);
        Logs.e("flags", flags);
        Logs.e("startId", startId);
        register();
        return START_NOT_STICKY;
    }

    /**
     * 注册广播接收者，内容观察者
     */
    private void register() {
        new SmsObserver(new Handler(Looper.myLooper())).registerObserver();
    }
}
