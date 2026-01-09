package com.caring.sass.nursing.service.unfinished.impl;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeMapper;
import com.caring.sass.nursing.dao.plan.ReminderLogMapper;
import com.caring.sass.nursing.dao.unfinished.UnfinishedFormSettingMapper;
import com.caring.sass.nursing.dao.unfinished.UnfinishedPatientRecordMapper;
import com.caring.sass.nursing.dto.traceInto.AppTracePlanList;
import com.caring.sass.nursing.dto.unfinished.UnfinishedListResult;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTime;
import com.caring.sass.nursing.entity.plan.ReminderLog;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFormResultLastPushTime;
import com.caring.sass.nursing.entity.unfinished.UnfinishedFormSetting;
import com.caring.sass.nursing.entity.unfinished.UnfinishedPatientRecord;
import com.caring.sass.nursing.enumeration.MonitoringTaskType;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.unfinished.UnfinishedPatientRecordService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 未完成推送的患者记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-05-27
 */
@Slf4j
@Service

public class UnfinishedPatientRecordServiceImpl extends SuperServiceImpl<UnfinishedPatientRecordMapper, UnfinishedPatientRecord> implements UnfinishedPatientRecordService {


    @Autowired
    ReminderLogMapper reminderLogMapper;

    @Autowired
    PatientApi patientApi;

    @Autowired
    PatientDrugsTimeMapper patientDrugsTimeMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UnfinishedFormSettingMapper unfinishedFormSettingMapper;

    /**
     * 统计医助下患者有多少有未完成的提醒
     * @param nursingId
     * @return
     */
    @Override
    public int countNursingHandleNumber(Long nursingId) {
        List<UnfinishedFormSetting> formSettings = unfinishedFormSettingMapper.selectList(Wraps.<UnfinishedFormSetting>lbQ());
        List<Long> settingIds = new ArrayList<>();
        for (UnfinishedFormSetting setting : formSettings) {
            String medicinePush = setting.getMedicinePush();
            if (StrUtil.isNotBlank(medicinePush) && UnfinishedFormSetting.MEDICINE_PUSH_YES.equals(medicinePush)) {
                settingIds.add(setting.getId());
            }
            if (StrUtil.isEmpty(medicinePush)) {
                settingIds.add(setting.getId());
            }
        }
        if (settingIds.isEmpty()) {
            return 0;
        }
        Integer selectCount = baseMapper.selectCount(Wraps.<UnfinishedPatientRecord>lbQ()
                .eq(UnfinishedPatientRecord::getNursingId, nursingId)
                .in(UnfinishedPatientRecord::getUnfinishedFormSettingId, settingIds)
                .eq(UnfinishedPatientRecord::getDeleteFlag, CommonStatus.NO)
                .eq(UnfinishedPatientRecord::getHandleStatus, CommonStatus.NO));
        if (selectCount == null) {
            return 0;
        }
        return selectCount;
    }

    /**
     * 立即执行 或 8点执行
     * 查询 未完成的用药打卡任务
     */
    public List<PatientDrugsTime> queryMedicineCheckInUnFinished(Boolean isTime) {

        // 如果不是立即执行。并且发起时间不是 8 点。那么就返回空
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        if (!isTime && hour != 8) {
            return Collections.emptyList();
        }

        // 查询昨天的吃药打卡 未完成的记录
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startTime = yesterday.atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(1);;
        List<PatientDrugsTime> patientDrugsTimes = patientDrugsTimeMapper.selectList(Wraps.<PatientDrugsTime>lbQ()
                .eq(PatientDrugsTime::getStatus, 2)
                .between(PatientDrugsTime::getDrugsTime, startTime, endTime)
                .eq(PatientDrugsTime::getNeedPush, CommonStatus.YES));
        return patientDrugsTimes;

    }

