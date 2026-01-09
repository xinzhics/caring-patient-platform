package com.caring.sass.nursing.service.traceInto.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.authority.entity.common.Hospital;
import com.caring.sass.authority.utils.HospitalExport;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoFieldOptionConfigMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoFormResultLastPushTimeMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoResultFieldAbnormalMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoResultMapper;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFieldOptionConfig;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFormResultLastPushTime;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResult;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResultFieldAbnormal;
import com.caring.sass.nursing.enumeration.MonitoringTaskType;
import com.caring.sass.nursing.service.traceInto.TraceIntoResultService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.TypeReferenceAdjustment;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 选项跟踪结果异常表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Service

public class TraceIntoResultServiceImpl extends SuperServiceImpl<TraceIntoResultMapper, TraceIntoResult> implements TraceIntoResultService {

    @Autowired
    TraceIntoResultFieldAbnormalMapper resultFieldAbnormalMapper;

    @Autowired
    TraceIntoFormResultLastPushTimeMapper formResultLastPushTimeMapper;


    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    PatientApi patientApi;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    FormResultMapper formResultMapper;

    @Autowired
    TraceIntoFieldOptionConfigMapper traceIntoFieldOptionConfigMapper;

    /**
     * 删除这些选项对应的异常记录
     * @param configs
     * @param tenantCode
     */
    @Override
    public void deleteByTraceIntoIds(Collection<TraceIntoFieldOptionConfig> configs, String tenantCode) {
        // 一个选项配置被删除了。 那么选项配置对应异常选项记录要清除。
        // 如果异常选项记录所在的题目下选项都被清除，那么题目也需要清除。
        // 当题目都被清除时，表单上传时间也需要清除
        if (CollUtil.isEmpty(configs)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        List<Long> configIds = configs.stream().map(SuperEntity::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(configIds)) {
            // 清除异常选项记录
            resultFieldAbnormalMapper.delete(Wraps.<TraceIntoResultFieldAbnormal>lbQ()
                    .in(TraceIntoResultFieldAbnormal::getTraceIntoConfigId, configIds));
        }
        // 查询config中随访计划的表单有多少表单结果上传时间。
        List<Long> planIds = configs.stream().map(TraceIntoFieldOptionConfig::getPlanId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(planIds)) {
            // 清除这些随访的表单结果 并且表单结果在 TraceIntoResultFieldAbnormal 中已经没有子记录。
            baseMapper.delete(Wraps.<TraceIntoResult>lbQ()
                    .in(TraceIntoResult::getPlanId, planIds)
                    .apply( true," form_result_id not in (SELECT form_result_id FROM t_trace_into_result_field_abnormal where tenant_code = '" + tenantCode + "') " ));

            // 清除患者 在这个随访中 未处理 记录。当患者没有未处理表单结果的时候
            formResultLastPushTimeMapper.delete(Wraps.<TraceIntoFormResultLastPushTime>lbQ()
                    .in(TraceIntoFormResultLastPushTime::getPlanId, planIds)
                    .in(TraceIntoFormResultLastPushTime::getHandleStatus, 0)
                    .apply(true, " patient_id not in (SELECT patient_id FROM t_trace_into_result where handle_status = 0 and clear_status = 0 and tenant_code = '" + tenantCode + "') "));

            // 清除患者 在这个随访中 已处理 记录。当患者没有已处理表单结果的时候
            formResultLastPushTimeMapper.delete(Wraps.<TraceIntoFormResultLastPushTime>lbQ()
                    .in(TraceIntoFormResultLastPushTime::getPlanId, planIds)
                    .in(TraceIntoFormResultLastPushTime::getHandleStatus, 1)
                    .apply(true, " patient_id not in (SELECT patient_id FROM t_trace_into_result where handle_status = 1 and clear_status = 0 and tenant_code = '" + tenantCode + "') "));
        }


    }



    /**
     * 当表单被删除时 ，清除表单相关的数据
     * @param formId
     * @param tenantCode
     */
    @Override
    public void formDeleteEvent(Long formId, String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        resultFieldAbnormalMapper.delete(Wraps.<TraceIntoResultFieldAbnormal>lbQ().eq(TraceIntoResultFieldAbnormal::getFormId, formId));

        formResultLastPushTimeMapper.delete(Wraps.<TraceIntoFormResultLastPushTime>lbQ().eq(TraceIntoFormResultLastPushTime::getFormId, formId));

        baseMapper.delete(Wraps.<TraceIntoResult>lbQ().eq(TraceIntoResult::getFormId, formId));

    }



    /**
     * 处理这些异常选项相关表单结果 清洗异常数据
     * @param optionConfigList
     */
    @Override
    public void traceIntoTask(List<TraceIntoFieldOptionConfig> optionConfigList, String tenantCode) {

        // 对选项跟踪 按 随访计划分组。
        // 查询项目中已经存在的表单结果。
        try {
            if (CollUtil.isEmpty(optionConfigList)) {
                return;
            }
            Map<Long, List<TraceIntoFieldOptionConfig>> formIdGroupMap = optionConfigList.stream().collect(Collectors.groupingBy(TraceIntoFieldOptionConfig::getFormId));
            Set<Long> formIds = formIdGroupMap.keySet();
            BaseContextHandler.setTenant(tenantCode);
            int size = 20;
            for (Long formId : formIds) {
                // 分页加载 表单的 结果。
                int current = 1;
                IPage<FormResult> page;
                List<TraceIntoFieldOptionConfig> optionConfigs = formIdGroupMap.get(formId);
                LbqWrapper<FormResult> lbqWrapper = Wraps.<FormResult>lbQ().eq(FormResult::getFormId, formId);
                boolean continueRun = true;
                while (continueRun) {
                    page = new Page(current, size);
                    IPage<FormResult> selectPage = formResultMapper.selectPage(page, lbqWrapper);
                    List<FormResult> records = selectPage.getRecords();
                    if (CollUtil.isEmpty(records)) {
                        continueRun = false;
                    }
                    try {
                        traceIntoTask(records, optionConfigs);
                    } catch (Exception e) {
                        log.error(this.getClass().getSimpleName() + "traceIntoTask error {}", formId);
                    }
                    long pages = selectPage.getPages();
                    current++;
                    if (pages == 0) {
                        continueRun = false;
                    }
                    if (current > pages) {
                        continueRun = false;
                    }
                }
            }
        } finally {
            redisTemplate.delete(SaasRedisBusinessKey.getTenantTraceIntoSwitch()+":"+tenantCode);
            redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantTraceIntoSwitch()+":"+tenantCode, MonitoringTaskType.FINISH.operateType);
        }

    }

