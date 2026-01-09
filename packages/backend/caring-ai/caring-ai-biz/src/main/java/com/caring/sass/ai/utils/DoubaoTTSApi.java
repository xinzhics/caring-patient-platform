package com.caring.sass.ai.utils;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.config.DoubaoTTSConfig;
import com.caring.sass.ai.entity.podcast.DoubaoVoiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Base64;

@Slf4j
@Component
public class DoubaoTTSApi {

    private DoubaoTTSConfig doubaoTTSConfig = new DoubaoTTSConfig();
    
    public DoubaoTTSApi(DoubaoTTSConfig doubaoTTSConfig) {
        this.doubaoTTSConfig = doubaoTTSConfig;
    }

    private static final String DEFAULT_ENCODING = "mp3";
    private static final float DEFAULT_SPEED_RATIO = 1.0f;

    public DoubaoTTSApi() {

    }

    public static void main(String[] args) {
        DoubaoTTSApi ttsApi = new DoubaoTTSApi();
        String generatedAudio = ttsApi.generateAudio(DoubaoVoiceType.KAILANGJIEJIE, "1234567891", "你好，我是田曼医生，这是我通过AI生成的形象");
        System.out.println(generatedAudio);
    }


    public String generateAudio(DoubaoVoiceType voiceType, String reqId, String text) {
        return generateAudio(voiceType, reqId, text, null, null);
    }

    public String generateAudio(DoubaoVoiceType voiceType, String reqid, String text, Float speedRatio, String encoding) {
        if (speedRatio == null) {
            speedRatio = DEFAULT_SPEED_RATIO;
        }
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }

        HttpURLConnection conn = null;
        try {
            conn = createConnection();
            JSONObject jsonRequest = buildJsonRequest(voiceType.getVoiceType(), reqid, text, speedRatio, encoding);

            // 发送请求
            sendRequest(conn, jsonRequest);

            // 读取响应
            String response = readResponse(conn);
            JSONObject jsonResponse = JSONObject.parseObject(response);

            // 解析返回参数
            int code = jsonResponse.getIntValue("code");
            String message = jsonResponse.getString("message");
            int sequence = jsonResponse.getIntValue("sequence");
            String data = jsonResponse.getString("data");
            JSONObject addition = jsonResponse.getJSONObject("addition"); // 可能为null
            String duration = addition != null ? addition.getString("duration") : null;

            System.out.println("Status Code: " + code);
            System.out.println("Message: " + message);
            System.out.println("Sequence: " + sequence);
            // 不打印base64音频数据
            // System.out.println("Data (Base64): " + data);

            if (duration != null) {
                System.out.println("Duration: " + duration + " ms");
            }

            // 解码base64字符串为字节数组
            byte[] audioBytes = Base64.getDecoder().decode(data);
            // 将字节数组保存为MP3文件，可以根据需要修改文件路径
            String filePath = doubaoTTSConfig.getFilePath();
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String audioFilePath = filePath + File.separator + reqid + "." + DEFAULT_ENCODING;
            saveAudioToFile(audioBytes, audioFilePath);

            return filePath;
        } catch (Exception e) {
            log.error("文字转语音异常", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return "";
    }

    private HttpURLConnection createConnection() throws IOException {
        URL url = new URL(doubaoTTSConfig.getApiUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer; " + doubaoTTSConfig.getToken());
        conn.setDoOutput(true);
        return conn;
    }

    private JSONObject buildJsonRequest(String voiceType, String reqid, String text, Float speedRatio, String encoding) {
        JSONObject jsonRequest = new JSONObject();
        JSONObject app = new JSONObject();
        app.put("appid", doubaoTTSConfig.getAppId());
        app.put("token", doubaoTTSConfig.getToken());
        app.put("cluster", doubaoTTSConfig.getCluster());
        jsonRequest.put("app", app);

        JSONObject user = new JSONObject();
        user.put("uid", doubaoTTSConfig.getUid());
        jsonRequest.put("user", user);

        JSONObject audio = new JSONObject();
        audio.put("voice_type", voiceType);
        audio.put("encoding", encoding);
        audio.put("speed_ratio", speedRatio);
        jsonRequest.put("audio", audio);

        JSONObject request = new JSONObject();
        request.put("reqid", reqid);
        request.put("text", text);
        request.put("operation", "query");
        jsonRequest.put("request", request);

        return jsonRequest;
    }

    private void sendRequest(HttpURLConnection conn, JSONObject jsonRequest) throws IOException {
        try (OutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
            byte[] input = jsonRequest.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    private void saveAudioToFile(byte[] audioBytes, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(audioBytes);
        }
    }
}