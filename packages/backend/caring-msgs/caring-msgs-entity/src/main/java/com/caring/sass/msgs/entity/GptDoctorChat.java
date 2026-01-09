package com.caring.sass.msgs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * GPT医生聊天记录
 * </p>
 *
 * @author 杨帅
 * @since 2023-02-10
 */
@Data
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_gpt_doctor_chat")
@ApiModel(value = "GptDoctorChat", description = "GPT医生聊天记录")
@AllArgsConstructor
public class GptDoctorChat extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static final String sending = "sending";
    public static final String AiRole = "Ai";
    public static final String sendSuccess = "sendSuccess";
    public static final String sendError = "sendError";

    /**
     * 问题消耗tokens
     */
    @TableField(exist = false)
    private long questionTokens = 0;

    /**
     * 会话标识
     */
    @ApiModelProperty(value = "会话标识")
    private String sessionId;

    @ApiModelProperty(value = "发送人IM账号")
    @Length(max = 100, message = "长度不能超过100")
    @TableField(value = "sender_im_account", condition = EQUAL)
    private String senderImAccount;

    /**
     * 发送者ID
     */
    @ApiModelProperty(value = "发送者ID")
    @TableField("sender_id")
    private Long senderId;

    @ApiModelProperty(value = "发送人角色(doctor, AI)")
    @Length(max = 50, message = "长度不能超过50")
    @TableField(value = "sender_role_type", condition = EQUAL)
    private String senderRoleType;

    /**
     * 群组ID（医生ID）
     */
    @ApiModelProperty(value = "群组ID（医生ID）")
    @TableField("im_group_id")
    private Long imGroupId;

    @ApiModelProperty(value = "消息内容")
    @Length(max = 1000, message = "长度不能超过1000")
    @TableField(value = "content", condition = LIKE)
    private String content;

    @ApiModelProperty(value = "类型(text, ASSISTANT_INTRODUCTION, HTML, KEY_WORD_CMS )")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "type_", condition = LIKE)
    private String type;

    @ApiModelProperty(value = "发送状态(发送中, 发送失败，发送成功)")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "send_status", condition = EQUAL)
    private String sendStatus;

    @ApiModelProperty(value = "回复的消息ID")
    @TableField(value = "reply_msg_id", condition = EQUAL)
    private Long replyMsgId;

    @ApiModelProperty(value = "AI回复的状态(成功或失败)")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "reply_status", condition = EQUAL)
    private String replyStatus;

    @ApiModelProperty(value = "消息异常原因")
    @Length(max = 255, message = "消息异常信息")
    @TableField(value = "send_error_msg", condition = LIKE)
    private String sendErrorMsg;

    @ApiModelProperty(value = "取消关键词消息 1 成功。 2 失败")
    @TableField(exist = false)
    private Integer cancelKeyWordMsg;

    @Override
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public LocalDateTime getCreateTime() {
        return super.getCreateTime();
    }

    @Override
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public LocalDateTime getUpdateTime() {
        return super.getUpdateTime();
    }


}
