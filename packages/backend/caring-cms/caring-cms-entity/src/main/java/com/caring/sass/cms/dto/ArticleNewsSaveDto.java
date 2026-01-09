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
@ApiModel(value = "ArticleNewsSaveDto", description = "保存图文dto")
public class ArticleNewsSaveDto {

    @ApiModelProperty(value = "标题")
    @Length(max = 200, message = "标题长度不能超过200")
    private String title;
    /**
     * 封面缩略图图片的url
     */
    @ApiModelProperty(value = "封面图片的url")
    private String thumbMediaUrl;

    @ApiModelProperty(value = "作者")
    @Length(max = 50, message = "作者长度不能超过200")
    private String author;

    @ApiModelProperty(value = "图文消息的摘要")
    @Length(max = 280, message = "图文消息的摘要长度不能超过280")
    private String digest;

    @ApiModelProperty(value = "是否显示封面")
    private Boolean showCoverPic;

    @ApiModelProperty(value = "原文链接")
    private String contentSourceUrl;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "是否打开评论")
    private Boolean needOpenComment;

    @ApiModelProperty(value = "是否粉丝才可评论")
    private Boolean onlyFansCanComment;

}
