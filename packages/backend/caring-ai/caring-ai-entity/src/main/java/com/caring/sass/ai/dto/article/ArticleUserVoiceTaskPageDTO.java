package com.caring.sass.ai.dto.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 声音管理
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
@ApiModel(value = "ArticleUserVoiceTaskPageDTO", description = "数字人视频制作任务")
public class ArticleUserVoiceTaskPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull
    private Long userId;
    /**
     * 音色id
     */
    @ApiModelProperty(value = "音色id")
    private Long voiceId;
    /**
     * 形象id
     */
    @ApiModelProperty(value = "形象id")
    private Long avatarId;


    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    @Length(max = 255, message = "任务状态长度不能超过255")
    private String taskStatus;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务类型 图片数字人 对口型数字人")
    private String taskType;

    @ApiModelProperty("1 文本， 2 本地音频， 3 我的博客")
    private Integer createType;


    @ApiModelProperty(value = "展示在ai医生数字分身平台")
    private Boolean showAIKnowledge;



}
