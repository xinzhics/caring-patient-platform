package com.caring.sass.ai.dto.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 形象管理
 * </p>
 *
 * @author leizhi
 * @since 2025-02-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleUserAvatarPageDTO", description = "形象管理")
public class ArticleUserAvatarPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片链接
     */
    @ApiModelProperty(value = "图片链接")
    @Length(max = 500, message = "图片链接长度不能超过500")
    private String avatarUrl;
    /**
     * 1默认，其他否
     */
    @ApiModelProperty(value = "1默认，其他否")
    private Boolean defaultAvatar;

    @ApiModelProperty(value = "用户id")
    private Long userId;

}
