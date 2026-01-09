package com.caring.sass.common.difyclient.util;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * HTTP客户端工具类
 */
public class HttpClientUtils {
    /**
     * JSON媒体类型
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 创建默认的OkHttpClient
     *
     * @return OkHttpClient实例
     */
    public static OkHttpClient createDefaultClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 创建自定义超时时间的OkHttpClient
     *
     * @param connectTimeout 连接超时时间（毫秒）
     * @param readTimeout    读取超时时间（毫秒）
     * @param writeTimeout   写入超时时间（毫秒）
     * @return OkHttpClient实例
     */
    public static OkHttpClient createClient(int connectTimeout, int readTimeout, int writeTimeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .build();
    }
}
