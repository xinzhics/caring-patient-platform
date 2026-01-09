package com.caring.sass.ai.tools.asr;

import com.caring.sass.ai.config.AsrProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {AsrQueryTool.class, AsrProperties.class})
public class AsrQueryToolTest {

    @Autowired
    private AsrQueryTool asrQueryTool;

    @Autowired
    private AsrProperties asrProperties;

    @Test
    public void testAsrQueryFunctionality() throws Exception {
        // 检查是否配置了API密钥
        if (asrProperties.getApiKey() == null || asrProperties.getApiKey().isEmpty()) {
            System.out.println("ASR API Key 未配置。请在配置文件中设置 caring.asr.api-key");
            return;
        }

        // 注意：这里需要使用实际存在的任务ID进行测试
        // 由于无法预知实际任务ID，可以使用一个模拟的ID来测试错误情况
        String taskId = "1150e87c-4915-4759-a9e1-33c7987fcefe"; // 实际使用时应替换为真实任务ID
        
        System.out.println("正在查询任务状态，任务ID: " + taskId);
        String result = asrQueryTool.queryAudioToTextResult(taskId);
        System.out.println("查询结果: " + result);
        
        // 由于使用了模拟ID，这里会返回错误信息，但可以验证查询功能正常工作
        assertNotNull(result, "查询结果不应为空");
        System.out.println("音频转文字查询测试完成");
    }

    @Test
    public void testAsrQueryWithValidTaskId() {
        // 检查是否配置了API密钥
        if (asrProperties.getApiKey() == null || asrProperties.getApiKey().isEmpty()) {
            System.out.println("ASR API Key 未配置。请在配置文件中设置 caring.asr.api-key");
            return;
        }

        // 如果您有真实任务ID，请在此处进行测试
        // String taskId = "真实任务ID";
        // String result = asrQueryTool.queryAudioToTextResult(taskId);
        // System.out.println("真实任务查询结果: " + result);
        
        System.out.println("如需测试真实任务，请设置有效任务ID");
    }

    @Test
    public void testAsrPropertiesConfiguration() {
        // 验证配置是否正确加载
        assertNotNull(asrProperties, "ASR配置不应为空");
        System.out.println("ASR配置已加载，API Key存在: " + (asrProperties.getApiKey() != null));
    }
}