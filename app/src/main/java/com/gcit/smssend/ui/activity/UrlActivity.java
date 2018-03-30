package com.gcit.smssend.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.network.HttpUtils;

public class UrlActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 请输入号码
     */
    private EditText mEtUrl;
    /**
     * 新增
     */
    private Button mBtnUrlAdd;

    public static void start(Context context) {
        Intent starter = new Intent(context, UrlActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        setupActionBar();
        initView();
    }

    private void initView() {
        mEtUrl = (EditText) findViewById(R.id.et_url);
        mBtnUrlAdd = (Button) findViewById(R.id.btn_url_add);
        mBtnUrlAdd.setOnClickListener(this);

        mEtUrl.setText(HttpUtils.getUrl());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_url_add:
                String url = mEtUrl.getEditableText().toString();
                if (TextUtils.isEmpty(url)) {
                    ToastUtils.showShort("您输入的链接为空");
                } else {
                    if (url.toLowerCase().contains("http")) {
                        HttpUtils.setUrl(url);
                        ToastUtils.showShort("设置成功");
                        onBackPressed();
                    } else {
                        ToastUtils.showShort("您输入的链接有误");
                    }
                }
                break;
            default:
                break;
        }
    }
}
