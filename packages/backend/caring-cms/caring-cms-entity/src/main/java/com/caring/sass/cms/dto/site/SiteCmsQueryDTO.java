package com.caring.sass.cms.dto.site;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @className: SiteCmsQueryDTO
 * @author: 杨帅
 * @date: 2023/9/11
 */
@Data
@ApiModel("建站查询文章的列表")
public class SiteCmsQueryDTO {


    @ApiModelProperty("文章的标题")
    private String title;

    @ApiModelProperty("文章的分类ID")
    private Long groupId;

    @ApiModelProperty("想过滤的文章ID")
    private List<Long> filterCmsIds;

}
