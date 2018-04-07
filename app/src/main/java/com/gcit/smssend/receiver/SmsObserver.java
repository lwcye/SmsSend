package com.gcit.smssend.receiver;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.gcit.smssend.base.BaseApp;
import com.gcit.smssend.constant.SharedPrefs;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.db.bean.MobileBean;
import com.gcit.smssend.db.bean.SmsBean;
import com.gcit.smssend.event.SmsEvent;
import com.gcit.smssend.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
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
    
    public  SmsObserver(Handler handler) {
        super(handler);
    }
    
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        loadSmsData();
    }
    
    public synchronized void loadSmsData() {
        final String sDay = TimeUtils.millis2String(System.currentTimeMillis(), new SimpleDateFormat("dd", Locale.getDefault()));
        final String sOldDay = BaseApp.getSpUtils().getString(SharedPrefs.SMS_OBSERVER_COUNT_HOUR, "");
        if (subscribe != null) {
            Logs.e(subscribe.isUnsubscribed());
            if (!subscribe.isUnsubscribed()) {
                return;
            }
        }
        subscribe = Observable.from(DbWrapper.getSession().getMobileBeanDao().loadAll())
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
                        Logs.e(mobile + ":空数据");
                        return;
                    }
                    int count = cursor.getCount();
                    if (sDay.equals(sOldDay)) {
                        long successCount = BaseApp.getSpUtils().getInt(mobile, -1);
                        Logs.e("successCount", successCount);
                        Logs.e("count", count);
                        if (count > successCount) {
                            postSmsBean(cursor);
                        }
                    } else {
                        postSmsBean(cursor);
                    }
                    BaseApp.getSpUtils().put(mobile, count);
                    closeCursor(cursor);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Logs.e(throwable);
                    BaseApp.getSpUtils().put(SharedPrefs.SMS_OBSERVER_COUNT_HOUR, sDay);
                }
            }, new Action0() {
                @Override
                public void call() {
                    BaseApp.getSpUtils().put(SharedPrefs.SMS_OBSERVER_COUNT_HOUR, sDay);
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
}
