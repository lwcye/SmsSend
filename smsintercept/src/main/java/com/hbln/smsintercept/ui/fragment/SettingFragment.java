package com.hbln.smsintercept.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseFragment;
import com.hbln.smsintercept.model.BaseParam;
import com.hbln.smsintercept.ui.adapter.RUAdapter;
import com.hbln.smsintercept.ui.adapter.RUViewHolder;
import com.hbln.smsintercept.ui.adapter.SimpleItemDecoration;
import com.hbln.smsintercept.utils.TitleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/7.
 */

public class SettingFragment extends BaseFragment implements RUAdapter.OnItemClickListener {
    /** 设置 */
    private RecyclerView mRvSetting;
    private List<String> mList;
    private RUAdapter<String> mAdapter;

    @Override
    public void initView(View view) {
        TitleUtil.attach(view).setTitle("设置");
        mRvSetting = (RecyclerView) view.findViewById(R.id.rv_setting);
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();
        mList.add("号码设置");
        mList.add("错误短信");
        mList.add("短信记录");
        mList.add("设置链接");
        mList.add("版本信息");
        mList.add("版权信息");

        mAdapter = new RUAdapter<String>(getContext(), mList, R.layout.item_table_cell) {
            @Override
            protected void onInflateData(RUViewHolder holder, String data, int position) {
                holder.setText(R.id.tv_cell_left, data);
            }
        };

        mRvSetting.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvSetting.addItemDecoration(new SimpleItemDecoration(getContext(), SimpleItemDecoration.VERTICAL_LIST));
        mRvSetting.setHasFixedSize(true);
        mRvSetting.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onItemClick(View view, int itemType, int position) {

    }
}
