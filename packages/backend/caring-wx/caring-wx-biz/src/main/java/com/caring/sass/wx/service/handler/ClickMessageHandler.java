package com.caring.sass.wx.service.handler;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantOfficialAccountType;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.wx.message.recv.VideoMessage;
import com.caring.sass.wx.dao.config.WxMenuMapper;
import com.caring.sass.wx.dao.menu.CustomMenuMapper;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.entity.config.WxMenu;
import com.caring.sass.wx.service.config.ConfigAdditionalService;
import com.caring.sass.wx.utils.JsonUtils;
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
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.builder.outxml.TextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xinzh
 */
@Slf4j
@Component
public class ClickMessageHandler implements WxMpMessageHandler {

    private final WxMenuMapper wxMenuMapper;

    private final TenantApi tenantApi;

    public ClickMessageHandler(WxMenuMapper wxMenuMapper, TenantApi tenantApi) {
        this.wxMenuMapper = wxMenuMapper;
        this.tenantApi = tenantApi;
    }

    /**
     * 点击微信菜单收到的消息处理。
     * @param wxMessage      微信推送消息
     * @param context        上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @param wxMpService    服务类
     * @param sessionManager session管理器
     * @return
     * @throws WxErrorException
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
                                    WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        List<WxMenu> wxMenus = wxMenuMapper.selectList(Wraps.<WxMenu>lbQ().eq(WxMenu::getWxAppId, appId));
        if (wxMenus.isEmpty()) {
            return null;
        }
        String eventKey = wxMessage.getEventKey();
        Map<String, WxMenu> collect = wxMenus.stream().filter(wxMenu -> wxMenu.getMenuKey() != null)
                .collect(Collectors.toMap(WxMenu::getMenuKey, item -> item));
        WxMenu wxMenu = collect.get(eventKey);
        if (wxMenu == null) {
            return null;
        }
        String mediaId = wxMenu.getMediaId();
        // 图片
        if (StrUtil.isNotEmpty(mediaId)) {
            return WxMpXmlOutMessage.IMAGE().mediaId(mediaId)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        }
        R<Tenant> tenant = tenantApi.getTenantByWxAppId(appId);
        if (tenant.getIsSuccess()) {
            Tenant data = tenant.getData();
            if (data.isPersonalServiceNumber()) {
                // 文本
                return  WxMpXmlOutMessage.TEXT().content(wxMenu.getTextContent())
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser())
                        .build();
            } else {
                WxMpKefuService kefuService = wxMpService.getKefuService();
                WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT()
                        .content(wxMenu.getTextContent())
                        .toUser(wxMessage.getFromUser()).build();
                kefuService.sendKefuMessage(kefuMessage);
                // 文本
                return null;
            }
        }
        return null;

    }
}
