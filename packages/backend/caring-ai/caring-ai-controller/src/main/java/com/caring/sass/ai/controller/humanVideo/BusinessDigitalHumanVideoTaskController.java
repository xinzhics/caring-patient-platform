package com.caring.sass.ai.controller.humanVideo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.card.service.BusinessCardDiagramResultService;
import com.caring.sass.ai.dto.humanVideo.BusinessDigitalHumanVideoTaskPageDTO;
import com.caring.sass.ai.dto.humanVideo.BusinessDigitalHumanVideoTaskSaveDTO;
import com.caring.sass.ai.dto.humanVideo.BusinessDigitalHumanVideoTaskUpdateDTO;
import com.caring.sass.ai.dto.humanVideo.HumanVideoPhotoDTO;
import com.caring.sass.ai.entity.card.BusinessCardDiagramResult;
import com.caring.sass.ai.entity.humanVideo.BusinessDigitalHumanVideoTask;
import com.caring.sass.ai.entity.humanVideo.HumanTaskSource;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.humanVideo.service.BusinessDigitalHumanVideoTaskService;
import com.caring.sass.ai.humanVideo.task.VoiceTaskComponent;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.repeat.annotation.RepeatSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 数字人视频制作任务
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessDigitalHumanVideoTask")
@Api(value = "BusinessDigitalHumanVideoTask", tags = "数字人视频制作任务")
public class BusinessDigitalHumanVideoTaskController {

    @Autowired
    private BusinessDigitalHumanVideoTaskService taskService;

    @Autowired
    BusinessCardDiagramResultService businessCardDiagramResultService;

    @Autowired
    VoiceTaskComponent voiceTaskComponent;

    @ApiOperation("小程序-查询历史数字人视频")
    @GetMapping("queryHistoryHumanVideo/{userId}")
    public R<List<HumanVideoPhotoDTO>> queryHistoryHumanVideo(@PathVariable("userId") Long userId) {

        List<BusinessDigitalHumanVideoTask> tasks = taskService.list(Wraps.<BusinessDigitalHumanVideoTask>lbQ()
                .ne(BusinessDigitalHumanVideoTask::getTaskStatus, HumanVideoTaskStatus.SUBMIT_ING)
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(BusinessDigitalHumanVideoTask::getUserId, userId));
        List<HumanVideoPhotoDTO> dtos = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(tasks)) {
            tasks.forEach(task -> {
                HumanVideoPhotoDTO photoDTO = new HumanVideoPhotoDTO();

                photoDTO.setName(task.getTaskName());
                if (HumanVideoTaskStatus.SUCCESS.equals(task.getTaskStatus())) {
                    photoDTO.setStatus("success");
                    photoDTO.setHumanVideoFileUrl(task.getHumanVideoUrl());
                    photoDTO.setHumanVideoCover(task.getHumanVideoCover());
                    photoDTO.setCreateTime(task.getEndTime());
                } else if (HumanVideoTaskStatus.FAIL.equals(task.getTaskStatus())) {
                    photoDTO.setStatus("fail");
                } else {
                    photoDTO.setStatus("created");
                }
                dtos.add(photoDTO);

            });
        }

        List<BusinessCardDiagramResult> diagramResults = businessCardDiagramResultService.list(Wraps.<BusinessCardDiagramResult>lbQ()
                .eq(SuperEntity::getCreateUser, userId));

