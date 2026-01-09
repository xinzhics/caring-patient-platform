package com.caring.sass.nursing.entity.plan;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 会员订阅护理计划
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_patient_nursing_plan")
@ApiModel(value = "PatientNursingPlan", description = "会员订阅护理计划")
@AllArgsConstructor
public class PatientNursingPlan extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 病人ID
     */
    @ApiModelProperty(value = "病人ID")
    @TableField(value = "patient_id", condition = EQUAL)
    @Excel(name = "病人ID")
    private Long patientId;

    /**
     * 护理计划ID
     */
    @ApiModelProperty(value = "护理计划ID")
    @TableField(value = "nursing_plant_id", condition = EQUAL)
    @Excel(name = "护理计划ID")
    private Long nursingPlantId;

    /**
     * 护理开始时间
     */
    @ApiModelProperty(value = "护理开始时间")
    @TableField("start_date")
    @Excel(name = "护理开始时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDate startDate;

    /**
     * 订阅状态(1：订阅   0：未订阅)
     */
    @ApiModelProperty(value = "订阅状态(1：订阅   0：未订阅)")
    @TableField(value = "is_subscribe", condition = EQUAL)
    @Excel(name = "订阅状态(1：订阅   0：未订阅)")
    private Integer isSubscribe;

    /**
     * 主要记录用药的订阅状态。当通过保存接口，将 用药提醒的 推送从 订阅修改为 取消订阅时，
     * 记录一下。后续添加用药时，就不自动打开了。
     */
    @ApiModelProperty(value = "患者手动取消订阅")
    @TableField(value = "patient_cancel_subscribe", condition = EQUAL)
    private Integer patientCancelSubscribe;

    @ApiModelProperty("护理计划名称,冗余字段")
    @TableField(exist = false)
    private String nursingPlanName;


    @ApiModelProperty("护理计划类型,冗余字段")
    @TableField(exist = false)
    private Integer planType;

    @ApiModelProperty("禁止关注（0 不禁止， 1 禁止关注）")
    @TableField(exist = false)
    private Integer disableSubscribe=0;


    @ApiModelProperty("记录用户自定义推送时间(未自定义则记录下次推送时间) 只针对复查提醒")
    @TableField(exist = false)
    private List<PatientCustomPlanTime> customPlanTimes;

}
