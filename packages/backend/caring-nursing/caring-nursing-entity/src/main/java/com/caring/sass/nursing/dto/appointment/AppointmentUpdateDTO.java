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
import com.caring.sass.base.entity.SuperEntity;
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
@ApiModel(value = "AppointmentUpdateDTO", description = "患者预约表")
public class AppointmentUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 机构id
     */
    @ApiModelProperty(value = "机构id")
    private Long organizationId;
    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id")
    private Long patientId;
    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    @Length(max = 100, message = "患者名称长度不能超过100")
    private String patientName;
    /**
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期")
    private LocalDate appointDate;
    /**
     * 就诊状态  未就诊：0  已就诊：1
     */
    @ApiModelProperty(value = "就诊状态  未就诊：0  已就诊：1")
    private Integer status;
    /**
     * 1 上午  2：下午
     */
    @ApiModelProperty(value = "1 上午  2：下午")
    private Integer time;
    /**
     * 医生id
     */
    @ApiModelProperty(value = "医生id")
    private Long doctorId;
    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    @Length(max = 255, message = "医生名称长度不能超过255")
    private String doctorName;
}
