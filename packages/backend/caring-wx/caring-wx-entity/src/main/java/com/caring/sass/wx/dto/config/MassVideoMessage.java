package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.WxMpMassVideo;

/**
 * @ClassName MassMpNewsMessage
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 15:24
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "MassVideoMessage", description = "群发视频消息")
public class MassVideoMessage {

    public WxMpMassOpenIdsMessage message;

    public WxMpMassTagMessage tagMessage;

    public WxMpMassVideo wxMpMassVideo;

}
