package com.caring.sass.msgs.dto;

import com.caring.sass.common.constant.MediaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "患者聊天记录")
public class ChatPatientPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息类型", name = "type")
    private MediaType type;

    @NotNull
    @ApiModelProperty(value = "患者的imAccount", name = "imAccount", dataType = "String")
    private String imAccount;

    @ApiModelProperty(value = "起始发送时间")
    private LocalDateTime startCreateTime;

    @ApiModelProperty(value = "最终发送时间")
    private LocalDateTime endCreateTime;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "当前用户类型")
    private String currentUserType;

}
