package com.caring.sass.ai.controller.card;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.caring.sass.ai.card.service.BusinessCardDiagramResultService;
import com.caring.sass.ai.entity.card.BusinessCardDiagramResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.caring.sass.ai.entity.card.BusinessCardDiagramTask;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.GenderType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * <p>
 * 前端控制器
 * 医生名片头像合成结果
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-10
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCardDiagramResult")
@Api(value = "BusinessCardDiagramResult", tags = "医生名片头像合成结果")
public class BusinessCardDiagramResultController {

    @Autowired
    BusinessCardDiagramResultService baseService;



    @ApiOperation(value = "创建一个数字人形象生成任务")
    @RequestMapping(value = "/createTask", method = RequestMethod.POST)
    public R<BusinessCardDiagramTask> createTask(@RequestParam(value = "file") MultipartFile file,
                                                 @RequestParam(value = "userId", required = false) Long userId,
                                                 @RequestParam("gender") GenderType gender) {

        if (Objects.isNull(userId)) {
            userId = BaseContextHandler.getUserId();
        }
        BusinessCardDiagramTask task = baseService.createTask(file, gender, userId);
        return R.success(task);
    }


    @ApiOperation(value = "查询照片数字人合成结果")
    @RequestMapping(value = "/queryTaskMergeResult", method = RequestMethod.GET)
    public R<List<BusinessCardDiagramResult>> queryTaskMergeResult(@RequestParam("taskId") Long taskId) {

        List<BusinessCardDiagramResult> results = baseService.queryTaskMergeResult(taskId);
        return R.success(results);

    }



    @Deprecated
    @ApiOperation(value = "上传一个照片")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public R<List<BusinessCardDiagramResult>> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam("gender") GenderType gender) {

        Long userId = BaseContextHandler.getUserId();
        // todo 需要确认，目前逯说不需要检查历史数据，所以先注释掉
   /*     List<BusinessCardDiagramResult> diagramResults = baseService.list(Wraps.<BusinessCardDiagramResult>lbQ()
                        .eq(BusinessCardDiagramResult::getHistory_, false)
                .eq(BusinessCardDiagramResult::getGender, gender)
                .eq(SuperEntity::getCreateUser, userId));
        if (CollUtil.isNotEmpty(diagramResults)) {
            return R.success(diagramResults);
        }*/

        List<BusinessCardDiagramResult> results = baseService.saveAndMerge(file, userId, gender);
        return R.success(results);

    }


    @Deprecated
    @ApiOperation(value = "获取合成的结果(当上面接口超时，可以使用这个刷新结果)")
    @RequestMapping(value = "/getMergeResult", method = RequestMethod.GET)
    public R<List<BusinessCardDiagramResult>> getMergeResult(@RequestParam("gender") GenderType gender,
                                                             @RequestParam(value = "timestamp", required = false) Long timestamp) {

        Long userId = BaseContextHandler.getUserId();
        LocalDateTime uploadTime = timestamp == null ? null : LocalDateTimeUtil.of(timestamp);
        List<BusinessCardDiagramResult> diagramResults = baseService.list(Wraps.<BusinessCardDiagramResult>lbQ()
                .eq(BusinessCardDiagramResult::getGender, gender)
                .eq(BusinessCardDiagramResult::getHistory_, false)
                .ge(uploadTime != null, BusinessCardDiagramResult::getCreateTime, uploadTime)
                .eq(SuperEntity::getCreateUser, userId));
        return R.success(diagramResults);

    }


}
