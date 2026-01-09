package com.caring.sass.ai.tools.asr;

import com.caring.sass.ai.config.AsrProperties;
import org.springframework.stereotype.Component;

/**
 * ASR工具测试类
 * 
 * @author leizhi
 */
@Component
public class AsrTest {

    private final AsrSubmitTool asrSubmitTool;
    private final AsrQueryTool asrQueryTool;
    private final AsrProperties asrProperties;

    public AsrTest(AsrSubmitTool asrSubmitTool, AsrQueryTool asrQueryTool, AsrProperties asrProperties) {
        this.asrSubmitTool = asrSubmitTool;
        this.asrQueryTool = asrQueryTool;
        this.asrProperties = asrProperties;
    }

    public void testAsrFunctionality() {
        // Check if API key is configured
        if (asrProperties.getApiKey() == null || asrProperties.getApiKey().isEmpty()) {
            System.out.println("ASR API Key not configured. Please set caring.asr.api-key in your configuration.");
            return;
        }

        try {
            // Example usage
            String fileUrl = "https://dashscope.oss-cn-beijing.aliyuncs.com/audios/welcome.mp3";
            System.out.println("Submitting audio file for transcription: " + fileUrl);
            
            String taskId = asrSubmitTool.submitAudioToText(fileUrl);
            System.out.println("Task ID returned: " + taskId);
            
            if (taskId != null && !taskId.startsWith("Error")) {
                System.out.println("Querying task status for task ID: " + taskId);
                String result = asrQueryTool.queryAudioToTextResult(taskId);
                System.out.println("Query result: " + result);
            }
        } catch (Exception e) {
            System.err.println("Error during ASR test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}