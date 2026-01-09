package com.caring.sass.authority.service.auth;

import com.caring.sass.authority.entity.auth.RoleOrg;
import com.caring.sass.base.service.SuperService;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 角色组织关系
 * </p>
 *
 * @author caring
 * @date 2019-07-03
 */
public interface RoleOrgService extends SuperService<RoleOrg> {

    /**
     * 根据角色id查询
     *
     * @param id
     * @return
     */
    List<Long> listOrgByRoleId(Long id);
}
