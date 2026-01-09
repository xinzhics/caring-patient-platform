package com.caring.sass.nursing.dto.traceInto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @className: TraceIntoOptionStatisticsDTO
 * @author: 杨帅
 * @date: 2023/8/11
 */
@Data
public class TraceIntoOptionStatisticsDTO {

    @ApiModelProperty("全部提交记录")
    private Integer allFormResultNumber;

    @ApiModelProperty("异常记录")
    private Integer abnormalNumber;

    @ApiModelProperty("异常字段统计")
    List<FormFieldInfoAbnormal> formFieldInfoAbnormalList;

}
