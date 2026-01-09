package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 患者 随访中 学习计划的统计
 */
@Data
public class PatientFollowLearnPlanStatisticsDTO {


    @ApiModelProperty("学习计划名称")
    private String learnPlanName;

    @ApiModelProperty("文章总数")
    private int cmsNumber;

    @ApiModelProperty("收到数量")
    private int receiveNumber;

    @ApiModelProperty("已读")
    private int readNumber;

    @ApiModelProperty("未读")
    private int noReadNumber;

    @ApiModelProperty("阅读率")
    private int readingRate;

}
