package com.caring.sass.tenant.service.router;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.entity.router.H5Ui;

import java.util.List;

/**
 * <p>
 * 业务接口
 * UI组件
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
public interface H5UiService extends SuperService<H5Ui> {

    /**
     * 路由不存在就创建
     */
    List<H5Ui> createIfNotExist();

    /**
     * 重置UI
     */
    List<H5Ui> resetUI();

    /**
     * 复制项目信息
     * @param fromTenantCode
     * @param toTenantCode
     */
    void copy(String fromTenantCode, String toTenantCode);
}
