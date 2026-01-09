package com.caring.sass.wx.service.miniApp;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.dto.config.WxMaKfMessageDto;
import com.caring.sass.wx.entity.config.Config;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateKeyword;
import me.chanjar.weixin.common.bean.subscribemsg.TemplateInfo;

import java.util.List;

/**
 * @className: MiniAppService
 * @author: 杨帅
 * @date: 2024/3/22
 */
public interface MiniAppService  extends SuperService<Config> {


    List<Config> listAllMiniAppWithoutTenant();


    Config selectByAppIdWithoutTenant(String appId);

    String createQRCode(String appId, String path, Integer width);

    WxMaJscode2SessionResult miniAppThirdPartyCode2Session(String js_code, String appId);


    IPage<Config> pageNoTenantCode(IPage buildPage, LambdaQueryWrapper<Config> wrapper);

    Config getByAppIdNoTenantCode(String appId);

    /**
     * 小程序获取用户的手机号
     * @param code
     * @param appId
     */
    WxMaPhoneNumberInfo getPhoneNumber(String code, String appId);

    /**
     * 发送客户消息
     */
    void sendCustomMessage(WxMaKfMessageDto wxMaKfMessageDto);

    /**
     * 发送订阅消息
     * @param wxMaKfMessageDto
     */
    void sendMessage(String appId, WxMaSubscribeMessage wxMaKfMessageDto);

    /**
     * 获取模版内容
     * @param appId
     * @param templateId
     */
    List<PubTemplateKeyword> getTemplate(String appId, String templateId);


    List<TemplateInfo> getTemplate(String appId);

    String generatescheme(String appId);
}
