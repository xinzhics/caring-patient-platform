package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "FunctionConfigurationPushSettingsDTO", description = "推送配置列表")
@AllArgsConstructor
public class FunctionConfigurationPushSettingsDTO {

    @ApiModelProperty("模板关键字")
    private String templateMessageIndefiner;

    @ApiModelProperty("推送配置的名称")
    private String name;

    @ApiModelProperty("微信模板ID")
    private String weiXinTemplateId;

    @ApiModelProperty("sass中的模板ID")
    private Long caringTemplateId;

}
