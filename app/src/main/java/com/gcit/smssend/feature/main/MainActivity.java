package com.gcit.smssend.feature.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gcit.smssend.R;
import com.gcit.smssend.constant.ENVs;
import com.gcit.smssend.db.bean.SmsBean;
import com.gcit.smssend.event.ServiceEvent;
import com.gcit.smssend.event.SmsEvent;
import com.gcit.smssend.mvp.MVPBaseActivity;
import com.gcit.smssend.ui.activity.DbActivity;
import com.gcit.smssend.ui.activity.ErrorActivity;
import com.gcit.smssend.ui.activity.MobileActivity;
import com.gcit.smssend.ui.adapter.RUAdapter;
import com.gcit.smssend.ui.adapter.RUViewHolder;
import com.gcit.smssend.ui.widget.SimpleCalendarDialogFragment;
import com.xdandroid.hellodaemon.IntentWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
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
public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View, View.OnClickListener, RUAdapter.OnItemClickListener {
    /** 开启短信转发 */
    private Button mBtnStatus;
    private RecyclerView mRvMain;
    private RUAdapter<SmsBean> mAdapter;
    /** 当前状态：开启 */
    private TextView mTvStatus;
    /** 是否需要退出 */
    private boolean mIsExit = false;
    /** 批量上传 */
    private Button mBtnPostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        mPresenter.keepService();

        //修改系统默认短信应用
        String defaultSmsApp = null;
        String currentPn = getPackageName();//获取当前程序包名
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
        }
        LogUtils.e(defaultSmsApp);
        if (!defaultSmsApp.equals(currentPn)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currentPn);
            startActivity(intent);
        }
    }


    private void initView() {
        mBtnStatus = (Button) findViewById(R.id.btn_status);
        mBtnStatus.setOnClickListener(this);
        mRvMain = (RecyclerView) findViewById(R.id.rv_main);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mBtnPostList = (Button) findViewById(R.id.btn_post_list);
        mBtnPostList.setOnClickListener(this);
    }

    private void initData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mAdapter = new RUAdapter<SmsBean>(getContext(), mPresenter.mSmsList, R.layout.item_main) {
            @Override
            protected void onInflateData(RUViewHolder holder, SmsBean data, int position) {
                holder.setText(R.id.tv_item_main_moblie, data.getMobile());
                holder.setText(R.id.tv_item_main_content, "\t\t\t" + data.getContent());
                holder.setText(R.id.tv_item_main_date, TimeUtils.millis2String(data.getCreate_time()));
                TextView tvState = holder.getViewById(R.id.tv_item_main_state);
                if (data.getIsSuccess()) {
                    tvState.setText("已完成");
                    tvState.setTextColor(Color.GREEN);
                } else {
                    tvState.setText(data.getErrorMsg());
                    tvState.setTextColor(Color.RED);
                    mPresenter.requestPostSms(position);
                }
            }
        };
        mAdapter.setOnItemClickListener(this);
        mRvMain.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvMain.setItemAnimator(new DefaultItemAnimator());
        mRvMain.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOrderEvent(SmsEvent smsEvent) {
        if (!mPresenter.mSmsList.contains(smsEvent.mSmsBean)) {
            if (mAdapter.getItemCount() > 100) {
                mPresenter.mSmsList.clear();
                mAdapter.setData(mPresenter.mSmsList);
            }
            mAdapter.addDataFirst(smsEvent.mSmsBean);
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
            mTvStatus.setText("已开启");
        } else {
            mTvStatus.setText("已关闭");
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
            IntentWrapper.onBackPressed(this);
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
                IntentWrapper.whiteListMatters(this, "轨迹跟踪服务的持续运行");
                mPresenter.mSmsList.clear();
                mAdapter.setData(mPresenter.mSmsList);
                SimpleCalendarDialogFragment simpleCalendarDialogFragment = new SimpleCalendarDialogFragment();
                simpleCalendarDialogFragment.setListener(new SimpleCalendarDialogFragment.OnSureListener() {
                    @Override
                    public void sureListener(long date) {
                        mPresenter.loadSmsData(date);
                        showLoading("导入数据中");
                    }
                });
                simpleCalendarDialogFragment.show(getSupportFragmentManager(), "simple-calendar");
                break;
            case R.id.btn_post_list:
                showLoading("上传数据中");
                mPresenter.requestPostSmsList();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View view, int itemType, final int position) {
        final SmsBean smsBean = mPresenter.mSmsList.get(position);
        if (smsBean.getIsSuccess()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("重新上传");
        builder.setMessage(smsBean.getMobile() + "：" + smsBean.getContent());
        builder.setPositiveButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.requestPostSms(position);
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

    @Override
    public void responseSmsData(List<SmsBean> smsBeen) {
        hideLoading();
        mTvStatus.setText("已显示" + smsBeen.size() + "条");
        mAdapter.setData(smsBeen);
    }

    @Override
    public void responseSmsPost() {
        mAdapter.notifyDataSetChanged();
        mTvStatus.setText("已显示" + mAdapter.getItemCount() + "条");
    }

    @Override
    public void responseSmsListPost() {
        hideLoading();
        ToastUtils.showShort("完成上传");
        mAdapter.notifyDataSetChanged();
    }
}
