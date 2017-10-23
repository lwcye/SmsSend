package com.gcit.smssend.network.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

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
public interface SmsInfoService {
    @FormUrlEncoded
    @POST("smsapi/smsinfo")
    Observable<Object> smsinfo(@Field("mobile") String mobile);
}
