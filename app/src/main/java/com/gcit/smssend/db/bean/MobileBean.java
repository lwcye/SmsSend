package com.gcit.smssend.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

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
@Entity
public class MobileBean {
    @Id
    private String mobile;

    @Generated(hash = 1562637509)
    public MobileBean(String mobile) {
        this.mobile = mobile;
    }

    @Generated(hash = 1605438776)
    public MobileBean() {
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
