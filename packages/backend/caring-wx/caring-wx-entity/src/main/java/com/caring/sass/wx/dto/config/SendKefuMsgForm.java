package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;

@Data
@ApiModel(value = "SendKefuMsgForm", description = "发送客服消息所需要的参数表单")
public class SendKefuMsgForm extends GeneralForm {

    @ApiModelProperty(value = "需要发送的客服消息", name = "message", dataType = "WxMpKefuMessage", allowEmptyValue = false)
    WxMpKefuMessage message;

}