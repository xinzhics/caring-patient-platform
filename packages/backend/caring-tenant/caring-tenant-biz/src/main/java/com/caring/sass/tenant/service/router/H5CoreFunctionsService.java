package com.caring.sass.tenant.service.router;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.entity.router.H5CoreFunctions;

/**
 * <p>
 * 业务接口
 * 患者个人中心核心功能
 * </p>
 *
 * @author 杨帅
 * @date 2023-06-27
 */
public interface H5CoreFunctionsService extends SuperService<H5CoreFunctions> {

    /**
     * 功能上线。初始化患者中心的 核心功能
     */
    void initH5CoreFunction();
}
