package com.caring.sass.nursing.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 患者信息完整度查询对象
 *
 * @author Evan Zhai
 */
@Data
@ApiModel(value = "CompletenessInformationQo", description = "患者信息完整度查询对象")
public class CompletenessInformationQo implements Serializable {
    private static final long serialVersionUID = 9078939352820466385L;

    @ApiModelProperty(value = "患者名字")
    private String name;

    @ApiModelProperty(value = "信息完整度区间ID")
    private Long intervalId;

    @ApiModelProperty(value = "医助id")
    private Long nursingId;
}
