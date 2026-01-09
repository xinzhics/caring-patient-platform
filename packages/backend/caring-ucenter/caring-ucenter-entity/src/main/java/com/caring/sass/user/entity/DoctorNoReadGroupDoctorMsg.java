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
 * @ClassName DoctorGroup
 * @Description 记录医生对小组中哪些医生设置了 不看他患者的消息
 * @Author yangShuai
 * @Date 2021/9/14 15:26
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_doctor_no_read_group_doctor_msg")
@ApiModel(value = "DoctorNoReadGroupDoctorMsg", description = "医生不看那些医生的患者IM")
@AllArgsConstructor
public class DoctorNoReadGroupDoctorMsg extends Entity<Long> {

    @ApiModelProperty(value = "医生ID")
    @TableField(value = "doctor_id", condition = EQUAL)
    @Excel(name = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "不想看某医生ID")
    @TableField(value = "no_read_group_doctor_id", condition = EQUAL)
    @Excel(name = "不想看某医生ID")
    private Long noReadGroupDoctorId;


}
