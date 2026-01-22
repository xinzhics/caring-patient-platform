package com.caring.sass.authority.service.auth.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.authority.dao.auth.MenuMapper;
import com.caring.sass.authority.dao.auth.RoleAuthorityMapper;
import com.caring.sass.authority.dao.auth.RoleMapper;
import com.caring.sass.authority.dto.auth.InitTenantAuth;
import com.caring.sass.authority.dto.auth.InitTenantMenu;
import com.caring.sass.authority.entity.auth.Menu;
import com.caring.sass.authority.entity.auth.Resource;
import com.caring.sass.authority.entity.auth.Role;
import com.caring.sass.authority.entity.auth.RoleAuthority;
import com.caring.sass.authority.enumeration.auth.AuthorizeType;
import com.caring.sass.authority.service.auth.MenuService;
import com.caring.sass.authority.service.auth.ResourceService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperCacheServiceImpl;
import com.caring.sass.common.constant.CacheKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.caring.sass.common.constant.CacheKey.MENU;
import static com.caring.sass.utils.StrPool.DEF_PARENT_ID;

/**
 * <p>
 * 业务实现类
 * 菜单
 * </p>
 *
 * @author caring
 * @date 2019-07-03
 */
@Slf4j
@Service

public class MenuServiceImpl extends SuperCacheServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RoleAuthorityMapper roleAuthorityMapper;

    @Autowired
    TenantApi tenantApi;


    @Override
    protected String getRegion() {
        return MENU;
    }

    /**
     * 查询用户可用菜单
     * 1，查询缓存中存放的当前用户拥有的所有菜单列表 [menuId,menuId...]
     * 2，缓存&DB为空则返回
     * 3，缓存总用户菜单列表 为空，但db存在，则将list便利set到缓存，并直接返回
     * 4，缓存存在用户菜单列表，则根据菜单id遍历去缓存查询菜单。
     * 5，过滤group后，返回
     *
     * <p>
     * 注意：什么地方需要清除 USER_MENU 缓存
     * 给用户重新分配角色时， 角色重新分配资源/菜单时
     *
     * @param group
     * @param userId
     * @return
     */
    @Override
    public List<Menu> findVisibleMenu(String group, Long userId) {
        String key = key(userId);
        List<Menu> visibleMenu = new ArrayList<>();
        CacheObject cacheObject = cacheChannel.get(CacheKey.USER_MENU, key, (k) -> {
            visibleMenu.addAll(baseMapper.findVisibleMenu(userId));
            return visibleMenu.stream().mapToLong(Menu::getId).boxed().collect(Collectors.toList());
        });

        if (cacheObject.getValue() == null) {
            return Collections.emptyList();
        }
        List<Menu> menuList;
        if (!visibleMenu.isEmpty()) {
            // TODO 异步性能 更佳
            visibleMenu.forEach((menu) -> {
                String menuKey = key(menu.getId());
                cacheChannel.set(MENU, menuKey, menu);
            });

            menuList = menuListFilterGroup(group, visibleMenu);
        } else {
            List<Long> list = (List<Long>) cacheObject.getValue();
            menuList = list.stream().map(this::getByIdCache)
                    .filter(Objects::nonNull).collect(Collectors.toList());
        }

        // 约定，个人服务号项目。不需要一下菜单
        //   - 微信推送 -> 素材管理
        //  - 微信推送 -> 推送管理
        //  - AI助手 -> 关注后自动回复
        R<String> accountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
        String accountTypeData = accountType.getData();
        Set<String> excludedPaths = new HashSet<>(Arrays.asList(
                // 微信推送
                "/wxGroup".toLowerCase(),
                // 素材管理  推送管理
                "/wxgroup/materiallibrary".toLowerCase(),
                "/wxgroup/pushmanager".toLowerCase(),
                // 关注后自动回复
                "/assistant/automatic".toLowerCase()
        ));
        if (accountTypeData !=null && accountTypeData.equals(TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString())) {
            menuList = menuList.stream()
                    .filter(menu -> !excludedPaths.contains(menu.getPath().toLowerCase())) // 统一转小写
                    .collect(Collectors.toList());
        }
        return menuListFilterGroup(group, menuList);
    }

    private List<Menu> menuListFilterGroup(String group, List<Menu> visibleMenu) {
        if (StrUtil.isEmpty(group)) {
            return visibleMenu;
        }
        return visibleMenu.stream().filter((menu) -> group.equals(menu.getGroup())).collect(Collectors.toList());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIdWithCache(List<Long> ids) {
        if (ids.isEmpty()) {
            return true;
        }
        boolean result = this.removeByIds(ids);
        if (result) {
            resourceService.removeByMenuIdWithCache(ids);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithCache(Menu menu) {
        boolean result = this.updateById(menu);
        if (result) {
            cacheChannel.clear(CacheKey.USER_MENU);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithCache(Menu menu) {
        menu.setIsEnable(Convert.toBool(menu.getIsEnable(), true));
        menu.setIsPublic(Convert.toBool(menu.getIsPublic(), false));
        menu.setParentId(Convert.toLong(menu.getParentId(), DEF_PARENT_ID));
        save(menu);

        if (menu.getIsPublic()) {
            cacheChannel.evict(CacheKey.USER_MENU);
        }
        return true;
    }


    @Override
    public void initTenantMenu(InitTenantAuth initTenantAuth) {

        List<String> roleCode = initTenantAuth.getRoleCode();
        if (CollUtil.isEmpty(roleCode)) {
            throw new BizException("roleCode不能为空");
        }
        List<String> tenantCode = initTenantAuth.getTenantCode();
        List<InitTenantMenu> menuList = initTenantAuth.getMenuList();
        if (CollUtil.isEmpty(menuList)) {
            throw new BizException("menuList不能为空");
        }
        if (CollUtil.isEmpty(tenantCode)) {
            R<List<Object>> allTenantCode = tenantApi.getAllTenantCode();
            if (allTenantCode.getIsSuccess() != null && allTenantCode.getIsSuccess() && allTenantCode.getData() != null) {
                tenantCode = allTenantCode.getData().stream().map(Object::toString).collect(Collectors.toList());
            } else {
                throw new BizException("没有查询到项目code");
            }
        }
        if (CollUtil.isEmpty(tenantCode)) {
            throw new BizException("没有查询到项目code");
        }
        for (String tenant : tenantCode) {
            BaseContextHandler.setTenant(tenant);

            SaasGlobalThreadPool.execute(() -> saveMenu(menuList, roleCode, tenant));
        }

    }

    /**
     * 保存 菜单
     * @param menuList 只保存第一层的菜单。 InitTenantMenu 中的子菜单 通过递归重新调用此 方法
     * @param roleCode
     * @param tenantCode
     */
    private void saveMenu(List<InitTenantMenu> menuList, List<String> roleCode, String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);

        // 查询要 关联菜单的角色
        List<Role> roles = roleMapper.selectList(Wraps.<Role>lbQ().select(SuperEntity::getId, Role::getCode).in(Role::getCode, roleCode));
        if (CollUtil.isEmpty(roles)) {
            return;
        }

        for (InitTenantMenu tenantMenu : menuList) {
            String path = tenantMenu.getPath();

            // 用菜单路径验证菜单是否已经添加到项目中去
            Integer count = baseMapper.selectCount(Wraps.<Menu>lbQ().eq(Menu::getPath, path));
            List<Resource> resources = tenantMenu.getResources();
            List<InitTenantMenu> menuChildList = null;
            if (count != null && count > 0) {

                // 菜单已经存在了，应该 跳过。但是需要检查有没有下级菜单
                menuChildList = tenantMenu.getMenuChildList();
                if (CollUtil.isEmpty(menuChildList)) {
                    return;
                } else {
                    // 给子菜单设置他们的 父级菜单路径
                    menuChildList.forEach(item -> item.setParentPath(path));
                    saveMenu(menuChildList, roleCode, tenantCode);
                    return;
                }
            } else {
                Long parentId = DEF_PARENT_ID;
                // 菜单还没有添加。 查询菜单是添加为 顶级菜单 还是 子菜单
                if (StringUtils.isNotEmptyString(tenantMenu.getParentPath())) {
                    List<Menu> menus = baseMapper.selectList(Wraps.<Menu>lbQ().select(SuperEntity::getId, Menu::getLabel).eq(Menu::getPath, tenantMenu.getParentPath()));
                    // 有要求父级菜单的路径，但是父级菜单不存在。
                    if (CollUtil.isEmpty(menus)) {
                        return;
                    }
                    Menu menu = menus.get(0);
                    parentId = menu.getId();
                }
                Menu menu = Menu.builder().parentId(parentId)
                        .label(tenantMenu.getLabel())
                        .component(tenantMenu.getComponent())
                        .describe(tenantMenu.getDescribe())
                        .icon(tenantMenu.getIcon())
                        .sortValue(tenantMenu.getSortValue())
                        .path(tenantMenu.getPath())
                        .isEnable(Convert.toBool(tenantMenu.getIsEnable(), true))
                        .isPublic(Convert.toBool(tenantMenu.getIsPublic(), false))
                        .build();
                baseMapper.insert(menu);
                if (CollUtil.isNotEmpty(resources)) {
                    for (Resource resource : resources) {
                        resource.setMenuId(menu.getId());
                        resourceService.save(resource);
                        for (Role role : roles) {
                            RoleAuthority roleAuthority = RoleAuthority.builder()
                                    .authorityType(AuthorizeType.RESOURCE)
                                    .authorityId(resource.getId())
                                    .roleId(role.getId())
                                    .build();
                            roleAuthorityMapper.insert(roleAuthority);
                        }
                    }
                }

                // 保存 菜单和 角色 关系
                for (Role role : roles) {
                    RoleAuthority roleAuthority = RoleAuthority.builder()
                            .authorityType(AuthorizeType.MENU)
                            .authorityId(menu.getId())
                            .roleId(role.getId())
                            .build();
                    roleAuthorityMapper.insert(roleAuthority);
                }

                menuChildList = tenantMenu.getMenuChildList();
                if (CollUtil.isNotEmpty(menuChildList)) {
                    menuChildList.forEach(item -> item.setParentPath(path));
                    saveMenu(menuChildList, roleCode, tenantCode);
                    return;
                }
            }
        }

    }

}
