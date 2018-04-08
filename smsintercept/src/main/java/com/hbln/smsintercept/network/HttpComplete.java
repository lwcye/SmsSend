package com.hbln.smsintercept.network;


import com.hbln.smsintercept.base.BaseActivity;

import rx.functions.Action0;

/**
 * <p>DESCRIBE</p><br>
 *
 * @author lwc
 * @date 2017/4/29 0:11
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class HttpComplete implements Action0 {
    private BaseActivity mBaseView;
    
    public HttpComplete(BaseActivity baseView) {
        mBaseView = baseView;
    }
    
    public HttpComplete() {
    }
    
    @Override
    public void call() {
        if (null != mBaseView) {
            mBaseView.hideLoading();
        }
    }
}
