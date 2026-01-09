package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用药预警-统计
 *
 * @author 代嘉乐
 */
@Data
@ApiModel(value = "DrugsStatisticsVo", description = "用药预警-统计")
public class DrugsStatisticsVo implements Serializable {
    /**
     * 药品ID
     */
    @ApiModelProperty(value = "药品ID")
    private Long drugsId;

    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    private String drugsName;

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    private String spec;

    /**
     * 厂商
     */
    @ApiModelProperty(value = "厂商")
    private String manufactor;

    /**
     * 当前用药患者人数
     */
    @ApiModelProperty(value = "当前用药患者人数")
    private int drugsPatient;

    /**
     * 正常患者数
     */
    @ApiModelProperty(value = "正常患者数")
    private int normalPatient;

    /**
     * 购药逾期患者数
     */
    @ApiModelProperty(value = "购药逾期患者数")
    private int drugsOverduePatient;

    /**
     * 余药不足患者数
     */
    @ApiModelProperty(value = "余药不足患者数")
    private int drugsDeficiencyPatient;

    /**
     * 正常患者占比
     */
    @ApiModelProperty(value = "正常患者占比")
    private String normalPatientRatio;

    /**
     * 购药逾期患者占比
     */
    @ApiModelProperty(value = "购药逾期患者占比")
    private String drugsOverduePatientRatio;

    /**
     * 余药不足患者占比
     */
    @ApiModelProperty(value = "余药不足患者占比")
    private String drugsDeficiencyPatientRatio;
}
