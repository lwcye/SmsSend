package com.gcit.smssend.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.gcit.smssend.utils.Logs;
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
public class KeepLiveActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.e("onCreate");
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logs.e("onStart");
        KeepLiveManager.getInstance().mKeepLiveActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logs.e("onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        Logs.e("onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logs.e("onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logs.e("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logs.e("onDestroy");
    }
}
