package com.caring.sass.nursing.entity.drugs;

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
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 每次推送生成一条记录，（记录药量，药品）
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
@TableName("t_patient_drugs_time")
@ApiModel(value = "PatientDrugsTime", description = "每次推送生成一条记录，（记录药量，药品）")
@AllArgsConstructor
public class PatientDrugsTime extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者药品id
     */
    @ApiModelProperty(value = "药品id")
    @Length(max = 255, message = "药品id长度不能超过255")
    @TableField(value = "drugs_id", condition = EQUAL)
    @Excel(name = "患者药品id")
    private Long drugsId;

    @ApiModelProperty(value = "患者药品id")
    @Length(max = 255, message = "患者药品id长度不能超过255")
    @TableField(value = "patient_drugs_id", condition = EQUAL)
    @Excel(name = "患者药品id")
    private Long patientDrugsId;

    /**
     * 用药时间
     */
    @ApiModelProperty(value = "用药时间")
    @TableField("drugs_time")
    @Excel(name = "用药时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime drugsTime;

    /**
     * 状态(1:已打卡 2：未打卡)
     */
    @ApiModelProperty(value = "状态(1:已打卡 2：未打卡)")
    @TableField("status")
    @Excel(name = "状态(1:已打卡 2：未打卡)")
    private Integer status;

    /**
     * 消耗量
     */
    @ApiModelProperty(value = "消耗量")
    @TableField("drugs_dose")
    @Excel(name = "消耗量")
    private Float drugsDose;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    @TableField(value = "patient_id", condition = LIKE)
    @Excel(name = "患者id")
    private Long patientId;

    /**
     * 药品名称，冗余字段
     */
    @ApiModelProperty(value = "药品名称，冗余字段")
    @Length(max = 100, message = "药品名称，冗余字段长度不能超过100")
    @TableField(value = "medicine_name", condition = LIKE)
    @Excel(name = "药品名称，冗余字段")
    private String medicineName;

    /**
     * 药品图片，冗余字段
     */
    @ApiModelProperty(value = "药品图片，冗余字段")
    @Length(max = 255, message = "药品图片，冗余字段长度不能超过255")
    @TableField(value = "medicine_icon", condition = LIKE)
    @Excel(name = "药品图片，冗余字段")
    private String medicineIcon;

    /**
     * 吃药单位
     */
    @ApiModelProperty(value = "吃药单位")
    @Length(max = 20, message = "吃药单位长度不能超过20")
    @TableField(value = "unit", condition = LIKE)
    @Excel(name = "吃药单位")
    private String unit;

    @ApiModelProperty(value = "需要推送 1， 不需要推送0 ")
    @TableField(value = "need_push")
    private Integer needPush;

}
