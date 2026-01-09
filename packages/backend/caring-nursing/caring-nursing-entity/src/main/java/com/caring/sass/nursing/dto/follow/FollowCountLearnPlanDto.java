package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "FollowCountLearnPlanDto", description = "学习计划统计的结构")
public class FollowCountLearnPlanDto {

    @ApiModelProperty(value = "计划的名称")
    private String planName;

    private List<CmsPushReadDetail> cmsPushReadDetails;

}
