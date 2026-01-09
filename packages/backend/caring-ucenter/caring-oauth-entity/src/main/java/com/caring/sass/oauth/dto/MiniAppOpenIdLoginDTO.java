package com.caring.sass.oauth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @className: MiniAppLoginDTO
 * @author: 杨帅
 * @date: 2024/3/26
 */
@Data
@ApiModel("小程序获取sass的token授权")
public class MiniAppOpenIdLoginDTO {


    /**
     * appId 可以知道是哪个项目的
     */
    @ApiModelProperty("小程序的appId")
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    @NotNull
    @NotEmpty
    private String appId;


    /**
     * 小程序的openId
     *
     */
    @ApiModelProperty("小程序用户的openId")
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    @NotNull
    @NotEmpty
    private String openId;


    @ApiModelProperty("小程序登录后的秘钥")
    private String sessionKey;



}