    /**
     * 查询要求的时间内。没有完成的随访提醒
     * @param planId
     * @param day
     * @return
     */
    public List<ReminderLog> queryPlanUnFinished(Long planId, String reminderTime, Integer day, Boolean isTime) {

        if (day == null) {
            return Collections.emptyList();
        }
        LocalDateTime startTime;
        LocalDateTime endTime;
        // 当选择是其他时，day 按 小时来计算
        if ("其他".equals(reminderTime)) {
            LocalDateTime dateTime = LocalDateTime.now().withSecond(0).withNano(0).withMinute(0);
            startTime = dateTime.plusHours(-day);
            endTime = startTime.withMinute(59).withSecond(59).withNano(999);
            return reminderLogMapper.selectList(Wraps.<ReminderLog>lbQ()
                    .eq(ReminderLog::getPlanId, planId)
                    .eq(ReminderLog::getFinishThisCheckIn, CommonStatus.NO)
                    .ge(SuperEntity::getCreateTime, startTime)
                    .lt(ReminderLog::getCreateTime, endTime));
        } else {
            LocalDate yesterday = LocalDate.now().minusDays(day);
            startTime = yesterday.atStartOfDay();
            endTime = startTime.plusDays(1);
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            // 如果不是立即执行。并且发起时间不是 8 点。那么就返回空
            if (!isTime && hour != 8) {
                return Collections.emptyList();
            }
            return reminderLogMapper.selectList(Wraps.<ReminderLog>lbQ()
                    .eq(ReminderLog::getPlanId, planId)
                    .eq(ReminderLog::getFinishThisCheckIn, CommonStatus.NO)
                    .ge(SuperEntity::getCreateTime, startTime)
                    .lt(ReminderLog::getCreateTime, endTime));
        }
    }


    /**
     * 查询患者信息
     * @param patientId
     * @return
     */
    public Patient getPatientById(Long patientId) {

        R<Patient> patientR = patientApi.getBaseInfoForStatisticsData(patientId);
        if (patientR.getIsSuccess()) {
            return patientR.getData();
        }
        return null;
    }


    /**
     * 创建未完成用药打卡记录。
     * @param formSetting
     * @param patientDrugsTimes
     * @param patientMap
     */
    public void createMedicineUnFinished(UnfinishedFormSetting formSetting, List<PatientDrugsTime> patientDrugsTimes, Map<Long, Patient> patientMap) {

        if (patientDrugsTimes.isEmpty()) {
            return;
        }
        List<UnfinishedPatientRecord> saveList = new ArrayList<>();
        UnfinishedPatientRecord record;
        for (PatientDrugsTime drugsTime : patientDrugsTimes) {
            Long patientId = drugsTime.getPatientId();
            Patient patient = patientMap.get(patientId);
            // 如果患者信息不存在。那么就跳过。
            if (patient == null) {
                patient = getPatientById(patientId);
                if (patient == null) {
                    continue;
                }
                patientMap.put(patientId, patient);
            }
            Long drugsTimeId = drugsTime.getId();
            Integer selected = baseMapper.selectCount(Wraps.<UnfinishedPatientRecord>lbQ().eq(UnfinishedPatientRecord::getRemindMessageId, drugsTimeId));
            if (selected == null || selected == 0) {
                record = new UnfinishedPatientRecord();
                record.setPatientId(patientId);
                record.setRemindMessageId(drugsTimeId);
                record.setRemindTime(drugsTime.getDrugsTime());
                record.setHandleStatus(CommonStatus.NO);
                record.setClearStatus(CommonStatus.NO);
                record.setSeeStatus(CommonStatus.NO);
                record.setDoctorId(patient.getDoctorId());
                record.setNursingId(patient.getServiceAdvisorId());
                record.setUnfinishedFormSettingId(formSetting.getId());
                record.setMedicineOrForm(2);
                record.setPlanType(formSetting.getPlanType());
                record.setDeleteFlag(CommonStatus.NO);
                saveList.add(record);
            }

        }
        if (!saveList.isEmpty()) {
            baseMapper.insertBatchSomeColumn(saveList);
        }
    }


