package com.caring.sass.authority.service.auth.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.caring.sass.authority.dao.auth.RoleMapper;
import com.caring.sass.authority.dto.auth.RoleSaveDTO;
import com.caring.sass.authority.dto.auth.RoleUpdateDTO;
import com.caring.sass.authority.entity.auth.Role;
import com.caring.sass.authority.entity.auth.RoleAuthority;
import com.caring.sass.authority.entity.auth.RoleOrg;
import com.caring.sass.authority.entity.auth.UserRole;
import com.caring.sass.authority.service.auth.RoleAuthorityService;
import com.caring.sass.authority.service.auth.RoleOrgService;
import com.caring.sass.authority.service.auth.RoleService;
import com.caring.sass.authority.service.auth.UserRoleService;
import com.caring.sass.authority.strategy.DataScopeContext;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperCacheServiceImpl;
import com.caring.sass.common.constant.CacheKey;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.CodeGenerate;
import com.caring.sass.utils.StrHelper;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.caring.sass.common.constant.CacheKey.ROLE;

/**
 * <p>
 * 业务实现类
 * 角色
 * </p>
 *
 * @author caring
 * @date 2019-07-03
 */
@Slf4j
@Service

public class RoleServiceImpl extends SuperCacheServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleOrgService roleOrgService;
    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private DataScopeContext dataScopeContext;
    @Autowired
    private CodeGenerate codeGenerate;

    @Override
    protected String getRegion() {
        return ROLE;
    }

    @Override
    public boolean isSuperAdmin(Long userId) {
        return userId != null && userId.equals(1L);
    }


    /**
     * 删除角色时，需要级联删除跟角色相关的一切资源：
     * 1，角色本身
     * 2，角色-组织：
     * 3，角色-权限（菜单和按钮）：
     * 4，角色-用户：角色拥有的用户
     * 5，用户-权限：
     *
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIdWithCache(List<Long> ids) {
        if (ids.isEmpty()) {
            return true;
        }
        // 橘色
        boolean removeFlag = removeByIds(ids);
        // 角色组织
        roleOrgService.remove(Wraps.<RoleOrg>lbQ().in(RoleOrg::getRoleId, ids));
        // 角色权限
        roleAuthorityService.remove(Wraps.<RoleAuthority>lbQ().in(RoleAuthority::getRoleId, ids));

        List<Long> userIds = userRoleService.listObjs(
                Wraps.<UserRole>lbQ().select(UserRole::getUserId).in(UserRole::getRoleId, ids),
                Convert::toLong);

        //角色拥有的用户
        userRoleService.remove(Wraps.<UserRole>lbQ().in(UserRole::getRoleId, ids));

        ids.forEach((id) -> {
            cacheChannel.evict(CacheKey.ROLE_MENU, String.valueOf(id));
            cacheChannel.evict(CacheKey.ROLE_RESOURCE, String.valueOf(id));
        });

        if (!userIds.isEmpty()) {
            //用户角色 、 用户菜单、用户资源
            String[] userIdArray = userIds.stream().map(this::key).toArray(String[]::new);
            cacheChannel.evict(CacheKey.USER_ROLE, userIdArray);
            cacheChannel.evict(CacheKey.USER_RESOURCE, userIdArray);
            cacheChannel.evict(CacheKey.USER_MENU, userIdArray);
        }
        return removeFlag;
    }

    /**
     * 要求的数据实时性强。这里不走缓存。防止缓存没刷新问题。
     * @param userId
     * @return
     */
    @Override
    public List<Role> findRoleByUserId(Long userId) {
//        String key = key(userId);
//        List<Role> roleList = new ArrayList<>();
//        CacheObject cacheObject = cacheChannel.get(CacheKey.USER_ROLE, key, (k) -> {
//            // 这里不会执行。 除非 缓存不存在。 但实际缓存的值可能是 []
//            roleList.addAll(baseMapper.findRoleByUserId(userId));
//            return roleList.stream().mapToLong(Role::getId).boxed().collect(Collectors.toList());
//        });
//
//        // 用户没有从缓存中拿到角色。
//        // 查询数据库
//        List<Role> roles;
//        List<Long> roleIds;
//
//        if (cacheObject.getValue() == null) {
//            roles = baseMapper.findRoleByUserId(userId);
//            roleIds = roles.stream().map(SuperEntity::getId).collect(Collectors.toList());
//        } else {
//            roleIds = (List<Long>) cacheObject.getValue();
//            if (CollUtil.isEmpty(roleIds)) {
//                roles = baseMapper.findRoleByUserId(userId);
//                roleIds = roles.stream().map(SuperEntity::getId).collect(Collectors.toList());
//            }
//        }
//        if (CollUtil.isEmpty(roleIds)) {
//            return new ArrayList<>();
//        } else {
//            cacheChannel.set(CacheKey.USER_ROLE, key, roleIds);
//            List<Role> menuList = roleIds.stream().map(this::getByIdCache)
//                    .filter(Objects::nonNull).collect(Collectors.toList());
//            return menuList;
//        }
        List<Role> roles;
        List<Long> roleIds;
        roles = baseMapper.findRoleByUserId(userId);
        roleIds = roles.stream().map(SuperEntity::getId).collect(Collectors.toList());
        return roleIds.stream().map(this::getById)
                .filter(Objects::nonNull).collect(Collectors.toList());

    }

    /**
     * 1，保存角色
     * 2，保存 与组织的关系
     *
     * @param data
     * @param userId 用户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleSaveDTO data, Long userId) {
        Role role = BeanPlusUtil.toBean(data, Role.class);
        role.setCode(StrHelper.getOrDef(data.getCode(), codeGenerate.next()));
        role.setReadonly(false);
        save(role);

        saveRoleOrg(userId, role, data.getOrgList());

        cacheChannel.set(CacheKey.ROLE, String.valueOf(role.getId()), role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateDTO data, Long userId) {
        Role role = BeanPlusUtil.toBean(data, Role.class);
        updateById(role);

        roleOrgService.remove(Wraps.<RoleOrg>lbQ().eq(RoleOrg::getRoleId, data.getId()));
        saveRoleOrg(userId, role, data.getOrgList());

    }

    private void saveRoleOrg(Long userId, Role role, List<Long> orgList) {
        // 根据 数据范围类型 和 勾选的组织ID， 重新计算全量的组织ID
        List<Long> orgIds = dataScopeContext.getOrgIdsForDataScope(orgList, role.getDsType(), userId);
        if (orgIds != null && !orgIds.isEmpty()) {
            List<RoleOrg> list = orgIds.stream().map((orgId) -> RoleOrg.builder().orgId(orgId).roleId(role.getId()).build()).collect(Collectors.toList());
            roleOrgService.saveBatch(list);
        }
    }

    @Override
    public List<Long> findUserIdByCode(String[] codes) {
        return baseMapper.findUserIdByCode(codes);
    }

    @Override
    public Boolean check(String code) {
        return super.count(Wraps.<Role>lbQ().eq(Role::getCode, code)) > 0;
    }
}
