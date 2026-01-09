package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.enums.OwnerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
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
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_channel_content")
@ApiModel(value = "ChannelContent", description = "栏目内容")
@AllArgsConstructor
public class ChannelContent extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 栏目id
     */
    @ApiModelProperty(value = "栏目id")
    @Length(min = 1, max = 32, message = "栏目id长度不能超过32")
    @TableField(value = "c_channel_id", condition = LIKE)
    @Excel(name = "栏目id")
    private Long channelId;

    /**
     * 频道名称
     */
    @ApiModelProperty(value = "文章标题")
    @Length(max = 200, message = "频道名称长度不能超过200")
    @TableField(value = "c_title", condition = LIKE)
    @Excel(name = "频道名称")
    private String title;

    @ApiModelProperty(value = "文章英文标题")
    @TableField(value = "english_title", condition = LIKE)
    private String englishTitle;

    @ApiModelProperty(value = "关键字")
    @TableField(value = "c_keyword", condition = LIKE)
    private String keyWord;

    @ApiModelProperty(value = "文献期次")
    @TableField(value = "c_cms_period", condition = LIKE)
    private String cmsPeriod;
    /**
     * 主缩略图
     */
    @ApiModelProperty(value = "主缩略图")
    @TableField(value = "c_icon", condition = LIKE)
    @Excel(name = "主缩略图")
    private String icon;

    /**
     * 内容，例如文章类型的，
     */
    @ApiModelProperty(value = "内容")
    @TableField(value = "c_content")
    @Excel(name = "内容，例如文章类型的，")
    private String content;

    /**
     * 链接地址
     */
    @ApiModelProperty(value = "链接地址")
    @TableField(value = "c_link", condition = LIKE)
    @Excel(name = "链接地址")
    private String link;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("n_sort")
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 置顶（0, 1）
     */
    @ApiModelProperty(value = "置顶（0, 1）")
    @TableField("n_is_top")
    @Excel(name = "置顶（0, 1）")
    private Integer isTop;

    /**
     * 点击量
     */
    @ApiModelProperty(value = "点击量")
    @TableField("n_hit_count")
    @Excel(name = "点击量")
    private Long hitCount;

    /**
     * 所属栏目类型，与栏目表保持一致
     */
    @ApiModelProperty(value = "所属栏目类型，与栏目表保持一致")
    @Length(max = 20, message = "所属栏目类型，与栏目表保持一致长度不能超过20")
    @TableField(value = "c_channel_type", condition = EQUAL)
    @Excel(name = "所属栏目类型，与栏目表保持一致")
    private String channelType;

    /**
     * 摘要
     */
    @ApiModelProperty(value = "摘要")
    @Length(max = 65535, message = "摘要长度不能超过65535")
    @TableField("c_summary")
    @Excel(name = "摘要")
    private String summary;

    /**
     * 是否允许评论，1允许，0不允许。
     */
    @ApiModelProperty(value = "是否允许评论，1允许，0不允许。")
    @TableField("n_can_comment")
    @Excel(name = "是否允许评论，1允许，0不允许。")
    private Integer canComment;

    /**
     * 留言数量
     */
    @ApiModelProperty(value = "留言数量")
    @TableField("n_message_num")
    @Excel(name = "留言数量")
    private Integer messageNum;

    /**
     * 来源Id
     */
    @TableField(value = "c_source_id", condition = EQUAL)
    private Long sourceId;

    /**
     * 来源类
     */
    @TableField(value = "c_source_entity_class", condition = EQUAL)
    private String sourceEntityClass;


    @TableField(exist = false)
    private String channelName;

    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    @Length(max = 200, message = "作者长度不能超过200")
    @TableField(value = "author", condition = EQUAL)
    @Excel(name = "作者")
    private String author;


    /**
     * 所有者类型
     */
    @ApiModelProperty(value = "所有者类型，SYS表示系统，TENANT表示项目")
    @NotNull(message = "所有者类型不能为空")
    @TableField(value = "owner_type", condition = EQUAL)
    private OwnerTypeEnum ownerType;

    /**
     * 是否是医生学院，true是，false否
     */
    @ApiModelProperty(value = "是否是医生学院，true是，false否")
    @TableField(value = "doctor_owner", condition = EQUAL)
    @Excel(name = "是否是医生学院，true是，false否")
    private Boolean doctorOwner;

    @ApiModelProperty(value = "栏目组的ID")
    @TableField(value = "channel_group_id", condition = EQUAL)
    private Long channelGroupId;

    @ApiModelProperty(value = "内容库ID")
    @TableField(value = "library_id", condition = EQUAL)
    private Long libraryId;

    @ApiModelProperty(value = "是否显示在患者首页 1 展示。 0 不展示")
    @TableField(value = "patient_home_show", condition = EQUAL)
    private Integer patientHomeShow;

    @ApiModelProperty(value = "是否显示在患者首页 推荐：recommend ， 文章：article ， 视频： video ")
    @TableField(value = "patient_home_region", condition = LIKE)
    private String patientHomeRegion;

    @ApiModelProperty(value = "评论列表")
    @TableField(exist = false)
    private List<ContentReply> contentReplies;

    @ApiModelProperty(value = "是否收藏")
    @TableField(exist = false)
    private Boolean contentCollect;


}
