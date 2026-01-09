package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GetFollowerInfoForm", description = "接口：getFollowerInfo的参数表单")
public class GetFollowerInfoForm extends GeneralForm {
    @ApiModelProperty(value = "用户的openId", name = "userOpenId", dataType = "String", allowEmptyValue = false)
    String userOpenId;
}