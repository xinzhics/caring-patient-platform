package com.caring.sass.nursing.service.drugs.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.constant.AttrBindEvent;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.constant.TagBindRedisKey;
import com.caring.sass.nursing.dao.drugs.PatientDrugsHistoryLogMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeSettingMapper;
import com.caring.sass.nursing.dao.plan.PatientNursingPlanMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import com.caring.sass.nursing.entity.drugs.*;
import com.caring.sass.nursing.entity.plan.PatientNursingPlan;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.DrugsOperateType;
import com.caring.sass.nursing.enumeration.EarlyWarningMonitorEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.drugs.*;
import com.caring.sass.nursing.util.I18nUtils;
import com.caring.sass.nursing.util.PatientDrugsNextReminderDateUtil;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.BizAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者添加的用药
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service
public class PatientDrugsServiceImpl extends SuperServiceImpl<PatientDrugsMapper, PatientDrugs> implements PatientDrugsService {

    @Autowired
    PatientDrugsHistoryLogMapper patientDrugsHistoryLogMapper;
    @Autowired
    DrugsConditionMonitoringService drugsConditionMonitoringService;

    @Autowired
    DatabaseProperties databaseProperties;

    @Autowired
    PatientDayDrugsService patientDayDrugsService;

    @Autowired
    PatientDrugsTimeMapper patientDrugsTimeMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    PatientNursingPlanMapper patientNursingPlanMapper;

    @Autowired
    PlanMapper planMapper;

    @Autowired
    PatientDrugsTimeSettingMapper patientDrugsTimeSettingMapper;

    @Autowired
    DrugsResultInformationService drugsResultInformationService;


    @Autowired
    SysDrugsService sysDrugsService;

    @Autowired
    MsgPatientSystemMessageApi msgPatientSystemMessageApi;


    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {

        PatientDrugs patientDrugs = baseMapper.selectById(id);
        if (Objects.isNull(patientDrugs)) {
            return true;
        }

        // 重新计算 患者今天需要的总打卡次数

        // 查询患者这个药品还没有推送的记录有多少
        LbqWrapper<PatientDrugsTime> lbqWrapper = Wraps.<PatientDrugsTime>lbQ()
                .eq(PatientDrugsTime::getPatientDrugsId, patientDrugs.getId())
                .ge(PatientDrugsTime::getDrugsTime, LocalDateTime.now());
        List<PatientDrugsTime> patientDrugsTimes = patientDrugsTimeMapper.selectList(lbqWrapper);


        // 删除这个药品的 未推送的记录
        if (CollUtil.isNotEmpty(patientDrugsTimes)) {
            List<Long> ids = patientDrugsTimes.stream().map(SuperEntity::getId).collect(Collectors.toList());
            patientDrugsTimeMapper.deleteBatchIds(ids);
            List<PatientDayDrugs> patientDayDrugs = patientDayDrugsService.getPatientDayDrugs(LocalDate.now());
            if (CollUtil.isNotEmpty(patientDayDrugs)) {
                PatientDayDrugs dayDrugs = patientDayDrugs.get(0);
                Integer numberTotal = dayDrugs.getCheckinNumberTotal();
                numberTotal = numberTotal - patientDayDrugs.size();
                if (numberTotal < 0) {
                    numberTotal = 0;
                }
                dayDrugs.setCheckinNumberTotal(numberTotal);
                patientDayDrugsService.updateById(dayDrugs);
            }
        }
        if (Objects.nonNull(patientDrugs.getDrugsId()) && Objects.nonNull(patientDrugs.getPatientId())) {
            drugsResultInformationService.remove(Wraps.<DrugsResultInformation>lbQ()
                    .eq(DrugsResultInformation::getPatientId, patientDrugs.getPatientId())
                    .eq(DrugsResultInformation::getDrugsId, patientDrugs.getDrugsId()));
        }
        baseMapper.deleteById(patientDrugs);
        return false;
    }



