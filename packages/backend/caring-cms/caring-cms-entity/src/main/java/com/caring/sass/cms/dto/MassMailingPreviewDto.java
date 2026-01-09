package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @ClassName MassMailingPreviewDto
 * @Description
 * @Author yangShuai
 * @Date 2021/11/19 14:15
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "MassMailingUpdateDto", description = "预览")
public class MassMailingPreviewDto extends MassMailingBaseDto {

    @ApiModelProperty(value = "不传Id则必须传里面的内容", required = false)
    private Long id;

    @ApiModelProperty(value = "微信号", required = true)
    @NotNull
    private String weiXinName;


}
