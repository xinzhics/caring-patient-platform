package com.caring.sass.nursing.dao.appointment;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.nursing.entity.appointment.Appointment;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 患者预约表
 * </p>
 *
 * @author leizhi
 * @date 2021-01-27
 */
@Repository
public interface AppointmentMapper extends SuperMapper<Appointment> {

    @Select(value = "SELECT tenant_code  FROM a_appointment WHERE status = #{status} and appoint_date < #{localDate} group by tenant_code")
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<String> getAllTenantCode(@Param("status") Integer status, @Param("localDate") LocalDate localDate);



    @Select(value = "SELECT tenant_code  FROM a_appointment WHERE status = #{status} group by tenant_code")
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<String> getAllTenantCodeByStatus(@Param("status") Integer status);
}
