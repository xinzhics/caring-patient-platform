package com.caring.sass.wx.service.handler;

import cn.hutool.http.HttpUtil;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.oauth.api.WXCallBackApi;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.service.config.ConfigAdditionalService;
import com.caring.sass.wx.service.config.WeiXinUserInfoService;
import com.caring.sass.wx.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;

/**
 * @author xinzh
 */
@Slf4j
@Component
public class ScanMessageHandler implements WxMpMessageHandler {


    private final WXCallBackApi wxCallBackApi;

    private final WeiXinUserInfoService weiXinUserInfoService;

    public final ConfigAdditionalService configAdditionalService;

    public ScanMessageHandler(ConfigAdditionalService configAdditionalService, WXCallBackApi wxCallBackApi,  WeiXinUserInfoService weiXinUserInfoService) {
        this.configAdditionalService = configAdditionalService;
        this.wxCallBackApi = wxCallBackApi;
        this.weiXinUserInfoService = weiXinUserInfoService;
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        String eventKey = wxMessage.getEventKey();
        if ((!StringUtils.isEmpty(eventKey)) && (eventKey.contains("doctor_personal_") || eventKey.contains("doctor_personal_english_"))) {
            String doctorId = "";
            String[] split = eventKey.split("_");
            doctorId = split[split.length -1];
            WxSubscribeDto subscribeDto = new WxSubscribeDto();
            subscribeDto.setOpenId(wxMessage.getFromUser());
            subscribeDto.setUnionId(wxMessage.getUnionId());
            subscribeDto.setWxAppId(appId);
            subscribeDto.setDoctorId(Long.parseLong(doctorId));
            if (eventKey.contains("doctor_personal_english_")) {
                subscribeDto.setLocale(Locale.ENGLISH);
            } else {
                subscribeDto.setLocale(Locale.SIMPLIFIED_CHINESE);
            }
            wxCallBackApi.subscribe(subscribeDto);

            weiXinUserInfoService.setOpenIdUserRole(wxMessage.getOpenId(), UserType.UCENTER_PATIENT);
            return null;
        }

        if ((!StringUtils.isEmpty(eventKey)) && (eventKey.contains("doctor_login_") || eventKey.contains("doctor_english_login_"))) {
            WxSubscribeDto subscribeDto = new WxSubscribeDto();
            subscribeDto.setOpenId(wxMessage.getFromUser());
            subscribeDto.setUnionId(wxMessage.getUnionId());
            subscribeDto.setWxAppId(appId);
            if (eventKey.contains("doctor_login_")) {
                subscribeDto.setLocale(Locale.SIMPLIFIED_CHINESE);
            } else {
                subscribeDto.setLocale(Locale.ENGLISH);
            }
            wxCallBackApi.doctorSubscribe(subscribeDto);

            weiXinUserInfoService.setOpenIdUserRole(wxMessage.getOpenId(), UserType.UCENTER_DOCTOR);
            return null;
        }

        if (eventKey.contains("assistant_login_") || eventKey.contains("assistant_english_login_")) {
            WxSubscribeDto subscribeDto = new WxSubscribeDto();
            subscribeDto.setOpenId(wxMessage.getFromUser());
            subscribeDto.setUnionId(wxMessage.getUnionId());
            subscribeDto.setWxAppId(appId);
            if (eventKey.contains("assistant_login_")) {
                subscribeDto.setLocale(Locale.SIMPLIFIED_CHINESE);
            } else {
                subscribeDto.setLocale(Locale.ENGLISH);
            }
            wxCallBackApi.assistantSubscribe(subscribeDto);
            return null;
        }

        if (eventKey.contains("qunfa_notification")) {
            WxSubscribeDto subscribeDto = new WxSubscribeDto();
            subscribeDto.setOpenId(wxMessage.getFromUser());
            subscribeDto.setUnionId(wxMessage.getUnionId());
            subscribeDto.setWxAppId(appId);
            wxCallBackApi.qunfaNotification(subscribeDto);
            return null;
        }

        ConfigAdditional configAdditional = configAdditionalService.getConfigAdditionalByWxAppId(appId);
        if ((null != configAdditional) && (!StringUtils.isEmpty(configAdditional.getLocationEventHandlerUrl()))) {
            try {
                HttpUtil.post(configAdditional.getLocationEventHandlerUrl(), JsonUtils.bean2String(wxMessage));
            } catch (Exception e) {
                log.error("", e);
                return null;
            }
        }
        return null;
    }
}
