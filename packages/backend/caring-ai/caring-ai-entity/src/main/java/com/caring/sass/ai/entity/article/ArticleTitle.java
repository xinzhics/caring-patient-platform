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

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_title")
@ApiModel(value = "ArticleTitle", description = "Ai创作文章大纲")
@AllArgsConstructor
public class ArticleTitle extends Entity<Long> {

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
    @Length(max = 500, message = "文章大纲长度不能超过500")
    @TableField(value = "article_title", condition = LIKE)
    @Excel(name = "文章大纲")
    private String articleTitle;


    @Builder
    public ArticleTitle(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long taskId, String articleTitle) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.taskId = taskId;
        this.articleTitle = articleTitle;
    }

}
