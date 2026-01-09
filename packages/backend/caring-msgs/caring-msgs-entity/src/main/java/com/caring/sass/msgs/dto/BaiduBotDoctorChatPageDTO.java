package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BaiduBotDoctorChatPageDTO", description = "百度灵医BOT医生聊天记录")
public class BaiduBotDoctorChatPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话标识
     */
    @ApiModelProperty(value = "会话标识")
    @Length(max = 64, message = "会话标识长度不能超过64")
    private String sessionId;
    /**
     * 发送者ID
     */
    @ApiModelProperty(value = "发送者ID")
    private Long senderId;
    /**
     * 消息角色(doctor, ai)
     */
    @ApiModelProperty(value = "消息角色(doctor, ai)")
    @Length(max = 50, message = "消息角色(doctor, ai)长度不能超过50")
    private String senderRoleType;
    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String content;
    /**
     * 类型(文本)
     */
    @ApiModelProperty(value = "类型(文本)")
    @Length(max = 255, message = "类型(文本)长度不能超过255")
    private String type;
    /**
     * 发送状态(发送成功，发送失败，发送中)
     */
    @ApiModelProperty(value = "发送状态(发送成功，发送失败，发送中)")
    @Length(max = 255, message = "发送状态(发送成功，发送失败，发送中)长度不能超过255")
    private String sendStatus;
    /**
     * 发送失败时的收集信息
     */
    @ApiModelProperty(value = "发送失败时的收集信息")
    @Length(max = 65535, message = "发送失败时的收集信息长度不能超过65,535")
    private String sendErrorMsg;
    /**
     * 回复的消息ID
     */
    @ApiModelProperty(value = "回复的消息ID")
    private Long replyMsgId;
    /**
     * 回复的消息状态(成功 失败)
     */
    @ApiModelProperty(value = "回复的消息状态(成功 失败)")
    @Length(max = 255, message = "回复的消息状态(成功 失败)长度不能超过255")
    private String replyStatus;

}
