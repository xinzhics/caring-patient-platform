package com.caring.sass.nursing.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 查询患者管理-监测数据-监测计划数
 *
 * @author 李祥
 */
@Data
@ApiModel(value = "IndicatorsPlanVo", description = "查询患者管理-监测数据-监测计划")
public class IndicatorsPlanVo implements Serializable {

    private static final long serialVersionUID = 7402097294211529287L;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    private Long planId;

    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称")
    @Excel(name = "计划名称")
    private String planName;
}
