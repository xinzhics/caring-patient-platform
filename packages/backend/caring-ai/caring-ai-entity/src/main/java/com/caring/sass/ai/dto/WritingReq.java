package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * AI写作请求参数
 *
 * @author leizhi
 */
@Data
@ApiModel(description = "AI写作请求参数")
public class WritingReq {

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 文章受众
     */
    private String audience;

    /**
     * 写作风格
     */
    private String writingStyle;

    /**
     * 字数
     */
    private String wordNumber;

    /**
     * 自动配图：是、否
     */
    private String autoMap;

    /**
     * 正文内容
     */
    private String article;

    /**
     * 大纲
     */
    private String outline;

    /**
     * 用户请求（指令）
     */
    private String query;

    /**
     * 前端要求的版本
     */
    private String version;

}
