package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("未处理列表修改处理状态DTO")
@Data
public class EliminateDto {
    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划Id",required = true)
    @NotNull (message = "计划id不能为空！")
    private Long planId;
    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    private Long patientId;
}
