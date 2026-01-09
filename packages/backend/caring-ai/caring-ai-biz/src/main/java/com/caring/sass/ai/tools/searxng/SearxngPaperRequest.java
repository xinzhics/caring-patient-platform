package com.caring.sass.ai.tools.searxng;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.caring.sass.common.utils.StringUtils;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 搜索引擎学术搜索
 *
 * @author leizhi
 */
@Slf4j
@Accessors(chain = true)
public class SearxngPaperRequest {

    /**
     * 自检搜索引擎API根地址
     */
    private static final String SEARXNG_URL = "http://93.179.103.99:8080/search";

    /**
     * 查询关键词
     */
    private String q;

    /**
     * 搜索类型
     */
    private static final String CATEGORIES = "science";

    /**
     * 搜索返回数据格式
     */
    private static final String FORMAT = "json";

    /**
     * 用到的搜索引擎
     */
    private static final String ENGINES = "arxiv,pubmed,google scholar,internetarchivescholar";


    public SearxngPaperRequest(String query) {
        if (StringUtils.isEmpty(query)) {
            throw new RuntimeException("query can't be empty");
        }
        this.q = query;
    }

    private String buildRequestUrl() throws Exception {
        return SEARXNG_URL
                + "?q=" + URLEncoder.encode(this.q, StandardCharsets.UTF_8.toString())
                + "&categories=" + URLEncoder.encode(CATEGORIES, StandardCharsets.UTF_8.toString())
                + "&format=" + URLEncoder.encode(FORMAT, StandardCharsets.UTF_8.toString())
                + "&engines=" + URLEncoder.encode(ENGINES, StandardCharsets.UTF_8.toString());
    }

    public List<Paper> sendRequest() {
        try {
            String requestUrl = buildRequestUrl();
            log.debug("Request URL: {}", requestUrl);

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("Failed : HTTP error code : {}", responseCode);
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response to List<Paper>
            JSONArray resultsArray = JSON.parseObject(response.toString()).getJSONArray("results");
            if (Objects.isNull(resultsArray) || resultsArray.isEmpty()) {
                return null;
            }
            return JSON.parseObject(resultsArray.toJSONString(), new TypeReference<List<Paper>>() {
            });
        } catch (Exception e) {
            log.error("Error occurred while sending request.", e);
            return null;
        }
    }

    public static void main(String[] args) {
        SearxngPaperRequest example = new SearxngPaperRequest("obesity AND metabolism improvement");
        List<Paper> response = example.sendRequest();
        for (Paper paper : response) {
            System.out.println(paper);
        }
    }

}
