package com.caring.sass.nursing.dto.traceInto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @className: FormFieldOptionInfo
 * @author: 杨帅
 * @date: 2023/8/8
 */
@Data
public class FormFieldOptionInfo {

    @ApiModelProperty("计划的ID")
    Long planId;

    @ApiModelProperty("计划的表单ID")
    Long formId;

    @ApiModelProperty("选项所在题目ID")
    String formFieldId;

    @ApiModelProperty("选项ID")
    String fieldOptionId;

    @ApiModelProperty("选项名称")
    String fieldOptionName;

    @ApiModelProperty("异常选项所在父题目的题目ID")
    String parentFieldId;

    @ApiModelProperty("异常选项所在父题目的选项ID")
    String parentFieldOptionId;

    @ApiModelProperty("已配置")
    Boolean setting;

    @ApiModelProperty("是否是子题目选项")
    Integer isChildField;

    @ApiModelProperty("是否异常选项。")
    Boolean isExceptionOptions;

    @ApiModelProperty("子题目信息")
    List<FormFieldInfo> fieldInfos;
}
