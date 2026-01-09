package com.caring.sass.wx.service.config.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.wx.dao.config.AppComponentTokenMapper;
import com.caring.sass.wx.entity.config.AppComponentToken;
import com.caring.sass.wx.service.config.AppComponentTokenService;
import lombok.extern.slf4j.Slf4j;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.third.JwThirdAPI;
import org.jeewx.api.third.model.ApiComponentToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName AppComponentTokenServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/12/30 9:58
 * @Version 1.0
 */
@Slf4j
@Service
public class AppComponentTokenServiceImpl extends SuperServiceImpl<AppComponentTokenMapper, AppComponentToken> implements AppComponentTokenService {


    /**
     * 获取到第三方平台的信息可能不包含 componentAccessToken
     * @param appId
     * @return
     */
    @Override
    public AppComponentToken getComponentToken(String appId) {

        LbqWrapper<AppComponentToken> wrapper = Wraps.<AppComponentToken>lbQ().eq(AppComponentToken::getAppId, appId);
        List<AppComponentToken> componentTokens = baseMapper.selectList(wrapper);
        if (CollUtil.isNotEmpty(componentTokens)) {
            return componentTokens.get(0);
        }
        return null;
    }


    /**
     * 获取第三方平台 授权码
     * @param appId
     * @return
     */
    @Override
    public String getComponentAccessToken(String appId) {
        AppComponentToken componentToken = getComponentToken(appId);
        if (Objects.isNull(componentToken)) {
            throw new BizException("第三方平台信息不存在");
        }
        checkTokenAndUpdate(componentToken);
        return componentToken.getComponentAccessToken();
    }


    /**
     * 获得component_verify_ticket后，按照获取第三方平台 component_access_token 接口文档，调用接口获取component_access_token
     * @link： https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/2.0/api/Before_Develop/creat_token.html
     * 更新 component 平台的 token
     * @return
     */
    private String updateComponentToken(AppComponentToken appComponentToken) {
        String componentAccessToken = null;
        ApiComponentToken componentToken = new ApiComponentToken();
        componentToken.setComponent_appid(appComponentToken.getAppId());
        componentToken.setComponent_appsecret(appComponentToken.getComponentAppSecret());
        componentToken.setComponent_verify_ticket(appComponentToken.getComponentVerifyTicket());

        try {
            componentAccessToken = JwThirdAPI.getAccessToken(componentToken);
            appComponentToken.setComponentAccessToken(componentAccessToken);
            appComponentToken.setComponentAccessTokenExpiresIn(System.currentTimeMillis());
            baseMapper.updateById(appComponentToken);
        } catch (WexinReqException e) {
            e.printStackTrace();
        }
        return componentAccessToken;
    }


    /**
     * 检查并更新token
     * @param componentToken
     */
    @Override
    public void checkTokenAndUpdate(AppComponentToken componentToken) {
        String componentAccessToken = componentToken.getComponentAccessToken();
        Long componentAccessTokenExpiresIn = componentToken.getComponentAccessTokenExpiresIn();
        if (StringUtils.isEmpty(componentAccessToken)) {
            updateComponentToken(componentToken);
            return;
        }

        // accessToken的最后更新时间比 当前时间往前120分钟早时，表示token快要过期了
        Long timeOut = System.currentTimeMillis() - (60 * 120 * 1000);
        if (componentAccessTokenExpiresIn <= timeOut) {
            updateComponentToken(componentToken);
        }

    }
}
