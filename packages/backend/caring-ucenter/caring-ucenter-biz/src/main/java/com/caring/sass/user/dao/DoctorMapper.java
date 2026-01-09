package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.user.dto.DoctorRanking;
import com.caring.sass.user.dto.GetPatientRecommendDoctorDTO;
import com.caring.sass.user.dto.GetPatientRecommendDoctorVo;
import com.caring.sass.user.entity.Doctor;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 医生表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Repository
public interface DoctorMapper extends SuperMapper<Doctor> {


    @Select(value = "SELECT doctor_id, count(*) total FROM u_user_patient WHERE doctor_id in (SELECT id from u_user_doctor where group_id = #{groupId}) GROUP BY doctor_id ORDER BY total desc")
    @Results({
            @Result(column="doctor_id",javaType=Long.class,property="doctorId"),
            @Result(column="total",property="total",javaType=Long.class)
    })
    List<DoctorRanking> countDoctorRanking(@Param("groupId") Long groupId);


    /**
     * 带数据权限的分页查询
     *
     * @param page
     * @param wrapper
     * @param dataScope
     * @return
     */
    IPage<Doctor> findPage(IPage<Doctor> page, @Param(Constants.WRAPPER) Wrapper<Doctor> wrapper, DataScope dataScope);

    /**
     * 推荐患者-查询医生列表
     * @param page
     * @param dto
     * @return
     */
    IPage<GetPatientRecommendDoctorVo> getPatientRecommendDoctor(Page page, @Param("dto")GetPatientRecommendDoctorDTO dto);


    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Doctor> findByMobileNoTenant(@Param("mobile") String mobile);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<Doctor> findByIdsNoTenant(@Param("ids") List<Long> ids);
}
