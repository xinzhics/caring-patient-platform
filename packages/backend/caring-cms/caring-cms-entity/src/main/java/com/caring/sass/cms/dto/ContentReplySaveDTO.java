package com.caring.sass.cms.dto;

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
 * 内容留言
 * </p>
 *
 * @author leizhi
 * @since 2020-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ContentReplySaveDTO", description = "内容留言")
public class ContentReplySaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 留言人
     */
    @ApiModelProperty(value = "留言人ID")
    @NotNull(message = "留言人ID不能为空")
    private Long replierId;

    @ApiModelProperty(value = "角色")
    @NotNull(message = "角色不能为空")
    private String roleType;

    /**
     * 文章Id
     */
    @ApiModelProperty(value = "文章Id", required = true)
    @NotNull(message = "文章ID不能为空")
    private Long contentId;

    /**
     * 留言内容
     */
    @ApiModelProperty(value = "留言内容")
    @NotEmpty(message = "留言内容不能为空")
    @Length(max = 2000, message = "留言内容长度不能超过2000")
    private String content;
}
