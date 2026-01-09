package com.caring.sass.user.dto;

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
@ApiModel(value = "SystemMsgSaveDTO")
public class SystemMsgSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "专员Id")
    private Long userId;

    @ApiModelProperty(value = "常用语")
    @Length(max = 255, message = "常用语长度不能超过20")
    private String content;

}
