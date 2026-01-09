package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * Ai创作文章大纲
 * </p>
 *
 * @author 杨帅
 * @since 2024-08-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleTitlePageDTO", description = "Ai创作文章大纲")
public class ArticleTitlePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * AI创作任务
     */
    @ApiModelProperty(value = "AI创作任务")
    private Long taskId;
    /**
     * 文章大纲
     */
    @ApiModelProperty(value = "文章大纲")
    @Length(max = 500, message = "文章大纲长度不能超过500")
    private String articleTitle;

}
