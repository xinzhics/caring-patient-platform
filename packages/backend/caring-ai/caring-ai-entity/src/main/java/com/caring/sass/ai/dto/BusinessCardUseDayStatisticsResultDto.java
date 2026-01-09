package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class BusinessCardUseDayStatisticsResultDto {


    @ApiModelProperty(value = "名片数量")
    private Integer cardCount;

    /**
     * 浏览人数
     */
    @ApiModelProperty(value = "浏览人数 一个人一天只+1次")
    private Integer peopleOfViews;

    /**
     * 浏览次数
     */
    @ApiModelProperty(value = "浏览次数")
    private Integer numberOfViews;

    /**
     * 转发次数
     */
    @ApiModelProperty(value = "转发次数")
    private Integer forwardingFrequency;

    /**
     * AI对话点击人次
     */
    @ApiModelProperty(value = "AI对话点击人次")
    private Integer aiDialogueNumberCount;

    /**
     * AI对话点击次数
     */
    @ApiModelProperty(value = "AI对话点击次数")
    private Integer aiDialogueClickCount;

}
