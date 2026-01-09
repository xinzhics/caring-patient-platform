package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "PushTemplateUpdateDTO", description = "快捷修改在线咨询，预约，病例讨论的微信模板ID")
@AllArgsConstructor
public class PushTemplateUpdateDTO {

    @ApiModelProperty("模板关键字")
    private String templateMessageIndefiner;

    @ApiModelProperty("微信模板ID")
    private String weiXinTemplateId;

    @ApiModelProperty("sass中的模板ID")
    private Long caringTemplateId;

    @NotEmpty
    private String tenantCode;


}
