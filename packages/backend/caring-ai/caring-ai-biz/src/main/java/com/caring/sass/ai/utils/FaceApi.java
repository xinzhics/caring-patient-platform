package com.caring.sass.ai.utils;

import com.alibaba.fastjson.JSON;
import com.caring.sass.ai.config.FaceConfig;
import com.caring.sass.ai.dto.*;
import com.caring.sass.ai.entity.face.MegviiTemplateDiagram;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 人脸检测接口
 */
@Component
public class FaceApi {

    // 人脸检测接口地址
    final String API_DETECT_URL = "https://api-cn.faceplusplus.com/facepp/v3/detect";

    // 融合人脸接口地址
    final String API_MERGE_FACE_URL = "https://api-cn.faceplusplus.com/imagepp/v1/mergeface";
    private static final MediaType MULTIPART_FORM_DATA = MediaType.get("multipart/form-data");
    @Autowired
    FaceConfig faceConfig;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private synchronized String getRedisKey() {

        LocalDateTime now = LocalDateTime.now();
        now.withNano(0);
        String key = "mergeFace_" + now;
        redisTemplate.opsForValue().setIfAbsent(key, (Long.MAX_VALUE - 3) + "", 2, TimeUnit.SECONDS);
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
     * 调用人脸融合接口
     * @param templateDiagram   模板图
     * @param merge_base64  待融合图片base64编码
     * @return 融合结果
     */
    public FaceMergeResultDTO mergeFace(MegviiTemplateDiagram templateDiagram, String merge_base64) {
        // 获取模板图的人脸矩形框信息
        String faceRectangle = templateDiagram.getFaceRectangle();
        faceRectangleDTO rectangleDTO = JSON.parseObject(faceRectangle, faceRectangleDTO.class);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MULTIPART_FORM_DATA)
                .addFormDataPart("api_key", faceConfig.getApiKey())
                .addFormDataPart("api_secret", faceConfig.getApiSecret())
                .addFormDataPart("merge_base64", merge_base64)
                .addFormDataPart("template_base64", templateDiagram.getImageBase64())  // 设置模板图的base64编码
                .addFormDataPart("template_rectangle", rectangleDTO.getFaceRectangleInfo())  // 设置模板图的人脸矩形框信息到请求参数中
                .addFormDataPart("merge_rate", faceConfig.getMerge_rate() + "")  // 设置融合率
                .addFormDataPart("feature_rate", faceConfig.getFeature_rate() + "" )  // 设置特征点提取率
                .build();

        OkHttpClient client = getOkHttpClient();

        // 创建请求
        Request request = new Request.Builder()
                .url(API_MERGE_FACE_URL)
                .post(multipartBody)
                .build();

        whenRedisValueIncrSuccess();
        // 调用人脸融合接口
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // 获取响应体内容
            String responseBody = response.body() != null ? response.body().string() : null;

            // 解析响应
            if (responseBody == null) {
                throw new IOException("Empty response body");
            }

            // 解析响应体内容为FaceMergeResultDTO对象并返回
            return JSON.parseObject(responseBody, FaceMergeResultDTO.class);
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }

        // 返回null表示调用失败
        return null;
    }



    /**
     * 调用人脸检测接口
     * @param imageBase64  图片base64编码
     * @return  人脸检测结果
     */
    public FaceV3DeteactDTO detect(String imageBase64) {
        // 使用https请求。调用人脸检测接口。
        // api地址: https://api-cn.faceplusplus.com/facepp/v3/detect
        // 请求方式: POST
        // 请求体 multipart/form-data
        // 请求参数
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MULTIPART_FORM_DATA)
                .addFormDataPart("api_key", faceConfig.getApiKey())
                .addFormDataPart("api_secret", faceConfig.getApiSecret())
                .addFormDataPart("image_base64", imageBase64) // 假设这里应该有实际的Base64编码图像数据
                .build();
        OkHttpClient client = getOkHttpClient();
        // 创建请求
        Request request = new Request.Builder()
                .url(API_DETECT_URL)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body() != null ? response.body().string() : null;
            // 解析响应
            if (responseBody == null) {
                throw new IOException("Empty response body");
            }
            return JSON.parseObject(responseBody, FaceV3DeteactDTO.class);
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
        return null;

    }


}
