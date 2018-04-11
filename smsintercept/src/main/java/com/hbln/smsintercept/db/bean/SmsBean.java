package com.hbln.smsintercept.db.bean;


import com.hbln.smsintercept.R;
import com.hbln.smsintercept.db.DbWrapper;
import com.hbln.smsintercept.event.NotifyAdapter;
import com.hbln.smsintercept.network.ApiResult;
import com.hbln.smsintercept.network.HttpUtils;
import com.hbln.smsintercept.utils.ResUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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

    /**
     * 上传短信的观察者
     * @param smsBean
     * @return
     */
    public static Observable<ApiResult> getSmsBeanPostObservable(final SmsBean smsBean) {
        return HttpUtils.getSmsInfoService().smspost(String.valueOf(smsBean.getCreate_time() / 1000), smsBean.getMobile(), smsBean.getContent())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DbWrapper.getSession().getSmsBeanDao().insert(smsBean);
                        smsBean.setIsSuccess(false);
                        smsBean.setErrorMsg(ResUtils.getString(R.string.error_network));
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ApiResult>() {
                    @Override
                    public void call(ApiResult apiResult) {
                        smsBean.setIsSuccess(true);
                        DbWrapper.getSession().getSmsBeanDao().delete(smsBean);
                        DbWrapper.getSession().getSuccessSmsBeanDao().insertOrReplace(new SuccessSmsBean(smsBean.getCreate_time(), smsBean.getMobile(), smsBean.getContent()));
                    }
                });
    }

    /**
     * 上传短信
     * @param smsBean
     */
    public static void requestPostSms(final SmsBean smsBean) {
        getSmsBeanPostObservable(smsBean)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ApiResult>() {
                    @Override
                    public void call(ApiResult apiResult) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        EventBus.getDefault().post(new NotifyAdapter(smsBean));
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        EventBus.getDefault().post(new NotifyAdapter(smsBean));
                    }
                });
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
