package com.caring.sass.file.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.file.dto.ScreenshotTaskSaveDTO;
import com.caring.sass.file.entity.ScreenshotTask;

/**
 * <p>
 * 业务接口
 * 视频截图任务
 * </p>
 *
 * @author 杨帅
 * @date 2023-04-06
 */
public interface ScreenshotTaskService extends SuperService<ScreenshotTask> {



    ScreenshotTask createScreenshotTask(ScreenshotTaskSaveDTO screenshotTaskSaveDTO);

}
