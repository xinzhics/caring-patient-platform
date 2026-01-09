package com.caring.sass.ai.humanVideo.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author leizhi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "VideoCreationRequest", description = "视频合成请求入参")
public class VideoCreationRequest {

    @ApiModelProperty("任务名称")
    private String taskName;


    @ApiModelProperty("形象类型 1 图片 2 视频")
    private Integer templateType;

    @ApiModelProperty("形象ID")
    private Long templateId;

    @ApiModelProperty("音色ID")
    private Long voiceId;

    @ApiModelProperty("1 文本， 2 本地音频， 3 我的博客")
    private Integer createType;

    @ApiModelProperty("用户提供的文本内容")
    private String talkText;

    @ApiModelProperty("音频的url")
    private String audioUrl;

    @ApiModelProperty("博客ID")
    private Long podcastId;

    @NotNull(message = "用户id不能为空")
    private Long userId;

}