package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("人脸检测返回结果")
public class FaceV3DeteactDTO {

    private List<FaceDto> faces;

    private String error_message;

    private int face_num;




}
