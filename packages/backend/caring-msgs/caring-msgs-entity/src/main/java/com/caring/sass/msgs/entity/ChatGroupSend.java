package com.caring.sass.msgs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.constant.MediaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_chat_group_send")
@ApiModel(value = "ChatGroupSend", description = "群发消息(发给 聊天小组的)")
public class ChatGroupSend extends Entity<Long> {

    @ApiModelProperty(value = "发送者环信账号", name = "senderImAccount", dataType = "String")
    @TableField(value = "sender_im_account", condition = EQUAL)
    protected String senderImAccount;

    @ApiModelProperty(value = "发送者ID", name = "senderId", dataType = "String")
    @TableField(value = "sender_id", condition = EQUAL)
    protected String senderId;

    @ApiModelProperty(value = "发送者名字", name = "senderName", dataType = "String")
    @TableField(value = "sender_name", condition = EQUAL)
    protected String senderName;

    @ApiModelProperty(value = "发送者头像", name = "senderAvatar", dataType = "String")
    @TableField(value = "sender_avatar")
    protected String senderAvatar;

    @Deprecated
    @ApiModelProperty(value = "接收者ID  jsonarray 医生新群发不使用此字段", name = "receiverIds", dataType = "String")
    @TableField(value = "receiver_ids")
    protected String receiverIds;

    @ApiModelProperty(value = "内容", name = "content", dataType = "String")
    @TableField(value = "content")
    protected String content;

    @ApiModelProperty(value = "内容ID （文章消息时有效）", name = "content", dataType = "String")
    @TableField(value = "cms_id")
    private Long cmsId;

    @ApiModelProperty(value = "内容标题（文章消息时有效）", name = "content", dataType = "String")
    @TableField(value = "cms_title")
    private String cmsTitle;

    @ApiModelProperty(value = "MediaType类型", name = "type", dataType = "String")
    @TableField(value = "type_")
    protected MediaType type;

    @ApiModelProperty(value = "发送时间")
    @TableField(value = "send_time")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "患者人数")
    @TableField(value = "patient_number")
    private Long patientNumber;

    @TableField(exist = false)
    private Object channelContent;

    @ApiModelProperty(value = "项目code")
    @TableField(value = "tenant_code")
    private String tenantCode;

    @ApiModelProperty(value = "群发对象")
    @TableField(exist = false)
    private List<ChatGroupSendObject> sendObjects;

}
