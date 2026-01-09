package com.caring.sass.nursing.dao.drugs;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 患者添加的用药
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface PatientDrugsMapper extends SuperMapper<PatientDrugs> {
    /**
     *  查询当前用药患者总数
     * @param drugsId
     * @param code
     * @param userId
     * @return
     */
    Integer doGetDrugsPatient(@Param("drugsId")Long drugsId, @Param("code")Integer code, @Param("userId")Long userId);


    @Select("SELECT tenant_code FROM t_patient_drugs where status = 0 and next_reminder_date >= #{date} and next_reminder_date < #{addDate} group by tenant_code")
    @Results({
            @Result(column="tenant_code", javaType=String.class, property="tenant_code"),
    })
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<String> selectWaitPushTenantCode(@Param("date") LocalDateTime date, @Param("addDate") LocalDateTime addDate);
}
