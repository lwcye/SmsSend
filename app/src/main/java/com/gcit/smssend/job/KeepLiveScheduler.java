package com.gcit.smssend.job;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.List;

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
public class KeepLiveScheduler extends JobScheduler{
    private static final KeepLiveScheduler ourInstance = new KeepLiveScheduler();

    public static KeepLiveScheduler getInstance() {
        return ourInstance;
    }

    private KeepLiveScheduler() {
    }

    @Override
    public int schedule(JobInfo jobInfo) {

        return jobInfo.getId();
    }

    @Override
    public int enqueue(JobInfo jobInfo, JobWorkItem jobWorkItem) {
        return 0;
    }

    @Override
    public void cancel(int i) {

    }

    @Override
    public void cancelAll() {

    }

    @NonNull
    @Override
    public List<JobInfo> getAllPendingJobs() {
        return null;
    }

    @Nullable
    @Override
    public JobInfo getPendingJob(int i) {
        return null;
    }

}
