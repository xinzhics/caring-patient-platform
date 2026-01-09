package com.caring.sass.file.api;

import com.caring.sass.base.R;
import com.caring.sass.file.dto.ScreenshotTaskSaveDTO;
import com.caring.sass.file.entity.ScreenshotTask;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @className: ScreenshotTaskApi
 * @author: 杨帅
 * @date: 2023/9/11
 */
@Component
@FeignClient(name = "${caring.feign.file-server:caring-file-server}")
public interface ScreenshotTaskApi {

    @PostMapping("/screenshotTask/createScreenshotTaskAndReturnCover")
    @ApiOperation("创建视频截图的任务,并返回视频封面")
    R<ScreenshotTask> createScreenshotTaskAndReturnCover(@RequestBody ScreenshotTaskSaveDTO screenshotTaskSaveDTO);


}