    /**
     * 立即执行，计算未完成的任务
     * @param formSettingList
     * @param tenantCode
     */
    @Override
    public void unFinishedTask(List<UnfinishedFormSetting> formSettingList, String tenantCode, Boolean isTime) {

        BaseContextHandler.setTenant(tenantCode);
        Map<Long, Patient> patientMap = new HashMap<>();
        for (UnfinishedFormSetting formSetting : formSettingList) {
            String medicinePush = formSetting.getMedicinePush();
            Long planId = formSetting.getPlanId();
            if (UnfinishedFormSetting.MEDICINE_PUSH_YES.equals(medicinePush)) {
                List<PatientDrugsTime> drugsTimes = queryMedicineCheckInUnFinished(isTime);
                createMedicineUnFinished(formSetting, drugsTimes, patientMap);

            } else if (Objects.nonNull(planId) && StrUtil.isEmpty(medicinePush)){
                // 处理随访计划的表单。
                String reminderTime = formSetting.getReminderTime();
                Integer reminderDay = formSetting.getReminderDay();
                // 查询计划提醒后，用户还一直没有完成的记录
                List<ReminderLog> reminderLogs = queryPlanUnFinished(planId, reminderTime, reminderDay, isTime);

                createPlanUnFinished(formSetting, reminderLogs);

            }
        }
        redisTemplate.delete(SaasRedisBusinessKey.UN_FINISHED_REMIND+":"+tenantCode);
        redisTemplate.opsForValue().set(SaasRedisBusinessKey.UN_FINISHED_REMIND+":"+tenantCode, MonitoringTaskType.FINISH.operateType);
        BaseContextHandler.setTenant(tenantCode);
    }

    /**
     * 创建计划的未完成记录
     * @param formSetting
     * @param reminderLogs
     */
    private void createPlanUnFinished(UnfinishedFormSetting formSetting, List<ReminderLog> reminderLogs) {

        if (reminderLogs.isEmpty()) {
            return;
        }
        List<UnfinishedPatientRecord> saveList = new ArrayList<>();
        UnfinishedPatientRecord record;
        for (ReminderLog reminderLog : reminderLogs) {
            Long patientId = reminderLog.getPatientId();
            Long reminderLogId = reminderLog.getId();
            Integer selected = baseMapper.selectCount(Wraps.<UnfinishedPatientRecord>lbQ().eq(UnfinishedPatientRecord::getRemindMessageId, reminderLogId));
            if (selected == null || selected == 0) {
                record = new UnfinishedPatientRecord();
                record.setPatientId(patientId);
                record.setRemindMessageId(reminderLogId);
                record.setRemindTime(reminderLog.getCreateTime());
                record.setHandleStatus(CommonStatus.NO);
                record.setClearStatus(CommonStatus.NO);
                record.setSeeStatus(CommonStatus.NO);
                record.setDoctorId(reminderLog.getDoctorId());
                record.setNursingId(reminderLog.getNursingId());
                record.setUnfinishedFormSettingId(formSetting.getId());
                record.setMedicineOrForm(1);
                record.setPlanType(formSetting.getPlanType());
                record.setDeleteFlag(CommonStatus.NO);
                saveList.add(record);
            }

        }
        if (!saveList.isEmpty()) {
            baseMapper.insertBatchSomeColumn(saveList);
        }
    }


