package com.caring.sass.msgs.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.constant.MediaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 环信消息体。区别于实体，groupId使用的String类型
 */
@Data
@ToString
@ApiModel(value = "ConsultationChatDto", description = "环信群组消息体")
public class ConsultationChatDto {

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @ApiModelProperty(value = "发送者环信账号", name = "senderImAccount", dataType = "String")
    protected String senderImAccount;

    @ApiModelProperty(value = "发送者ID", name = "senderId", dataType = "Long")
    protected Long senderId;

    @ApiModelProperty(value = "发送者名字", name = "senderName", dataType = "String")
    protected String senderName;

    @ApiModelProperty(value = "发送者角色", name = "senderRoleType", dataType = "String")
    protected String senderRoleType;

    @ApiModelProperty(value = "发送者角色备注", name = "senderRoleType", dataType = "String")
    protected String senderRoleRemark;

    @ApiModelProperty(value = "发送者头像", name = "senderAvatar", dataType = "String")
    protected String senderAvatar;

    @ApiModelProperty(value = "IM群组ID", name = "imGroupId", dataType = "String")
    protected String imGroupId;

    @ApiModelProperty(value = "内容", name = "content", dataType = "String")
    protected String content;

    @ApiModelProperty(value = "MediaType类型", name = "type", dataType = "String")
    protected MediaType type;

    @TableField(exist = false)
    private String JPushAppKey;

    @TableField(exist = false)
    private String JPushMasterSecret;


}
