package com.caring.sass.wx.dto.config;

import com.caring.sass.wx.constant.WeiXinConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UploadTemporaryMatrialForm", description = "接口：上传临时素材的参数表单数据")
public class UploadTemporaryMatrialForm extends GeneralForm {

    @ApiModelProperty(value = "媒体的URl", name = "url", dataType = "String", example = "http://.....", allowEmptyValue = false)
    String url;

    @ApiModelProperty(value = "媒体的本地路径", name = "path", dataType = "String", example = "d:/abc.mpeg", allowEmptyValue = false)
    String path;

    @ApiModelProperty(value = "文件类型", name = "fileType", dataType = "String", example = "mpeg", allowEmptyValue = false)
    WeiXinConstants.FileType fileType;

    @ApiModelProperty(value = "媒体的类型", name = "mediaType", dataType = "String", example = "mpeg", allowEmptyValue = false)
    WeiXinConstants.MediaType mediaType;

    @ApiModelProperty(value = "文件名，上传时刻自定义", name = "fileName", dataType = "String", allowEmptyValue = false)
    String fileName;

    /**
     * 是否异步
     */
    private Boolean asynchronous;
    /**
     * redis 队列回调的key值
     */
    private String redisCallBackKey;
    /**
     * redis 队列回调的value值
     */
    private String redisCallBackValue;

    private String name;
    private String videoTitle;
    private String videoIntroduction;
}