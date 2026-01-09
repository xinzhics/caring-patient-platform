package com.caring.sass.wx.service.thirdParty;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.collection.CollUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.wx.config.WxMpServiceHolder;
import com.caring.sass.wx.dao.config.ConfigMapper;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.miniapp.MiniAppServiceHolder;
import com.caring.sass.wx.miniapp.SassMiNiAppDefaultConfigImpl;
import com.caring.sass.wx.service.config.AppComponentTokenService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.third.JwThirdAPI;
import org.jeewx.api.third.model.ApiAuthorizerToken;
import org.jeewx.api.third.model.ApiAuthorizerTokenRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName WeChatAppInfoAccessToken
 * @Description 定时检查刷新 微信公众号的 授权 accessToken 过期时间
 * 第三方平台在用户授权时，拉取到公众号的 accessToken。 accessToken 有效时间2小时，需要定时刷新
 * @Author yangShuai
 * @Date 2021/1/5 17:27
 * @Version 1.0
 */
@Component
public class WeChatAppInfoAccessToken {


    @Autowired
    ConfigMapper configMapper;

    @Autowired
    ComponentAppService componentAppService;

    @Autowired
    AppComponentTokenService appComponentTokenService;

    /**
     * @Author yangShuai
     * @Description 定时处理 即将过期的 公众号授权。
     * @Date 2021/1/6 9:33
     * @return void
     */
    public void getWeChatAppInfo() {

        long time = System.currentTimeMillis() - (60 * 90 * 1000);
        List<Config> tokenTimeOut = configMapper.selectTokenTimeOutWithoutTenant(time);
        if (CollUtil.isNotEmpty(tokenTimeOut)) {
            for (Config appInfo : tokenTimeOut) {
                refreshAccessToken(appInfo);
            }
        }
    }


    public void refreshAccessToken(String appId) {

        List<Config> configs = configMapper.selectByAppIdWithoutTenant(appId);
        if (CollUtil.isNotEmpty(configs)) {
            for (Config config : configs) {
                refreshAccessToken(config);
            }
        }

    }

    /**
     * 刷新accessToken
     * @param appInfo
     */
    public void refreshAccessToken(Config appInfo) {
        if (Objects.isNull(appInfo)) {
            return;
        }
        String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();
        ApiAuthorizerToken authorizerToken = new ApiAuthorizerToken();
        authorizerToken.setAuthorizer_appid(appInfo.getAppId());
        authorizerToken.setAuthorizer_refresh_token(appInfo.getAuthorizerRefreshToken());
        authorizerToken.setComponent_appid(componentAppId);
        String componentAccessToken = appComponentTokenService.getComponentAccessToken(componentAppId);
        try {
            ApiAuthorizerTokenRet apiAuthorizerToken = JwThirdAPI.apiAuthorizerToken(authorizerToken, componentAccessToken);
            String authorizerAccessToken = apiAuthorizerToken.getAuthorizer_access_token();
            String authorizerRefreshToken = apiAuthorizerToken.getAuthorizer_refresh_token();
            Integer expiresIn = apiAuthorizerToken.getExpires_in();
            if (StringUtils.isEmpty(authorizerAccessToken) || StringUtils.isEmpty(authorizerRefreshToken)) {
                return;
            }
            appInfo.setAuthorizerRefreshToken(authorizerRefreshToken);
            appInfo.setAuthorizerAccessToken(authorizerAccessToken);
            appInfo.setExpiresIn(Long.valueOf(expiresIn));
            appInfo.setAccessTokenCreateTime(System.currentTimeMillis());
            BaseContextHandler.setTenant(appInfo.getTenantCode());
            configMapper.updateById(appInfo);

            // 只有公众号才需要注入缓存
            if (appInfo.getAuthType() == null || appInfo.getAuthType().equals(1)) {
                // 注入微信服务池问题
                WxMpService wxMpService = WxMpServiceHolder.getWxMpService(appInfo.getAppId());
                WxMpConfigStorage wxMpConfigStorage = wxMpService.getWxMpConfigStorage();
                wxMpConfigStorage.updateAccessToken(appInfo.getAuthorizerAccessToken(), 7200);
                wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
            } else {
                WxMaService wxMaService = MiniAppServiceHolder.getWxMaService(appInfo.getAppId());
                SassMiNiAppDefaultConfigImpl miNiAppDefaultConfig = new SassMiNiAppDefaultConfigImpl();
                miNiAppDefaultConfig.updateAccessToken(appInfo.getAuthorizerAccessToken(), 7000);
                wxMaService.setWxMaConfig(miNiAppDefaultConfig);
            }
        } catch (WexinReqException e) {
            e.printStackTrace();
        }
    }


}