    /**
     * 处理多个表单结果
     * @param formResults
     * @param optionConfigList
     */
    @Override
    public void traceIntoTask(List<FormResult> formResults,  List<TraceIntoFieldOptionConfig> optionConfigList) {

        if (CollUtil.isEmpty(formResults) || CollUtil.isEmpty(optionConfigList)) {
            return;
        }
        for (FormResult formResult : formResults) {
            try {
                traceIntoTask(formResult, optionConfigList);
            } catch (Exception e) {
                log.error(this.getClass().getSimpleName() + "traceIntoTask error form Result id {}", formResult.getId());
            }
        }

    }



    /**
     * 创建或修改 患者 在护理计划 未处理异常 的最后表单结果提交时间
     * @param patientId
     * @param nursingId
     * @param formId
     * @param planId
     * @param localDateTime
     */
    public void createOrUpdatePatientLastPushTimeFormResult(Long patientId, Long nursingId, Long doctorId, Long formId, Long planId, LocalDateTime localDateTime) {

        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        String lock = "PATIENT_TRACE_FORM_RESULT_LAST_TIME" + patientId.toString() + formId.toString();
        try {
            lockBoolean = distributedLock.lock(lock, 10000L, 5);
            if (lockBoolean) {
                TraceIntoFormResultLastPushTime lastPushTime = formResultLastPushTimeMapper.selectOne(Wraps.<TraceIntoFormResultLastPushTime>lbQ()
                        .eq(TraceIntoFormResultLastPushTime::getHandleStatus, 0)
                        .eq(TraceIntoFormResultLastPushTime::getPatientId, patientId)
                        .eq(TraceIntoFormResultLastPushTime::getFormId, formId));
                if (Objects.nonNull(lastPushTime)) {
                    @NotNull(message = "表单结果的上传时间不能为空") LocalDateTime pushTime = lastPushTime.getPushTime();
                    if (pushTime.isBefore(localDateTime)) {
                        lastPushTime.setPushTime(localDateTime);
                        formResultLastPushTimeMapper.updateById(lastPushTime);
                    }
                } else {
                    lastPushTime = new TraceIntoFormResultLastPushTime();
                    lastPushTime.setPushTime(localDateTime);
                    lastPushTime.setHandleTime(localDateTime);
                    lastPushTime.setFormId(formId);
                    lastPushTime.setHandleStatus(0);
                    lastPushTime.setNursingId(nursingId);
                    lastPushTime.setPatientId(patientId);
                    lastPushTime.setDoctorId(doctorId);
                    lastPushTime.setPlanId(planId);
                    formResultLastPushTimeMapper.insert(lastPushTime);
                }
            }
        } catch (Exception e) {
            log.error(this.getClass().getSimpleName() + "createOrUpdatePatientFormResult error {} {} {} {}", patientId, nursingId, formId, localDateTime);
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }

    }




