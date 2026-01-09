package com.caring.sass.nursing.entity.drugs;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 患者添加的用药
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_patient_drugs")
@ApiModel(value = "PatientDrugs", description = "患者添加的用药")
@AllArgsConstructor
public class PatientDrugs extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    @TableField("patient_id")
    @Excel(name = "患者id")
    private Long patientId;

    /**
     * 药品id
     */
    @ApiModelProperty(value = "药品id，此值未空，表示用户自己自定义的药品")
    @TableField("drugs_id")
    @Excel(name = "药品id")
    private Long drugsId;

    /**
     * 每天用药次数
     */
    @ApiModelProperty(value = "每周期用药次数")
    @TableField("number_of_day")
    @Excel(name = "每周期用药次数")
    private Integer numberOfDay;

    /**
     * 已推送打卡次数
     */
    @ApiModelProperty(value = "已推送打卡次数")
    @TableField("checkin_num")
    @Excel(name = "已推送打卡次数")
    private Integer checkinNum;

    /**
     * 用药时间:如（08:00,09:00）
     */
    @Deprecated
    @ApiModelProperty(value = "用药时间:如（08:00,09:00）")
    @Length(max = 255, message = "用药时间:如（08:00,09:00）长度不能超过255")
    @TableField(value = "drugs_time", condition = EQUAL)
    @Excel(name = "用药时间:如（08:00,09:00）")
    private String drugsTime;

    /**
     * 用药周期(0  代表无限期  1：选择截止日期)
     */
    @ApiModelProperty(value = "用药周期(0  代表无限期  1：选择截止日期)")
    @TableField("cycle")
    @Excel(name = "用药周期(0  代表无限期  1：选择截止日期)")
    private Integer cycle;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    @TableField("end_time")
    @Excel(name = "结束日期", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime endTime;

    /**
     * 每次剂量
     */
    @ApiModelProperty(value = "每次剂量")
    @TableField("dose")
    @Excel(name = "每次剂量")
    private Float dose;

    /**
     * 0：使用  1：停用（不推送，没有终止）  2：终止用药（历史用药）
     */
    @ApiModelProperty(value = "0：使用  1：停用（手动终止, 不进行推送）  2：终止用药（系统自动设置）")
    @TableField(value = "status", condition = EQUAL)
    @Excel(name = "0：使用  1：停用（没有终止）  2：终止用药（历史用药）")
    private Integer status;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    @Length(max = 20, message = "单位长度不能超过20")
    @TableField(value = "unit", condition = LIKE)
    @Excel(name = "单位")
    private String unit;

    /**
     * 周期天数
     */
    @ApiModelProperty(value = "周期循环次数")
    @TableField("cycle_day")
    @Excel(name = "周期天数")
    private Integer cycleDay;

    /**
     * 药品图标
     */
    @ApiModelProperty(value = "药品图标")
    @Length(max = 200, message = "药品图标长度不能超过200")
    @TableField(value = "medicine_icon", condition = LIKE)
    @Excel(name = "药品图标")
    private String medicineIcon;

    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 200, message = "药品名称长度不能超过200")
    @TableField(value = "medicine_name", condition = LIKE)
    @Excel(name = "药品名称")
    private String medicineName;

    /**
     * 盒数
     */
    @ApiModelProperty(value = "盒数")
    @TableField("number_of_boxes")
    @Excel(name = "盒数")
    private Integer numberOfBoxes;

    /**
     * {@link com.caring.sass.nursing.enumeration.EarlyWarningMonitorEnum}
     */
    @ApiModelProperty(value = "预警监听 1： 可监听， 2：不需要监听")
    @TableField("early_warning_monitor")
    private Integer earlyWarningMonitor;

    /**
     * 关闭提醒 1 表示关闭， 2 表示开启
     */
    @ApiModelProperty(value = "关闭提醒 1 表示关闭， 2 表示开启")
    @TableField("close_remind")
    private Integer closeRemind;

    @ApiModelProperty(value = "开始吃药日期")
    @TableField("start_take_medicine_date")
    private LocalDate startTakeMedicineDate;

    @ApiModelProperty(value = "停止用药原因（recover: 病好了， bad_curative_effect: 疗效不好，" +
            "adverse_reactions: 出现严重不良反应，abnormal_test_index：检验指标异常，economic_reasons: 经济原因，" +
            " doctor_changing_medicine: 医生换药， do_not_want_to_eat: 不想吃了，  other: 其他（请补充说明停药原因）")
    @TableField("stop_reason")
    private String stopReason;


    @ApiModelProperty(value = "停止用药原因备注")
    @TableField("stop_reason_remark")
    private String stopReasonRemark;

    /**
     * 购药提醒时间
     */
    @Deprecated
    @ApiModelProperty(value = "购药提醒时间")
    @TableField("buy_drugs_reminder_time")
    private LocalDate buyDrugsReminderTime;

    @ApiModelProperty(value = "时间周期：天(day), 小时(hour)，周(week),月(moon)")
    @TableField("time_period")
    private PatientDrugsTimePeriodEnum timePeriod;

    @ApiModelProperty(value = "1到30")
    @TableField("cycle_duration")
    private Integer cycleDuration;

    @ApiModelProperty(value = "下次推送时间")
    @TableField("next_reminder_date")
    private LocalDateTime nextReminderDate;


    @ApiModelProperty(value = "用药时间")
    @TableField(exist = false)
    private List<PatientDrugsTimeSetting> patientDrugsTimeSettingList;

}
