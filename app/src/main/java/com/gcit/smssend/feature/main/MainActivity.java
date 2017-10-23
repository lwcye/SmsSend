package com.gcit.smssend.feature.main;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.receiver.KeepLiveReceiver;
import com.gcit.smssend.receiver.SystemReceiver;
import com.gcit.smssend.service.SmsService;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    KeepLiveReceiver mKeepLiveReceiver = new KeepLiveReceiver();
    SystemReceiver mSystemReceiver = new SystemReceiver();
    /** Hello World! */
    private TextView mTvSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxPermissions permissions = new RxPermissions(getBaseActivity());
        permissions.request(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        startServiceEx(new Intent(getBaseActivity(), SmsService.class));
                    }
                });
        //注册监听锁屏广播
        if (mKeepLiveReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(mKeepLiveReceiver, filter);
        }
        //注册监听锁屏广播
        if (mSystemReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(mSystemReceiver, filter);
        }
    }

    private void initView() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mKeepLiveReceiver != null) {
            unregisterReceiver(mKeepLiveReceiver);
        }
        if (mSystemReceiver != null) {
            unregisterReceiver(mSystemReceiver);
        }
    }
}