    /**
     * 用户用药更新后。 刷新用户的 用药历史记录
     *
     * TODO: 前端展示界面已经取消。
     * @param drugs
     * @param model
     * @param tenantCode
     */
    @Deprecated
    public void patientDrugHistoryLogUpdate(PatientDrugs drugs, PatientDrugs model, String tenantCode, String userType) {
        BaseContextHandler.setTenant(tenantCode);
        boolean resetCurrentDrugs = false;
        if (drugs.getStatus() != null && (model.getStatus() == 1 || model.getStatus() == 2)) {
            // 用户停止了用药
            PatientDrugsHistoryLog drugsHistoryLog = PatientDrugsHistoryLog.builder()
                    .patientId(model.getPatientId())
                    .drugsId(model.getDrugsId())
                    .medicineName(model.getMedicineName())
                    .historyDate(LocalDate.now())
                    .operateType(DrugsOperateType.STOP.operateType)
                    .operateTypeSort(DrugsOperateType.STOP.operateTypeSort)
                    .build();
            patientDrugsHistoryLogMapper.insert(drugsHistoryLog);
            resetCurrentDrugs = true;
        }

        // 每日用药
        if ((model.getNumberOfDay() != null && !model.getNumberOfDay().equals(drugs.getNumberOfDay())) ||
                (model.getDose() != null && !model.getDose().equals(drugs.getDose())) ||
                (model.getUnit() != null && !model.getUnit().equals(drugs.getUnit())) ||
                (model.getTimePeriod() != null && !model.getTimePeriod().equals(drugs.getTimePeriod())) ||
                (model.getCycleDuration() != null && !model.getCycleDuration().equals(drugs.getCycleDuration()))) {
            // 用户修改了用药的剂量
            // 查询今天有没有修改记录。
            Long drugsId = drugs.getDrugsId();
            LbqWrapper<PatientDrugsHistoryLog> lbqWrapper = Wraps.<PatientDrugsHistoryLog>lbQ()
                    .eq(PatientDrugsHistoryLog::getPatientId, model.getPatientId())
                    .eq(PatientDrugsHistoryLog::getDrugsId, drugsId)
                    .eq(PatientDrugsHistoryLog::getOperateType, DrugsOperateType.UPDATED.operateType)
                    .eq(PatientDrugsHistoryLog::getHistoryDate, LocalDate.now());

            List<PatientDrugsHistoryLog> selectList = patientDrugsHistoryLogMapper.selectList(lbqWrapper);
            PatientDrugsHistoryLog historyLog;
            if (CollUtil.isNotEmpty(selectList)) {
                historyLog = selectList.get(0);
                // 如果出现很多条， 只保留一条
                if (selectList.size() > 1) {
                    for (int i = 1; i < selectList.size(); i++) {
                        PatientDrugsHistoryLog drugsHistoryLog = selectList.get(i);
                        patientDrugsHistoryLogMapper.deleteById(drugsHistoryLog);
                    }
                }
            } else {
                historyLog = PatientDrugsHistoryLog.builder()
                        .patientId(model.getPatientId())
                        .drugsId(model.getDrugsId())
                        .medicineName(model.getMedicineName())
                        .historyDate(LocalDate.now())
                        .operateType(DrugsOperateType.UPDATED.operateType)
                        .operateTypeSort(DrugsOperateType.UPDATED.operateTypeSort)
                        .build();
            }
            historyLog.setBeforeCurrentDose(drugs.getDose());
            historyLog.setBeforeCurrentNumberOfDay(drugs.getNumberOfDay());
            historyLog.setBeforeCurrentUnit(drugs.getUnit());
            historyLog.setBeforeCycleDuration(drugs.getCycleDuration());
            historyLog.setBeforeTimePeriod(drugs.getTimePeriod());
            historyLog.setCurrentDose(model.getDose());
            historyLog.setCurrentNumberOfDay(model.getNumberOfDay());
            historyLog.setCurrentUnit(model.getUnit());
            historyLog.setCurrentCycleDuration(model.getCycleDuration());
            historyLog.setCurrentTimePeriod(model.getTimePeriod());
            if (historyLog.getId() == null) {
                patientDrugsHistoryLogMapper.insert(historyLog);
            } else {
                patientDrugsHistoryLogMapper.updateById(historyLog);
            }
            // 患者 添加用药 或者修改用药后， 发送一条互动消息到医生， 提醒医生
            sendPatientDrugHistoryLogUpdate(historyLog, tenantCode, userType);
            resetCurrentDrugs = true;
        }
        if (resetCurrentDrugs) {
            resetCurrentDrugs(model.getPatientId());
        }
    }

    private void updateDrugs(PatientDrugs drugs ,PatientDrugs model) {
        String tenant = BaseContextHandler.getTenant();
        // 如果用户的 用药状态改变。
        if (drugs.getStatus() != null && !drugs.getStatus().equals(model.getStatus())) {
            if (model.getStatus() != null && model.getStatus().equals(0)) {
                patientDrugChange(model, AttrBindEvent.ADD_DRUG, tenant);
            } else {
                patientDrugChange(model, AttrBindEvent.STOP_DRUG, tenant);
            }
        }
        String userType = BaseContextHandler.getUserType();
        SaasGlobalThreadPool.execute(() -> patientDrugHistoryLogUpdate(drugs, model, tenant, userType));
    }

    /**
     * 如果之前是历史用药
     * 现在改为了正在用药
     * 则将药品设置可以 用药预警监听
     * @param drugs
     * @param model
     */
    public void setEarlyWarningMonitor( PatientDrugs drugs, PatientDrugs model) {
        if (Objects.nonNull(drugs) && Objects.nonNull(model)) {
            Integer status = drugs.getStatus();
            if (status != null && model.getDrugsId() != null && (status.equals(1) || status.equals(2))) {
                if (model.getStatus() != null && model.getStatus() == 0 ) {
                    model.setEarlyWarningMonitor(EarlyWarningMonitorEnum.CAN_MONITOR.code);
                    model.setStartTakeMedicineDate(LocalDate.now());
                }
            }
        }
    }

