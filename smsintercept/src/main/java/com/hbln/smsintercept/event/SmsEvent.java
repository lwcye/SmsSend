package com.hbln.smsintercept.event;


import com.hbln.smsintercept.db.bean.SmsBean;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2017/10/25
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class SmsEvent {
    public SmsBean mSmsBean;

    public SmsEvent(SmsBean smsBean) {
        mSmsBean = smsBean;
    }
}
