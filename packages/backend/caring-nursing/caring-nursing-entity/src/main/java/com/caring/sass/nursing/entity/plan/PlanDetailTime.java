package com.caring.sass.nursing.entity.plan;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 护理计划详情
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_plan_detail_time")
@ApiModel(value = "PlanDetailTime", description = "护理计划详情")
@AllArgsConstructor
public class PlanDetailTime extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 推送频率(0:单次 )
     */
    @ApiModelProperty(value = "推送频率(0:单次 ) 必传，默认传0")
    @TableField("frequency")
    @Excel(name = "推送频率(0:单次 )")
    private Integer frequency;

    /**
     * 推送时间
     */
    @ApiModelProperty(value = "推送时间 必传")
    @TableField("time_")
    @NotNull(message = "推送时间不能为空，请检查")
    @Excel(name = "推送时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalTime time;

    /**
     * 护理计划详情ID
     */
    @ApiModelProperty(value = "护理计划详情ID")
    @TableField("nursing_plan_detail_id")
    @Excel(name = "护理计划详情ID")
    private Long nursingPlanDetailId;

    /**
     * 触发前？触发后？几天？
     */
    @ApiModelProperty(value = "触发前？触发后？几天？必传 -9999 表示注册完成时推送")
    @TableField("pre_time")
    @Excel(name = "触发前？触发后？几天？")
    private Integer preTime;

    /**
     * 护理计划推送 指定 使用的模板
     */
    @ApiModelProperty(value = "护理计划推送 指定 使用的模板, 或学习计划推送的文章Id 或文章链接")
    @TableField(value = "template_message_id", condition = EQUAL)
    @Excel(name = "护理计划推送 指定 使用的模板, 或者推送的文章")
    private String templateMessageId;

    @ApiModelProperty(value = "文章的标题")
    @TableField(value = "cms_title", condition = EQUAL)
    private String cmsTitle;

    @ApiModelProperty(value = "学习计划推送的是否链接 (0： 文章， 1： 链接)")
    @TableField(value = "send_url", condition = EQUAL)
    private Integer sendUrl;

    @ApiModelProperty(value = "租户")
    @TableField(exist = false)
    private String tenantCode;

}