    @Deprecated
    @Override
    public boolean updateAllById(PatientDrugs model) {
        throw new BizException("当前版本过低，请升级版本");
//        PatientDrugs drugs = baseMapper.selectById(model.getId());
//        String tenant = BaseContextHandler.getTenant();
//        // 如果不是正在使用的药品
//        // 现在修改为正在使用的药品了。
//        // 则验证一下是否药品已经被添加过正在使用
//        if (!drugs.getStatus().equals(0) && model.getStatus().equals(0)) {
//            checkDrugsExist(model.getPatientId(), model.getDrugsId());
//        }
//        // 默认set一下之前的值
//        model.setEarlyWarningMonitor(drugs.getEarlyWarningMonitor());
//        model.setStartTakeMedicineDate(drugs.getStartTakeMedicineDate());
//        if (Objects.equals(1, model.getCycle())) {
//            Integer cycleDay = model.getCycleDay();
//            BizAssert.notNull(cycleDay, "用药周期不能为空");
//            //  如： 1月6日添加用药。1月7日推送，1月8日停止用药
//            LocalDate endTime = model.getStartTakeMedicineDate().plusDays(cycleDay);
//            LocalDateTime dateTime = LocalDateTime.of(endTime, LocalTime.now());
//            model.setEndTime(dateTime);
//        }
//        setEarlyWarningMonitor(drugs, model);
//        super.updateAllById(model);
//        updateDrugs(drugs, model);
//        if (model.getCycle() != null && model.getCycle() == 1) {
//            // 异步计算这个药量和周期天数 需不需要预警
//            SaasGlobalThreadPool.execute(() -> calculateDosageCycle(model, tenant));
//        }
//        SaasGlobalThreadPool.execute(() -> drugsConditionMonitoringService.synDrugsConditionMonitoringTaskPatientDrugsChange(model, tenant));
//        return true;
    }


    /**
     * 重置 今天 当前用药
     */
    public void resetCurrentDrugs(Long patientId) {

        LbqWrapper<PatientDrugsHistoryLog> wrapper = Wraps.<PatientDrugsHistoryLog>lbQ().eq(PatientDrugsHistoryLog::getPatientId, patientId)
                .eq(PatientDrugsHistoryLog::getHistoryDate, LocalDate.now())
                .eq(PatientDrugsHistoryLog::getOperateType, DrugsOperateType.CURRENT.operateType);
        patientDrugsHistoryLogMapper.delete(wrapper);
        List<PatientDrugs> drugsList = baseMapper.selectList(Wraps.<PatientDrugs>lbQ().eq(PatientDrugs::getPatientId, patientId)
                .eq(PatientDrugs::getStatus, 0));
        List<PatientDrugsHistoryLog> historyLogs = new ArrayList<>(drugsList.size());
        for (PatientDrugs patientDrug : drugsList) {
            historyLogs.add(PatientDrugsHistoryLog.builder()
                    .drugsId(patientDrug.getDrugsId())
                    .patientId(patientDrug.getPatientId())
                    .currentUnit(patientDrug.getUnit())
                    .currentDose(patientDrug.getDose())
                    .currentNumberOfDay(patientDrug.getNumberOfDay())
                    .currentCycleDuration(patientDrug.getCycleDuration())
                    .currentTimePeriod(patientDrug.getTimePeriod())
                    .historyDate(LocalDate.now())
                    .operateType(DrugsOperateType.CURRENT.operateType)
                    .operateTypeSort(DrugsOperateType.CURRENT.operateTypeSort)
                    .medicineName(patientDrug.getMedicineName())
                    .build());
        }
        if (CollUtil.isNotEmpty(historyLogs)) {
            patientDrugsHistoryLogMapper.insertBatchSomeColumn(historyLogs);
        }

    }

    /**
     * 用户药品信息发生了变化。 通知 标签。 更新患者的标签
     * @param model
     * @param attrBindEvent
     * @param tenant
     */
    private void patientDrugChange(PatientDrugs model, AttrBindEvent attrBindEvent, String tenant) {
        AttrBindChangeDto changeDto = new AttrBindChangeDto();
        changeDto.setEvent(attrBindEvent);
        changeDto.setTenantCode(tenant);
        changeDto.setDrugsId(model.getDrugsId());
        changeDto.setPatientId(model.getPatientId());
        String string = JSON.toJSONString(changeDto);
        redisTemplate.opsForList().leftPush(TagBindRedisKey.TENANT_ATTR_BIND, string);

    }

