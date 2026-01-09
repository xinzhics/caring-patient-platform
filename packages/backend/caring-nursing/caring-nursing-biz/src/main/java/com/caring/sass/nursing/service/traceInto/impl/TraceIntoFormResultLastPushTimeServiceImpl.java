package com.caring.sass.nursing.service.traceInto.impl;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoFieldOptionConfigMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoFormResultLastPushTimeMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoResultFieldAbnormalMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoResultMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormOptions;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.dto.traceInto.*;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFieldOptionConfig;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFormResultLastPushTime;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResult;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResultFieldAbnormal;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.traceInto.TraceIntoFormResultLastPushTimeService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.list.AbstractLinkedList;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 选项跟踪患者最新上传时间记录表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Service

public class TraceIntoFormResultLastPushTimeServiceImpl extends SuperServiceImpl<TraceIntoFormResultLastPushTimeMapper, TraceIntoFormResultLastPushTime> implements TraceIntoFormResultLastPushTimeService {

    @Autowired
    TraceIntoFieldOptionConfigMapper traceIntoFieldOptionConfigMapper;

    @Autowired
    PlanMapper planMapper;

    @Autowired
    FormMapper formMapper;

    @Autowired
    PatientApi patientApi;

    @Autowired
    TraceIntoResultMapper traceIntoResultMapper;

    @Autowired
    TraceIntoResultFieldAbnormalMapper traceIntoResultFieldAbnormalMapper;


    @Autowired
    DistributedLock distributedLock;


