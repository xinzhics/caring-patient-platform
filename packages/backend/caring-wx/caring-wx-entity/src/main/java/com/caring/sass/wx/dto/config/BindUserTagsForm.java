package com.caring.sass.wx.dto.config;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "BindUserTagsForm", description = "绑定用户标签的数据DTO")
public class BindUserTagsForm extends GeneralForm {
    @ApiModelProperty(value = "多个用户的OpenId以英文逗号 , 分隔", name = "openIds", dataType = "String", example = "aaa,bbbb", required = true, allowEmptyValue = false)
    String openIds;

    @ApiModelProperty(value = "标签Id", name = "tagId", dataType = "String", example = "aaaa", allowEmptyValue = false, required = true)
    String tagId;

    @ApiModelProperty(value = "清除标签Id", name = "tagId", dataType = "String", example = "aaaa", allowEmptyValue = false, required = true)
    List<String> clearTagId;
}