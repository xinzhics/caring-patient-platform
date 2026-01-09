package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ChatGroupSendMemberDto
 * @Description
 * @Author yangShuai
 * @Date 2021/9/9 15:01
 * @Version 1.0
 */
@Data
public class ChatGroupSendMemberDto {

    @ApiModelProperty("类型: all, status, diagnosis, group")
    private String type;

    @ApiModelProperty("type为 status, diagnosis, group 时, 需要传对应的 值, ")
    private String typeValue;

    @ApiModelProperty("是否选中 typeValue, 选中表示 全选typeValue, 忽略 selectPersonIds, deletePersonIds ")
    private Boolean valueSelected;

    @ApiModelProperty("选中的患者ID , 分隔")
    private String selectPersonIds;

    @ApiModelProperty("移除的患者ID , 分隔")
    private String deletePersonIds;

}
