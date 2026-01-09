package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ContentCollectUpdateDTO", description = "收藏记录")
public class ContentCollectUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "收藏人id")
    private Long userId;

    @ApiModelProperty(value = "内容id")
    @NotNull(message = "内容id不能为空")
    private Long contentId;

}
