package com.caring.sass.ai.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MiniAppUserChatSaveDTO", description = "敏智用户聊天记录")
public class MiniAppUserChatSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对话ID，不存在时自动生成一次对话
     */
    @ApiModelProperty(value = "对话ID，不存在时自动生成一次对话")
    private Long historyId;
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
     * 登录的用户类型(miniAppUser, doctor)
     */
    @ApiModelProperty(value = "登录的用户类型(miniAppUser, doctor)")
    @Length(max = 50, message = "登录的用户类型(miniAppUser, doctor)长度不能超过50")
    private String currentUserType;
    /**
     * AI交互中两者的角色(user, ai)
     */
    @ApiModelProperty(value = "AI交互中两者的角色(user, ai)")
    @Length(max = 50, message = "AI交互中两者的角色(user, ai)长度不能超过50")
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

}
