package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 写提纲入参
 *
 * @author leizhi
 */
@Data
@ApiModel(description = "AI写提纲入参")
public class OutLineReq {
    /**
     * 会话标识
     */
    @NotEmpty
    @ApiModelProperty(value = "会话标识")
    private String uid;

    /**
     * 关键词
     */
    @NotEmpty
    @ApiModelProperty(value = "关键词")
    private String keyword;

    /**
     * 文章受众
     */
    @NotEmpty
    @ApiModelProperty(value = "文章受众")
    private String audience;

    /**
     * 写作风格
     */
    @NotEmpty
    @ApiModelProperty(value = "写作风格")
    private String writingStyle;

    private String version;
}
