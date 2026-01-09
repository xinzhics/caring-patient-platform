package com.caring.sass.cms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.enums.OwnerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ApiModel(value = "ChannelContentUpdateDTO", description = "栏目内容")
public class ChannelContentUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id")
    @Length(max = 32, message = "栏目id长度不能超过32")
    private String channelId;
    /**
     * 频道名称
     */
    @ApiModelProperty(value = "频道名称")
    @Length(max = 200, message = "频道名称长度不能超过200")
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
    @ApiModelProperty(value = "所属栏目类型， 枚举类型 （文章传 Article, 图文消息 TextImage, 轮播图 Banner, 学习计划 LearningPlan ）")
    @Length(max = 20, message = "所属栏目类型，与栏目表保持一致长度不能超过20")
    private String channelType;

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

    @ApiModelProperty(value = "分组ID")
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

    @ApiModelProperty(value = "是否显示在患者首页 推荐：recommend ， 文章：article ， 视频： video")
    private String patientHomeRegion;

    @ApiModelProperty(value = "文章英文标题")
    private String englishTitle;

    @ApiModelProperty(value = "关键字")
    private String keyWord;

}
