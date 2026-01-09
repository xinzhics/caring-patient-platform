package com.caring.sass.nursing.enumeration;

import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName DrugsOperateType
 * @Description
 * @Author yangShuai
 * @Date 2022/1/6 13:31
 * @Version 1.0
 */
public enum FormResultCountScoreEnum {

    /**
     * 得分之和
     */
    sum_score("sum_score","得分之和"),

    /**
     * 得分的平均分
     */
    average_score("average_score","得分的平均分"),

    /**
     * 得分之和+平均分
     */
    sum_average_score("sum_average_score","得分之和+平均分");


    @ApiModelProperty(value = "类型")
    public String type;

    @ApiModelProperty(value = "类型名称")
    public String wayName;


    FormResultCountScoreEnum(String type, String wayName) {
        this.type = type;
        this.wayName = wayName;
    }
}
