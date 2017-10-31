package com.gcit.smssend.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.base.BaseApp;
import com.gcit.smssend.constant.ENVs;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.db.bean.SmsBean;
import com.gcit.smssend.db.bean.SuccessSmsBean;
import com.gcit.smssend.event.ServiceEvent;
import com.gcit.smssend.event.SmsEvent;
import com.gcit.smssend.network.ApiResult;
import com.gcit.smssend.network.HttpUtils;
import com.gcit.smssend.receiver.KeepLiveReceiver;
import com.gcit.smssend.receiver.SmsObserver;
import com.gcit.smssend.receiver.SystemReceiver;
import com.gcit.smssend.service.SmsService;
import com.gcit.smssend.ui.adapter.RUAdapter;
import com.gcit.smssend.ui.adapter.RUViewHolder;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * <p>首页</p><br>
 *
 * @author - lwc
 * @date - 2017/10/25 15:33
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, RUAdapter.OnItemClickListener {
    /** 监听锁频的广播 */
    KeepLiveReceiver mKeepLiveReceiver = new KeepLiveReceiver();
    /** 监听系统的广播 */
    SystemReceiver mSystemReceiver = new SystemReceiver();
    /** 开启短信转发 */
    private Button mBtnStatus;
    private RecyclerView mRvMain;
    private RUAdapter<SmsBean> mAdapter;
    private List<SmsBean> mList;
    /** 当前状态：开启 */
    private TextView mTvStatus;
    /** 是否需要退出 */
    private boolean mIsExit = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        
        keepService();
        register();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        if (mAdapter != null && mList != null) {
            mList.clear();
            mAdapter.setData(mList);
        }
    }
    
    /**
     * 开启服务
     */
    private void keepService() {
        RxPermissions permissions = new RxPermissions(getBaseActivity());
        permissions.request(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    startServiceEx(new Intent(getBaseActivity(), SmsService.class));
                    new SmsObserver(new Handler(getMainLooper())).registerObserver();
                }
            });
    }
    
    /**
     * 注册
     */
    private void register() {
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
        mBtnStatus = (Button) findViewById(R.id.btn_status);
        mBtnStatus.setOnClickListener(this);
        mRvMain = (RecyclerView) findViewById(R.id.rv_main);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
    }
    
    private void initData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mList = new ArrayList<>();
        mAdapter = new RUAdapter<SmsBean>(getContext(), mList, R.layout.item_main) {
            @Override
            protected void onInflateData(RUViewHolder holder, SmsBean data, int position) {
                holder.setText(R.id.tv_item_main_moblie, data.getMobile());
                holder.setText(R.id.tv_item_main_content, "\t\t\t\t" + data.getContent());
                holder.setText(R.id.tv_item_main_date, TimeUtils.millis2String(data.getCreate_time()));
                TextView tvState = holder.getViewById(R.id.tv_item_main_state);
                if (data.getIsSuccess()) {
                    tvState.setText("已完成");
                    tvState.setTextColor(Color.GREEN);
                } else {
                    tvState.setText(data.getErrorMsg());
                    tvState.setTextColor(Color.RED);
                    requestSms(data, position);
                }
            }
        };
        mAdapter.setOnItemClickListener(this);
        mRvMain.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvMain.setItemAnimator(new DefaultItemAnimator());
        mRvMain.setAdapter(mAdapter);
    }
    
    private void requestSms(final SmsBean data, final int index) {
        HttpUtils.getSmsInfoService().smspost(String.valueOf(data.getCreate_time() / 1000), data.getMobile(), data.getContent())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    DbWrapper.getSession().getSmsBeanDao().insert(data);
                }
            })
            .observeOn(Schedulers.io())
            .doOnNext(new Action1<ApiResult>() {
                @Override
                public void call(ApiResult apiResult) {
                    data.setIsSuccess(true);
                    DbWrapper.getSession().getSmsBeanDao().delete(data);
                    DbWrapper.getSession().getSuccessSmsBeanDao().insertOrReplace(new SuccessSmsBean(data.getCreate_time(), data.getMobile(), data.getContent()));
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<ApiResult>() {
                @Override
                public void call(ApiResult apiResult) {
                    mAdapter.notifyItemChanged(index);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    data.setIsSuccess(false);
                    data.setErrorMsg(getString(R.string.error_network));
                    mAdapter.notifyItemChanged(index);
                }
            });
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
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOrderEvent(SmsEvent smsEvent) {
        if (!mList.contains(smsEvent.mSmsBean)) {
            if (mAdapter.getItemCount() > 100) {
                mList.clear();
                mAdapter.setData(mList);
            }
            mAdapter.addDataLast(smsEvent.mSmsBean);
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOrderEvent(ServiceEvent serviceEvent) {
        resetState(serviceEvent.isKeepLive);
    }
    
    /**
     * 重新设置状态
     *
     * @param isKeepLive 服务是否存活
     */
    private void resetState(boolean isKeepLive) {
        if (isKeepLive) {
            mTvStatus.setText("当前状态：开启");
            mBtnStatus.setText("退出");
        } else {
            mTvStatus.setText("当前状态：关闭");
            mBtnStatus.setText("开启");
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getString(R.string.activity_mobile));
        menu.add(getString(R.string.activity_error));
        menu.add(getString(R.string.activity_db));
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getString(R.string.activity_mobile).equals(item.getTitle())) {
            MobileActivity.start(getContext());
            return super.onOptionsItemSelected(item);
        }
        if (getString(R.string.activity_error).equals(item.getTitle())) {
            ErrorActivity.start(getContext());
            return super.onOptionsItemSelected(item);
        }
        if (getString(R.string.activity_db).equals(item.getTitle())) {
            DbActivity.start(getContext());
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    public void onBackPressed() {
        if (mIsExit) {
            finish();
        } else {
            mIsExit = true;
            ToastUtils.showShort(getString(R.string.exit_if_again));
            Observable.timer(ENVs.BACK_TO_EXIT_INTERVAL, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mIsExit = false;
                    }
                });
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_status:
                if (BaseApp.getInstance().isKeepLive) {
                    onBackPressed();
                } else {
                    keepService();
                }
                break;
            default:
                break;
        }
    }
    
    @Override
    public void onItemClick(View view, int itemType, final int position) {
        final SmsBean smsBean = mList.get(position);
        if (smsBean.getIsSuccess()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("重新上传");
        builder.setMessage(smsBean.getMobile() + "：" + smsBean.getContent());
        builder.setPositiveButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestSms(smsBean, position);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
