package com.gcit.smssend.receiver;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.gcit.smssend.utils.Logs;

/**
 * <p>describe</p><br>
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
    static final String[] PROJECTION = new String[]{
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
    };
    private static final String SMS_INBOX_URI = "content://sms/inbox";//API level>=23,可直接使用Telephony.Sms.Inbox.CONTENT_URI
    private static final String SMS_URI = "content://sms";//API level>=23,可直接使用Telephony.Sms.CONTENT_URI

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Logs.e(selfChange);
        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(SMS_INBOX_URI), PROJECTION,
                Telephony.Sms.READ + "=?", new String[]{"0"}, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        getSmsCodeFromObserver(cursor);
    }

    public SmsObserver(Handler handler) {
        super(handler);
    }

    /**
     * 注册内容观察者
     */
    public void registerObserver() {
        Utils.getApp().getContentResolver().registerContentObserver(Uri.parse(SMS_URI), true, this);
    }

    /**
     * 包访问级别:提高性能
     * 从内容观察者得到短信验证码
     *
     * @param cursor
     */
    void getSmsCodeFromObserver(Cursor cursor) {
        if (cursor == null) {
            return;
        }

        while (cursor.moveToNext()) {
            String smsSender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
            String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
            Logs.e("smsSender", smsSender, "smsBody", smsBody);
            long id = cursor.getLong(cursor.getColumnIndex(Telephony.Sms._ID));
            Logs.e("id", id);

            String date = TimeUtils.millis2String(cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE)));
            Logs.e("date", date);
        }

        closeCursor(cursor);
    }

    private void closeCursor(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return;
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }
}