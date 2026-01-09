package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.user.entity.StatisticsNursingStaff;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 护理医助统计
 * </p>
 *
 * @author leizhi
 * @date 2021-11-05
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface StatisticsNursingStaffMapper extends SuperMapper<StatisticsNursingStaff> {

    /**
     * 通过数据库聚合函数查询统计数据
     *
     * @return 统计列表
     */
    List<StatisticsNursingStaff> queryDb();

    /**
     * 查询所有患者数
     *
     * @return 所有患者数
     */
    @Select("select count(*) from u_user_patient")
    Long queryAllPatientNum();

    /**
     * 查询所有医生数
     *
     * @return 所有医生数
     */
    @Select("select count(*) from u_user_doctor")
    Long queryAllDoctorNum();

    /**
     * 查询所有医助数
     *
     * @return 所有医助数
     */
    @Select("select count(*) from u_user_nursing_staff")
    Long queryAllNursingStaffNum();

    /**
     * 查询所有项目数
     *
     * @return 所有项目数
     */
    @Select("select count(*) from d_tenant where status = 'NORMAL' ")
    Long queryAllTenantNum();


    @Select("select count(*) from u_user_doctor where tenant_code in (select code from d_tenant as dt where dt.id in (SELECT tenant_id FROM d_global_user_tenant where  account_id = #{userId}))")
    Long queryCustomersDoctorNum(@Param("userId") Long userId);

    @Select("select count(*) from u_user_patient where tenant_code in (select code from d_tenant as dt where dt.id in (SELECT tenant_id FROM d_global_user_tenant where  account_id = #{userId}))")
    Long queryCustomersPatientNum(@Param("userId") Long userId);

    @Select("select count(*) from u_user_nursing_staff where tenant_code in (select code from d_tenant as dt where dt.id in (SELECT tenant_id FROM d_global_user_tenant where  account_id = #{userId}))")
    Long queryCustomersNursingStaffNum(@Param("userId") Long userId);

    @Select("select count(*) from d_tenant where status = 'NORMAL' and id in (SELECT tenant_id FROM d_global_user_tenant where account_id = #{userId})")
    Long queryCustomersTenantNum(@Param("userId") Long userId);
}
