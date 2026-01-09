package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @className: PatientMessageCountDto
 * @author: 杨帅
 * @date: 2023/8/25
 */
@Data
public class PatientMessageCountDto {

    @ApiModelProperty("系统未读消息数量")
    private int systemMessageCount;

    @ApiModelProperty("在线咨询未读消息")
    private int imMessageCount;

    @ApiModelProperty("总未读消息数量")
    private int allMessageCount;

    @ApiModelProperty("系统未读消息，咨询消息分类统计")
    private List<PatientMessageGroupListDTO> messageList;

}
