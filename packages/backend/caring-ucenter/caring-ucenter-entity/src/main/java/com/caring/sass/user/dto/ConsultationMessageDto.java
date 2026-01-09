package com.caring.sass.user.dto;

import com.caring.sass.common.constant.MediaType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @ClassName ConsultationMessageDto
 * @Description 群组消息发送体
 * @Author yangShuai
 * @Date 2021/6/4 10:32
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class ConsultationMessageDto {

    @ApiModelProperty(value = "消息内容", name = "type", dataType = "String")
    private String content;

    @ApiModelProperty(value = "MediaType类型", name = "type", dataType = "String")
    private MediaType type;

}
