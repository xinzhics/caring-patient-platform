package com.caring.sass.ai.dto.know;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KnowledgeFileCount {


    @ApiModelProperty("专业学术资料")
    private int zhuanYeXueShuCount;


    @ApiModelProperty("个人成果")
    private int geRenChengGuoCount;


    @ApiModelProperty("病例库")
    private int bingLiKuCount;

    @ApiModelProperty("日常收集")
    private int riChangShouJICount;



}
