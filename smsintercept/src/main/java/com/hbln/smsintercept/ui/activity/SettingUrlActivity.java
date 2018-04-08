package com.hbln.smsintercept.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseActivity;
import com.hbln.smsintercept.base.MyApplication;
import com.hbln.smsintercept.constant.SharedPrefs;
import com.hbln.smsintercept.network.HttpUtils;
import com.hbln.smsintercept.utils.TitleUtil;

public class SettingUrlActivity extends BaseActivity implements View.OnClickListener {
    /** 请输入链接网址 */
    private EditText mEtSettingUrl;
    /** 设置 */
    private AppCompatButton mBtnSettingUrl;
    private TextView mTvSettingUrl;

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingUrlActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_url);
        initView();
        initData();
    }


    private void initView() {
        TitleUtil.attach(getBaseActivity()).setTitle("设置链接").setBack(true);
        mEtSettingUrl = (EditText) findViewById(R.id.et_setting_url);
        mBtnSettingUrl = (AppCompatButton) findViewById(R.id.btn_setting_url);
        mTvSettingUrl = (TextView) findViewById(R.id.tv_setting_url);
        mBtnSettingUrl.setOnClickListener(this);
    }

    private void initData() {
        mTvSettingUrl.setText(String.format("当前设置的链接为：\n%s", HttpUtils.getUrl()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting_url:
                String url = mEtSettingUrl.getText().toString().trim();
                if (EmptyUtils.isNotEmpty(url)) {
                    if (!url.startsWith("http")) {
                        ToastUtils.showShort("链接请写明http或者https协议");
                        return;
                    }
                    if (!url.endsWith("/")) {
                        ToastUtils.showShort("链接请以 / 结尾");
                        return;
                    }
                    MyApplication.getSpUtils().put(SharedPrefs.URL, url);
                    HttpUtils.setUrl(url);
                    ToastUtils.showShort("设置成功");

                } else {
                    ToastUtils.showShort("请输入链接网址");
                }
                break;
            default:
                break;
        }
    }
}
