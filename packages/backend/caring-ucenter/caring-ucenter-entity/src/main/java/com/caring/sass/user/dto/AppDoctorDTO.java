package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "AppDoctorDTO", description = "app的医生查询条件")
public class AppDoctorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "独立医生 1 为独立医生， 0 为非独立医生")
    private Integer independence;

    @ApiModelProperty(value = "医助ID")
    private Long nursingId;

    @ApiModelProperty(value = "小组ID")
    private Long groupId;

    @ApiModelProperty(value = "医生的名称")
    private String doctorName;

}
