package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel(value = "UpdateTagForm", description = "接口：更新标签的参数表单")
public class UpdateTagForm extends GeneralForm {

    @ApiModelProperty(value = "要修改的标签的TagId", name = "tagId", dataType = "Long", allowEmptyValue = false)
    Long tagId;

    @ApiModelProperty(value = "需要更新的标签的标签名", name = "tagName", dataType = "String", allowEmptyValue = false)
    String tagName;

}