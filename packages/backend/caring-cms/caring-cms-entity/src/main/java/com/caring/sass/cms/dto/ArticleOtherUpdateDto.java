package com.caring.sass.cms.dto;

import com.caring.sass.base.entity.SuperEntity;
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
@ApiModel(value = "ArticleOtherUpdateDto", description = "保存其他素材")
public class ArticleOtherUpdateDto extends ArticleOtherSaveDto {

    @ApiModelProperty(value = "主键", required = true)
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;



}
