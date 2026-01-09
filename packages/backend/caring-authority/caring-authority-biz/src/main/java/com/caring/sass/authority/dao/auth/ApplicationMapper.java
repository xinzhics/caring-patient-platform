package com.caring.sass.authority.dao.auth;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.authority.entity.auth.Application;
import com.caring.sass.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * Mapper 接口
 * 应用
 * </p>
 *
 * @author caring
 * @date 2019-12-15
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface ApplicationMapper extends SuperMapper<Application> {
}
