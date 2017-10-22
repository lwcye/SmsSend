package com.gcit.smssend.receiver;

import android.content.Context;
import android.content.Intent;

import com.gcit.smssend.base.BaseReceiver;
import com.gcit.smssend.utils.keepLive.KeepLiveManager;

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
public class KeepLiveReceiver extends BaseReceiver {
    @Override
    public void onReceive(Context context, Intent intent, int flag) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            KeepLiveManager.getInstance().startKeepLiveActivity();
        } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
            KeepLiveManager.getInstance().finishKeepLiveActivity();
        }
        KeepLiveManager.getInstance().startKeepLiveService();
    }
}
