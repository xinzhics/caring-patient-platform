package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("人脸检测的面部信息")
public class FaceDto {

    @ApiModelProperty("人脸的token")
    private String face_token;

    @ApiModelProperty("人脸的矩形信息")
    private faceRectangleDTO face_rectangle;

}
