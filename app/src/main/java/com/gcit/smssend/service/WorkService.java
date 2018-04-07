package com.gcit.smssend.service;

import android.content.Intent;
import android.os.IBinder;

import com.gcit.smssend.model.SmsModel;
import com.xdandroid.hellodaemon.AbsWorkService;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2018/3/28 17:49
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class WorkService extends AbsWorkService {
    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Subscription sDisposable;

    public WorkService() {
    }

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) {
            sDisposable.unsubscribe();
        }
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        System.out.println("检查磁盘中是否有上次销毁时保存的数据");
        sDisposable = Observable
                .interval(60, TimeUnit.SECONDS)
                //取消任务时取消定时唤醒
                .doOnNext(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        cancelJobAlarmSub();
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {
                        SmsModel.loadSmsListFromSystem();
                    }
                });
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isUnsubscribed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        System.out.println("保存数据到磁盘。");
    }
}
