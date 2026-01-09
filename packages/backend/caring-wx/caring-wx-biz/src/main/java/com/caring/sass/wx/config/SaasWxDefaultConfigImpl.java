package com.caring.sass.wx.config;

import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.service.thirdParty.WeChatAppInfoAccessToken;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

import java.util.Objects;

/**
 * @ClassName SaasWxMpService
 * @Description
 * @Author yangShuai
 * @Date 2022/1/19 13:36
 * @Version 1.0
 */
public class SaasWxDefaultConfigImpl extends WxMpDefaultConfigImpl {

    private Boolean thirdAuthorization;



    /**
     * 重写这个 强制设置 token过期的方法
     *
     */
    @Override
    public void expireAccessToken() {

        String appId = getAppId();
        String secret = getSecret();

        // 说明这个 appID的公众号是第三方平台授权的。 获取 accessToken的方式有变化。
        if (StringUtils.isEmpty(secret) && thirdAuthorization != null && thirdAuthorization) {
            WeChatAppInfoAccessToken bean = SpringUtils.getBean(WeChatAppInfoAccessToken.class);
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
