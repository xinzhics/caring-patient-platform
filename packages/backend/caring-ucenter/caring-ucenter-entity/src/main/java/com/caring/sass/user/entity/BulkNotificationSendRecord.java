package com.caring.sass.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.user.constant.NotificationSendStatus;
import com.caring.sass.common.enums.NotificationTarget;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @ClassName NotificationSendRecord
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 10:38
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_bulk_notification_send_record")
@ApiModel(value = "BulkNotificationSendRecord", description = "群发通知记录")
@AllArgsConstructor
public class BulkNotificationSendRecord extends Entity<Long> {


    @ApiModelProperty(value = "通知ID")
    @TableField(value = "notification_id")
    private Long notificationId;

    @ApiModelProperty(value = "通知的目标")
    @TableField(value = "notification_target")
    private NotificationTarget notificationTarget;

    @ApiModelProperty(value = "通知的目标openId")
    @TableField(value = "open_id")
    private String openId;

    @ApiModelProperty(value = "通知的目标")
    @TableField(value = "target_id")
    private Long targetId;

    @ApiModelProperty(value = "通知的目标名称")
    @TableField(value = "target_name")
    private String targetName;

    @ApiModelProperty(value = "发送状态")
    @TableField(value = "notification_send_status")
    private NotificationSendStatus notificationSendStatus;

    @ApiModelProperty(value = "给程序员看的异常")
    @TableField(value = "wx_error")
    private String wxError;


    @ApiModelProperty(value = "用户自己看的异常")
    @TableField(value = "error_msg")
    private String errorMsg;

    @TableField(exist = false)
    private String templateMsgDto;

    @TableField(exist = false)
    private String appId;

    @TableField(exist = false)
    private String url;

    @TableField(exist = false)
    private String tenantCode;


}
