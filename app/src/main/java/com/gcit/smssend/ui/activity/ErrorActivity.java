package com.gcit.smssend.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.db.bean.SmsBean;
import com.gcit.smssend.network.ApiResult;
import com.gcit.smssend.network.HttpUtils;
import com.gcit.smssend.ui.adapter.RUAdapter;
import com.gcit.smssend.ui.adapter.RUViewHolder;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ErrorActivity extends BaseActivity implements RUAdapter.OnItemClickListener {
    RUAdapter<SmsBean> mAdapter;
    List<SmsBean> mList;
    AlertDialog mDialog;
    /** 当前发送失败短信：19条 */
    private TextView mTvError;
    private RecyclerView mRvError;

    public static void start(Context context) {
        Intent starter = new Intent(context, ErrorActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        initView();
        initData();
        setupActionBar();
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
        HttpUtils.getSmsInfoService().smspost(String.valueOf(data.getCreate_time() / 1000), data.getMobile(), data.getContent())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ApiResult>() {
                    @Override
                    public void call(ApiResult apiResult) {
                        data.setIsSuccess(true);
                        DbWrapper.getSession().getSmsBeanDao().delete(data);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ApiResult>() {
                    @Override
                    public void call(ApiResult apiResult) {
                        loadErrorSms();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        data.setIsSuccess(false);
                        data.setErrorMsg(getString(R.string.error_network));
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void initView() {
        mTvError = (TextView) findViewById(R.id.tv_error);
        mRvError = (RecyclerView) findViewById(R.id.rv_error);
    }

    public void responseErrorSms(List<SmsBean> smsBeen) {
        mList = smsBeen;
        mTvError.setText("当前发送失败短信：" + mList.size() + "条");
        mAdapter.setData(mList);
    }

    public void loadErrorSms() {
        List<SmsBean> smsBeen = DbWrapper.getSession().getSmsBeanDao().loadAll();
        responseErrorSms(smsBeen);
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
}
