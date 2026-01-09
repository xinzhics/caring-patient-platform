package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @ClassName MassMailingTimingDto
 * @Description
 * @Author yangShuai
 * @Date 2021/12/1 14:36
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "MassMailingTimingDto", description = "定时发送的dto")
public class MassMailingTimingDto {

    /**
     * 定时发送的时间
     */
    @ApiModelProperty(value = "定时发送的时间")
    @NotNull
    private LocalDateTime timingSendTime;

    /**
     *
     */
    @ApiModelProperty(value = "id")
    @NotNull
    private Long id;


}
