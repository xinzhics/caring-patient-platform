package com.caring.sass.ai.config;

import com.caring.sass.ai.entity.search.SearchResult;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 搜索工具类
 *
 * @author leizhi
 */
@Slf4j
@Component
public class BingSearchConfig {

    private final SearchProperties searchProperties;

    public BingSearchConfig(SearchProperties searchProperties) {
        this.searchProperties = searchProperties;
    }

    public SearchResult query(String query) {
        SearchResult results = new SearchResult();
        String encodedQuery;
        try {
            encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("编码异常", e);
            throw new RuntimeException(e);
        }
        OkHttpClient client = new OkHttpClient();
        // 默认返回记录数
        int count = 8;
        String requestUrl = searchProperties.getEndpoint() + "?q=" + encodedQuery + "&count=" + count;
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("Ocp-Apim-Subscription-Key", searchProperties.getSubscriptionKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String json = response.body().string();
            Gson gson = new Gson();
            results = gson.fromJson(json, new TypeToken<SearchResult>() {
            }.getType());
            log.debug("调用搜索返回结果:{}", results);
            return results;
        } catch (Exception e) {
            log.error("调用搜索接口异常", e);
            return results;
        }
    }

}
