package com.caring.sass.nursing.dto.plan;

import com.caring.sass.nursing.entity.plan.PlanDetailTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "PlanDetailDTO", description = "护理计划详情")
public class PlanDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 第几天开始执行
     */
    @ApiModelProperty(value = "第几天开始执行 必传")
    private Integer execute;
    /**
     * 内容（文字：内容  文章：url）
     */
    @ApiModelProperty(value = "内容（文字：内容  文章：url）")
    @Length(max = 1000, message = "内容（文字：内容  文章：url）长度不能超过1000")
    private String content;
    /**
     * 护理计划ID
     */
    @ApiModelProperty(value = "护理计划ID")
    private Long nursingPlanId;
    /**
     * 推送类型(0:图文消息 1：模板消息 2：文字)
     */
    @ApiModelProperty(value = "推送类型(1：模板消息 2：文字) 必传 默认传1")
    private Integer type;

    @ApiModelProperty(value = "跳转类型  null 0 默认表单, 2 外部跳转链接 必传 默认传0")
    private Integer pushType;
    /**
     * 营养食谱小时数
     */
    @ApiModelProperty(value = "营养食谱小时数 可不传")
    private Integer num;
    /**
     * 营养食谱小时数类型（0  检验数据 1个性化饮食）
     */
    @ApiModelProperty(value = "营养食谱小时数类型（0  检验数据 1个性化饮食） 可不传")
    private Integer dietType;
    /**
     * 护理计划推送 指定 使用的模板
     */
    @ApiModelProperty(value = "学习计划关联的模板ID存放")
    private String templateMessageId;

    @ApiModelProperty("推送时间")
    private List<PlanDetailTime> planDetailTimes;
}
