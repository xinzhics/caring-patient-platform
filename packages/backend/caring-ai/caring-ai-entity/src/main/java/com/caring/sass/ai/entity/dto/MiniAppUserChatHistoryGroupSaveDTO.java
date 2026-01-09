package com.caring.sass.ai.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 敏智用户历史对话
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
@ApiModel(value = "MiniAppUserChatHistoryGroupSaveDTO", description = "敏智用户历史对话")
public class MiniAppUserChatHistoryGroupSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 对话的历史标题
     */
    @ApiModelProperty(value = "对话的历史标题")
    private String contentTitle;

}
