package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel("发送医助通知")
@AllArgsConstructor
@NoArgsConstructor
public class SendAssistanceNoticeDto {

    @ApiModelProperty("医助的IM账号")
    String assistanceImAccount;

    @ApiModelProperty("医助通知的消息内容")
    String msgContent;

    @ApiModelProperty("医助通知的消息业务类型")
    String msgType;

    public enum AssistanceNoticeMsgType {

        DOCTOR_CHAT_GROUP,

        PATIENT_APPOINTMENT,

    }

}
