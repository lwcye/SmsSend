package com.hbln.smsintercept.network;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseActivity;
import com.hbln.smsintercept.mvp.BaseView;
import com.hbln.smsintercept.utils.ResUtils;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import rx.functions.Action1;

/**
 * <p>网络请求错误处理</p><br>
 *
 * @author lwc
 * @date 2017/3/20 16:07
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class HttpError implements Action1<Throwable> {
    public final static int ERROR_CODE_TIMEOUT = 90;
    public final static int ERROR_CODE_ELSE = 91;
    /** 基本视图 */
    private BaseActivity mBaseView;

    /**
     * 无参构造类
     */
    public HttpError() {
    }

    /**
     * 错误情况
     *
     * @param errorCode 错误代码
     * @param message 错误信息
     */
    public void error(int errorCode, String message) {
        ToastUtils.showShort(message);
    }

    /**
     * 构造类，传入BaseView以便取消Loading
     *
     * @param baseView BaseView
     */
    public HttpError(BaseActivity baseView) {
        mBaseView = baseView;
    }

    @Override
    public void call(Throwable throwable) {
        if (null != mBaseView) {
            mBaseView.hideLoading();
        }
        if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException ||
                throwable instanceof SocketException || throwable instanceof UnknownHostException) {
            // 网络超时
            error(ERROR_CODE_TIMEOUT, ResUtils.getString(R.string.error_network_data));
            return;
        }
        // 其他错误
        error(ERROR_CODE_ELSE, ResUtils.getString(R.string.error_network_data));
        LogUtils.e(throwable);
    }
}
