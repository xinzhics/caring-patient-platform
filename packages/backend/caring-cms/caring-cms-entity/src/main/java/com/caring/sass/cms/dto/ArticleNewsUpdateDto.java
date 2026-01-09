package com.caring.sass.cms.dto;

import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @ClassName ArticlePageDto
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 16:47
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ArticleNewsUpdateDto", description = "保存图文dto")
public class ArticleNewsUpdateDto extends ArticleNewsSaveDto{


    @ApiModelProperty(value = "主键", required = true)
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

}
