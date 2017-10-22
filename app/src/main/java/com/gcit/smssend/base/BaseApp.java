package com.gcit.smssend.base;

import android.app.Application;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.gcit.smssend.BuildConfig;
import com.gcit.smssend.constant.SharedPrefs;
import com.gcit.smssend.network.RetrofitWrapper;
import com.gcit.smssend.utils.Logs;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.umeng.analytics.MobclickAgent;

import java.net.Proxy;


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
        // 文件下载器
        FileDownloader.init(this, new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(10_000)
                        .readTimeout(10_000)
                        .proxy(Proxy.NO_PROXY)
                )));

        //友盟统计
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "59eb3c7cf29d98676400000c", "lwcye"));
        MobclickAgent.setCatchUncaughtExceptions(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}