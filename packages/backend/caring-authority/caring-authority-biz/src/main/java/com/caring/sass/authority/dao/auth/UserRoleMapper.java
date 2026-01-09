package com.caring.sass.authority.dao.auth;

import com.caring.sass.authority.entity.auth.UserRole;
import com.caring.sass.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 角色分配
 * 账号角色绑定
 * </p>
 *
 * @author caring
 * @date 2019-07-03
 */
@Repository
public interface UserRoleMapper extends SuperMapper<UserRole> {

}
