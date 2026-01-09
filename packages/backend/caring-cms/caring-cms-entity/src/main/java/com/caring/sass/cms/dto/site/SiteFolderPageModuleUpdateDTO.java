package com.caring.sass.cms.dto.site;

import com.caring.sass.cms.constant.SiteImageFillStyle;
import com.caring.sass.cms.constant.SitePageModuleType;
import com.caring.sass.cms.constant.SiteTitlePositionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
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
 * 建站文件夹中的页面表
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
@ApiModel(value = "SiteFolderPageModuleUpdateDTO", description = "建站文件夹中的页面表")
public class SiteFolderPageModuleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 组件类型(文本，文章，视频，图片，轮播图。。。)
     */
    @ApiModelProperty(value = "组件类型(文本，文章，视频，图片，轮播图。。。)")
    @Length(max = 200, message = "组件类型(文本，文章，视频，图片，轮播图。。。)长度不能超过200")
    private SitePageModuleType moduleType;
    /**
     * 是否为模板(0 不是， 1 是)
     */
    @ApiModelProperty(value = "是否为模板(0 不是， 1 是)")
    private Integer template;
    /**
     * 组件类型的汉语名称
     */
    @ApiModelProperty(value = "组件类型的汉语名称")
    @Length(max = 50, message = "组件类型的汉语名称长度不能超过50")
    private String moduleName;
    /**
     * 文件夹的ID
     */
    @ApiModelProperty(value = "文件夹的ID")
    private Long folderId;
    /**
     * 组件所在页面ID
     */
    @ApiModelProperty(value = "组件所在页面ID")
    private Long folderPageId;
    /**
     * 组件拥有的css类名数组对象
     */
    @ApiModelProperty(value = "组件拥有的css类名数组对象")
    @Length(max = 500, message = "组件拥有的css类名数组对象长度不能超过500")
    private String className;
    /**
     * 组件样式对象
     * {@link com.caring.sass.cms.dto.site.SiteJsonStyleDTO}
     */
    @ApiModelProperty(value = "组件样式对象")
    private String moduleStyle;
    /**
     * 组件的h5内容(文本组件内容，文章主标题内容)
     */
    @ApiModelProperty(value = "组件的h5内容(文本组件内容，文章主标题内容)")
    private String moduleHtmlContent;
    /**
     * 单图片组件的url
     */
    @ApiModelProperty(value = "单图片组件的url")
    @Length(max = 300, message = "单图片组件的url长度不能超过300")
    private String moduleImageUrl;
    /**
     * 轮播方式 （默认，自动，手动）
     */
    @ApiModelProperty(value = "轮播方式 （默认，自动，手动）")
    @Length(max = 50, message = "轮播方式 （默认，自动，手动）长度不能超过50")
    private String carouselWay;
    /**
     * 展示时间
     */
    @ApiModelProperty(value = "展示时间")
    private Float carouselShowTime;
    /**
     * 轮播的切换时间
     */
    @ApiModelProperty(value = "轮播的切换时间")
    private Float carouselSwitchTime;


    @ApiModelProperty(value = "图片间距")
    private Integer carouselImageSpacing;

    @ApiModelProperty(value = "图片缩放方式")
    private SiteImageFillStyle carouselImageFillStyle;
    /**
     * 组件主标题显示状态（0不显示， 1显示）
     */
    @ApiModelProperty(value = "组件主标题显示状态（0不显示， 1显示）")
    private Integer moduleTitleShowStatus;
    /**
     * 组件主标题显示位置(left, center)
     */
    @ApiModelProperty(value = "组件主标题显示位置(left, center)")
    @Length(max = 30, message = "组件主标题显示位置(left, center)长度不能超过30")
    private SiteTitlePositionEnum moduleTitleShowPosition;
    /**
     * 控制显示（摘要，时间，收藏，浏览量，留言）存储json对象
     */
    @ApiModelProperty(value = "控制显示 [\"ABSTRACT\",\"TIME\",\"COLLECT\",\"PAGE_VIEW\",\"COMMENT\"]")
    @Length(max = 200, message = "控制显示（摘要，时间，收藏，浏览量，留言）存储json对象长度不能超过200")
    private String moduleShowContent;
}
