package com.caring.sass.wx.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DownloadMatrialForm", description = "接口：下载素材的参数表单")
public class DownloadMatrialForm extends GeneralForm {

    @ApiModelProperty(value = "素材id", name = "mediaId", dataType = "String", allowEmptyValue = false)
    String mediaId;

}