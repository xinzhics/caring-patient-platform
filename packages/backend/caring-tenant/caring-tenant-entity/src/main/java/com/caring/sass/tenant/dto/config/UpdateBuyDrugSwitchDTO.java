package com.caring.sass.tenant.dto.config;

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
@ApiModel(value = "UpdateDrugSwitchDTO", description = "修改用药开关参数")
public class UpdateBuyDrugSwitchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否开启购药跳转url：0开启，1不开启
     */
    @ApiModelProperty(value = "是否开启购药跳转url：0开启，1不开启")
    private Integer buyDrugsUrlSwitch;

    /**
     * 购药跳转url
     */
    @ApiModelProperty(value = "购药跳转url")
    private String buyDrugsUrl;
}
