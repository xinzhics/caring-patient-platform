package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.cms.constant.SiteCarouseWayEnum;
import com.caring.sass.cms.constant.SiteImageFillStyle;
import com.caring.sass.cms.constant.SitePageModuleType;
import com.caring.sass.cms.constant.SiteTitlePositionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_build_site_folder_page_module")
@ApiModel(value = "SiteFolderPageModule", description = "建站页面的组件")
@AllArgsConstructor
public class SiteFolderPageModule extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 组件类型(文本，文章，视频，图片，轮播图。。。)
     */
    @ApiModelProperty(value = "组件类型")
    @Length(max = 200, message = "组件类型长度不能超过200")
    @TableField(value = "module_type", condition = EQUAL)
    private SitePageModuleType moduleType;

    /**
     * 是否为模板(0 不是， 1 是)
     * {@link com.caring.sass.common.constant.CommonStatus}
     */
    @Deprecated
    @ApiModelProperty(value = "是否为模板(0 不是， 1 是)")
    @TableField("template")
    @Excel(name = "是否为模板(0 不是， 1 是)")
    private Integer template;

    /**
     * 组件类型的汉语名称
     */
    @ApiModelProperty(value = "后端保存时，根据moduleType生成")
    @TableField(value = "module_name", condition = EQUAL)
    @Excel(name = "组件类型的汉语名称")
    private String moduleName;

    /**
     * 文件夹的ID
     */
    @ApiModelProperty(value = "文件夹的ID")
    @TableField("folder_id")
    @Excel(name = "文件夹的ID")
    private Long folderId;

    /**
     * 组件所在页面ID
     */
    @ApiModelProperty(value = "组件所在页面ID")
    @TableField("folder_page_id")
    @Excel(name = "组件所在页面ID")
    private Long folderPageId;

    /**
     * 组件拥有的css类名数组对象
     */
    @ApiModelProperty(value = "组件的css类名数组对象 ")
    @Length(max = 500, message = "组件拥有的css类名数组对象长度不能超过500")
    @TableField(value = "class_name", condition = LIKE)
    @Excel(name = "组件拥有的css类名数组对象")
    private String className;

    /**
     * 组件样式对象
     * {@link com.caring.sass.cms.dto.site.SiteJsonStyleDTO}
     */
    @ApiModelProperty(value = "组件样式对象")
    @TableField("module_style")
    @Excel(name = "组件样式对象")
    private String moduleStyle;

    /**
     * 组件的h5内容(文本组件内容，文章主标题内容)
     */
    @ApiModelProperty(value = "组件的h5内容(文本组件内容，文章主标题内容)")
    @TableField("module_html_content")
    @Excel(name = "组件的h5内容(文本组件内容，文章主标题内容)")
    private String moduleHtmlContent;

    /**
     * 单图片组件的url
     */
    @ApiModelProperty(value = "单图片组件的url")
    @Length(max = 300, message = "单图片组件的url长度不能超过300")
    @TableField(value = "module_image_url", condition = LIKE)
    @Excel(name = "单图片组件的url")
    private String moduleImageUrl;

    /**
     * 轮播方式 （默认，自动，手动）
     */
    @ApiModelProperty(value = "轮播方式")
    @Length(max = 50, message = "轮播方式长度不能超过50")
    @TableField(value = "carousel_way", condition = LIKE)
    @Excel(name = "轮播方式")
    private SiteCarouseWayEnum carouselWay;

    /**
     * 展示时间
     */
    @ApiModelProperty(value = "展示时间")
    @TableField("carousel_show_time")
    @Excel(name = "展示时间")
    private Float carouselShowTime;

    /**
     * 轮播的切换时间
     */
    @ApiModelProperty(value = "轮播的切换时间")
    @TableField("carousel_switch_time")
    @Excel(name = "轮播的切换时间")
    private Float carouselSwitchTime;


    @ApiModelProperty(value = "图片间距")
    @TableField("carousel_image_spacing")
    private Integer carouselImageSpacing;

    @ApiModelProperty(value = "图片缩放方式")
    @TableField("carousel_image_fill_style")
    private SiteImageFillStyle carouselImageFillStyle;

    /**
     * 组件主标题显示状态（0不显示， 1显示）
     * {@link com.caring.sass.common.constant.CommonStatus}
     */
    @ApiModelProperty(value = "组件主标题显示状态（0不显示， 1显示）")
    @TableField("module_title_show_status")
    @Excel(name = "组件主标题显示状态（0不显示， 1显示）")
    private Integer moduleTitleShowStatus;

    @ApiModelProperty(value = "组件副标题显示状态（0不显示， 1显示）")
    @TableField("module_subtitle_show_status")
    @Excel(name = "组件副标题显示状态（0不显示， 1显示）")
    private Integer moduleSubtitleShowStatus;

    @ApiModelProperty(value = "组件副标题内容")
    @TableField("module_subtitle_content")
    private String moduleSubtitleContent;

    /**
     * 组件主标题显示位置(left, center)
     */
    @ApiModelProperty(value = "组件主标题显示位置(left, center)")
    @Length(max = 30, message = "组件主标题显示位置(left, center)长度不能超过30")
    @TableField(value = "module_title_show_position", condition = LIKE)
    @Excel(name = "组件主标题显示位置(left, center)")
    private SiteTitlePositionEnum moduleTitleShowPosition;

    /**
     * 控制显示（摘要，时间，收藏，浏览量，留言）存储json对象
     *
     */
    @ApiModelProperty(value = "控制显示 [\"ABSTRACT\",\"TIME\",\"COLLECT\",\"PAGE_VIEW\",\"COMMENT\"]")
    @TableField(value = "module_show_content", condition = LIKE)
    @Excel(name = "控制显示（摘要，时间，收藏，浏览量，留言）存储json对象")
    private String moduleShowContent;

    @ApiModelProperty(value = "视频库内视频的ID (视频组件用 -1时表示非视频库视频)")
    @TableField(value = "ware_house_video_id", condition = EQUAL)
    private Long wareHouseVideoId;


    @ApiModelProperty(value = "视频组件URL (视频组件用)")
    @TableField(value = "video_url", condition = LIKE)
    private String videoUrl;

    @ApiModelProperty(value = "视频组件封面 (视频组件用)")
    @TableField(value = "video_cover", condition = LIKE)
    private String videoCover;

    @ApiModelProperty(value = "一屏显示数量")
    @TableField(value = "screen_show_number", condition = EQUAL)
    private Integer screenShowNumber;

    @ApiModelProperty(value = "搜索组件关联的页面ID")
    @TableField(value = "page_id_json_array", condition = EQUAL)
    private String pageIdJsonArray;

    @ApiModelProperty(value = "隐藏图片的边框")
    @TableField(value = "hide_image_border", condition = EQUAL)
    private Integer hideImageBorder;

    @ApiModelProperty(value = "租户信息")
    @TableField(exist = false)
    private String tenantCode;

    @ApiModelProperty(value = "组件的跳转信息")
    @TableField(exist = false)
    private SiteJumpInformation jumpInformation;

    @ApiModelProperty(value = "多功能导航组件的标签")
    @TableField(exist = false)
    private List<SiteModuleLabel> moduleLabelList;

    @ApiModelProperty(value = "文章，多视频，多功能导航的样式设置")
    @TableField(exist = false)
    private List<SiteModuleTitleStyle> moduleTitleStyles;

    @ApiModelProperty(value = "组件中的板块")
    @TableField(exist = false)
    private List<SiteModulePlate> modulePlates;


    public void setModuleLabelList(List<SiteModuleLabel> moduleLabelList) {
        SiteModuleLabel.sort(moduleLabelList);
        this.moduleLabelList = moduleLabelList;
    }

    public void setModulePlates(List<SiteModulePlate> modulePlates) {
        SiteModulePlate.sort(modulePlates);
        this.modulePlates = modulePlates;
    }
}
