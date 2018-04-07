package com.hbln.smsintercept.network.service;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2017/10/27
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public interface TestService {
    @FormUrlEncoded
    @POST("/appgwuser/cqcity-goods-core/api/genera/goods/goodsinfo/getgoodsinfolist")
    Observable<Object> errorCode(@Field("goodsTypeId") String goodsTypeId);
}
