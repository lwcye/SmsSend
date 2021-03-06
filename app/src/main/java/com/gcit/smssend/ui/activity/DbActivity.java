package com.gcit.smssend.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.gcit.smssend.R;
import com.gcit.smssend.base.BaseActivity;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.utils.Logs;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DbActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvDbCount;
    /** 清除数据库 */
    private Button mBtnDbClone;
    private LinearLayout mLlDbCount;
    
    public static void start(Context context) {
        Intent starter = new Intent(context, DbActivity.class);
        context.startActivity(starter);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        initView();
        initData();
        setupActionBar();
    }
    
    
    private void initView() {
        mTvDbCount = (TextView) findViewById(R.id.tv_db_count);
        mBtnDbClone = (Button) findViewById(R.id.btn_db_clone);
        mBtnDbClone.setOnClickListener(this);
        mLlDbCount = (LinearLayout) findViewById(R.id.ll_db_count);
        mLlDbCount.setOnClickListener(this);
    }
    
    private void initData() {
        long count = DbWrapper.getSession().getSuccessSmsBeanDao().count();
        mTvDbCount.setText(count + "条");
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_db_clone:
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
                            Logs.e("interval");
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
            case R.id.ll_db_count:
                DbDetailActivity.start(getContext());
                break;
        }
    }
}
