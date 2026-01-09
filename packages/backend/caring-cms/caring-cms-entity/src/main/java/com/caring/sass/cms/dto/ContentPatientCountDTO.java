package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: ContentPatientCountDTO
 * @author: 杨帅
 * @date: 2023/8/28
 */
@Data
public class ContentPatientCountDTO {

    @ApiModelProperty("收藏数量")
    private int collectCount;

    @ApiModelProperty("评论文章数量")
    private int replyContentCount;

}
