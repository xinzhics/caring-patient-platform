package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("小程序客服卡片消息")
@Data
public class WxMaKfMessageDto {

    String appId;

    String openId;

    String pagePath;

    String title;

    String thumb_media_id;

}

