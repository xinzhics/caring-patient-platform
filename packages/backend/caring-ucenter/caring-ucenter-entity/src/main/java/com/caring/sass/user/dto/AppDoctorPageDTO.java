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
@ApiModel(value = "AppDoctorPageDTO", description = "app小组下医生分页")
public class AppDoctorPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "小组ID")
    private Long groupId;

    @ApiModelProperty(value = "医助ID")
    private Long nursingId;


    @ApiModelProperty(value = "医生名称")
    private String doctorName;

}
