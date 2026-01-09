package com.caring.sass.nursing.dto.appointment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 患者预约表
 * </p>
 *
 * @author leizhi
 * @since 2021-01-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "AppointmentSaveDTO", description = "患者预约表")
public class AppointmentSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 患者id
     */
    @NotNull(message = "患者id不能为空")
    @ApiModelProperty(value = "患者id")
    private Long patientId;

    /**
     * 预约日期
     */
    @NotNull(message = "预约日期不能为空")
    @ApiModelProperty(value = "预约日期")
    private LocalDate appointDate;

    /**
     * 1 上午  2：下午
     */
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "1 上午  2：下午")
    private Integer time;

    /**
     * 医生id
     */
    @NotNull(message = "医生id不能为空")
    @ApiModelProperty(value = "医生id")
    private Long doctorId;
}