        if (CollUtil.isNotEmpty(diagramResults)) {
            diagramResults.forEach(diagramResult -> {
                HumanVideoPhotoDTO photoDTO = new HumanVideoPhotoDTO();

                photoDTO.setName("数字人");
                photoDTO.setCreateTime(diagramResult.getCreateTime());
                photoDTO.setHumanVideoFileUrl(diagramResult.getImageObsUrl());
                photoDTO.setStatus("success");
                dtos.add(photoDTO);

            });
        }
        dtos.sort((o1, o2) -> {
            if (o1.getCreateTime() == null && o2.getCreateTime() == null) return 0;
            if (o1.getCreateTime() == null) return 1;
            if (o2.getCreateTime() == null) return -1;
            return o2.getCreateTime().compareTo(o1.getCreateTime());
        });
        return R.success(dtos);
    }

    @ApiOperation("小程序-查询提交数字人资料过程的任务")
    @PostMapping("getNotStartTask/{userId}")
    public R<BusinessDigitalHumanVideoTask> getNotStartTask(@PathVariable("userId") Long userId) {

        BusinessDigitalHumanVideoTask videoTask = taskService.getNotStartTask(userId);
        return R.success(videoTask);
    }


    @ApiOperation("小程序-数字人创作- 设置制作方式")
    @RepeatSubmit(interval = 5)
    @PostMapping("submitDigitalHumanVideoTask")
    public R<BusinessDigitalHumanVideoTask> submitDigitalHumanVideoTask(@RequestBody @Validated BusinessDigitalHumanVideoTaskUpdateDTO updateDTO) {

        BusinessDigitalHumanVideoTask videoTask = taskService.submitDigitalHumanVideoTask(updateDTO);
        return R.success(videoTask);

    }


    @ApiOperation("小程序-数字人创作- 上传资料")
    @PostMapping("submitTaskOtherData")
    public R<BusinessDigitalHumanVideoTask> submitTaskOtherData(@RequestBody @Validated BusinessDigitalHumanVideoTaskUpdateDTO updateDTO) {

        BusinessDigitalHumanVideoTask videoTask = taskService.submitTaskOtherData(updateDTO);
        return R.success(videoTask);

    }


    @ApiOperation("小程序-数字人创作- 开始制作")
    @RepeatSubmit(interval = 5)
    @PostMapping("submitEndStartTask")
    public R<BusinessDigitalHumanVideoTask> submitEndStartTask(@RequestBody @Validated BusinessDigitalHumanVideoTaskUpdateDTO updateDTO) {

        BusinessDigitalHumanVideoTask videoTask = taskService.submitEndStartTask(updateDTO);
        return R.success(videoTask);

    }


    @ApiOperation("图片数字人创作- 校验人脸照片")
    @PostMapping("verifyImage")
    public R<JSONObject> verifyImage(@RequestBody @Validated BusinessDigitalHumanVideoTaskSaveDTO updateDTO) {

        JSONObject jsonObject = taskService.verifyImage(updateDTO);
        return R.success(jsonObject);

    }

    @ApiOperation("管理员-提交数字人创作任务")
    @PostMapping("adminSubmitTask")
    public R<BusinessDigitalHumanVideoTask> adminSubmitTask(@RequestBody @Validated BusinessDigitalHumanVideoTaskUpdateDTO updateDTO) {

        BusinessDigitalHumanVideoTask videoTask = taskService.adminSubmitStartTask(updateDTO);
        return R.success(videoTask);

    }

    @ApiOperation("管理员-数字人视频分页记录")
    @PostMapping("adminPage")
    public R<IPage<BusinessDigitalHumanVideoTask>> adminPage(@RequestBody @Validated PageParams<BusinessDigitalHumanVideoTaskPageDTO> params) {

        IPage<BusinessDigitalHumanVideoTask> builtPage = params.buildPage();

        BusinessDigitalHumanVideoTaskPageDTO model = params.getModel();
        LbqWrapper<BusinessDigitalHumanVideoTask> wrapper = Wraps.<BusinessDigitalHumanVideoTask>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(BusinessDigitalHumanVideoTask::getTaskStatus, model.getTaskStatus())
                .like(BusinessDigitalHumanVideoTask::getTaskName, model.getTaskName())
                .eq(SuperEntity::getCreateUser, BaseContextHandler.getUserId())
                .eq(BusinessDigitalHumanVideoTask::getTaskSource, HumanTaskSource.WEB_ADMIN);
        taskService.page(builtPage, wrapper);

        return R.success(builtPage);

    }


    @ApiOperation("百度的回调")
    @PostMapping("anno/callback")
    public R<String> callback(@RequestParam(name = "requestId", required = false) String requestId,
                              @RequestParam(name = "type", required = false) String type,
                              @RequestBody String data) {

        // {"type":"VIDEO_GENERATE_IMAGE","data":{"taskId":"img-rdgr3ih33br19m6e",
        // "videoUrl":"https://digital-human-pipeline-output.cdn.bcebos.com/vis/video/v-rdghttn9cykxgavg80.mp4",
        // "failedCode":0,"duration":4888,"subtitleFileUrl":"","status":"SUCCESS"}}
        log.error("baidu callback: requestId: {}, type: {}, data: {}", requestId, type, data);
        if (StrUtil.isNotEmpty(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String string = jsonObject.getString("data");
            if (StrUtil.isNotEmpty(string)) {
                JSONObject dataResult = JSONObject.parseObject(string);
                String status = dataResult.getString("status");
                if (StrUtil.equals(status, "SUCCESS")) {
                    String taskId = dataResult.getString("taskId");
                    String videoUrl = dataResult.getString("videoUrl");
                    taskService.updateSuccess(taskId, videoUrl);
                } else if (StrUtil.equals(status, "FAILED")) {
                    String taskId = dataResult.getString("taskId");
                    taskService.updateFailed(taskId, string);
                }
            }
        }

        return R.success("SUCCESS");

    }





}
