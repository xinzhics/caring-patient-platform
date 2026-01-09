package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.cms.constant.SiteJumpTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 建站组件元素跳转信息
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-05
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_build_site_jump_information")
@ApiModel(value = "SiteJumpInformation", description = "建站组件元素跳转信息")
@AllArgsConstructor
public class SiteJumpInformation extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件夹ID
     */
    @ApiModelProperty(value = "文件夹ID")
    @TableField("folder_id")
    @Excel(name = "文件夹ID")
    private Long folderId;

    /**
     * 页面ID
     */
    @ApiModelProperty(value = "页面ID")
    @TableField("page_id")
    @Excel(name = "页面ID")
    private Long pageId;

    /**
     * 组件id
     */
    @ApiModelProperty(value = "组件id")
    @TableField("module_id")
    @Excel(name = "组件id")
    private Long moduleId;

    /**
     * 板块ID
     */
    @ApiModelProperty(value = "板块ID")
    @TableField("module_plate_id")
    @Excel(name = "板块ID")
    private Long modulePlateId;

    /**
     * 跳转方式
     */
    @ApiModelProperty(value = "跳转方式")
    @Length(max = 200, message = "跳转方式长度不能超过200")
    @TableField(value = "jump_type", condition = LIKE)
    @Excel(name = "跳转方式")
    private SiteJumpTypeEnum jumpType;

    /**
     * 跳转文件夹内页面ID
     */
    @ApiModelProperty(value = "跳转文件夹内页面ID")
    @TableField("jump_page_id")
    @Excel(name = "跳转文件夹内页面ID")
    private Long jumpPageId;

    /**
     * 跳转小程序ID
     */
    @ApiModelProperty(value = "跳转小程序ID")
    @Length(max = 100, message = "跳转小程序ID长度不能超过100")
    @TableField(value = "jump_app_id", condition = LIKE)
    @Excel(name = "跳转小程序ID")
    private String jumpAppId;

    /**
     * 跳转小程序页面的路径
     */
    @ApiModelProperty(value = "跳转小程序页面的路径")
    @Length(max = 300, message = "跳转小程序页面的路径长度不能超过300")
    @TableField(value = "jump_app_page_url", condition = LIKE)
    @Excel(name = "跳转小程序页面的路径")
    private String jumpAppPageUrl;

    /**
     * 跳转外链网址
     */
    @ApiModelProperty(value = "跳转外链网址")
    @Length(max = 65535, message = "跳转外链网址长度不能超过65535")
    @TableField("jump_website")
    @Excel(name = "跳转外链网址")
    private String jumpWebsite;

    /**
     * 跳转文章的ID
     */
    @ApiModelProperty(value = "跳转文章的ID")
    @TableField("jump_cms_id")
    @Excel(name = "跳转文章的ID")
    private Long jumpCmsId;

    @ApiModelProperty(value = "跳转视频ID 本地视频时传")
    @TableField(value = "jump_video_id", condition = EQUAL)
    private String jumpVideoId;

    /**
     * 跳转视频url
     */
    @ApiModelProperty(value = "跳转视频url")
    @Length(max = 500, message = "跳转视频url长度不能超过500")
    @TableField(value = "jump_video_url", condition = LIKE)
    @Excel(name = "跳转视频url")
    private String jumpVideoUrl;

    /**
     * 跳转视频名称
     */
    @ApiModelProperty(value = "跳转视频文章名称")
    @Length(max = 30, message = "跳转视频名称长度不能超过30")
    @TableField(value = "jump_video_title", condition = LIKE)
    @Excel(name = "跳转视频名称")
    private String jumpVideoTitle;

    /**
     * 跳转视频封面
     */
    @ApiModelProperty(value = "跳转视频封面")
    @Length(max = 300, message = "跳转视频封面长度不能超过300")
    @TableField(value = "jump_video_cover", condition = LIKE)
    @Excel(name = "跳转视频封面")
    private String jumpVideoCover;

    /**
     * 本地视频，视频地址
     */
    @ApiModelProperty(value = "本地视频 local_video，视频地址 video_url")
    @Length(max = 50, message = "本地视频，视频地址长度不能超过50")
    @TableField(value = "jump_video_source", condition = EQUAL)
    @Excel(name = "本地视频，视频地址")
    private String jumpVideoSource;




}
