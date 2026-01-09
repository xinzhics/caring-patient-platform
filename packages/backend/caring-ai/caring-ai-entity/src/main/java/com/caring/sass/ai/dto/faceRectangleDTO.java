package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "人脸检测返回结果")
public class faceRectangleDTO {

    @ApiModelProperty("人脸框宽度")
    private int width;

    @ApiModelProperty("人脸框高度")
    private int top;

    @ApiModelProperty("人脸框左上角纵坐标")
    private int left;

    @ApiModelProperty("人脸框左上角横坐标")
    private int height;


    /**
     * 模板图中进行人脸融合的人脸框位置
     * @return 人脸框位置 top left width height
     */
    public String getFaceRectangleInfo() {
        return top + "," + left + "," + width + "," + height;
    }

}
