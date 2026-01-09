package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "GptDoctorChatSaveDTO", description = "GPT医生聊天记录")
public class GptDoctorChatSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话标识
     */
    @ApiModelProperty(value = "会话标识")
    private String uid;

    @ApiModelProperty(value = "发送人IM账号")
    @NotEmpty
    private String senderImAccount;
    /**
     * 发送者ID
     */
    @ApiModelProperty(value = "发送者ID")
    @NotNull
    private Long senderId;


    @ApiModelProperty(value = "发送人角色 AI  doctor")
    @NotEmpty
    @Length(max = 50, message = "长度不能超过50")
    private String senderRoleType;

    /**
     * 群组ID（医生ID）
     */
    @ApiModelProperty(value = "群组ID（医生ID）")
    @NotNull
    private Long imGroupId;


    @ApiModelProperty(value = "消息内容")
    @Length(max = 600, message = "长度不能超过600")
    private String content;

    @ApiModelProperty(value = "消息类型 (text, ASSISTANT_INTRODUCTION, HTML, KEY_WORD_CMS)")
    @Length(max = 255, message = "长度不能超过255")
    @NotEmpty
    private String type;

    @ApiModelProperty(value = "租户")
    private String tenantCode;

}
