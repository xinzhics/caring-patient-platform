package com.caring.sass.nursing.entity.plan;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName PatientCustomPlanTime
 * @Description 记录用户自定义的推送时间
 * @Author yangShuai
 * @Date 2021/8/30 9:32
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_patient_custom_plan_time")
@ApiModel(value = "PatientCustomPlanTime", description = "自定义护理计划推送")
@AllArgsConstructor
public class PatientCustomPlanTime extends Entity<Long> {


    @ApiModelProperty(value = "病人ID")
    @TableField(value = "patient_id", condition = EQUAL)
    @Excel(name = "病人ID")
    private Long patientId;

    @ApiModelProperty(value = "开启自定义 (1 开启， 2 关闭)")
    @TableField(value = "customize_status", condition = EQUAL)
    @Excel(name = "开启自定义")
    private Integer customizeStatus;

    @ApiModelProperty(value = "护理计划ID")
    @TableField(value = "nursing_plant_id", condition = EQUAL)
    @Excel(name = "护理计划ID")
    private Long nursingPlantId;

    @ApiModelProperty(value = "护理计划详情ID")
    @TableField("nursing_plan_detail_id")
    @Excel(name = "护理计划详情ID")
    private Long nursingPlanDetailId;

    @ApiModelProperty(value = "护理计划推送时间详情ID")
    @TableField("nursing_plan_detail_time_id")
    @Excel(name = "护理计划推送时间详情ID")
    private Long nursingPlanDetailTimeId;

    @ApiModelProperty(value = "下一次提醒的时间")
    @TableField("next_remind_time")
    @Excel(name = "下一次提醒的时间")
    private LocalDateTime nextRemindTime;

    @ApiModelProperty(value = "提醒未填的超时时间")
    @TableField("remind_time_out")
    @Excel(name = "提醒未填的超时时间")
    private LocalDateTime remindTimeOut;


    @ApiModelProperty(value = "下次提醒之后 搁几天推送 频率(0:单次 )")
    @TableField("frequency")
    @Excel(name = "推送频率(0:单次 )")
    private Integer frequency;

    @ApiModelProperty(value = "护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）")
    @TableField(value = "plan_type", condition = EQUAL)
    @Excel(name = "护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）", replace = {"是_true", "否_false", "_null"})
    private Integer planType;


}
