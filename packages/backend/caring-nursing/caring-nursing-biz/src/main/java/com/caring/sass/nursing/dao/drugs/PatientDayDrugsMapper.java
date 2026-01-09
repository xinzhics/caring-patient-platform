package com.caring.sass.nursing.dao.drugs;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.drugs.PatientDayDrugs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 患者每天的用药量记录（一天生成一次）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface PatientDayDrugsMapper extends SuperMapper<PatientDayDrugs> {


    @Select("select c.id, c.checkined_number, c.checkin_number_total, c.patient_id from t_patient_day_drugs c where DATE_FORMAT(create_time,'%Y-%m-%d') = DATE_FORMAT(#{date},'%Y-%m-%d')")
    @Results({
            @Result(column="id", javaType=Long.class, property="id"),
            @Result(column="patient_id", javaType=Long.class, property="patientId"),
            @Result(column="checkin_number_total", javaType=Integer.class, property="checkinNumberTotal"),
            @Result(column="checkined_number", javaType= Integer.class, property="checkinedNumber"),
    })
    List<PatientDayDrugs> getPatientDayDrugs(@Param("date") LocalDate date);


    @Select("select * from t_patient_day_drugs where patient_id = #{patientId} and DATE_FORMAT(create_time,'%Y-%m') = #{time} order by create_time asc")
    @Results({
            @Result(column="id", javaType=Long.class, property="id"),
            @Result(column="patient_id", javaType=Long.class, property="patientId"),
            @Result(column="checkin_number_total", javaType=Integer.class, property="checkinNumberTotal"),
            @Result(column="take_drugs_count_of_day", javaType=Float.class, property="takeDrugsCountOfDay"),
            @Result(column="status", javaType=Integer.class, property="status"),
            @Result(column="doctor_id", javaType=Long.class, property="doctorId"),
            @Result(column="service_advisor_id", javaType=Long.class, property="serviceAdvisorId"),
            @Result(column="checkined_number", javaType= Integer.class, property="checkinedNumber"),
    })
    List<PatientDayDrugs> calendar(@Param("patientId") Long patientId, @Param("time") String time);


    @Select("select * from t_patient_day_drugs where patient_id = #{patientId} and DATE_FORMAT(create_time,'%Y-%m-%d') = #{time}")
    @Results({
            @Result(column="id", javaType=Long.class, property="id"),
            @Result(column="patient_id", javaType=Long.class, property="patientId"),
            @Result(column="checkin_number_total", javaType=Integer.class, property="checkinNumberTotal"),
            @Result(column="take_drugs_count_of_day", javaType=Float.class, property="takeDrugsCountOfDay"),
            @Result(column="status", javaType=Integer.class, property="status"),
            @Result(column="doctor_id", javaType=Long.class, property="doctorId"),
            @Result(column="service_advisor_id", javaType=Long.class, property="serviceAdvisorId"),
            @Result(column="checkined_number", javaType= Integer.class, property="checkinedNumber"),
    })
    List<PatientDayDrugs> getToday(@Param("patientId") Long patientId, @Param("time") String time);
}
