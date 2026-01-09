package com.caring.sass.wx.service.handler;

import cn.hutool.http.HttpUtil;
import com.caring.sass.oauth.api.WXCallBackApi;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.service.config.ConfigAdditionalService;
import com.caring.sass.wx.utils.JsonUtils;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author xinzh
 */
@Component
public class UnSubscribeHandler implements WxMpMessageHandler {
    
    @Autowired
    private WXCallBackApi wxCallBackApi;

    @Autowired
    public ConfigAdditionalService configAdditionalService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        ConfigAdditional setting = configAdditionalService.getConfigAdditionalByWxAppId(appId);
        if ((null != setting) && (!StringUtils.isEmpty(setting.getUnsubscribeEventHandlerUrl()))) {
            try {
                HttpUtil.post(setting.getUnsubscribeEventHandlerUrl(), JsonUtils.bean2String(wxMessage));
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        wxCallBackApi.unsubscribe(wxMessage.getFromUser());
        return null;
    }
}
