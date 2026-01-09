package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.TenantOperate;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 项目运营配置
 * </p>
 *
 * @author 杨帅
 * @date 2023-05-10
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface TenantOperateMapper extends SuperMapper<TenantOperate> {

}
