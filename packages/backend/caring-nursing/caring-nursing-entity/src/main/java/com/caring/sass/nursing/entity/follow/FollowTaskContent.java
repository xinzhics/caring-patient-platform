package com.caring.sass.nursing.entity.follow;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_follow_task_content")
@ApiModel(value = "FollowTaskContent", description = "随访任务内容列表")
@AllArgsConstructor
public class FollowTaskContent extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    @TableField("plan_id")
    @Excel(name = "计划ID")
    private Long planId;

    @ApiModelProperty(value = "护理计划的名称")
    @TableField(value = "plan_name", condition = EQUAL)
    private String planName;

    @ApiModelProperty(value = "护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）")
    @TableField(value = "plan_type", condition = EQUAL)
    private Integer planType;

    @ApiModelProperty(value = "护理计划的状态")
    @TableField(value = "plan_status", condition = EQUAL)
    private Integer planStatus;
    /**
     * 显示的名称
     */
    @ApiModelProperty(value = "显示的名称")
    @Length(max = 50, message = "显示的名称长度不能超过50")
    @TableField(value = "show_name", condition = LIKE)
    @Excel(name = "显示的名称")
    private String showName;

    /**
     * 显示或隐藏（0：隐藏  1：显示）
     */
    @ApiModelProperty(value = "显示或隐藏（0：隐藏  1：显示）")
    @TableField("show_content")
    @Excel(name = "显示或隐藏（0：隐藏  1：显示）")
    private Integer showContent;

    /**
     * 时间范围
     */
    @ApiModelProperty(value = "时间范围")
    @Length(max = 50, message = "时间范围长度不能超过50")
    @TableField(value = "time_frame", condition = LIKE)
    @Excel(name = "时间范围")
    private String timeFrame;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("content_sort")
    @Excel(name = "排序")
    private Integer contentSort;

    @ApiModelProperty(value = "计划数量")
    @TableField(exist = false)
    private int planNumber = 1;

    @ApiModelProperty(value = "患者是否订阅 -1 学习计划，不处理。 1 是订阅，0是未订阅")
    @TableField(exist = false)
    private int subscribe = 0;

    @Builder
    public FollowTaskContent(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,  String planName,
                    Long planId, String showName, Integer showContent, Integer planType, String timeFrame, Integer contentSort, Integer planStatus) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.planId = planId;
        this.showName = showName;
        this.showContent = showContent;
        this.timeFrame = timeFrame;
        this.contentSort = contentSort;
        this.planType = planType;
        this.planStatus = planStatus;
        this.planName = planName;
    }

}
