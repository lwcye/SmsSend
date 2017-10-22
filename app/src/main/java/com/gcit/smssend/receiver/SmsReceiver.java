package com.gcit.smssend.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import com.blankj.utilcode.util.TimeUtils;
import com.gcit.smssend.base.BaseReceiver;
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
public class SmsReceiver extends BaseReceiver {
    private static final String SMS_RECEIVED_ACTION = Telephony.Sms.Intents.SMS_RECEIVED_ACTION;// 接收到短信时的action

    @Override
    public void onReceive(Context context, Intent intent, int flag) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            getSmsCodeFromReceiver(intent);

        }
    }

    /**
     * 包访问级别:提高性能
     * 从接收者中得到短信验证码
     *
     * @param intent
     */
    void getSmsCodeFromReceiver(Intent intent) {
        SmsMessage[] messages = null;
        if (Build.VERSION.SDK_INT >= 19) {
            messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            if (messages == null) {
                return;
            }
        } else {
            messages = getSmsUnder19(intent);
            if (messages == null) {
                return;
            }
        }

        if (messages.length > 0) {
            for (int i = 0; i < messages.length; i++) {
                SmsMessage sms = messages[i];
                Logs.e(sms);
                String smsSender = sms.getOriginatingAddress();
                String smsBody = sms.getMessageBody();
                String date = TimeUtils.millis2String(sms.getTimestampMillis());
                Logs.e("smsSender", smsSender, "smsBody", smsBody, "date", date);
            }
        }
    }

    @Nullable
    private SmsMessage[] getSmsUnder19(Intent intent) {
        SmsMessage[] messages;
        Bundle bundle = intent.getExtras();
        // 相关链接:https://developer.android.com/reference/android/provider/Telephony.Sms.Intents.html#SMS_DELIVER_ACTION
        Object[] pdus = (Object[]) bundle.get("pdus");

        if ((pdus == null) || (pdus.length == 0)) {
            return null;
        }

        messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        return messages;
    }
}
