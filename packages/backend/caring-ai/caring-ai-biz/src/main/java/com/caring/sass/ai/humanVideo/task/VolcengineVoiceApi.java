package com.caring.sass.ai.humanVideo.task;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.humanVideo.config.HumanVideoConfig;
import com.caring.sass.common.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 本API提供调用火山 声音复刻的代码
 * 1. 创建音色， 训练音色
 *
 * 2. 检查音色状态
 */
@Slf4j
@Component
public class VolcengineVoiceApi {

    @Autowired
    HumanVideoConfig humanVideoConfig;

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    private final String baseUrl = "https://openspeech.bytedance.com";


    private final CloseableHttpClient httpClient ;

    public VolcengineVoiceApi() {
        this.httpClient = HttpClients.custom()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(10)
                .build();
    }

    private Map<String, String> generateHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer;" + humanVideoConfig.getSpeakerAccessToken());
        headers.put("Resource-Id", "volc.megatts.voiceclone");
        return headers;
    }



    private synchronized String getRedisKey() {

        LocalDateTime now = LocalDateTime.now();
        now.withNano(0);
        String key = "volcengine_mergeFace_" + now;
        redisTemplate.opsForValue().setIfAbsent(key, (Long.MAX_VALUE - 9) + "", 2, TimeUnit.SECONDS);
        return key;
    }

    /**
     * 每秒去尝试获取一个请求face接口的机会。
     * 由于 face 并发为3 。秒只能有3个请求。
     * 使用redis。先根据当前时间的 时分秒 去获取一个key
     * 然后使用key 对 value + 1
     * 由于value 最大为Long.MAX_VALUE。
     * 超过最大值，则表示当前秒已经没有可用的请求了。
     * 等待下一秒在请求。
     */
    private void whenRedisValueIncrSuccess() {
        String redisKey = getRedisKey();
        try {
            redisTemplate.opsForValue().increment(redisKey);
        } catch (Exception e) {
            try {
                Thread.sleep(1000);
                whenRedisValueIncrSuccess();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    /**
     * 下载音频到本地
     * 将音频转成base64 编码
     */
    private String[] encodeAudioFile(String filePath) {

        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/business/card/audio";
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        UUID fastUUID = UUID.fastUUID();
        String fileName = fastUUID.toString().replace("-", "");
        File file = null;
        try {
            file = FileUtils.downLoadFromFile(filePath, fileName, dir + path);
        } catch (IOException e) {
            log.error("download file error: {}", e.getMessage());
            return null;
        }
        if (Objects.isNull(file)) {
            log.error("download file error: file not exist");
            return null;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            String encodedData = Base64.getEncoder().encodeToString(bytes);
            String audioFormat = filePath.substring(filePath.lastIndexOf('.') + 1);
            return new String[]{encodedData, audioFormat};
        } catch (IOException e) {
            log.error("音频文件编码失败", e);
            return null;
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }


    /**
     * 上传音频文件并开始训练
     *
     * @param audioPath 音频文件路径
     * @param spkId     声音ID
     * @return API响应的JsonObject
     */
    public String train(String audioPath, String spkId) {

        whenRedisValueIncrSuccess();

        String url = baseUrl + "/api/v1/mega_tts/audio/upload";
        Map<String, String> headers = generateHeaders();
        String[] encodedData = encodeAudioFile(audioPath);
        if (encodedData == null) {
            return "音频下载处理失败";
        }
        Map<String, Object> audio = new HashMap<>();
        audio.put("audio_bytes", encodedData[0]);
        audio.put("audio_format", encodedData[1]);
        Map<String, Object> data = new HashMap<>();
        data.put("appid", humanVideoConfig.getSpeakerAppId());
        data.put("speaker_id", spkId);
        data.put("audios", new Object[]{audio});
        data.put("source", 2);
        data.put("language", 0);
        data.put("model_type", 1);

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(data);

        try {
            HttpPost request = new HttpPost(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
            request.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.info("status code = " + statusCode);
                log.info("headers = " + response.getAllHeaders());
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info(responseString);
                JSONObject object = gson.fromJson(responseString, JSONObject.class);
                JSONObject baseResp = object.getJSONObject("BaseResp");
                int status = (int) Double.parseDouble(baseResp.get("StatusCode").toString());
                if (0 == status) {
                    return "SUCCESS";
                } else {
                    return baseResp.toString();
                }
            }
        } catch (IOException e) {
            log.error("HTTP请求失败", e);
            JsonObject error = new JsonObject();
            error.addProperty("code", -1);
            error.addProperty("message", e.getMessage());
            return error.toString();
        }
    }

    /**
     * 查询训练状态
     *
     * @param spkId 声音ID
     * @return API响应的JsonObject
     */
    public JSONObject getStatus(String spkId) {


        whenRedisValueIncrSuccess();


        String url = baseUrl + "/api/v1/mega_tts/status";
        Map<String, String> headers = generateHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("appid", humanVideoConfig.getSpeakerAppId());
        body.put("speaker_id", spkId);

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(body);

        try {
            HttpPost request = new HttpPost(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
            request.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.info("status code = " + statusCode);
                if (statusCode != 200) {
                    throw new RuntimeException("getStatus请求错误: " + EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
                }
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info(responseString);
                return gson.fromJson(responseString, JSONObject.class);
            }
        } catch (IOException e) {
            log.error("HTTP请求失败", e);
            JSONObject error = new JSONObject();
            error.put("code", -1);
            error.put("message", e.getMessage());
            return error;
        }
    }


    /**
     * 语音合成
     * @param userId
     * @param spkId
     * @param textContent
     * @return
     */
    public JSONObject audioSynthesis(Long userId, String spkId, String textContent) {

        whenRedisValueIncrSuccess();


        TtsRequest ttsRequest = new TtsRequest(textContent, humanVideoConfig.getSpeakerAppId(),
                humanVideoConfig.getSpeakerAccessToken(), "volcano_icl", userId.toString(), spkId);
        String jsonString = JSON.toJSONString(ttsRequest);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonString);
        Request request = new Request.Builder()
                .url(baseUrl + "/api/v1/tts")
                .post(body)
                .header("Authorization", "Bearer; " + humanVideoConfig.getSpeakerAccessToken())
                .build();

        // 返回值  {
        //    "reqid": "reqid",
        //    "code": 3000,
        //    "operation": "query",
        //    "message": "Success",
        //    "sequence": -1,
        //    "data": "base64 encoded binary data",
        //    "addition": {
        //        "duration": "1960"
        //    }
        //}
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseBodyString = responseBody.string();
                log.info("Response Body: " + responseBodyString);
                return JSONObject.parseObject(responseBodyString);
            } else {
                System.out.println("Response Body is null");
                return null;
            }

        } catch (IOException e) {
            log.error("audioSynthesis error {}", e.getMessage());
        }

        return null;

    }





}
