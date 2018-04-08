package com.hbln.smsintercept.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseActivity;
import com.hbln.smsintercept.db.bean.MobileBean;
import com.hbln.smsintercept.network.HttpError;
import com.hbln.smsintercept.ui.adapter.RUAdapter;
import com.hbln.smsintercept.ui.adapter.RUViewHolder;
import com.hbln.smsintercept.utils.TitleUtil;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

public class SettingMobileActivity extends BaseActivity implements View.OnClickListener {
    /** 已设置号码 */
    private RecyclerView mRvSettingMobile;
    /** 请输入新增号码 */
    private EditText mEtSettingMobile;
    /** 新增号码 */
    private AppCompatButton mBtnSettingMobile;

    /** 数据 */
    private List<MobileBean> mList;
    /** 适配器 */
    private RUAdapter<MobileBean> mAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingMobileActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_mobile);
        initView();
        initData();
    }

    private void initView() {
        TitleUtil.attach(getBaseActivity()).setTitle("号码设置").setBack(true);
        mRvSettingMobile = (RecyclerView) findViewById(R.id.rv_setting_mobile);
        mEtSettingMobile = (EditText) findViewById(R.id.et_setting_mobile);
        mBtnSettingMobile = (AppCompatButton) findViewById(R.id.btn_setting_mobile);
        mBtnSettingMobile.setOnClickListener(this);
    }

    private void initData() {
        loadData();

        mList = new ArrayList<>();
        mAdapter = new RUAdapter<MobileBean>(getContext(), mList, R.layout.item_setting_mobile) {
            @Override
            protected void onInflateData(RUViewHolder holder, MobileBean data, int position) {
                holder.setText(R.id.item_setting_mobile_position, "号段" + (position + 1) + "：");
                holder.setText(R.id.item_setting_mobile, data.getMobile());
            }
        };
        mRvSettingMobile.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvSettingMobile.setHasFixedSize(true);
        mRvSettingMobile.setAdapter(mAdapter);
    }

    /**
     * 导入数据
     */
    private void loadData() {
        Observable.just(MobileBean.loadMobile())
                .compose(getBaseActivity().<List<MobileBean>>applySchedulers(ActivityEvent.DESTROY))
                .subscribe(new Action1<List<MobileBean>>() {
                    @Override
                    public void call(List<MobileBean> mMobileBeans) {
                        resetData(mMobileBeans);
                    }
                }, new HttpError());
    }

    /**
     * 重新设置数据
     * @param list 数据
     */
    private void resetData(List<MobileBean> list) {
        mList = list;
        if (EmptyUtils.isNotEmpty(list)) {
            mAdapter.setData(list);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting_mobile:
                String mobile = mEtSettingMobile.getText().toString().trim();
                if (EmptyUtils.isNotEmpty(mobile)) {
                    MobileBean.insertMobile(new MobileBean(mobile));
                    ToastUtils.showShort("添加成功");
                    mEtSettingMobile.setText("");
                    loadData();
                } else {
                    ToastUtils.showShort("请输入新增号码");
                }
                break;
            default:
                break;
        }
    }
}
