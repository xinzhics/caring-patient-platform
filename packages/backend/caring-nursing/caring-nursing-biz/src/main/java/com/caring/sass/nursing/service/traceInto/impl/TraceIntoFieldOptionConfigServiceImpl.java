package com.caring.sass.nursing.service.traceInto.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.plan.PlanDetailMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoFieldOptionConfigMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormOptions;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.dto.traceInto.*;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.entity.plan.PlanDetail;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFieldOptionConfig;
import com.caring.sass.nursing.enumeration.FollowUpPlanTypeEnum;
import com.caring.sass.nursing.enumeration.MonitoringTaskType;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.traceInto.TraceIntoFieldOptionConfigService;
import com.caring.sass.nursing.service.traceInto.TraceIntoResultService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 选项跟踪表单字段选项配置表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Service

public class TraceIntoFieldOptionConfigServiceImpl extends SuperServiceImpl<TraceIntoFieldOptionConfigMapper, TraceIntoFieldOptionConfig> implements TraceIntoFieldOptionConfigService {


    @Autowired
    TraceIntoResultService traceIntoResultService;

    @Autowired
    PlanMapper planMapper;

    @Autowired
    PlanDetailMapper planDetailMapper;

    @Autowired
    FormMapper formMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 查询有跳转表单的 健康日志，复查提醒，自定义随访
     * @return
     */
    public List<PlanFormDTO> getPlanFormList(List<TraceIntoFieldOptionConfig> optionConfigList) {
        List<Plan> planList = planMapper.selectList(Wraps.<Plan>lbQ().in(Plan::getPlanType, PlanEnum.HEALTH_LOG.getCode(), PlanEnum.REVIEW_REMIND.getCode()));
        List<Plan> carePlanList = planMapper.selectList(Wraps.<Plan>lbQ().eq(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.CARE_PLAN.operateType).eq(Plan::getIsAdminTemplate, 1));
        // 确定哪些随访是跳转打开表单的
        Map<Long, Plan> planMap = new HashMap<>();
        List<Long> planIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(planList)) {
            planList.forEach(item -> {planIds.add(item.getId()); planMap.put(item.getId(), item);});
        }
        if (CollUtil.isNotEmpty(carePlanList)) {
            carePlanList.forEach(item -> {planIds.add(item.getId()); planMap.put(item.getId(), item);});
        }
        List<PlanFormDTO> planFormDTOS = new ArrayList<>();
        if (CollUtil.isEmpty(planIds)) {
            return new ArrayList<>();
        }
        List<PlanDetail> planDetails = planDetailMapper.selectList(Wraps.<PlanDetail>lbQ().in(PlanDetail::getNursingPlanId, planIds));
        List<String> planIdsHasForm = new ArrayList<>();
        for (PlanDetail planDetail : planDetails) {
            if (Objects.isNull(planDetail.getPushType()) || planDetail.getPushType().equals(0)) {
                planIdsHasForm.add(planDetail.getNursingPlanId().toString());
            }
        }
        List<Form> formList = formMapper.selectList(Wraps.<Form>lbQ().in(Form::getBusinessId, planIdsHasForm).select(SuperEntity::getId, Form::getBusinessId, Form::getFieldsJson));
        PlanFormDTO healthLog = null;  // 健康日志
        PlanFormDTO reviewRemind = null;   // 复查提醒
        List<PlanFormDTO> otherPlanList = new ArrayList<>();
        PlanFormDTO otherPlan;

