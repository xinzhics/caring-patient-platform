package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.GlobalUserTenant;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 用户项目管理表
 * </p>
 *
 * @author 杨帅
 * @date 2023-04-11
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface GlobalUserTenantMapper extends SuperMapper<GlobalUserTenant> {

}
