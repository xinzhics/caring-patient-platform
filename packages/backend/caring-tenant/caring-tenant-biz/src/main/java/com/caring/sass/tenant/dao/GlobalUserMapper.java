package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.tenant.entity.GlobalUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 全局账号
 * </p>
 *
 * @author caring
 * @date 2019-10-25
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface GlobalUserMapper extends SuperMapper<GlobalUser> {

}
