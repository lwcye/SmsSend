package com.gcit.smssend.base;

import android.app.Application;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.gcit.smssend.BuildConfig;
import com.gcit.smssend.constant.SharedPrefs;
import com.gcit.smssend.constant.URLs;
import com.gcit.smssend.db.DbWrapper;
import com.gcit.smssend.network.HttpUtils;
import com.gcit.smssend.network.RetrofitWrapper;
import com.gcit.smssend.service.WorkService;
import com.gcit.smssend.utils.Logs;
import com.umeng.analytics.MobclickAgent;
import com.xdandroid.hellodaemon.DaemonEnv;


/**
 * <p>Application</p><br>
 *
 * @author lwc
 * @date 2017/3/25 18:03
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class BaseApp extends Application {
    private static BaseApp sBaseApp;
    /** 系统偏好设置 */
    private static SPUtils spUtils;
    /** 服务是否存在 */
    public boolean isKeepLive = false;

    public static BaseApp getInstance() {
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
        init();
    }

    private void init() {
        //工具初始化
        Utils.init(this);
        // Retrofit初始化
        RetrofitWrapper.init(this);
        //初始化SharedPreference文件
        spUtils = SPUtils.getInstance(SharedPrefs.FILE_NAME);
        //Log初始化
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);
        LogUtils.getConfig().setGlobalTag("cqcity");

        // 日志初始化
        Logs.init(true, false, 'v', "cqcity");

        CrashUtils.init();

        //友盟统计
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "59eb3c7cf29d98676400000c", "lwcye"));
        MobclickAgent.setCatchUncaughtExceptions(true);

        //Retrofit
        RetrofitWrapper.init(this, 12000, 12000);
        HttpUtils.setUrl(URLs.HTTP_HOST);

        //db
        DbWrapper.init(this);

        //保活 helloDemon
        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(this, WorkService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        WorkService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(WorkService.class);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
