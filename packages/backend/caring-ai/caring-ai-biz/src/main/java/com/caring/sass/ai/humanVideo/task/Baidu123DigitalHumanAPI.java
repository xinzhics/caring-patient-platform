package com.caring.sass.ai.humanVideo.task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.humanVideo.config.HumanVideoConfig;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Baidu123DigitalHumanAPI {


    @Autowired
    HumanVideoConfig humanVideoConfig;

    private static final Logger logger = Logger.getLogger(Baidu123DigitalHumanAPI.class.getName());
    private final String baseUrl = "https://open.xiling.baidu.com";
    private final CloseableHttpClient httpClient;

    public Baidu123DigitalHumanAPI() {
        this.httpClient = HttpClients.custom()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(10)
                .build();
    }


    private String generateSignature() {
        // 生成过期时间（1小时后）
        String appId = humanVideoConfig.getBaidu123AppId(); //填真实的appid
        String appKey = humanVideoConfig.getBaidu123AppSecret(); //填真实的appkey
        // expiredTime eg: 2020-10-28T19:40:58.963441+08:00
        String expiredTime = ZonedDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        // token eg: i-khpg99yk2j3gk/7dcefadebec0ee51f312ef1344d1d1cda9db4e35de7279ac7438823b5db96887/2020-10-28T19:40:58.963441+08:00
        return appId + "/" + hmacSha256(appKey, appId + expiredTime) + "/" + expiredTime;

    }

    private static String hmacSha256(String key, String data) {
        HmacUtils hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key);
        return hmac.hmacHex(data);
    }


    private Map<String, String> generateHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf-8");
        headers.put("Authorization", generateSignature());
        return headers;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }

        bos.close();
        return bos.toByteArray();
    }

    public static File downLoadFromFile(String u, String fileName, String dir) throws IOException {
        FileOutputStream fos = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(3000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            inputStream = conn.getInputStream();
            byte[] getData = readInputStream(inputStream);

            File saveDir = new File(dir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String fileType = u.substring(u.lastIndexOf(".") + 1);
            if (fileType.equals("null")) {
                fileType = "mp4";
            }
            String path = new StringBuilder().append(dir).append(fileName).append(".").append(fileType).toString();
            File file = new File(path);
            fos = new FileOutputStream(file);
            fos.write(getData);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }

    /**
     * 下载模版视频到本地
     */
    public File downLoadVideoFile(String filePath) {

        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/business/card/video";
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        UUID fastUUID = UUID.fastUUID();
        String fileName = fastUUID.toString().replace("-", "");
        File file = null;
        try {
            file = downLoadFromFile(filePath, fileName, dir + path);
        } catch (IOException e) {
            log.error("download file error: {}", e.getMessage());
            return null;
        }
        if (Objects.isNull(file)) {
            log.error("download file error: file not exist");
            return null;
        }
        return file;
    }


    public JsonObject submitVideoTask(String templateVideoId, String driveType,
                                      String inputAudioUrl, Map<String, Integer> videoParams, String callbackUrl) {
        try {
            if ((templateVideoId == null || templateVideoId.isEmpty())) {
                throw new IllegalArgumentException("templateVideoId必须至少提供一个");
            }
            if (videoParams == null || !videoParams.containsKey("width") || !videoParams.containsKey("height")) {
                throw new IllegalArgumentException("videoParams必须包含width和height参数");
            }
             if ("VOICE".equals(driveType)) {
                if (inputAudioUrl == null || inputAudioUrl.isEmpty()) {
                    throw new IllegalArgumentException("当driveType为VOICE时，inputAudioUrl为必填项");
                }
            } else {
                throw new IllegalArgumentException("driveType必须是TEXT或VOICE");
            }

            String endpoint = "/api/digitalhuman/open/v1/video/submit/fast";
            String url = baseUrl + endpoint;

            Map<String, Object> payload = new HashMap<>();
            payload.put("driveType", driveType);
            payload.put("videoParams", videoParams);
            payload.put("templateVideoId", templateVideoId);
            payload.put("inputAudioUrl", inputAudioUrl);
            if (callbackUrl != null && !callbackUrl.isEmpty()) {
                payload.put("callbackUrl", callbackUrl);
            }

            Map<String, String> headers = generateHeaders();
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(payload);

            logger.info("提交视频合成任务请求信息:");
            logger.info("URL: " + url);
            logger.info("Headers: " + headers);
            logger.info("Payload: " + jsonPayload);

            HttpPost request = new HttpPost(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
            request.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                logger.info("响应状态码: " + statusCode);
                logger.info("响应内容: " + responseString);

                return gson.fromJson(responseString, JsonObject.class);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "提交视频合成任务失败", e);
            JsonObject error = new JsonObject();
            error.addProperty("code", -1);
            error.addProperty("message", e.getMessage());
            return error;
        }
    }

    public static void main(String[] args) {
        Baidu123DigitalHumanAPI humanAPI = new Baidu123DigitalHumanAPI();
        humanAPI.humanVideoConfig = new HumanVideoConfig();
        humanAPI.humanVideoConfig.setBaidu123AppId(System.getenv().getOrDefault("BAIDU_123_APP_ID", ""));
        humanAPI.humanVideoConfig.setBaidu123AppSecret(System.getenv().getOrDefault("BAIDU_123_APP_SECRET", ""));
        JsonObject jsonObject = humanAPI.queryTaskStatus("vf3-rm5q3dxntrfz4h8v");
        System.out.println(jsonObject);
    }

    public JsonObject queryTaskStatus(String taskId) {
        try {
            String endpoint = "/api/digitalhuman/open/v1/video/task";
            String url = baseUrl + endpoint + "?taskId=" + taskId;

            Map<String, String> headers = generateHeaders();

            logger.info("查询任务状态请求信息:");
            logger.info("URL: " + url);
            logger.info("Headers: " + headers);

            HttpGet request = new HttpGet(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                logger.info("响应状态码: " + statusCode);
                logger.info("响应内容: " + responseString);

                return new Gson().fromJson(responseString, JsonObject.class);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "查询任务状态失败", e);
            JsonObject error = new JsonObject();
            error.addProperty("code", -1);
            error.addProperty("message", e.getMessage());
            return error;
        }
    }

    /**
     * 上传视频到 百度云 的obs
     * @param providerType
     * @return
     */
    public JsonObject uploadFile(File file, String providerType) {
        try {
            if (!file.exists()) {
                throw new IllegalArgumentException("文件不存在: ");
            }

            String endpoint = "/api/digitalhuman/open/v1/file/upload";
            String url = baseUrl + endpoint;

            Map<String, String> headers = generateHeaders();
            headers.remove("Content-Type");

            String sourceFileName = file.getName();

            logger.info("上传文件请求信息:");
            logger.info("URL: " + url);
            logger.info("Headers: " + headers);
            logger.info("Provider Type: " + providerType);
            logger.info("Source File Name: " + sourceFileName);

            HttpPost request = new HttpPost(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, sourceFileName);
            builder.addTextBody("providerType", providerType);
            builder.addTextBody("sourceFileName", sourceFileName);
            HttpEntity multipart = builder.build();
            request.setEntity(multipart);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                logger.info("响应状态码: " + statusCode);
                logger.info("响应内容: " + responseString);

                return new Gson().fromJson(responseString, JsonObject.class);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "上传文件失败", e);
            JsonObject error = new JsonObject();
            error.addProperty("code", -1);
            error.addProperty("message", e.getMessage());
            return error;
        }
    }

    /**
     * 根据百度的文档要求。这里取消下载视频后，在上传百度操作。
     * 直接提供给百度 底板视频的链接，降低系统的宽带占用
     * @param videoUrl
     * @param audioUrl
     * @param width
     * @param height
     * @return
     */
    public JSONObject callBaiduApi(String videoUrl, String audioUrl, Integer width, Integer height) {

        // 下载视频到本地
        JSONObject result = new JSONObject();

        Map<String, Integer> dimensions = new HashMap<>();
        dimensions.put("width", width);
        dimensions.put("height", height);

        String apiUrl = ApplicationDomainUtil.apiUrl();

        JsonObject submitResult = submitVideoTask(
                videoUrl,
                "VOICE",
                audioUrl,
                dimensions,
                apiUrl+ "/api/ai/businessDigitalHumanVideoTask/anno/callback"
        );
        if (submitResult.get("code").getAsInt() != 0) {
            result.put("code", submitResult.get("code").getAsInt());
            result.put("message", submitResult.get("message").toString());
            return result;
        }

        String taskId = submitResult.getAsJsonObject("result").get("taskId").getAsString();
        String templateId = submitResult.getAsJsonObject("result").get("templateId").getAsString();
        logger.info("任务提交成功，任务ID: " + taskId + ", 底板ID: " + templateId);
        result.put("code", submitResult.get("code").getAsInt());
        result.put("taskId", taskId);
        result.put("templateId", templateId);
        return result;
    }
}