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

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

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
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_plan_detail")
@ApiModel(value = "PlanDetail", description = "护理计划详情")
@AllArgsConstructor
public class PlanDetail extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 第几天开始执行
     * 学习计划中不需要
     */
    @ApiModelProperty(value = "第几天开始执行")
    @TableField("execute_")
    @Excel(name = "第几天开始执行")
    private Integer execute;

    /**
     * 内容（文字：内容  文章：url）
     */
    @ApiModelProperty(value = "内容（文字：内容  文章：Id）")
    @Length(max = 1000, message = "内容（文字：内容  文章：Id）")
    @TableField(value = "content", condition = EQUAL)
    @Excel(name = "内容（文字：内容  文章：url）")
    private String content;

    /**
     * 护理计划ID
     */
    @ApiModelProperty(value = "护理计划ID")
    @TableField("nursing_plan_id")
    @Excel(name = "护理计划ID")
    private Long nursingPlanId;

    @ApiModelProperty(value = "推送类型")
    @TableField("push_type")
    @Excel(name = "推送类型 null 0 默认表单, 2 外部跳转链接")
    private Integer pushType;

    /**
     * 推送类型(0:图文消息 1：模板消息 2：文字)
     */
    @ApiModelProperty(value = "推送类型(1：模板消息  3 链接)")
    @TableField("type_")
    @Excel(name = "推送类型(1：模板消息 2：文字)")
    private Integer type;

    /**
     * 营养食谱小时数
     */
    @ApiModelProperty(value = "营养食谱小时数")
    @TableField("num")
    @Excel(name = "营养食谱小时数")
    private Integer num;

    /**
     * 营养食谱小时数类型（0  检验数据 1个性化饮食）
     */
    @ApiModelProperty(value = "营养食谱小时数类型（0  检验数据 1个性化饮食）")
    @TableField("diet_type")
    @Excel(name = "营养食谱小时数类型（0  检验数据 1个性化饮食）", replace = {"是_true", "否_false", "_null"})
    private Integer dietType;

    /**
     * 护理计划推送 指定 使用的模板
     */
    @ApiModelProperty(value = "护理计划推送 指定 使用的模板")
    @Length(max = 32, message = "护理计划推送 指定 使用的模板长度不能超过32")
    @TableField(value = "template_message_id", condition = EQUAL)
    @Excel(name = "护理计划推送 指定 使用的模板")
    private String templateMessageId;

    @ApiModelProperty(value = "租户")
    @TableField(exist = false)
    private String tenantCode;

    @ApiModelProperty("推送时间设置")
    @TableField(exist = false)
    private List<PlanDetailTime> planDetailTimeList;

    @Builder
    public PlanDetail(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Integer execute, String content, Long nursingPlanId, Integer type, Integer num, Integer dietType, String templateMessageId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.execute = execute;
        this.content = content;
        this.nursingPlanId = nursingPlanId;
        this.type = type;
        this.num = num;
        this.dietType = dietType;
        this.templateMessageId = templateMessageId;
    }

}
