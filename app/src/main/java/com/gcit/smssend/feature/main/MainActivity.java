package com.gcit.smssend.feature.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.service.SmsService;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class MainActivity extends BaseActivity {

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
    }

    private void initView() {
    }
}
