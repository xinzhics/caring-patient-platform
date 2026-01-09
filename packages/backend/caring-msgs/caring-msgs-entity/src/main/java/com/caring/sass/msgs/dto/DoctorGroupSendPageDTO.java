package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: DoctorGroupSendPageDTO
 * @author: 杨帅
 * @date: 2024/1/11
 */
@Data
@ApiModel("医生查询群发")
public class DoctorGroupSendPageDTO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户角色")
    private String userRole;

    @ApiModelProperty("查询的关键字")
    private String searchKeyName;

    @ApiModelProperty("查询的页码 从 1 开始")
    private Integer page;


}
