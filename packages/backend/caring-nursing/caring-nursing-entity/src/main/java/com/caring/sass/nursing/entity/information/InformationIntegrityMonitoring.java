package com.caring.sass.nursing.entity.information;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 信息完整度监控指标
 * </p>
 *
 * @author yangshuai
 * @since 2022-05-24
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_information_integrity_monitoring")
@ApiModel(value = "InformationIntegrityMonitoring", description = "信息完整度监控指标")
@AllArgsConstructor
public class InformationIntegrityMonitoring extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 基本信息，疾病信息， 复查提醒，健康日志，护理计划，监测计划，自定义护理计划，自定义监测计划
     */
    @ApiModelProperty(value = "基本信息，疾病信息， 复查提醒，健康日志，护理计划，监测计划，自定义护理计划，自定义监测计划")
    @Length(max = 255, message = "基本信息，疾病信息， 复查提醒，健康日志，护理计划，监测计划，自定义护理计划，自定义监测计划长度不能超过255")
    @TableField(value = "business_type", condition = EQUAL)
    @Excel(name = "基本信息，疾病信息， 复查提醒，健康日志，护理计划，监测计划，自定义护理计划，自定义监测计划")
    private String businessType;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    @TableField("plan_id")
    @Excel(name = "计划ID")
    private Long planId;


    @ApiModelProperty(value = "计划名称")
    @TableField("plan_name")
    @Excel(name = "计划名称")
    private String planName;

    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @TableField("form_id")
    @Excel(name = "表单ID")
    private Long formId;

    /**
     * 表单中字段ID
     */
    @ApiModelProperty(value = "表单中字段ID")
    @Length(max = 40, message = "表单中字段ID长度不能超过40")
    @TableField(value = "field_id", condition = LIKE)
    @Excel(name = "表单中字段ID")
    private String fieldId;

    @ApiModelProperty(value = "表单中字段Label")
    @Length(max = 500, message = "表单中字段Label长度不能超过500")
    @TableField(value = "field_label", condition = LIKE)
    @Excel(name = "表单中字段Label")
    private String fieldLabel;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("monitor_sort")
    @Excel(name = "排序")
    private Integer monitorSort;


    /**
     * 添加人: 杨帅
     */
    @ApiModelProperty(value = "表单字段的描述")
    @TableField(exist = false)
    private String fieldLabelDesc;


}
