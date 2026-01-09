package com.caring.sass.ai.tools.pubmed;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装 ESearch 请求参数的类。
 *
 * @author leizhi
 * @see <a href="https://www.ncbi.nlm.nih.gov/books/NBK25499/#chapter4.EInfo">
 */
@Slf4j
@Accessors(chain = true)
@Data
public class ESearchRequest {

    /**
     * 基础 URL
     */
    private static final String BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";

    /**
     * 数据库名称，如 "pubmed"、"protein" 等
     */
    private String db;

    /**
     * Entrez文本查询, 所有特殊字符必须进行URL编码,空格可以用加号（+）替换
     */
    private String term;

    /**
     * 是否使用历史服务器存储搜索结果
     */
    private boolean useHistory;

    /**
     * 从之前的 ESearch、EPost 或 ELINK 调用返回的 Web 环境字符串
     */
    private String webEnv;


    /**
     * 由之前 ESearch、EPost 或 ELINK 调用返回的查询键
     */
    private Integer queryKey;


    /**
     * 要显示的检索结果的起始索引（默认值为 0）
     */
    private Integer retStart;


    /**
     * 要显示的检索结果的最大数量（默认值为 20）
     */
    private Integer retMax;


    /**
     * 检索类型，如 "uilist" 或 "count"
     */
    private String retType;


    /**
     * 返回输出的格式，如 "XML" 或 "JSON"
     */
    private String retMode;


    /**
     * 用于排序检索结果的方法
     */
    private String sort;


    /**
     * 搜索的特定字段，如 "title"、"author" 等
     */
    private String field;


    /**
     * 用于限制搜索范围的日期类型，如 "MDAT"、"PDAT" 等
     */
    private String dateType;


    /**
     * 返回最近 n 天内的数据
     */
    private Integer relDate;

    /**
     * 用于限制搜索结果的时间范围的最小日期
     */
    private String minDate;

    /**
     * 用于限制搜索结果的时间范围的最大日期
     */
    private String maxDate;

    /**
     * pubmed库查询构造函数
     *
     * @param term 搜索关键词
     */
    public ESearchRequest(String term) {
        this.db = "pubmed";
        this.term = term;
    }

    public ESearchRequest(String db, String term) {
        this.db = db;
        this.term = term;
    }

    /**
     * 构建请求 URL
     */
    private String buildRequestUrl() {
        Map<String, String> params = new HashMap<>();
        params.put("db", db);
        params.put("term", term);
        if (useHistory) {
            params.put("usehistory", "y");
        }
        if (webEnv != null && !webEnv.isEmpty()) {
            params.put("WebEnv", webEnv);
        }
        if (queryKey != null) {
            params.put("query_key", queryKey.toString());
        }
        if (retStart != null) {
            params.put("retstart", retStart.toString());
        }
        if (retMax != null) {
            params.put("retmax", retMax.toString());
        }
        if (retType != null && !retType.isEmpty()) {
            params.put("rettype", retType);
        }
        if (retMode != null && !retMode.isEmpty()) {
            params.put("retmode", retMode);
        }
        if (sort != null && !sort.isEmpty()) {
            params.put("sort", sort);
        }
        if (field != null && !field.isEmpty()) {
            params.put("field", field);
        }
        if (dateType != null && !dateType.isEmpty()) {
            params.put("datetype", dateType);
        }
        if (relDate != null) {
            params.put("reldate", relDate.toString());
        }
        if (minDate != null && !minDate.isEmpty()) {
            params.put("mindate", minDate);
        }
        if (maxDate != null && !maxDate.isEmpty()) {
            params.put("maxdate", maxDate);
        }

        // 使用 StringBuilder 构建 URL
        boolean isFirstParam = true;
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        for (Map.Entry<String, String> param : params.entrySet()) {
            urlBuilder.append(isFirstParam ? "?" : "&")
                    .append(param.getKey())
                    .append("=")
                    .append(param.getValue());
            isFirstParam = false;
        }

        return urlBuilder.toString();
    }

    /**
     * 可能会请求失败，需要重试
     */
    public String sendRequest() throws Exception {
        String requestUrl = buildRequestUrl();
        log.debug("ESearchRequest  url is :{}", requestUrl);
        System.out.println(requestUrl);

        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            // 获取错误流
            InputStream errorStream = conn.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            StringBuilder errorResponse = new StringBuilder();

            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();
            String errorInfo = errorResponse.toString();
            log.error("sendRequest error:{}", errorInfo);

            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static void main(String[] args) {
        // 简单关键词搜索
        String a = null;
        try {
            a = URLEncoder.encode("neoadjuvant immunotherapy AND cancer stem cell efficacy", StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 布尔运算符
        String a1 = "lung cancer AND mesothelioma";
        // 短语搜索
        String a2 = "lung cancer treatment";
        // 字段限制
        String a3 = "cancer[title]";
        String a4 = "milk";
        try {
            String r = new ESearchRequest(a).setRetMode("JSON").setSort("relevance").sendRequest();
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
