package com.caring.sass.nursing.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: TagPatientCountResult
 * @author: 杨帅
 * @date: 2024/3/29
 */
@Data
@ApiModel
public class TagPatientCountResult {

    @ApiModelProperty("标签ID")
    private Long tagId;


    @ApiModelProperty("患者数量的统计")
    private Integer patientCount;


}
