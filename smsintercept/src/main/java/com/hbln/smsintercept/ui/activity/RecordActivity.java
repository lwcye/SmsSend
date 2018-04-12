package com.hbln.smsintercept.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseActivity;
import com.hbln.smsintercept.db.DbWrapper;
import com.hbln.smsintercept.db.bean.MobileBean;
import com.hbln.smsintercept.db.dao.SuccessSmsBeanDao;
import com.hbln.smsintercept.ui.adapter.RUAdapter;
import com.hbln.smsintercept.ui.adapter.RUViewHolder;
import com.hbln.smsintercept.utils.TitleUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RecordActivity extends BaseActivity implements View.OnClickListener, RUAdapter.OnItemClickListener {
    private RecyclerView mRvRecord;
    /** 清除数据库 */
    private Button mBtnDbClone;
    private RUAdapter<MobileBean> mAdapter;
    private List<MobileBean> mMobileBeans;

    public static void start(Context context) {
        Intent starter = new Intent(context, RecordActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        TitleUtil.attach(this)
                .setTitle("短信记录")
                .setBack(true);

        initView();
        initData();
    }


    private void initView() {
        mRvRecord = (RecyclerView) findViewById(R.id.rv_record);
        mBtnDbClone = (Button) findViewById(R.id.btn_record_del);
        mBtnDbClone.setOnClickListener(this);
    }

    private void initData() {
        mMobileBeans = DbWrapper.getSession().getMobileBeanDao().loadAll();
        mAdapter = new RUAdapter<MobileBean>(getContext(), mMobileBeans, R.layout.item_record) {
            @Override
            protected void onInflateData(RUViewHolder holder, MobileBean data, int position) {
                holder.setText(R.id.tv_item_record_title, data.getMobile());
                long count = DbWrapper.getSession().getSuccessSmsBeanDao().queryBuilder()
                        .where(SuccessSmsBeanDao.Properties.Mobile.like("%" + data.getMobile() + "%"))
                        .count();
                holder.setText(R.id.tv_item_record_count, count + "条");
            }
        };
        mRvRecord.setHasFixedSize(true);
        mRvRecord.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvRecord.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_del:
                //删除数据库
                Observable.just(null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                DbWrapper.getSession().getSuccessSmsBeanDao().deleteAll();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                            }
                        });

                //显示进度
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("正在清除数据库");
                dialog.show();

                //效验数据库信息
                Observable.interval(2, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                LogUtils.e("interval");
                                if (DbWrapper.getSession().getSuccessSmsBeanDao().count() == 0) {
                                    ToastUtils.showShort("已清除");
                                    throw new RuntimeException("已清除");
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                dialog.cancel();
                                finish();
                            }
                        });
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View view, int itemType, int position) {
        MobileBean mobileBean = mMobileBeans.get(position);
        RecordDetailActivity.start(getContext(), mobileBean);
    }
}
