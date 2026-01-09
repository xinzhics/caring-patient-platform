package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * AI起标题请求参数
 *
 * @author leizhi
 */
@Data
@ApiModel(description = "AI写作请求参数")
public class TitleReq {
    /**
     * 会话标识
     */
    @NotEmpty
    @ApiModelProperty(value = "会话标识")
    private String uid;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    private String keyword;

    /**
     * 文章受众
     */
    @ApiModelProperty(value = "文章受众")
    private String audience;

    /**
     * 写作风格
     */
    @ApiModelProperty(value = "写作风格")
    private String writingStyle;

    /**
     * 字数
     */
    @ApiModelProperty(value = "字数")
    private String wordNumber;


    /**
     * 正文内容
     */
    @ApiModelProperty(value = "正文内容")
    private String article;

    private String version;

}
