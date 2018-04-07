package com.hbln.smsintercept.receiver;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.hbln.smsintercept.model.SmsModel;

import java.util.Calendar;

import rx.Subscription;

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
    private StringBuilder stringBuilder = new StringBuilder();

    public SmsObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        stringBuilder.append(selfChange).append("\n");
        loadSmsData();
    }

    /**
     * 导入短信数据
     */
    public synchronized void loadSmsData() {
        long time = TimeUtils.getNowMills() - 8 * TimeConstants.HOUR;
        String where = " protocol = '0' AND type = 1 AND date >  " + time;

        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(SmsObserver.SMS_INBOX_URI), SmsObserver.PROJECTION,
                where, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            stringBuilder.append(cursor.getCount()).append("\n");
            while (cursor.moveToNext()) {
                String smsSender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                stringBuilder.append("smsSender" + smsSender + "smsBody" + smsBody + "date" + TimeUtils.millis2String(date)).append("\n");
            }
        }
        SmsModel.closeCursor(cursor);
        LogUtils.e(stringBuilder.toString());
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
