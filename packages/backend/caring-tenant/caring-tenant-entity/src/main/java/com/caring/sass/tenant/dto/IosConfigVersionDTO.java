package com.caring.sass.tenant.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: IosConfigVersionDTO
 * @author: 杨帅
 * @date: 2023/7/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("ios的版本号")
public class IosConfigVersionDTO {

    @ApiModelProperty("版本号")
    private String appVersionName;

    @ApiModelProperty("更新记录")
    private String renewRecord;

}
