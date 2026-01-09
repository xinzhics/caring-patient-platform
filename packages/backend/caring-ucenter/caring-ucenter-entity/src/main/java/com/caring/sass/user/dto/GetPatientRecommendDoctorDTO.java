package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "GetPatientRecommendDoctorDTO", description = "超管端-患者推荐-查询医生")
public class GetPatientRecommendDoctorDTO {
    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码",required = true)
    @NotBlank(message = "租户编码不能为空")
    private String tenantCode;

    /**
     * 模糊搜索医生名称
     */
    @ApiModelProperty(value = "模糊搜索医生名称")
    private String doctorName;

    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小", example = "5")
    @NotNull(message = "页面大小不能为空")
    private long size = 5;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", example = "1")
    @NotNull(message = "当前页不能为空")
    private long current = 1;
}
