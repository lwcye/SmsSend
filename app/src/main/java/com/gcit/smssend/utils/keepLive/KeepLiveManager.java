package com.gcit.smssend.utils.keepLive;

import android.app.Activity;
import android.content.Intent;

import com.blankj.utilcode.util.Utils;
import com.gcit.smssend.service.SmsService;
import com.gcit.smssend.ui.activity.KeepLiveActivity;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2017/10/22
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class KeepLiveManager {
    private static final KeepLiveManager ourInstance = new KeepLiveManager();
    public Activity mKeepLiveActivity;

    private KeepLiveManager() {
    }

    public static KeepLiveManager getInstance() {
        return ourInstance;
    }

    public void startKeepLiveActivity() {
        Utils.getApp().startActivity(new Intent(Utils.getApp(), KeepLiveActivity.class));
    }

    public void finishKeepLiveActivity() {
        if (mKeepLiveActivity != null) {
            mKeepLiveActivity.finish();
        }
    }

    public void startKeepLiveService() {
        Utils.getApp().startService(new Intent(Utils.getApp(), SmsService.class));
    }
}
