package com.caring.sass.ai.know.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.know.config.DifyApi;
import com.caring.sass.ai.know.config.DifyApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * dify科普名片api
 */
@Service
public class DifyCardApi {

    @Autowired
    DifyApiConfig apiConfig;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    DifyFlowControl difyFlowControl;

    /**
     * 根据医生擅长和科室生成常见问题
     *
     * @param department 科室
     * @param goodAt     擅长
     */
    public String generatorQuestion(String department, String goodAt, Long userId) {


        String urlPath = apiConfig.getApiDomain() + DifyApi.chatMessage.getPath();

        difyFlowControl.whenRedisValueIncrSuccess();
        // 用于存储响应内容
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(DifyApi.chatMessage.getMethod());
            conn.setRequestProperty("Authorization", "Bearer " + apiConfig.getGeneral_question_key());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 构建请求体
            JSONObject jsonObject = new JSONObject();
            JSONObject inputs = new JSONObject();
            inputs.put("department", department);
            inputs.put("goodAt", goodAt);
            jsonObject.put("inputs", inputs);
            // 使用阻塞模式。 3分钟的文本生成标题，应该不用很久
            jsonObject.put("query", "生成五个常见问题");
            jsonObject.put("response_mode", "blocking");
            // 替换为实际用户 ID
            jsonObject.put("user", userId.toString());

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

                // 检查返回的JSON格式是否正确
                try {
                    String answer = parsedObject.getString("answer");
                    JSONArray questions = JSON.parseArray(answer);
                    for (Object obj : questions) {
                        JSONObject questionObj = (JSONObject) obj;
                        if (!questionObj.containsKey("question")) {
                            return "";
                        }
                    }
                    return answer;
                } catch (Exception e) {
                    return "";
                }
            } else {
                // 读取错误响应内容
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        errorResponse.append(line);
                    }
                }
                System.out.println("AI generatorQuestion 调用失败，响应代码: " + responseCode);
                System.out.println("错误详情: " + errorResponse.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
