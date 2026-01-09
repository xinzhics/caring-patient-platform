package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName ConsultationGroupAcceptOrReject
 * @Description
 * @Author yangShuai
 * @Date 2022/5/12 11:10
 * @Version 1.0
 */
@Data
@ApiModel(value = "ConsultationGroupAcceptOrReject", description = "接受或拒绝加入讨论组")
public class ConsultationGroupAcceptOrReject {

    /**
     * 接受
     */
    public static String ACCEPT = "accept";


    /**
     * 拒绝
     */
    public static String REJECT = "reject";

    @ApiModelProperty("小组ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty("医生的IM账号")
    @NotEmpty
    private String imAccount;


    @ApiModelProperty("操作： accept 接受，reject 拒绝")
    @NotEmpty
    private String action;

    @ApiModelProperty("操作：reject 拒绝原因")
    private String rejectMessage;


}
