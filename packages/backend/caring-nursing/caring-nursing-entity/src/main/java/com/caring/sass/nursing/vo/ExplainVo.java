package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ExplinVo", description = "监测数据监测计划说明Vo对象")
public class ExplainVo {

    /**
     *最大条件符号
     */
    @ApiModelProperty(value = "最大条件符号")
    private String maxConditionSymbol;
    /**
     *最大条件值
     */
    @ApiModelProperty(value = "最大条件值")
    private String maxConditionValue;
    /**
     *最小条件符号
     */
    @ApiModelProperty(value = "最小条件符号")
    private String minConditionSymbol;
    /**
     *最小条件值
     */
    @ApiModelProperty(value = "最小条件值")
    private String minConditionValue;
    /**
     *填写值单位
     */
    @ApiModelProperty(value = "填写值单位")
    private String realWriteValueRightUnit;
    /**
     * 表单字段
     */
    @ApiModelProperty(value = "表单字段")
    private String fieldLabel;
}
