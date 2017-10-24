package com.gcit.smssend.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;

import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.db.bean.SmsBean;
import com.gcit.smssend.network.HttpUtils;
import com.gcit.smssend.receiver.KeepLiveReceiver;
import com.gcit.smssend.receiver.SystemReceiver;
import com.gcit.smssend.service.SmsService;
import com.gcit.smssend.ui.adapter.RUAdapter;
import com.gcit.smssend.ui.adapter.RUViewHolder;
import com.gcit.smssend.utils.Logs;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    /** 监听锁频的广播 */
    KeepLiveReceiver mKeepLiveReceiver = new KeepLiveReceiver();
    /** 监听系统的广播 */
    SystemReceiver mSystemReceiver = new SystemReceiver();
    /** 开启短信转发 */
    private Button mBtnStatus;
    private RecyclerView mRvMain;
    private RUAdapter<SmsBean> mAdapter;
    private List<SmsBean> mList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        
        
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
        HttpUtils.getSmsInfoService().smsinfo("1000")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    Logs.e(o);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Logs.e(throwable);
                }
            });
    }
    
    
    private void initView() {
        mBtnStatus = (Button) findViewById(R.id.btn_status);
        mBtnStatus.setOnClickListener(this);
        mRvMain = (RecyclerView) findViewById(R.id.rv_main);
    }
    
    private void initData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mList = new ArrayList<>();
        mAdapter = new RUAdapter<SmsBean>(getContext(), mList, R.layout.item_main) {
            @Override
            protected void onInflateData(RUViewHolder holder, SmsBean data, int position) {
                
            }
        };
        mRvMain.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvMain.setAdapter(mAdapter);
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOrderEvent(SmsBean smsBean) {
        if (mAdapter.getItemCount() > 20) {
            mAdapter.removeData(0);
        }
        mAdapter.addDataLast(smsBean);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu = menu.addSubMenu(1, Menu.FIRST + 9, 9, "sub menu");
        subMenu.add(1, Menu.FIRST + 10, 1, "sub1");
        subMenu.add(1, Menu.FIRST + 11, 2, "sub2");
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mKeepLiveReceiver != null) {
            unregisterReceiver(mKeepLiveReceiver);
        }
        if (mSystemReceiver != null) {
            unregisterReceiver(mSystemReceiver);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_status:
                break;
        }
    }
}
