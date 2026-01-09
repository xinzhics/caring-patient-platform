package com.caring.sass.nursing.dto.plan;

import com.caring.sass.base.entity.SuperEntity;
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
@Builder
@ApiModel(value = "PlanDetailUpdateDTO", description = "护理计划详情")
public class PlanDetailUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 第几天开始执行
     */
    @ApiModelProperty(value = "第几天开始执行")
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
    @ApiModelProperty(value = "推送类型(0:图文消息 1：模板消息 2：文字)")
    private Integer type;

    @ApiModelProperty(value = "推送类型  null 0 默认表单, 2 外部跳转链接")
    private Integer pushType;
    /**
     * 营养食谱小时数
     */
    @ApiModelProperty(value = "营养食谱小时数")
    private Integer num;
    /**
     * 营养食谱小时数类型（0  检验数据 1个性化饮食）
     */
    @ApiModelProperty(value = "营养食谱小时数类型（0  检验数据 1个性化饮食）")
    private Boolean dietType;
    /**
     * 护理计划推送 指定 使用的模板
     */
    @ApiModelProperty(value = "护理计划推送 指定 使用的模板")
    @Length(max = 32, message = "护理计划推送 指定 使用的模板长度不能超过32")
    private String templateMessageId;
}
