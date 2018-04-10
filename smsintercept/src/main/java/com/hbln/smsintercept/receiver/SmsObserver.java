package com.hbln.smsintercept.receiver;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.hbln.smsintercept.base.MyApplication;
import com.hbln.smsintercept.constant.SharedPrefs;
import com.hbln.smsintercept.db.DbWrapper;
import com.hbln.smsintercept.db.bean.MobileBean;
import com.hbln.smsintercept.db.bean.SmsBean;
import com.hbln.smsintercept.event.SmsEvent;
import com.hbln.smsintercept.model.SmsModel;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <p>观察短信的信息</p><br>
 *
 * @author - lwc
 * @date - 2017/10/21
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class SmsObserver extends ContentObserver {
    public static final String[] PROJECTION = new String[]{
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
    };
    public static final String SMS_INBOX_URI = "content://sms/inbox";//API level>=23,可直接使用Telephony.Sms.Inbox.CONTENT_URI
    public static final String SMS_URI = "content://sms";//API level>=23,可直接使用Telephony.Sms.CONTENT_URI
    Subscription subscribe;

    public SmsObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        loadSmsData();
    }

    /**
     * 导入短信数据
     */
    public synchronized void loadSmsData() {
        final String sDay = TimeUtils.getNowString(new SimpleDateFormat("yyyyMMddHH", Locale.getDefault()));
        final String sOldDay = MyApplication.getSpUtils().getString(SharedPrefs.SMS_OBSERVER_COUNT_HOUR, "");
        if (subscribe != null) {
            if (!subscribe.isUnsubscribed()) {
                return;
            }
        }
        subscribe = Observable.from(MobileBean.loadMobile())
                .flatMap(new Func1<MobileBean, Observable<String>>() {
                    @Override
                    public Observable<String> call(MobileBean mobileBean) {
                        return Observable.just(mobileBean.getMobile());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String mobile) {
                        long day = System.currentTimeMillis() % TimeConstants.DAY + 8 * TimeConstants.HOUR;
                        final long currentDay = System.currentTimeMillis() - day;
                        String where = " address like '%" + mobile + "%' AND protocol = '0' AND type = 1 AND date >  " + currentDay;
                        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(SmsObserver.SMS_INBOX_URI), SmsObserver.PROJECTION,
                                where, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
                        if (null == cursor) {
                            LogUtils.e(mobile + ":空数据");
                            return;
                        }
                        int count = cursor.getCount();
                        if (sDay.equals(sOldDay)) {
                            long successCount = MyApplication.getSpUtils().getInt(mobile, -1);
                            LogUtils.e("以上传成功数量：", successCount);
                            LogUtils.e("本次请求的数量：", count);
                            if (count > successCount) {
                                postSmsBean(cursor);
                            }
                        } else {
                            postSmsBean(cursor);
                        }
                        MyApplication.getSpUtils().put(mobile, count);
                        CloseUtils.closeIO(cursor);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e(throwable);
                        MyApplication.getSpUtils().put(SharedPrefs.SMS_OBSERVER_COUNT_HOUR, sDay);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        MyApplication.getSpUtils().put(SharedPrefs.SMS_OBSERVER_COUNT_HOUR, sDay);
                    }
                });
    }

    private void postSmsBean(Cursor cursor) {
        while (cursor.moveToNext()) {
            String smsSender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
            String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
            long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
            if (!DbWrapper.isSaved(date)) {
                //成功短信的数据库不存在,就发送出去
                EventBus.getDefault().post(new SmsEvent(new SmsBean(date, smsSender, smsBody, false, "上传中")));
            }
        }
    }

    /**
     * 注册内容观察者
     */
    public void registerObserver() {
        Utils.getApp().getContentResolver().registerContentObserver(Uri.parse(SMS_URI), true, this);
    }

    public void unregisterObserver() {
        Utils.getApp().getContentResolver().unregisterContentObserver(this);
    }
}
