package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_outline")
@ApiModel(value = "ArticleOutline", description = "Ai创作文章正文")
@AllArgsConstructor
public class ArticleOutline extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * AI创作任务
     */
    @ApiModelProperty(value = "AI创作任务")
    @TableField("task_id")
    @Excel(name = "AI创作任务")
    private Long taskId;

    /**
     * 文章大纲
     */
    @ApiModelProperty(value = "文章大纲")
    @Length(max = 65535, message = "文章大纲长度不能超过65535")
    @TableField("article_outline")
    @Excel(name = "文章大纲")
    private String articleOutline;

    /**
     * 文章的正文
     */
    @ApiModelProperty(value = "文章的正文")
    @Length(max = 65535, message = "文章的正文长度不能超过65535")
    @TableField("article_content")
    @Excel(name = "文章的正文")
    private String articleContent;


    @Builder
    public ArticleOutline(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long taskId, String articleOutline, String articleContent) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.taskId = taskId;
        this.articleOutline = articleOutline;
        this.articleContent = articleContent;
    }

}
