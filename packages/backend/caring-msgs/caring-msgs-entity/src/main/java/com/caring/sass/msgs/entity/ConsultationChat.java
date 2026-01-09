package com.caring.sass.msgs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.constant.MediaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE_RIGHT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Builder
@TableName("m_msg_consultation_chat")
@ApiModel(value = "ConsultationChat", description = "环信群组消息(会诊)")
public class ConsultationChat extends Entity<Long> {

    @ApiModelProperty(value = "发送者环信账号", name = "senderImAccount", dataType = "String")
    @TableField(value = "sender_im_account", condition = EQUAL)
    protected String senderImAccount;

    @ApiModelProperty(value = "发送者ID", name = "senderId", dataType = "Long")
    @TableField(value = "sender_id", condition = EQUAL)
    protected Long senderId;

    @ApiModelProperty(value = "发送者名字", name = "senderName", dataType = "String")
    @TableField(value = "sender_name", condition = EQUAL)
    protected String senderName;

    @ApiModelProperty(value = "发送者角色", name = "senderRoleType", dataType = "String")
    @TableField(value = "sender_role_type", condition = EQUAL)
    protected String senderRoleType;

    @ApiModelProperty(value = "发送者角色备注", name = "senderRoleType", dataType = "String")
    @TableField(value = "sender_role_remark", condition = EQUAL)
    protected String senderRoleRemark;

    @ApiModelProperty(value = "发送者头像", name = "senderAvatar", dataType = "String")
    @TableField(value = "sender_avatar", condition = EQUAL)
    protected String senderAvatar;

    @ApiModelProperty(value = "saas的群组ID", name = "groupId", dataType = "Long")
    @TableField(value = "group_id", condition = EQUAL)
    protected Long groupId;

    @ApiModelProperty(value = "环信IM群组ID", name = "imGroupId", dataType = "String")
    @TableField(value = "im_group_id", condition = EQUAL)
    protected String imGroupId;

    @ApiModelProperty(value = "内容", name = "content", dataType = "String")
    @TableField(value = "content", condition = LIKE_RIGHT)
    protected String content;

    @ApiModelProperty(value = "MediaType类型", name = "type", dataType = "String")
    @TableField(value = "type_", condition = EQUAL)
    protected MediaType type;

    /**
     * 创建消息记录
     */
    @TableField(exist = false)
    private Boolean createMessageRecord;

    /**
     * app的专员Id
     */
    @TableField(exist = false)
    private Long receiverId;

    /**
     * 专员的im 。用来接收极光推送
     */
    @TableField(exist = false)
    private String receiverIm;

    /**
     * 被保存消息读取状态的角色
     */
    @TableField(exist = false)
    private String receiverRoleType;

    /**
     * 清除未读消息
     */
    @TableField(exist = false)
    private Boolean cleanUnread;

    /**
     * 是否接收极光推送
     */
    @TableField(exist = false)
    private Boolean sendJPush;

    @ApiModelProperty("消息接收人(医助和系统医生)")
    @TableField(exist = false)
    List<ConsultationMessageStatus> messageStatuses;


}
