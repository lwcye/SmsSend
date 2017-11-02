package com.gcit.smssend.feature.main;

import com.gcit.smssend.db.bean.SmsBean;
import com.gcit.smssend.mvp.BasePresenter;
import com.gcit.smssend.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainContract {
    interface View extends BaseView {
        void responseSmsData(List<SmsBean> smsBeen);
        
        void responseSmsPost();
        
        void responseSmsListPost();
    }
    
    interface Presenter extends BasePresenter<View> {
        void loadSmsData(long selectorDate);
        
        void keepService();
        
        void requestPostSms(final int index);
        
        void requestPostSmsList();
    }
}
