package com.caring.sass.ai.dto.podcast;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PodcastAudioTaskSaveDTO", description = "播客音频任务")
public class PodcastAudioTaskSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 播客ID
     */
    @ApiModelProperty(value = "播客ID")
    @NotNull(message = "播客ID不能为空")
    private Long podcastId;
    /**
     * 音频文本
     */
    @ApiModelProperty(value = "音频文本")
    @Length(max = 65535, message = "音频文本长度不能超过65,535")
    private String audioText;
    /**
     * 声音设置ID
     */
    @ApiModelProperty(value = "声音设置ID")
    @NotNull(message = "声音设置ID不能为空")
    private Long soundSetId;
    /**
     * 音频任务ID
     */
    @ApiModelProperty(value = "音频任务ID")
    @NotEmpty(message = "音频任务ID不能为空")
    @Length(max = 50, message = "音频任务ID长度不能超过50")
    private String taskId;
    /**
     * 音频任务状态
     */
    @ApiModelProperty(value = "音频任务状态")
    @NotEmpty(message = "音频任务状态不能为空")
    @Length(max = 500, message = "音频任务状态长度不能超过500")
    private String taskStatus;
    /**
     * 音频任务状态码
     */
    @ApiModelProperty(value = "音频任务状态码")
    @NotEmpty(message = "音频任务状态码不能为空")
    @Length(max = 500, message = "音频任务状态码长度不能超过500")
    private String taskStatusCode;
    /**
     * 任务异常消息
     */
    @ApiModelProperty(value = "任务异常消息")
    @NotEmpty(message = "任务异常消息不能为空")
    @Length(max = 1000, message = "任务异常消息长度不能超过1000")
    private String taskErrorMessage;
    /**
     * 任务顺序
     */
    @ApiModelProperty(value = "任务顺序")
    @NotNull(message = "任务顺序不能为空")
    private Integer taskSort;
    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "所属用户ID")
    @NotNull(message = "所属用户ID不能为空")
    private Long userId;

}
