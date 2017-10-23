package com.gcit.smssend.utils.keepLive;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.blankj.utilcode.util.Utils;
import com.gcit.smssend.job.KeepLiveScheduler;
import com.gcit.smssend.job.KeepLiveService;
import com.gcit.smssend.service.SmsService;
import com.gcit.smssend.ui.activity.KeepLiveActivity;
import com.gcit.smssend.utils.Logs;

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
        Logs.e("finishKeepLiveActivity", mKeepLiveActivity != null);
        if (mKeepLiveActivity != null) {
            mKeepLiveActivity.finish();
        }
    }

    /**
     * 启动需要的服务
     */
    public void startKeepLiveService() {
        Utils.getApp().startService(new Intent(Utils.getApp(), SmsService.class));
    }

    /**
     * 提升Service的优先级为前台Service
     * 作用：防杀，使Service不容易被杀死
     * 使用范围：到Android6.0版本
     * 原理：通过实现一个内部 Service，在 LiveService 和其内部 Service 中同时发送具有相同 ID 的 Notification，
     * 然后将内部 Service 结束掉。随着内部 Service 的结束，Notification 将会消失，但系统优先级依然保持为2。
     *
     * @param keepLiveService 需要保持的Service
     * @param innerService 内部Service
     */
    public void setForeGround(final Service keepLiveService, final Service innerService) {
        final int foregroundPushId = 1;
        Logs.e("liveService -> setForeground,keepLiveService : " + keepLiveService + ",innerService:" + innerService);
        if (keepLiveService != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                keepLiveService.startForeground(foregroundPushId, new Notification());
            } else {
                keepLiveService.startForeground(foregroundPushId, new Notification());
                if (innerService != null) {
                    innerService.startForeground(foregroundPushId, new Notification());
                    innerService.stopSelf();
                }
            }
        }
    }

    /**
     * 启动JobScheduler拉活
     * 使用范围:用于Android5.0以后版本进程保活，对被"强制停止"有效
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startJobScheduler() {
        int jobId = 1;
        JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(Utils.getApp(), KeepLiveService.class));
        builder.setPeriodic(10);
        builder.setPersisted(true);

        KeepLiveScheduler.getInstance().schedule(builder.build());
    }
}
