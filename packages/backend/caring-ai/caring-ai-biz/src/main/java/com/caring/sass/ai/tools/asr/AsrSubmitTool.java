package com.caring.sass.ai.tools.asr;

import com.caring.sass.ai.config.AsrProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 音频转文字提交工具类
 *
 * @author leizhi
 */
@Component
@Slf4j
public class AsrSubmitTool {

    private static final String API_URL_SUBMIT = "https://dashscope.aliyuncs.com/api/v1/services/audio/asr/transcription";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final AsrProperties asrProperties;

    public AsrSubmitTool(AsrProperties asrProperties) {
        this.asrProperties = asrProperties;
    }

    @Tool("useful when you need to submit audio file for speech-to-text conversion.")
    public String submitAudioToText(@P("audio file URL for speech-to-text conversion") String fileUrl) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // Prepare the request payload
        AsrModels.Parameters parameters = new AsrModels.Parameters();
        parameters.setChannelId(new Integer[]{0});
        parameters.setEnableItn(false);
        parameters.setLanguage("zh"); // Default to Chinese

        AsrModels.Input input = new AsrModels.Input();
        input.setFileUrl(fileUrl);

        AsrModels.SubmitRequest submitRequest = new AsrModels.SubmitRequest();
        submitRequest.setModel("qwen3-asr-flash-filetrans");
        submitRequest.setInput(input);
        submitRequest.setParameters(parameters);

        String payloadJson = gson.toJson(submitRequest);

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), payloadJson);
        Request request = new Request.Builder()
                .url(API_URL_SUBMIT)
                .addHeader("Authorization", "Bearer " + asrProperties.getApiKey())
                .addHeader("Content-Type", "application/json")
                .addHeader("X-DashScope-Async", "enable")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String respBody = response.body().string();
                AsrModels.ApiResponse apiResp = gson.fromJson(respBody, AsrModels.ApiResponse.class);
                if (apiResp.getOutput() != null && apiResp.getOutput().getTaskId() != null) {
                    String taskId = apiResp.getOutput().getTaskId();
                    log.info("ASR任务已提交，task_id: {}", taskId);
                    return taskId;
                } else {
                    log.error("提交返回内容: {}", respBody);
                    return "Error: Failed to get task ID from response";
                }
            } else {
                log.error("任务提交失败! HTTP code: {}", response.code());
                if (response.body() != null) {
                    String errorBody = response.body().string();
                    log.error("Error response: {}", errorBody);
                    return "Error: " + errorBody;
                }
                return "Error: HTTP " + response.code();
            }
        } catch (IOException e) {
            log.error("提交音频转文字任务时发生异常", e);
            return "Error: " + e.getMessage();
        }
    }
}