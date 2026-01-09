package com.caring.sass.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.user.constant.NotificationJumpType;
import com.caring.sass.user.constant.NotificationSendStatus;
import com.caring.sass.common.enums.NotificationTarget;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName BulkNotification
 * @Description 群发通知
 * @Author yangShuai
 * @Date 2021/11/2 9:57
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_bulk_notification")
@ApiModel(value = "BulkNotification", description = "群发通知")
@AllArgsConstructor
public class BulkNotification extends Entity<Long> {

    @ApiModelProperty(value = "sass模板的Id")
    @TableField(value = "sass_template_msg_id")
    private Long sassTemplateMsgId;

    @ApiModelProperty(value = "群发名称")
    @Length(max = 255, message = "群发名称长度不能超过255")
    @TableField(value = "notification_name")
    private String notificationName;

    @ApiModelProperty(value = "发送时间")
    @TableField(value = "send_time")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "消息的code随机生成")
    @TableField(value = "code", condition = EQUAL)
    private String code;

    @ApiModelProperty(value = "消息ID从1000自增")
    @TableField(value = "message_id")
    private Long messageId;

    @ApiModelProperty(value = "通知的目标")
    @TableField(value = "notification_target")
    private NotificationTarget notificationTarget;

    @ApiModelProperty(value = "模板名称")
    @TableField(value = "template_name")
    private String templateName;

    @ApiModelProperty(value = "发送状态")
    @TableField(value = "notification_send_status")
    private NotificationSendStatus notificationSendStatus;

    @ApiModelProperty(value = "跳转类型")
    @TableField(value = "notification_jump_type")
    private NotificationJumpType notificationJumpType;

    @ApiModelProperty(value = "链接/内容ID/等")
    @TableField(value = "jump_business_content")
    private String jumpBusinessContent;

    @TableField(exist = false)
    private Integer sendNumber;

}
