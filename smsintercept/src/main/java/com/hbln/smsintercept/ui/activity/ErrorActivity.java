package com.hbln.smsintercept.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseActivity;
import com.hbln.smsintercept.db.DbWrapper;
import com.hbln.smsintercept.db.bean.SmsBean;
import com.hbln.smsintercept.network.ApiResult;
import com.hbln.smsintercept.network.HttpUtils;
import com.hbln.smsintercept.ui.adapter.RUAdapter;
import com.hbln.smsintercept.ui.adapter.RUViewHolder;
import com.hbln.smsintercept.utils.TitleUtil;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Administrator
 */
public class ErrorActivity extends BaseActivity implements RUAdapter.OnItemClickListener, View.OnClickListener {
    RUAdapter<SmsBean> mAdapter;
    List<SmsBean> mList;
    AlertDialog mDialog;
    /** 当前发送失败短信：19条 */
    private TextView mTvError;
    private RecyclerView mRvError;
    /** 重新上传 */
    private AppCompatButton mBtnError;

    public static void start(Context context) {
        Intent starter = new Intent(context, ErrorActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        TitleUtil.attach(this)
                .setTitle("错误短信")
                .setBack(true);
        initView();
        initData();
    }

    private void initData() {
        mList = new ArrayList<>();
        mAdapter = new RUAdapter<SmsBean>(getContext(), mList, R.layout.item_main) {
            @Override
            protected void onInflateData(RUViewHolder holder, final SmsBean data, final int position) {
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
                }
            }
        };
        mAdapter.setOnItemClickListener(this);
        mRvError.setItemAnimator(new DefaultItemAnimator());
        mRvError.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvError.setAdapter(mAdapter);
        loadErrorSms();
    }

    private void requestSms(final SmsBean data) {
        SmsBean.requestPostSms(data, new Action1<SmsBean>() {
            @Override
            public void call(SmsBean mSmsBean) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {
        mTvError = (TextView) findViewById(R.id.tv_error);
        mRvError = (RecyclerView) findViewById(R.id.rv_error);
        mBtnError = (AppCompatButton) findViewById(R.id.btn_error);
        mBtnError.setOnClickListener(this);
    }

    public void responseErrorSms(List<SmsBean> mSmsBeans) {
        mList = mSmsBeans;
        mTvError.setText("当前上传失败短信：" + mList.size() + "条");
        mAdapter.setData(mList);
    }

    public void loadErrorSms() {
        Observable.just(DbWrapper.getSession().getSmsBeanDao().loadAll())
                .compose(getBaseActivity().<List<SmsBean>>applySchedulers(ActivityEvent.DESTROY))
                .subscribe(new Action1<List<SmsBean>>() {
                    @Override
                    public void call(List<SmsBean> mSmsBeans) {
                        responseErrorSms(mSmsBeans);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable mThrowable) {
                        LogUtils.e(mThrowable);
                    }
                });
    }

    @Override
    public void onItemClick(View view, int itemType, final int position) {
        SmsBean smsBean = mList.get(position);
        if (smsBean.getIsSuccess()) {
            DbWrapper.getSession().getSmsBeanDao().delete(smsBean);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("重新上传");
        builder.setMessage(smsBean.getMobile() + "：" + smsBean.getContent());
        builder.setPositiveButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestSms(mList.get(position));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_error:
                for (SmsBean smsBean : mList) {
                    requestSms(smsBean);
                }
                break;
            default:
                break;
        }
    }
}
