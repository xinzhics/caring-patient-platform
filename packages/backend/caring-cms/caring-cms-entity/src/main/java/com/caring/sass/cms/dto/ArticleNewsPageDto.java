package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

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
@Builder
@ApiModel(value = "ArticleNewsPageDto", description = "图文分页dto")
public class ArticleNewsPageDto {



    @ApiModelProperty(value = "标题")
    @Length(max = 200, message = "标题长度不能超过200")
    private String title;

}
