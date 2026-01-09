package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用药预警-余药不足-具体数据
 *
 * @author 代嘉乐
 */
@Data
@ApiModel(value = "DrugsDeficiencyMessageData", description = "用药预警-余药不足-具体数据")
public class DrugsAvailableMessageData implements Serializable {
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
     * 剩余药量可用（逾期）天数
     */
    @ApiModelProperty(value = "剩余药量可用（逾期）天数")
    private Integer drugsAvailableDay;

    /**
     * 药量用完时间
     */
    @ApiModelProperty(value = "药量用完时间")
    private LocalDate drugsEndTime;
}
