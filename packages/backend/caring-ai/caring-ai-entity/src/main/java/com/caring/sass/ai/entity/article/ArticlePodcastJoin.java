package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.podcast.PodcastTaskStatus;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 播客和创作文章关联表
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-18
 */
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_podcast_join")
@ApiModel(value = "ArticlePodcastJoin", description = "播客和创作文章关联表")
@AllArgsConstructor
public class ArticlePodcastJoin extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "任务ID")
    @TableField(value = "task_id", condition = EQUAL)
    private Long taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @TableField(value = "task_name", condition = LIKE)
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 任务类型 音频 文章
     */
    @ApiModelProperty(value = "任务类型 音频 文章")
    @Length(max = 20, message = "任务类型 音频 文章长度不能超过20")
    @TableField(value = "task_type", condition = EQUAL)
    @Excel(name = "任务类型 音频 文章")
    private TaskType taskType;

    /**
     * 任务状态 已完成 未完成
     */
    @ApiModelProperty(value = "任务状态 已完成 未完成")
    @Length(max = 100, message = "任务状态 已完成 未完成长度不能超过100")
    @TableField(value = "task_status", condition = EQUAL)
    @Excel(name = "任务状态 已完成 未完成")
    private TaskStatus taskStatus;

    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    @NotNull(message = "所属用户ID不能为空")
    @TableField("user_id")
    @Excel(name = "所属用户ID")
    private Long userId;

    @ApiModelProperty(value = "展示在ai医生数字分身平台")
    @TableField("show_ai_knowledge")
    private Boolean showAIKnowledge;

    public ArticlePodcastJoin() {
    }

    public ArticlePodcastJoin(ArticleTask task) {
        this.createTime = task.getCreateTime();
        this.userId = task.getCreateUser();
        this.updateTime = LocalDateTime.now();
        this.taskId = task.getId();
        this.taskName = task.getTitle();
        this.taskType = TaskType.ARTICLE;
        this.taskStatus = TaskStatus.NOT_FINISHED;
    }


    public ArticlePodcastJoin(Podcast podcast) {
        this.createTime = podcast.getCreateTime();
        this.userId = podcast.getUserId();
        this.updateTime = LocalDateTime.now();
        this.taskId = podcast.getId();
        this.taskName = podcast.getPodcastName();
        this.taskType = TaskType.AUDIO;
        PodcastTaskStatus podcastTaskStatus = podcast.getTaskStatus();
        if (podcastTaskStatus.equals(PodcastTaskStatus.TASK_FINISH)) {
            this.taskStatus = TaskStatus.FINISHED;
        } else {
            this.taskStatus = TaskStatus.NOT_FINISHED;
        }

    }

}
