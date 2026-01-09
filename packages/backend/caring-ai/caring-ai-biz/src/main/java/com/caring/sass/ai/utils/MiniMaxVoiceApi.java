package com.caring.sass.ai.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.caring.sass.ai.article.service.ArticleUserVoiceService;
import com.caring.sass.ai.config.MiniMaxVoiceConfig;
import com.caring.sass.ai.entity.article.ArticleUserVoice;
import com.caring.sass.ai.utils.miniMax.*;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.utils.SpringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MiniMaxVoiceApi {

    private static final String API_URL_UPLOAD = "https://api.minimax.chat/v1/files/upload";
    private static final String API_URL_VOICE_CLONE = "https://api.minimax.chat/v1/voice_clone";
    private static final String API_URL_DELETE_VOICE = "https://api.minimax.chat/v1/delete_voice";
    private static final String API_URL_T2A_V2 = "https://api.minimax.chat/v1/t2a_v2";
    private static final String API_URL_GET_VOICE = "https://api.minimax.chat/v1/get_voice";
////    private static final String GROUP_ID = "1893892385562169986";
//    private static final String GROUP_ID = "1893138295433479100";
////    private static final String API_KEY = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiJsZWl6IiwiVXNlck5hbWUiOiJ2b2ljZSIsIkFjY291bnQiOiJ2b2ljZUAxODkzODkyMzg1NTYyMTY5OTg2IiwiU3ViamVjdElEIjoiMTg5MTQ2MDczMDUyMDA4MDQyMiIsIlBob25lIjoiIiwiR3JvdXBJRCI6IjE4OTM4OTIzODU1NjIxNjk5ODYiLCJQYWdlTmFtZSI6IiIsIk1haWwiOiIiLCJDcmVhdGVUaW1lIjoiMjAyNS0wMi0yNSAxNzo0NTowNiIsIlRva2VuVHlwZSI6MSwiaXNzIjoibWluaW1heCJ9.Vx7gX3R_F-piUUr49PZ7lUkHHlBoHxxkdfJHQhKWZ5SQP7EZBz2aAnjDgQpDoLTFyLPMvfLPdw-bp1BRAAyTIS2Pjsx9QQCr6cEIxSPKxh5n7dUtciueSsQ4Z4ZPG6c-2o-NZjY1J5BEiXHCQ1efbKuIEWzxNMBmYO-u9zYgb7RecpjK3JlrFiUWW5_7YxvRBMS5QZwz3IGqww7jV2yeeyBLKxvpjWwpoyPe5dprOERc6MbjdVNTjwto_OIShKoQtkNKV2ZQZ6sVAnjj9O0mPOc7rPeN0ELdb6qsawbg1N3PxV2zP66AX-xMy8_JkH4aBE0r3sW5Y6RNx0hN9z3FSA";
//    private static final String API_KEY = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiLkuIrmtbfljaHmn6Dkv6Hmga_np5HmioDmnInpmZDlhazlj7giLCJVc2VyTmFtZSI6Iumbt-aZuiIsIkFjY291bnQiOiIiLCJTdWJqZWN0SUQiOiIxODkzMTM4Mjk1NDQxODY3NzA4IiwiUGhvbmUiOiIxNzM3NTc0NzQwNSIsIkdyb3VwSUQiOiIxODkzMTM4Mjk1NDMzNDc5MTAwIiwiUGFnZU5hbWUiOiIiLCJNYWlsIjoiIiwiQ3JlYXRlVGltZSI6IjIwMjUtMDQtMDMgMDk6MjA6MzkiLCJUb2tlblR5cGUiOjEsImlzcyI6Im1pbmltYXgifQ.UxIH-6XNCxwIqjJUYdnwhlcnIWJlsmOse7Wb0_uEMh6YnrSBP8gZ6mXYgAkdSoJPDXidcimgXR5QLpdQf-k3DKYYifF1NtcMPtqAY5xl_D8ySx7OYTsMEMlk3RvT6ZstRdUdSXHgvvra8gicy_eczEFEXbSzJdPTK083XN9n6rmIOwFXRQE4J_R5V4Cgy7uqdb5Pr34pWBQKx1Msvw8XBy-4ES5S1-k9IqaU2szZ1gNL8w61aM_ZE7shOvi7f5T4ALESAtayTs5up4XWqHnCotg2-YUR7CSOvZ69szio3ZXJ21ZAcEuqX1Nk_rGAw2-DOuerGGrwjD5tLHmxP5rf6Q";


    // 从配置文件读取，不再硬编码
    // private static final String GROUP_ID_KAILING = "1893138295433479100";
    // private static final String GROUP_ID_OTHER = "1893892385562169986";
    // private static final String API_KEY_KAILING = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiLkuIrmtbfljaHmn6Dkv6Hmga_np5HmioDmnInpmZDlhazlj7giLCJVc2VyTmFtZSI6Iumbt-aZuiIsIkFjY291bnQiOiIiLCJTdWJqZWN0SUQiOiIxODkzMTM4Mjk1NDQxODY3NzA4IiwiUGhvbmUiOiIxNzM3NTc0NzQwNSIsIkdyb3VwSUQiOiIxODkzMTM4Mjk1NDMzNDc5MTAwIiwiUGFnZU5hbWUiOiIiLCJNYWlsIjoiIiwiQ3JlYXRlVGltZSI6IjIwMjUtMDQtMDMgMDk6MjA6MzkiLCJUb2tlblR5cGUiOjEsImlzcyI6Im1pbmltYXgifQ.UxIH-6XNCxwIqjJUYdnwhlcnIWJlsmOse7Wb0_uEMh6YnrSBP8gZ6mXYgAkdSoJPDXidcimgXR5QLpdQf-k3DKYYifF1NtcMPtqAY5xl_D8ySx7OYTsMEMlk3RvT6ZstRdUdSXHgvvra8gicy_eczEFEXbSzJdPTK083XN9n6rmIOwFXRQE4J_R5V4Cgy7uqdb5Pr34pWBQKx1Msvw8XBy-4ES5S1-k9IqaU2szZ1gNL8w61aM_ZE7shOvi7f5T4ALESAtayTs5up4XWqHnCotg2-YUR7CSOvZ69szio3ZXJ21ZAcEuqX1Nk_rGAw2-DOuerGGrwjD5tLHmxP5rf6Q";
    // private static final String API_KEY_OTHER = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiJsZWl6IiwiVXNlck5hbWUiOiJ2b2ljZSIsIkFjY291bnQiOiJ2b2ljZUAxODkzODkyMzg1NTYyMTY5OTg2IiwiU3ViamVjdElEIjoiMTg5MTQ2MDczMDUyMDA4MDQyMiIsIlBob25lIjoiIiwiR3JvdXBJRCI6IjE4OTM4OTIzODU1NjIxNjk5ODYiLCJQYWdlTmFtZSI6IiIsIk1haWwiOiIiLCJDcmVhdGVUaW1lIjoiMjAyNS0wMi0yNSAxNzo0NTowNiIsIlRva2VuVHlwZSI6MSwiaXNzIjoibWluaW1heCJ9.Vx7gX3R_F-piUUr49PZ7lUkHHlBoHxxkdfJHQhKWZ5SQP7EZBz2aAnjDgQpDoLTFyLPMvfLPdw-bp1BRAAyTIS2Pjsx9QQCr6cEIxSPKxh5n7dUtciueSsQ4Z4ZPG6c-2o-NZjY1J5BEiXHCQ1efbKuIEWzxNMBmYO-u9zYgb7RecpjK3JlrFiUWW5_7YxvRBMS5QZwz3IGqww7jV2yeeyBLKxvpjWwpoyPe5dprOERc6MbjdVNTjwto_OIShKoQtkNKV2ZQZ6sVAnjj9O0mPOc7rPeN0ELdb6qsawbg1N3PxV2zP66AX-xMy8_JkH4aBE0r3sW5Y6RNx0hN9z3FSA";

    private static final String MINI_MAX_VOICE_API_KEY = "MiniMaxVoiceApiKey";

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    MiniMaxVoiceConfig miniMaxVoiceConfig;


    /**
     * 海螺限制 一分钟只有20
     *
     */
    public void checkQps() {
        Integer maxRpm = miniMaxVoiceConfig.getMaxRpm();
        while (true) {
            LocalDateTime now = LocalDateTime.now();
            now = now.withNano(0).withSecond(0);
            String key = MINI_MAX_VOICE_API_KEY + now;
            redisTemplate.opsForValue().setIfAbsent(key, (Long.MAX_VALUE - maxRpm) + "", 60, TimeUnit.SECONDS);
            try {
                redisTemplate.opsForValue().increment(key);
                break;
            } catch (Exception e) {
                log.error("MiniMaxVoiceApi No Rpm available, waiting for 2 seconds...", e);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    log.error("MiniMaxVoiceApi Thread interrupted while waiting for Rpm: {}", ex.getMessage());
                }
            }
        }

    }



    private String getGroupId(String account) {
        if ("kailing".equals(account)) {
            return miniMaxVoiceConfig.getGroupIdKailing();
        } else {
            return miniMaxVoiceConfig.getGroupIdOther();
        }
    }

    private String getApiKey(String account) {
        if ("kailing".equals(account)) {
            return miniMaxVoiceConfig.getApiKeyKailing();
        } else {
            return miniMaxVoiceConfig.getApiKeyOther();
        }
    }

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    /**
     * 上传文件到MiniMax API。
     *
     * @param file 要上传的文件。
     * @param purpose 文件上传的目的。
     * @return API返回的文件ID。
     * @throws IOException 如果上传过程中发生错误。
     */
    public Long uploadFile(File file, String purpose,  String account) throws IOException {
        checkQps();
        MediaType mediaType = MediaType.parse("audio/mpeg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(mediaType, file))
                .addFormDataPart("purpose", purpose)
                .build();

        Request request = new Request.Builder()
                .url(API_URL_UPLOAD + "?GroupId=" + getGroupId(account))
                .header("Authorization", "Bearer " + getApiKey(account))
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 确保返回的是JSON对象
            assert response.body() != null;
            String responseBody = response.body().string();
            Map<String, Object> responseMap = JSON.parseObject(responseBody, new TypeReference<Map<String, Object>>() {});
            log.debug("responseMap {}", responseMap);
            return (Long) ((Map<String, Object>) responseMap.get("file")).get("file_id");
        }
    }

    /**
     * 重载方法，从云访问URL上传文件到MiniMax API。
     *
     * @param fileUrl 文件的URL。
     * @param purpose 文件上传的目的。
     * @return API返回的文件ID。
     * @throws IOException 如果上传过程中发生错误。
     */
    public Long uploadFile(String fileUrl, String purpose, String account) throws IOException {
        checkQps();

        Request request = new Request.Builder()
                .url(fileUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            InputStream inputStream = response.body().byteStream();
            File tempFile;
            if (fileUrl.endsWith(".mp3") || fileUrl.endsWith(".MP3")) {
                tempFile = File.createTempFile("temp", ".mp3");
            } else if (fileUrl.endsWith(".wav") || fileUrl.endsWith(".WAV")) {
                tempFile = File.createTempFile("temp", ".wav");
            } else if (fileUrl.endsWith(".m4a") || fileUrl.endsWith(".M4A")) {
                tempFile = File.createTempFile("temp", ".m4a");
            } else {
                throw new IOException("Unsupported file type");
            }
            try {
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return uploadFile(tempFile, purpose, account);
            } finally {
                tempFile.delete();
            }
        }
    }

    /**
     * 使用MiniMax API克隆语音。
     *
     * @param fileId 要克隆的文件ID。
     * @param voiceId 要克隆的语音ID。
     * @param text 试听文本。
     * @return 语音克隆的结果。
     * @throws IOException 如果克隆过程中发生错误。
     */
    public VoiceCloneResponse cloneVoice(Long fileId, String voiceId, String text, String account) throws IOException {
        checkQps();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Map<String, Object> payload = new HashMap<>();
        payload.put("file_id", fileId);
        payload.put("voice_id", voiceId);
        if (StrUtil.isNotEmpty(text)) {
            payload.put("text", text);
            payload.put("model", "speech-01-turbo");
            payload.put("need_noise_reduction", true);
        }

        // 使用 com.alibaba.fastjson.JSON 的静态方法 toJSONString 序列化 payload
        String jsonPayload = JSON.toJSONString(payload);
        if (jsonPayload == null) {
            throw new IllegalArgumentException("Payload serialization resulted in null.");
        }

        RequestBody body = RequestBody.create(mediaType, jsonPayload);
        Request request = new Request.Builder()
                .url(API_URL_VOICE_CLONE + "?GroupId=" + getGroupId(account))
                .header("Authorization", "Bearer " + getApiKey(account))
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 确保返回的是JSON对象
            assert response.body() != null;
            String responseBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            log.debug("responseMap {}", responseBody);
            return objectMapper.readValue(responseBody, VoiceCloneResponse.class);
        }
    }

    /**
     * 查询可用音色ID。
     *
     * @param voiceType 音色类型，支持 "system", "voice_cloning", "voice_generation", "music_generation", "all"。
     * @return 查询结果。
     * @throws IOException 如果查询过程中发生错误。
     */
    public Map<String, Object> getVoice(String voiceType, String account) throws IOException {
        checkQps();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> payload = new HashMap<>();
        payload.put("voice_type", voiceType);

        String jsonPayload = JSON.toJSONString(payload);
        if (jsonPayload == null) {
            throw new IllegalArgumentException("Payload serialization resulted in null.");
        }

        RequestBody body = RequestBody.create(mediaType, jsonPayload);
        Request request = new Request.Builder()
                .url(API_URL_GET_VOICE + "?GroupId=" + getGroupId(account))
                .header("Authorization", "Bearer " + getApiKey(account))
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 确保返回的是JSON对象
            assert response.body() != null;
            String responseBody = response.body().string();
            Map<String, Object> responseMap = JSON.parseObject(responseBody, new TypeReference<Map<String, Object>>() {});
            log.debug("responseMap {}", responseMap);
            return responseMap;
        }
    }

    /**
     * 删除指定的文件。
     *
     * @param fileId 要删除的文件ID。
     * @return 删除操作的结果。
     * @throws IOException 如果删除过程中发生错误。
     */
    public Map<String, Object> deleteFile(Long fileId, String account) throws IOException {
        checkQps();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Map<String, Long> payload = new HashMap<>();
        payload.put("file_id", fileId);

        String jsonPayload = JSON.toJSONString(payload);
        if (jsonPayload == null) {
            throw new IllegalArgumentException("Payload serialization resulted in null.");
        }

        RequestBody body = RequestBody.create(mediaType, jsonPayload);
        Request request = new Request.Builder()
                .url(API_URL_UPLOAD + "?GroupId=" + getGroupId(account))
                .header("Authorization", "Bearer " + getApiKey(account))
                .delete(body) // 使用 DELETE 方法
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 确保返回的是JSON对象
            assert response.body() != null;
            String responseBody = response.body().string();
            Map<String, Object> responseMap = JSON.parseObject(responseBody, new TypeReference<Map<String, Object>>() {});
            log.debug("responseMap {}", responseMap);
            return responseMap;
        }
    }

    /**
     * 删除指定的语音。
     *
     * @param voiceType 语音类型，支持 "voice_cloning" 或 "voice_generation"。
     * @param voiceId 要删除的语音ID。
     * @return 删除操作的结果。
     * @throws IOException 如果删除过程中发生错误。
     */
    public Map<String, Object> deleteVoice(String voiceType, String voiceId, String account) throws IOException {
        checkQps();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> payload = new HashMap<>();
        payload.put("voice_type", voiceType);
        payload.put("voice_id", voiceId);

        String jsonPayload = JSON.toJSONString(payload);
        if (jsonPayload == null) {
            throw new IllegalArgumentException("Payload serialization resulted in null.");
        }

        RequestBody body = RequestBody.create(mediaType, jsonPayload);
        Request request = new Request.Builder()
                .url(API_URL_DELETE_VOICE + "?GroupId=" + getGroupId(account))
                .header("Authorization", "Bearer " + getApiKey(account))
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 确保返回的是JSON对象
            assert response.body() != null;
            String responseBody = response.body().string();
            Map<String, Object> responseMap = JSON.parseObject(responseBody, new TypeReference<Map<String, Object>>() {});
            log.debug("responseMap {}", responseMap);
            return responseMap;
        }
    }

    /**
     * T2A v2（语音生成）接口实现
     *
     * @param text 待合成的文本
     * @param voiceId 请求的音色id
     * @return 返回的语音生成结果
     * @throws IOException 如果请求过程中发生错误
     */
    public VoiceGenerationResponse generateVoice(
            String text, String voiceId) throws IOException{
        ArticleUserVoiceService bean = SpringUtils.getBean(ArticleUserVoiceService.class);
        ArticleUserVoice articleUserVoice = bean.getOne(Wraps.<ArticleUserVoice>lbQ().eq(ArticleUserVoice::getVoiceId, voiceId));
        if (articleUserVoice == null) {
            // 音色可能被重新克隆。查找redis中 旧音色id 重新克隆后的新音色id
            String newVoiceId = redisTemplate.opsForValue().get(voiceId);
            if (newVoiceId == null) {
                throw new BizException("音色不存在");
            } else {
                articleUserVoice = bean.getOne(Wraps.<ArticleUserVoice>lbQ().eq(ArticleUserVoice::getVoiceId, newVoiceId));
            }
        } else if (articleUserVoice.getMiniExpired() != null && articleUserVoice.getMiniExpired().equals(1)) {
            boolean codeCheck = bean.reStartClone(articleUserVoice);
            if (!codeCheck) {
                throw new BizException("海螺音色已过期，重新克隆音色失败");
            }
            // 重新获取一下 音色记录。 防止当前记录的音色是旧的
            articleUserVoice = bean.getById(articleUserVoice.getId());

        }

        articleUserVoice.setUseCount(articleUserVoice.getUseCount() + 1);
        bean.updateById(articleUserVoice);

        checkQps();

        voiceId = articleUserVoice.getVoiceId();
        String account = articleUserVoice.getAccount();
        if (account == null) {
            account = "kailing";
        } else {
            account = articleUserVoice.getAccount();
        }
        String model = "speech-01-turbo";
        VoiceSetting voiceSetting = new VoiceSetting();
        voiceSetting.setVoiceId(voiceId);
        voiceSetting.setSpeed(1);
        voiceSetting.setVolume(1);
        voiceSetting.setPitch(0);
        return generateVoice(text, model, voiceSetting, null, null, null, false, null, false, account);
    }


    /**
     * T2A v2（语音生成）接口实现
     *
     * @param text 待合成的文本
     * @param model 模型版本
     * @param voiceSetting 语音设置
     * @param audioSetting 音频设置
     * @param pronunciationDict 发音字典
     * @param timberWeights 混音权重（与 voice_id 二选一必填）
     * @param stream 是否流式输出
     * @param languageBoost 语言增强参数
     * @param subtitleEnable 是否开启字幕服务
     * @return 返回的语音生成结果
     * @throws IOException 如果请求过程中发生错误
     */
    private VoiceGenerationResponse generateVoice(
            String text, String model, VoiceSetting voiceSetting, AudioSetting audioSetting,
            PronunciationDict pronunciationDict, Map<String, Double> timberWeights,
            boolean stream, String languageBoost, boolean subtitleEnable, String account) throws IOException {

        // 构建请求体
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("text", text);
        payload.put("stream", stream);
        payload.put("language_boost", languageBoost);
        payload.put("subtitle_enable", subtitleEnable);

        if (voiceSetting != null) {
            payload.put("voice_setting", voiceSetting.toMap());
        }

        if (audioSetting != null) {
            payload.put("audio_setting", audioSetting.toMap());
        }

        if (pronunciationDict != null) {
            payload.put("pronunciation_dict", pronunciationDict.toMap());
        }

        if (timberWeights != null) {
            payload.put("timber_weights", timberWeights);
        }

        // 转换为 JSON 格式
        String jsonPayload = JSON.toJSONString(payload);

        // 构建请求
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonPayload);
        Request request = new Request.Builder()
                .url(API_URL_T2A_V2 + "?GroupId=" + getGroupId(account)) // 替换为实际的 API URL
                .header("Authorization", "Bearer " + getApiKey(account))
                .post(body)
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code() + ": " + response.body().string());
            }

            // 解析响应
            String responseBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            log.debug("Response: {}", responseBody);

            return objectMapper.readValue(responseBody, VoiceGenerationResponse.class);
        }
    }


}