package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @className: DoctorSubscibeKeyWordReply
 * @author: 杨帅
 * @date: 2023/9/15
 */
@Data
@ApiModel
public class DoctorSubscribeKeyWordReply {


    @ApiModelProperty("医生ID")
    @NotNull
    private Long doctorId;

    @ApiModelProperty("医生IM账号")
    @NotEmpty
    private String imAccount;

    @ApiModelProperty("关键词的名字")
    @NotEmpty
    private String keyWord;

}
