package com.caring.sass.wx.service.handler;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.oauth.api.WXCallBackApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.user.dto.WxSubscribeDto;
import com.caring.sass.wx.entity.keyword.Keyword;
import com.caring.sass.wx.service.keyword.KeywordService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutImageMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.builder.outxml.ImageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author leizhi
 */
@Component
public class TextHandler implements WxMpMessageHandler {

    private final KeywordService keywordService;

    private final WXCallBackApi wxCallBackApi;

    private final TenantApi tenantApi;

    public TextHandler(KeywordService keywordService, WXCallBackApi wxCallBackApi, TenantApi tenantApi) {
        this.keywordService = keywordService;
        this.wxCallBackApi = wxCallBackApi;
        this.tenantApi = tenantApi;
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        String reply = "";
        Keyword k = keywordService.matchKeyword(wxMessage.getContent());
        if (k != null) {
            reply = k.getReply();
        }
        // 关键字不匹配，发送环信消息
        if (StrUtil.isBlank(reply)) {
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
                    return WxMpXmlOutMessage.TEXT()
                            .toUser(wxMessage.getFromUser())
                            .content("如需咨询医生，请在下方进入「我的服务」→「在线咨询」\uD83D\uDC47")
                            .fromUser(wxMessage.getToUser())
                            .build();
                }
            }
        }
        if (k != null) {
            Integer msgType = k.getMsgType();
            if (msgType == 1) {
                ImageBuilder image = WxMpXmlOutMessage.IMAGE();
                image.mediaId(k.getMediaId());
                image.fromUser(wxMessage.getToUser());
                image.toUser(wxMessage.getFromUser());
                WxMpXmlOutImageMessage message = image.build();
                System.out.println("TextHandler handle" + message.toXml());
                return message;
            }
            if (msgType == 0) {
                return WxMpXmlOutMessage.TEXT()
                        .toUser(wxMessage.getFromUser())
                        .content(reply)
                        .fromUser(wxMessage.getToUser())
                        .build();
            }
        }
        return null;

    }
}
