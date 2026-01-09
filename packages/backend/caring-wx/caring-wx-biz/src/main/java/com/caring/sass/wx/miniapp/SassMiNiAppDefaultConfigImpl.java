package com.caring.sass.wx.miniapp;

import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.service.thirdParty.WeChatAppInfoAccessToken;

import java.util.Objects;

/**
 * @ClassName SaasWxMpService
 * @Description
 * @Author yangShuai
 * @Date 2022/1/19 13:36
 * @Version 1.0
 */
public class SassMiNiAppDefaultConfigImpl extends WxMaDefaultConfigImpl {

    private Boolean thirdAuthorization;
    /**
     * 重写这个 强制设置 token过期的方法
     * 强制token过期时，直接刷新token
     */
    @Override
    public void expireAccessToken() {
        String appId = getAppid();
        String secret = getSecret();

        WeChatAppInfoAccessToken bean = SpringUtils.getBean(WeChatAppInfoAccessToken.class);
        if (StringUtils.isEmpty(secret) && thirdAuthorization != null && thirdAuthorization) {
            if (Objects.nonNull(bean)) {
                bean.refreshAccessToken(appId);
            }
        } else {
            super.expireAccessToken();
        }

    }

    public Boolean getThirdAuthorization() {
        return thirdAuthorization;
    }

    public void setThirdAuthorization(Boolean thirdAuthorization) {
        this.thirdAuthorization = thirdAuthorization;
    }

}
