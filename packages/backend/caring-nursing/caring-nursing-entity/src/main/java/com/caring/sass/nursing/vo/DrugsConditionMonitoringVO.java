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
 * 查询患者管理-用药预警-预警药品及条件
 *
 * @author 李祥
 */
@Data
@ApiModel(value = "DrugsConditionMonitoringVO", description = "查询患者管理-用药预警-预警药品及条件")
public class DrugsConditionMonitoringVO implements Serializable {

    private static final long serialVersionUID = 7402097294211529287L;
    @ApiModelProperty(value = "患者管理-用药预警-预警条件表ID ")
    private Long id;
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
    @Length(max = 2000, message = "规格长度不能超过2000")
    private String spec;

    /**
     * 厂商
     */
    @ApiModelProperty(value = "厂商")
    @Length(max = 100, message = "厂商长度不能超过100")
    private String manufactor;

    /**
     * 提醒时间（余药不足前X天）
     */
    @ApiModelProperty(value = "提醒时间（余药不足前X天）")
    private Long reminderTime;

    /**
     * 购药地址
     */
    @ApiModelProperty(value = "购药地址")
    @Length(max = 1000, message = "购药地址长度不能超过1000")
    private String buyingMedicineUrl;

    /**
     * 模板消息ID
     */
    @ApiModelProperty(value = "模板消息ID")
    private Long templateMsgId;
}
