package com.caring.sass.wx.config;

import com.caring.sass.exception.BizException;
import com.caring.sass.wx.dao.config.ConfigMapper;
import com.caring.sass.wx.entity.config.Config;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import net.oschina.j2cache.cache.support.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author xinzh
 * 这个服务池 只提供公众号使用。
 */
@Slf4j
@Component
public class WxMpServiceHolder {

    private static WxMpService wxMpService;

    @Autowired
    public WxMpServiceHolder(WxMpService wxMpService) {
        WxMpServiceHolder.wxMpService = wxMpService;
    }


    public static void delete(String appId) {
        wxMpService.removeConfigStorage(appId);
    }

    public static WxMpService getWxMpService(String appId) {
        try {
            return wxMpService.switchoverTo(appId);
        } catch (RuntimeException runtimeException) {
            ConfigMapper configMapper = SpringUtil.getBean(ConfigMapper.class);
            List<Config> configs = configMapper.selectByAppIdWithoutTenant(appId);
            if (CollectionUtils.isEmpty(configs)) {
                log.error("WxMpServiceHolder getWxMpService selectByAppIdWithoutTenant no find");
                throw new BizException("未获取到微信公众号配置");
            }
            Config config = configs.get(0);
            SaasWxDefaultConfigImpl configStorage = new SaasWxDefaultConfigImpl();
            configStorage.setAppId(config.getAppId());
            if (config.getThirdAuthorization()!= null && config.getThirdAuthorization() && !StringUtils.isEmpty(config.getAuthorizerAccessToken())) {
                configStorage.updateAccessToken(config.getAuthorizerAccessToken(), 7200);
                configStorage.setThirdAuthorization(true);
            } else {
                configStorage.setSecret(config.getAppSecret());
                configStorage.setToken(config.getToken());
                configStorage.setAesKey(config.getAseKey());
                configStorage.setThirdAuthorization(false);
            }
            wxMpService.addConfigStorage(config.getAppId(), configStorage);
            wxMpService.switchoverTo(configStorage.getAppId());
            try {
                wxMpService.getAccessToken();
            } catch (WxErrorException e) {
                log.error("wxMpService， getAccessToken error {} , {}", appId, e.getError());
                throw new BizException("wxMpService， getAccessToken error {}" + appId);
            }
            return wxMpService;
        }
    }


}
