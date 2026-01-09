package com.caring.sass.cms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.enums.CmsRoleRemark;
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
 * 栏目
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
@ApiModel(value = "ChannelSaveDTO", description = "栏目")
public class ChannelSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 频道名称
     */
    @ApiModelProperty(value = "频道名称")
    @Length(max = 50, message = "频道名称长度不能超过50")
    private String channelName;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序, 不传时， 默认自增")
    private Integer sort;

    /**
     * 频道类型 , TextImage/Article/Banner/LearningPlan
     */
    @ApiModelProperty(value = "频道类型 , 枚举类型 （文章传 Article, 图文消息 TextImage, 轮播图 Banner, 学习计划 LearningPlan ）", required = true)
    @Length(max = 20, message = "频道类型 , 长度不能超过20")
    private String channelType;

    /**
     * 所有者类型
     */
    @ApiModelProperty(value = "所有者类型，SYS表示系统，TENANT表示项目")
    @NotNull(message = "所有者类型不能为空")
    private OwnerTypeEnum ownerType;

    /**
     * 是否是医生学院，true是，false否
     */
    @ApiModelProperty(value = "是否是医生学院，true是，false否")
    private Boolean doctorOwner;

    @ApiModelProperty(value = "父级ID")
    private Long parentId;

    @ApiModelProperty(value = "角色分类")
    private CmsRoleRemark cmsRoleRemark;

    @ApiModelProperty(value = "分组名称")
    private Long channelGroupId;

    @ApiModelProperty(value = "内容库ID")
    private Long libraryId;

}