    public List<AppTracePlanList> getAppUnFinishedPlanList(Long nursingId) {

        List<UnfinishedFormSetting> formSettings = unfinishedFormSettingMapper.selectList(Wraps.<UnfinishedFormSetting>lbQ());
        List<Long> ids = new ArrayList<>();
        for (UnfinishedFormSetting formSetting : formSettings) {
            String medicinePush = formSetting.getMedicinePush();
            if (UnfinishedFormSetting.MEDICINE_PUSH_YES.equals(medicinePush)) {
                ids.add(formSetting.getId());
            } else if (Objects.nonNull(formSetting.getPlanId()) && medicinePush == null) {
                // 是其他计划的配置，并且不是用药提醒
                ids.add(formSetting.getId());
            }
        }
        Map<Long, Integer> unFinishedMap = new HashMap<>();
        if (CollUtil.isNotEmpty(ids)) {
            QueryWrapper<UnfinishedPatientRecord> queryWrapper = Wrappers.<UnfinishedPatientRecord>query();
            queryWrapper.select("unfinished_form_setting_id as unfinishedFormSettingId", "count(*) as total");
            queryWrapper.in("unfinished_form_setting_id", ids);
            queryWrapper.eq("nursing_id", nursingId);
            queryWrapper.eq("handle_status", CommonStatus.NO);
            queryWrapper.eq("delete_flag", CommonStatus.NO);
            queryWrapper.groupBy("unfinished_form_setting_id");
            List<Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);
            if (CollUtil.isNotEmpty(mapList)) {
                for (Map<String, Object> objectMap : mapList) {
                    Object unfinishedFormSettingId = objectMap.get("unfinishedFormSettingId");
                    Object total = objectMap.get("total");
                    if (total != null && unfinishedFormSettingId != null) {
                        unFinishedMap.put(Long.parseLong(unfinishedFormSettingId.toString()), Integer.parseInt(total.toString()));
                    }
                }
            }
        }
        List<AppTracePlanList> planList = new ArrayList<>();
        AppTracePlanList appTracePlanList;
        for (UnfinishedFormSetting setting : formSettings) {
            if (ids.contains(setting.getId())) {
                Integer integer = unFinishedMap.get(setting.getId());
                appTracePlanList = new AppTracePlanList();
                appTracePlanList.setPlanId(setting.getPlanId());
                appTracePlanList.setFormId(setting.getFormId());
                appTracePlanList.setPlanType(PlanEnum.getPlanEnum(setting.getPlanType()));
                appTracePlanList.setNoHandlePatientNumber(integer == null ? 0 : integer);
                appTracePlanList.setUnFinishedSettingId(setting.getId());
                appTracePlanList.setPlanName(setting.getPlanName());
                planList.add(appTracePlanList);
            }
        }
        return planList;
    }


    @Override
    public IPage<UnfinishedListResult> appPage(IPage<UnfinishedPatientRecord> page, LbqWrapper<UnfinishedPatientRecord> lbqWrapper) {

        IPage<UnfinishedListResult> result = new Page<>();
        IPage<UnfinishedPatientRecord> recordIPage = baseMapper.selectPage(page, lbqWrapper);
        result.setTotal(recordIPage.getTotal());
        result.setCurrent(recordIPage.getCurrent());
        result.setSize(recordIPage.getSize());
        result.setPages(recordIPage.getPages());
        List<UnfinishedListResult> records = new ArrayList<>();
        result.setRecords(records);
        List<UnfinishedPatientRecord> pageRecords = recordIPage.getRecords();
        if (CollUtil.isEmpty(pageRecords)) {
            return result;
        }
        List<Long> patientIds = pageRecords.stream().map(UnfinishedPatientRecord::getPatientId).collect(Collectors.toList());
        if (CollUtil.isEmpty(patientIds)) {
            return result;
        }
        R<List<Patient>> apiByIds = patientApi.findByIds(patientIds);
        if (CollUtil.isNotEmpty(apiByIds.getData())) {
            Map<Long, Patient> patientMap = apiByIds.getData().stream().collect(Collectors.toMap(Patient::getId, item -> item));
            for (UnfinishedPatientRecord record : recordIPage.getRecords()) {
                UnfinishedListResult listResult = new UnfinishedListResult();
                Patient patient = patientMap.get(record.getPatientId());
                listResult.setId(record.getId());
                listResult.setPatientId(record.getPatientId());
                listResult.setDoctorId(record.getDoctorId());
                if (patient != null) {
                    listResult.setDoctorName(patient.getDoctorName());
                    listResult.setPatientName(patient.getName());
                    listResult.setPatientAvatar(patient.getAvatar());
                    listResult.setPatientImAccount(patient.getImAccount());
                    listResult.setPatientFollowStatus(patient.getStatus());
                }
                listResult.setSeeStatus(record.getSeeStatus());
                listResult.setRemindTime(record.getRemindTime());
                listResult.setHandleTime(record.getHandleTime());
                records.add(listResult);
            }
        }
        return result;
    }
}
