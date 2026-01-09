package com.caring.sass.wx.service.handler;

import cn.hutool.http.HttpUtil;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.service.config.ConfigAdditionalService;
import com.caring.sass.wx.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author xinzh
 */
@Slf4j
@Component
public class LocationMessageHandler implements WxMpMessageHandler {

    public final ConfigAdditionalService configAdditionalService;

    public LocationMessageHandler(ConfigAdditionalService configAdditionalService) {
        this.configAdditionalService = configAdditionalService;
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        ConfigAdditional setting = configAdditionalService.getConfigAdditionalByWxAppId(appId);
        if ((null != setting) && (!StringUtils.isEmpty(setting.getLocationEventHandlerUrl()))) {
            try {
                HttpUtil.post(setting.getLocationEventHandlerUrl(), JsonUtils.bean2String(wxMessage));
            } catch (Exception e) {
                log.error("", e);
                return null;
            }
        }
        return null;
    }
}
