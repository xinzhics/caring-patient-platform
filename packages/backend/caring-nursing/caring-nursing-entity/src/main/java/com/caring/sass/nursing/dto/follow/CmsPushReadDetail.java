package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 随访 学习计划 类型 文章的统计
 */
@Data
public class CmsPushReadDetail {

    @ApiModelProperty(value = "文章ID")
    private Long cmsId;

    @ApiModelProperty(value = "文章链接")
    private String cmsLink;

    @ApiModelProperty(value = "文章标题")
    private String cmsTitle;

    @ApiModelProperty(value = "文章推送数量")
    private int pushUserNumber;

    @ApiModelProperty(value = "文章打开用户总数")
    private int readUserNumber;

    @ApiModelProperty(value = "文章未打开用户总数")
    private int noReadUserNumber;

    @ApiModelProperty(value = "阅读率")
    private int readingRate;

    public CmsPushReadDetail() {
        this.pushUserNumber = 0;
        this.readUserNumber = 0;
        this.noReadUserNumber = 0;
        this.readingRate = 0;
    }
}