package com.gcit.smssend.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

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
    private String create_time;
    private String mobile;
    private String content;
    @Generated(hash = 994034542)
    public SmsModel(String create_time, String mobile, String content) {
        this.create_time = create_time;
        this.mobile = mobile;
        this.content = content;
    }
    @Generated(hash = 1496777313)
    public SmsModel() {
    }
    public String getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(String create_time) {
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
}
