package com.caring.sass.authority.service.auth;

import com.caring.sass.authority.entity.auth.Application;
import com.caring.sass.base.service.SuperCacheService;

/**
 * <p>
 * 业务接口
 * 应用
 * </p>
 *
 * @author caring
 * @date 2019-12-15
 */
public interface ApplicationService extends SuperCacheService<Application> {
    /**
     * 根据 clientid 和 clientSecret 查询
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    Application getByClient(String clientId, String clientSecret);

    /**
     * 通过应用客户端id查询应用信息
     *
     * @param clientId 客户端id
     * @return 应用信息
     */
    Application findByClientId(String clientId);
}
