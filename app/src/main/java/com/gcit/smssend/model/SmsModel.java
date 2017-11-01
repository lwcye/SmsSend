package com.gcit.smssend.model;

import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.blankj.utilcode.util.Utils;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.db.bean.MobileBean;
import com.gcit.smssend.db.bean.SuccessSmsBean;
import com.gcit.smssend.receiver.SmsObserver;
import com.gcit.smssend.utils.Logs;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * <p>DESCRIBE</p><br>
 *
 * @author lwc
 * @date 2017/11/1 19:46
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class SmsModel {
    static List<SuccessSmsBean> mSmsList;
    
    public static void loadSmsList() {
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
            .subscribe(new Action1<String>() {
                @Override
                public void call(String mobile) {
                    String where = " address like '%" + mobile + "%' AND protocol = '0' AND type = 1 AND date >  "
                        + (System.currentTimeMillis() - 24 * 60 * 60 * 1000);
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
                        mSmsList.add(new SuccessSmsBean(date, smsSender, smsBody));
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Logs.e(throwable);
                }
            }, new Action0() {
                @Override
                public void call() {
                    Logs.e(mSmsList.size());
                }
            });
    }
}
