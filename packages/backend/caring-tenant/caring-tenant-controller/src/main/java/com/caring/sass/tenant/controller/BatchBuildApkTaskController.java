package com.caring.sass.tenant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.enums.BatchBuildTask;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.dto.BatchBuildApkDto;
import com.caring.sass.tenant.dto.BatchBuildTaskPage;
import com.caring.sass.tenant.entity.BatchBuildApkTask;
import com.caring.sass.tenant.entity.BatchBuildTaskChild;
import com.caring.sass.tenant.service.batchBuild.BatchBuildApkTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName BatchBuildApkTaskController
 * @Description
 * @Author yangShuai
 * @Date 2021/10/26 15:49
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/batchBuildApk")
@Api(value = "BatchBuildApkTaskController", tags = "批量打包")
public class BatchBuildApkTaskController {


    @Autowired
    BatchBuildApkTaskService buildApkTaskService;

    @ApiOperation("创建批量打包任务")
    @PostMapping("batchBuildApkTask")
    public R<Boolean> batchBuildApkTask(@Validated @RequestBody BatchBuildApkDto batchBuildApkDto) {

        buildApkTaskService.createBatchBuildApkTask(batchBuildApkDto);
        return R.success(true);
    }

    @ApiOperation("开始被暂停的任务")
    @GetMapping("restartBatchTask/{taskId}")
    public R<Boolean> restartBatchTask(@PathVariable("taskId") Long taskId) {

        buildApkTaskService.restartBatchBuildApkTask(taskId);
        return R.success(true);
    }

    @ApiOperation("停止总任务")
    @GetMapping("stopBatchTask/{taskId}")
    public R<Boolean> stopBatchTask(@PathVariable("taskId") Long taskId) {

        buildApkTaskService.stopBatchBuildApkTask(taskId);
        return R.success(true);
    }

    @ApiOperation("删除总任务")
    @GetMapping("deleteBatchTask/{taskId}")
    public R<Boolean> deleteBatchTask(@PathVariable("taskId") Long taskId) {

        buildApkTaskService.deleteBatchBuildTask(taskId);
        return R.success(true);
    }

    @ApiOperation("停止一个子任务")
    @GetMapping("stopBatchChildTask/{childTaskId}")
    public R<Boolean> stopBatchChildTask(@PathVariable("childTaskId") Long childTaskId) {
        buildApkTaskService.stopBatchChildTask(childTaskId);
        return R.success(true);
    }

    @ApiOperation("重新开始一个子任务")
    @GetMapping("restartBatchChildTask/{childTaskId}")
    public R<Boolean> restartBatchChildTask(@PathVariable("childTaskId") Long childTaskId) {
        buildApkTaskService.restartBatchChildTask(childTaskId);
        return R.success(true);
    }


    @ApiOperation("分页查看批量任务")
    @PostMapping("pageBatchBuildTask")
    public R<IPage<BatchBuildApkTask>> pageBatchBuildTask(@RequestBody PageParams<BatchBuildTaskPage> pageParams) {

        IPage<BatchBuildApkTask> buildPage = pageParams.buildPage();
        Long userId = BaseContextHandler.getUserId();
        BatchBuildTaskPage paramsModel = pageParams.getModel();
        LbqWrapper<BatchBuildApkTask> lbqWrapper = Wraps.<BatchBuildApkTask>lbQ();
        if (paramsModel.getTaskStatus() != null) {
            lbqWrapper.eq(BatchBuildApkTask::getTaskStatus, paramsModel.getTaskStatus());
        }
        if (Objects.nonNull(userId)) {
            lbqWrapper.eq(SuperEntity::getCreateUser, userId);
        }
        lbqWrapper.ne(BatchBuildApkTask::getTaskStatus, BatchBuildTask.DELETE);
        lbqWrapper.orderByDesc(Entity::getUpdateTime);
        IPage<BatchBuildApkTask> buildTask = buildApkTaskService.pageBatchBuildTask(buildPage, lbqWrapper);
        List<BatchBuildApkTask> records = buildTask.getRecords();
        // 统计 每个总任务下的子任务打包情况
        buildApkTaskService.statisticsBuildStatus(records);
        return R.success(buildTask);

    }


    @ApiOperation("分页查看批量的子任务")
    @PostMapping("pageBatchBuildChildTask")
    public R<IPage<BatchBuildTaskChild>> pageBatchBuildChildTask(@RequestBody PageParams<BatchBuildTaskPage> pageParams) {

        IPage<BatchBuildTaskChild> buildPage = pageParams.buildPage();

        BatchBuildTaskPage paramsModel = pageParams.getModel();
        LbqWrapper<BatchBuildTaskChild> lbqWrapper = Wraps.<BatchBuildTaskChild>lbQ();
        lbqWrapper.eq(BatchBuildTaskChild::getBatchBuildApkTaskId, paramsModel.getBatchBuildApkTaskId());
        if (paramsModel.getTaskStatus() != null) {
            lbqWrapper.eq(BatchBuildTaskChild::getTaskStatus, paramsModel.getTaskStatus());
        }
        lbqWrapper.orderByDesc(Entity::getUpdateTime);
        IPage<BatchBuildTaskChild> buildTask = buildApkTaskService.pageBatchBuildChildTask(buildPage, lbqWrapper);
        List<BatchBuildTaskChild> records = buildTask.getRecords();
        buildApkTaskService.setTenantName(records);
        return R.success(buildTask);

    }



}
