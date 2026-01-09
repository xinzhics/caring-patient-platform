package com.caring.sass.cms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
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
@TableName("t_cms_article_other")
@ApiModel(value = "ArticleOther", description = "其他素材")
@AllArgsConstructor
public class ArticleOther extends Entity<Long> {


    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "素材saas的url")
    @TableField(value = "media_saas_url")
    private String mediaSaasUrl;

    @ApiModelProperty(value = "素材标题")
    @Length(max = 30, message = "素材标题不能超过200")
    @TableField(value = "title", condition = LIKE)
    private String title;

    @ApiModelProperty(value = "素材类型(image,voice,video,thumb)")
    @TableField(value = "material_type", condition = EQUAL)
    private String materialType;

    @ApiModelProperty(value = "视频描述")
    @Length(max = 280, message = "视频描述不能超过200")
    @TableField(value = "introduction")
    private String introduction;

    @ApiModelProperty(value = "文件名称")
    @TableField(value = "file_name")
    private String fileName;

    @ApiModelProperty(value = "文件大小")
    @TableField(value = "file_size")
    private String fileSize;

    @ApiModelProperty(value = "素材微信的id")
    @TableField(value = "media_id")
    private String mediaId;

    @ApiModelProperty(value = "素材微信的url")
    @TableField(value = "media_url")
    private String mediaUrl;

    @TableField(exist = false)
    private String tenantCode;

    @ApiModelProperty(value = "视频上传状态  uploading，uploadSuccess， uploadError")
    @TableField(value = "video_upload_wx", condition = EQUAL)
    private String videoUploadWx;

}
