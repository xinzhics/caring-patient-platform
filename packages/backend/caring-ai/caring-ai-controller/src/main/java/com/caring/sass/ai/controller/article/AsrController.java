package com.caring.sass.ai.controller.article;

import com.caring.sass.ai.tools.asr.AsrQueryTool;
import com.caring.sass.ai.tools.asr.AsrSubmitTool;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 前端控制器
 * ASR音频转文字服务
 * </p>
 *
 * @author AI Assistant
 * @date 2024-12-05
 */
@Slf4j
@RestController
@RequestMapping("/asr")
@Api(value = "ASR", tags = "ASR音频转文字服务")
public class AsrController {

    @Autowired
    private AsrSubmitTool asrSubmitTool;

    @Autowired
    private AsrQueryTool asrQueryTool;

    @ApiOperation("提交音频文件进行转文字")
    @PostMapping("/submit")
    public R<String> submitAudioToText(@RequestParam @NotNull(message = "音频文件URL不能为空") String fileUrl) {
        try {
            String taskId = asrSubmitTool.submitAudioToText(fileUrl);
            if (taskId != null && !taskId.startsWith("Error")) {
                return R.success(taskId);
            } else {
                return R.fail(taskId != null ? taskId : "提交失败");
            }
        } catch (Exception e) {
            log.error("提交音频转文字任务异常", e);
            return R.fail("提交失败：" + e.getMessage());
        }
    }

    @ApiOperation("查询音频转文字任务结果")
    @GetMapping("/query")
    public R<String> queryAudioToTextResult(@RequestParam @NotNull(message = "任务ID不能为空") String taskId) {
        try {
            String result = asrQueryTool.queryAudioToTextResult(taskId);
            if (result != null && !result.startsWith("Error")) {
                return R.success(result);
            } else {
                return R.fail(result != null ? result : "查询失败");
            }
        } catch (Exception e) {
            log.error("查询音频转文字任务结果异常", e);
            return R.fail("查询失败：" + e.getMessage());
        }
    }
}