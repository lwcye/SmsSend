package com.gcit.smssend.receiver;

import android.content.Context;
import android.content.Intent;

import com.gcit.smssend.base.BaseReceiver;
import com.gcit.smssend.utils.keepLive.KeepLiveManager;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2017/10/23
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class SystemReceiver extends BaseReceiver {
    @Override
    public void onReceive(Context context, Intent intent, int flag) {
        KeepLiveManager.getInstance().startKeepLiveService();
    }
}
