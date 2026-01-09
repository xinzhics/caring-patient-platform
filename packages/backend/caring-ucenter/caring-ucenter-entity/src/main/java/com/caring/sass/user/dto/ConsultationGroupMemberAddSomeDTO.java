package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ConsultationGroupMemberAddSomeDTO
 * @Description
 * @Author yangShuai
 * @Date 2022/5/10 15:41
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ConsultationGroupMemberAddSomeDTO", description = "批量添加人员进入讨论组")
public class ConsultationGroupMemberAddSomeDTO {

    /**
     * 讨论组ID
     */
    @ApiModelProperty("讨论组的ID")
    @NotNull
    private Long groupId;

    /**
     * 批量添加进来的医生的openID
     */
    @ApiModelProperty("要批量邀请的医生的openId")
    private List<String> doctorOpenId;

    @ApiModelProperty("要批量邀请的医生的手机号")
    private Set<String> doctorMobileId;

    /**
     * 医助ID
     */
    @ApiModelProperty("医生邀请自己的医助时， 传医助的IM账号")
    private String nursingIMAccount;

    @ApiModelProperty(value = "邀请人ID")
    private Long invitePeople;

    @ApiModelProperty(value = "邀请人角色 医助 NursingStaff 或 医生 doctor")
    private String invitePeopleRole;

}
