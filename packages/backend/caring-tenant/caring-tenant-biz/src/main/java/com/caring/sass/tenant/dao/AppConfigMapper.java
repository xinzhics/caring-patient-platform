package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.AppConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 项目APP配置
 * </p>
 *
 * @author leizhi
 * @date 2020-09-21
 */
@Repository
public interface AppConfigMapper extends SuperMapper<AppConfig> {

    /**
     * 查询所有项目app配置
     *
     * @param codes 项目code
     */
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<AppConfig> listAllConfigByTenantCode(List<String> codes);



    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<AppConfig> selectAppointmentDoctorScope();


}
