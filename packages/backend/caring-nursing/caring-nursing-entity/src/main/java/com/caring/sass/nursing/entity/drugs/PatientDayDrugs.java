package com.caring.sass.nursing.entity.drugs;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 实体类
 * 患者每天的用药量记录（一天生成一次）
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
@TableName("t_patient_day_drugs")
@ApiModel(value = "PatientDayDrugs", description = "患者每天的用药量记录（一天生成一次）")
@AllArgsConstructor
public class PatientDayDrugs extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    @TableField("patient_id")
    @Excel(name = "患者id")
    private Long patientId;

    /**
     * 每天用药总量
     * 这个字段没有意义
     */
    @Deprecated
    @ApiModelProperty(value = "每天用药总量")
    @TableField("drugs_count_of_day")
    @Excel(name = "每天用药总量")
    private Float drugsCountOfDay;

    /**
     * 每天已用总量
     */
    @Deprecated
    @ApiModelProperty(value = "每天已用总量")
    @TableField("take_drugs_count_of_day")
    @Excel(name = "每天已用总量")
    private Float takeDrugsCountOfDay;

    /**
     * 已打卡次数
     */
    @ApiModelProperty(value = "已打卡次数")
    @TableField("checkined_number")
    @Excel(name = "已打卡次数")
    private Integer checkinedNumber;

    /**
     * 总打卡次数
     */
    @ApiModelProperty(value = "总打卡次数")
    @TableField("checkin_number_total")
    @Excel(name = "总打卡次数")
    private Integer checkinNumberTotal;

    /**
     * 0:未打卡  1：部分打卡   2已打卡
     */
    @ApiModelProperty(value = "0:未打卡  1：部分打卡   2已打卡")
    @TableField("status")
    @Excel(name = "0:未打卡  1：部分打卡   2已打卡")
    private Integer status;

    @ApiModelProperty(value = "")
    @TableField("day_compliance")
    @Excel(name = "")
    private Integer dayCompliance;

    /**
     * 医生Id
     */
    @ApiModelProperty(value = "医生Id")
    @TableField("doctor_id")
    @Excel(name = "医生Id")
    private Long doctorId;

    /**
     * 医助Id
     */
    @ApiModelProperty(value = "医助Id")
    @TableField("service_advisor_id")
    @Excel(name = "医助Id")
    private Long serviceAdvisorId;




}
