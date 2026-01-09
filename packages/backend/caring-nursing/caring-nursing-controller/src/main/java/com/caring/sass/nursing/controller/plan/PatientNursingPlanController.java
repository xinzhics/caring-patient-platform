package com.caring.sass.nursing.controller.plan;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.nursing.dto.follow.PatientMenuFollow;
import com.caring.sass.nursing.dto.follow.PatientMenuFollowItem;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.dto.plan.PatientNursingPlanPageDTO;
import com.caring.sass.nursing.dto.plan.PatientNursingPlanSaveDTO;
import com.caring.sass.nursing.dto.plan.PatientNursingPlanUpdateDTO;
import com.caring.sass.nursing.dto.plan.SubscribeDTO;
import com.caring.sass.nursing.entity.PatientPlan;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.SugarFormResult;
import com.caring.sass.nursing.entity.plan.PatientNursingPlan;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.form.FormResultService;
import com.caring.sass.nursing.service.form.SugarFormResultService;
import com.caring.sass.nursing.service.plan.PatientNursingPlanService;
import com.caring.sass.nursing.service.plan.PlanService;
import com.caring.sass.nursing.service.task.TaskFacade;
import com.caring.sass.repeat.annotation.RepeatSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * <p>
 * 前端控制器
 * 会员订阅护理计划
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientNursingPlan")
@Api(value = "PatientNursingPlan", tags = "会员订阅护理计划")
//@PreAuth(replace = "patientNursingPlan:")
public class PatientNursingPlanController extends SuperController<PatientNursingPlanService, Long, PatientNursingPlan, PatientNursingPlanPageDTO, PatientNursingPlanSaveDTO, PatientNursingPlanUpdateDTO> {

    @Autowired
    TaskFacade taskFacade;

    @Autowired
    PlanService planService;

    @Autowired
    FormResultService formResultService;

    @Autowired
    SugarFormResultService sugarFormResultService;

    @ApiOperation("查询患者的护理计划")
    @GetMapping({"/getMyNursingPlans/{patientId}"})
    public R<SubscribeDTO> getMyNursingPlans(@PathVariable("patientId") Long patientId) {
        SubscribeDTO subscribeDTO = baseService.getSubscribeList(patientId);

        baseService.setPatientCustomPlanTime(subscribeDTO);
        return R.success(subscribeDTO);
    }

    @RepeatSubmit(message = "正在提交中")
    @SysLog("修改患者的护理计划")
    @ApiOperation("修改患者的护理计划")
    @PostMapping({"/updateMyNursingPlans/{patientId}"})
    public R<Boolean> updateMyNursingPlans(@PathVariable("patientId") Long patientId,
                                           @RequestBody SubscribeDTO subscribeDTO) {
        baseService.updateMyNursingPlans(patientId,subscribeDTO);
        return R.success();
    }

    @ApiOperation("患者单独订阅某护理计划")
    @SysLog("患者单独订阅某护理计划")
    @PutMapping("subscribePlan")
    public R<Boolean> subscribePlan(@RequestParam Long patientId,
                                    @RequestParam(required = false) Long planId,
                                    @RequestParam(required = false) Integer planType) {
        if (planId == null) {
            planId = planService.getPLanIdByPlanType(planType);
        }
        if (planId == null) {
            return R.success();
        }
        baseService.subscribePlan(patientId, planId);
        return R.success();
    }


    @ApiOperation("患者是否订阅计划")
    @GetMapping("patient/subscribe")
    public R<Boolean> getPatientSubscribePlan(@RequestParam Long patientId,
                                              @RequestParam(required = false) Long planId,
                                              @RequestParam(required = false) Integer planType) {
        if (planId == null) {
            planId = planService.getPLanIdByPlanType(planType);
        }
        if (planId == null) {
            return R.success();
        }
        int count = baseService.count(Wraps.<PatientNursingPlan>lbQ()
                .eq(PatientNursingPlan::getIsSubscribe, CommonStatus.YES)
                .eq(PatientNursingPlan::getNursingPlantId, planId)
                .eq(PatientNursingPlan::getPatientId, patientId));
        if (count > 0) {
            return R.success(true);
        } else {
            return R.success(false);
        }
    }

