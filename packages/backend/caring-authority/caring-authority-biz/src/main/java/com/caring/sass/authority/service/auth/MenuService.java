package com.caring.sass.authority.service.auth;

import com.caring.sass.authority.dto.auth.InitTenantAuth;
import com.caring.sass.authority.entity.auth.Menu;
import com.caring.sass.base.service.SuperCacheService;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 菜单
 * </p>
 *
 * @author caring
 * @date 2019-07-03
 */
public interface MenuService extends SuperCacheService<Menu> {

    /**
     * 根据ID删除
     *
     * @param ids
     * @return
     */
    boolean removeByIdWithCache(List<Long> ids);

    /**
     * 修改菜单
     *
     * @param menu
     * @return
     */
    boolean updateWithCache(Menu menu);

    /**
     * 保存菜单
     *
     * @param menu
     * @return
     */
    boolean saveWithCache(Menu menu);

    /**
     * 查询用户可用菜单
     *
     * @param group
     * @param userId
     * @return
     */
    List<Menu> findVisibleMenu(String group, Long userId);

    /**
     * 给项目初始化菜单
     * @param initTenantAuth
     */
    void initTenantMenu(InitTenantAuth initTenantAuth);
}
