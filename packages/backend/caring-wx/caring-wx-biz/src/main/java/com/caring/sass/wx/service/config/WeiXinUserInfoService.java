package com.caring.sass.wx.service.config;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * @ClassName WeiXinUserInfoService
 * @Description
 * @Author yangShuai
 * @Date 2022/1/7 16:08
 * @Version 1.0
 */
public interface WeiXinUserInfoService  extends SuperService<WeiXinUserInfo> {

    /**
     * 通过openId获取微信信息
     * @param openId
     * @return
     */
    WeiXinUserInfo getByOpenId(String openId);

    /**
     * 异步保存微信用户的信息
     * @param wxOAuth2UserInfo
     */
    void asyncSaveWeiXinUserInfo(WxOAuth2UserInfo wxOAuth2UserInfo);


    void setOpenIdUserRole(String openId, String userRole);
}