    @ApiOperation("患者取消订阅计划")
    @DeleteMapping("patient/subscribe")
    @SysLog("取消订阅计划")
    public R<Boolean> putPatientSubscribePlan(@RequestParam Long patientId,
                                              @RequestParam(required = false) Long planId,
                                              @RequestParam(required = false) Integer planType) {
        if (planId == null) {
            planId = planService.getPLanIdByPlanType(planType);
        }
        if (planId == null) {
            return R.success();
        }
        baseService.remove(Wraps.<PatientNursingPlan>lbQ()
                .eq(PatientNursingPlan::getIsSubscribe, CommonStatus.YES)
                .eq(PatientNursingPlan::getNursingPlantId, planId)
                .eq(PatientNursingPlan::getPatientId, patientId));
        return R.success();
    }

    @ApiOperation("患者入组后首次默认绑定全部随访")
    @SysLog("患者入组后首次默认绑定全部随访")
    @PutMapping("patientFirstSubscribePlanAll")
    public R<Boolean> patientFirstSubscribePlanAll(@RequestParam Long patientId) {
        baseService.patientFirstSubscribePlanAll(patientId);
        return R.success();
    }


    @ApiOperation("患者随访日历数据4.3")
    @GetMapping("/v_4_3/patientMenuFollow")
    public R<List<PatientMenuFollowItem>> patientMenuFollow(@RequestParam(name = "patientId", required = false) Long patientId) {

        List<PatientMenuFollowItem> followItems = baseService.patientMenuFollow(patientId, true);

        return R.success(followItems);
    }


    @ApiOperation("查询要求注册完成时推送的护理计划。 并直接进行推送")
    @PutMapping("queryRegisterCompletePlanAndPush")
    public R<Boolean> queryRegisterCompletePlanAndPush(@RequestParam("patientId") Long patientId) {

        String tenant = BaseContextHandler.getTenant();
        SaasGlobalThreadPool.execute(() -> baseService.queryRegisterCompletePlanAndPush(tenant, patientId));
        return R.success(true);
    };




    @ApiOperation("患者随访日历数据")
    @Deprecated
    @GetMapping("patientMenuFollow")
    public R<PatientMenuFollow> patientMenuFollow(@RequestParam(name = "patientId", required = false) Long patientId,
                                                   @RequestParam(name = "doctorId", required = false) Long doctorId) {

        PatientMenuFollow patientMenuFollow = baseService.patientMenuFollow(patientId, doctorId);

        return R.success(patientMenuFollow);
    }



