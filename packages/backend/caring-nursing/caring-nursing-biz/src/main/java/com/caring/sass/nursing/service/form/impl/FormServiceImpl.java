package com.caring.sass.nursing.service.form.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.nursing.constant.TagAttrFieldType;
import com.caring.sass.nursing.dao.form.*;
import com.caring.sass.nursing.dao.information.InformationIntegrityMonitoringMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormScoreRuleUpdateDTO;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.entity.form.*;
import com.caring.sass.nursing.entity.information.InformationIntegrityMonitoring;
import com.caring.sass.nursing.entity.information.PatientInformationField;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.form.FormResultBackUpService;
import com.caring.sass.nursing.service.form.FormService;
import com.caring.sass.nursing.service.form.PatientFormFieldReferenceService;
import com.caring.sass.nursing.service.information.CompletenessInformationService;
import com.caring.sass.nursing.service.information.PatientInformationFieldService;
import com.caring.sass.nursing.service.traceInto.TraceIntoFieldOptionConfigService;
import com.caring.sass.nursing.service.traceInto.TraceIntoResultService;
import com.caring.sass.nursing.util.FormUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 自定义表单表
 * </p>
 * 对系统的表单增加redis缓存处理。
 * 基本信息， 疾病信息， 健康日志， 复查提醒， 自定义监测计划， 自定义随访的表单。
 * 基本信息和疾病信息使用 tenantCode:类型 缓存
 * 其他表单使用 tenantCode:planId 缓存
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class FormServiceImpl extends SuperServiceImpl<FormMapper, Form> implements FormService {

    private final FormResultMerge formResultMerge;

    private final PlanMapper planMapper;

    @Autowired
    FormResultMapper formResultMapper;

    @Autowired
    FormResultBackUpService formResultBackUpService;

    @Autowired
    PatientFormFieldReferenceService patientFormFieldReferenceService;

    @Autowired
    IndicatorsConditionMonitoringMapper indicatorsConditionMonitoringMapper;

    @Autowired
    IndicatorsResultInformationMapper indicatorsResultInformationMapper;

    @Autowired
    InformationIntegrityMonitoringMapper informationIntegrityMonitoringMapper;

    @Autowired
    PatientInformationFieldService patientInformationFieldService;

    @Order(10)
    @Lazy
    @Autowired
    CompletenessInformationService completenessInformationService;

    @Autowired
    TraceIntoResultService traceIntoResultService;

    @Autowired
    TraceIntoFieldOptionConfigService traceIntoFieldOptionConfigService;

    @Autowired
    FormScoreRuleMapper formScoreRuleMapper;

    @Autowired
    FormFieldsGroupMapper formFieldsGroupMapper;

    @Autowired
    FormResultScoreMapper formResultScoreMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final DatabaseProperties databaseProperties;

    public FormServiceImpl(FormResultMerge formResultMerge, PlanMapper planMapper, DatabaseProperties databaseProperties) {
        this.formResultMerge = formResultMerge;
        this.planMapper = planMapper;
        this.databaseProperties = databaseProperties;
    }


    /**
     * 通过分类或者关联的计划ID查询表单
     * 优先从redis的缓存中去获取
     * @param formEnum
     * @param businessId
     * @return
     */
    @Override
    public Form getFormByRedis(FormEnum formEnum, String businessId) {

        String tenant = BaseContextHandler.getTenant();
        BoundHashOperations operations = redisTemplate.boundHashOps(SaasRedisBusinessKey.SYSTEM_FORM_REDIS_TENANT + tenant);
        Object formObj;
        if (Objects.nonNull(formEnum) && (FormEnum.BASE_INFO.equals(formEnum) || FormEnum.HEALTH_RECORD.equals(formEnum))) {
            formObj = operations.get(formEnum.toString());
            if (Objects.isNull(formObj)) {
                Form form = baseMapper.selectOne(Wraps.<Form>lbQ().eq(Form::getCategory, formEnum).last(" limit 0,1 "));
                if (Objects.nonNull(form)) {
                    operations.put(formEnum.toString(), JSON.toJSONString(form));
                }
                return form;
            } else {
                return JSON.parseObject(formObj.toString(), Form.class);
            }
        } else if (StrUtil.isNotEmpty(businessId)) {
            formObj = operations.get(businessId);
            if (Objects.isNull(formObj)) {
                Form form = baseMapper.selectOne(Wraps.<Form>lbQ().eq(Form::getBusinessId, businessId).last(" limit 0,1 "));
                if (Objects.nonNull(form)) {
                    operations.put(businessId, JSON.toJSONString(form));
                }
                return form;
            } else {
                return JSON.parseObject(formObj.toString(), Form.class);
            }
        } else {
            return null;
        }

    }

    /**
     * 将表单从redis中清除
     * @param businessId
     */
    public void deleteFormToRedis(String businessId) {
        String tenant = BaseContextHandler.getTenant();
        BoundHashOperations operations = redisTemplate.boundHashOps(SaasRedisBusinessKey.SYSTEM_FORM_REDIS_TENANT + tenant);
        operations.delete(businessId);
    }

    /**
     * 保存或者更新表单的redis缓存
     * @param model
     */
    public void saveOrUpdateFormRedis(Form model) {
        String tenant = BaseContextHandler.getTenant();
        BoundHashOperations operations = redisTemplate.boundHashOps(SaasRedisBusinessKey.SYSTEM_FORM_REDIS_TENANT + tenant);
        if (Objects.nonNull(model.getCategory()) && (FormEnum.BASE_INFO.equals(model.getCategory()) || FormEnum.HEALTH_RECORD.equals(model.getCategory()))) {
            operations.delete(model.getCategory().toString());
            operations.put(model.getCategory().toString(), JSON.toJSONString(model));
        } else {
            operations.delete(model.getBusinessId());
            operations.put(model.getBusinessId(), JSON.toJSONString(model));
        }
    }

    /**
     * 保存的时候增加redis缓存
     * @param model
     * @return
     */
    @Override
    protected R<Boolean> handlerSave(Form model) {
        String fieldsJson = model.getFieldsJson();
        if (StrUtil.isNotEmpty(fieldsJson)) {
            model.setFieldsJson(FormUtil.reWriteFormField(fieldsJson));
            model.setScoreQuestionnaire(FormUtil.isScoreQuestionnaire(fieldsJson));
        }
        baseMapper.insert(model);
        saveOrUpdateFormRedis(model);
        FormScoreRuleUpdateDTO updateDTO = model.getFormScoreRuleUpdateDTO();
        if (Objects.nonNull(updateDTO)) {
            FormScoreRule scoreRule = new FormScoreRule();
            BeanUtils.copyProperties(updateDTO, scoreRule);
            scoreRule.setFormId(model.getId());
            formScoreRuleMapper.insert(scoreRule);
        }
        List<Long> groupIds = model.getDeleteFieldGroupIds();
        if (CollUtil.isNotEmpty(groupIds)) {
            formFieldsGroupMapper.deleteBatchIds(groupIds);
        }

        saveDailySubmissionLimit(model.getId(), model.getDailySubmissionLimit());
        return R.success();
    }

    /**
     * 保存表单每日提交上限到redis
     */
    public void saveDailySubmissionLimit(Long formId, Integer dailySubmissionLimit) {
        String tenant = BaseContextHandler.getTenant();
        if (dailySubmissionLimit != null) {
            redisTemplate.opsForHash().delete(SaasRedisBusinessKey.FORM_DAILY_SUBMISSION_LIMIT + tenant, formId.toString());
            redisTemplate.opsForHash().put(SaasRedisBusinessKey.FORM_DAILY_SUBMISSION_LIMIT + tenant, formId.toString(), dailySubmissionLimit.toString());
        }
    }

    /**
     * 查询表单的每日提交上限次数
     * @param formId
     * @return
     */
    @Override
    public Integer queryFormDailySubmissionLimit(Long formId) {
        String tenant = BaseContextHandler.getTenant();
        Object o = redisTemplate.opsForHash().get(SaasRedisBusinessKey.FORM_DAILY_SUBMISSION_LIMIT + tenant, formId.toString());
        Integer dailySubmissionLimit;
        if (Objects.nonNull(o)) {
            dailySubmissionLimit = Integer.parseInt(o.toString());
        } else {
            Form form = baseMapper.selectOne(Wraps.<Form>lbQ().eq(SuperEntity::getId, formId).select(SuperEntity::getId, Form::getDailySubmissionLimit));
            dailySubmissionLimit = form.getDailySubmissionLimit();
            if (Objects.nonNull(dailySubmissionLimit)) {
                redisTemplate.opsForHash().put(SaasRedisBusinessKey.FORM_DAILY_SUBMISSION_LIMIT + tenant, formId.toString(), dailySubmissionLimit.toString());
            }
        }
        return dailySubmissionLimit;

    }

    /**
     * 只更新表单的名称。 并清楚缓存
     * @param businessId
     * @param name
     */
    @Override
    public void updateFormName(String businessId, String name) {

        if (StrUtil.isEmpty(name) || StrUtil.isEmpty(businessId)) {
            return;
        }
        Form form = baseMapper.selectOne(Wraps.<Form>lbQ()
                .select(SuperEntity::getId, Form::getName)
                .eq(Form::getBusinessId, businessId).last(" limit 0,1 "));
        if (Objects.isNull(form)) {
            return;
        }
        if (StrUtil.isNotEmpty(form.getName()) && form.getName().equals(name)) {
            return;
        }
        deleteFormToRedis(businessId);
        form.setName(name);
        baseMapper.updateById(form);

    }


    @Override
    protected R<Boolean> handlerUpdateById(Form model) {
        String fieldsJson = model.getFieldsJson();
        if (StrUtil.isNotEmpty(fieldsJson)) {
            model.setFieldsJson(FormUtil.reWriteFormField(fieldsJson));
            model.setScoreQuestionnaire(FormUtil.isScoreQuestionnaire(fieldsJson));
        }
        baseMapper.updateById(model);
        saveOrUpdateFormRedis(model);
        boolean shouldSyncFormResult = StrUtil.isNotEmpty(model.getFieldsJson()) ||
                StrUtil.isNotEmpty(model.getName()) ||
                Objects.nonNull(model.getOneQuestionPage());
        if (shouldSyncFormResult) {
            String tenant = BaseContextHandler.getTenant();
            formResultMerge.syncCustomFromResult(model, tenant);
            traceIntoFieldOptionConfigService.clearConfig(model, tenant);
        }
        FormScoreRuleUpdateDTO ruleUpdateDTO = model.getFormScoreRuleUpdateDTO();
        if (Objects.nonNull(ruleUpdateDTO)) {
            FormScoreRule scoreRule = new FormScoreRule();
            BeanUtils.copyProperties(ruleUpdateDTO, scoreRule);
            scoreRule.setFormId(model.getId());
            if (Objects.nonNull(scoreRule.getId())) {
                formScoreRuleMapper.updateById(scoreRule);
            } else {
                formScoreRuleMapper.insert(scoreRule);
            }
        }
        List<Long> groupIds = model.getDeleteFieldGroupIds();
        if (CollUtil.isNotEmpty(groupIds)) {
            formFieldsGroupMapper.deleteBatchIds(groupIds);
        }

        saveDailySubmissionLimit(model.getId(), model.getDailySubmissionLimit());
        return R.success();
    }

    @Override
    public Boolean copyForm(String fromTenantCode, String toTenantCode, Map<Long, Long> planIdMaps) {
        String currentTenant = BaseContextHandler.getTenant();

        BaseContextHandler.setTenant(fromTenantCode);
        List<Form> forms = baseMapper.selectList(Wrappers.emptyWrapper());

        Map<Long, Long> formIdMap = new HashMap<>();
        // 查询表单的计分规则。
        List<FormScoreRule> scoreRules = formScoreRuleMapper.selectList(Wraps.lbQ());

        // 查询表单的题目分组规则。
        List<FormFieldsGroup> groupList = formFieldsGroupMapper.selectList(Wraps.lbQ());
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        // 修改数据
        List<Form> toSaveFroms = forms.stream().peek(f -> {
            f.setSourceFormId(f.getId());
            long nextId = snowflake.nextId();
            formIdMap.put(f.getId(), nextId);
            f.setId(nextId);
            if (Objects.nonNull(f.getBusinessId())) {
                Long key = Convert.toLong(f.getBusinessId());
                Long newPlanId = planIdMaps.get(key);
                f.setBusinessId(Convert.toStr(newPlanId));
                f.setSourceBusinessId(Convert.toStr(key));
            }
        }).collect(Collectors.toList());
        BaseContextHandler.setTenant(toTenantCode);
        saveBatchSomeColumn(toSaveFroms);

        // 复制计分规则
        if (CollUtil.isNotEmpty(scoreRules)) {
            scoreRules.forEach(item -> {
                item.setId(null);
                item.setFormId(formIdMap.get(item.getFormId()));
            });
            formScoreRuleMapper.insertBatchSomeColumn(scoreRules);
        }

        // 复制分组规则
        if (CollUtil.isNotEmpty(groupList)) {
            groupList.forEach(item -> {
                item.setId(null);
                item.setFormId(formIdMap.get(item.getFormId()));
            });
            formFieldsGroupMapper.insertBatchSomeColumn(groupList);
        }

        BaseContextHandler.setTenant(currentTenant);
        return true;
    }


    /**
     * 更换为redis缓存获取表单
     * @param category
     * @return
     */
    @Override
    public List<FormField> getTagAttrField(FormEnum category) {
        Form form = getFormByRedis(category, null);
        List<FormField> returnFormFields = new ArrayList<>(20);
        if (Objects.nonNull(form)) {
            String fieldsJson = form.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
            for (FormField field : formFields) {
                if (TagAttrFieldType.checkExistType(field.getWidgetType(), null) ||
                        TagAttrFieldType.checkExistType(field.getExactType(), null)) {
                    returnFormFields.add(field);
                }
            }
        }
        return returnFormFields;
    }

    /**
     * 更换为redis缓存获取字段
     * @param businessId
     * @return
     */
    @Override
    public List<FormField>  getTagAttrMonitoringIndicatorsField(String businessId) {
        Form form = getFormByRedis(null, businessId);
        List<FormField> returnFormFields = new ArrayList<>(20);
        if (Objects.nonNull(form)) {
            String fieldsJson = form.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
            for (FormField field : formFields) {
                if (TagAttrFieldType.checkExistType(field.getExactType(), TagAttrFieldType.EXACT_TYPE)) {
                    returnFormFields.add(field);
                }
            }
        }
        return returnFormFields;

    }


    /**
     * 返回监控指标使用的字段
     * 更换为redis缓存
     * @param planId
     * @param planType
     * @param category
     * @return
     */
    @Override
    public Form getMonitoringIntervalFields(Long planId, Integer planType, FormEnum category) {
        Form form = null;
        if (Objects.nonNull(planId)) {
            form = getFormByRedis(null, planId.toString());
        } else if (Objects.nonNull(planType)) {
            List<Plan> plans = planMapper.selectList(Wraps.<Plan>lbQ().eq(Plan::getPlanType, planType));
            if (CollUtil.isNotEmpty(plans)) {
                Plan plan = plans.get(0);
                form = getFormByRedis(null, plan.getId().toString());
            }
        } else if (Objects.nonNull(category)) {
            if (category.eq(FormEnum.BASE_INFO) || category.eq(FormEnum.HEALTH_RECORD)) {
                form = getFormByRedis(category, null);
            }
        }
        if (Objects.nonNull(form)) {
            String fieldsJson = form.getFieldsJson();
            List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
            List<FormField> fieldList = formFields.stream()
                    .filter(item -> monitoringIntervalFields(item.getWidgetType()))
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(fieldList)) {
                form.setFieldsJson(JSON.toJSONString(fieldList));
            }
            return form;
        }
        return null;
    }

    /**
     * 判断字段类型是否为非 填写字段
     * @param widgetType
     * @return
     */
    private boolean monitoringIntervalFields(String widgetType) {
        if (FormWidgetType.SPLIT_LINE.equals(widgetType) ||
                FormWidgetType.PAGE.equals(widgetType) ||
                FormWidgetType.DESC.equals(widgetType)) {
            return false;
        }
        return true;
    }


    /**
     * 查询planId 关联的 表单。 删除表单下的业务。
     *
     * @param planId
     */
    @Override
    public void removePlanForm(Serializable planId) {
        Form form = baseMapper.selectOne(Wraps.<Form>lbQ().eq(Form::getBusinessId, planId.toString()).last(" limit 0,1 "));
        if (Objects.isNull(form)) {
            return;
        }
        Long formId = form.getId();
        baseMapper.delete(Wraps.<Form>lbQ().eq(Form::getBusinessId, planId.toString()));

        // 从redis中将可能存在的表单清除
        deleteFormToRedis(planId.toString());
        // 表单结果的修改记录
        formResultBackUpService.remove(Wraps.<FormResultBackUp>lbQ()
                .apply(" form_result_id in (select id from t_custom_form_result where business_id = '" + planId.toString() +"') "));

        // 表单结果。
        formResultMapper.delete(Wraps.<FormResult>lbQ()
                .eq(FormResult::getBusinessId, planId.toString()));

        // 删除题目分组
        formFieldsGroupMapper.delete(Wraps.<FormFieldsGroup>lbQ().eq(FormFieldsGroup::getFormId, formId));

        // 删除分数规则
        formScoreRuleMapper.delete(Wraps.<FormScoreRule>lbQ().eq(FormScoreRule::getFormId, formId));

        // 删除成绩结果
        formResultScoreMapper.delete(Wraps.<FormResultScore>lbQ().eq(FormResultScore::getFormId, formId));

        // 表单关联的 基线值 目标值 设置
        patientFormFieldReferenceService.remove(Wraps.<PatientFormFieldReference>lbQ()
                .eq(PatientFormFieldReference::getBusinessId, planId.toString()));

        // 监测数据指标条件
        indicatorsConditionMonitoringMapper.delete(Wraps.<IndicatorsConditionMonitoring>lbQ()
                .eq(IndicatorsConditionMonitoring::getPlanId, planId));
        // 监测数据处理记录
        indicatorsResultInformationMapper.delete(Wraps.<IndicatorsResultInformation>lbQ()
                .eq(IndicatorsResultInformation::getFormId, formId));
        // 信息完整度的监控 指标
        informationIntegrityMonitoringMapper.delete(Wraps.<InformationIntegrityMonitoring>lbQ()
                .eq(InformationIntegrityMonitoring::getFormId, formId));
        // 患者信息完整度字段
        // 患者信息完整度
        List<PatientInformationField> informationFields = patientInformationFieldService.list(Wraps.<PatientInformationField>lbQ()
                .eq(PatientInformationField::getFormId, formId)
                .select(SuperEntity::getId, PatientInformationField::getInformationId)
                .groupBy(PatientInformationField::getInformationId));

        if (CollUtil.isNotEmpty(informationFields)) {
            Set<Long> informationIds = informationFields.stream().map(PatientInformationField::getInformationId)
                    .collect(Collectors.toSet());

            patientInformationFieldService.remove(Wraps.<PatientInformationField>lbQ()
                    .eq(PatientInformationField::getFormId, formId));

            // 信息完整度字段 清除之后。需要重新计算 信息完整度。
            completenessInformationService.calculateCompletion(informationIds);
        }
        String tenant = BaseContextHandler.getTenant();
        traceIntoResultService.formDeleteEvent(formId, tenant);
        traceIntoFieldOptionConfigService.clearFormAllConfig(formId);
    }


    @Override
    public Form getIntoTheGroupOneQuestionPage() {
        Form form = getFormByRedis(FormEnum.BASE_INFO, null);
        if (Objects.nonNull(form)) {
            form.setFieldsJson(null);
        }
        return form;
    }
}
