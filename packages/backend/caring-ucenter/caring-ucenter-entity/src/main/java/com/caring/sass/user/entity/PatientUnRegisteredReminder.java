package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @ClassName PatientUnRegisteredReminder
 * @Description
 * @Author yangShuai
 * @Date 2021/11/30 10:29
 * @Version 1.0
 */
@Data
@Deprecated
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_patient_unregistered_reminder")
@ApiModel(value = "PatientUnRegisteredReminder", description = "患者注册提醒")
@AllArgsConstructor
public class PatientUnRegisteredReminder extends Entity<Long> {

    @ApiModelProperty(value = "患者id")
    @TableField("patient_id")
    @Excel(name = "患者id")
    private Long patientId;

    @ApiModelProperty(value = "提醒的内容")
    @TableField("reminder")
    private String reminder;


    @ApiModelProperty(value = "异常说明")
    @TableField("error_message")
    private String errorMessage;
}
