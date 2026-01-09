package com.caring.sass.ai.dto;

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
 * Ai创作文章正文
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
@ApiModel(value = "ArticleOutlineSaveDTO", description = "Ai创作文章正文")
public class ArticleOutlineSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * AI创作任务
     */
    @NotNull
    @ApiModelProperty(value = "AI创作任务")
    private Long taskId;
    /**
     * 文章大纲
     */
    @ApiModelProperty(value = "文章大纲")
    @Length(max = 65535, message = "文章大纲长度不能超过65,535")
    private String articleOutline;
    /**
     * 文章的正文
     */
    @ApiModelProperty(value = "文章的正文")
    private String articleContent;

}
