package com.hbln.smsintercept.base;

import android.app.Application;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.hbln.smsintercept.constant.SharedPrefs;
import com.hbln.smsintercept.constant.URLs;
import com.hbln.smsintercept.db.DbWrapper;
import com.hbln.smsintercept.network.HttpUtils;
import com.hbln.smsintercept.network.RetrofitWrapper;
import com.hbln.smsintercept.service.WorkService;
import com.umeng.analytics.MobclickAgent;
import com.xdandroid.hellodaemon.DaemonEnv;

/**
 * Created by Administrator on 2018/3/31.
 */

public class MyApplication extends Application {
    private static MyApplication sBaseApp;
    /**
     * 系统偏好设置
     */
    private static SPUtils spUtils;
    /**
     * 服务是否存在
     */
    public boolean isKeepLive = false;

    public static MyApplication getInstance() {
        return sBaseApp;
    }

    /**
     * 获得SharedPreference进行操作
     *
     * @return SPUtils
     */
    public static SPUtils getSpUtils() {
        if (null == spUtils) {
            spUtils = SPUtils.getInstance(SharedPrefs.FILE_NAME);
        }
        return spUtils;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sBaseApp = this;
        //工具类
        Utils.init(this);
        CrashUtils.init();
        LogUtils.getConfig().setLogSwitch(true);
        LogUtils.getConfig().setLog2FileSwitch(true);
        LogUtils.getConfig().setConsoleFilter(LogUtils.V);
        LogUtils.getConfig().setGlobalTag("cqcity");
        // Retrofit初始化
        RetrofitWrapper.init(this);
        String url = getSpUtils().getString(SharedPrefs.URL, "");
        if (EmptyUtils.isEmpty(url)) {
            HttpUtils.setUrl(URLs.HTTP_HOST);
        } else {
            HttpUtils.setUrl(url);
        }
        //初始化SharedPreference文件
        spUtils = SPUtils.getInstance(SharedPrefs.FILE_NAME);
        //数据库
        DbWrapper.init(this);

        //友盟统计
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "59eb3c7cf29d98676400000c", "lwcye"));
        MobclickAgent.setCatchUncaughtExceptions(true);

        //保活 helloDemon
        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(this, WorkService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        WorkService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(WorkService.class);


    }
}
