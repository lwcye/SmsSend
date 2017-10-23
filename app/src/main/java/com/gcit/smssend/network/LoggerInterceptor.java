package com.gcit.smssend.network;


import com.gcit.smssend.utils.JsonUtil;
import com.gcit.smssend.utils.Logs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * <p>打印日志拦截器</p><br>
 *
 * @author - lwc
 * @date - 2017/7/7
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class LoggerInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response oldResponse;
        try {
            oldResponse = chain.proceed(request);
        } catch (IOException e) {
            throw e;
        }
        StringBuilder logger = new StringBuilder();
        logger.append("request:" + oldResponse.request().url());

        RequestBody reqBody = oldResponse.request().body();
        if (reqBody instanceof FormBody) {
            FormBody body = (FormBody) reqBody;
            int size = body.size();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                map.put(body.name(i), body.value(i));
            }
            //请求JSON
            logger.append("\nJson:").append(JsonUtil.objectToString(map));
        } else {
            Buffer buffer = new Buffer();
            try {
                reqBody.writeTo(buffer);
            } catch (IOException e) {
            }
            logger.append("\nJson:").append(new String(buffer.readByteArray()));
        }

        // 处理应答
        MediaType mediaType = oldResponse.body().contentType();
        byte[] responseBytes = oldResponse.body().bytes();
        String response = new String(responseBytes);
        // 响应数据
        logger.append("\nResponse:").append(response);

        Headers.Builder headersBuilder = oldResponse.headers().newBuilder();
        Response.Builder builder = oldResponse.newBuilder();
        builder.headers(headersBuilder.build())
                .body(ResponseBody.create(mediaType, responseBytes));
        Logs.d(logger.toString());

        return builder.build();
    }

}
