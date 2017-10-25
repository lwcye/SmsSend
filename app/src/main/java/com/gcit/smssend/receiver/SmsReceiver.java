package com.gcit.smssend.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import com.gcit.smssend.base.BaseReceiver;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.db.bean.MobileBean;
import com.gcit.smssend.db.bean.SmsBean;
import com.gcit.smssend.event.SmsEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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
            String smsSender = null;
            StringBuilder smsBody = new StringBuilder();
            long date = 0;
            for (int i = 0; i < messages.length; i++) {
                if (i == 0) {
                    smsSender = messages[0].getOriginatingAddress();
                    date = messages[0].getTimestampMillis();
                }
                smsBody.append(messages[0].getMessageBody());
            }

            //过滤号码
            List<MobileBean> mobileBeen = DbWrapper.getSession().getMobileBeanDao().loadAll();
            if (mobileBeen != null && mobileBeen.size() > 0) {
                for (MobileBean mobileBean : mobileBeen) {
                    if (smsSender != null && smsSender.contains(mobileBean.getMobile())) {
                        EventBus.getDefault().post(new SmsEvent(new SmsBean(date, smsSender, smsBody.toString(), false, "上传中")));
                        return;
                    }
                }
            } else {
                EventBus.getDefault().post(new SmsEvent(new SmsBean(date, smsSender, smsBody.toString(), false, "上传中")));
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
