package com.caring.sass.nursing.controller.follow;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dto.follow.*;
import com.caring.sass.nursing.entity.follow.FollowTask;
import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import com.caring.sass.nursing.service.follow.FollowTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 随访任务
 * </p>
 *
 * @author 杨帅
 * @date 2023-01-04
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/followTask")
@Api(value = "FollowTask", tags = "随访任务")
//@PreAuth(replace = "followTask:")
public class FollowTaskController extends SuperController<FollowTaskService, Long, FollowTask, FollowTaskPageDTO, FollowTaskSaveDTO, FollowTaskUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FollowTask> followTaskList = list.stream().map((map) -> {
            FollowTask followTask = FollowTask.builder().build();
            //TODO 请在这里完成转换
            return followTask;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(followTaskList));
    }


    @ApiOperation("PC端获取项目的随访任务设置")
    @GetMapping("getFollowTask")
    public R<FollowTaskUpdateDTO> getFollowTask(@RequestParam("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        FollowTaskUpdateDTO followTask = baseService.getFollowTask();
        return R.success(followTask);
    }


    @Override
    public R<FollowTask> handlerUpdate(FollowTaskUpdateDTO model) {
        String tenantCode = model.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        List<FollowTaskContent> contentList = model.getContentList();
        FollowTask followTask = BeanUtil.toBean(model, FollowTask.class);

        baseService.updateById(followTask, contentList);
        return R.success(followTask);
    }

    @ApiOperation("pc查询随访任务的简介")
    @GetMapping("pcFindFollowBriefIntroduction/{tenantCode}")
    public R<FollowBriefIntroductionDTO> pcFindFollowBriefIntroduction(@PathVariable("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        FollowBriefIntroductionDTO briefIntroductionDTO = baseService.findFollowBriefIntroduction();
        return R.success(briefIntroductionDTO);
    }

    @ApiOperation("【APP】 查询随访任务的简介")
    @GetMapping("appFindFollowBriefIntroduction")
    public R<FollowBriefIntroductionDTO> appFindFollowBriefIntroduction(@RequestParam(required = false) String tagIds) {

        List<Long> tagIdList = StringUtils.splitToList(tagIds, ",");
        FollowBriefIntroductionDTO briefIntroductionDTO = baseService.appFindFollowBriefIntroduction(tagIdList);
        return R.success(briefIntroductionDTO);
    }


    @ApiOperation("【APP】 查询全部随访任务的计划安排")
    @ApiImplicitParam(name = "currentPage", value = "页码 从 1 开始")
    @GetMapping("findFollowPlanAll")
    public R<FollowAllPlanDTO> findFollowPlan(@RequestParam("currentPage") Integer currentPage, @RequestParam(required = false) String tagIds) {

        List<Long> tagIdList = StringUtils.splitToList(tagIds, ",");
        FollowAllPlanDTO allPlanDTO = baseService.findFollowPlan(currentPage, tagIdList);
        return R.success(allPlanDTO);

    }

    @ApiOperation("【APP】 查询学习计划")
    @GetMapping("findLearnPlan")
    public R<List<FollowPlanShowDTO>> findLearnPlan(@RequestParam(required = false) String tagIds) {

        List<Long> tagIdList = StringUtils.splitToList(tagIds, ",");
        List<FollowPlanShowDTO> followPlan = baseService.findLearnPlan(tagIdList);
        return R.success(followPlan);


    }


    @ApiOperation("【APP】 查询其他计划")
    @GetMapping("findOtherPlan")
    public R<FollowPlanShowDTO> findOtherPlan(@RequestParam("planId") Long planId, @RequestParam("currentPage") Integer currentPage) {

        FollowPlanShowDTO followPlan = baseService.findOtherPlan(planId, currentPage);
        return R.success(followPlan);


    }


    @ApiOperation("修改随访计划中内容的展示状态")
    @PutMapping("updateFollowContentShowStatus")
    public R<Void> updateFollowContentShowStatus(@RequestBody FollowTaskContentUpdateDTO followTaskContentUpdateDTO) {
        String tenantCode = followTaskContentUpdateDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        baseService.updateFollowContentShowStatus(followTaskContentUpdateDTO);
        return success(null);

    }

    @ApiOperation("【APP】查询随访统计的详细")
    @GetMapping("queryFollowCountDetail")
    public R<FollowCountDetailDTO> queryFollowCountDetail() {

        FollowCountDetailDTO countDetailDTO = baseService.queryFollowCountDetail();
        return R.success(countDetailDTO);

    }


    @ApiOperation("【APP】查询非学习计划的栏目的统计数据")
    @GetMapping("queryCountOtherPush")
    public R<FollowCountOtherPlanDTO> queryCountOtherPush(Long followTaskContentId) {

        FollowCountOtherPlanDTO dto = baseService.queryCountOtherPush(followTaskContentId);
        return R.success(dto);

    }

    @ApiOperation("【APP】查询学习计划栏目下文章的统计数据")
    @GetMapping("queryCountLearnPlanPush")
    public R<List<FollowCountLearnPlanDto>> queryCountLearnPlanPush() {

        List<FollowCountLearnPlanDto> list = baseService.queryCountLearnPlanPush();
        return R.success(list);

    }


    @ApiOperation("【患者端】查询随访信息简介")
    @GetMapping("patientQueryFollowUpInfo/{patientId}")
    public R<FollowBriefIntroductionDTO> patientQueryFollowUpInfo(@PathVariable Long patientId) {

        FollowBriefIntroductionDTO followBriefIntroductionDTO =  baseService.patientFindFollowBriefIntroduction(patientId);
        return R.success(followBriefIntroductionDTO);
    }


    @ApiOperation("【患者端】查询 随访的 待执行计划")
    @ApiImplicitParam(name = "planId", value = "护理计划ID, 不传则是全部")
    @GetMapping("patientQueryFollowPlanUnExecuted/{patientId}")
    public R<List<FollowAllExecutionCycleDTO>> patientQueryFollowPlanUnExecuted(@PathVariable Long patientId,
                                                                      @RequestParam(required = false) Long planId) {

        List<FollowAllExecutionCycleDTO> followAllExecutionCycleDTOS = baseService.patientQueryFollowPlanUnExecuted(patientId, planId);
        return R.success(followAllExecutionCycleDTOS);
    }



    @ApiOperation("【患者端】查询 随访的 已执行计划")
    @ApiImplicitParam(name = "planId", value = "护理计划ID, 不传则是全部")
    @GetMapping("patientQueryFollowPlanExecuted/{patientId}")
    public R<List<FollowAllExecutionCycleDTO>> patientQueryFollowPlanExecuted(@PathVariable Long patientId,
                                                                      @RequestParam(required = false) Long planId) {

        List<FollowAllExecutionCycleDTO> dtoList = baseService.patientQueryFollowPlanExecuted(patientId, planId);
        return R.success(dtoList);
    }


    @ApiOperation("【患者端】查询 学习计划  执行计划")
    @GetMapping("patientQueryLearnPlan/{patientId}")
    public R<List<FollowPlanShowDTO>> patientQueryLearnPlan(@PathVariable Long patientId) {

        List<FollowPlanShowDTO> showDTOS = baseService.patientQueryLearnPlan(patientId);

        return R.success(showDTOS);
    }

    @ApiOperation("【患者端】查询 随访的 日历")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "learnPlan", value = "是否为学习计划, 学习计划传1 否则不穿"),
            @ApiImplicitParam(name = "planId", value = "护理计划Id"),
            @ApiImplicitParam(name = "queryMonthly", value = "日历所在月份的第一天")
    })
    @GetMapping("patientQueryFollowPlanCalendar/{patientId}")
    public R<Set<LocalDate>> patientQueryFollowPlanCalendar(@PathVariable Long patientId,
                                              @RequestParam(required = false) Long planId,
                                              @RequestParam(required = false) Integer learnPlan,
                                              @RequestParam LocalDate queryMonthly) {

        Set<LocalDate> calendar = baseService.patientQueryFollowPlanCalendar(patientId, planId, learnPlan, queryMonthly);
        return R.success(calendar);
    }


    @ApiOperation("【患者端】查询 日历 某天的随访任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "learnPlan", value = "是否为学习计划, 学习计划传1 否则不穿"),
            @ApiImplicitParam(name = "planId", value = "护理计划Id"),
            @ApiImplicitParam(name = "queryMonthly", value = "选择的日期2023-03-02")
    })
    @GetMapping("patientQueryFollowPlanCalendarDayPlanDetail/{patientId}")
    public R<FollowAllExecutionCycleDTO> patientQueryFollowPlanCalendarDayPlanDetail(@PathVariable Long patientId,
                                                                                     @RequestParam(required = false) Long planId,
                                                                                     @RequestParam(required = false) Integer learnPlan,
                                                                                     @RequestParam LocalDate queryMonthly) {
        FollowAllExecutionCycleDTO followAllExecutionCycleDTO = baseService.patientQueryFollowPlanCalendarDayPlanDetail(patientId, planId, learnPlan, queryMonthly);
        return R.success(followAllExecutionCycleDTO);
    }

    @ApiOperation("【患者端】 查询随访统计的简介")
    @GetMapping("patientQueryFollowCountDetail")
    public R<FollowCountDetailDTO> patientQueryFollowCountDetail(@RequestParam Long patientId) {

        FollowCountDetailDTO detailDTO = baseService.patientQueryFollowCountDetail(patientId);
        return R.success(detailDTO);

    }

    @ApiOperation("【患者端】 统计学习计划推送阅读情况")
    @GetMapping("patientLearnPlanStatistics/{patientId}")
    public R<List<PatientFollowLearnPlanStatisticsDTO>> patientLearnPlanStatistics(@PathVariable Long patientId) {


        List<PatientFollowLearnPlanStatisticsDTO> dtoList = baseService.patientLearnPlanStatistics(patientId);

        return R.success(dtoList);
    }


    @ApiOperation("【患者端】 统计其他计划的推送打卡情况")
    @ApiImplicitParam(name = "planId", value = "planId")
    @GetMapping("patientFollowPlanStatistics/{patientId}")
    public R<FollowCountOtherPlanDTO> patientFollowPlanStatistics(@PathVariable Long patientId,
                                                                  @RequestParam Long planId) {

        FollowCountOtherPlanDTO followCountOtherPlanDTO = baseService.patientFollowPlanStatistics(patientId, planId);
        return R.success(followCountOtherPlanDTO);
    }


    @ApiOperation("【医生端】 查询随访任务的简介")
    @GetMapping("doctorFindFollowBriefIntroduction")
    public R<FollowBriefIntroductionDTO> doctorFindFollowBriefIntroduction(@RequestParam(required = false) String tagIds) {

        List<Long> tagIdList = StringUtils.splitToList(tagIds, ",");
        FollowBriefIntroductionDTO briefIntroductionDTO = baseService.doctorFindFollowBriefIntroduction(tagIdList);
        return R.success(briefIntroductionDTO);
    }

    @ApiOperation("【医生端】 查询全部随访任务的计划安排")
    @ApiImplicitParam(name = "currentPage", value = "页码 从 1 开始")
    @GetMapping("doctorFindFollowPlan")
    public R<FollowAllPlanDTO> doctorFindFollowPlan(@RequestParam("currentPage") Integer currentPage,
                                              @RequestParam(required = false) String tagIds) {

        List<Long> tagIdList = StringUtils.splitToList(tagIds, ",");
        FollowAllPlanDTO allPlanDTO = baseService.findFollowPlan(currentPage, tagIdList);
        return R.success(allPlanDTO);

    }

    @ApiOperation("【医生端】 查询学习计划")
    @GetMapping("doctorFindLearnPlan")
    public R<List<FollowPlanShowDTO>> doctorFindLearnPlan(@RequestParam(required = false) String tagIds) {

        List<Long> tagIdList = StringUtils.splitToList(tagIds, ",");
        List<FollowPlanShowDTO> followPlan = baseService.findLearnPlan(tagIdList);
        return R.success(followPlan);

    }


    @ApiOperation("【医生端】 查询其他计划")
    @GetMapping("doctorFindOtherPlan")
    public R<FollowPlanShowDTO> doctorFindOtherPlan(@RequestParam("planId") Long planId, @RequestParam("currentPage") Integer currentPage) {

        FollowPlanShowDTO followPlan = baseService.findOtherPlan(planId, currentPage);
        return R.success(followPlan);

    }


    @ApiOperation("【医生端】查询随访统计的详细")
    @GetMapping("doctorQueryFollowCountDetail")
    public R<FollowCountDetailDTO> doctorQueryFollowCountDetail() {

        FollowCountDetailDTO countDetailDTO = baseService.queryFollowCountDetail();
        return R.success(countDetailDTO);

    }


    @ApiOperation("【医生端】查询非学习计划的栏目的统计数据")
    @GetMapping("doctorQueryCountOtherPush")
    public R<FollowCountOtherPlanDTO> doctorQueryCountOtherPush(Long followTaskContentId) {

        FollowCountOtherPlanDTO dto = baseService.doctorQueryCountOtherPush(followTaskContentId);
        return R.success(dto);

    }

    @ApiOperation("【医生端】查询学习计划栏目下文章的统计数据")
    @GetMapping("doctorQueryCountLearnPlanPush")
    public R<List<FollowCountLearnPlanDto>> doctorQueryCountLearnPlanPush() {

        List<FollowCountLearnPlanDto> list = baseService.doctorQueryCountLearnPlanPush();
        return R.success(list);

    }

}
