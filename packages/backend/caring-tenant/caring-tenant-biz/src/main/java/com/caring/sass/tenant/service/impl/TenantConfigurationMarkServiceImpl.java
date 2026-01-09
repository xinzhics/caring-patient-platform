package com.caring.sass.tenant.service.impl;


import com.caring.sass.tenant.dao.TenantConfigurationMarkMapper;
import com.caring.sass.tenant.entity.TenantConfigurationMark;
import com.caring.sass.tenant.service.TenantConfigurationMarkService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 项目配置标记表
 * </p>
 *
 * @author 杨帅
 * @date 2023-07-18
 */
@Slf4j
@Service

public class TenantConfigurationMarkServiceImpl extends SuperServiceImpl<TenantConfigurationMarkMapper, TenantConfigurationMark> implements TenantConfigurationMarkService {
}
