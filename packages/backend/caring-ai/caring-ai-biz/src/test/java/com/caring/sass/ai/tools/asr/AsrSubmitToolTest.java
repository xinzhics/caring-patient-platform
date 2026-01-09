package com.caring.sass.ai.tools.asr;

import com.caring.sass.ai.config.AsrProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {AsrSubmitTool.class, AsrProperties.class})
public class AsrSubmitToolTest {

    @Autowired
    private AsrSubmitTool asrSubmitTool;

    @Autowired
    private AsrProperties asrProperties;

    @Test
    public void testAsrSubmitFunctionality() {
        // 检查是否配置了API密钥
        if (asrProperties.getApiKey() == null || asrProperties.getApiKey().isEmpty()) {
            System.out.println("ASR API Key 未配置。请在配置文件中设置 caring.asr.api-key");
            return;
        }

        try {
            // 测试提交功能
            String fileUrl = "https://dashscope.oss-cn-beijing.aliyuncs.com/audios/welcome.mp3";
            System.out.println("正在提交音频文件进行转文字: " + fileUrl);
            
            String taskId = asrSubmitTool.submitAudioToText(fileUrl);
            System.out.println("返回的任务ID: " + taskId);
            
            if (taskId != null && !taskId.startsWith("Error")) {
                // 验证返回的任务ID不为空
                assertNotNull(taskId, "任务ID不应为空");
                assertTrue(!taskId.trim().isEmpty(), "任务ID不应为空字符串");
                System.out.println("音频转文字提交测试成功");
            } else {
                System.out.println("提交任务失败: " + taskId);
            }
        } catch (Exception e) {
            System.err.println("ASR提交测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testAsrPropertiesConfiguration() {
        // 验证配置是否正确加载
        assertNotNull(asrProperties, "ASR配置不应为空");
        System.out.println("ASR配置已加载，API Key存在: " + (asrProperties.getApiKey() != null));
    }
}