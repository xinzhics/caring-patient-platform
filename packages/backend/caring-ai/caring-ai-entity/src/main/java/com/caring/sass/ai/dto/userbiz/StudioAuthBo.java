package com.caring.sass.ai.dto.userbiz;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudioAuthBo {


    @NotNull
    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("客户端认证")
    private String clientToken;

    @ApiModelProperty("token快过期时，刷新token")
    private String refreshToken;



}
