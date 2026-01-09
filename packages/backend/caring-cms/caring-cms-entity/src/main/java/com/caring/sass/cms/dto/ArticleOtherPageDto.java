package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

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
@ApiModel(value = "ArticleOtherPageDto", description = "其他素材的分页")
public class ArticleOtherPageDto {

    @ApiModelProperty(value = "素材类型(image,voice,video,thumb)")
    private String materialType;


    @ApiModelProperty(value = "素材标题")
    private String title;

    @ApiModelProperty(value = "视频上传状态  uploading，uploadSuccess， uploadError")
    private String videoUploadWx;

}
