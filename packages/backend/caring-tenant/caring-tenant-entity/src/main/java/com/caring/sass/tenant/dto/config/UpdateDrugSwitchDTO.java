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
public class UpdateDrugSwitchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否在入组界面中是否显示推荐用药：0显示，1不显示
     */
    @ApiModelProperty(value = "是否在入组界面中是否显示推荐用药：0显示，1不显示")
    private Integer hasShowRecommendDrugs;

    /**
     * 是否在注册时填写用药信息：0填写，1不填写
     */
    @ApiModelProperty(value = "是否在注册时填写用药信息：0填写，1不填写")
    private Integer hasFillDrugs;
}
