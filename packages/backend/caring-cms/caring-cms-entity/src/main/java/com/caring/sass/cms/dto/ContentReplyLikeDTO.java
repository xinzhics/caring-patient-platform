package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 内容留言
 * </p>
 *
 * @author leizhi
 * @since 2020-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ContentReplyLikeDTO", description = "内容留言点赞")
public class ContentReplyLikeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 留言id
     */
    @ApiModelProperty(value = "留言Id", required = true)
    @NotNull(message = "留言Id")
    private Long replyId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户Id", required = true)
    @NotNull(message = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "角色类型", required = true)
    @NotNull(message = "角色类型")
    private String roleType;

    @ApiModelProperty(value = "操作，1点赞，2取消点赞", required = true)
    @NotNull(message = "操作不能为空")
    private Integer operation;
}
