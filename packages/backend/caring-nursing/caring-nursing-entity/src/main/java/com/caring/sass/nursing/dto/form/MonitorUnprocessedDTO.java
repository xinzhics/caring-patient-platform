package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("监测计划未处理列表DTO")
@Data
public class MonitorUnprocessedDTO {
    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划Id",required = true)
    @NotNull(message = "计划Id必填")
    private Long planId;
    /**
     * 搜索框患者名称
     */
    @ApiModelProperty(value = "搜索框患者名称")
    private String patientName;
    /**
     * 时间排序  1=倒序（默认）  2=正序
     */
    @ApiModelProperty(value = "时间排序  1=倒序（默认）  2=正序",required = true)
    @NotNull(message = "时间排序必填")
    private Integer sort = 1;
    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID",hidden = true)
    private Long nursingId;

}
