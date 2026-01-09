package com.caring.sass.nursing.controller.plan;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dto.form.CopyPlanDTO;
import com.caring.sass.nursing.dto.plan.*;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.entity.plan.PlanTag;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.plan.PlanDetailService;
import com.caring.sass.nursing.service.plan.PlanService;
import com.caring.sass.nursing.service.plan.PlanTagService;
import com.caring.sass.nursing.service.task.impl.NursingPlanTask;
import com.caring.sass.nursing.util.PlanDict;
import com.caring.sass.utils.SpringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 护理计划（随访服务）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/plan")
@Api(value = "Plan", tags = "护理计划")
public class PlanController extends SuperController<PlanService, Long, Plan, PlanPageDTO, PlanSaveDTO, PlanUpdateDTO> {

    @Autowired
    PlanDetailService planDetailService;

    @Autowired
    PlanTagService planTagService;

    @Autowired
    FormMapper formMapper;


    @ApiOperation("根据类型获取护理计划")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "planType", value = "护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）", dataType = "long", paramType = "query")
    )
    @GetMapping("getPlanByType")
    public R<Plan> getPlanByType(@RequestParam @NotNull Integer planType) {
        List<Plan> plans = baseService.list(Wraps.<Plan>lbQ().eq(Plan::getPlanType, planType));
        if (CollUtil.isNotEmpty(plans)) {
            Plan p = plans.get(0);
            return R.success(p);
        }
        return R.success(new Plan());
    }

    @ApiOperation("获取护理计划的详情")
    @GetMapping("/planDetail/{planId}")
    public R<PlanDTO> planDetail(@PathVariable("planId") Long planId) {
        Plan plan = baseService.getById(planId);
        PlanDTO planDTO = new PlanDTO();
        BeanUtils.copyProperties(plan, planDTO);
        List<PlanDetailDTO> detailDTOList = this.planDetailService.findDetailWithTimeById(planId);
        planDTO.setPlanDetailDTOList(detailDTOList);
        PlanTag planTag = planTagService.getByPlanId(planId);
        planDTO.setTagId(planTag.getTagId());
        return R.success(planDTO);
    }


    @ApiOperation("创建项目时， 初始化护理计划")
    @GetMapping("initPlan")
    public R<Boolean> createProjectInitPlan() {
        baseService.initPlan();
        return R.success(true);
    }

    @ApiOperation("根据随访计划类型获取监测计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "admin 时 返回系统模板， 二级项目租户时 返回计划", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "followUpPlanType", value = "随访计划类型  护理计划 care_plan， 监测数据 monitoring_data", dataType = "String", paramType = "path")
    })
    @GetMapping("/monitoringDataPlan/{tenantCode}/{followUpPlanType}")
    public R<List<Plan>> getMonitoringDataPlan(@PathVariable("tenantCode") String tenantCode,
                                  @PathVariable(value = "followUpPlanType") String followUpPlanType) {
        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<Plan> wrapper = Wraps.<Plan>lbQ().eq(Plan::getFollowUpPlanType, followUpPlanType)
                .orderByAsc(SuperEntity::getCreateTime);
        List<Plan> plans = baseService.list(wrapper);
        return R.success(plans);

    }

    @ApiOperation("查询文件夹的分享链接被用在那个项目")
    @GetMapping({"/checkFolderShareUrlExist"})
    public R<List<String>> checkFolderShareUrlExist(@RequestParam("url") String url) {

        List<String> tenantCodes = baseService.checkFolderShareUrlExist(url);
        return R.success(tenantCodes);

    }

    @ApiOperation("更换两个计划的创建时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "admin 时 返回系统模板， 二级项目租户时 返回计划", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "firstPLanId", value = "监测计划ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "toPLanId", value = "要交换的监测计划ID", dataType = "Long", paramType = "query"),
    })
    @GetMapping("/changePlanCreateTime/{tenantCode}")
    public R<Boolean> changePlanCreateTime(@PathVariable("tenantCode") String tenantCode,
                                              @RequestParam("firstPLanId") Long firstPLanId,
                                              @RequestParam("toPLanId") Long toPLanId) {
        BaseContextHandler.setTenant(tenantCode);
        Plan firstPlan = baseService.getById(firstPLanId);
        Plan toPLan = baseService.getById(toPLanId);

        LocalDateTime tempCreate = toPLan.getCreateTime();
        if (tempCreate.equals(firstPlan.getCreateTime())) {
            tempCreate = tempCreate.plusSeconds(1);
        }
        toPLan.setCreateTime(firstPlan.getCreateTime());
        firstPlan.setCreateTime(tempCreate);
        baseService.updateById(toPLan);
        baseService.updateById(firstPlan);
        return R.success(true);

    }

    @ApiOperation("患者端监测计划列表")
    @GetMapping("/PatientMonitoringDataPlan")
    public R<List<Plan>> getPatientMonitoringDataPlan() {
        LbqWrapper<Plan> wrapper = Wraps.<Plan>lbQ().eq(Plan::getFollowUpPlanType, "monitoring_data")
                .eq(Plan::getStatus, 1)
                .orderByAsc(SuperEntity::getCreateTime);
        List<Plan> plans = baseService.list(wrapper);
        return R.success(plans);

    }


    @ApiOperation("项目端查看患者疾病信息中监测计划")
    @GetMapping("/tenantMonitoringDataPlan")
    public R<List<Plan>> getTenantMonitoringDataPlan() {
        LbqWrapper<Plan> wrapper = Wraps.<Plan>lbQ().eq(Plan::getFollowUpPlanType, "monitoring_data")
                .orderByAsc(SuperEntity::getCreateTime);
        List<Plan> plans = baseService.list(wrapper);
        return R.success(plans);

    }

    @ApiOperation("更新护理计划状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "planId", value = "护理计划id", dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "status", value = "开启状态（1 开启， 0 关闭）", dataType = "integer", paramType = "query")
    })
    @GetMapping("/updateStatus/{tenantCode}/{planId}")
    public R<String> updateStatus(@PathVariable("tenantCode") String tenantCode,
                                  @PathVariable(value = "planId") Long planId,
                                  @RequestParam(value = "status") Integer status) {
        BaseContextHandler.setTenant(tenantCode);
        if (status == 1 || status == 0) {
            baseService.updateStatus(planId, status);
            return R.success("修改成功");
        } else {
            return R.fail("状态参数不正确");
        }
    }

    @ApiOperation("获取护理计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "二级项目租户", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "isAdminTemplate", value = "0 系统计划， 1 自定义计划", dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "followUpPlanType", value = "随访计划类型  护理计划 care_plan， 监测数据 monitoring_data", dataType = "String", paramType = "query")
    })
    @GetMapping("/getPlan/{tenantCode}/{isAdminTemplate}")
    public R<List<Plan>> getPlan(@PathVariable("tenantCode") String tenantCode,
                                 @PathVariable("isAdminTemplate") Integer isAdminTemplate,
                                 @RequestParam(value = "followUpPlanType", required = false) String followUpPlanType) {

        BaseContextHandler.setTenant(tenantCode);
        LbqWrapper<Plan> wrapper = Wraps.<Plan>lbQ()
                .eq(Plan::getIsAdminTemplate, isAdminTemplate);
        if (StringUtils.isNotEmptyString(followUpPlanType)) {
            wrapper.eq(Plan::getFollowUpPlanType, followUpPlanType);
        }
        List<Plan> planList = baseService.list(wrapper);
        return R.success(planList);
    }


    @ApiOperation("保存计划作为系统模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "二级项目租户", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "plan", value = "护理计划", dataType = "Plan", paramType = "body")
    })
    @PostMapping("/saveSystemTemplatePlan/{tenantCode}")
    public R<Plan> saveSystemTemplatePlan(@PathVariable("tenantCode") String tenantCode,
            @RequestBody Plan plan) {
        BaseContextHandler.setTenant(tenantCode);
        Long planId = plan.getId();
        plan.setCreateTime(LocalDateTime.now());
        List<Form> formList = formMapper.selectList(Wraps.<Form>lbQ().eq(Form::getBusinessId, planId.toString()));

        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        plan.setSystemTemplate(1);
        plan.setStatus(0);
        baseService.saveSystemTemplatePlan(plan);
        if (CollUtil.isNotEmpty(formList)) {
            for (Form form : formList) {
                form.setId(null);
                form.setBusinessId(plan.getId().toString());
                formMapper.insert(form);
            }
        }

        return R.success(plan);
    }


    @ApiOperation("复制系统计划模板到项目中")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "二级项目租户", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "planId", value = "系统护理计划ID", dataType = "Integer", paramType = "path")
    })
    @GetMapping("/copySystemTemplatePlan/{tenantCode}/{planId}")
    public R<Plan> copySystemTemplatePlan(@PathVariable("tenantCode") String tenantCode,
                                 @PathVariable("planId") Long planId) {

        BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
        Plan plan = baseService.getById(planId);
        List<PlanDetailDTO> detailDTOList = this.planDetailService.findDetailWithTimeById(planId);
        plan.setPlanDetailList(detailDTOList);
        List<Form> formList = formMapper.selectList(Wraps.<Form>lbQ().eq(Form::getBusinessId, planId.toString()));

        BaseContextHandler.setTenant(tenantCode);
        plan.setSystemTemplate(0);
        plan.setStatus(0);
        plan.setCreateTime(LocalDateTime.now());
        baseService.copySystemTemplatePlan(plan);
        if (CollUtil.isNotEmpty(formList)) {
            for (Form form : formList) {
                form.setId(null);
                form.setBusinessId(plan.getId().toString());
                formMapper.insert(form);
            }
        }
        return R.success(plan);
    }


    @ApiOperation("获取有表单自定义护理计划")
    @GetMapping("/hasForm/plan")
    public R<List<Plan>> getPlan(@RequestParam(value = "tenantCode", required = false) String tenantCode) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        List<Plan> planList = baseService.getPlan(1, PlanDict.CARE_PLAN);
        // 对护理计划进行过滤。将 没有表单的护理计划移除
        if (CollUtil.isEmpty(planList)) {
            return R.success(planList);
        }

        List<String> planIds = new ArrayList<>();
        for (Plan plan : planList) {
            planIds.add(plan.getId().toString());
        }
        List<Form> formList = formMapper.selectList(Wraps.<Form>lbQ().in(Form::getBusinessId, planIds).select(Form::getBusinessId));
        Set<String> formBusinessId = formList.stream().map(Form::getBusinessId).collect(Collectors.toSet());
        List<Plan> returnPlan = new ArrayList<>(planList.size());
        for (Plan plan : planList) {
            if (formBusinessId.contains(plan.getId().toString())) {
                returnPlan.add(plan);
            }
        }
        return R.success(returnPlan);
    }

    @ApiOperation("获取护理计划和护理计划的详情")
    @GetMapping("/getPlanAndDetail/{tenantCode}/{planId}")
    public R<Plan> getPlanAndDetail(@PathVariable("tenantCode") String tenantCode,
                                    @PathVariable("planId") Long planId) {

        BaseContextHandler.setTenant(tenantCode);
        Plan plan = baseService.getOne(planId);
        return R.success(plan);
    }

    @ApiOperation("将当前护理计划恢复")
    @GetMapping("/reset/plan/{tenantCode}/{planId}")
    public R<Plan> resetPlan(@PathVariable("tenantCode") String tenantCode, @PathVariable("planId") Long planId) {
        BaseContextHandler.setTenant(tenantCode);
        Boolean resetPlan = baseService.resetPlan(planId);
        if (resetPlan) {
            Plan plan = baseService.getOne(planId);
            return R.success(plan);
        } else {
            return R.fail("重置失败");
        }
    }

    @ApiOperation("删除租户下的计划")
    @DeleteMapping("/delete/plan/{tenantCode}/{planId}")
    public R<Boolean> deletePlan(@PathVariable("tenantCode") String tenantCode, @PathVariable("planId") Long planId) {

        BaseContextHandler.setTenant(tenantCode);
        baseService.removeById(planId);
        return R.success(true);
    }



    @ApiOperation("新增或更新一个护理计划的设置")
    @PostMapping("/update/plan/{tenantCode}")
    public R<Plan> updatePlan(@PathVariable("tenantCode") String tenantCode,
                              @RequestBody Plan plan) {
        baseService.checkPlanAll(plan);
        BaseContextHandler.setTenant(tenantCode);
        Boolean updatePlan = baseService.updatePlan(plan);
        return R.success(plan);
    }


    @ApiOperation("复制护理计划")
    @PostMapping("copyPlan")
    public R<Map<Long, Long>> copyPlan(@RequestBody @Validated CopyPlanDTO copyPlanDTO) {
        Map<Long, Long> planIdMaps = baseService.copyPlan(copyPlanDTO);
        return R.success(planIdMaps);
    }

    @ApiOperation("导出随访记录列表")
    @GetMapping("exportFollowPlan")
    public R<List<Plan>> exportFollowPlan() {
        List<Plan> list = baseService.exportFollowPlan();
        return R.success(list);
    }



    @GetMapping("test1")
    @ApiOperation("测试随访任务创建")
    public R<Void> test1(@RequestParam PlanEnum planEnum) {

        NursingPlanTask bean = SpringUtils.getBean(NursingPlanTask.class);
        String tenantCode = "0112";
        LocalDateTime now = LocalDateTime.now();
        bean.nursingPlanPush(tenantCode, planEnum, now);
        return R.success(null);
    }


}
