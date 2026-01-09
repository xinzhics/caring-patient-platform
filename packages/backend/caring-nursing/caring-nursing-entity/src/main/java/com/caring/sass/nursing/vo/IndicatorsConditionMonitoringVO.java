package com.caring.sass.nursing.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.entity.form.IndicatorsConditionMonitoring;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * 查询患者管理-监测数据-监测计划-监测指标及条件
 *
 * @author 李祥
 */
@Data
@ApiModel(value = "IndicatorsConditionMonitoringVO", description = "查询患者管理-监测数据-监测计划-监测指标及条件")
public class IndicatorsConditionMonitoringVO implements Serializable {

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
    private String planName;
    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    private Long formId;
    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段ID 指标字段ID")
    private String fieldId;
    /**
     * 表单字段
     */
    @ApiModelProperty(value = "表单字段 指标字段名称")
    private String fieldLabel;
    /**
     * 表单监控条件字段右侧单位
     */
    @ApiModelProperty(value = "表单监控条件字段右侧单位")
    private String realWriteValueRightUnit;
    /**
     * 异常条件
     */
    @ApiModelProperty(value = "异常条件")
    private List<IndicatorsConditionMonitoring> indicatorsConditionMonitorings;
}
