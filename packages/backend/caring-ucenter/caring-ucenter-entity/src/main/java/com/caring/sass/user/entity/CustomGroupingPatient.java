package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName CustomGroupingPatient
 * @Description
 * @Author yangShuai
 * @Date 2021/8/27 11:24
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_custom_grouping_patient")
@ApiModel(value = "CustomGroupingPatient", description = "自定义分组下的患者")
@AllArgsConstructor
public class CustomGroupingPatient extends Entity<Long> {


    @ApiModelProperty(value = "自定义分组的ID")
    @TableField(value = "custom_grouping_id", condition = EQUAL)
    @Excel(name = "自定义分组的ID")
    private Long customGroupingId;


    @ApiModelProperty(value = "患者的ID")
    @TableField(value = "patient_id", condition = EQUAL)
    @Excel(name = "患者的ID")
    private Long patientId;



}
