package com.caring.sass.wx.dto.config;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PublishMenuForm", description = "发布菜单接口参数表单")
public class PublishMenuForm extends GeneralForm {

    @ApiModelProperty(value = "菜单json数据")
    JSONArray menuJson;

}