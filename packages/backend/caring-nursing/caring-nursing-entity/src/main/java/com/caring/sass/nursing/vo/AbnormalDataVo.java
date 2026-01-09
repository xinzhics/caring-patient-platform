package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "abnormalDataVo", description = "监测计划异常数据VO对象")
public class AbnormalDataVo implements Serializable {
    /**
     *  计划id
     */
    @ApiModelProperty(value = "计划id")
    private Long  planId;

    /**
     *  计划名称
     */
    @ApiModelProperty(value = "计划名称")
    private String planName;

    /**
     *  异常患者人数
     */
    @ApiModelProperty(value = "异常患者人数")
    private Integer patientNumber;

}
