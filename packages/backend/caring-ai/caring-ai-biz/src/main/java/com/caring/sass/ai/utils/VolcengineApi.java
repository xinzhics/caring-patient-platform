package com.caring.sass.ai.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.config.FaceConfig;
import com.caring.sass.ai.signer.Credentials;
import com.caring.sass.ai.signer.Signer;
import java.net.URLDecoder;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class VolcengineApi {

    @Autowired
    FaceConfig faceConfig;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

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

    public static void main(String[] args) throws Exception {
        VolcengineApi api = new VolcengineApi();
        String faceImage;
        String templateImage;
        String source_similarity = "0.8";
        Float gpen = null;
        faceImage = "https://test-config.obs.cn-north-4.myhuaweicloud.com:443/0112%2F2024%2F06%2F9608d9ae-1bde-41a4-bcf3-3f8c7db9dfba.jpg";
        templateImage = "https://test-config.obs.cn-north-4.myhuaweicloud.com:443/MDAwMA==/2024/06/964d1586-5351-4ec1-acd4-54c5578858ea.jpg";
//        faceImage = URLDecoder.decode(faceImage, StandardCharsets.UTF_8.name());
//        templateImage = URLDecoder.decode(templateImage, StandardCharsets.UTF_8.name());
        String string1 = "face_swap3_3";
        try {
            String string = api.mergeImage(faceImage, templateImage, source_similarity, gpen, "face_swap3_3");
            System.out.println(string);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 调用火山引擎 进行图片融合
     * @param faceImage
     * @param templateImage
     * @param source_similarity
     * @param gpen
     * @return
     * @throws Exception
     */
    public String mergeImage(String faceImage, String templateImage, String source_similarity, Float gpen, String reqKey) throws Exception {
        /* create credentials */
        Credentials credentials = new Credentials();
        credentials.setAccessKeyID(System.getenv().getOrDefault("VOLCENGINE_ACCESS_KEY_ID", ""));
        credentials.setSecretAccessKey(System.getenv().getOrDefault("VOLCENGINE_SECRET_ACCESS_KEY", ""));
        credentials.setRegion(System.getenv().getOrDefault("VOLCENGINE_REGION", "cn-north-1"));
        credentials.setService("cv");

        System.out.println("faceImage: " + faceImage);   // 200
        System.out.println("templateImage: " + templateImage);   // 200

        // QPS 限制 10个
        whenRedisValueIncrSuccess();

        /* create signer */
        Signer signer = new Signer();

        /* create http client */
        CloseableHttpClient httpClient = HttpClients.createDefault();

        /* prepare request */
        HttpPost request = new HttpPost();
        request.setURI(new URI("https://visual.volcengineapi.com?Action=FaceSwap&Version=2022-08-31"));

        request.addHeader("Content-Type", "application/json");

        // 设置请求参数
        List<String> imageUrls = Arrays.asList(faceImage,
                templateImage); // 示例URL列表

        // 将参数转换为JSON格式
        Gson gson = new Gson();
        String requestBody = gson.toJson(new Body(reqKey, imageUrls, source_similarity, gpen));

        // 创建请求体
        HttpEntity entity = new StringEntity(requestBody, "UTF-8");
        request.setEntity(entity);
        signer.sign(request, credentials);

        /* launch request */
        CloseableHttpResponse response = httpClient.execute(request);

        /* status code */
        System.out.println(response.getStatusLine().getStatusCode());   // 200

        /* get response body */
        entity = response.getEntity();
        String dataBase64 = null;
        try {
            if (entity != null) {
                String result = EntityUtils.toString(entity);
//                System.out.println(result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                Object code = jsonObject.get("code");
                if (code.toString().equals("10000")) {
                    Object message = jsonObject.get("message");
                    Object status = jsonObject.get("status");
                    Object request_id = jsonObject.get("request_id");
                    log.info("VolcengineApi mergeImage result code {}, message: {}, status: {}, request_id: {} ", code.toString(), message.toString(), status.toString(), request_id.toString());
                    if (message.equals("Success")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        Object binary_data_base64 = data.get("binary_data_base64");
                        if (binary_data_base64 != null) {
                            JSONArray jsonArray = JSONArray.parseArray(binary_data_base64.toString());
                            Object object = jsonArray.get(0);
                            if (object != null) {
                                dataBase64 = object.toString();
                            }
                        }
                    }
                } else {
                    throw new Exception(code.toString());
                }
            }
        } finally {
            response.close();
            httpClient.close();
        }
        return dataBase64;

    }


    private static class Body {
        private String req_key;
        private List<String> image_urls;

        String source_similarity;
        Float gpen;

        public Body(String reqKey, List<String> imageUrls, String source_similarity, Float gpen) {
            this.req_key = reqKey;
            this.image_urls = imageUrls;
            this.source_similarity = source_similarity;
            this.gpen = gpen;
        }
    }


}
