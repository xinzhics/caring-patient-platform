package com.caring.sass.wx.service.handler;

import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.oauth.api.WXCallBackApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.user.dto.WxSubscribeDto;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpKefuService;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信关注消息处理
 *
 * @author xinzh
 */
@Slf4j
@Component
public class ImageMessageHandler implements WxMpMessageHandler {

    private final WXCallBackApi wxCallBackApi;

    private final TenantApi tenantApi;

    public ImageMessageHandler(WXCallBackApi wxCallBackApi, TenantApi tenantApi) {
        this.wxCallBackApi = wxCallBackApi;
        this.tenantApi = tenantApi;
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {

        log.error(" 拦截消息请求 {}", wxMessage.getMsgType());
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        WxSubscribeDto subscribeDto = new WxSubscribeDto();
        subscribeDto.setOpenId(wxMessage.getFromUser());
        subscribeDto.setWxAppId(appId);
        R<String> userExist = wxCallBackApi.wxUserExist(subscribeDto);
        if (userExist.getIsSuccess() != null && userExist.getIsSuccess()) {
            String userType = userExist.getData();
            R<String> officialAccountType = tenantApi.queryOfficialAccountType(BaseContextHandler.getTenant());
            if (TenantOfficialAccountType.PERSONAL_SERVICE_NUMBER.toString().equals(officialAccountType.getData())) {
                return null;
            }
            if (UserType.UCENTER_PATIENT.equals(userType)) {
                WxMpKefuService kefuService = wxMpService.getKefuService();
                WxMpKefuMessage wxMpKefuMessage = new WxMpKefuMessage();
                wxMpKefuMessage.setContent("如需咨询医生，请在下方进入「我的服务」→「在线咨询」\uD83D\uDC47");
                wxMpKefuMessage.setToUser(wxMessage.getFromUser());
                wxMpKefuMessage.setMsgType(WxConsts.KefuMsgType.TEXT);
                kefuService.sendKefuMessage(wxMpKefuMessage);
            }
        }
        return null;

    }
}
