package com.caring.sass.nursing.service.drugs.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.drugs.PatientDrugsMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsTimeSettingMapper;
import com.caring.sass.nursing.dto.drugs.DrugsArrangeVo;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTime;
import com.caring.sass.nursing.service.drugs.PatientDrugsService;
import com.caring.sass.nursing.service.drugs.PatientDrugsTimeService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.nursing.util.PatientDrugsNextReminderDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 每次推送生成一条记录，（记录药量，药品）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service
public class PatientDrugsTimeServiceImpl extends SuperServiceImpl<PatientDrugsTimeMapper, PatientDrugsTime> implements PatientDrugsTimeService {


    @Autowired
    PatientDrugsService patientDrugsService;

    @Autowired
    PatientDrugsTimeSettingMapper patientDrugsTimeSettingMapper;

    /**
     * 查询患者 当前正在使用的用药。根据用药设置，生成此时刻及此时刻之后的用药安排。
     */
    @Override
    public List<PatientDrugsTime> queryFutureDrugTime(Long patientId) {
        List<PatientDrugs> patientDrugs = patientDrugsService.list(Wraps.<PatientDrugs>lbQ().eq(PatientDrugs::getPatientId, patientId).eq(PatientDrugs::getStatus, 0));
        List<PatientDrugsTime> currentDrugTimes = new ArrayList<>();
        if (CollUtil.isNotEmpty(patientDrugs)) {
            patientDrugsService.setDrugsTimeSetting(patientDrugs);
            for (PatientDrugs patientDrug : patientDrugs) {
                LocalDateTime nextReminderDate = patientDrug.getNextReminderDate();
                while (nextReminderDate != null && nextReminderDate.toLocalDate().equals(LocalDate.now())) {
                    currentDrugTimes.add(PatientDrugsTime
                            .builder()
                            .drugsTime(nextReminderDate)
                            .drugsId(patientDrug.getDrugsId())
                            .patientDrugsId(patientDrug.getId())
                            .status(2)
                            .drugsDose(patientDrug.getDose())
                            .patientId(patientDrug.getPatientId())
                            .medicineIcon(patientDrug.getMedicineIcon())
                            .medicineName(patientDrug.getMedicineName())
                            .unit(patientDrug.getUnit()).build());
                    nextReminderDate = PatientDrugsNextReminderDateUtil.getPatientDrugsNextReminderDate(patientDrug, patientDrug.getPatientDrugsTimeSettingList(), nextReminderDate);
                }
            }
        }
        return currentDrugTimes;
    }


    /**
     * 患者药品id。
     * @param patientDrugsId
     * @param cycleStartTimeDay 最近的一个周期开始时间
     * @param startTimeDay 患者首次开始吃药的时间
     * @return
     */
    @Override
    public float sumPatientDrugsDose(Long patientDrugsId, LocalDateTime cycleStartTimeDay, LocalDateTime startTimeDay) {

        List<Map<String, Object>> mapList = baseMapper.selectMaps(Wrappers.<PatientDrugsTime>query()
                .select("sum(drugs_dose) as drugsDoseSum")
                .eq("patient_drugs_id", patientDrugsId)
                .lt("drugs_time", cycleStartTimeDay)
                .ge("drugs_time", startTimeDay)
        );
        if (CollUtil.isNotEmpty(mapList)) {
            Map<String, Object> stringObjectMap = mapList.get(0);
            if (stringObjectMap == null) {
                return 0f;
            }
            Object doseSum = stringObjectMap.get("drugsDoseSum");
            if (doseSum != null) {
                return Float.parseFloat(doseSum.toString());
            }
        }
        return 0.0f;
    }


    /**
     * 查询这个过去 或 今天的用药安排。
     * @param patientId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<PatientDrugsTime> queryByDrugsTimestamp(Long patientId, LocalDateTime startTime, LocalDateTime endTime) {

        // 已经推送的
        List<PatientDrugsTime> patientDrugsTimes = baseMapper.selectList(Wraps.<PatientDrugsTime>lbQ()
                .eq(PatientDrugsTime::getPatientId, patientId)
                .between(PatientDrugsTime::getDrugsTime, startTime, endTime));
        if (patientDrugsTimes == null) {
            patientDrugsTimes = new ArrayList<>();
        }
        // 如果 日期是今天 还要根据药箱中的正在使用的药品。生成今日剩余的推送安排。
        if (startTime.toLocalDate().equals(LocalDate.now())) {
            List<PatientDrugsTime> drugsTimeList = queryFutureDrugTime(patientId);
            patientDrugsTimes.addAll(drugsTimeList);
        }
        return patientDrugsTimes;
    }



    /**
     * 查询患者今天的吃药时间点。
     * @param patientId
     * @return
     */
    @Override
    public Map<LocalTime, Integer> queryTodayDrugsTimes(Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        List<PatientDrugsTime> drugsTimes = queryByDrugsTimestamp(patientId, now.with(LocalTime.MIN), now.with(LocalTime.MAX));
        if (CollUtil.isEmpty(drugsTimes)) {
            return new HashMap<>();
        }
        Map<LocalTime, Integer> hashMap = new HashMap<>();
        for (PatientDrugsTime time : drugsTimes) {
            if (time.getStatus() != null && time.getStatus().equals(CommonStatus.YES)) {
                continue;
            }
            LocalTime localTime = time.getDrugsTime().toLocalTime().plusSeconds(0).plusNanos(0);
            hashMap.put(localTime, time.getStatus());
        }
        return hashMap;
    }

}
