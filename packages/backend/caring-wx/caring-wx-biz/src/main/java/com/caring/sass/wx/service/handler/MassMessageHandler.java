package com.caring.sass.wx.service.handler;

import com.caring.sass.cms.MassCallBackApi;
import com.caring.sass.cms.dto.MassCallBack;
import com.caring.sass.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信群发事件回调
 *
 * @author xinzh
 */
@Slf4j
@Component
public class MassMessageHandler implements WxMpMessageHandler {

    private final MassCallBackApi massCallBackApi;

    public MassMessageHandler(MassCallBackApi massCallBackApi) {
        this.massCallBackApi = massCallBackApi;
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        try {

            MassCallBack massCallBack = new MassCallBack();
            Map<String, Object> allFieldsMap = wxMessage.getAllFieldsMap();
            Map<String, Object> articleUrlResult = (HashMap) allFieldsMap.get("ArticleUrlResult");
            Map<String, Object> copyrightCheckResult = (HashMap) allFieldsMap.get("CopyrightCheckResult");
            massCallBack.setMsgId(wxMessage.getMsgId().toString());
            massCallBack.setErrorCount(wxMessage.getErrorCount());
            massCallBack.setFilterCount(wxMessage.getFilterCount());
            massCallBack.setTotalCount(wxMessage.getTotalCount());
            massCallBack.setStatus(wxMessage.getStatus());
            massCallBack.setSentCount(wxMessage.getSentCount());
            massCallBack.setArticleUrlResult(articleUrlResult);
            massCallBack.setTenantCode(BaseContextHandler.getTenant());
            massCallBack.setCopyrightCheckResult(copyrightCheckResult);
            massCallBackApi.massCallBack(massCallBack);
        } catch (Exception e) {
            log.error("群发出错了", e);
        }
        return null;
    }
}
