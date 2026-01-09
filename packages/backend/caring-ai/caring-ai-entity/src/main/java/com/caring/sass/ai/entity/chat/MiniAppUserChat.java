package com.caring.sass.ai.entity.chat;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 敏智用户聊天记录
 * </p>
 *
 * @author 杨帅
 * @since 2024-03-28
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_miniapp_user_chat")
@ApiModel(value = "MiniAppUserChat", description = "敏智用户聊天记录")
@AllArgsConstructor
public class MiniAppUserChat extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static final String sending = "sending";
    public static final String AiRole = "ai";    // 表示01bot对话助手
    public static final String User = "user";    // 表示用户
    public static final String sendSuccess = "sendSuccess";
    public static final String sendError = "sendError";

    /**
     * 对话ID，不存在时自动生成一次对话
     */
    @ApiModelProperty(value = "对话ID，不存在时自动生成一次对话")
    @NotNull(message = "对话ID，不存在时自动生成一次对话不能为空")
    @TableField("history_id")
    @Excel(name = "对话ID，不存在时自动生成一次对话")
    private Long historyId;

    /**
     * 会话标识
     */
    @ApiModelProperty(value = "会话标识")
    @Length(max = 64, message = "会话标识长度不能超过64")
    @TableField(value = "session_id", condition = LIKE)
    @Excel(name = "会话标识")
    private String sessionId;

    /**
     * 发送者ID
     */
    @ApiModelProperty(value = "发送者ID")
    @TableField("sender_id")
    @Excel(name = "发送者ID")
    private Long senderId;

    /**
     * 登录的用户类型(miniAppUser, doctor)
     */
    @ApiModelProperty(value = "登录的用户类型(miniAppUser, doctor)")
    @Length(max = 50, message = "登录的用户类型(miniAppUser, doctor)长度不能超过50")
    @TableField(value = "current_user_type", condition = LIKE)
    @Excel(name = "登录的用户类型(miniAppUser, doctor)")
    private String currentUserType;

    /**
     * AI交互中两者的角色(user, ai)
     */
    @ApiModelProperty(value = "AI交互中两者的角色(user, ai)")
    @Length(max = 50, message = "AI交互中两者的角色(user, ai)长度不能超过50")
    @TableField(value = "sender_role_type", condition = LIKE)
    @Excel(name = "AI交互中两者的角色(user, ai)")
    private String senderRoleType;


    @ApiModelProperty(value = "消息内容")
    @TableField("content")
    @Excel(name = "消息内容")
    private String content;

    @ApiModelProperty(value = "更多问题")
    @TableField("more_question")
    @Excel(name = "消息内容")
    private String moreQuestion;

    @ApiModelProperty(value = "参考资料")
    @TableField("reference")
    @Excel(name = "参考资料")
    private String reference;

    @ApiModelProperty(value = "相关搜索")
    @TableField("refer")
    @Excel(name = "相关搜索")
    private String refer;

    /**
     * 类型(文本)
     */
    @ApiModelProperty(value = "类型(文本)")
    @Length(max = 255, message = "类型(文本)长度不能超过255")
    @TableField(value = "type_", condition = LIKE)
    @Excel(name = "类型(文本)")
    private String type;

    /**
     * 发送状态(发送成功，发送失败，发送中)
     */
    @ApiModelProperty(value = "发送状态(发送成功，发送失败，发送中)")
    @Length(max = 255, message = "发送状态(发送成功，发送失败，发送中)长度不能超过255")
    @TableField(value = "send_status", condition = LIKE)
    @Excel(name = "发送状态(发送成功，发送失败，发送中)")
    private String sendStatus;

    /**
     * 发送失败时的收集信息
     */
    @ApiModelProperty(value = "发送失败时的收集信息")
    @Length(max = 65535, message = "发送失败时的收集信息长度不能超过65535")
    @TableField("send_error_msg")
    @Excel(name = "发送失败时的收集信息")
    private String sendErrorMsg;

    /**
     * 回复的消息ID
     */
    @ApiModelProperty(value = "回复的消息ID")
    @TableField("reply_msg_id")
    @Excel(name = "回复的消息ID")
    private Long replyMsgId;

    /**
     * 回复的消息状态(成功 失败)
     */
    @ApiModelProperty(value = "回复的消息状态(成功 失败)")
    @Length(max = 255, message = "回复的消息状态(成功 失败)长度不能超过255")
    @TableField(value = "reply_status", condition = LIKE)
    @Excel(name = "回复的消息状态(成功 失败)")
    private String replyStatus;

    @Override
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public LocalDateTime getCreateTime() {
        return super.getCreateTime();
    }



}
