package com.hbln.smsintercept.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hbln.smsintercept.base.BaseFragment;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public abstract class MVPBaseFragment<V extends BaseView, T extends BasePresenterImpl<V>> extends BaseFragment implements BaseView {
    public T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建Presenter
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    /**
     * 创建Presenter对象
     *
     * @return Presenter对象
     * @note 若需要使用MVP模式，则子类需实现此方法
     */
    protected abstract T createPresenter();

    @Override
    public Context getContext() {
        return super.getContext();
    }

}
