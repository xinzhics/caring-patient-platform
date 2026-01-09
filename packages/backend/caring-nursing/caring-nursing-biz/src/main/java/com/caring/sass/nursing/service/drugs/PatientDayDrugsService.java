package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.drugs.CalendarVo;
import com.caring.sass.nursing.entity.drugs.PatientDayDrugs;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者每天的用药量记录（一天生成一次）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface PatientDayDrugsService extends SuperService<PatientDayDrugs> {

    PatientDayDrugs createPatientDayDrugs(NursingPlanPatientBaseInfoDTO patientBaseInfo);



    CalendarVo calendar(String date, Long patientId);


    List<PatientDayDrugs> getPatientDayDrugs(LocalDate now);

    boolean existPatientDrugsToday(Long patientId, Long drugsId, LocalDate now);


    CalendarVo calendar(LocalDate startTime, LocalDate now, Long patientId);
}
