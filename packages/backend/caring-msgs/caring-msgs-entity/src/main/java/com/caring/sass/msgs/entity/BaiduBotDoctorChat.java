package com.caring.sass.msgs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 百度灵医BOT医生聊天记录
 * </p>
 *
 * @author 杨帅
 * @since 2024-02-29
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_baidu_bot_doctor_chat")
@ApiModel(value = "BaiduBotDoctorChat", description = "百度灵医BOT医生聊天记录")
@AllArgsConstructor
public class BaiduBotDoctorChat extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static final String sending = "sending";
    public static final String AiRole = "assistant";    // 表示01bot对话助手
    public static final String User = "user";    // 表示用户
    public static final String sendSuccess = "sendSuccess";
    public static final String sendError = "sendError";

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
     * 消息角色(doctor, ai)
     */
    @ApiModelProperty(value = "消息角色(user: 表示用户, assistant： 表示01bot对话助手)")
    @TableField(value = "sender_role_type", condition = LIKE)
    private String senderRoleType;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")

    @TableField("content")
    @Excel(name = "消息内容")
    private String content;

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

    @Override
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public LocalDateTime getUpdateTime() {
        return super.getUpdateTime();
    }

    @Builder
    public BaiduBotDoctorChat(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,
                    String sessionId, Long senderId, String senderRoleType, String content, String type, 
                    String sendStatus, String sendErrorMsg, Long replyMsgId, String replyStatus) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.senderRoleType = senderRoleType;
        this.content = content;
        this.type = type;
        this.sendStatus = sendStatus;
        this.sendErrorMsg = sendErrorMsg;
        this.replyMsgId = replyMsgId;
        this.replyStatus = replyStatus;
    }

}
