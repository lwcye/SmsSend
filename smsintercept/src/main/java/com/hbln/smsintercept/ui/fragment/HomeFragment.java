package com.hbln.smsintercept.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.NetworkUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseFragment;

/**
 * Created by Administrator on 2018/4/7.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    /** 已连接 */
    private AppCompatTextView mTvHomeStatusService;
    /** 已连接 */
    private AppCompatTextView mTvHomeStatusNetwork;
    /** 最新短信更新时间 */
    private AppCompatTextView mTvHomeSmsNew;
    /** 最新短信更新时间 */
    private AppCompatTextView mTvHomeSmsSync;
    /** 同步上传 */
    private AppCompatButton mBtnHome;

    @Override
    public void initView(View view) {
        mTvHomeStatusService = (AppCompatTextView) view.findViewById(R.id.tv_home_status_service);
        mTvHomeStatusNetwork = (AppCompatTextView) view.findViewById(R.id.tv_home_status_network);
        mTvHomeSmsNew = (AppCompatTextView) view.findViewById(R.id.tv_home_sms_new);
        mTvHomeSmsSync = (AppCompatTextView) view.findViewById(R.id.tv_home_sms_sync);
        mBtnHome = (AppCompatButton) view.findViewById(R.id.btn_home);
        mBtnHome.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if (NetworkUtils.isConnected()) {
            mTvHomeStatusNetwork.setText(R.string.status_connect);
        } else {
            mTvHomeStatusNetwork.setTextColor(Color.RED);
            mTvHomeStatusNetwork.setText(R.string.status_connect_error);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_home:
                break;
        }
    }
}
