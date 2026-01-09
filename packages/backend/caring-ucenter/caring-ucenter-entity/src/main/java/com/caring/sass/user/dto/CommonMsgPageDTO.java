package com.caring.sass.user.dto;

import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CommonMsgPageDTO", description = "常用语")
public class CommonMsgPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "常用语分类id")
    private Long typeId;

    @ApiModelProperty(value = "用户ID")
    private Long accountId;

    @ApiModelProperty(value = "搜索内容常用语关键词")
    @Length(max = 255, message = "常用语长度不能超过20")
    private String content;

    @ApiModelProperty(value = "用户类型 NursingStaff, doctor")
    private String userType;

    public String getUserType() {
        if (StringUtils.isEmpty(userType)) {
            return UserType.UCENTER_NURSING_STAFF;
        } else {
            return userType;
        }
    }
}
