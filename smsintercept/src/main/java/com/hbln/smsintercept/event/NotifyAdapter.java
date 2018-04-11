package com.hbln.smsintercept.event;

import com.hbln.smsintercept.db.bean.SmsBean;

/**
 * Created by Administrator on 2018/4/8.
 */

public class NotifyAdapter {
    public SmsBean mSmsBean;

    public NotifyAdapter(SmsBean smsBean) {
        mSmsBean = smsBean;
    }
}
