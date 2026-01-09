package com.caring.sass.ai.entity.podcast;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 播客音频任务
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-12
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_podcast_audio_task")
@ApiModel(value = "PodcastAudioTask", description = "播客音频任务")
@AllArgsConstructor
public class PodcastAudioTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 播客ID
     */
    @ApiModelProperty(value = "播客ID")
    @NotNull(message = "播客ID不能为空")
    @TableField("podcast_id")
    @Excel(name = "播客ID")
    private Long podcastId;

    /**
     * 音频文本
     */
    @ApiModelProperty(value = "音频文本")
    @Length(max = 65535, message = "音频文本长度不能超过65535")
    @TableField("audio_text")
    @Excel(name = "音频文本")
    private String audioText;

    /**
     * 声音设置ID
     */
    @ApiModelProperty(value = "声音设置ID")
    @NotNull(message = "声音设置ID不能为空")
    @TableField("sound_set_id")
    @Excel(name = "声音设置ID")
    private Long soundSetId;

    /**
     * 音频任务ID
     */
    @ApiModelProperty(value = "音频任务ID")
    @NotEmpty(message = "音频任务ID不能为空")
    @Length(max = 50, message = "音频任务ID长度不能超过50")
    @TableField(value = "task_id", condition = LIKE)
    @Excel(name = "音频任务ID")
    private String taskId;

    /**
     * 音频任务状态
     */
    @ApiModelProperty(value = "音频任务状态")
    @NotEmpty(message = "音频任务状态不能为空")
    @Length(max = 500, message = "音频任务状态长度不能超过500")
    @TableField(value = "task_status", condition = LIKE)
    @Excel(name = "音频任务状态")
    private PodcastAudioTaskStatus taskStatus;

    /**
     * 音频任务状态码
     */
    @ApiModelProperty(value = "音频任务状态码")
    @NotEmpty(message = "音频任务状态码不能为空")
    @TableField(value = "audio_url", condition = LIKE)
    private String audioUrl;

    /**
     * 任务异常消息
     */
    @ApiModelProperty(value = "任务异常消息")
    @NotEmpty(message = "任务异常消息不能为空")
    @Length(max = 1000, message = "任务异常消息长度不能超过1000")
    @TableField(value = "task_error_message", condition = LIKE)
    @Excel(name = "任务异常消息")
    private String taskErrorMessage;

    /**
     * 任务顺序
     */
    @ApiModelProperty(value = "任务顺序")
    @NotNull(message = "任务顺序不能为空")
    @TableField("task_sort")
    @Excel(name = "任务顺序")
    private Integer taskSort;


    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    @NotNull(message = "所属用户ID不能为空")
    @TableField("user_id")
    @Excel(name = "所属用户ID")
    private Long userId;






}
