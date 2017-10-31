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

import java.util.List;

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
    static final String[] PROJECTION = new String[]{
        Telephony.Sms._ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE
    };
    private static final String SMS_INBOX_URI = "content://sms/inbox";//API level>=23,可直接使用Telephony.Sms.Inbox.CONTENT_URI
    private static final String SMS_URI = "content://sms";//API level>=23,可直接使用Telephony.Sms.CONTENT_URI
    
    public SmsObserver(Handler handler) {
        super(handler);
    }
    
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Logs.e(selfChange);
        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(SMS_INBOX_URI), PROJECTION,
            Telephony.Sms.READ + "=?", new String[]{"0"}, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        getSmsCodeFromObserver(cursor);
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
     * @param cursor 游标
     */
    void getSmsCodeFromObserver(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        long successCount = BaseApp.getSpUtils().getInt(SharedPrefs.SMS_OBSERVER_COUNT, -1);
        int count = cursor.getCount();
        if (count > successCount) {
            List<MobileBean> mobileBeen = DbWrapper.getSession().getMobileBeanDao().loadAll();
            while (cursor.moveToNext()) {
                String smsSender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                
                long timeSpanByNow = TimeUtils.getTimeSpanByNow(date, TimeConstants.DAY);
                if (timeSpanByNow < 5) {
                    //过滤5天以上的数据
                    if (filterPhoneNum(smsSender, mobileBeen)) {
                        //过滤设置的短信号码,默认全部上传
                        if (!DbWrapper.isSaved(date)) {
                            //成功短信的数据库不存在,就发送出去
                            EventBus.getDefault().post(new SmsEvent(new SmsBean(date, smsSender, smsBody, false, "上传中")));
                        }
                    }
                } else {
                    break;
                }
            }
            BaseApp.getSpUtils().put(SharedPrefs.SMS_OBSERVER_COUNT, count);
        }
        closeCursor(cursor);
        //关闭游标
    }
    
    /**
     * 过滤电话号码
     *
     * @param smsSender 电话号码
     */
    private boolean filterPhoneNum(String smsSender, List<MobileBean> mobileBeen) {
        //过滤号码
        if (mobileBeen != null && mobileBeen.size() > 0) {
            for (MobileBean mobileBean : mobileBeen) {
                if (smsSender != null && smsSender.contains(mobileBean.getMobile())) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
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
