package com.hbln.smsintercept.ui.fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseFragment;
import com.hbln.smsintercept.db.DbWrapper;
import com.hbln.smsintercept.db.bean.MobileBean;
import com.hbln.smsintercept.db.bean.SmsBean;
import com.hbln.smsintercept.db.bean.SuccessSmsBean;
import com.hbln.smsintercept.db.dao.SuccessSmsBeanDao;
import com.hbln.smsintercept.network.HttpUtils;
import com.hbln.smsintercept.receiver.SmsObserver;
import com.hbln.smsintercept.ui.widget.SimpleCalendarDialogFragment;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.Collections;
import java.util.Comparator;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/7.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    /** 已连接 */
    private AppCompatTextView mTvHomeStatusService;
    /** 已连接 */
    private AppCompatTextView mTvHomeStatusNetwork;
    /** 最新短信更新时间 */
    private AppCompatTextView mTvHomeSmsNew;
    /** 最新短信更新时间 */
    private AppCompatTextView mTvHomeSmsSync;
    /** 同步上传 */
    private AppCompatButton mBtnHome;

    @Override
    public void initView(View view) {
        mTvHomeStatusService = (AppCompatTextView) view.findViewById(R.id.tv_home_status_service);
        mTvHomeStatusNetwork = (AppCompatTextView) view.findViewById(R.id.tv_home_status_network);
        mTvHomeSmsNew = (AppCompatTextView) view.findViewById(R.id.tv_home_sms_new);
        mTvHomeSmsSync = (AppCompatTextView) view.findViewById(R.id.tv_home_sms_sync);
        mBtnHome = (AppCompatButton) view.findViewById(R.id.btn_home);
        mBtnHome.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if (NetworkUtils.isConnected()) {
            mTvHomeStatusNetwork.setText(R.string.status_connect);
        } else {
            mTvHomeStatusNetwork.setTextColor(Color.RED);
            mTvHomeStatusNetwork.setText(R.string.status_connect_error);
        }
        HttpUtils.getSmsInfoService().smsinfo("")
                .compose(applySchedulers(FragmentEvent.DESTROY))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mTvHomeStatusService.setTextColor(Color.RED);
                        mTvHomeStatusService.setText(R.string.status_connect_error);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        mTvHomeStatusService.setText(R.string.status_connect);
                    }
                });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                SimpleCalendarDialogFragment simpleCalendarDialogFragment = new SimpleCalendarDialogFragment();
                simpleCalendarDialogFragment.setListener(new SimpleCalendarDialogFragment.OnSureListener() {
                    @Override
                    public void sureListener(long date) {
                        loadSmsData(date);
                        showLoading("导入数据中");
                    }
                });
                simpleCalendarDialogFragment.show(getBaseActivity().getSupportFragmentManager(), "simple-calendar");
                break;
            default:
                break;
        }
    }

    private void loadSmsData(final long selectorDate) {
        Observable.from(DbWrapper.getSession().getMobileBeanDao().loadAll())
                .flatMap(new Func1<MobileBean, Observable<String>>() {
                    @Override
                    public Observable<String> call(MobileBean mobileBean) {
                        return Observable.just(mobileBean.getMobile());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String mobile) {
                        long minDate;
                        if (selectorDate <= 0) {
                            long day = System.currentTimeMillis() % TimeConstants.DAY + 8 * TimeConstants.HOUR;
                            minDate = System.currentTimeMillis() - day;
                        } else {
                            minDate = selectorDate;
                        }
                        String where = " address like '%" + mobile + "%' AND protocol = '0' AND type = 1 AND date >  "
                                + minDate;
                        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(SmsObserver.SMS_INBOX_URI), SmsObserver.PROJECTION,
                                where, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
                        if (null == cursor) {
                            LogUtils.e(mobile + ":空数据");
                            return;
                        }
                        while (cursor.moveToNext()) {
                            String smsSender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                            String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                            long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                            boolean saved = DbWrapper.getSession().getSuccessSmsBeanDao().count() > 0 &&
                                    DbWrapper.getSession().getSuccessSmsBeanDao().queryBuilder()
                                            .where(SuccessSmsBeanDao.Properties.Create_time.eq(date)).count() > 0;
                            SmsBean.requestPostSms(new SmsBean(date, smsSender, smsBody, saved, saved ? "已完成" : "未上传"), null);
                        }
                        CloseUtils.closeIO(cursor);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String mobile) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        hideLoading();
                        throwable.printStackTrace();
                        ToastUtils.showShort("查询出错：" + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        hideLoading();
                    }
                });
    }
}
