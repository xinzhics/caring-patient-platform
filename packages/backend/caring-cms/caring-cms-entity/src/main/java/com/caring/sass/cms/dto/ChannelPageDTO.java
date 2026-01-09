package com.caring.sass.cms.dto;

import com.caring.sass.common.enums.OwnerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@ApiModel(value = "ChannelPageDTO", description = "栏目")
public class ChannelPageDTO implements Serializable {

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
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 频道类型 , Resource/Course/Picture/Text/Article/Teacher/CoursePackage
     */
    @ApiModelProperty(value = "频道类型 ,  枚举类型 （文章传 Article, 图文消息 TextImage, 轮播图 Banner, 学习计划 LearningPlan ）")
    @Length(max = 20, message = "频道类型 ,  枚举类型长度不能超过20")
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

    @ApiModelProperty(value = "分组名称")
    private Long channelGroupId;

    @ApiModelProperty(value = "内容库ID")
    private Long libraryId;

    @ApiModelProperty("租户")
    private String tenantCode;

}