    @Override
    public void traceIntoTask(FormResult formResult, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        List<TraceIntoFieldOptionConfig> optionConfigs = traceIntoFieldOptionConfigMapper.selectList(Wraps.<TraceIntoFieldOptionConfig>lbQ().eq(TraceIntoFieldOptionConfig::getFormId, formResult.getFormId()));
        if (CollUtil.isEmpty(optionConfigs)) {
            return;
        }
        traceIntoTask(formResult, optionConfigs);
    }

    /**
     * 根据表单结果。和选项跟踪配置。检查是否有异常条件。
     * @param formResult
     * @param optionConfigList
     */
    public void traceIntoTask(FormResult formResult, List<TraceIntoFieldOptionConfig> optionConfigList) {
        // 一个表单结果。在15秒内。只能由一个线程处理。避免生成重复的异常。
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(SaasRedisBusinessKey.getTenantTraceIntoResult() + formResult.getId(), "1");
        if (ifAbsent == null || !ifAbsent) {
            return;
        }
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantTraceIntoResult() + formResult.getId(), "1", 15, TimeUnit.SECONDS);

        try {
            // 检查表单结果是否已经有 记录过异常。 如果有。则需要更新或删除异常
            TraceIntoResult traceIntoResult = baseMapper.selectOne(Wraps.<TraceIntoResult>lbQ()
                    .eq(TraceIntoResult::getFormResultId, formResult.getId())
                    .last(" limit 0,1 "));
            boolean updateLastPushTime = false;
            // 清除此表单结果 已经存在的异常记录
            resultFieldAbnormalMapper.delete(Wraps.<TraceIntoResultFieldAbnormal>lbQ().eq(TraceIntoResultFieldAbnormal::getFormResultId, formResult.getId()));

            // 计算是否 有异常结果。
            List<TraceIntoResultFieldAbnormal> fieldAbnormals = handleFormField(formResult, optionConfigList);
            String tenant = BaseContextHandler.getTenant();
            if (CollUtil.isNotEmpty(fieldAbnormals)) {
                // 表单结果有异常选项记录 并且之前没有 生成过。
                if (Objects.isNull(traceIntoResult)) {
                    updateLastPushTime = true;
                    traceIntoResult = new TraceIntoResult();
                    R<Patient> patientR = patientApi.getBaseInfoForStatisticsData(formResult.getUserId());
                    if (patientR.getIsSuccess() && patientR.getData() != null) {
                        Patient data = patientR.getData();
                        Long serviceAdvisorId = data.getServiceAdvisorId();
                        Long doctorId = data.getDoctorId();
                        if (Objects.nonNull(serviceAdvisorId)) {
                            traceIntoResult.setNursingId(serviceAdvisorId);
                        } else {
                            traceIntoResult.setNursingId(0L);
                        }
                        if (Objects.nonNull(doctorId)) {
                            traceIntoResult.setDoctorId(doctorId);
                        } else {
                            traceIntoResult.setDoctorId(0L);
                        }
                    }
                    traceIntoResult.setClearStatus(0);
                    traceIntoResult.setFormId(formResult.getFormId());
                    traceIntoResult.setFormResultId(formResult.getId());
                    traceIntoResult.setHandleStatus(0);
                    traceIntoResult.setPatientId(formResult.getUserId());
                    if (StrUtil.isNotEmpty(formResult.getBusinessId())) {
                        traceIntoResult.setPlanId(Long.parseLong(formResult.getBusinessId()));
                    } else {
                        traceIntoResult.setPlanId(0L);
                    }
                    traceIntoResult.setPushTime(formResult.getCreateTime());

                    baseMapper.insert(traceIntoResult);
                }
                @NotNull(message = "医助ID不能为空") Long nursingId = traceIntoResult.getNursingId();
                @NotNull(message = "医生ID不能为空") Long doctorId = traceIntoResult.getDoctorId();
                fieldAbnormals.forEach(p -> {
                    p.setCreateTime(formResult.getCreateTime());
                    p.setNursingId(nursingId);
                    p.setDoctorId(doctorId);
                });

                resultFieldAbnormalMapper.insertBatchSomeColumn(fieldAbnormals);
                // 当 患者这个表单结果是第一次处理时，需要检查 患者这个随访中 同一处理状态 是否有记录最新上传时间
                if (updateLastPushTime) {
                    createOrUpdatePatientLastPushTimeFormResult(traceIntoResult.getPatientId(), nursingId, doctorId, traceIntoResult.getFormId(),
                            traceIntoResult.getPlanId(), formResult.getCreateTime());
                }
            } else {
                // 表单结果没有异常选择记录。
                if (Objects.nonNull(traceIntoResult)) {
                    Integer handleStatus = traceIntoResult.getHandleStatus();
                    baseMapper.deleteById(traceIntoResult.getId());
                    // 统计患者在这个表单 是否还有 当前这个处理状态的记录。 如果没有，则需要吧患者在这个表单的最后提交时间删除
                    Integer count = baseMapper.selectCount(Wraps.<TraceIntoResult>lbQ()
                            .eq(TraceIntoResult::getFormId, traceIntoResult.getFormId())
                            .eq(TraceIntoResult::getPatientId, traceIntoResult.getPatientId())
                            .eq(TraceIntoResult::getHandleStatus, handleStatus));
                    if (count == null || count == 0) {
                        formResultLastPushTimeMapper.delete(Wraps.<TraceIntoFormResultLastPushTime>lbQ()
                                .eq(TraceIntoFormResultLastPushTime::getPatientId, traceIntoResult.getPatientId())
                                .eq(TraceIntoFormResultLastPushTime::getFormId, formResult.getFormId())
                                .eq(TraceIntoFormResultLastPushTime::getHandleStatus, handleStatus));
                    }
                }
            }
        } catch (Exception e) {
            log.error(this.getClass().getSimpleName() + "traceIntoTask error {}", formResult.getId());
        } finally {
            redisTemplate.delete(SaasRedisBusinessKey.getTenantTraceIntoResult() + formResult.getId());
        }


    }


    /**
     * 分析表单结果的题目是否异常跟踪选项
     * @param formResult
     * @param optionConfigList
     * @return
     */
    private List<TraceIntoResultFieldAbnormal> handleFormField(FormResult formResult,  List<TraceIntoFieldOptionConfig> optionConfigList) {

        if (Objects.isNull(formResult)) {
            return new ArrayList<>();
        }
        if (CollUtil.isEmpty(optionConfigList)) {
            return new ArrayList<>();
        }
        String jsonContent = formResult.getJsonContent();
        if (StrUtil.isEmpty(jsonContent)) {
            return new ArrayList<>();
        }
        List<FormFieldDto> fieldDtos = JSONArray.parseArray(jsonContent, FormFieldDto.class);
        if (CollUtil.isEmpty(fieldDtos)) {
            return new ArrayList<>();
        }
        Map<String, List<String>> configFieldOptionMap = new HashMap<>();
        Map<String, Long> configFieldIdMap = new HashMap<>();
        for (TraceIntoFieldOptionConfig optionConfig : optionConfigList) {
            List<String> stringList = configFieldOptionMap.get(optionConfig.getFormFieldId());
            if (CollUtil.isEmpty(stringList)) {
                stringList = new ArrayList<>();
            }
            stringList.add(optionConfig.getFieldOptionId());
            configFieldOptionMap.put(optionConfig.getFormFieldId(), stringList);
            configFieldIdMap.put(optionConfig.getFieldOptionId(), optionConfig.getId());
        }

        @Length(max = 32, message = "业务Id，业务Id，冗余字段，与t_custom_form中的c_bussiness_id相同长度不能超过32") String businessId = formResult.getBusinessId();
        Long planId = 0L;
        if (StrUtil.isNotEmpty(businessId)) {
            planId = Long.parseLong(businessId);
        }
        return handleFormField(formResult.getUserId(), formResult.getFormId(), formResult.getId(), planId, null, null, fieldDtos, configFieldOptionMap, configFieldIdMap);
    }

    /**
     * 分析单选 多选 下拉提醒中的选项结果是否是异常跟踪选项。
     * @param patientId
     * @param formId
     * @param formResultId
     * @param planId
     * @param parentFormFieldId
     * @param parentFormFieldOptionId
     * @param fieldDtos
     * @param configFieldOptionMap
     * @return
     */
    private List<TraceIntoResultFieldAbnormal> handleFormField(Long patientId, Long formId, Long formResultId, Long planId,
                                                               String  parentFormFieldId, String parentFormFieldOptionId,
                                                               List<FormFieldDto> fieldDtos,
                                                               Map<String, List<String>> configFieldOptionMap, Map<String, Long> configFieldIdMap) {
        List<TraceIntoResultFieldAbnormal> traceIntoResultFieldAbnormals = new ArrayList<>();
        for (FormFieldDto fieldDto : fieldDtos) {
            String widgetType = fieldDto.getWidgetType();
            if (FormWidgetType.RADIO.equals(widgetType) || FormWidgetType.CHECK_BOX.equals(widgetType) || FormWidgetType.DROPDOWN_SELECT.equals(widgetType)) {
                List<String> fieldOptionConfigs = configFieldOptionMap.get(fieldDto.getId());

                String fieldDtoValues = fieldDto.getValues();
                if (StrUtil.isEmpty(fieldDtoValues)) {
                    continue;
                }
                JSONArray jsonArray = JSONArray.parseArray(fieldDtoValues);
                if (CollUtil.isEmpty(jsonArray)) {
                    continue;
                }
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String attrValue = jsonObject.getString("attrValue");
                    if (fieldOptionConfigs != null && StrUtil.isNotBlank(attrValue) && fieldOptionConfigs.contains(attrValue)) {
                        TraceIntoResultFieldAbnormal fieldAbnormal = TraceIntoResultFieldAbnormal.builder()
                                .formId(formId)
                                .planId(planId)
                                .patientId(patientId)
                                .formResultId(formResultId)
                                .parentFieldId(parentFormFieldId)
                                .parentFieldOptionId(parentFormFieldOptionId)
                                .formFieldId(fieldDto.getId())
                                .formFieldOptionId(attrValue)
                                .traceIntoConfigId(configFieldIdMap.get(attrValue))
                                .build();
                        traceIntoResultFieldAbnormals.add(fieldAbnormal);
                    }
                    String questions = jsonObject.getString("questions");
                    if (StrUtil.isNotBlank(questions)) {
                        List<FormFieldDto> formFieldDtos = JSONArray.parseArray(questions, FormFieldDto.class);
                        if (CollUtil.isNotEmpty(formFieldDtos)) {
                            traceIntoResultFieldAbnormals.addAll(handleFormField(patientId, formId, formResultId, planId, fieldDto.getId(), attrValue, formFieldDtos, configFieldOptionMap, configFieldIdMap));
                        }
                    }
                }
            }
        }
        return traceIntoResultFieldAbnormals;
    }


}
