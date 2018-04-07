package com.hbln.smsintercept.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blankj.utilcode.util.FragmentUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseActivity;
import com.hbln.smsintercept.ui.fragment.HomeFragment;
import com.hbln.smsintercept.ui.fragment.SettingFragment;
import com.hbln.smsintercept.ui.fragment.SmsFragment;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.xdandroid.hellodaemon.IntentWrapper;

import rx.functions.Action1;

public class HomeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    /** 界面 */
    private FrameLayout mFragContainer;
    /** 短信 */
    private RadioButton mRbHome0;
    /** 首页 */
    private RadioButton mRbHome1;
    /** 设置 */
    private RadioButton mRbHome2;
    /** 组管理 */
    private RadioGroup mRgHomeTab;

    /** 首页 */
    private HomeFragment homeFragment;
    /** 短信 */
    private SmsFragment smsFragment;
    /** 设置 */
    private SettingFragment settingFragment;
    /** 索引 */
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();

    }

    private void initData() {
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                    }
                });
        homeFragment = new HomeFragment();
        smsFragment = new SmsFragment();
        settingFragment = new SettingFragment();
        FragmentUtils.add(getSupportFragmentManager(), homeFragment, R.id.frag_container);
        FragmentUtils.add(getSupportFragmentManager(), smsFragment, R.id.frag_container);
        FragmentUtils.add(getSupportFragmentManager(), settingFragment, R.id.frag_container);

        mRbHome1.setChecked(true);
    }

    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    public void onBackPressed() {
        IntentWrapper.onBackPressed(this);
    }

    private void initView() {
        mFragContainer = (FrameLayout) findViewById(R.id.frag_container);
        mRbHome0 = (RadioButton) findViewById(R.id.rb_home_0);
        mRbHome1 = (RadioButton) findViewById(R.id.rb_home_1);
        mRbHome2 = (RadioButton) findViewById(R.id.rb_home_2);
        mRgHomeTab = (RadioGroup) findViewById(R.id.rg_home_tab);
        mRgHomeTab.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_home_0:
                showFragment(0);
                break;
            case R.id.rb_home_1:
                showFragment(1);
                break;
            case R.id.rb_home_2:
                showFragment(2);
                break;
            default:
                break;
        }
    }

    /**
     * 显示界面
     *
     * @param i 序列
     */
    public void showFragment(int i) {
        index = i;
        if (index == 0) {
            FragmentUtils.hide(homeFragment);
            FragmentUtils.show(smsFragment);
            FragmentUtils.hide(settingFragment);
        } else if (index == 1) {
            FragmentUtils.show(homeFragment);
            FragmentUtils.hide(smsFragment);
            FragmentUtils.hide(settingFragment);
        } else {
            FragmentUtils.hide(homeFragment);
            FragmentUtils.hide(smsFragment);
            FragmentUtils.show(settingFragment);
        }
    }
}
