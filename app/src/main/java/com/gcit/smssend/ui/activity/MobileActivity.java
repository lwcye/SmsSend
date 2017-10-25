package com.gcit.smssend.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.daimajia.swipe.SwipeLayout;
import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.db.bean.MobileBean;
import com.gcit.smssend.ui.adapter.RUAdapter;
import com.gcit.smssend.ui.adapter.RUViewHolder;
import com.gcit.smssend.ui.adapter.SimpleItemDecoration;

import java.util.List;

public class MobileActivity extends BaseActivity implements View.OnClickListener {
    RUAdapter<MobileBean> mAdapter;
    List<MobileBean> mList;
    private RecyclerView mRvMobile;
    /** 请输入号码 */
    private EditText mEtMobile;
    /** 新增 */
    private Button mBtnMobileAdd;

    public static void start(Context context) {
        Intent starter = new Intent(context, MobileActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
        setupActionBar();
        initView();
        initData();
    }

    private void initData() {
        mList = getMobileList();
        mAdapter = new RUAdapter<MobileBean>(getContext(), mList, R.layout.item_mobile) {
            @Override
            protected void onInflateData(RUViewHolder holder, MobileBean data, int position) {
                SwipeLayout swipeLayout = holder.getViewById(R.id.swipe_item_mobile);
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.getCurrentBottomView());

                holder.setText(R.id.tv_item_mobile, data.getMobile());
            }

            @Override
            protected void onViewCached(final RUViewHolder holder, View view, int resId) {
                super.onViewCached(holder, view, resId);
                holder.setOnClickListener(R.id.tv_item_mobile_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleDeleteMobile(holder.getHolderPosition());
                    }
                });
            }
        };
        mRvMobile.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvMobile.setHasFixedSize(true);
        mRvMobile.setItemAnimator(new DefaultItemAnimator());
        mRvMobile.addItemDecoration(new SimpleItemDecoration(getContext(), SimpleItemDecoration.VERTICAL_LIST));
        mRvMobile.setAdapter(mAdapter);
    }


    private void initView() {
        mRvMobile = (RecyclerView) findViewById(R.id.rv_mobile);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mBtnMobileAdd = (Button) findViewById(R.id.btn_mobile_add);
        mBtnMobileAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mobile_add:
                handleAddMobile(mEtMobile.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    private void handleAddMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort("设置的号码为空");
            return;
        }
        MobileBean mobileBean = new MobileBean(mobile);
        DbWrapper.getSession().getMobileBeanDao().insertOrReplace(mobileBean);

        mEtMobile.setText("");
        mAdapter.addDataLast(mobileBean);
    }

    private void handleDeleteMobile(int position) {
        DbWrapper.getSession().getMobileBeanDao().delete(mList.get(position));
        mAdapter.removeData(position);
    }

    public List<MobileBean> getMobileList() {
        return DbWrapper.getSession().getMobileBeanDao().loadAll();
    }
}
