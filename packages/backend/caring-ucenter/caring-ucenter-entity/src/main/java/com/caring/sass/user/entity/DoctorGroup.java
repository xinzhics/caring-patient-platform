package com.caring.sass.user.entity;

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
 * @ClassName DoctorGroup
 * @Description
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
@TableName("u_user_doctor_group")
@ApiModel(value = "DoctorGroup", description = "医生小组关联表")
@AllArgsConstructor
public class DoctorGroup extends Entity<Long> {

    @ApiModelProperty(value = "医生ID")
    @TableField(value = "doctor_id", condition = EQUAL)
    @Excel(name = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "小组ID")
    @TableField(value = "group_id", condition = EQUAL)
    @Excel(name = "小组ID")
    private Long groupId;

    @ApiModelProperty(value = "加入小组的时间")
    @TableField(value = "join_group_time", condition = EQUAL)
    @Excel(name = "加入小组的时间")
    protected LocalDateTime joinGroupTime;



}
