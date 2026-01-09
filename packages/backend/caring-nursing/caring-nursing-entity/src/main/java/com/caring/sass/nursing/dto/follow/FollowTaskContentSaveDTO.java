package com.caring.sass.nursing.dto.follow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 随访任务内容列表
 * </p>
 *
 * @author 杨帅
 * @since 2023-01-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FollowTaskContentSaveDTO", description = "随访任务内容列表")
public class FollowTaskContentSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    private Long planId;
    /**
     * 显示的名称
     */
    @ApiModelProperty(value = "显示的名称")
    @Length(max = 50, message = "显示的名称长度不能超过50")
    private String showName;
    /**
     * 显示或隐藏（0：隐藏  1：显示）
     */
    @ApiModelProperty(value = "显示或隐藏（0：隐藏  1：显示）")
    private Integer show;
    /**
     * 时间范围
     */
    @ApiModelProperty(value = "时间范围")
    @Length(max = 50, message = "时间范围长度不能超过50")
    private String timeFrame;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer contentSort;

}
