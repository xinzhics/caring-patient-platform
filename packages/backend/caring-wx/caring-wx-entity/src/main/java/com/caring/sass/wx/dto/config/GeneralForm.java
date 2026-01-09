package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GeneralForm", description = "微信配置信息")
public class GeneralForm {

    /**
     * 微信AppId
     */
    @ApiModelProperty(value = "微信AppId")
    String wxAppId;

    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    String businessId;

    /**
     * 项目Id
     */
    @ApiModelProperty(value = "项目Id,项目Id和wxAppId 两者不能同时为空")
    String projectId;


    @ApiModelProperty(value = "项目租户")
    String tenantCode;

}
