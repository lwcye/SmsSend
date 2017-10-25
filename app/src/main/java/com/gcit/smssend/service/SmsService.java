package com.gcit.smssend.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.gcit.smssend.base.BaseApp;
import com.gcit.smssend.event.ServiceEvent;
import com.gcit.smssend.utils.keepLive.KeepLiveManager;

import org.greenrobot.eventbus.EventBus;

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
        BaseApp.getInstance().isKeepLive = true;
        EventBus.getDefault().post(new ServiceEvent(BaseApp.getInstance().isKeepLive));
        register();
        mSmsService = this;
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseApp.getInstance().isKeepLive = false;
        EventBus.getDefault().post(new ServiceEvent(BaseApp.getInstance().isKeepLive));
    }

    /**
     * 注册广播接收者，内容观察者
     */
    private void register() {
//        new SmsObserver(new Handler(Looper.myLooper())).registerObserver();
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
