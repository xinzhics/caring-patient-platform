package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

@Data
@ApiModel(value = "SendTemplateMessageForm", description = "发送模板消息参数表单")
public class SendTemplateMessageForm extends GeneralForm {

    @ApiModelProperty(value = "要推送的模板消息", name = "templateMessage", dataType = "WxMpTemplateMessage", allowEmptyValue = false)
    WxMpTemplateMessage templateMessage;

    @ApiModelProperty("护理计划推送任务， 不为空时，需要在推送完成后，异步回调")
    String reminderLogPush;

}