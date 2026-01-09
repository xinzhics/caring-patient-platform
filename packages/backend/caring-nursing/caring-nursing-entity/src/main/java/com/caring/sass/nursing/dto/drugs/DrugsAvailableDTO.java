package com.caring.sass.nursing.dto.drugs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("用药预警DTO")
@Data
public class DrugsAvailableDTO {
    /**
     * 搜索框患者名称
     */
    @ApiModelProperty(value = "搜索框患者名称")
    private String patientName;
    /**
     * 时间排序  2=倒序  1=正序（默认）
     */
    @ApiModelProperty(value = "时间排序  2=倒序（默认）  1=正序",example = "1")
    private Integer timeSort = 1;

    /**
     * 剩余药量（逾期天数）排序  2=倒序  1=正序（默认）
     */
    @ApiModelProperty(value = "剩余药量（逾期天数）排序  2=倒序  1=正序",example = "1")
    private Integer drugsSort = 1;
    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小", example = "20")
    @NotNull(message = "页面大小不能为空")
    private long size = 20;
    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", example = "1")
    @NotNull(message = "当前页不能为空")
    private long current = 1;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID",hidden = true)
    private Long nursingId;

    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",hidden = true)
    private String tenant;
}
