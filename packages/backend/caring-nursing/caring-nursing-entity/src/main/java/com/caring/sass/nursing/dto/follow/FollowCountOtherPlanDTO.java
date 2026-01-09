package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FollowCountOtherPlanDTO", description = "其他随访统计的结构")
public class FollowCountOtherPlanDTO {

    @ApiModelProperty(value = "计划的名称")
    private String planName;

    @ApiModelProperty(value = "计划的ID")
    private Long planId;

    @ApiModelProperty(value = "是个跳转链接")
    private String urlLink;

    @ApiModelProperty("计划的类型")
    private Integer planType;

    @ApiModelProperty("送达人数")
    private int pushUserNumber;

    @ApiModelProperty("打开的用户数量")
    private int openUserNumber;

    @ApiModelProperty("未打开的用户数量")
    private int noOpenUserNumber;

    @ApiModelProperty("打开率")
    private int openingRate;

    @ApiModelProperty("推送次数")
    private int pushNumber;

    @ApiModelProperty("已完成次数")
    private int completeNumber;

    @ApiModelProperty("未完成次数")
    private int noCompleteNumber;

    @ApiModelProperty("完成率")
    private int completeRate;


}