    /**
     * 患者 添加用药 或者修改用药后， 发送一条互动消息到医生， 提醒医生
     * @param drugsHistoryLog
     * @param tenantCode
     */
    public void sendPatientDrugHistoryLogUpdate(PatientDrugsHistoryLog drugsHistoryLog, String tenantCode, String userType) {

        if (UserType.PATIENT.equals(userType)) {
            Long patientId = drugsHistoryLog.getPatientId();
            if (Objects.isNull(patientId)) {
                return;
            }
            NursingPlanPatientDTO patientDTO = new NursingPlanPatientDTO();
            List<Long> patientIds = new ArrayList<>();
            patientIds.add(patientId);
            patientDTO.setIds(patientIds);
            patientDTO.setTenantCode(tenantCode);
            R<List<NursingPlanPatientBaseInfoDTO>> patientApiBaseInfoForNursingPlan = patientApi.getBaseInfoForNursingPlan(patientDTO);
            for (NursingPlanPatientBaseInfoDTO planPatientBaseInfoDTO : patientApiBaseInfoForNursingPlan.getData()) {
                MsgPatientSystemMessageSaveDTO saveDTO = new MsgPatientSystemMessageSaveDTO();
                saveDTO.setPatientId(patientId);
                saveDTO.setPatientOpenId(planPatientBaseInfoDTO.getOpenId());
                saveDTO.setDoctorId(planPatientBaseInfoDTO.getDoctorId());
                saveDTO.setBusinessId(drugsHistoryLog.getId());
                saveDTO.setPlanName(drugsHistoryLog.getMedicineName());
                saveDTO.setPatientCanSee(CommonStatus.NO);
                saveDTO.setReadStatus(CommonStatus.NO);
                saveDTO.setTenantCode(tenantCode);
                saveDTO.setDoctorCommentStatus(CommonStatus.NO);
                saveDTO.setDoctorReadStatus(CommonStatus.NO);
                saveDTO.setFunctionType(PlanFunctionTypeEnum.INTERACTIVE_MESSAGE.getCode());
                saveDTO.setInteractiveFunctionType(PlanFunctionTypeEnum.MEDICATION.getCode());
                saveDTO.setMedicineOperationType(drugsHistoryLog.getOperateType());
                saveDTO.setPushTime(LocalDateTime.now());
                saveDTO.setPushPerson(planPatientBaseInfoDTO.getName());
                msgPatientSystemMessageApi.saveSystemMessage(saveDTO);
            }
        }
    }

    /**
     * 添加药品后。异步更新 历史用药记录
     * @param model
     * @param tenantCode
     */
    private void addDrugPatientHistoryLogUpdate(PatientDrugs model, String tenantCode, String userType) {
        BaseContextHandler.setTenant(tenantCode);
        PatientDrugsHistoryLog drugsHistoryLog = PatientDrugsHistoryLog.builder()
                .patientId(model.getPatientId())
                .drugsId(model.getDrugsId())
                .medicineName(model.getMedicineName())
                .historyDate(LocalDate.now())
                .operateType(DrugsOperateType.CREATED.operateType)
                .operateTypeSort(DrugsOperateType.CREATED.operateTypeSort)
                .build();
        patientDrugsHistoryLogMapper.insert(drugsHistoryLog);
        resetCurrentDrugs(model.getPatientId());

        // 患者 添加用药 或者修改用药后， 发送一条互动消息到医生， 提醒医生
        sendPatientDrugHistoryLogUpdate(drugsHistoryLog, tenantCode, userType);

        List<Plan> planList = planMapper.selectList(Wraps.<Plan>lbQ().eq(Plan::getPlanType, PlanEnum.MEDICATION_REMIND.getCode()).last(" limit 0,1 "));
        if (CollUtil.isNotEmpty(planList)) {
            Plan plan = planList.get(0);
            List<PatientNursingPlan> nursingPlanList = patientNursingPlanMapper.selectList(Wraps.<PatientNursingPlan>lbQ()
                    .eq(PatientNursingPlan::getPatientId, model.getPatientId())
                    .eq(PatientNursingPlan::getNursingPlantId, plan.getId())
                    .last(" limit 0,1 "));
            if (CollUtil.isEmpty(nursingPlanList)) {
                PatientNursingPlan nursingPlan = new PatientNursingPlan();
                nursingPlan.setPatientId(model.getPatientId());
                nursingPlan.setNursingPlantId(plan.getId());
                nursingPlan.setStartDate(LocalDate.now());
                nursingPlan.setIsSubscribe(1);
                patientNursingPlanMapper.insert(nursingPlan);
            } else {
                PatientNursingPlan patientNursingPlan = nursingPlanList.get(0);
                if (patientNursingPlan.getPatientCancelSubscribe() == null || patientNursingPlan.getPatientCancelSubscribe() == 0) {
                    patientNursingPlan.setIsSubscribe(1);
                    patientNursingPlanMapper.updateById(patientNursingPlan);
                }
            }
        }

    }

    /**
     * 检验患者的药箱中是否有正在使用的这种药品。
     * 有则不能添加
     * @param patient
     * @param drugsId
     */
    public void checkDrugsExist(Long patient, Long drugsId) {

        Integer count = baseMapper.selectCount(Wraps.<PatientDrugs>lbQ()
                .eq(PatientDrugs::getPatientId, patient)
                .eq(PatientDrugs::getDrugsId, drugsId)
                .eq(PatientDrugs::getStatus, 0));
        if (count != null && count > 0) {
            throw new BizException(I18nUtils.getMessage("PATIENT_DRUGS_EXIST"));
        }

    }

