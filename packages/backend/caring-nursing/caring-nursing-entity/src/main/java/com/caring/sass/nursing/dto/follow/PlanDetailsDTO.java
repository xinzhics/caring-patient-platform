package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PlanDetailsDTO {

    @ApiModelProperty("计划的类型")
    Integer planType;

    @ApiModelProperty("计划的ID")
    Long planId;

    @ApiModelProperty("学习计划中设置的文章的ID")
    Long cmsId;

    @ApiModelProperty("一个外部的URL")
    String hrefUrl;

    @ApiModelProperty("计划展示的第一标题")
    String firstShowTitle;

    @ApiModelProperty("计划展示的第二标题")
    String secondShowTitle;

    @ApiModelProperty("执行的日期")
    LocalDate planExExecutionDay;

    @ApiModelProperty("计划执行的时间")
    LocalTime planExecutionDate;

    @ApiModelProperty("-1 未推送， 1 表示 推送并已读， 0 表示推送未读")
    Integer readStatus = -1;

    @ApiModelProperty("未读文章 设置为已读 接口需要的 ID")
    Long messageId;
}
