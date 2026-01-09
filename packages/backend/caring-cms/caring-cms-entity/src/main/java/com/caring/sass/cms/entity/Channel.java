package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.common.enums.CmsRoleRemark;
import com.caring.sass.common.enums.OwnerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 栏目
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
@TableName("t_cms_channel")
@ApiModel(value = "Channel", description = "栏目")
@AllArgsConstructor
public class Channel extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 频道名称
     */
    @ApiModelProperty(value = "频道名称")
    @Length(max = 50, message = "频道名称长度不能超过50")
    @TableField(value = "channel_name", condition = LIKE)
    @Excel(name = "频道名称")
    private String channelName;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 频道类型 , TextImage/Article/Banner/LearningPlan
     */
    @ApiModelProperty(value = "频道类型 , 枚举类型 TextImage/Article/Banner/LearningPlan")
    @Length(max = 20, message = "频道类型 , 长度不能超过20")
    @TableField(value = "channel_type", condition = EQUAL)
    @Excel(name = "频道类型 , 枚举类型 TextImage/Article/Banner/LearningPlan")
    private String channelType;

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

    @ApiModelProperty(value = "父级ID")
    @TableField(value = "parent_id", condition = EQUAL)
    private Long parentId;

    @ApiModelProperty(value = "角色分类")
    @TableField(value = "cms_role_remark")
    private CmsRoleRemark cmsRoleRemark;

    @ApiModelProperty(value = "栏目组的ID")
    @TableField(value = "channel_group_id", condition = EQUAL)
    private Long channelGroupId;

    @ApiModelProperty(value = "内容库ID")
    @TableField(value = "library_id", condition = EQUAL)
    private Long libraryId;

    @TableField(exist = false)
    private String tenantCode;

    /**
     * 文章数
     */
    @ApiModelProperty(value = "文章数")
    @TableField(exist = false)
    @Excel(name = "文章数")
    private Integer articleCount;

    @TableField(exist = false)
    private Boolean hasChildren;
}
