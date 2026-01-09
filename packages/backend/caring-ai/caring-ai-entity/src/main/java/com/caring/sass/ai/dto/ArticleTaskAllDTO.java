package com.caring.sass.ai.dto;

import com.caring.sass.ai.entity.article.ArticleOutline;
import com.caring.sass.ai.entity.article.ArticleTask;
import com.caring.sass.ai.entity.article.ArticleTitle;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * Ai创作任务
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
@ApiModel(value = "ArticleTaskAllDTO", description = "Ai创作任务")
public class ArticleTaskAllDTO implements Serializable {

    @ApiModelProperty("Ai创作任务")
    private ArticleTask articleTask;

    @ApiModelProperty("Ai创作任务提纲 正文")
    private ArticleOutline articleOutline;


    @ApiModelProperty("Ai创作任务标题")
    private ArticleTitle articleTitle;



}
