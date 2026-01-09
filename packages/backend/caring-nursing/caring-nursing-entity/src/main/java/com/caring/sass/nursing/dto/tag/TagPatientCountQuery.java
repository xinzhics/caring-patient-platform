package com.caring.sass.nursing.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: TagPatientCountQuery
 * @author: 杨帅
 * @date: 2024/3/29
 */
@Data
@ApiModel("医助医生查询项目标签和标签下患者数量")
public class TagPatientCountQuery {


    @ApiModelProperty(value = "维度: all, 或者不传")
    private String dimension;


    @ApiModelProperty(value = "医生ID")
    private Long doctorId;


    @ApiModelProperty(value = "医助ID")
    private Long serviceAdvisorId;


    @ApiModelProperty(value = "小组ID")
    private Long groupId;

}
