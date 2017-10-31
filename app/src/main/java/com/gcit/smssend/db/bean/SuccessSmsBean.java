package com.gcit.smssend.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * <p>发送成功的短信</p><br>
 *
 * @author lwc
 * @date 2017/10/31 20:23
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
@Entity
public class SuccessSmsBean {
    @Id private long create_time;
    private String mobile;
    private String content;
    
    @Generated(hash = 783744412)
    public SuccessSmsBean(long create_time, String mobile, String content) {
        this.create_time = create_time;
        this.mobile = mobile;
        this.content = content;
    }

    @Generated(hash = 1692463496)
    public SuccessSmsBean() {
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

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }
    
}
