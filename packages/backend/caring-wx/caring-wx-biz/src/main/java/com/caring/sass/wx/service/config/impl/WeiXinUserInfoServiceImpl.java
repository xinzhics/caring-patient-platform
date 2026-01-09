package com.caring.sass.wx.service.config.impl;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.wx.dao.config.WeiXinUserInfoMapper;
import com.caring.sass.wx.entity.config.WeiXinUserInfo;
import com.caring.sass.wx.service.config.WeiXinUserInfoService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ClassName WeiXinUserInfoServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2022/1/7 16:09
 * @Version 1.0
 */
@Slf4j
@Service
public class WeiXinUserInfoServiceImpl extends SuperServiceImpl<WeiXinUserInfoMapper, WeiXinUserInfo> implements WeiXinUserInfoService {


    @Autowired
    FileUploadApi fileUploadApi;

    /**
     * 通过openId 获取 微信用户信息
     * @param openId
     * @return
     */
    @Override
    public WeiXinUserInfo getByOpenId(String openId) {
        LbqWrapper<WeiXinUserInfo> lbqWrapper = Wraps.<WeiXinUserInfo>lbQ().eq(WeiXinUserInfo::getOpenId, openId).last(" limit 0,1 ");
        return baseMapper.selectOne(lbqWrapper);
    }


    /**
     * 异步保存微信用户信息
     * @param wxOAuth2UserInfo
     */
    @Override
    public void asyncSaveWeiXinUserInfo(WxOAuth2UserInfo wxOAuth2UserInfo) {
        WeiXinUserInfo weiXinUserInfo = getByOpenId(wxOAuth2UserInfo.getOpenid());
        if (Objects.isNull(weiXinUserInfo)) {
            weiXinUserInfo = new WeiXinUserInfo();
        }
        weiXinUserInfo.setOpenId(wxOAuth2UserInfo.getOpenid());
        weiXinUserInfo.setNickname(wxOAuth2UserInfo.getNickname());
        weiXinUserInfo.setUnionId(wxOAuth2UserInfo.getUnionId());
        weiXinUserInfo.setHeadImgUrl(wxOAuth2UserInfo.getHeadImgUrl());
        if (weiXinUserInfo.getId() != null) {
            baseMapper.updateById(weiXinUserInfo);
        } else {
            baseMapper.insert(weiXinUserInfo);
        }
    }

    @Override
    public void setOpenIdUserRole(String openId, String userRole) {
        WeiXinUserInfo userInfo = getByOpenId(openId);
        if (Objects.isNull(userInfo)) {
            userInfo = new WeiXinUserInfo();
        }
        if (StrUtil.isNotEmpty(userInfo.getUserRole())) {
            log.error("user role exist， Refuse to reselect");
            return;
        }
        if (UserType.UCENTER_DOCTOR.equals(userRole) || UserType.UCENTER_PATIENT.equals(userRole)) {
            userInfo.setOpenId(openId);
            userInfo.setUserRole(userRole);
            if (userInfo.getId() == null) {
                baseMapper.insert(userInfo);
            } else {
                baseMapper.updateById(userInfo);
            }
        } else {
            throw new BizException("不存在的身份");
        }

    }


}
