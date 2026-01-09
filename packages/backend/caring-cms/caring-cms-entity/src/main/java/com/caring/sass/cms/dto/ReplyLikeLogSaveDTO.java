package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 点赞记录
 * </p>
 *
 * @author leizhi
 * @since 2021-03-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ReplyLikeLogSaveDTO", description = "点赞记录")
public class ReplyLikeLogSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞人id
     */
    @ApiModelProperty(value = "点赞人id")
    @NotNull(message = "点赞人id不能为空")
    private Long userId;
    /**
     * 评论id
     */
    @ApiModelProperty(value = "评论id")
    @NotNull(message = "评论id不能为空")
    private Long replyId;


    @ApiModelProperty(value = "角色")
    @NotNull(message = "角色不能为空")
    private String roleType;

}
