package com.caring.sass.ai.dto.card;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("科普名片数字人自我介绍参数")
@Data
public class DifyHumanVideoDto {


    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "医院")
    private String hospital;

    @ApiModelProperty(value = "科室")
    private String department;

    @ApiModelProperty(value = "职称")
    private String title;

    @ApiModelProperty(value = "擅长")
    private String expertise;

    @ApiModelProperty(value = "个人简介")
    private String bio;

}


