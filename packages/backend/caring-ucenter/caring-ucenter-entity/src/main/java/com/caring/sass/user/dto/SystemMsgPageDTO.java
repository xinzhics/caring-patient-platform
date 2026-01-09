package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SystemMsgPageDTO", description = "系统消息")
public class SystemMsgPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "专员Id")
    private Long userId;

    @ApiModelProperty(value = "角色默认专员, 医生 doctor")
    String userRole;


}
