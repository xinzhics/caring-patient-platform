package com.caring.sass.cms.dto;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @ClassName MassMailingUpdateDto
 * @Description
 * @Author yangShuai
 * @Date 2021/11/19 14:14
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "MassMailingUpdateDto", description = "更新群发消息")
public class MassMailingUpdateDto extends MassMailingSaveDTO {


    @ApiModelProperty(value = "主键", required = true)
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;


}
