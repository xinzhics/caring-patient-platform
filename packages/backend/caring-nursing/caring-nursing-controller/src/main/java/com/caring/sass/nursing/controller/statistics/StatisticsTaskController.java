package com.caring.sass.nursing.controller.statistics;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dto.statistics.StatisticsTaskListDto;
import com.caring.sass.nursing.dto.statistics.StatisticsTaskPageDTO;
import com.caring.sass.nursing.dto.statistics.StatisticsTaskSaveDTO;
import com.caring.sass.nursing.dto.statistics.StatisticsTaskUpdateDTO;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;
import com.caring.sass.nursing.service.statistics.StatisticsMasterService;
import com.caring.sass.nursing.service.statistics.StatisticsTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName StatisticsTaskController
 * @Description
 * @Author yangShuai
 * @Date 2022/4/18 17:05
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/statisticsTask")
@Api(value = "StatisticsTask", tags = "统计任务")
public class StatisticsTaskController extends SuperController<StatisticsTaskService, Long, StatisticsTask, StatisticsTaskPageDTO,
        StatisticsTaskSaveDTO, StatisticsTaskUpdateDTO> {

    @Autowired
    StatisticsMasterService statisticsMasterService;

    @Override
    public R<StatisticsTask> handlerSave(StatisticsTaskSaveDTO model) {

        @NotEmpty String tenantCode = model.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        StatisticsTask statisticsTask = new StatisticsTask();
        BeanUtils.copyProperties(model, statisticsTask);
        baseService.save(statisticsTask, new ArrayList<>());
        return new R(R.SUCCESS_CODE, statisticsTask, "ok", false);
    }

    @Override
    public R<StatisticsTask> handlerUpdate(StatisticsTaskUpdateDTO model) {

        @NotEmpty String tenantCode = model.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        StatisticsTask statisticsTask = new StatisticsTask();
        BeanUtils.copyProperties(model, statisticsTask);
        baseService.update(statisticsTask, model.getStatisticsIntervals());
        return new R(R.SUCCESS_CODE, statisticsTask, "ok", false);
    }


    @ApiOperation("超管端统计任务的列表")
    @GetMapping("statisticsTaskPage/{tenantCode}")
    public R<List<StatisticsTaskListDto>> statisticsTaskPage(@PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        List<StatisticsTaskListDto> list = baseService.statisticsTaskPage();
        return R.success(list);
    }

    @ApiOperation("编辑统计任务时获取详情")
    @GetMapping("getStatisticsInfo/{tenantCode}/{taskId}")
    public R<StatisticsTask> getStatisticsInfo(@PathVariable("tenantCode") String tenantCode ,
                                               @PathVariable("taskId") String taskId) {
        BaseContextHandler.setTenant(tenantCode);
        StatisticsTask statisticsInfo = baseService.getStatisticsInfo(taskId);
        return R.success(statisticsInfo);

    }


    @ApiOperation("删除统计任务已经数据")
    @GetMapping("deleteTaskAll/{tenantCode}/{taskId}")
    public R<String> deleteTaskAll(@PathVariable("tenantCode") String tenantCode ,
                                   @PathVariable("taskId") String taskId) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.removeById(taskId);
        return R.success("success");

    }


    @ApiOperation("创建项目时，初始化统计任务")
    @GetMapping("initTenantDefaultMaster")
    public R<String> initTenantDefaultMaster() {
        statisticsMasterService.initTenantDefaultChart();
        return R.success("success");
    }

    @ApiOperation("复制项目的统计")
    @GetMapping("copyTask")
    public R<String> copyTask(@RequestParam("fromTenantCode") String fromTenantCode,
                              @RequestParam("toTenantCode") String toTenantCode) {

        baseService.copyTask(fromTenantCode, toTenantCode);
        return R.success("success");
    }



    @ApiOperation("修改统计图表显示和隐藏")
    @GetMapping("updateChartShowOrHide/{tenantCode}/{chartId}")
    public R<String> updateChartShowOrHide(@PathVariable("chartId") Long chartId,
                                           @PathVariable("tenantCode") String tenantCode,
                                           @RequestParam("type") String chartType,
                                           @RequestParam("showOrHide") String showOrHide) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.updateChartShowOrHide(chartId, chartType, showOrHide);
        return R.success("success");
    }


    @ApiOperation("修改统计图表宽度")
    @GetMapping("updateChartWidth/{tenantCode}/{chartId}")
    public R<String> updateChartWidth(@PathVariable("chartId") Long chartId,
                                      @PathVariable("tenantCode") String tenantCode,
                                           @RequestParam("type") String chartType,
                                           @RequestParam("chartWidth") Integer chartWidth) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.updateChartChartWidth(chartId, chartType, chartWidth);
        return R.success("success");
    }

    @ApiOperation("修改统计图表排序")
    @GetMapping("updateChartOrder/{tenantCode}/{chartId}")
    public R<String> updateChartOrder(@PathVariable("chartId") Long chartId,
                                      @PathVariable("tenantCode") String tenantCode,
                                      @RequestParam("type") String chartType,
                                      @RequestParam("fromChartOrder") Integer fromChartOrder,
                                      @RequestParam("toChartOrder") Integer toChartOrder) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.updateChartChartOrder(chartId, chartType, fromChartOrder, toChartOrder);
        return R.success("success");
    }


}
