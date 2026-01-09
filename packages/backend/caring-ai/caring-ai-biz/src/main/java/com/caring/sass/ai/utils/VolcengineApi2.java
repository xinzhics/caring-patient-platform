package com.caring.sass.ai.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.config.FaceConfig;
import com.caring.sass.ai.entity.card.BusinessCardDiagramResult;
import com.caring.sass.ai.signer.Credentials;
import com.caring.sass.ai.signer.Signer;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class VolcengineApi2 {

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



    private synchronized String getSingleImagePhotographyRedisKey() {

        LocalDateTime now = LocalDateTime.now();
        now.withNano(0);
        String key = "volcengine_singleimagephotography_" + now;
        redisTemplate.opsForValue().setIfAbsent(key, (Long.MAX_VALUE - 2) + "", 2, TimeUnit.SECONDS);
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
    private void whenRedisValueIncrSingleImagePhotographySuccess() {
        String redisKey = getSingleImagePhotographyRedisKey();
        try {
            redisTemplate.opsForValue().increment(redisKey);
        } catch (Exception e) {
            try {
                Thread.sleep(1000);
                whenRedisValueIncrSingleImagePhotographySuccess();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        VolcengineApi2 api = new VolcengineApi2();

        List<String> req_key = new ArrayList<>();
        // 美漫风格
//        req_key.add("img2img_photoverse_american_comics");
        // 商务证件照
        req_key.add("img2img_photoverse_executive_ID_photo");
        // 3d人偶
        req_key.add("img2img_photoverse_3d_weird");

        List<BusinessCardDiagramResult> list = new ArrayList<>();
        String faceImage;
        faceImage = "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/2025%2F04%2Fcbd8ab3f-3d89-410b-a6c6-d2fcf36fb562.jpg";
        try {
            String string = api.singleImagePhotography(faceImage, "img2img_photoverse_american_comics");
            System.out.println(string);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String mergeImage(String faceImage, String templateImage, String source_similarity, Float gpen, String reqKey) throws Exception {
       return mergeImageFacesWap(faceImage, templateImage, source_similarity, gpen, faceConfig.getSkin());
    }




    /**
     * 调用火山引擎 进行 单图写真
     * @param faceImage
     * @return
     * @throws Exception
     */
    public String singleImagePhotography(String faceImage, String req_key) throws Exception {
        /* create credentials */
        Credentials credentials = new Credentials();
        credentials.setAccessKeyID(faceConfig.getAccessKeyId());
        credentials.setSecretAccessKey(faceConfig.getSecretAccessKey());
        credentials.setRegion("cn-north-1");
        credentials.setService("cv");
        // QPS 限制 2个
        whenRedisValueIncrSingleImagePhotographySuccess();

        /* create signer */
        Signer signer = new Signer();

        /* create http client */
        CloseableHttpClient httpClient = HttpClients.createDefault();

        /* prepare request */
        HttpPost request = new HttpPost();
        request.setURI(new URI("https://visual.volcengineapi.com?Action=HighAesSmartDrawing&Version=2022-08-31"));

        request.addHeader("Content-Type", "application/json");
        request.addHeader("MIME-Type", "application/json");

        // 将参数转换为JSON格式
        Gson gson = new Gson();
        String requestBody = gson.toJson(new SingleImagePhotographyBody(req_key, faceImage));

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
                JSONObject jsonObject = JSONObject.parseObject(result);
                Object code = jsonObject.get("code");
                if (code.toString().equals("10000")) {
                    Object message = jsonObject.get("message");
                    Object status = jsonObject.get("status");
                    Object request_id = jsonObject.get("request_id");
                    log.info("VolcengineApi singleImagePhotography result code {}, message: {}, status: {}, request_id: {} ", code.toString(), message.toString(), status.toString(), request_id.toString());
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

    private static class SingleImagePhotographyBody {

        String req_key;

        List<String> image_urls;

        Boolean return_url;

        public SingleImagePhotographyBody(String req_key, String imageUrl) {
            List<String> imageUrls = Arrays.asList(imageUrl);
            this.req_key = req_key;
            this.image_urls = imageUrls;
            this.return_url = false;
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
    public String mergeImageFacesWap(String faceImage, String templateImage, String source_similarity, Float gpen, Float shin) throws Exception {
        /* create credentials */
        Credentials credentials = new Credentials();
        credentials.setAccessKeyID(faceConfig.getAccessKeyId());
        credentials.setSecretAccessKey(faceConfig.getSecretAccessKey());
        credentials.setRegion("cn-north-1");
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
        request.setURI(new URI("https://visual.volcengineapi.com?Action=FaceswapAI&Version=2022-08-31"));

        request.addHeader("Content-Type", "application/json");

        // 设置请求参数
        List<String> imageUrls = Arrays.asList(faceImage,
                templateImage); // 示例URL列表

        // 将参数转换为JSON格式
        Gson gson = new Gson();
        String requestBody = gson.toJson(new Body("faceswap_ai", imageUrls, source_similarity, gpen, shin));

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
        Float skin;

        public Body(String reqKey, List<String> imageUrls, String source_similarity, Float gpen, Float skin) {
            this.req_key = reqKey;
            this.image_urls = imageUrls;
            this.source_similarity = source_similarity;
            this.gpen = gpen;
            this.skin = skin;
        }
    }


}
