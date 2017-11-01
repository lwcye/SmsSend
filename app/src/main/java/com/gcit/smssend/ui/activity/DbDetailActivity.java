package com.gcit.smssend.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.TimeUtils;
import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.db.bean.SuccessSmsBean;
import com.gcit.smssend.ui.adapter.RUAdapter;
import com.gcit.smssend.ui.adapter.RUViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DbDetailActivity extends BaseActivity {
    
    private RecyclerView mRvDbDetail;
    private RUAdapter<SuccessSmsBean> mAdapter;
    private List<SuccessSmsBean> mList = new ArrayList<>();
    
    public static void start(Context context) {
        Intent starter = new Intent(context, DbDetailActivity.class);
        context.startActivity(starter);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_detail);
        initView();
        initData();
        loadSmsData();
        setupActionBar();
    }
    
    private void initData() {
        
        mAdapter = new RUAdapter<SuccessSmsBean>(getContext(), mList, R.layout.item_main) {
            @Override
            protected void onInflateData(RUViewHolder holder, SuccessSmsBean data, int position) {
                holder.setText(R.id.tv_item_main_moblie, data.getMobile());
                holder.setText(R.id.tv_item_main_content, "\t\t\t" + data.getContent());
                holder.setText(R.id.tv_item_main_date, TimeUtils.millis2String(data.getCreate_time()));
            }
        };
        
        mRvDbDetail.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvDbDetail.setItemAnimator(new DefaultItemAnimator());
        mRvDbDetail.setAdapter(mAdapter);
    }
    
    private void loadSmsData() {
        responseData(DbWrapper.getSession().getSuccessSmsBeanDao().loadAll());
    }
    
    public void responseData(List<SuccessSmsBean> list) {
        mList = list;
        Collections.sort(mList, new Comparator<SuccessSmsBean>() {
            
            @Override
            public int compare(SuccessSmsBean o1, SuccessSmsBean o2) {
                long i = o2.getCreate_time() - o1.getCreate_time();
                return (int) i;
            }
        });
        mAdapter.setData(mList);
    }
    
    private void initView() {
        mRvDbDetail = (RecyclerView) findViewById(R.id.rv_db_detail);
    }
}
