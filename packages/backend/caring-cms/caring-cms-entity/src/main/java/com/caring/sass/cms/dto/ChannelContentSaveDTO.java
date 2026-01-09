package com.caring.sass.cms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.enums.OwnerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 栏目内容
 * </p>
 *
 * @author leizhi
 * @since 2020-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ChannelContentSaveDTO", description = "栏目内容")
public class ChannelContentSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id", required = true)
    @Length(max = 32, message = "栏目id长度不能超过32")
    private String channelId;
    /**
     * 频道名称
     */
    @ApiModelProperty(value = "标题")
    @Length(max = 200, message = "标题长度不能超过200")
    private String title;
    /**
     * 主缩略图
     */
    @ApiModelProperty(value = "主缩略图")
    @Length(max = 255, message = "主缩略图长度不能超过255")
    private String icon;

    /**
     * 内容，例如文章类型的，
     */
    @ApiModelProperty(value = "内容，例如文章类型的，")
    private String content;
    /**
     * 链接地址
     */
    @ApiModelProperty(value = "链接地址")
    private String link;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**
     * 所属栏目类型，与栏目表保持一致
     */
    @ApiModelProperty(value = "所属栏目类型，请保持和栏目类型一致， 枚举类型 （文章传 Article, 图文消息 TextImage, 轮播图 Banner, 学习计划 LearningPlan ）", required = true)
    @Length(max = 20, message = "所属栏目类型，与栏目表保持一致长度不能超过20")
    private String channelType;
    /**
     * 主图标名字
     */
    @ApiModelProperty(value = "主图标名字")
    @Length(max = 50, message = "主图标名字长度不能超过50")
    private String iconName;

    /**
     * 摘要
     */
    @ApiModelProperty(value = "摘要")
    @Length(max = 65535, message = "摘要长度不能超过65,535")
    private String summary;
    /**
     * 是否允许评论，1允许，0不允许。
     */
    @ApiModelProperty(value = "是否允许评论，1允许，0不允许。")
    private Integer canComment;
    /**
     * 内容类别（system系统类别， customer 项目或客户类别）
     */
    @ApiModelProperty(value = "内容类别（SYSTEM  超级管理员 使用 SYSTEM， CUSTOMIZE ProjectAdmin使用 CUSTOMIZE）", required = true)
    @Length(max = 30, message = "内容类别（SYSTEM系统类别， CUSTOMIZE 项目或客户类别）长度不能超过30")
    private String type;

    /**
     * 所有者类型
     */
    @ApiModelProperty(value = "所有者类型，SYS表示系统，TENANT表示项目")
    @NotNull(message = "所有者类型不能为空")
    private OwnerTypeEnum ownerType;

    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    @Length(max = 200, message = "作者长度不能超过200")
    private String author;

    /**
     * 是否是医生学院，true是，false否
     */
    @ApiModelProperty(value = "是否是医生学院，true是，false否")
    private Boolean doctorOwner;

    @ApiModelProperty(value = "分组名称")
    private Long channelGroupId;

    @ApiModelProperty(value = "内容库ID")
    private Long libraryId;

    /**
     * 置顶（0, 1）
     */
    @ApiModelProperty(value = "置顶（0, 1）")
    private Integer isTop;

    /**
     * 点击量
     */
    @ApiModelProperty(value = "点击量")
    private Long hitCount;

    @ApiModelProperty(value = "是否显示在患者首页 1 展示。 0 不展示")
    private Integer patientHomeShow;

    @ApiModelProperty(value = "是否显示在患者首页 推荐：recommend ， 文章：article ， 视频： video ")
    private String patientHomeRegion;

    @ApiModelProperty(value = "文章英文标题")
    private String englishTitle;

    @ApiModelProperty(value = "关键字")
    private String keyWord;

}
