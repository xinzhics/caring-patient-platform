package com.caring.sass.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @ClassName UserBizInfo
 * @Description 查询用户信息
 * @Author yangShuai
 * @Date 2022/4/13 9:46
 * @Version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Data
public class UserBizInfo {

    private Long userId;

    @ApiModelProperty("用户的角色，患者，医生，医助")
    private String userType;

    private String name;


}
