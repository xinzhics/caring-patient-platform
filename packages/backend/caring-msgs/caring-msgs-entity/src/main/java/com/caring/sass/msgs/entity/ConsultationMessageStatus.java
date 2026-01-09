package com.caring.sass.msgs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * 此表目前只记录专员的消息读取状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_consultation_message_status")
@Builder
@ApiModel(value = "ConsultationMessageStatus", description = "环信群组消息读取状态(会诊)")
public class ConsultationMessageStatus extends Entity<Long> {


    @ApiModelProperty(value = "接收者ID", name = "receiverId", dataType = "String")
    @TableField(value = "receiver_id", condition = EQUAL)
    protected Long receiverId;

    @ApiModelProperty(value = "接收人角色", name = "receiverRoleType", dataType = "String")
    @TableField(value = "receiver_role_type", condition = EQUAL)
    protected String receiverRoleType;

    @ApiModelProperty(value = "群消息ID", name = "consultationChatId", dataType = "String")
    @TableField(value = "consultation_chat_id", condition = EQUAL)
    protected Long consultationChatId;

    @ApiModelProperty(value = "群组ID", name = "groupId", dataType = "Long")
    @TableField(value = "group_id", condition = EQUAL)
    protected Long groupId;

    @ApiModelProperty(value = "读取状态(1 已读， 2 未读)", name = "status", dataType = "Integer")
    @TableField(value = "status", condition = EQUAL)
    protected Integer status;


}
