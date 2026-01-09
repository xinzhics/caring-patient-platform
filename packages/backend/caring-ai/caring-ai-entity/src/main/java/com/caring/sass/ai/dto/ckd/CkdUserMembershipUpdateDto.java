package com.caring.sass.ai.dto.ckd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CkdUserMembershipUpdateDto", description = "设置CKD用户会员")
public class CkdUserMembershipUpdateDto {


    @ApiModelProperty("CKD用户信息ID")
    private Long ckdUserId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("会员等级")
    private CkdMembershipLevel membershipLevel;

    @ApiModelProperty("会员到期时间")
    private LocalDateTime expirationDate;

}
