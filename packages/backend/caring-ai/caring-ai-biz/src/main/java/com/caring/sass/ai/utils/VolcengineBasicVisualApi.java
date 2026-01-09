package com.caring.sass.ai.utils;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.signer.Credentials;
import com.caring.sass.ai.signer.Signer;
import com.caring.sass.exception.BizException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * 火山引擎智能视觉服务
 */
@Slf4j
public class VolcengineBasicVisualApi {

    private static String domain = "https://visual.volcengineapi.com";


    private static String video_req_key = "realman_change_lips_basic_chimera";




    /**
     * 火山视频改口型 创建任务
     * @param videoUrl 视频形象
     * @param pure_audio_url 音频链接
     * @return
     */
    public static String createVideoTask(String videoUrl, String pure_audio_url) throws Exception {


        /* create credentials */
        Credentials credentials = new Credentials();
        credentials.setAccessKeyID(System.getenv().getOrDefault("VOLCENGINE_ACCESS_KEY_ID", ""));
        credentials.setSecretAccessKey(System.getenv().getOrDefault("VOLCENGINE_SECRET_ACCESS_KEY", ""));
        credentials.setRegion(System.getenv().getOrDefault("VOLCENGINE_REGION", "cn-north-1"));
        credentials.setService("cv");


        /* create signer */
        Signer signer = new Signer();

        /* create http client */
        CloseableHttpClient httpClient = HttpClients.createDefault();

        /* prepare request */
        HttpPost request = new HttpPost();
        request.setURI(new URI(domain + "?Action=CVSubmitTask&Version=2022-08-31"));
        request.addHeader("Content-Type", "application/json");



        // 将参数转换为JSON格式
        Gson gson = new Gson();
        String requestBody = gson.toJson(new CreateVideoTaskBody(video_req_key, pure_audio_url, videoUrl));

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
        try {
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                JSONObject jsonObject = JSONObject.parseObject(result);
                Object code = jsonObject.get("code");
                if (code.toString().equals("10000")) {
                    Object message = jsonObject.get("message");
                    Object status = jsonObject.get("status");
                    Object request_id = jsonObject.get("request_id");
                    log.info("VolcengineApi mergeImage result code {}, message: {}, status: {}, request_id: {} ", code, message.toString(), status.toString(), request_id.toString());
                    if (message.equals("Success")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        return data.getString("task_id");
                    }
                } else {
                    throw new Exception(code.toString());
                }
            }
        } finally {
            response.close();
            httpClient.close();
        }
        return null;

    }

    public static void main(String[] args) {
        try {
            JSONObject videoResult = getCreateVideoResult("5493720232860333608");
            System.out.println(videoResult.toString());
            String video_url = videoResult.getString("url");
            System.out.println(video_url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询火山形象创建的结果
     * @param taskId
     * @return
     */
    public static JSONObject getCreateVideoResult(String taskId) throws Exception {


        /* create credentials */
        Credentials credentials = new Credentials();
        credentials.setAccessKeyID(System.getenv().getOrDefault("VOLCENGINE_ACCESS_KEY_ID", ""));
        credentials.setSecretAccessKey(System.getenv().getOrDefault("VOLCENGINE_SECRET_ACCESS_KEY", ""));
        credentials.setRegion(System.getenv().getOrDefault("VOLCENGINE_REGION", "cn-north-1"));
        credentials.setService("cv");


        /* create signer */
        Signer signer = new Signer();

        /* create http client */
        CloseableHttpClient httpClient = HttpClients.createDefault();

        /* prepare request */
        HttpPost request = new HttpPost();
        request.setURI(new URI(domain + "?Action=CVGetResult&Version=2022-08-31"));
        request.addHeader("Content-Type", "application/json");

        // 将参数转换为JSON格式
        Gson gson = new Gson();
        String requestBody = gson.toJson(new ResultBody(video_req_key, taskId));

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
        try {
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                JSONObject jsonObject = JSONObject.parseObject(result);
                Object code = jsonObject.get("code");
                if (code.toString().equals("10000")) {
                    Object message = jsonObject.get("message");
                    Object status = jsonObject.get("status");
                    Object request_id = jsonObject.get("request_id");
                    log.info("VolcengineApi getCreateVideoResult result code {}, message: {}, status: {}, request_id: {} ", code, message.toString(), status.toString(), request_id.toString());
                    if (message.equals("Success")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String statusStr = data.getString("status");
                        if (statusStr.equals("expired") || statusStr.equals("not_found")) {
                            throw new BizException(statusStr);
                        }
                        if (statusStr.equals("done")) {
                            return data.getJSONObject("resp_data");
                        }
                    } else {
                        throw new BizException(message.toString());
                    }
                } else {
                    throw new Exception(code.toString());
                }
            }
        } finally {
            response.close();
            httpClient.close();
        }
        return null;


    }




    /**
     * 查询形象结果的body
     */
    private static class CreateVideoTaskBody {
        private String req_key;
        private String pure_audio_url;
        private String url;

        public CreateVideoTaskBody(String reqKey, String audio_url, String videoUrl) {
            this.req_key = reqKey;
            this.pure_audio_url = audio_url;
            this.url = videoUrl;
        }
    }

    /**
     * 查询形象结果的body
     */
    private static class ResultBody {
        private String req_key;
        private String task_id;

        public ResultBody(String reqKey, String imageUrl) {
            this.req_key = reqKey;
            this.task_id = imageUrl;
        }
    }

    /**
     * 发起创建形象的body
     */
    private static class CreateImageBody {
        private String req_key;
        private String image_url;

        public CreateImageBody(String reqKey, String imageUrl) {
            this.req_key = reqKey;
            this.image_url = imageUrl;
        }
    }
}
