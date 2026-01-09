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
public class MiniAppPhoneLoginDTO extends MiniAppOpenIdLoginDTO {


    /**
     * 手机号可以用来查询是否是系统项目医生和他的关注项目公众号的状态。
     */
    @ApiModelProperty("小程序的提交的手机号")
    @Pattern(regexp = "^[^#\\*%,';]+$", message = "参数异常")
    @NotNull
    @NotEmpty
    private String phoneNumber;


    @ApiModelProperty("知识库主任ID")
    private Long knowUserId;

    @ApiModelProperty("点记小程序必传客户端类型 知识库： know ; ")
    private String clientType;


}
