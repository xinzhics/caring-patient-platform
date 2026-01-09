package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.cms.constant.SitePageTemplateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

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
@TableName("t_build_site_folder_page")
@ApiModel(value = "SiteFolderPage", description = "建站文件夹中的页面表")
@AllArgsConstructor
public class SiteFolderPage extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static String DEFAULT_PAGE_NAME = "自定义标题";

    /**
     * 文件夹名称
     */
    @ApiModelProperty(value = "文件名称")
    @Length(max = 50, message = "文件名称长度不能超过50")
    @TableField(value = "page_name", condition = LIKE)
    @Excel(name = "文件夹名称")
    private String pageName;

    /**
     * 是否为模板(0 不是， 1 是)
     * {@link com.caring.sass.common.constant.CommonStatus}
     */
    @ApiModelProperty(value = "是否为模板(0 不是， 1 是)")
    @TableField(value = "template", condition = EQUAL)
    @Excel(name = "是否为模板(0 不是， 1 是)")
    private Integer template;

    /**
     * 是否首页(0 不是， 1 是)  文件夹中第一个页面是首页
     * {@link com.caring.sass.common.constant.CommonStatus}
     */
    @ApiModelProperty(value = "是否首页(0 不是， 1 是)  文件夹中第一个页面是首页")
    @TableField("home_status")
    @Excel(name = "是否首页(0 不是， 1 是)  文件夹中第一个页面是首页")
    private Integer homeStatus;

    /**
     * 模板类型(page 单页模板 ， folder 文件夹模板)
     */
    @ApiModelProperty(value = "模板类型(PAGE 单页模板 ， FOLDER 文件夹模板)")
    @Length(max = 50, message = "模板类型(PAGE 单页模板 ， FOLDER 文件夹模板)长度不能超过50")
    @TableField(value = "template_type", condition = EQUAL)
    @Excel(name = "模板类型(page 单页模板 ， folder 文件夹模板)")
    private SitePageTemplateType templateType;

    /**
     * 页面的基础样式
     */
    @ApiModelProperty(value = "页面样式(标题栏背景色) {background: rgb(16, 16, 16)} ")
    @Length(max = 1000, message = "页面的基础样式长度不能超过1000")
    @TableField(value = "page_style", condition = LIKE)
    @Excel(name = "页面的基础样式")
    private String pageStyle;

    /**
     * 文件夹的ID
     */
    @ApiModelProperty(value = "文件夹的ID")
    @TableField(value = "folder_id", condition = EQUAL)
    @Excel(name = "文件夹的ID")
    private Long folderId;

    /**
     * 复制次数
     */
    @ApiModelProperty(value = "复制次数")
    @TableField("copy_number")
    @Excel(name = "复制次数")
    private Integer copyNumber;

    @ApiModelProperty(value = "文件夹名称拼音")
    @Length(max = 50, message = "文件夹名称拼音长度不能超过50")
    @TableField(value = "name_pinyin", condition = LIKE)
    @Excel(name = "文件夹名称")
    private String namePinyin;

    @ApiModelProperty(value = "名称首字母")
    @TableField(value = "name_first_letter", condition = EQUAL)
    @Excel(name = "名称首字母")
    private String nameFirstLetter;


    @ApiModelProperty(value = "封面")
    @TableField(value = "cover_image", condition = EQUAL)
    @Excel(name = "封面")
    private String coverImage;

    @ApiModelProperty(value = "组件列表")
    @TableField(exist = false)
    private List<SiteFolderPageModule> moduleList;


}
