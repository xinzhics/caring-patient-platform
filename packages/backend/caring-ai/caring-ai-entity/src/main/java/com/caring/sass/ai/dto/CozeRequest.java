package com.caring.sass.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "扣子接口的请求参数")
public class CozeRequest {

    @NotEmpty
    @NotNull
    @ApiModelProperty(value = "扣子接口的请求参数")
    private String query;

    @NotEmpty
    @NotNull
    @ApiModelProperty(value = "用户")
    private String user;


    @NotEmpty
    @NotNull
    @ApiModelProperty(value = "bot")
    private String bot_id;

}
