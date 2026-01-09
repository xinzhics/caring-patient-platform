package com.caring.sass.nursing.dto.traceInto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormFieldInfo {

    @ApiModelProperty("计划的ID")
    Long planId;

    @ApiModelProperty("计划的表单ID")
    Long formId;

    @ApiModelProperty("题目ID")
    String formFieldId;

    @ApiModelProperty("题目名称")
    String formFieldName;

    @ApiModelProperty("已配置")
    Boolean setting;

    @ApiModelProperty("是否是子题目")
    Integer isChildField;

    @ApiModelProperty("选项信息")
    List<FormFieldOptionInfo> optionInfos;

}