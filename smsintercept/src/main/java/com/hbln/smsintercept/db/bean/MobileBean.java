package com.hbln.smsintercept.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.hbln.smsintercept.db.DbWrapper;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

/**
 * <p>手机模型</p><br>
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
public class MobileBean implements Parcelable {
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

    /**
     * 导入数据
     * @return List<MobileBean>
     */
    public static List<MobileBean> loadMobile() {
        return DbWrapper.getSession().getMobileBeanDao().loadAll();
    }

    /**
     * 插入数据
     * @param mMobileBean 数据
     */
    public static void insertMobile(MobileBean mMobileBean) {
        DbWrapper.getSession().getMobileBeanDao().insert(mMobileBean);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mobile);
    }

    protected MobileBean(Parcel in) {
        this.mobile = in.readString();
    }

    public static final Parcelable.Creator<MobileBean> CREATOR = new Parcelable.Creator<MobileBean>() {
        @Override
        public MobileBean createFromParcel(Parcel source) {
            return new MobileBean(source);
        }

        @Override
        public MobileBean[] newArray(int size) {
            return new MobileBean[size];
        }
    };
}
