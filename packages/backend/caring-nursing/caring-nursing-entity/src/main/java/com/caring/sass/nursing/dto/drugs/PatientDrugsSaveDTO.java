package com.caring.sass.nursing.dto.drugs;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.constant.PatientDrugsTimePeriodEnum;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTimeSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 实体类
 * 患者添加的用药
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientDrugsSaveDTO", description = "患者添加的用药")
public class PatientDrugsSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者id
     */
    @NotNull
    @ApiModelProperty(value = "患者id")
    private Long patientId;
    /**
     * 药品id
     */
    @ApiModelProperty(value = "药品id")
    private Long drugsId;
    /**
     * 每天用药次数
     */
    @ApiModelProperty(value = "周期内用药次数")
    private Integer numberOfDay;
    /**
     * 每天已推送打卡次数
     */
    @ApiModelProperty(value = "每天已推送打卡次数")
    private Integer checkinNum;
    /**
     * 用药时间:如（08:00,09:00）
     */
    @Deprecated
    @ApiModelProperty(value = "已弃用 用药时间:如（08:00,09:00）")
    private String drugsTime;
    /**
     * 用药周期(0  代表无限期  1：选择截止日期)
     */
    @ApiModelProperty(value = "用药周期(0  代表无限期  1：选择截止日期)")
    private Integer cycle;
    /**
     * 每次剂量
     */
    @ApiModelProperty(value = "每次剂量")
    private Float dose;
    /**
     * 0：使用  1：停用（不推送，没有终止）  2：终止用药（历史用药）
     */
    @ApiModelProperty(value = "0：使用  1：停用（不推送，没有终止）  2：终止用药（历史用药）")
    private Integer status;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    @Length(max = 20, message = "单位长度不能超过20")
    private String unit;
    /**
     * 周期天数
     */
    @ApiModelProperty(value = "周期循环次数")
    private Integer cycleDay;
    /**
     * 药品图标
     */
    @ApiModelProperty(value = "药品图标")
    @Length(max = 200, message = "药品图标长度不能超过200")
    private String medicineIcon;
    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 200, message = "药品名称长度不能超过200")
    private String medicineName;
    /**
     * 盒数
     */
    @ApiModelProperty(value = "盒数")
    private Integer numberOfBoxes;

    @ApiModelProperty(value = "时间周期：天(day), 小时(hour)，周(week),月(moon)")
    @TableField("time_period")
    private PatientDrugsTimePeriodEnum timePeriod;


    @ApiModelProperty(value = "1到30")
    @TableField("cycle_duration")
    private Integer cycleDuration;

    /**
     * 购药提醒时间
     */
    @ApiModelProperty(value = "购药提醒时间")
    private LocalDate buyDrugsReminderTime;


    @ApiModelProperty("每次时间设置(1小时1次不需要设置)")
    List<PatientDrugsTimeSetting> patientDrugsTimeSettingList;


    @ApiModelProperty(value = "IM消息ID")
    private Long messageId;


}
