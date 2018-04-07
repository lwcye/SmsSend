package com.hbln.smsintercept.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseFragment;
import com.hbln.smsintercept.mvp.MVPBaseActivity;
import com.hbln.smsintercept.ui.activity.HomeActivity;
import com.hbln.smsintercept.utils.TitleUtil;

/**
 * Created by Administrator on 2018/4/7.
 */

public class SmsFragment extends BaseFragment {
    @Override
    public void initView(View view) {
        TitleUtil.attach(view).setTitle("短信")
                .setRightDrawable(0, 0, R.drawable.icon_setting, 0)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        ((HomeActivity) getBaseActivity()).showFragment(2);
                    }
                });
    }

    @Override
    public void initData() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sms;
    }
}
