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

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 患者表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_patient")
@ApiModel(value = "Patient", description = "患者表")
@AllArgsConstructor
public class PatientQuery extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 50, message = "手机号码长度不能超过50")
    @TableField(value = "mobile", condition = LIKE)
    @Excel(name = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "数据是否加密")
    @TableField("encrypted")
    private Integer encrypted;

}
