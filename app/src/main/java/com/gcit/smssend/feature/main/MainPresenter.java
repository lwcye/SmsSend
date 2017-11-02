package com.gcit.smssend.feature.main;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;

import com.blankj.utilcode.util.Utils;
import com.gcit.smssend.R;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.db.bean.MobileBean;
import com.gcit.smssend.db.bean.SmsBean;
import com.gcit.smssend.db.bean.SuccessSmsBean;
import com.gcit.smssend.mvp.BasePresenterImpl;
import com.gcit.smssend.network.ApiResult;
import com.gcit.smssend.network.HttpUtils;
import com.gcit.smssend.receiver.KeepLiveReceiver;
import com.gcit.smssend.receiver.SmsObserver;
import com.gcit.smssend.receiver.SystemReceiver;
import com.gcit.smssend.service.SmsService;
import com.gcit.smssend.utils.Logs;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.os.Looper.getMainLooper;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {
    public List<SmsBean> mSmsList = new ArrayList<>();
    /** 监听锁频的广播 */
    private KeepLiveReceiver mKeepLiveReceiver = new KeepLiveReceiver();
    /** 监听系统的广播 */
    private SystemReceiver mSystemReceiver = new SystemReceiver();

    @Override
    public void loadSmsData(final long selectorDate) {
        mSmsList = new ArrayList<>();
        Observable.from(DbWrapper.getSession().getMobileBeanDao().loadAll())
                .flatMap(new Func1<MobileBean, Observable<String>>() {
                    @Override
                    public Observable<String> call(MobileBean mobileBean) {
                        Logs.e(mobileBean.getMobile());
                        return Observable.just(mobileBean.getMobile());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String mobile) {
//                    String where = " address like '%" + mobile + "%' AND protocol = '0' AND type = 1 AND date >  "
//                        + (System.currentTimeMillis() - ENVs.COUNT_SMS_INTERVAL);
                        String where = " address like '%" + mobile + "%' AND protocol = '0' AND type = 1 AND date >  "
                                + selectorDate;
                        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(SmsObserver.SMS_INBOX_URI), SmsObserver.PROJECTION,
                                where, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
                        if (null == cursor) {
                            Logs.e(mobile + ":空数据");
                            return;
                        }
                        while (cursor.moveToNext()) {
                            String smsSender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                            String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                            long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                            boolean saved = DbWrapper.isSaved(date);
                            mSmsList.add(new SmsBean(date, smsSender, smsBody, saved, saved ? "已完成" : "未上传"));
                        }
                        closeCursor(cursor);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String mobile) {
                        Logs.e(mSmsList.size());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logs.e(throwable);
                        getView().responseSmsData(mSmsList);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Collections.sort(mSmsList, new Comparator<SmsBean>() {
                            @Override
                            public int compare(SmsBean o1, SmsBean o2) {
                                long i = o2.getCreate_time() - o1.getCreate_time();
                                return (int) i;
                            }
                        });
                        getView().responseSmsData(mSmsList);
                        Logs.e(mSmsList.size());
                    }
                });
    }

    /**
     * 关闭游标
     *
     * @param cursor 游标
     */
    private void closeCursor(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return;
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public void keepService() {
        RxPermissions permissions = new RxPermissions(getView().getBaseActivity());
        permissions.request(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        getView().startServiceEx(new Intent(getView().getBaseActivity(), SmsService.class));
                        new SmsObserver(new Handler(getMainLooper())).registerObserver();
                    }
                });
        register();
    }


    /**
     * 注册保持线程的广播
     */
    private void register() {
        //注册监听锁屏广播
        if (mKeepLiveReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getView().getBaseActivity().registerReceiver(mKeepLiveReceiver, filter);
        }
        //注册监听锁屏广播
        if (mSystemReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getView().getBaseActivity().registerReceiver(mSystemReceiver, filter);
        }
    }

    @Override
    public void detachView() {
        if (mKeepLiveReceiver != null) {
            getView().getBaseActivity().unregisterReceiver(mKeepLiveReceiver);
        }
        if (mSystemReceiver != null) {
            getView().getBaseActivity().unregisterReceiver(mSystemReceiver);
        }
        super.detachView();
    }

    @Override
    public void requestPostSms(final int index) {
        SmsBean data = mSmsList.get(index);
        getSmsBeanPostObservable(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ApiResult>() {
                    @Override
                    public void call(ApiResult apiResult) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getView().responseSmsPost();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        getView().responseSmsPost();
                    }
                });
    }

    private Observable<ApiResult> getSmsBeanPostObservable(final SmsBean smsBean) {
        return HttpUtils.getSmsInfoService().smspost(String.valueOf(smsBean.getCreate_time() / 1000), smsBean.getMobile(), smsBean.getContent())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DbWrapper.getSession().getSmsBeanDao().insert(smsBean);
                        smsBean.setIsSuccess(false);
                        smsBean.setErrorMsg(getView().getBaseActivity().getString(R.string.error_network));
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ApiResult>() {
                    @Override
                    public void call(ApiResult apiResult) {
                        smsBean.setIsSuccess(true);
                        DbWrapper.getSession().getSmsBeanDao().delete(smsBean);
                        DbWrapper.getSession().getSuccessSmsBeanDao().insertOrReplace(new SuccessSmsBean(smsBean.getCreate_time(), smsBean.getMobile(), smsBean.getContent()));
                    }
                });
    }

    @Override
    public void requestPostSmsList() {
        final int[] index = {0};
        Observable.from(mSmsList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<SmsBean, Boolean>() {
                    @Override
                    public Boolean call(SmsBean smsBean) {
                        return !smsBean.getIsSuccess();
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<SmsBean, Observable<ApiResult>>() {
                    @Override
                    public Observable<ApiResult> call(SmsBean smsBean) {
                        return getSmsBeanPostObservable(smsBean);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ApiResult>() {
                    @Override
                    public void call(ApiResult apiResult) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getView().responseSmsListPost();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        getView().responseSmsListPost();
                    }
                });
    }
}
