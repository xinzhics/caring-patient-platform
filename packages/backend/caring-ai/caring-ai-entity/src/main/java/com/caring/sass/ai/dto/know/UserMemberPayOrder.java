package com.caring.sass.ai.dto.know;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@lombok.Data
public class UserMemberPayOrder {

    @ApiModelProperty("uid")
    private String uid;


    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("用户购买会员的类型")
    @NotNull
    private KnowMemberType memberType;


    @ApiModelProperty("购买会员的域名")
    @NotNull
    private String userDomain;


    @ApiModelProperty("openId")
    private String openId;

}
