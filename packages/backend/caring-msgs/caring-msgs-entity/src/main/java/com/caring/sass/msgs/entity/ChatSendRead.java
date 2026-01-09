package com.caring.sass.msgs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_chat_send_read")
@ApiModel(value = "ChatSendRead", description = "聊天组消息接收人未读的记录")
public class ChatSendRead extends Entity<Long> {

    @ApiModelProperty(value = "聊天组消息ID", name = "chatId", dataType = "Long")
    @TableField(value = "chat_id", condition = EQUAL)
    private Long chatId;

    @ApiModelProperty(value = "chat对象的 receiverImAccount (患者的IM账号)", name = "groupId", dataType = "String")
    @TableField(value = "group_id", condition = EQUAL)
    private String groupId;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户ID(医生或者专员)", name = "userId", dataType = "Long")
    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;

    /**
     * 用户角色
     */
    @ApiModelProperty(value = "用户的角色(医生或者专员)", name = "roleType", dataType = "String")
    @TableField(value = "role_type", condition = EQUAL)
    private String roleType;


    @ApiModelProperty(value = "消息发送人的角色", name = "sendUserRole", dataType = "String")
    @TableField(value = "send_user_role", condition = EQUAL)
    private String sendUserRole;


    @ApiModelProperty(value = "是否删除", name = "is_delete", dataType = "Integer")
    @TableField(value = "is_delete", condition = EQUAL)
    private Integer is_delete;


    @ApiModelProperty(value = "是否需要短信通知")
    @TableField(value = "need_sms_notice", condition = EQUAL)
    private Boolean needSmsNotice;

    /**
     * 不设置这个值，则不给环信推送消息
     */
    @TableField(exist = false)
    private String imAccount;

    /**
     * 不创建未读记录
     */
    @TableField(exist = false)
    private Boolean noCreateReadLog = false;



    @ApiModelProperty(value = "患者的名称")
    @TableField(exist = false)
    private String patientName;

    @ApiModelProperty(value = "患者的头像")
    @TableField(exist = false)
    private Long personAvatar;

}