    public void setEndTime(PatientDrugs model) {
        // 0  代表无限期  1：选择截止日期
        Integer cycle = model.getCycle();
        if (Objects.equals(0, cycle)) {
            model.setEndTime(null);
        }
        if (Objects.equals(1, cycle)) {
            Integer cycleDay = model.getCycleDay();
            BizAssert.notNull(cycleDay, "用药周期不能为空");
            //  如： 1月6日添加用药。1月7日推送，1月8日停止用药
            // 需要使用用药周期循环次数 X 用药的周期维度
            PatientDrugsTimePeriodEnum timePeriod = model.getTimePeriod();
            int coefficient = 0;
            if (PatientDrugsTimePeriodEnum.day.equals(timePeriod)) {
                coefficient = 1;
            } else if (PatientDrugsTimePeriodEnum.hour.equals(timePeriod)) {
                model.setEndTime(null);
                return;
            } else if (PatientDrugsTimePeriodEnum.week.equals(timePeriod)) {
                coefficient = 7;
            } else if (PatientDrugsTimePeriodEnum.moon.equals(timePeriod)) {
                coefficient = 30;
            }
            LocalDateTime now = LocalDateTime.now();
            int i = cycleDay * coefficient;
            LocalDateTime endTime = now.plusDays(i);
            model.setEndTime(endTime);
        }
    }

    @Override
    public void savePatientDrugs(PatientDrugs model, List<PatientDrugsTimeSetting> timeSettingList) {
        Long drugsId = model.getDrugsId();
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());
        boolean needMonitor = false;
        if (drugsId == null) {
            drugsId = snowflake.nextId();
            model.setDrugsId(drugsId);
            model.setEarlyWarningMonitor(EarlyWarningMonitorEnum.NO_NEED_MONITOR.code);
        } else {
            needMonitor = true;
            // 判断药品 患者有没有正在使用的同种药品
            checkDrugsExist(model.getPatientId(), drugsId);
            model.setEarlyWarningMonitor(EarlyWarningMonitorEnum.CAN_MONITOR.code);
        }
        setEndTime(model);
        model.setStartTakeMedicineDate(LocalDate.now());
        baseMapper.insert(model);
        List<PatientDrugsTimeSetting> timeSettings = null;
        if (CollUtil.isNotEmpty(timeSettingList)) {
            for (PatientDrugsTimeSetting timeSetting : timeSettingList) {
                timeSetting.setPatientDrugsId(model.getId());
                timeSetting.setPatientId(model.getPatientId());
            }
            patientDrugsTimeSettingMapper.insertBatchSomeColumn(timeSettingList);
            timeSettings = patientDrugsTimeSettingMapper.selectList(Wraps.<PatientDrugsTimeSetting>lbQ()
                    .eq(PatientDrugsTimeSetting::getPatientDrugsId, model.getId())
                    .orderByAsc(PatientDrugsTimeSetting::getDayOfTheCycle)
                    .orderByAsc(PatientDrugsTimeSetting::getTriggerTimeOfTheDay));
            model.setNextReminderDate(PatientDrugsNextReminderDateUtil.getPatientDrugsNextReminderDate(model, timeSettings));
        } else {
            model.setNextReminderDate(PatientDrugsNextReminderDateUtil.getPatientDrugsNextReminderDate(model, timeSettingList));
        }
        baseMapper.updateById(model);

        String tenant = BaseContextHandler.getTenant();
        String userType = BaseContextHandler.getUserType();
        patientDrugChange(model, AttrBindEvent.ADD_DRUG, tenant);


        if (model.getCycle() != null && model.getCycle() == 1 && model.getDrugsId() != null) {
            // 异步计算这个药量和周期天数 需不需要预警
            SaasGlobalThreadPool.execute(() -> calculateDosageCycle(model, tenant));
        }

        SaasGlobalThreadPool.execute(() -> addDrugPatientHistoryLogUpdate(model, tenant, userType));
        if (needMonitor) {
            SaasGlobalThreadPool.execute(() -> drugsConditionMonitoringService.synDrugsConditionMonitoringTaskPatientDrugsChange(model, tenant));
        }

