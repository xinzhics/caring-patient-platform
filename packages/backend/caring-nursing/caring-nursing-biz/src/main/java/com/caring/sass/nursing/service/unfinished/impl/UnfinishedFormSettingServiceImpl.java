package com.caring.sass.nursing.service.unfinished.impl;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.plan.PlanDetailMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dao.unfinished.UnfinishedFormSettingMapper;
import com.caring.sass.nursing.dto.traceInto.PlanFormDTO;
import com.caring.sass.nursing.dto.unfinished.UnfinishedFormSettingUpdateDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.entity.plan.PlanDetail;
import com.caring.sass.nursing.entity.unfinished.UnfinishedFormSetting;
import com.caring.sass.nursing.enumeration.FollowUpPlanTypeEnum;
import com.caring.sass.nursing.enumeration.MonitoringTaskType;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.unfinished.UnfinishedFormSettingService;
import com.caring.sass.nursing.service.unfinished.UnfinishedPatientRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者管理-未完成表单跟踪设置
 * </p>
 *
 * @author 杨帅
 * @date 2024-05-27
 */
@Slf4j
@Service

public class UnfinishedFormSettingServiceImpl extends SuperServiceImpl<UnfinishedFormSettingMapper, UnfinishedFormSetting> implements UnfinishedFormSettingService {

    @Autowired
    PlanMapper planMapper;

    @Autowired
    PlanDetailMapper planDetailMapper;

    @Autowired
    FormMapper formMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UnfinishedPatientRecordService unfinishedPatientRecordService;

    @Override
    public List<PlanFormDTO> webQueryConfig() {

        List<UnfinishedFormSetting> formSettingList = baseMapper.selectList(Wraps.<UnfinishedFormSetting>lbQ());
        return getPlanFormList(formSettingList);
    }


