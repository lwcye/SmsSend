package com.gcit.smssend.network.service;

import com.gcit.smssend.network.ApiResult;

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
    Observable<ApiResult> smsinfo(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("smsapi/smspost")
    Observable<ApiResult> smspost(
            @Field("create_time") String create_time,
            @Field("mobile") String mobile,
            @Field("content") String content
    );

}
