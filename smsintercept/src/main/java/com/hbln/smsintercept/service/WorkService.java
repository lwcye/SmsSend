package com.hbln.smsintercept.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.blankj.utilcode.util.LogUtils;
import com.hbln.smsintercept.db.bean.SmsBean;
import com.hbln.smsintercept.event.SmsEvent;
import com.hbln.smsintercept.receiver.SmsObserver;
import com.xdandroid.hellodaemon.AbsWorkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * <p>线程</p><br>
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
    private final SmsObserver smsObserver = new SmsObserver(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            LogUtils.e(message.obj);
            return false;
        }
    }));

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

    @Override
    public void onCreate() {
        super.onCreate();
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
        sDisposable = Observable
                .interval(60, TimeUnit.SECONDS)
                //取消任务时取消定时唤醒
                .doOnNext(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        cancelJobAlarmSub();
                        smsObserver.unregisterObserver();
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {
                        smsObserver.registerObserver();
                        if (!EventBus.getDefault().isRegistered(WorkService.this)) {
                            LogUtils.d("没有注册过 EventBus");
                            EventBus.getDefault().register(WorkService.this);
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOrderEvent(SmsEvent smsEvent) {
        SmsBean.requestPostSms(smsEvent.mSmsBean, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(WorkService.this);
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
        LogUtils.e("onServiceKilled");
    }
}
