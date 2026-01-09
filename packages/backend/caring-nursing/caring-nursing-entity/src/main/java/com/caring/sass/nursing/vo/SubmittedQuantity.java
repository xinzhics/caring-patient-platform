package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 监测计划统计提交数量
 *
 *
 */
@Data
@ApiModel(value = "SubmittedQuantity", description = "监测计划统计提交数量")
public class SubmittedQuantity {
    /**
     * 累计提交总数
     */
    @ApiModelProperty(value = "累计提交总数")
    private int totalSubmissions;
    /**
     * 累计提交异常数量
     */
    @ApiModelProperty(value = "累计提交异常数量")
    private int submitExceptionQuantity;
    /**
     * 已处理异常数量
     */
    @ApiModelProperty(value = "已处理异常数量")
    private int numberExceptionsQuantity;
    /**
     * 未处理异常数量
     */
    @ApiModelProperty(value = "未处理异常数量")
    private int untreatedExceptionsQuantity;
    /**
     * 提交数据正常数量
     */
    @ApiModelProperty(value = "提交数据正常数量")
    private int normalQuantity;
    /**
     * 已处理异常数量百分比
     */
    @ApiModelProperty(value = "已处理异常数量百分比")
    private String numberExceptionsQuantityPercentage;
    /**
     * 未处理异常数量百分比
     */
    @ApiModelProperty(value = "未处理异常数量百分比")
    private String untreatedExceptionsQuantityPercentage;
    /**
     * 提交数据正常数量百分比
     */
    @ApiModelProperty(value = "提交数据正常数量百分比")
    private String normalQuantityPercentage;
}
