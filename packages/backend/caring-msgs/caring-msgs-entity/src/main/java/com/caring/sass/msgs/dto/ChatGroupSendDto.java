package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ChatGroupSendDto
 * @Description
 * @Author yangShuai
 * @Date 2021/9/9 14:38
 * @Version 1.0
 */
@Data
public class ChatGroupSendDto {

    @ApiModelProperty(value = "发送者ID", name = "senderId", dataType = "String")
    protected String senderId;

    @ApiModelProperty(value = "接收者", name = "memberDtos", dataType = "List")
    private List<ChatGroupSendMemberDto> memberDtos;

}
