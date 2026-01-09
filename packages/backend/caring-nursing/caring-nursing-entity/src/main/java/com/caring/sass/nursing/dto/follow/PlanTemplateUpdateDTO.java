package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "PlanTemplateUpdateDTO", description = "快捷修改指标监测或自定义护理计划的微信模板ID")
@AllArgsConstructor
public class PlanTemplateUpdateDTO {

    @ApiModelProperty("计划ID")
    private Long planId;

    @ApiModelProperty("微信模板ID")
    private String weiXinTemplateId;

    @ApiModelProperty("微信模板标题")
    private String weixinTemplateTitle;

    @ApiModelProperty("sass中的模板ID")
    private Long caringTemplateId;

    @NotEmpty
    private String tenantCode;


}
