package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @ClassName Article
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 15:57
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_article_news")
@ApiModel(value = "ArticleNews", description = "图文素材")
@AllArgsConstructor
public class ArticleNews extends Entity<Long> {


    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "标题")
    @Length(max = 200, message = "标题长度不能超过200")
    @TableField(value = "title", condition = LIKE)
    @Excel(name = "标题")
    private String title;

    /**
     * 封面图片的url
     */
    @ApiModelProperty(value = "封面图片的url")
    @TableField(value = "thumb_media_url")
    private String thumbMediaUrl;

    /**
     * 图文消息的封面url.
     */
    @ApiModelProperty(value = "封面图片在微信的url")
    @TableField(value = "thumb_url")
    private String thumbUrl;

    /**
     * 封面图片的素材id
     * url更新时，需要更新删除此id的素材后，再更新
     */
    @ApiModelProperty(value = "封面图片素材Id")
    @TableField(value = "thumb_media_id")
    private String thumbMediaId;

    @ApiModelProperty(value = "作者")
    @Length(max = 50, message = "作者长度不能超过200")
    @TableField(value = "author")
    @Excel(name = "作者")
    private String author;

    @ApiModelProperty(value = "图文消息的摘要")
    @Length(max = 280, message = "图文消息的摘要长度不能超过280")
    @TableField(value = "digest", condition = LIKE)
    @Excel(name = "图文消息的摘要")
    private String digest;

    @ApiModelProperty(value = "是否显示封面")
    @TableField(value = "show_cover_pic")
    @Excel(name = "是否显示封面")
    private Boolean showCoverPic;

    @ApiModelProperty(value = "文章")
    @TableField(value = "content")
    @Excel(name = "文章")
    private String content;

    @ApiModelProperty(value = "原文链接")
    @TableField(value = "content_source_url")
    private String contentSourceUrl;

    @ApiModelProperty(value = "是否打开评论")
    @TableField(value = "need_open_comment")
    @Excel(name = "是否打开评论")
    private Boolean needOpenComment;

    @ApiModelProperty(value = "是否粉丝才可评论")
    @TableField(value = "only_fans_can_comment")
    @Excel(name = "是否粉丝才可评论")
    private Boolean onlyFansCanComment;

    @ApiModelProperty(value = "文章内容中url和微信url")
    @TableField(value = "file_url_map")
    private String fileUrlMap;


    /**
     * 用户声明文章的状态
     */
    @ApiModelProperty(value = "用户声明文章的状态")
    @TableField(value = "user_declare_state")
    private String userDeclareState;

    /**
     * 相似原创文的url
     */
    @ApiModelProperty(value = "相似原创文的url")
    @TableField(value = "original_article_url")
    private String originalArticleUrl;

    /**
     * 是否需要替换成原创文内容
     */
    @ApiModelProperty(value = "是否需要替换成原创文内容")
    @TableField(value = "need_replace_content")
    private String needReplaceContent;

    /**
     * 是否需要注明转载来源
     */
    @ApiModelProperty(value = "是否需要注明转载来源")
    @TableField(value = "need_show_reprint_source")
    private String needShowReprintSource;

    /**
     * 相似原创文的类型
     */
    @ApiModelProperty(value = "相似原创文的类型")
    @TableField(value = "original_article_type")
    private String originalArticleType;

    /**
     * 是否能转载
     */
    @ApiModelProperty(value = "是否能转载")
    @TableField(value = "can_reprint")
    private String canReprint;

    /**
     * 系统校验的状态
     */
    @ApiModelProperty(value = "系统校验的状态")
    @TableField(value = "audit_state")
    private String auditState;


}
