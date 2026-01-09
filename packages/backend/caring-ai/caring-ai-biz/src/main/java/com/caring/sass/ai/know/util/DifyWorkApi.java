package com.caring.sass.ai.know.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.know.config.DifyApi;
import com.caring.sass.ai.know.config.DifyApiConfig;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * dify工作室的api
 */
@Service
public class DifyWorkApi {

    @Autowired
    DifyApiConfig apiConfig;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    DifyFlowControl difyFlowControl;

    private static final String DIFY_API_KEY = System.getenv().getOrDefault("DIFY_API_KEY", "");


    /**
     * 查询dify理解用户的问题
     * @param question
     */
    public String queryDifyLiJieQuestion(String question, Long userId) {


        String urlPath = apiConfig.getApiDomain() +  DifyApi.completionMessages.getPath();

        difyFlowControl.whenRedisValueIncrSuccess();
        StringBuilder response = new StringBuilder(); // 用于存储响应内容
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.completionMessages.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + DIFY_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 构建请求体
            JSONObject jsonObject = new JSONObject();
            JSONObject inputs = new JSONObject();
            inputs.put("query", question);
            jsonObject.put("inputs", inputs);
            jsonObject.put("response_mode", "blocking");    // 使用阻塞模式。 3分钟的文本生成标题，应该不用很久
            jsonObject.put("user", userId.toString()); // 替换为实际用户 ID

            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("AI 调用成功");

                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject parsedObject = JSON.parseObject(response.toString());
                return parsedObject.getString("answer");
            } else {
                System.out.println("AI queryDifyLiJieQuestion 调用失败，响应代码: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * curl -X POST 'http://dify.caringopen.cn/v1/completion-messages' \
     * --header 'Authorization: Bearer {api_key}' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{
     *     "inputs": {"query": "Hello, world!"},
     *     "response_mode": "streaming",
     *     "user": "abc-123"
     * }'
     */
    public String callAiCreateTitle(String textContent, Long userId) {

        String urlPath = apiConfig.getApiDomain() +  DifyApi.completionMessages.getPath();
        difyFlowControl.whenRedisValueIncrSuccess();
        StringBuilder response = new StringBuilder(); // 用于存储响应内容
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.completionMessages.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getTitleCreateWorkApiKey());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 构建请求体
            JSONObject jsonObject = new JSONObject();
            JSONObject inputs = new JSONObject();
            inputs.put("query", textContent);
            jsonObject.put("inputs", inputs);
            jsonObject.put("response_mode", "blocking");    // 使用阻塞模式。 3分钟的文本生成标题，应该不用很久
            jsonObject.put("user", userId.toString()); // 替换为实际用户 ID

            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("AI 调用成功");

                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject parsedObject = JSON.parseObject(response.toString());
                return parsedObject.getString("answer");
            } else {
                System.out.println("AI callAiCreateTitle 调用失败，响应代码: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 返回响应内容
    }


    /**
     * AI搜索的翻译和总结
     * @param title
     * @param abstract_
     * @param userId
     * @return
     */
    public String translationAndSummary(String title, String abstract_, Long userId) {

        String urlPath = apiConfig.getApiDomain() +  DifyApi.completionMessages.getPath();
        difyFlowControl.whenRedisValueIncrSuccess();
        StringBuilder response = new StringBuilder(); // 用于存储响应内容
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.completionMessages.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getDocu_search_translation_and_summary());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 构建请求体
            JSONObject jsonObject = new JSONObject();
            JSONObject inputs = new JSONObject();
            inputs.put("title", title);
            inputs.put("abstract", abstract_);
            jsonObject.put("inputs", inputs);
            jsonObject.put("response_mode", "blocking");    // 使用阻塞模式。 3分钟的文本生成标题，应该不用很久
            jsonObject.put("user", userId.toString()); // 替换为实际用户 ID

            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("AI 调用成功");

                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject parsedObject = JSON.parseObject(response.toString());
                return parsedObject.getString("answer");
            } else {
                System.out.println("AI translationAndSummary 调用失败，响应代码: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 返回响应内容
    }


    /**
     * 调用专业学术资料。查询关键词
     * @param textContent
     * @param userId
     * @return
     */
    public String callAiCreateKeywordGetKeyWord(String textContent, Long userId, String appKey) {


        String urlPath = apiConfig.getApiDomain() +  DifyApi.workflowsRun.getPath();
        difyFlowControl.whenRedisValueIncrSuccess();
        StringBuilder response = new StringBuilder(); // 用于存储响应内容
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.workflowsRun.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + appKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 构建请求体
            JSONObject jsonObject = new JSONObject();
            JSONObject inputs = new JSONObject();
            inputs.put("text", textContent);
            jsonObject.put("inputs", inputs);
            jsonObject.put("response_mode", "blocking");    // 使用阻塞模式。 3分钟的文本生成标题，应该不用很久
            jsonObject.put("user", userId.toString()); // 替换为实际用户 ID

            String jsonInputString = jsonObject.toString();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("AI 调用成功");

                // 读取响应内容
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONObject parsedObject = JSON.parseObject(response.toString());
                JSONObject data = parsedObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONObject outputs = data.getJSONObject("outputs");
                    if (Objects.nonNull(outputs)) {
                        String text = outputs.getString("text");
                        System.out.println(text);
                        return text;
                    }
                }
                return null;
            } else {
                System.out.println("AI callAiCreateKeywordGetKeyWord 调用失败，响应代码: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 返回响应内容
    }


    /**
     * 使用sse方式。调用dify。
     * @param uid
     * @param dialogText
     * @throws Exception
     */
    public void callDifyCompletionMessagesStreaming(SseEmitter sseEmitter, String uid, String dialogText){

        String urlPath = apiConfig.getApiDomain() +  DifyApi.completionMessages.getPath();
        difyFlowControl.whenRedisValueIncrSuccess();

        // 使用 okHttp3 sse 方式请求接口
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        JSONObject jsonObject = new JSONObject();
        JSONObject inputs = new JSONObject();
        inputs.put("dialog_text", dialogText);
        jsonObject.put("inputs", inputs);
        jsonObject.put("response_mode", "streaming");    // 使用阻塞模式。 3分钟的文本生成标题，应该不用很久
        jsonObject.put("user", uid); // 替换为实际用户 ID

        EventSource.Factory factory = EventSources.createFactory(client);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String messageJson = JSONObject.toJSONString(jsonObject);
        RequestBody body = RequestBody.create(JSON, messageJson);
        Request request = new Request.Builder()
                .url(urlPath)
                .addHeader("Authorization", "Bearer " + apiConfig.getCase_create_key())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        DifyBotSSEEventSourceListener sourceListener = new DifyBotSSEEventSourceListener(sseEmitter, redisTemplate, uid);
        factory.newEventSource(request, sourceListener);

    }

}
