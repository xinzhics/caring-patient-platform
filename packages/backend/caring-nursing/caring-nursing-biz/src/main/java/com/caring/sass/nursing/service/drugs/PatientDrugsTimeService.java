package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTime;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 每次推送生成一条记录，（记录药量，药品）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface PatientDrugsTimeService extends SuperService<PatientDrugsTime> {

    /**
     * 查询患者未来的 吃药安排。只限今天
     * @param patientId
     * @return
     */
    List<PatientDrugsTime> queryFutureDrugTime(Long patientId);

    float sumPatientDrugsDose(Long patientDrugsId, LocalDateTime cycleStartTimeDay, LocalDateTime startTimeDay);

    List<PatientDrugsTime> queryByDrugsTimestamp(Long patientId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询患者今天的吃药时间点。
     * @param patientId
     * @return
     */
    Map<LocalTime, Integer> queryTodayDrugsTimes(Long patientId);



}