    @Override
    public List<AppTracePlanList> getAppTracePlanList(Long nursingId) {
        LbqWrapper<TraceIntoFieldOptionConfig> lbqWrapper = Wraps.lbQ();
        lbqWrapper.select(SuperEntity::getId, TraceIntoFieldOptionConfig::getPlanId, TraceIntoFieldOptionConfig::getFormId);
        List<TraceIntoFieldOptionConfig> optionConfigs = traceIntoFieldOptionConfigMapper.selectList(lbqWrapper);
        if (CollUtil.isEmpty(optionConfigs)) {
            return new ArrayList<>();
        }
        Set<Long> planIdSet = optionConfigs.stream().map(TraceIntoFieldOptionConfig::getPlanId).collect(Collectors.toSet());
        Map<Long, Long> planFormIds = optionConfigs.stream().collect(Collectors.toMap(TraceIntoFieldOptionConfig::getPlanId, TraceIntoFieldOptionConfig::getFormId, (o1, o2) -> o2));
        if (CollUtil.isEmpty(planIdSet)) {
            return new ArrayList<>();
        }
        LbqWrapper<Plan> wrapper = Wraps.<Plan>lbQ();
        wrapper.in(SuperEntity::getId, planIdSet);
        wrapper.select(SuperEntity::getId, Plan::getName, Plan::getPlanType);
        List<Plan> planList = planMapper.selectList(wrapper);
        if (CollUtil.isEmpty(planList)) {
            return new ArrayList<>();
        }
        Plan healthLogs = null;
        Plan reviewReminder = null;
        List<Plan> otherPlan = new ArrayList<>();
        List<Long> planIds = new ArrayList<>();
        for (Plan plan : planList) {
            planIds.add(plan.getId());
            if (plan.getPlanType() != null && PlanEnum.HEALTH_LOG.getCode().equals(plan.getPlanType())) {
                healthLogs = plan;
            } else if (plan.getPlanType() != null && PlanEnum.REVIEW_REMIND.getCode().equals(plan.getPlanType())) {
                reviewReminder = plan;
            } else {
                otherPlan.add(plan);
            }
        }
        QueryWrapper<TraceIntoFormResultLastPushTime> queryWrapper = Wrappers.<TraceIntoFormResultLastPushTime>query();
        queryWrapper.select("plan_id as planId", "count(*) as total");
        queryWrapper.in("plan_id", planIds);
        queryWrapper.eq("nursing_id", nursingId);
        queryWrapper.eq("handle_status", 0);
        queryWrapper.groupBy("plan_id");
        List<Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);
        Map<Long, Integer> planCount = new HashMap<>();
        if (CollUtil.isNotEmpty(mapList)) {
            for (Map<String, Object> objectMap : mapList) {
                Object planId = objectMap.get("planId");
                Object total = objectMap.get("total");
                if (total != null && planId != null) {
                    planCount.put(Long.parseLong(planId.toString()), Integer.parseInt(total.toString()));
                }
            }
        }
        List<AppTracePlanList> appTracePlanLists = new ArrayList<>();
        if (reviewReminder != null) {
            @Length(max = 100, message = "名称长度不能超过100") String name = reviewReminder.getName();
            Long planId = reviewReminder.getId();
            Long formId = planFormIds.get(planId);
            Integer count = planCount.get(planId);
            AppTracePlanList tracePlanList = new AppTracePlanList(formId, planId, name, PlanEnum.REVIEW_REMIND, count);
            appTracePlanLists.add(tracePlanList);
        }
        if (healthLogs != null) {
            @Length(max = 100, message = "名称长度不能超过100") String name = healthLogs.getName();
            Long planId = healthLogs.getId();
            Long formId = planFormIds.get(planId);
            Integer count = planCount.get(planId);
            AppTracePlanList tracePlanList = new AppTracePlanList(formId, planId, name, PlanEnum.HEALTH_LOG, count);
            appTracePlanLists.add(tracePlanList);
        }
        if (CollUtil.isNotEmpty(otherPlan)) {
            for (Plan plan : otherPlan) {
                @Length(max = 100, message = "名称长度不能超过100") String name = plan.getName();
                Long planId = plan.getId();
                Long formId = planFormIds.get(planId);
                Integer count = planCount.get(planId);
                System.out.println(formId);
                AppTracePlanList tracePlanList = new AppTracePlanList(formId, planId, name, PlanEnum.CUSTOM_PLAN, count);
                appTracePlanLists.add(tracePlanList);
            }
        }
        return appTracePlanLists;
    }


    @Override
    public AppTracePlanList countHandleNumber(Long nursingId, Long planId) {

        QueryWrapper<TraceIntoFormResultLastPushTime> queryWrapper = Wrappers.<TraceIntoFormResultLastPushTime>query();
        queryWrapper.select("handle_status as handleStatus", "count(*) as total");
        queryWrapper.eq("plan_id", planId);
        queryWrapper.eq("nursing_id", nursingId);
        queryWrapper.groupBy("handle_status");
        List<Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);
        AppTracePlanList appTracePlanList = new AppTracePlanList();
        if (CollUtil.isNotEmpty(mapList)) {
            for (Map<String, Object> objectMap : mapList) {
                Object handleStatus = objectMap.get("handleStatus");
                Object total = objectMap.get("total");
                if (handleStatus != null && total != null) {
                    if (handleStatus.toString().equals("0")) {
                        appTracePlanList.setNoHandleNumber(Integer.parseInt(total.toString()));
                    } else if (handleStatus.toString().equals("1")) {
                        appTracePlanList.setHandleNumber(Integer.parseInt(total.toString()));
                    }
                }
            }
        }

        return appTracePlanList;
    }

    /**
     * 统计医助 未处理的 患者数量
     * @param nursingId
     * @return
     */
    @Override
    public int countNursingHandleNumber(Long nursingId) {
        if (Objects.isNull(nursingId)) {
            throw new BizException("参数nursingId不能为空");
        }
        LbqWrapper<TraceIntoFormResultLastPushTime> lbqWrapper = Wraps.lbQ();
        lbqWrapper.eq(TraceIntoFormResultLastPushTime::getNursingId, nursingId);
        lbqWrapper.eq(TraceIntoFormResultLastPushTime::getHandleStatus, 0);
        Integer count = baseMapper.selectCount(lbqWrapper);
        if (count == null) {
            return 0;
        } else {
            return count;
        }
    }

    /**
     * 全部清空
     * @param nursingId
     * @param formId
     */
    @Override
    public void allClearData(Long nursingId, Long formId) {

        LbqWrapper<TraceIntoFormResultLastPushTime> lbqWrapper = Wraps.<TraceIntoFormResultLastPushTime>lbQ()
                .eq(TraceIntoFormResultLastPushTime::getNursingId, nursingId)
                .eq(TraceIntoFormResultLastPushTime::getFormId, formId)
                .eq(TraceIntoFormResultLastPushTime::getHandleStatus, 1);
        baseMapper.delete(lbqWrapper);

        UpdateWrapper<TraceIntoResult> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("clear_status", 1);
        updateWrapper.eq("nursing_id", nursingId);
        updateWrapper.eq("form_id", formId);
        updateWrapper.eq("handle_status", 1);
        updateWrapper.eq("clear_status", 0);
        traceIntoResultMapper.update(new TraceIntoResult(), updateWrapper);
    }

    /**
     * 医助处理自己的全部患者数据
     * @param nursingId
     * @param formId
     */
    @Override
    public void allHandleResult(Long nursingId, Long formId) {
        List<TraceIntoFormResultLastPushTime> lastPushTimes = baseMapper.selectList(Wraps.<TraceIntoFormResultLastPushTime>lbQ()
                .eq(TraceIntoFormResultLastPushTime::getNursingId, nursingId)
                .eq(TraceIntoFormResultLastPushTime::getFormId, formId)
                .eq(TraceIntoFormResultLastPushTime::getHandleStatus, 0));
        for (TraceIntoFormResultLastPushTime pushTime : lastPushTimes) {
            try {
                handleResult(pushTime);
            } catch (Exception e) {

            }
        }
    }


    /**
     * app处理某患者的异常数据
     * @param patientId
     * @param formId
     */
    @Override
    public void handleResult(Long patientId, Long formId) {

        TraceIntoFormResultLastPushTime pushTime = baseMapper.selectOne(Wraps.<TraceIntoFormResultLastPushTime>lbQ()
                .eq(TraceIntoFormResultLastPushTime::getPatientId, patientId)
                .eq(TraceIntoFormResultLastPushTime::getFormId, formId)
                .eq(TraceIntoFormResultLastPushTime::getHandleStatus, 0)
                .last(" limit 0,1 "));
        handleResult(pushTime);

    }


    private void handleResult(TraceIntoFormResultLastPushTime pushTime ) {
        if (Objects.nonNull(pushTime)) {
            boolean lastPushTime = handlePatientFormResultLastPushTime(pushTime.getPatientId(), pushTime.getFormId(), pushTime.getNursingId(), pushTime.getDoctorId(), pushTime.getPlanId());
            if (lastPushTime) {
                UpdateWrapper<TraceIntoResult> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("handle_status", 1);
                updateWrapper.eq("patient_id", pushTime.getPatientId());
                updateWrapper.eq("form_id", pushTime.getFormId());
                updateWrapper.eq("handle_status", 0);
                traceIntoResultMapper.update(new TraceIntoResult(), updateWrapper);
            } else {
                throw new BizException("处理失败");
            }
        }
    }

    /**
     * 对患者随访计划表单的结果异常进行处理。
     * @param patientId
     * @param formId
     * @param nursingId
     * @param planId
     * @return
     */
    public boolean handlePatientFormResultLastPushTime(Long patientId, Long formId, Long nursingId, Long doctorId, Long planId) {

        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        String lock = "PATIENT_TRACE_FORM_RESULT_LAST_TIME" + patientId.toString() + formId.toString();
        try {
            lockBoolean = distributedLock.lock(lock, 10000L, 5);
            if (lockBoolean) {
                baseMapper.delete(Wraps.<TraceIntoFormResultLastPushTime>lbQ()
                        .eq(TraceIntoFormResultLastPushTime::getPatientId, patientId)
                        .in(TraceIntoFormResultLastPushTime::getHandleStatus, 0,1)
                        .eq(TraceIntoFormResultLastPushTime::getFormId, formId));
                TraceIntoFormResultLastPushTime pushTime = new TraceIntoFormResultLastPushTime();
                pushTime.setPushTime(LocalDateTime.now());
                pushTime.setHandleTime(LocalDateTime.now());
                pushTime.setFormId(formId);
                pushTime.setHandleStatus(1);
                pushTime.setNursingId(nursingId);
                pushTime.setDoctorId(doctorId);
                pushTime.setPatientId(patientId);
                pushTime.setPlanId(planId);
                baseMapper.insert(pushTime);
            }
        } catch (Exception e) {
            log.error(this.getClass().getSimpleName() + "handlePatientFormResult error {} {} {} {}", patientId, nursingId, formId);
            return false;
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }

        return true;
    }



    /**
     * app 未处理 。已处理 的分页列表。并封装其中的表单数据
     * @param page
     * @param lbqWrapper
     * @return
     */
    @Override
    public IPage<TraceFormPatientResultPageDTO> appPage(IPage<TraceIntoFormResultLastPushTime> page, LbqWrapper<TraceIntoFormResultLastPushTime> lbqWrapper) {
        IPage<TraceIntoFormResultLastPushTime> selectPage = baseMapper.selectPage(page, lbqWrapper);
        List<TraceIntoFormResultLastPushTime> records = selectPage.getRecords();
        Page<TraceFormPatientResultPageDTO> resultPageDTOPage = new Page<>();
        resultPageDTOPage.setSize(selectPage.getSize());
        resultPageDTOPage.setTotal(selectPage.getTotal());
        resultPageDTOPage.setPages(selectPage.getPages());
        resultPageDTOPage.setCurrent(selectPage.getCurrent());
        if (CollUtil.isEmpty(records)) {
            return resultPageDTOPage;
        }
        TraceIntoFormResultLastPushTime lastPushTime = records.get(0);
        @NotNull(message = "表单ID不能为空") Long formId = lastPushTime.getFormId();
        @NotNull(message = "处理状态（0 未处理， 1 已处理）不能为空") Integer handleStatus = lastPushTime.getHandleStatus();
        List<Long> patientIds = records.stream().map(TraceIntoFormResultLastPushTime::getPatientId).collect(Collectors.toList());
        NursingPlanPatientDTO patientDTO = new NursingPlanPatientDTO();
        patientDTO.setIds(patientIds);
        patientDTO.setTenantCode(BaseContextHandler.getTenant());
        R<List<NursingPlanPatientBaseInfoDTO>> patientBaseInfos = patientApi.getBaseInfoForNursingPlan(patientDTO);
        Map<Long, NursingPlanPatientBaseInfoDTO> baseInfoDTOMap = new HashMap<>();
        if (patientBaseInfos.getIsSuccess()) {
            List<NursingPlanPatientBaseInfoDTO> baseInfosData = patientBaseInfos.getData();
            baseInfoDTOMap = baseInfosData.stream().collect(Collectors.toMap(NursingPlanPatientBaseInfoDTO::getId, item -> item, (o1, o2) -> o2));
        }

        List<TraceIntoResult> traceIntoResults = traceIntoResultMapper.selectList(Wraps.<TraceIntoResult>lbQ()
                .eq(TraceIntoResult::getHandleStatus, handleStatus)
                .eq(TraceIntoResult::getFormId, formId)
                .in(TraceIntoResult::getPatientId, patientIds));
        if (CollUtil.isEmpty(traceIntoResults)) {
            return resultPageDTOPage;
        }
        Form form = formMapper.selectById(formId);
        List<Long> patientFormResultIds = traceIntoResults.stream().map(TraceIntoResult::getFormResultId).collect(Collectors.toList());
        if (CollUtil.isEmpty(patientFormResultIds)) {
            return resultPageDTOPage;
        }
        Map<Long, List<TraceIntoResult>> patientFormResult = traceIntoResults.stream().collect(Collectors.groupingBy(TraceIntoResult::getPatientId));
        List<TraceFormPatientResultPageDTO> pageDTOS = new ArrayList<>();
        resultPageDTOPage.setRecords(pageDTOS);
        List<TraceIntoResultFieldAbnormal> resultFieldAbnormals = traceIntoResultFieldAbnormalMapper.selectList(Wraps.<TraceIntoResultFieldAbnormal>lbQ()
                .in(TraceIntoResultFieldAbnormal::getFormResultId, patientFormResultIds));
        Map<Long, List<TraceIntoResultFieldAbnormal>> abnormalGroupFormResult = resultFieldAbnormals.stream().collect(Collectors.groupingBy(TraceIntoResultFieldAbnormal::getFormResultId));
        List<TraceIntoResultFieldAbnormal> traceIntoResultFieldAbnormals;
        List<TraceIntoResult> intoResultList;
        String fieldsJson = form.getFieldsJson();
        List<FormField> formFields = JSONArray.parseArray(fieldsJson, FormField.class);
        for (TraceIntoFormResultLastPushTime record : records) {
            TraceFormPatientResultPageDTO resultPageDTO = new TraceFormPatientResultPageDTO();
            NursingPlanPatientBaseInfoDTO baseInfoDTO = baseInfoDTOMap.get(record.getPatientId());
            if (Objects.isNull(baseInfoDTO)) {
                continue;
            }
            // 设置患者信息
            resultPageDTO.setDoctorId(baseInfoDTO.getDoctorId());
            resultPageDTO.setDoctorName(baseInfoDTO.getDoctorName());
            resultPageDTO.setPatientAvatar(baseInfoDTO.getAvatar());
            resultPageDTO.setPatientName(baseInfoDTO.getName());
            resultPageDTO.setPatientImAccount(baseInfoDTO.getImAccount());
            resultPageDTO.setPatientId(record.getPatientId());

            resultPageDTO.setPushTime(record.getPushTime());
            resultPageDTO.setHandleTime(record.getHandleTime());

            List<PlanFormDTO> formResultDto = new ArrayList<>();
            List<FormFieldInfo> fieldInfos;
            intoResultList = patientFormResult.get(record.getPatientId());
            if (CollUtil.isEmpty(intoResultList)) {
                continue;
            }
            for (TraceIntoResult intoResult : intoResultList) {
                traceIntoResultFieldAbnormals = abnormalGroupFormResult.get(intoResult.getFormResultId());
                if (CollUtil.isEmpty(traceIntoResultFieldAbnormals)) {
                    continue;
                }
                PlanFormDTO formDTO = new PlanFormDTO();
                formDTO.setCreateTime(intoResult.getPushTime());
                formDTO.setFormResultId(intoResult.getFormResultId());
                formDTO.setPlanId(intoResult.getPlanId());
                Map<String, List<String>> stringListMap = resultFieldAbnormalMap(traceIntoResultFieldAbnormals);
                fieldInfos = findPatientAbnormalName(formFields, false, stringListMap);
                if (CollUtil.isEmpty(fieldInfos)) {
                    continue;
                }
                formDTO.setFieldInfos(fieldInfos);
                formResultDto.add(formDTO);
            }
            if (CollUtil.isEmpty(formResultDto)) {
                continue;
            }
            formResultDto.sort((o1, o2) -> {
                if (o1.getCreateTime().isBefore(o2.getCreateTime())) {
                    return 1;
                } else {
                    return -1;
                }
            });
            resultPageDTO.setFormResultDto(formResultDto);
            pageDTOS.add(resultPageDTO);
        }
        return resultPageDTOPage;
    }


    private  Map<String, List<String>> resultFieldAbnormalMap(List<TraceIntoResultFieldAbnormal> traceIntoResultFieldAbnormals) {
        Map<String, List<String>> resultFieldAbnormalMap = new HashMap<>();
        for (TraceIntoResultFieldAbnormal resultFieldAbnormal : traceIntoResultFieldAbnormals) {
            List<String> stringList = resultFieldAbnormalMap.get(resultFieldAbnormal.getFormFieldId());
            if (CollUtil.isEmpty(stringList)) {
                stringList = new ArrayList<>();
            }
            stringList.add(resultFieldAbnormal.getFormFieldOptionId());
            resultFieldAbnormalMap.put(resultFieldAbnormal.getFormFieldId(), stringList);
        }
        return resultFieldAbnormalMap;
    }

    /**
     * 根据患者异常选项 和 表单的字段。封装前端显示的内容
     * @param formFields
     * @param isChild
     * @param errorResultMap
     * @return
     */
    private List<FormFieldInfo> findPatientAbnormalName(List<FormField> formFields, Boolean isChild, Map<String, List<String>> errorResultMap) {

        List<FormFieldInfo> fieldInfoList = new ArrayList<>();
        if (CollUtil.isEmpty(formFields)) {
            return fieldInfoList;
        }
        if (CollUtil.isEmpty(errorResultMap)) {
            return fieldInfoList;
        }
        FormFieldInfo fieldInfo;
        List<FormFieldOptionInfo> fieldOptionInfos;
        FormFieldOptionInfo optionInfo;
        for (FormField formField : formFields) {
            if (FormWidgetType.RADIO.equals(formField.getWidgetType())
                    || FormWidgetType.CHECK_BOX.equals(formField.getWidgetType())
                    || FormWidgetType.DROPDOWN_SELECT.equals(formField.getWidgetType())) {
                List<String> stringList = errorResultMap.get(formField.getId());
                List<FormOptions> options = formField.getOptions();
                if (CollUtil.isEmpty(options)) {
                    continue;
                }
                fieldInfo = new FormFieldInfo();
                fieldOptionInfos = new ArrayList<>();
                for (FormOptions option : options) {
                    optionInfo = new FormFieldOptionInfo();
                    optionInfo.setFieldOptionName(option.getAttrValue());
                    String optionId = option.getId();
                    List<FormField> questions = option.getQuestions();
                    if (CollUtil.isNotEmpty(questions)) {
                        List<FormFieldInfo> formFieldInfos = findPatientAbnormalName(questions, true, errorResultMap);
                        if (CollUtil.isNotEmpty(formFieldInfos)) {
                            fieldInfoList.addAll(formFieldInfos);
                        }
                    }
                    if (stringList != null && stringList.contains(optionId)) {
                        optionInfo.setIsExceptionOptions(true);
                        fieldOptionInfos.add(optionInfo);
                    }
                }
                if (CollUtil.isNotEmpty(fieldOptionInfos)) {
                    fieldInfo.setIsChildField(isChild ? 1 : 0);
                    fieldInfo.setFormFieldName(formField.getLabel());
                    fieldInfo.setOptionInfos(fieldOptionInfos);
                    fieldInfoList.add(fieldInfo);
                }
            }
        }
        return fieldInfoList;
    }


}
