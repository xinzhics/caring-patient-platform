package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "人脸融合结果")
public class FaceMergeResultDTO {

    /**
     * 融合后的图片，jpg 格式。base64 编码的二进制图片数据。
     */
    private String result;

    /**
     * 当发生错误时才返回。
     */
    private String error_message;
}
