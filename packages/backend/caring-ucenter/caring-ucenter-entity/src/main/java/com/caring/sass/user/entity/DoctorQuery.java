package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * 数据加密后删除
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_doctor")
@ApiModel(value = "Doctor", description = "医生表")
@AllArgsConstructor
public class DoctorQuery extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "手机号码")
    @Length(max = 12, message = "手机号码长度不能超过12")
    @TableField(value = "mobile", condition = LIKE)
    @Excel(name = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "数据是否加密")
    @TableField("encrypted")
    private Integer encrypted;

}
