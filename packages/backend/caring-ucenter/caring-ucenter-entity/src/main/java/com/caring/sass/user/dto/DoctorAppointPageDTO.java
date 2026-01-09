package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 医生表
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DoctorAppointPageDTO", description = "医生表")
public class DoctorAppointPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    private Long nursingId;
    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    @Length(max = 32, message = "机构ID长度不能超过32")
    private Long organId;

    @ApiModelProperty(value = "患者的医生ID")
    private Long doctorId;



}
