package com.caring.sass.wx.service.config;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.entity.config.AppComponentToken;

/**
 * @ClassName AppComponentTokenService
 * @Description
 * @Author yangShuai
 * @Date 2021/12/30 10:01
 * @Version 1.0
 */
public interface AppComponentTokenService extends SuperService<AppComponentToken> {

    /**
     * 获取第三方平台信息
     * @param appId
     * @return
     */
    AppComponentToken getComponentToken(String appId);

    /**
     * 获取第三方平台 授权码
     * @param appId
     * @return
     */
    String getComponentAccessToken(String appId);


    /**
     * 检查 componentAccessToken 是否快要过期。
     * 快过期时，更新componentAccessToken
     * @param componentToken
     */
    void checkTokenAndUpdate(AppComponentToken componentToken);


}
