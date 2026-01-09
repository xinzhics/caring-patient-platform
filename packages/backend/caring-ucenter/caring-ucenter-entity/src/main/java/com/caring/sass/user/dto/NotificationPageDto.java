package com.caring.sass.user.dto;

import com.caring.sass.user.constant.NotificationSendStatus;
import com.caring.sass.common.enums.NotificationTarget;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName NotificationSaveDto
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 11:29
 * @Version 1.0
 */
@Data
public class NotificationPageDto {

    @ApiModelProperty(value = "群发名称")
    private String notificationName;

    @ApiModelProperty(value = "通知的目标")
    private NotificationTarget notificationTarget;

    @ApiModelProperty(value = "发送状态")
    private NotificationSendStatus notificationSendStatus;

}
