package com.caring.sass.nursing.service.task;


import com.caring.sass.base.R;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.wx.ConfigAdditionalApi;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 护理计划抽象类
 *
 * @author xinzh
 */
public abstract class SuperTask {

    @Autowired
    protected TenantApi tenantApi;


    /**
     * 获取所有的正常的项目
     */
    protected List<Tenant> getAllNormalTenant() {
        R<List<Tenant>> listR = tenantApi.getAllNormalTenant();

        if (!listR.getIsSuccess()) {
            // 如果查询失败，记录错误日志
            return new ArrayList<>(); // 返回空列表
        }

        List<Tenant> tenants = listR.getData();

        if (Objects.isNull(tenants) || tenants.isEmpty()) {
            // 如果返回的数据为空或空列表，直接返回空列表
            return new ArrayList<>();
        }

        // 过滤状态为正常的项目
        return tenants.stream().filter(tenant -> TenantStatusEnum.NORMAL.equals(tenant.getStatus()))
                .collect(Collectors.toList());
    }

}
