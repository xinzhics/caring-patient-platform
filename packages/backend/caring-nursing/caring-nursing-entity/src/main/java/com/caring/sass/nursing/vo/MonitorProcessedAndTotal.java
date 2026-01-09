package com.caring.sass.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "MonitorProcessedAndTotal", description = "监测数据已处理列表Vo对象")
public class MonitorProcessedAndTotal {
    /**
     * 已处理列表数据
     */
    @ApiModelProperty(value = "已处理列表数据")
    private List<MonitorProcessedVo> MonitorProcessedVo;
    /**
     * 总数量
     */
    @ApiModelProperty(value = "总条数")
    private Long total;
    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划id")
    private Long planId;
}
