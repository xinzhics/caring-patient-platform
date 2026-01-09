package com.caring.sass.ai.dto.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 上传视频底板后，获取底板的信息
 */
@ApiModel
@Data
public class ArticleVideoDTO {

    @ApiModelProperty("视频底板的链接")
    @NotEmpty
    private String videoUrl;

    @ApiModelProperty("是否需要音频")
    private boolean needAudio = true;

    @ApiModelProperty("解析到的音频链接")
    private String audioUrl;

    @ApiModelProperty("视频宽度")
    private int videoWidth;

    @ApiModelProperty("视频高度")
    private int videoHeight;

}
