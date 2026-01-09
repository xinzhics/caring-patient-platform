package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;

@Data
public class FollowAllPlanDetailTimeDTO {

    @ApiModelProperty("复查提醒， 健康日志， 指标监测， 随访 的推送ID，推送日志中的workId")
    Long planDetailTimeId;

    @ApiModelProperty("学习计划中设置的文章的ID")
    Long cmsId;

    @ApiModelProperty("一个外部的URL")
    String hrefUrl;

    @ApiModelProperty("计划展示的第二标题")
    String secondShowTitle;

    @ApiModelProperty("计划执行的时间")
    LocalTime planExecutionDate;

    @ApiModelProperty("0 未读， 1： 已读")
    int cmsReadStatus = 0;

    @ApiModelProperty("未读文章 使用此字段变为已读")
    Long messageId;

}
