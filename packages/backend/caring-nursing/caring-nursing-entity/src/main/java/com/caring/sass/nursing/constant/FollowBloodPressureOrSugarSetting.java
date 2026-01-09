package com.caring.sass.nursing.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("血糖或者血压的推送模板设置")
public class FollowBloodPressureOrSugarSetting {

    @ApiModelProperty("血糖 血压计划的ID")
    private Long planId;

    @ApiModelProperty("血糖 血压推送设置的ID")
    private Long planDetailTimeId;

    @ApiModelProperty("推送的名称")
    private String pushName;

    @ApiModelProperty("模板管理中模板的ID")
    private Long templateId;

    @ApiModelProperty("微信公众号的模板ID")
    private String weiXinTemplateId;

}
