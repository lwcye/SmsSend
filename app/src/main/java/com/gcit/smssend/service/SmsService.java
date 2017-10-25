package com.gcit.smssend.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;

import com.gcit.smssend.base.BaseApp;
import com.gcit.smssend.constant.SharedPrefs;
import com.gcit.smssend.receiver.SmsReceiver;
import com.gcit.smssend.utils.keepLive.KeepLiveManager;

public class SmsService extends Service {
    private static final String ACCOUNT_TYPE = "android.accounts.AccountAuthenticator";
    public static SmsService mSmsService;
    public SmsReceiver mSmsReceiver = new SmsReceiver();
    
    public SmsService() {
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BaseApp.getSpUtils().put(SharedPrefs.isSmsServiceKeepLive, true);
        register();
        mSmsService = this;
        return Service.START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSmsReceiver != null) {
            unregisterReceiver(mSmsReceiver);
        }
        BaseApp.getSpUtils().put(SharedPrefs.isSmsServiceKeepLive, false);
    }
    
    /**
     * 注册广播接收者，内容观察者
     */
    private void register() {
//        new SmsObserver(new Handler(Looper.myLooper())).registerObserver();
        if (mSmsReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(mSmsReceiver, filter);
        }
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
