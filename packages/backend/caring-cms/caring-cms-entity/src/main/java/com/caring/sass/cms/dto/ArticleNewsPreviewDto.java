package com.caring.sass.cms.dto;

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
@ApiModel(value = "ArticleNewsPreviewDto", description = "图文预览dto")
public class ArticleNewsPreviewDto extends ArticleNewsSaveDto{


    @ApiModelProperty(value = "主键(有就传，没有不传)")
    private Long id;

    @ApiModelProperty(value = "微信号", required = true)
    @NotNull
    private String weiXinName;
}
