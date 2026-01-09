package com.caring.sass.tenant.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.entity.TenantConfigurationSchedule;

/**
 * <p>
 * 业务接口
 * 项目配置进度表
 * </p>
 *
 * @author 杨帅
 * @date 2023-07-18
 */
public interface TenantConfigurationScheduleService extends SuperService<TenantConfigurationSchedule> {

    void onLinkInit();

    // 项目复制时。根据复制的内容 复制初始化新的 步骤和 标记
    void copy(String fromTenantCode, String toTenantCode);
}