    @ApiOperation("获取患者的监测计划列表")
    @GetMapping("patientPlanList/formResult")
    public R<List<PatientPlan>> getPatientPlan(Long patientId) {
        LbqWrapper<Plan> wrapper = Wraps.<Plan>lbQ().eq(Plan::getFollowUpPlanType, "monitoring_data")
                .eq(Plan::getStatus, 1)
                .orderByAsc(SuperEntity::getCreateTime);
        List<Plan> plans = planService.list(wrapper);
        // 遍历查询是否有血糖计划
        SugarFormResult sugarFormResult = null;
        Map<String, FormResult> formResultMap = new HashMap<>();
        List<FormResult> formResults = new ArrayList<>();
        for (Plan plan : plans) {
            if (plan.getPlanType() != null && PlanEnum.BLOOD_SUGAR.getCode().equals(plan.getPlanType())) {
                sugarFormResult = sugarFormResultService.getOne(Wraps.<SugarFormResult>lbQ()
                        .eq(SugarFormResult::getPatientId, patientId)
                        .orderByDesc(SugarFormResult::getCreateDay)
                        .orderByDesc(SugarFormResult::getCreateTime)
                        .last(" limit 0,1 "));
            } else {
                FormResult formResult = formResultService.getOne(Wraps.<FormResult>lbQ()
                        .in(FormResult::getBusinessId, plan.getId().toString())
                        .eq(FormResult::getUserId, patientId)
                        .orderByDesc(FormResult::getCreateTime)
                        .last(" limit 0,1 "));
                if (Objects.nonNull(formResult)) {
                    formResultMap.put(plan.getId().toString(), formResult);
                    if (formResult.getScoreQuestionnaire() != null && formResult.getScoreQuestionnaire().equals(CommonStatus.YES)) {
                        formResults.add(formResult);
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(formResults)) {
            for (FormResult result : formResults) {
                List<FormResult> list = new ArrayList<>();
                list.add(result);
                formResultService.checkFormResultSetScore(list, false);
            }
        }
        List<PatientPlan> patientPlans = new ArrayList<>();
        PatientPlan patientPlan;
        for (Plan plan : plans) {
            patientPlan = new PatientPlan();
            patientPlan.setPlanId(plan.getId());
            patientPlan.setPlanType(plan.getPlanType());
            patientPlan.setName(plan.getName());

            int count = baseService.count(Wraps.<PatientNursingPlan>lbQ()
                    .eq(PatientNursingPlan::getIsSubscribe, CommonStatus.YES)
                    .eq(PatientNursingPlan::getNursingPlantId, plan.getId())
                    .eq(PatientNursingPlan::getPatientId, patientId));
            if (count > 0) {
                patientPlan.setSubscribe(true);
            } else {
                patientPlan.setSubscribe(false);
            }
            if (plan.getPlanType() != null && PlanEnum.BLOOD_SUGAR.getCode().equals(plan.getPlanType())) {
                if (Objects.nonNull(sugarFormResult)) {
                    patientPlan.setSugarValue(sugarFormResult.getSugarValue());
                    patientPlan.setCreateDay(sugarFormResult.getCreateDay());
                    patientPlan.setSugarType(sugarFormResult.getType());
                    patientPlan.setDataFeedBack(sugarFormResult.getDataFeedBack());
                }
            } else {
                FormResult result = formResultMap.get(plan.getId().toString());
                if (Objects.nonNull(result)) {
                    patientPlan.setFormResultId(result.getId());
                    patientPlan.setFormResultCreateTime(result.getCreateTime());
                    if (result.getScoreQuestionnaire() != null && result.getScoreQuestionnaire().equals(CommonStatus.YES)) {
                        patientPlan.setScoreQuestionnaire(result.getScoreQuestionnaire());
                        patientPlan.setShowFormResultSumScore(result.getShowFormResultSumScore());
                        patientPlan.setFormResultSumScore(result.getFormResultSumScore());
                        patientPlan.setShowFormResultAverageScore(result.getShowFormResultAverageScore());
                        patientPlan.setFormResultAverageScore(result.getFormResultAverageScore());
                    } else {
                        patientPlan.setDataFeedBack(result.getDataFeedBack());

                        String jsonContent = result.getJsonContent();
                        List<FormField> tempFormFields = JSONArray.parseArray(jsonContent, FormField.class);
                        List<FormField> formFieldsResult = new ArrayList<>();
                        List<FormField> openDataFeedBackFormField = new ArrayList<>();
                        for (FormField formField : tempFormFields) {
                            Boolean dataFeedback = formField.getShowDataFeedback();
                            if ( formField.getWidgetType().equals(FormWidgetType.NUMBER)) {
                                formFieldsResult.add(formField);
                                if (dataFeedback != null && dataFeedback) {
                                    openDataFeedBackFormField.add(formField);
                                }
                            }
                        }
                        // 如果是血压。直接返回全部数字类型的字段
                        if ((plan.getPlanType() != null && PlanEnum.BLOOD_PRESSURE.getCode().equals(plan.getPlanType()))) {
                            patientPlan.setFormFields(formFieldsResult);
                        } else if (CollUtil.isNotEmpty(openDataFeedBackFormField)) {
                            patientPlan.setFormFields(openDataFeedBackFormField);
                        } else {
                            patientPlan.setFormFields(formFieldsResult);
                        }
                    }
                }
            }
            patientPlans.add(patientPlan);
        }
        return R.success(patientPlans);

    }


}
