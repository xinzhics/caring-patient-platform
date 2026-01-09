package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 医生的自定义小组患者
 * </p>
 *
 * @author yangshuai
 * @since 2022-08-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "NursingCustomGroupPatientPageDTO", description = "医助的自定义小组患者")
public class NursingCustomGroupPatientPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自定义小组ID
     */
    @ApiModelProperty(value = "自定义小组ID, 不传 则不会设置选中")
    private Long customGroupId;

    @NotNull
    @ApiModelProperty(value = "医助ID")
    private Long nursingId;
    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    private String patientName;

}
