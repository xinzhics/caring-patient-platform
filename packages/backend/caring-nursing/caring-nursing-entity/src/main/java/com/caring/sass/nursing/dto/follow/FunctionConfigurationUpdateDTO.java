package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "FunctionConfigurationUpdateStatusDTO", description = "功能配置修改状态或微信模板ID")
@AllArgsConstructor
public class FunctionConfigurationUpdateDTO {

    @ApiModelProperty("功能配置的ID")
    private Long functionConfigurationId;

    @ApiModelProperty("计划的状态 1, 开启， 0关闭")
    private Integer functionStatus;

    @ApiModelProperty("微信模板ID")
    private String weiXinTemplateId;

    @NotEmpty
    private String tenantCode;


}
