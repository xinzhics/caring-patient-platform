package com.caring.sass.ai.entity.face;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@Data
public class VolcengineParams {

    @ApiModelProperty("人脸照片")
    String faceImage;

    @ApiModelProperty("模版图")
    String templateImage;

    @ApiModelProperty("高清效果,支持[0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]")
    Float gpen;

    @ApiModelProperty("美化效果,支持[0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]")
    Float skin;

}