        Set<Long> selectPlans = new HashSet<>();
        Set<String> selectFields = new HashSet<>();
        Set<String> selectOptions = new HashSet<>();
        if (CollUtil.isNotEmpty(optionConfigList)) {
            optionConfigList.forEach(item -> {
                selectPlans.add(item.getPlanId());
                selectFields.add(item.getPlanId() + item.getFormFieldId());
                if (item.getIsChildField().equals(1)) {
                    selectFields.add(item.getPlanId() + item.getParentFieldId());
                    selectOptions.add(item.getPlanId() + item.getParentFieldId() + item.getParentFieldOptionId() + item.getFormFieldId() + item.getFieldOptionId());
                } else {
                    selectOptions.add(item.getPlanId() + item.getFormFieldId() + item.getFieldOptionId());
                }
            });
        }
        if (CollUtil.isNotEmpty(formList)) {
            for (Form form : formList) {
                String businessId = form.getBusinessId();
                Plan plan = planMap.get(Long.parseLong(businessId));
                if (Objects.isNull(plan)) {
                    continue;
                }
                if (plan.getPlanType() != null && PlanEnum.HEALTH_LOG.getCode().equals(plan.getPlanType())) {
                    healthLog = new PlanFormDTO();
                    healthLog.setPlanId(plan.getId());
                    healthLog.setPlanName(plan.getName());
                    setFormFieldInfo(healthLog, form, selectPlans, selectFields, selectOptions);
                } else if (plan.getPlanType() != null && PlanEnum.REVIEW_REMIND.getCode().equals(plan.getPlanType())) {
                    reviewRemind = new PlanFormDTO();
                    reviewRemind.setPlanId(plan.getId());
                    reviewRemind.setPlanName(plan.getName());
                    setFormFieldInfo(reviewRemind, form, selectPlans, selectFields, selectOptions);
                } else {
                    otherPlan = new PlanFormDTO();
                    otherPlan.setPlanId(plan.getId());
                    otherPlan.setPlanName(plan.getName());
                    setFormFieldInfo(otherPlan, form, selectPlans, selectFields, selectOptions);
                    otherPlanList.add(otherPlan);
                }
            }
        }
        if (Objects.nonNull(reviewRemind)) {
            planFormDTOS.add(reviewRemind);
        }
        if (Objects.nonNull(healthLog)) {
            planFormDTOS.add(healthLog);
        }
        if (CollUtil.isNotEmpty(otherPlanList)) {
            planFormDTOS.addAll(otherPlanList);
        }
        return planFormDTOS;

    }

    /**
     * 根据字段和选项 封装 超管端可见的 数据
     * @param formFieldDtos
     * @param planId
     * @param formId
     * @param isChild
     * @param parentFormFieldId
     * @param parentFormFieldOptionId
     * @param selectFields
     * @param selectOptions
     * @return
     */
    public List<FormFieldInfo> setFieldInfos(List<FormField> formFieldDtos, Long planId,
                                             Long formId, Boolean isChild,
                                             String parentFormFieldId, String parentFormFieldOptionId,
                                             Set<String> selectFields, Set<String> selectOptions) {
        List<FormFieldInfo> fieldInfoList = new ArrayList<>();
        FormFieldInfo fieldInfo;
        FormFieldOptionInfo optionInfo;
        if (CollUtil.isEmpty(formFieldDtos)) {
            return fieldInfoList;
        }
        for (FormField fieldDto : formFieldDtos) {
            if (FormWidgetType.CHECK_BOX.equals(fieldDto.getWidgetType()) ||
                    FormWidgetType.RADIO.equals(fieldDto.getWidgetType()) ||
                    FormWidgetType.DROPDOWN_SELECT.equals(fieldDto.getWidgetType())) {
                fieldInfo = new FormFieldInfo();
                fieldInfo.setFormId(formId);
                fieldInfo.setPlanId(planId);
                fieldInfo.setFormFieldId(fieldDto.getId());
                fieldInfo.setFormFieldName(fieldDto.getLabel());
                fieldInfo.setIsChildField(isChild ? 1 : 0);
                fieldInfo.setSetting(selectFields.contains(planId + fieldDto.getId()));

                List<FormFieldOptionInfo> optionInfos = new ArrayList<>();
                List<FormOptions> options = fieldDto.getOptions();
                if (CollUtil.isNotEmpty(options)) {
                    for (FormOptions option : options) {
                        optionInfo = new FormFieldOptionInfo();
                        optionInfo.setPlanId(planId);
                        optionInfo.setFormId(formId);
                        optionInfo.setFormFieldId(fieldDto.getId());
                        optionInfo.setFieldOptionId(option.getId());
                        optionInfo.setFieldOptionName(option.getAttrValue());
                        if (isChild) {
                            optionInfo.setParentFieldId(parentFormFieldId);
                            optionInfo.setParentFieldOptionId(parentFormFieldOptionId);
                            optionInfo.setSetting(selectOptions.contains(planId + parentFormFieldId + parentFormFieldOptionId + fieldDto.getId() + option.getId()));
                            optionInfo.setIsChildField(1);
                        } else {
                            optionInfo.setSetting(selectOptions.contains(planId + fieldDto.getId() + option.getId()));
                            optionInfo.setIsChildField(0);
                        }
                        List<FormField> questions = option.getQuestions();
                        if (CollUtil.isNotEmpty(questions)) {
                            List<FormFieldInfo> formFieldInfos = setFieldInfos(questions, planId, formId, true, fieldDto.getId(), option.getId(), selectFields, selectOptions);
                            if (CollUtil.isNotEmpty(formFieldInfos)) {
                                optionInfo.setFieldInfos(formFieldInfos);
                            }
                        }
                        optionInfos.add(optionInfo);
                    }
                }
                fieldInfo.setOptionInfos(optionInfos);
                fieldInfoList.add(fieldInfo);
            }
        }
        return fieldInfoList;
    }

    /**
     * 设置字段问题
     * @param planFormDTO
     * @param form
     */
    public void setFormFieldInfo(PlanFormDTO planFormDTO, Form form, Set<Long> selectPlans, Set<String> selectFields, Set<String> selectOptions) {
        planFormDTO.setFormId(form.getId());
        planFormDTO.setSetting(selectPlans.contains(planFormDTO.getPlanId()));
        String fieldsJson = form.getFieldsJson();
        List<FormField> formFieldDtos = JSONArray.parseArray(fieldsJson, FormField.class);
        List<FormFieldInfo> fieldInfoList = setFieldInfos(formFieldDtos, planFormDTO.getPlanId(), form.getId(), false, null, null, selectFields, selectOptions);
        planFormDTO.setFieldInfos(fieldInfoList);

    }


    /**
     * 查询超管端随访计划并设置字段、选项和选中情况
     * @return
     */
    @Override
    public List<PlanFormDTO> webQueryConfig() {
        List<TraceIntoFieldOptionConfig> optionConfigList = baseMapper.selectList(Wraps.<TraceIntoFieldOptionConfig>lbQ());
        return getPlanFormList(optionConfigList);
    }

    @Override
    public void clearFormAllConfig(Long formId) {
        baseMapper.delete(Wraps.<TraceIntoFieldOptionConfig>lbQ().eq(TraceIntoFieldOptionConfig::getFormId, formId));

    }

    @Override
    public void clearConfig(Form model, String tenant) {
        BaseContextHandler.setTenant(tenant);
        List<TraceIntoFieldOptionConfig> optionConfigList = baseMapper.selectList(Wraps.<TraceIntoFieldOptionConfig>lbQ().eq(TraceIntoFieldOptionConfig::getFormId, model.getId()));
        if (CollUtil.isEmpty(optionConfigList)) {
            return;
        }
        Map<String, TraceIntoFieldOptionConfig> optionConfigMap = new HashMap<>();
        for (TraceIntoFieldOptionConfig optionConfig : optionConfigList) {
            optionConfigMap.put(optionConfig.getFieldOptionId(), optionConfig);
        }
        String fieldsJson = model.getFieldsJson();
        List<FormField> formFields = JSONArray.parseArray(fieldsJson, FormField.class);
        if (CollUtil.isEmpty(formFields)) {
            return;
        }
        clearConfig(formFields, optionConfigMap);
        if (CollUtil.isEmpty(optionConfigMap)) {
            return;
        }
        Collection<TraceIntoFieldOptionConfig> values = optionConfigMap.values();

        List<Long> ids = values.stream().map(SuperEntity::getId).collect(Collectors.toList());
        baseMapper.deleteBatchIds(ids);

        traceIntoResultService.deleteByTraceIntoIds(values, tenant);
    }

    /**
     * 根据表单字段选项 保存 异常选项配置
     * @param formFields
     * @param optionConfigMap
     */
    private void clearConfig(List<FormField> formFields, Map<String, TraceIntoFieldOptionConfig> optionConfigMap) {
        if (CollUtil.isEmpty(formFields)) {
            return;
        }
        for (FormField formField : formFields) {
            List<FormOptions> options = formField.getOptions();
            if (CollUtil.isEmpty(options)) {
                continue;
            }
            for (FormOptions option : options) {
                List<FormField> questions = option.getQuestions();
                if (CollUtil.isNotEmpty(questions)) {
                    clearConfig(questions, optionConfigMap);
                }
                String optionId = option.getId();
                if (StrUtil.isNotBlank(optionId)) {
                    TraceIntoFieldOptionConfig optionConfig = optionConfigMap.get(optionId);
                    if (Objects.nonNull(optionConfig)) {
                        optionConfigMap.remove(optionId);
                    }
                }
            }
        }
    }


    @Transactional
    @Override
    public void updateConfig(List<TraceIntoFieldOptionConfigUpdateDTO> updateDTOList) {
        List<TraceIntoFieldOptionConfig> optionConfigList = baseMapper.selectList(Wraps.<TraceIntoFieldOptionConfig>lbQ());

        Map<String, TraceIntoFieldOptionConfig> oldConfigMap = new HashMap<>();

        String tenant = BaseContextHandler.getTenant();
        if (CollUtil.isNotEmpty(updateDTOList)) {
            for (TraceIntoFieldOptionConfig config : optionConfigList) {
                Long planId = config.getPlanId();
                String formFieldId = config.getFormFieldId();
                String fieldOptionId = config.getFieldOptionId();
                oldConfigMap.put(planId + formFieldId + fieldOptionId, config);
            }

            List<TraceIntoFieldOptionConfig> saveConfig = new ArrayList<>();
            TraceIntoFieldOptionConfig oldConfig;
            TraceIntoFieldOptionConfig optionConfig;
            for (TraceIntoFieldOptionConfigUpdateDTO updateDTO : updateDTOList) {
                 String key = updateDTO.getPlanId() + updateDTO.getFormFieldId() + updateDTO.getFieldOptionId();
                 oldConfig = oldConfigMap.get(key);
                 if (Objects.nonNull(oldConfig)) {
                     oldConfigMap.remove(key);
                 } else {
                     optionConfig = new TraceIntoFieldOptionConfig();
                     BeanUtils.copyProperties(updateDTO, optionConfig);
                     saveConfig.add(optionConfig);
                 }
            }
            if (CollUtil.isNotEmpty(saveConfig)) {
                baseMapper.insertBatchSomeColumn(saveConfig);
            }
            // map 中没有被移除的 就是 不需要的
            Collection<TraceIntoFieldOptionConfig> configs = oldConfigMap.values();
            if (CollUtil.isNotEmpty(configs)) {
                List<Long> ids = configs.stream().map(SuperEntity::getId).collect(Collectors.toList());
                baseMapper.deleteBatchIds(ids);
                // TODO 清除已经生成的异常字段记录
                traceIntoResultService.deleteByTraceIntoIds(configs, tenant);
            }
        } else {
            baseMapper.delete(Wraps.<TraceIntoFieldOptionConfig>lbQ());
            traceIntoResultService.deleteByTraceIntoIds(optionConfigList, tenant);
        }

        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.getTenantTraceIntoSwitch()+":"+tenant);
        if (Objects.nonNull(value) && value.equals(MonitoringTaskType.FINISH.operateType)) {
            redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantTraceIntoSwitch()+":"+tenant, MonitoringTaskType.WAIT.operateType);
        }

    }


    /**
     * 立即生效
     * @param tenantCode
     */
    @Override
    public void traceIntoTask(String tenantCode) {
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantTraceIntoSwitch()+":"+tenantCode, MonitoringTaskType.RUNING.operateType, 2, TimeUnit.HOURS);
        BaseContextHandler.setTenant(tenantCode);
        List<TraceIntoFieldOptionConfig> fieldOptionConfigs = baseMapper.selectList(Wraps.<TraceIntoFieldOptionConfig>lbQ());
        SaasGlobalThreadPool.execute(() -> traceIntoResultService.traceIntoTask(fieldOptionConfigs, tenantCode));
    }


}
