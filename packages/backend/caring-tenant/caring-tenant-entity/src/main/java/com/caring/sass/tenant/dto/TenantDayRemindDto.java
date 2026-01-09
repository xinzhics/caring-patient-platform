package com.caring.sass.tenant.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: TenantDayRemindDto
 * @author: 杨帅
 * @date: 2023/5/11
 */
@Data
@ApiModel
public class TenantDayRemindDto {

    @ApiModelProperty("剩余天数 -1 不需要提醒， 0：系统已到期，请及时续费")
    private Integer daysRemaining;

    @ApiModelProperty("1： 显示曾经理的信息， 0： 显示联系人姓名")
    private Integer showCaringAdminContact;

    @ApiModelProperty("联系人姓名")
    private String contactsName;

    @ApiModelProperty("联系人电话")
    private String contactsPhone;


}
