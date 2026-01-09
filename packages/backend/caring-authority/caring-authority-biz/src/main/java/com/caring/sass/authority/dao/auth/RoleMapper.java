package com.caring.sass.authority.dao.auth;

import com.caring.sass.authority.entity.auth.Role;
import com.caring.sass.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 角色
 * </p>
 *
 * @author caring
 * @date 2019-07-03
 */
@Repository
public interface RoleMapper extends SuperMapper<Role> {
    /**
     * 查询用户拥有的角色
     *
     * @param userId
     * @return
     */
    List<Role> findRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据角色编码查询用户ID
     *
     * @param codes 角色编码
     * @return
     */
    List<Long> findUserIdByCode(@Param("codes") String[] codes);
}
