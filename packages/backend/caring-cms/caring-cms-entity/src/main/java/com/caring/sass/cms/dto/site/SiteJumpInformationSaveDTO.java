package com.caring.sass.cms.dto.site;

import com.caring.sass.cms.constant.SiteJumpTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SiteJumpInformationSaveDTO", description = "建站组件元素跳转信息")
public class SiteJumpInformationSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件夹ID
     */
    @ApiModelProperty(value = "文件夹ID")
    private Long folderId;
    /**
     * 页面ID
     */
    @ApiModelProperty(value = "页面ID")
    private Long pageId;
    /**
     * 组件id
     */
    @ApiModelProperty(value = "组件id")
    private Long moduleId;
    /**
     * 板块ID
     */
    @ApiModelProperty(value = "板块ID")
    private Long modulePlateId;
    /**
     * 跳转方式
     */
    @ApiModelProperty(value = "跳转方式")
    @Length(max = 200, message = "跳转方式长度不能超过200")
    private SiteJumpTypeEnum jumpType;
    /**
     * 跳转文件夹内页面ID
     */
    @ApiModelProperty(value = "跳转文件夹内页面ID")
    private Long jumpPageId;
    /**
     * 跳转小程序ID
     */
    @ApiModelProperty(value = "跳转小程序ID")
    @Length(max = 100, message = "跳转小程序ID长度不能超过100")
    private String jumpAppId;
    /**
     * 跳转小程序页面的路径
     */
    @ApiModelProperty(value = "跳转小程序页面的路径")
    @Length(max = 300, message = "跳转小程序页面的路径长度不能超过300")
    private String jumpAppPageUrl;
    /**
     * 跳转外链网址
     */
    @ApiModelProperty(value = "跳转外链网址")
    @Length(max = 65535, message = "跳转外链网址长度不能超过65,535")
    private String jumpWebsite;
    /**
     * 跳转文章的ID
     */
    @ApiModelProperty(value = "跳转文章的ID")
    private Long jumpCmsId;
    /**
     * 跳转视频url
     */
    @ApiModelProperty(value = "跳转视频url")
    @Length(max = 500, message = "跳转视频url长度不能超过500")
    private String jumpVideoUrl;
    /**
     * 跳转视频名称
     */
    @ApiModelProperty(value = "跳转视频名称")
    @Length(max = 30, message = "跳转视频名称长度不能超过30")
    private String jumpVideoTitle;
    /**
     * 跳转视频封面
     */
    @ApiModelProperty(value = "跳转视频封面")
    @Length(max = 300, message = "跳转视频封面长度不能超过300")
    private String jumpVideoCover;
    /**
     * 本地视频，视频地址
     */
    @ApiModelProperty(value = "本地视频 local_video，视频地址 video_url")
    @Length(max = 50, message = "本地视频，视频地址长度不能超过50")
    private String jumpVideoSource;

}
