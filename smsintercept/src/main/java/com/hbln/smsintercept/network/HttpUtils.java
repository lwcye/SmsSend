package com.hbln.smsintercept.network;

import com.blankj.utilcode.util.AppUtils;
import com.hbln.smsintercept.network.service.SmsInfoService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;

/**
 * 网络接口定义
 *
 * @author lwc
 * @date 2017.03.17
 * @note -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class HttpUtils {
    /**
     * 统计服务URL
     */
    private static SmsInfoService sSmsInfoService;
    /**
     * 统计服务URL
     */
    private static String sUrl = "";

    /**
     * 设置统计服务的URL
     *
     * @param url url
     */
    public static void setUrl(String url) {
        sUrl = url;
        sSmsInfoService = null;
    }

    /**
     * 设置统计服务的URL
     *
     */
    public static String getUrl() {
        return sUrl;
    }

    /**
     * 获取产品服务(固定密钥)
     *
     * @return 服务对象
     */
    public static synchronized SmsInfoService getSmsInfoService() {
        if (sSmsInfoService == null) {
            // DES加密拦截
            List<Interceptor> ssInterceptorList = new ArrayList<>();
            if (AppUtils.isAppDebug()) {
                ssInterceptorList.add(new LoggerInterceptor());
            }
            sSmsInfoService = RetrofitWrapper.createInstance(sUrl, RetrofitWrapper.CONVERTER_GSON,
                    ssInterceptorList, false).create(SmsInfoService.class);
        }

        return sSmsInfoService;
    }
}
