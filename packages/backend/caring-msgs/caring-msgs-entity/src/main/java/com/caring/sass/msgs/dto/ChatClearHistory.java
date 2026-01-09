package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ChatClearHistory
 * @Description
 * @Author yangShuai
 * @Date 2021/9/14 17:46
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatClearHistory {

    @ApiModelProperty(value = "所属消息的组ID（saas中是chat对象的 receiverImAccount ）")
    private List<String> groupIds;

    @ApiModelProperty(value = "用户ID", name = "userId", dataType = "Long")
    private Long userId;

    @ApiModelProperty(value = "用户的角色doctor,NursingStaff", name = "roleType", dataType = "String")
    private String roleType;

}
