package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.user.dto.StatisticsPatientDto;
import com.caring.sass.user.entity.Patient;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 患者表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Repository
public interface PatientMapper extends SuperMapper<Patient> {



    @Select(value = "SELECT count(sex) as countsex, sex FROM u_user_patient WHERE service_advisor_id = #{serviceAdvisorId} and sex is not null group by sex")
    @Results({
            @Result(column="sex",javaType=String.class,property="key"),
            @Result(column="countsex",javaType=Integer.class,property="count")
    })
    @SqlParser(filter = true)
    List<StatisticsPatientDto> countSexGroupBySex(@Param("serviceAdvisorId") Long serviceAdvisorId);

    @Select(value = "SELECT count(sex) as countsex, sex FROM u_user_patient WHERE doctor_id = #{doctorId} and sex is not null group by sex")
    @Results({
            @Result(column="sex",javaType=String.class,property="key"),
            @Result(column="countsex",javaType=Integer.class,property="count")
    })
    @SqlParser(filter = true)
    List<StatisticsPatientDto> countDoctorPatientSexGroupBySex(@Param("doctorId") Long doctorId);


    @Select(value = "SELECT count(diagnosis_id) as countDiagnosisId, diagnosis_id FROM u_user_patient WHERE service_advisor_id = #{serviceAdvisorId} and diagnosis_id is not null group by diagnosis_id")
    @Results({
            @Result(column="diagnosis_id",javaType=String.class,property="key"),
            @Result(column="countDiagnosisId",javaType=Integer.class,property="count")
    })
    @SqlParser(filter = true)
    List<StatisticsPatientDto> countPatientDiagnosisId(@Param("serviceAdvisorId") Long serviceAdvisorId);


    @Select(value = "SELECT" +
            " IF (( DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( t.birthday ) ), '%Y' ) + 0 ) < 18, 'less18'," +
            " IF (( DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( t.birthday ) ), '%Y' ) + 0 ) < 25, 'more18less25'," +
            " IF (( DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( t.birthday ) ), '%Y' ) + 0 ) < 30, 'more25less30'," +
            " IF (( DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( t.birthday ) ), '%Y' ) + 0 ) < 50, 'more30less50', 'more51' ) ) ) ) AS age, count(1) as countAge" +
            " FROM" +
            " u_user_patient t where t.doctor_id = #{doctorId} and t.birthday is not null" +
            " group by age")
    @Results({
            @Result(column="age",javaType=String.class,property="key"),
            @Result(column="countAge",javaType=Integer.class,property="count")
    })
    @SqlParser(filter = true)
    List<StatisticsPatientDto> countDoctorPatientAge(@Param("doctorId") Long doctorId);


    @Select(value = "SELECT" +
            " IF (( DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( t.birthday ) ), '%Y' ) + 0 ) < 18, 'less18'," +
            " IF (( DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( t.birthday ) ), '%Y' ) + 0 ) < 25, 'more18less25'," +
            " IF (( DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( t.birthday ) ), '%Y' ) + 0 ) < 30, 'more25less30'," +
            " IF (( DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( t.birthday ) ), '%Y' ) + 0 ) < 50, 'more30less50', 'more51' ) ) ) ) AS age, count(1) as countAge" +
            " FROM" +
            " u_user_patient t where t.service_advisor_id = #{serviceAdvisorId} and t.birthday is not null" +
            " group by age")
    @Results({
            @Result(column="age",javaType=String.class,property="key"),
            @Result(column="countAge",javaType=Integer.class,property="count")
    })
    @SqlParser(filter = true)
    List<StatisticsPatientDto> countPatientAge(@Param("serviceAdvisorId") Long serviceAdvisorId);


    /**
     * 带数据权限的分页查询
     *
     * @param page
     * @param wrapper
     * @param dataScope
     * @return
     */
    IPage<Patient> findPage(IPage<Patient> page, @Param(Constants.WRAPPER) Wrapper<Patient> wrapper, DataScope dataScope);

}
