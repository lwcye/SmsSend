package com.gcit.smssend.db.bean;

/**
 * <p>DESCRIBE</p><br>
 *
 * @author lwc
 * @date 2017/10/24 21:10
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class SmsBean {
    private String create_time;
    private String mobile;
    private String content;
    
    public SmsBean(String create_time, String mobile, String content) {
        this.create_time = create_time;
        this.mobile = mobile;
        this.content = content;
    }
}
