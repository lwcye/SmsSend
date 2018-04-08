package com.hbln.smsintercept.network;

import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseActivity;
import com.hbln.smsintercept.utils.ResUtils;

import rx.functions.Action1;

/**
 * <p>DESCRIBE</p><br>
 *
 * @author lwc
 * @date 2017/12/13 22:57
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public abstract class HttpResult<T> implements Action1<T> {
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_NULL = 2;
    public static final int CODE_FAIL = 0;
    public static final int CODE_TOKEN = 100001;

    /** 基类 */
    private BaseActivity mView;
    
    /**
     * 无参构造类
     */
    protected HttpResult() {
    }
    
    /**
     * 构造类，用于取消loading
     *
     * @param view BaseView
     */
    protected HttpResult(BaseActivity view) {
        mView = view;
    }
    
    /**
     * 请求结果
     *
     * @param t 请求结果实体
     */
    public abstract void result(T t);
    
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
     * 处理结果逻辑
     *
     * @param t 请求结果实例
     */
    @Override
    public void call(T t) {
        if (EmptyUtils.isEmpty(t)) {
            error(CODE_NULL, ResUtils.getString(R.string.error_network_data));
            return;
        }
        result(t);
    }
}
