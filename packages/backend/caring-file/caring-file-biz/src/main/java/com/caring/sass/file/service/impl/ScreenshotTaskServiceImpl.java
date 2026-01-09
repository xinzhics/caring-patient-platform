package com.caring.sass.file.service.impl;


import com.alibaba.fastjson.JSON;
import com.caring.sass.file.dao.ScreenshotTaskMapper;
import com.caring.sass.file.dto.ScreenshotTaskSaveDTO;
import com.caring.sass.file.entity.ScreenshotTask;
import com.caring.sass.file.service.ScreenshotTaskService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.huaweicloud.sdk.mpc.v1.model.CreateThumbnailsTaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 视频截图任务
 * </p>
 *
 * @author 杨帅
 * @date 2023-04-06
 */
@Slf4j
@Service

public class ScreenshotTaskServiceImpl extends SuperServiceImpl<ScreenshotTaskMapper, ScreenshotTask> implements ScreenshotTaskService {

    @Autowired
    CaringMpcClient caringMpcClient;


    /**
     * @title 创建一个视频截图的任务
     * @author 杨帅
     * @updateTime 2023/4/6 14:36
     * @throws
     */
    @Override
    public ScreenshotTask createScreenshotTask(ScreenshotTaskSaveDTO screenshotTaskSaveDTO) {

        String object = screenshotTaskSaveDTO.getObject();
        String bucket = screenshotTaskSaveDTO.getBucket();
        String fileName = screenshotTaskSaveDTO.getFileName();
        ScreenshotTask screenshotTask = new ScreenshotTask().setFileName(fileName);
        CreateThumbnailsTaskResponse taskResponse = caringMpcClient.createScreenshotTask(bucket, object);
        if (Objects.isNull(taskResponse)) {
            screenshotTask.setStatus("Return Null");
        }
        screenshotTask.setStatus(taskResponse.getStatus());
        screenshotTask.setDescription(taskResponse.getDescription());
        screenshotTask.setOutputFileName(taskResponse.getOutputFileName());
        screenshotTask.setTaskId(taskResponse.getTaskId());
        screenshotTask.setOutput(JSON.toJSONString(taskResponse.getOutput()));
        baseMapper.insert(screenshotTask);
        return screenshotTask;

    }




}
