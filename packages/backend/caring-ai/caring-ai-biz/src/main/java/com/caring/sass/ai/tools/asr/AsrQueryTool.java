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
 * 音频转文字查询工具类
 *
 * @author leizhi
 */
@Component
@Slf4j
public class AsrQueryTool {

    private static final String API_URL_BASE = "https://dashscope.aliyuncs.com/api/v1/tasks/";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final AsrProperties asrProperties;

    public AsrQueryTool(AsrProperties asrProperties) {
        this.asrProperties = asrProperties;
    }

    @Tool("useful when you need to query the status of speech-to-text conversion task.")
    public String queryAudioToTextResult(@P("task ID to query speech-to-text result") String taskId) throws Exception {
        if (taskId == null || taskId.trim().isEmpty()) {
            return "Error: Task ID is required";
        }

        OkHttpClient client = new OkHttpClient();

        String queryUrl = API_URL_BASE + taskId;
        Request request = new Request.Builder()
                .url(queryUrl)
                .addHeader("Authorization", "Bearer " + asrProperties.getApiKey())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String queryResponse = response.body().string();
                AsrModels.ApiResponse apiResp = gson.fromJson(queryResponse, AsrModels.ApiResponse.class);

                if (apiResp.getOutput() != null && apiResp.getOutput().getTaskStatus() != null) {
                    String status = apiResp.getOutput().getTaskStatus();
                    log.info("查询任务状态 - task_id: {}, status: {}", taskId, status);
                    
                    // Return the full response for the user to see the status and result
                    return queryResponse;
                } else {
                    log.warn("查询返回内容: {}", queryResponse);
                    return queryResponse;
                }
            } else {
                log.error("查询任务失败 - task_id: {}", taskId);
                return "Error: Failed to get response from server";
            }
        } catch (IOException e) {
            log.error("查询音频转文字任务时发生异常 - task_id: {}", taskId, e);
            return "Error: " + e.getMessage();
        }
    }
}