package com.gcit.smssend.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * <p>describe</p><br>
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
public class SmsModel {
    @Id
    private Long create_time;
    private String mobile;
    private String content;
    private boolean isSuccess;
    private String errorMsg;

    @Generated(hash = 1848191635)
    public SmsModel(Long create_time, String mobile, String content, boolean isSuccess,
            String errorMsg) {
        this.create_time = create_time;
        this.mobile = mobile;
        this.content = content;
        this.isSuccess = isSuccess;
        this.errorMsg = errorMsg;
    }

    @Generated(hash = 1496777313)
    public SmsModel() {
    }

    public Long getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(Long create_time) {
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
