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
@ApiModel(value = "DrugsListVo", description = "用药预警-统计-药品列表")
public class DrugsListVo implements Serializable {
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


}
