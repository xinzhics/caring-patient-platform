package com.caring.sass.ai.dto.ckd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CkdFreeTimeDto", description = "用户粉丝免费时长")
public class CkdFreeTimeDto {

    @ApiModelProperty("当前用户ID")
    @NotNull
    private Long userId;


    @ApiModelProperty("粉丝拥有的免费天数")
    @NonNull
    private Integer freeDay;

}
