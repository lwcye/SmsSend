package com.hbln.smsintercept.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseActivity;
import com.hbln.smsintercept.db.DbWrapper;
import com.hbln.smsintercept.db.bean.MobileBean;
import com.hbln.smsintercept.db.bean.SuccessSmsBean;
import com.hbln.smsintercept.db.dao.SuccessSmsBeanDao;
import com.hbln.smsintercept.ui.adapter.RUAdapter;
import com.hbln.smsintercept.ui.adapter.RUViewHolder;
import com.hbln.smsintercept.utils.TitleUtil;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

public class RecordDetailActivity extends BaseActivity {
    private static String INTENT_BEAN = "bean";
    private RecyclerView mRvDbDetail;
    private MobileBean mMobileBean;
    private RUAdapter<SuccessSmsBean> mAdapter;
    private List<SuccessSmsBean> mList = new ArrayList<>();

    public static void start(Context context, MobileBean mMobileBean) {
        Intent starter = new Intent(context, RecordDetailActivity.class);
        starter.putExtra(INTENT_BEAN, mMobileBean);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        TitleUtil.attach(this)
                .setTitle("记录详情")
                .setBack(true);

        initView();
        initData();
        loadSmsData();
    }

    private void initData() {
        mList = new ArrayList<>();
        mMobileBean = getIntent().getParcelableExtra(INTENT_BEAN);
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
        Observable.just(DbWrapper.getSession().getSuccessSmsBeanDao().queryBuilder()
                .where(SuccessSmsBeanDao.Properties.Mobile.like("%" + mMobileBean.getMobile() + "%"))
                .list())
                .compose(getBaseActivity().<List<SuccessSmsBean>>applySchedulers(ActivityEvent.DESTROY))
                .subscribe(new Action1<List<SuccessSmsBean>>() {
                    @Override
                    public void call(List<SuccessSmsBean> mSuccessSmsBeans) {
                        responseData(mSuccessSmsBeans);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable mThrowable) {
                        LogUtils.e(mThrowable);
                    }
                });
    }

    /**
     *
     * @param list
     */
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
        mRvDbDetail = (RecyclerView) findViewById(R.id.rv_record_detail);
    }
}
