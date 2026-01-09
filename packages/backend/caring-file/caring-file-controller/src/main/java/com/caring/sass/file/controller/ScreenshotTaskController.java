package com.caring.sass.file.controller;

import cn.hutool.Hutool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.utils.AESEncryption;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.file.dto.ScreenshotTaskPageDTO;
import com.caring.sass.file.dto.ScreenshotTaskSaveDTO;
import com.caring.sass.file.dto.ScreenshotTaskUpdateDTO;
import com.caring.sass.file.entity.ScreenshotTask;
import com.caring.sass.file.properties.FileServerProperties;
import com.caring.sass.file.service.ScreenshotTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 视频截图任务
 * </p>
 *
 * @author 杨帅
 * @date 2023-04-06
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/screenshotTask")
@Api(value = "ScreenshotTask", tags = "视频截图任务")
public class ScreenshotTaskController extends SuperController<ScreenshotTaskService, Long, ScreenshotTask, ScreenshotTaskPageDTO, ScreenshotTaskSaveDTO, ScreenshotTaskUpdateDTO> {

    public static String hwOBSPath = "https://%s.obs.%s.myhuaweicloud.com/";


    @Autowired
    FileServerProperties fileServerProperties;


    @ApiOperation("获取华为OBS的配置信息")
    @GetMapping("getHuaweiObsPath")
    public R<JSONObject> getHuaweiObsPath() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hwOBSPath", hwOBSPath);
        FileServerProperties.HW hw = fileServerProperties.getHw();
        String accessKey = hw.getAccessKey();
        String securityKey = hw.getSecurityKey();

        jsonObject.put("accessKey", accessKey);
        jsonObject.put("securityKey", securityKey);

        String jsonString = jsonObject.toJSONString();

        String IV = UUID.randomUUID().toString();
        IV = IV.replace("-", "").substring(0, 16);
        String encrypt = AESEncryption.encrypt(jsonString, IV);

        JSONObject object = new JSONObject();
        object.put("IV", IV);
        object.put("result", encrypt);
        return R.success(object);
    }



    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<ScreenshotTask> screenshotTaskList = list.stream().map((map) -> {
            ScreenshotTask screenshotTask = ScreenshotTask.builder().build();
            //TODO 请在这里完成转换
            return screenshotTask;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(screenshotTaskList));
    }

    @PostMapping("createScreenshotTask")
    @ApiOperation("创建一个视频截图的任务")
    public R<ScreenshotTask> createScreenshotTask(@RequestBody ScreenshotTaskSaveDTO screenshotTaskSaveDTO) {
        ScreenshotTask screenshotTask = baseService.createScreenshotTask(screenshotTaskSaveDTO);
        String status = screenshotTask.getStatus();
        if (status.equals("FINISHED")) {
            String path = getCoverUrl(screenshotTask);
            screenshotTask.setCoverUrl(path);
        }
        return R.success(screenshotTask);
    }

    @PostMapping("createScreenshotTaskAndReturnCover")
    @ApiOperation("创建视频截图的任务,并返回视频封面")
    public R<ScreenshotTask> createScreenshotTaskAndReturnCover(@RequestBody ScreenshotTaskSaveDTO screenshotTaskSaveDTO) {
        ScreenshotTask screenshotTask = baseService.getOne(Wraps.<ScreenshotTask>lbQ()
                .eq(ScreenshotTask::getFileName, screenshotTaskSaveDTO.getFileName())
                .last(" limit 0,1 "));
        if (screenshotTask == null) {
            screenshotTask = baseService.createScreenshotTask(screenshotTaskSaveDTO);
        }
        String status = screenshotTask.getStatus();
        if (status.equals("FINISHED")) {
            String path = getCoverUrl(screenshotTask);
            screenshotTask.setCoverUrl(path);
        }
        return R.success(screenshotTask);
    }

    private String getCoverUrl(ScreenshotTask screenshotTask) {
        String output = screenshotTask.getOutput();
        JSONObject jsonObject = JSON.parseObject(output);
        Object object = jsonObject.get("object");
        if (Objects.nonNull(object)) {
            String bucket = jsonObject.getString("bucket");
            String location = jsonObject.getString("location");
            String obj = object.toString();
            return String.format(hwOBSPath, bucket, location) + obj + screenshotTask.getOutputFileName();
        }
        return null;
    }



    @GetMapping("/anno/getScreenshotPhoto")
    @ApiOperation("查询视频封面地址")
    public R<String> annoGetScreenshotPhoto(@RequestParam String fileName) {
        return getScreenshotPhoto(fileName);
    }


    @GetMapping("getScreenshotPhoto")
    @ApiOperation("查询视频封面地址")
    public R<String> getScreenshotPhoto(@RequestParam String fileName) {

        ScreenshotTask screenshotTask = baseService.getOne(Wraps.<ScreenshotTask>lbQ().eq(ScreenshotTask::getFileName, fileName).last(" limit 0,1 "));
        if (Objects.isNull(screenshotTask)) {
            return R.success(null);
        }
        String status = screenshotTask.getStatus();
        if (status.equals("FINISHED")) {
            String path = getCoverUrl(screenshotTask);
            return R.success(path);
        }
        return R.success(null);
    }


}
