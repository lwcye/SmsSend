package com.gcit.smssend.job;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

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
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class KeepLiveService extends JobService {
    private volatile static Service mKeepLiveService = null;
    /** 通过变量控制是否需要退出Service */
    private boolean isServiceExit = false;

    public static boolean isServiceLive() {
        return mKeepLiveService != null;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