    /**
     * 更新整个 未完成随访跟踪
     * @param updateDTOList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateConfig(List<UnfinishedFormSettingUpdateDTO> updateDTOList) {

        List<UnfinishedFormSetting> formSettingList = baseMapper.selectList(Wraps.<UnfinishedFormSetting>lbQ());
        if (formSettingList == null) {
            formSettingList = new ArrayList<>();
        }
        Map<Long, UnfinishedFormSetting> map = new HashMap<>();
        UnfinishedFormSetting medicineSetting = new UnfinishedFormSetting();
        if (CollUtil.isNotEmpty(formSettingList)) {
            for (UnfinishedFormSetting formSetting : formSettingList) {
                if (UnfinishedFormSetting.MEDICINE_PUSH_YES.equals(formSetting.getMedicinePush())) {
                    medicineSetting = formSetting;
                } else if (UnfinishedFormSetting.MEDICINE_PUSH_NO.equals(formSetting.getMedicinePush())) {
                    medicineSetting = formSetting;
                } else {
                    map.put(formSetting.getPlanId(), formSetting);
                }
            }
        }
        for (UnfinishedFormSettingUpdateDTO settingUpdateDTO : updateDTOList) {
            // 设置用药提醒的跟踪
            String medicinePush = settingUpdateDTO.getMedicinePush();
            if (StrUtil.isNotEmpty(medicinePush)) {
                medicineSetting.setMedicinePush(medicinePush);
                medicineSetting.setPlanName(PlanEnum.MEDICATION_REMIND.getDesc());
                if (medicineSetting.getId() == null) {
                    baseMapper.insert(medicineSetting);
                } else {
                    baseMapper.updateById(medicineSetting);
                }
                continue;
            }

            Long planId = settingUpdateDTO.getPlanId();
            UnfinishedFormSetting formSetting = map.get(planId);

            // 不设延期跟踪天数。就跳过。
            if (StrUtil.isEmpty(settingUpdateDTO.getReminderTime())) {
                continue;
            }
            if (formSetting == null || StrUtil.isEmpty(settingUpdateDTO.getReminderTime())) {
                formSetting = new UnfinishedFormSetting();
                formSetting.setPlanId(planId);
                formSetting.setFormId(settingUpdateDTO.getFormId());
                formSetting.setPlanType(settingUpdateDTO.getPlanType());
            } else {
                map.remove(planId);
            }
            formSetting.setPlanName(settingUpdateDTO.getPlanName());
            formSetting.setReminderTime(settingUpdateDTO.getReminderTime());
            formSetting.setReminderDay(settingUpdateDTO.getReminderDay());

            if (formSetting.getId() == null) {
                baseMapper.insert(formSetting);
            } else {
                baseMapper.updateById(formSetting);
            }
        }
        Collection<UnfinishedFormSetting> values = map.values();
        if (!values.isEmpty()) {
            baseMapper.deleteBatchIds(values.stream().map(UnfinishedFormSetting::getId).collect(Collectors.toList()));
        }
        String tenant = BaseContextHandler.getTenant();
        String value= redisTemplate.opsForValue().get(SaasRedisBusinessKey.UN_FINISHED_REMIND+":"+tenant);
        if (Objects.nonNull(value) && value.equals(MonitoringTaskType.FINISH.operateType)) {
            redisTemplate.opsForValue().set(SaasRedisBusinessKey.UN_FINISHED_REMIND+":"+tenant, MonitoringTaskType.WAIT.operateType);
        }


    }



    @Override
    public void xxlJobUnFinishedTask(String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        List<UnfinishedFormSetting> formSettingList = baseMapper.selectList(Wraps.<UnfinishedFormSetting>lbQ());
        SaasGlobalThreadPool.execute(() -> unfinishedPatientRecordService.unFinishedTask(formSettingList, tenantCode, false));

    }


    /**
     * 执行某项目的未完成跟踪任务
     * @param tenantCode
     */
    @Override
    public void unFinishedTask(String tenantCode) {
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.UN_FINISHED_REMIND+":"+tenantCode, MonitoringTaskType.RUNING.operateType, 2, TimeUnit.HOURS);
        BaseContextHandler.setTenant(tenantCode);
        List<UnfinishedFormSetting> formSettingList = baseMapper.selectList(Wraps.<UnfinishedFormSetting>lbQ());
        SaasGlobalThreadPool.execute(() -> unfinishedPatientRecordService.unFinishedTask(formSettingList, tenantCode, true));

    }

    /**
     * 查询有跳转表单的 健康日志，复查提醒，自定义随访
     * @return
     */
    public List<PlanFormDTO> getPlanFormList(List<UnfinishedFormSetting> formSettingList) {

        List<Plan> bloodSugar = planMapper.selectList(Wraps.<Plan>lbQ()
                .eq(Plan::getPlanType,  PlanEnum.BLOOD_SUGAR.getCode()));

        List<Plan> planList = planMapper.selectList(Wraps.<Plan>lbQ()
                .in(Plan::getPlanType, PlanEnum.HEALTH_LOG.getCode(), PlanEnum.REVIEW_REMIND.getCode(), PlanEnum.BLOOD_PRESSURE.getCode()));

        List<Plan> carePlanList = planMapper.selectList(Wraps.<Plan>lbQ()
                .in(Plan::getFollowUpPlanType, FollowUpPlanTypeEnum.CARE_PLAN.operateType, FollowUpPlanTypeEnum.MONITORING_DATA.operateType)
                .eq(Plan::getIsAdminTemplate, 1));
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

        int selectMedication = 0;
        Set<Long> selectPlans = new HashSet<>();
        // 配置信息
        Map<Long, UnfinishedFormSetting> map = new HashMap<>();
        UnfinishedFormSetting medicineSetting = new UnfinishedFormSetting();
        if (CollUtil.isNotEmpty(formSettingList)) {
            for (UnfinishedFormSetting formSetting : formSettingList) {
                if (UnfinishedFormSetting.MEDICINE_PUSH_YES.equals(formSetting.getMedicinePush())) {
                    selectMedication = 1;
                    medicineSetting = formSetting;
                } else if (UnfinishedFormSetting.MEDICINE_PUSH_NO.equals(formSetting.getMedicinePush())) {
                    medicineSetting = formSetting;
                } else {
                    if (StrUtil.isNotEmpty(formSetting.getReminderTime()) && Objects.nonNull(formSetting.getReminderDay())) {
                        selectPlans.add(formSetting.getPlanId());
                        map.put(formSetting.getPlanId(), formSetting);
                    }
                }
            }
        }
        PlanFormDTO formDTO;

        // 设置用药的配置
        formDTO = new PlanFormDTO();
        formDTO.setMedicationStatus(CommonStatus.YES);
        if (selectMedication == 1) {
            formDTO.setSetting(true);
            formDTO.setMedicinePush(UnfinishedFormSetting.MEDICINE_PUSH_YES);
        } else {
            formDTO.setSetting(false);
            formDTO.setMedicinePush(UnfinishedFormSetting.MEDICINE_PUSH_NO);
        }
        formDTO.setPlanType(PlanEnum.MEDICATION_REMIND.getCode());
        formDTO.setReminderDay(medicineSetting.getReminderDay());
        formDTO.setReminderTime(medicineSetting.getReminderTime());
        formDTO.setPlanName(PlanEnum.MEDICATION_REMIND.getDesc());
        planFormDTOS.add(formDTO);

        // 设置 血糖
        if (CollUtil.isNotEmpty(bloodSugar)) {
            Plan bloodSugarPlan = bloodSugar.get(0);
            formDTO = new PlanFormDTO();
            Long planId = bloodSugarPlan.getId();
            UnfinishedFormSetting formSetting = map.get(planId);
            if (selectPlans.contains(planId)) {
                formDTO.setSetting(true);
                formDTO.setReminderTime(formSetting.getReminderTime());
                formDTO.setReminderDay(formSetting.getReminderDay());
            }
            formDTO.setPlanId(planId);
            formDTO.setPlanType(PlanEnum.BLOOD_SUGAR.getCode());
            formDTO.setPlanName(bloodSugarPlan.getName());
            formDTO.setMedicationStatus(CommonStatus.NO);
            planFormDTOS.add(formDTO);
        }


        for (Form form : formList) {
            if (form.getBusinessId() == null) {
                continue;
            }
            formDTO = new PlanFormDTO();
            long planId = Long.parseLong(form.getBusinessId());
            Plan plan = planMap.get(planId);
            UnfinishedFormSetting formSetting = map.get(planId);
            if (selectPlans.contains(planId)) {
                formDTO.setSetting(true);
                formDTO.setReminderTime(formSetting.getReminderTime());
                formDTO.setReminderDay(formSetting.getReminderDay());
            }
            formDTO.setFormId(form.getId());
            formDTO.setPlanId(planId);
            formDTO.setPlanType(plan.getPlanType());
            formDTO.setPlanName(plan.getName());
            formDTO.setMedicationStatus(CommonStatus.NO);
            planFormDTOS.add(formDTO);
        }

        return planFormDTOS;

    }
}
