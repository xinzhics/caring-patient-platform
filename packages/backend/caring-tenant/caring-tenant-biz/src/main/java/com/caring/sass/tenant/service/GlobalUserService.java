package com.caring.sass.tenant.service;

import com.caring.sass.authority.dto.auth.UserUpdatePasswordDTO;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.dto.GlobalUserSaveDTO;
import com.caring.sass.tenant.dto.GlobalUserUpdateDTO;
import com.caring.sass.tenant.entity.GlobalUser;
import com.caring.sass.tenant.entity.GlobalUserTenant;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 全局账号
 * </p>
 *
 * @author caring
 * @date 2019-10-25
 */
public interface GlobalUserService extends SuperService<GlobalUser> {

    /**
     * 检测账号是否可用
     *
     * @param account
     * @return
     */
    Boolean check(String account, Long accountId);

    /**
     * 新建用户
     *
     * @param data
     * @return
     */
    GlobalUser save(GlobalUserSaveDTO data);


    /**
     * 修改
     *
     * @param data
     * @return
     */
    GlobalUser update(GlobalUserUpdateDTO data);

    /**
     * 修改密码
     *
     * @param model
     * @return
     */
    Boolean updatePassword(UserUpdatePasswordDTO model);

    /**
     * 修改全局用户的权限
     * @param updateDTO
     */
    void updateLibraryUser(GlobalUserUpdateDTO updateDTO);

    /**
     * 删除客户账号
     * @param id
     */
    void delete(Long id);

    /**
     * @title 查询客户的项目
     * @author 杨帅
     * @updateTime 2023/4/12 14:17
     * @throws
     */
    List<GlobalUserTenant> selectTenantIds(Long userId);

    /**
     * @title 查询项目的关联的客户
     * @author 杨帅
     * @updateTime 2023/4/12 14:39
     * @throws
     */
    List<GlobalUserTenant> selectTenantIds(List<Long> tenantIds);

}
