package com.caring.sass.nursing.service.information.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.information.InformationIntegrityMonitoringMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.dto.information.InformationIntegrityMonitoringTaskDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.information.CompletenessInformation;
import com.caring.sass.nursing.entity.information.InformationIntegrityMonitoring;
import com.caring.sass.nursing.entity.information.PatientInformationField;
import com.caring.sass.nursing.service.form.FormService;
import com.caring.sass.nursing.service.information.CompletenessInformationService;
import com.caring.sass.nursing.service.information.InformationIntegrityMonitoringService;
import com.caring.sass.nursing.service.information.PatientInformationFieldService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.dto.PatientPageDTO;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 信息完整度监控指标
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Service
public class InformationIntegrityMonitoringServiceImpl extends SuperServiceImpl<InformationIntegrityMonitoringMapper, InformationIntegrityMonitoring> implements InformationIntegrityMonitoringService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PatientApi patientApi;
    @Autowired
    private FormService formService;
    @Autowired
    private CompletenessInformationService completenessInformationService;
    @Autowired
    private PatientInformationFieldService patientInformationFieldService;
    @Autowired
    private FormResultMapper formResultMapper;
    @Autowired
    private DatabaseProperties databaseProperties;
    /**
     * 信息完整度同步任务 （信息完整度监控指标录入结束触发）
     *
     * @param model
     * @return
     */
    @Override
    public Boolean synInformationIntegrityMonitoringTask(InformationIntegrityMonitoringTaskDTO model) {
        //标记任务进行中 (如果任务一天还没执行玩，说明系统有问题，自动过期掉任务)
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.getTenantCodeInformationIntegritySwitch() + ":" + model.getTenantCode(), "true", 24, TimeUnit.HOURS);
        // 执行同步任务信息完整度的逻辑
        SaasGlobalThreadPool.execute(() -> doSynInformationIntegrityMonitoringTask(model.getTenantCode()));
        return Boolean.TRUE;
    }


    @Override
    public boolean removeById(Serializable id) {

        InformationIntegrityMonitoring integrityMonitoring = baseMapper.selectById(id);
        Long formId = integrityMonitoring.getFormId();
        String fieldId = integrityMonitoring.getFieldId();
        patientInformationFieldService.remove(Wraps.<PatientInformationField>lbQ()
                .eq(PatientInformationField::getFormId, formId)
                .eq(PatientInformationField::getFieldId, fieldId));
        baseMapper.deleteById(id);
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    /**
     * 执行同步任务信息完整度的逻辑
     *
     * @param tenantCode 租户编码
     */
    public void doSynInformationIntegrityMonitoringTask(String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        try {
            //获取项目的信息完整度的监控指标中的表单
            LbqWrapper<InformationIntegrityMonitoring> lbqWrapper = new LbqWrapper();
            lbqWrapper.orderByAsc(InformationIntegrityMonitoring::getMonitorSort);
            List<InformationIntegrityMonitoring> informationIntegrityMonitorings = list(lbqWrapper);
            if (CollectionUtils.isEmpty(informationIntegrityMonitorings)) {
                return;
            }
            PageParams<PatientPageDTO> pagesQ = new PageParams<>();
            pagesQ.setCurrent(1L);
            pagesQ.setSize(200L);
            pagesQ.setModel(PatientPageDTO.builder().build());
            R<IPage<Patient>> pagesR = patientApi.page(pagesQ);
            if (pagesR.getIsSuccess() && !ObjectUtils.isEmpty(pagesR.getData()) && !CollectionUtils.isEmpty(pagesR.getData().getRecords())) {
                //总页数
                long pages = pagesR.getData().getPages();
                //第一页的任务执行
                generateCompletionPatientInformation(tenantCode, informationIntegrityMonitorings, pagesR.getData().getRecords());
                //第一页之后的任务执行
                for (int i = 1; i < pages; i++) {
                    pagesQ.setCurrent(i + 1);
                    pagesR = patientApi.page(pagesQ);
                    if (pagesR.getIsSuccess() && !ObjectUtils.isEmpty(pagesR.getData()) && !CollectionUtils.isEmpty(pagesR.getData().getRecords())) {
                        generateCompletionPatientInformation(tenantCode, informationIntegrityMonitorings, pagesR.getData().getRecords());
                    } else {
                        log.error("执行同步任务信息完整度的逻辑 未查询到患者 第【" + pagesQ.getCurrent() + "】页 信息！");
                    }
                }
            } else {
                log.error("执行同步任务信息完整度的逻辑 未查询到患者 第【" + pagesQ.getCurrent() + "】页 信息！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("执行同步任务信息完整度的逻辑 失败！", e);
        }finally {
            //执行完成后移除任务标记
            redisTemplate.delete(SaasRedisBusinessKey.getTenantCodeInformationIntegritySwitch() + ":" + tenantCode);
        }
    }

    /**
     * 执行同步任务信息完整度的逻辑(指定患者触发)
     *
     * @param tenantCode 租户编码
     */
    public void doSingleSynInformationIntegrityMonitoringTask(String tenantCode, List<Patient> patients) {
        BaseContextHandler.setTenant(tenantCode);
        try {
            //获取项目的信息完整度的监控指标中的表单
            LbqWrapper<InformationIntegrityMonitoring> lbqWrapper = new LbqWrapper();
            lbqWrapper.orderByAsc(InformationIntegrityMonitoring::getMonitorSort);
            List<InformationIntegrityMonitoring> informationIntegrityMonitorings = this.list(lbqWrapper);
            if (CollectionUtils.isEmpty(informationIntegrityMonitorings)) {
                return;
            }
            generateCompletionPatientInformation(tenantCode, informationIntegrityMonitorings, patients);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("执行同步任务信息完整度的逻辑(指定患者触发) 失败！", e);
        }
    }

    /**
     * 患者更新了一个表单。就检查这个表单是否和信息完整度有关系
     * @param tenantCode
     * @param patient
     * @param formResultId
     */
    @Override
    public void doSingleSynInformationIntegrityMonitoringTask(String tenantCode, Patient patient, Long formId, Long formResultId) {
        BaseContextHandler.setTenant(tenantCode);
        // 只需要更新这一个表单的字段
        LbqWrapper<InformationIntegrityMonitoring> lbqWrapper = new LbqWrapper();
        lbqWrapper.orderByAsc(InformationIntegrityMonitoring::getMonitorSort);
        List<InformationIntegrityMonitoring> list = this.list(lbqWrapper);
        log.info("患者更新了一个表单。就检查这个表单是否和信息完整度有关系 list={}",JSON.toJSONString(list));
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<InformationIntegrityMonitoring> monitorings = list.stream().filter(item -> item.getFormId().equals(formId)).collect(Collectors.toList());
        if (CollUtil.isEmpty(monitorings)) {
            return;
        }
        int total = list.size();
        LbqWrapper<CompletenessInformation> lbqCompletenessInformationWrapper = new LbqWrapper();
        lbqCompletenessInformationWrapper.eq(CompletenessInformation::getPatientId, patient.getId());
        CompletenessInformation completenessInformation = completenessInformationService.getOne(lbqCompletenessInformationWrapper);
        //生成患者信息完整度概览表
        long informationId;
        if (ObjectUtils.isEmpty(completenessInformation)) {
            completenessInformation = new CompletenessInformation();
            DatabaseProperties.Id id = databaseProperties.getId();
            Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());
            informationId = snowflake.nextId();
            completenessInformation.setId(informationId);
            completenessInformation.setPatientId(patient.getId());
        } else {
            informationId = completenessInformation.getId();
        }
        LocalDateTime lastWriteTime = completenessInformation.getLastWriteTime();
        FormResult formResult = formResultMapper.selectById(formResultId);
        if (Objects.nonNull(formResult)) {
            LocalDateTime updateTime = formResult.getUpdateTime();
            lastWriteTime = getLastLocalDateTime(lastWriteTime, updateTime);
        }
        AtomicInteger writeFieldCount = new AtomicInteger(0);
        doGeneratePatientInformationFields(monitorings, completenessInformation, formId, null, formResult, informationId, writeFieldCount);

        completenessInformation.setLastWriteTime(lastWriteTime);
        int count = patientInformationFieldService.count(Wraps.<PatientInformationField>lbQ()
                .eq(PatientInformationField::getInformationId, informationId)
                .eq(PatientInformationField::getFieldWrite, 1));
        log.info("患者更新了一个表单。就检查这个表单是否和信息完整度有关系 total={},count={}",total,count);
        completenessInformation.setCompletion(getCompletenessInformationComplete(total, count));
        if (100 == completenessInformation.getCompletion()) {
            completenessInformation.setComplete(1);
        } else {
            completenessInformation.setComplete(0);
        }
        completenessInformationService.saveOrUpdate(completenessInformation);

    }

    /**
     * 最后填写时间
     * @param lastWriteTime
     * @param formResultUpdateTime
     * @return
     */
    private LocalDateTime getLastLocalDateTime(LocalDateTime lastWriteTime, LocalDateTime formResultUpdateTime) {
        if (lastWriteTime == null) {
            lastWriteTime = formResultUpdateTime;
        } else {
            if (lastWriteTime.isBefore(formResultUpdateTime)) {
                lastWriteTime = formResultUpdateTime;
            }
        }
        return lastWriteTime;
    }
    /**
     * 生成患者信息完整度信息
     * TODO: 可能会出现并发冲突
     * @param tenantCode
     * @param informationIntegrityMonitorings 项目的信息完整度的监控指标
     * @param patients
     */
    private void generateCompletionPatientInformation(String tenantCode, List<InformationIntegrityMonitoring> informationIntegrityMonitorings, List<Patient> patients) {
        BaseContextHandler.setTenant(tenantCode);
        Map<Long, List<InformationIntegrityMonitoring>> classMap = informationIntegrityMonitorings.stream()
                .collect(Collectors.groupingBy(InformationIntegrityMonitoring::getFormId));

        int size = informationIntegrityMonitorings.size();
        patients.stream().forEach(item -> {
            LbqWrapper<CompletenessInformation> lbqCompletenessInformationWrapper = new LbqWrapper();
            lbqCompletenessInformationWrapper.eq(CompletenessInformation::getPatientId, item.getId());
            CompletenessInformation completenessInformation = completenessInformationService.getOne(lbqCompletenessInformationWrapper);

            //生成患者信息完整度概览表
            long informationId;
            if (ObjectUtils.isEmpty(completenessInformation)) {
                completenessInformation = new CompletenessInformation();
                DatabaseProperties.Id id = databaseProperties.getId();
                Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());
                informationId = snowflake.nextId();
                completenessInformation.setId(informationId);
            } else {
                informationId = completenessInformation.getId();
                patientInformationFieldService.remove(Wraps.<PatientInformationField>lbQ().eq(PatientInformationField::getInformationId, informationId));
            }
            LocalDateTime lastWriteTime = completenessInformation.getLastWriteTime();
            // 患者填写了多少题
            AtomicInteger writeFieldCount = new AtomicInteger(0);
            //按照表单id 对项目的信息完整度的监控指标 进行分组
            //生成患者信息完整度概览表、患者信息完整度字段表
            for (Long formId : classMap.keySet()) {
                //获取当前表单指定患者填写的最新一次表单结果
                LbqWrapper<FormResult> formResultLbqWrapper = Wraps.<FormResult>lbQ().eq(FormResult::getUserId, item.getId())
                        .eq(FormResult::getFormId, formId)
                        .eq(FormResult::getDeleteMark, CommonStatus.NO)
                        .orderByDesc(Entity::getUpdateTime)
                        .last(" limit 0,1 ");
                FormResult formResult = formResultMapper.selectOne(formResultLbqWrapper);
                Form form = null;
                if (Objects.nonNull(formResult)) {
                    LocalDateTime updateTime = formResult.getUpdateTime();
                    lastWriteTime = getLastLocalDateTime(lastWriteTime, updateTime);
                } else {
                    form = formService.getById(formId);
                }

                //当前表单监控字段
                List<InformationIntegrityMonitoring> monitoringfields = classMap.get(formId);
                doGeneratePatientInformationFields(monitoringfields, completenessInformation, formId, form, formResult, informationId, writeFieldCount);
            }

            completenessInformation.setPatientId(item.getId());
            completenessInformation.setLastWriteTime(lastWriteTime);
            completenessInformation.setCompletion(getCompletenessInformationComplete(size, writeFieldCount.get()));
            if (100 == completenessInformation.getCompletion()) {
                completenessInformation.setComplete(1);
            } else {
                completenessInformation.setComplete(0);
            }
            completenessInformationService.saveOrUpdate(completenessInformation);
        });
    }



    /**
     * 获取信息完整度百分比
     *
     * @param monitoringFieldCount
     * @param writeFieldCount
     * @return
     */
    private Integer getCompletenessInformationComplete(Integer monitoringFieldCount, Integer writeFieldCount) {
        if (monitoringFieldCount == 0) {
            return 100;
        }
        BigDecimal totalDecimal = new BigDecimal(monitoringFieldCount);
        BigDecimal chartDecimal = new BigDecimal(writeFieldCount);
        return BigDecimalUtils.proportion(chartDecimal, totalDecimal);
    }

    /**
     * 生成患者信息完整度字段表
     *
     * @param monitoringfields           表单监控字段
     * @param oldCompletenessInformation 历史患者监控指标
     * @param formId                     表单id
     * @param lastWriteFormResult        最后一次填写的表单结果
     * @return
     */
    private void doGeneratePatientInformationFields(List<InformationIntegrityMonitoring> monitoringfields, CompletenessInformation oldCompletenessInformation,
                                                       Long formId, Form form,
                                                       FormResult lastWriteFormResult,Long informationId, AtomicInteger writeFieldCount) {
        log.info("生成患者信息完整度字段表 入参 monitoringfields={},oldCompletenessInformation={},formId={},lastWriteFormResult={}",monitoringfields,oldCompletenessInformation,formId,lastWriteFormResult);
        //患者信息完整度字段
        List<PatientInformationField> patientInformationFields = new ArrayList<>();
        List<FormField> formResultFields = null;
        List<FormField> formFields = null;
        if (!ObjectUtils.isEmpty(lastWriteFormResult)) {
            formResultFields = JSON.parseArray(lastWriteFormResult.getJsonContent(), FormField.class);
        }
        if (Objects.nonNull(form)) {
            formFields = JSON.parseArray(form.getFieldsJson(), FormField.class);
        }
        final List<FormField> lastWriteFormFields = formResultFields;
        final List<FormField> formFieldsFinal = formFields;
        //用户实际填写字段数量统计
        monitoringfields.stream().forEach(i -> {
            PatientInformationField patientInformationField = new PatientInformationField();
            if (oldCompletenessInformation.getId() != null) {
                LbqWrapper<PatientInformationField> lbqPatientInformationFieldWrapper = new LbqWrapper();
                lbqPatientInformationFieldWrapper.eq(PatientInformationField::getInformationId, oldCompletenessInformation.getId());
                lbqPatientInformationFieldWrapper.eq(PatientInformationField::getFormId, formId);
                lbqPatientInformationFieldWrapper.eq(PatientInformationField::getFieldId, i.getFieldId());
                List<PatientInformationField> list = patientInformationFieldService.list(lbqPatientInformationFieldWrapper);
                if (CollUtil.isNotEmpty(list)) {
                    patientInformationField = list.get(0);
                }
                if (ObjectUtils.isEmpty(patientInformationField)) {
                    patientInformationField = new PatientInformationField();
                }
            }
            patientInformationField.setFieldWrite(0);
            if (CollUtil.isNotEmpty(lastWriteFormFields)) {
                for (FormField lastWriteFormField : lastWriteFormFields) {
                    if (lastWriteFormField.getId().equals(i.getFieldId())) {
                        int write = judgeValuesWrite(lastWriteFormField.getWidgetType(), lastWriteFormField.getValues(), lastWriteFormField.getValue());
                        patientInformationField.setFieldExactType(lastWriteFormField.getExactType());
                        patientInformationField.setFieldWidgetType(lastWriteFormField.getWidgetType());
                        patientInformationField.setFieldValues(lastWriteFormField.getValues());
                        patientInformationField.setFieldValue(lastWriteFormField.getValue());
                        patientInformationField.setOtherValue(lastWriteFormField.getOtherValue());
                        patientInformationField.setFieldWrite(write);
                        patientInformationField.setWriteTime(lastWriteFormResult.getCreateTime());
                        if(write==1){
                            writeFieldCount.getAndIncrement();
                        }
                        break;
                    }
                }
            } else {
                // 如果是 患者关注进来。没有这些字段。则需要生产这个类型
                if (CollUtil.isNotEmpty(formFieldsFinal)) {
                    for (FormField formField : formFieldsFinal) {
                        if (formField.getId().equals(i.getFieldId())) {
                            patientInformationField.setFieldExactType(formField.getExactType());
                            patientInformationField.setFieldWidgetType(formField.getWidgetType());
                            break;
                        }
                    }
                }
            }
            patientInformationField.setInformationId(informationId);
            patientInformationField.setFormId(formId);
            patientInformationField.setBusinessType(i.getBusinessType());
            patientInformationField.setPlanName(i.getPlanName());
            patientInformationField.setFieldId(i.getFieldId());
            patientInformationField.setFieldLabel(i.getFieldLabel());
            patientInformationField.setFieldLabelDesc(i.getFieldLabelDesc());
            patientInformationField.setFieldSort(i.getMonitorSort());
            patientInformationFields.add(patientInformationField);
        });
        if (!CollectionUtils.isEmpty(patientInformationFields)) {
            patientInformationFieldService.saveOrUpdateBatch(patientInformationFields);
        }
    }

    /**
     *
     * 判断这个字段的结果。患者有没有填写。
     * @param values
     * @param value
     * @return 1: 填写了。 0 没填写
     */
    public static int judgeValuesWrite(String widgetType,  String values, String value) {
        FormFieldDto fieldDto = new FormFieldDto();
        fieldDto.setValues(values);
        List<String> valuesOrIds = fieldDto.parseResultValuesOrIds();
        if (CollUtil.isNotEmpty(valuesOrIds)) {
            return 1;
        }
        // 地址组件。可能只填写了详细地址。没有选行政区划
        if (FormWidgetType.ADDRESS.equals(widgetType)) {
            if (StrUtil.isNotEmpty(value)) {
                return 1;
            }
        }
        return 0;
    }


    /**
     * 由于产品调整页面需要。
     * 所以此方法调整。
     * 满足 生产患者完整度 是所需的信息要求
     * @param queryWrapper
     * @return
     */
    @Override
    public List<InformationIntegrityMonitoring> list(Wrapper<InformationIntegrityMonitoring> queryWrapper) {
        List<InformationIntegrityMonitoring> list = super.list(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        Set<Long> formIds = list.stream().map(InformationIntegrityMonitoring::getFormId).collect(Collectors.toSet());

        try {

            List<Form> formList = formService.listByIds(formIds);
            if (CollUtil.isEmpty(formList)) {
                return list;
            }
            // 找到 InformationIntegrityMonitoring 中 关联的 FormFiled。 拿到FormField 中的 labelDesc.
            Map<Long, List<InformationIntegrityMonitoring>> monitorFormIdGroup = list.stream()
                    .collect(Collectors.groupingBy(InformationIntegrityMonitoring::getFormId));
            formList.forEach(form -> {
                Long formId = form.getId();

                // 拿到form 关联的 监控
                List<InformationIntegrityMonitoring> monitorings = monitorFormIdGroup.get(formId);
                if (CollUtil.isNotEmpty(monitorings)) {

                    String fieldsJson = form.getFieldsJson();
                    // 表单中 字段和 监控指标 比对。 找到需要的
                    List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
                    // fieldId -> fieldLabelDesc
                    Map<String, String> fieldLabelDescMap = formFields.stream()
                            .filter(field -> StrUtil.isNotEmpty(field.getLabelDesc()))
                            .collect(Collectors.toMap(FormField::getId, FormField::getLabelDesc, (o1, o2) -> o2));
                    for (InformationIntegrityMonitoring monitoring : monitorings) {
                        String desc = fieldLabelDescMap.get(monitoring.getFieldId());
                        monitoring.setFieldLabelDesc(desc);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.info("IntegrityMonitoring find form field error: {}", e.getMessage());
        }

        return list;
    }
}
