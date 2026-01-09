package com.caring.sass.ai.humanVideo.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.humanVideo.config.HumanVideoConfig;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaiduDigitalHumanAPI {
    private static final Logger logger = LoggerFactory.getLogger(BaiduDigitalHumanAPI.class);
    private static final String BASE_URL = "https://open.xiling.baidu.com";
    private final CloseableHttpClient httpClient;

    @Autowired
    HumanVideoConfig humanVideoConfig;

    public BaiduDigitalHumanAPI() {
        this.httpClient = createHttpClient();
        humanVideoConfig = new HumanVideoConfig();
    }

    private CloseableHttpClient createHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(10);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30 * 1000)
                .setSocketTimeout(60 * 1000)
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private String generateHeaders() {
        // 生成过期时间（1小时后）
        String appId = humanVideoConfig.getBaidu123AppId(); //填真实的appid
        String appKey = humanVideoConfig.getBaidu123AppSecret(); //填真实的appkey
        String expiredTime = ZonedDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return appId + "/" + hmacSha256(appKey, appId + expiredTime) + "/" + expiredTime;

    }



    private static String hmacSha256(String key, String data) {
        HmacUtils hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key);
        return hmac.hmacHex(data);
    }


    /**
     * 查询任务状态
     * @param taskId
     * @return
     */
    public JsonObject queryTaskStatus(String taskId) {
        try {
            String endpoint = "/api/digitalhuman/open/v1/video/image/task";
            String url = BASE_URL + endpoint + "?taskId=" + taskId;

            String authorization = generateHeaders();

            logger.info("查询任务状态请求信息:");
            logger.info("URL: " + url);
            logger.info("authorization: " + authorization);

            HttpGet request = new HttpGet(url);
            request.setHeader("Content-Type", "application/json;charset=utf-8");
            request.setHeader("Authorization", authorization);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                logger.info("响应状态码: " + statusCode);
                logger.info("响应内容: " + responseString);

                return new Gson().fromJson(responseString, JsonObject.class);
            }
        } catch (IOException e) {
            JsonObject error = new JsonObject();
            error.addProperty("code", -1);
            error.addProperty("message", e.getMessage());
            return error;
        }
    }


    /**
     * 提交视频合成任务
     *
     * @param inputImageUrl 人像图片 url，url 字符长度不超过 1000，图片需要包含清晰的人脸
     * @param driveType 驱动数字人的数据类型，枚举值，默认 TEXT。
     * 1.TEXT：文本驱动，系统会调用 TTS 合成音频后驱动数字人
     * 2.VOICE: 音频驱动，使用输入音频驱动数字人
     *
     * @param inputAudioUrl 驱动数字人播报的音频 url，url 字符长度不超过 1000
     * @param callbackUrl  接口调用方接受任务回调通知的url
     * @return
     */
    public JSONObject submitVideoTask(String inputImageUrl, String driveType,
                                      String inputAudioUrl, String callbackUrl) {
        try {
            String endpoint = "/api/digitalhuman/open/v1/video/image/submit";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("inputImageUrl", inputImageUrl);
            jsonObject.put("driveType", driveType);

            if ("VOICE".equals(driveType)) {
                jsonObject.put("inputAudioUrl", inputAudioUrl);
            }

            jsonObject.put("callbackUrl", callbackUrl);

            String headers = generateHeaders();
            String response = postRequest(BASE_URL + endpoint, jsonObject, headers);

            JSONObject jsonObject1 = JSONObject.parseObject(response);
            logger.info("提交视频合成任务成功: {}", jsonObject1);
            return jsonObject1;
        } catch (Exception e) {
            logger.error("提交视频合成任务失败: {}", e.getMessage(), e);
            JSONObject errorResult = new JSONObject();
            errorResult.put("code", -1);
            errorResult.put("message", e.getMessage());
            return errorResult;
        }
    }


    public JSONObject verifyImage(String inputImageUrl) {
        try {
            String endpoint = "/api/digitalhuman/open/v1/video/image/verify";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("inputImageUrl", inputImageUrl);
            String headers = generateHeaders();
            String response = postRequest(BASE_URL + endpoint, jsonObject, headers);

            JSONObject jsonObject1 = JSONObject.parseObject(response);
            logger.info("人像图片校验成功: {}", jsonObject1);
            return jsonObject1;
        } catch (Exception e) {
            logger.error("人像图片校验失败: {}", e.getMessage(), e);
            JSONObject errorResult = new JSONObject();
            errorResult.put("code", -1);
            errorResult.put("message", e.getMessage());
            return errorResult;
        }
    }

    private String postRequest(String url, JSONObject jsonObject, String authorization) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        httpPost.setHeader("Authorization", authorization);
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(jsonObject);
        httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));
        System.out.println(JSONObject.toJSON(httpPost).toString());
        System.out.println(JSONObject.toJSON(new StringEntity(jsonPayload, StandardCharsets.UTF_8)).toString());
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        }
        return null;
    }


    public JSONObject callBaidu(String inputImageUrl, String audioUrl) {

        JSONObject object = verifyImage(inputImageUrl);

        if (!object.get("code").toString().equals("0")) {
            return object;
        }
        logger.info("开始提交视频合成任务...");
        Map<String, Object> taskParams = new HashMap<>();
        taskParams.put("inputImageUrl", inputImageUrl);
        taskParams.put("driveType", "VOICE");
        taskParams.put("inputAudioUrl", audioUrl);

        String apiUrl = ApplicationDomainUtil.apiUrl();

        JSONObject videoTask = submitVideoTask(inputImageUrl, "VOICE", audioUrl,
                apiUrl + "/api/ai/businessDigitalHumanVideoTask/anno/callback");
        Integer code = videoTask.getInteger("code");
        if (code.equals(0)) {
            JSONObject result = videoTask.getJSONObject("result");
            result.put("code", 0);
            return result;
        } else {
            return videoTask;
        }

    }

}