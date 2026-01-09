package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @className: PatientMessageGroupListDTO
 * @author: 杨帅
 * @date: 2023/8/25
 */
@Data
public class PatientMessageGroupListDTO {

    @ApiModelProperty("消息类型")
    private String messageType;

    @ApiModelProperty("医生名称")
    private String doctorName;

    @ApiModelProperty("医生头像")
    private String doctorAvatar;

    @ApiModelProperty("医生职称")
    private String doctorTitle;

    @ApiModelProperty("消息名称")
    private String messageName;

    @ApiModelProperty("消息内容")
    private String messageContent;

    @ApiModelProperty("消息的发送时间")
    private LocalDateTime messageSendTime;

    @ApiModelProperty("消息未读数量")
    private Integer noReadCount;

}