        // 新添加用药。 如果吃药周期是 N天一次。N 周一次，N月一次。 判断最近的下次推送日期是否在今天切时间未过去。 是，跳过。否，在今天生成一次吃药打卡记录
        initPatientDrugsTime(model, timeSettings);
    }

    @Autowired
    PatientApi patientApi;



    /**
     * 吃药周期是 N天一次。N 周一次，N月一次
     * 由于患者添加用药时，今天的吃药已经时间已经过去。
     * 系统默认生成今天的吃药打卡记录，用于记录用药剂量
     * @param model
     */
    public void initPatientDrugsTime(PatientDrugs model, List<PatientDrugsTimeSetting> timeSettings) {
        PatientDrugsTimePeriodEnum timePeriod = model.getTimePeriod();
        if (CollUtil.isEmpty(timeSettings)) {
            return;
        }
        if (!PatientDrugsTimePeriodEnum.hour.equals(timePeriod)) {
            LocalDateTime reminderDate = model.getNextReminderDate();
            Patient patient = null;
            NursingPlanPatientBaseInfoDTO baseInfoDTO = null;
            PatientDayDrugs dayDrugs = null;
            List<PatientDrugsTime>  patientDrugsTimes = new ArrayList<>();
            for (PatientDrugsTimeSetting timeSetting : timeSettings) {
                Integer theCycle = timeSetting.getDayOfTheCycle();
                // 周期内的第一天或者没有设置
                if (theCycle == null || theCycle == 1) {
                    LocalTime timeOfTheDay = timeSetting.getTriggerTimeOfTheDay();
                    LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), timeOfTheDay);
                    dateTime = dateTime.withSecond(0).withNano(0);
                    // 今天的提醒时间已经过去了。
                    if (dateTime.isBefore(reminderDate)) {
                        if (patient == null) {
                            R<Patient> patientR = patientApi.get(model.getPatientId());
                            if (!patientR.getIsSuccess() || patientR.getData() == null) {
                                return;
                            }
                            patient = patientR.getData();
                            baseInfoDTO  = new NursingPlanPatientBaseInfoDTO();
                            BeanUtils.copyProperties(patient, baseInfoDTO);
                            baseInfoDTO.setId(patient.getId());
                            dayDrugs = patientDayDrugsService.createPatientDayDrugs(baseInfoDTO);
                        }
                        Integer count = patientDrugsTimeMapper.selectCount(Wraps.<PatientDrugsTime>lbQ()
                                .eq(PatientDrugsTime::getDrugsTime, dateTime)
                                .eq(PatientDrugsTime::getPatientId, model.getPatientId())
                                .eq(PatientDrugsTime::getPatientDrugsId, model.getId()));
                        if (count == null || count == 0) {
                            PatientDrugsTime patientDrugsTime = PatientDrugsTime
                                    .builder()
                                    .drugsTime(dateTime)
                                    .drugsId(model.getDrugsId())
                                    .patientDrugsId(model.getId())
                                    .status(2)
                                    .drugsDose(model.getDose())
                                    .patientId(model.getPatientId())
                                    .medicineIcon(model.getMedicineIcon())
                                    .medicineName(model.getMedicineName())
                                    .unit(model.getUnit()).build();
                            patientDrugsTime.setNeedPush(0);
                            if (dayDrugs.getCheckinedNumber() == 0) {
                                dayDrugs.setStatus(0);
                            } else {
                                dayDrugs.setStatus(1);
                            }
                            patientDrugsTimes.add(patientDrugsTime);
                        }
                    }
                }
            }
            if (patientDrugsTimes.size() > 0) {
                patientDrugsTimeMapper.insertBatchSomeColumn(patientDrugsTimes);
                patientDayDrugsService.updateById(dayDrugs);
            }
        }
    }

    /**
     * 单独更新患者药品的 用药时间设置 和 下次推送用药推送时间
     * @param model
     * @param timeSettings
     */
    @Override
    public void updatePatientDrugsTimeSetting(PatientDrugs model, List<PatientDrugsTimeSetting> timeSettings) {


        if (CollUtil.isNotEmpty(timeSettings)) {
            PatientDrugs drugs = baseMapper.selectById(model.getId());
            patientDrugsTimeSettingMapper.delete(Wraps.<PatientDrugsTimeSetting>lbQ()
                    .eq(PatientDrugsTimeSetting::getPatientDrugsId, model.getId()));
            for (PatientDrugsTimeSetting timeSetting : timeSettings) {
                timeSetting.setId(null);
                timeSetting.setPatientDrugsId(model.getId());
                timeSetting.setPatientId(model.getPatientId());
            }
            patientDrugsTimeSettingMapper.insertBatchSomeColumn(timeSettings);
            timeSettings = patientDrugsTimeSettingMapper.selectList(Wraps.<PatientDrugsTimeSetting>lbQ()
                    .eq(PatientDrugsTimeSetting::getPatientDrugsId, model.getId())
                    .orderByAsc(PatientDrugsTimeSetting::getDayOfTheCycle)
                    .orderByAsc(PatientDrugsTimeSetting::getTriggerTimeOfTheDay));
            model.setNextReminderDate(PatientDrugsNextReminderDateUtil.getPatientDrugsNextReminderDate(model, timeSettings));
            baseMapper.updateById(model);
            updateDrugs(drugs, model);
        }
    }

    @Override
    public void updatePatientDrugs(PatientDrugs model, List<PatientDrugsTimeSetting> timeSettingList) {
        String tenant = BaseContextHandler.getTenant();
        PatientDrugs drugs = baseMapper.selectById(model.getId());
        // 如果不是正在使用的药品
        // 现在修改为正在使用的药品了。
        // 则验证一下是否药品已经被添加过正在使用
        if (!drugs.getStatus().equals(0) && model.getStatus().equals(0)) {
            checkDrugsExist(model.getPatientId(), model.getDrugsId());
        }
        model.setStartTakeMedicineDate(drugs.getStartTakeMedicineDate());
        // 默认set一下之前的值
        model.setEarlyWarningMonitor(drugs.getEarlyWarningMonitor());
        setEarlyWarningMonitor(drugs, model);
        setEndTime(model);
        patientDrugsTimeSettingMapper.delete(Wraps.<PatientDrugsTimeSetting>lbQ()
                .eq(PatientDrugsTimeSetting::getPatientDrugsId, model.getId()));
        if (CollUtil.isNotEmpty(timeSettingList)) {
            for (PatientDrugsTimeSetting timeSetting : timeSettingList) {
                timeSetting.setId(null);
                timeSetting.setPatientDrugsId(model.getId());
                timeSetting.setPatientId(model.getPatientId());
            }
            patientDrugsTimeSettingMapper.insertBatchSomeColumn(timeSettingList);
            timeSettingList = patientDrugsTimeSettingMapper.selectList(Wraps.<PatientDrugsTimeSetting>lbQ()
                    .eq(PatientDrugsTimeSetting::getPatientDrugsId, model.getId())
                    .orderByAsc(PatientDrugsTimeSetting::getDayOfTheCycle)
                    .orderByAsc(PatientDrugsTimeSetting::getTriggerTimeOfTheDay));
            model.setNextReminderDate(PatientDrugsNextReminderDateUtil.getPatientDrugsNextReminderDate(model, timeSettingList));
        } else {
            model.setNextReminderDate(PatientDrugsNextReminderDateUtil.getPatientDrugsNextReminderDate(model, timeSettingList));
        }
        baseMapper.updateById(model);
        Integer status = drugs.getStatus();

        // 修改用药 药品从停止用药恢复了。
        if (status != null && model.getDrugsId() != null && (status.equals(1) || status.equals(2)) && model.getStatus() == 0) {
            initPatientDrugsTime(model, timeSettingList);
            if (model.getCycle() != null && model.getCycle() == 1 && model.getDrugsId() != null) {
                // 异步计算这个药量和周期天数 需不需要预警
                SaasGlobalThreadPool.execute(() -> calculateDosageCycle(model, tenant));
            }
        }
        updateDrugs(drugs, model);
        if (drugs.getDrugsId() != null){
            SaasGlobalThreadPool.execute(() -> drugsConditionMonitoringService.synDrugsConditionMonitoringTaskPatientDrugsChange(model,tenant));
        }

    }

    /**
     * 查询用药的基本信息。并返回用药时间
     * @param id
     * @return
     */
    @Override
    public PatientDrugs getById(Serializable id) {
        PatientDrugs patientDrugs = baseMapper.selectById(id);
        if (Objects.isNull(patientDrugs)) {
            return null;
        }
        List<PatientDrugsTimeSetting> timeSettings = patientDrugsTimeSettingMapper.selectList(Wraps.<PatientDrugsTimeSetting>lbQ()
                .eq(PatientDrugsTimeSetting::getPatientDrugsId, patientDrugs.getId()));
        patientDrugs.setPatientDrugsTimeSettingList(timeSettings);
        return patientDrugs;
    }

    /**
     * 设置药品的用药时间
     * @param drugsList
     */
    @Override
    public void setDrugsTimeSetting(List<PatientDrugs> drugsList) {

        List<Long> collect = drugsList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        List<PatientDrugsTimeSetting> timeSettings = patientDrugsTimeSettingMapper.selectList(Wraps.<PatientDrugsTimeSetting>lbQ()
                .in(PatientDrugsTimeSetting::getPatientDrugsId, collect));
        if (CollUtil.isEmpty(timeSettings)) {
            return;
        }
        Map<Long, List<PatientDrugsTimeSetting>> longListMap = timeSettings.stream().collect(Collectors.groupingBy(PatientDrugsTimeSetting::getPatientDrugsId));
        for (PatientDrugs drugs : drugsList) {
            List<PatientDrugsTimeSetting> drugsTimeSettings = longListMap.get(drugs.getId());
            drugs.setPatientDrugsTimeSettingList(drugsTimeSettings);
        }
    }

    /**
     * 已弃用
     * @param model
     * @return
     */
    @Deprecated
    @Override
    public boolean updateById(PatientDrugs model) {
        throw new BizException("当前版本过低，请升级版本");
    }

    @Deprecated
    @Override
    public boolean save(PatientDrugs model) {
        throw new BizException("当前版本过低，请升级版本");
    }

    /**
     * 计算患者添加的这个药量在周期内是否需要预警。
     * 周期吃10天药，
     * 药量只够5天药，则需要预警。
     * 周期吃10天药。
     * 药量够吃10天药，则不需要预警
     */
    public void calculateDosageCycle(PatientDrugs model, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        if (model.getDrugsId() == null) {
            return;
        }
        DrugsConditionMonitoring conditionMonitoring = drugsConditionMonitoringService.getOne(Wraps.<DrugsConditionMonitoring>lbQ()
                .eq(DrugsConditionMonitoring::getDrugsId, model.getDrugsId())
                .last(" limit 0,1 "));
        if (Objects.isNull(conditionMonitoring)) {
            return;
        }
        SysDrugs sysDrugs = sysDrugsService.getOne(Wraps.<SysDrugs>lbQ().select(SuperEntity::getId, SysDrugs::getDosageCount)
                .eq(SuperEntity::getId, model.getDrugsId()));
        if (Objects.isNull(sysDrugs)) {
            return;
        }
        if (Objects.isNull(sysDrugs.getDosageCount())) {
            return;
        }
        if (model.getNumberOfDay()==null || model.getDose()==null || ObjectUtils.isEmpty(model.getStartTakeMedicineDate())){
            log.error("药品ID【"+model.getDrugsId()+"】对应药品每天用药次数、每次剂量、开始吃药日期不存在！");
            return;
        }
        if (sysDrugs.getDosageCount()==null || model.getNumberOfBoxes()==null){
            log.error("药品ID【"+model.getDrugsId()+"】对应药品药量计数或者盒数不存在！");
            return;
        }
        BigDecimal dosageCount = new BigDecimal(sysDrugs.getDosageCount());
        BigDecimal numberOfBoxes = new BigDecimal(model.getNumberOfBoxes());
        // 总药量=药量计数*盒数
        BigDecimal totalDosage = dosageCount.multiply(numberOfBoxes);

        //总消耗量= 每天用药次数*每次剂量*用药天数（开始吃药日期到当前日期的天数）
//        BigDecimal numberOfDay = new BigDecimal(model.getNumberOfDay());
//        BigDecimal dose = new BigDecimal(model.getDose());
//        BigDecimal cycleDay = new BigDecimal(model.getCycleDay() +1);

        // 新规则 用药周期数 X 周期内用药次数 X 每次用药剂量 = 患者所需的总药量
        // 周期内用药次数
        BigDecimal numberOfDay = new BigDecimal(model.getNumberOfDay());
        // 每次剂量
        BigDecimal dose = new BigDecimal(model.getDose());
        // 用药周期数
        BigDecimal cycleDay = new BigDecimal(model.getCycleDay());

        // 所需总药量
        BigDecimal totalNeedDosage = numberOfDay.multiply(dose).multiply(cycleDay);

        // 得出所需总量超过 药箱中总药量时，说明药品需要预警
        if (totalDosage.compareTo(totalNeedDosage) < 0) {
            // 需要预警，总药量不够吃。
            if (EarlyWarningMonitorEnum.NO_NEED_MONITOR.code.equals(model.getEarlyWarningMonitor())) {
                // 检查是不是已经生成过预警了。如果生成过预警，就不需要监听了。 如果没有生成过预警，则加入可监听列表
                int count = drugsResultInformationService.count(Wraps.<DrugsResultInformation>lbQ()
                        .eq(DrugsResultInformation::getPatientId, model.getPatientId())
                        .eq(DrugsResultInformation::getDrugsId, model.getDrugsId()));
                if (count > 0) {
                    drugsResultInformationService.remove(Wraps.<DrugsResultInformation>lbQ()
                    .eq(DrugsResultInformation::getPatientId, model.getPatientId())
                    .eq(DrugsResultInformation::getDrugsId, model.getDrugsId()));
                }
                model.setEarlyWarningMonitor(EarlyWarningMonitorEnum.CAN_MONITOR.code);
                baseMapper.updateById(model);
            }
        } else {
            // 不需要预警，总药量够吃
            if (EarlyWarningMonitorEnum.CAN_MONITOR.code.equals(model.getEarlyWarningMonitor())) {
                model.setEarlyWarningMonitor(EarlyWarningMonitorEnum.NO_NEED_MONITOR.code);
                baseMapper.updateById(model);
            } else {
                int count = drugsResultInformationService.count(Wraps.<DrugsResultInformation>lbQ()
                        .eq(DrugsResultInformation::getPatientId, model.getPatientId())
                        .eq(DrugsResultInformation::getDrugsId, model.getDrugsId()));
                if (count > 0) {
                    drugsResultInformationService.remove(Wraps.<DrugsResultInformation>lbQ()
                            .eq(DrugsResultInformation::getPatientId, model.getPatientId())
                            .eq(DrugsResultInformation::getDrugsId, model.getDrugsId()));
                }
            }
        }

    }


    @Override
    public void updateAllPatientDrugsTime() {

        List<PatientDrugs> patientDrugs = baseMapper.selectList(Wraps.<PatientDrugs>lbQ().eq(PatientDrugs::getStatus, 0));
        if (CollUtil.isNotEmpty(patientDrugs)) {
            return;
        }
        for (PatientDrugs drug : patientDrugs) {
            List<PatientDrugsTimeSetting> drugsTimeSettings = patientDrugsTimeSettingMapper.selectList(Wraps.<PatientDrugsTimeSetting>lbQ()
                    .eq(PatientDrugsTimeSetting::getPatientDrugsId, drug.getId())
                    .orderByAsc(PatientDrugsTimeSetting::getDayOfTheCycle)
                    .orderByAsc(PatientDrugsTimeSetting::getTriggerTimeOfTheDay));
            drug.setNextReminderDate(PatientDrugsNextReminderDateUtil.getPatientDrugsNextReminderDate(drug, drugsTimeSettings));
            baseMapper.updateById(drug);
        }


    }
}
