package com.caring.sass.wx.wxUtil;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ProxySelector;

public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static String get(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(2000).setConnectionRequestTimeout(1000).setSocketTimeout(2000).build();
            httpGet.setConfig(requestConfig);
            HttpClient httpclient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            return result;
        } catch (Exception e) {
            logger.error("GET请求失败", e);
        }
        return null;
    }


    public static String postWithoutWaiting(String url, JSONObject params) throws Exception {
        HttpClient httpclient = null;
        httpclient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(180000).setConnectionRequestTimeout(180000).setSocketTimeout(180000).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json");
        if (params != null) {
            String content = params.toString();
            StringEntity stringEntity = new StringEntity(content, "utf-8");
            httpPost.setEntity(stringEntity);
        }

        HttpResponse response = httpclient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        try {
            String returnVal = EntityUtils.toString(entity);
            return returnVal;
        } catch (Exception var10) {
            return null;
        }
    }
}
