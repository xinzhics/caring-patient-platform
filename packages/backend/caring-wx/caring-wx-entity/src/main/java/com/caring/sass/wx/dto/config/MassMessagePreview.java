package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.mp.bean.WxMpMassNews;
import me.chanjar.weixin.mp.bean.WxMpMassPreviewMessage;
import me.chanjar.weixin.mp.bean.WxMpMassVideo;

/**
 * @ClassName MassMessagePreview
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 15:43
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "MassMessagePreview", description = "群发预览")
public class MassMessagePreview {

    public WxMpMassPreviewMessage wxMpMassPreviewMessage;

    public  WxMpMassVideo wxMpMassVideo;

    public WxMpMassNews wxMpMassNews;

}
