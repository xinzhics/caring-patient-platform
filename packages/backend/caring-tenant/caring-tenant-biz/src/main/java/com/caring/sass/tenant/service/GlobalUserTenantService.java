package com.caring.sass.tenant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.dto.GlobalUserTenantPageDTO;
import com.caring.sass.tenant.entity.GlobalUserTenant;
import com.caring.sass.tenant.entity.Tenant;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 用户项目管理表
 * </p>
 *
 * @author 杨帅
 * @date 2023-04-11
 */
public interface GlobalUserTenantService extends SuperService<GlobalUserTenant> {

    /**
     * @title
     * @author 杨帅
     * @updateTime 2023/4/12 10:31
     * @throws
     */
    void pageTenant(IPage<Tenant> page, GlobalUserTenantPageDTO paramsModel);


    void setTenantInfo(List<GlobalUserTenant> records);

}
