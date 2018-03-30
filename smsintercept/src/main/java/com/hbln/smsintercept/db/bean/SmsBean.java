package com.hbln.smsintercept.db.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * <p>发送失败的短信</p><br>
 *
 * @author - lwc
 * @date - 2017/10/24
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
@Entity
public class SmsBean {
    @Id
    private long create_time;
    private String mobile;
    private String content;
    private boolean isSuccess;
    private String errorMsg;
    @Generated(hash = 899343996)
    public SmsBean(long create_time, String mobile, String content, boolean isSuccess,
            String errorMsg) {
        this.create_time = create_time;
        this.mobile = mobile;
        this.content = content;
        this.isSuccess = isSuccess;
        this.errorMsg = errorMsg;
    }
    @Generated(hash = 1006465373)
    public SmsBean() {
    }
    public long getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }
    public String getMobile() {
        return this.mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public boolean getIsSuccess() {
        return this.isSuccess;
    }
    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    public String getErrorMsg() {
        return this.errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
