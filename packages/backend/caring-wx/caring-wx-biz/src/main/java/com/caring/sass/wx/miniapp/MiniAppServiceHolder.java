package com.caring.sass.wx.miniapp;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.caring.sass.exception.BizException;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.service.miniApp.MiniAppService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import net.oschina.j2cache.cache.support.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author xinzh
 * 这个服务池 只提供公众号使用。
 */
@Slf4j
@Component
public class MiniAppServiceHolder {

    private static WxMaService wxMaService;

    @Autowired
    public MiniAppServiceHolder(WxMaService wxMaService) {
        MiniAppServiceHolder.wxMaService = wxMaService;
    }


    public static void delete(String appId) {
        wxMaService.removeConfig(appId);
    }

    public static WxMaService getWxMaService(String appId) {
        try {
            return wxMaService.switchoverTo(appId);
        } catch (RuntimeException runtimeException) {
            MiniAppService miniAppService = SpringUtil.getBean(MiniAppService.class);
            Config config = miniAppService.selectByAppIdWithoutTenant(appId);
            if (Objects.isNull(config)) {
                log.error("MiniAppServiceHolder getWxMaService selectByAppIdWithoutTenant no find {}", appId);
                throw new BizException("小程序不存在");
            }
            SassMiNiAppDefaultConfigImpl configStorage = new SassMiNiAppDefaultConfigImpl();
            configStorage.setAppid(config.getAppId());
            if (config.getThirdAuthorization()!= null && config.getThirdAuthorization() && !StringUtils.isEmpty(config.getAuthorizerAccessToken())) {
                configStorage.updateAccessToken(config.getAuthorizerAccessToken(), 7200);
                configStorage.setThirdAuthorization(true);
            } else {
                configStorage.setSecret(config.getAppSecret());
                configStorage.setToken(config.getToken());
//                configStorage.setAesKey(config.getAseKey());
                configStorage.setThirdAuthorization(false);
            }
            wxMaService.addConfig(config.getAppId(), configStorage);
            wxMaService.switchoverTo(config.getAppId());
            try {
                wxMaService.getAccessToken();
            } catch (WxErrorException e) {
                log.error("wxMpService， getAccessToken error {} , {}", appId, e.getError());
                throw new BizException("wxMpService， getAccessToken error {}" + appId);
            }
            return wxMaService;
        }
    }


}
