package com.caring.sass.user.service;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.dto.StatisticsDiagnosticResult;
import com.caring.sass.user.dto.StatisticsPatientDto;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;

import java.util.List;
import java.util.Map;

/**
 * @ClassName StatisticsService
 * @Description
 * @Author yangShuai
 * @Date 2020/11/27 9:41
 * @Version 1.0
 */
public interface StatisticsService {
    List<StatisticsPatientDto> countPatient(Long objId, Long doctorId, Map<String, String> dictionaryMap);

    List<StatisticsPatientDto> countPatientSex(Long nursingStaffId, Long doctorId);

    List<StatisticsPatientDto> countPatientDiagnosisId(Long objId);

    List<StatisticsPatientDto> countPatientAge(Long objId, Long doctorId);

    /**
     * 查询项目患者数
     *
     * @return 项目患者数
     */
    Integer findTotalPatient(LbqWrapper<Patient> queryWrapper);

    /**
     * 查询项目护理医助数
     *
     * @return 护理医助数
     */
    Integer findTotalNursingStaff(LbqWrapper<NursingStaff> queryWrapper);

    /**
     * 查询项目医生数
     *
     * @return 项目医生数
     */
    Integer findTotalDoctor(LbqWrapper<Doctor> queryWrapper);

    JSONObject statisticPatientByDoctorId(Long doctorId);

    /**
     * 医生的患者统计大盘
     *
     * @param doctorId 医生id
     * @return 统计大盘数据
     */
    JSONObject statisticDashboard(Long doctorId);

    JSONObject statisticPatientByDoctorId(Long userId, String dimension);


    /**
     * 统计诊断类型
     * @param orgIds
     * @param nursingId
     * @param doctorId
     * @return
     */
    StatisticsDiagnosticResult diagnosticTypeStatistics(List<Long> orgIds, Long nursingId, Long doctorId);

    /**
     * 统计医生
     * @param between
     * @return
     */
    Integer findLoginDoctor(LbqWrapper<Doctor> between);
}
