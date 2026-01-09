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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * TODO:: 加密完成后删除
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_nursing_staff")
@ApiModel(value = "NursingStaff", description = "用户-医助")
@AllArgsConstructor
public class NursingStaffQuery extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @Length(max = 50, message = "用户名长度不能超过50")
    @TableField(value = "login_name", condition = LIKE)
    @Excel(name = "用户名")
    private String loginName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 20, message = "手机号码长度不能超过20")
    @TableField(value = "mobile", condition = LIKE)
    @Excel(name = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "数据是否加密")
    @TableField("encrypted")
    private Integer encrypted;

}
