package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RecommendUrlVo", description = "推荐患者-医生列表-推荐页链接地址")
public class RecommendUrlVo {
    /**
     * 推荐页链接地址
     */
    @ApiModelProperty(value = "推荐页链接地址")
    private String recommendUrl;
}
