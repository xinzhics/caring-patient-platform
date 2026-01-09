package com.caring.sass.user.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.user.merck.MerckPerson;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 敏识用户
 * </p>
 *
 * @author 杨帅
 * @date 2023-12-20
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface MerckPersonMapper extends SuperMapper<MerckPerson> {

}
