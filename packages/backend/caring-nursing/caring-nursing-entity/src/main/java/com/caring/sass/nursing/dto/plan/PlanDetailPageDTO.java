package com.caring.sass.nursing.dto.plan;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
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
@ApiModel(value = "PlanDetailPageDTO", description = "护理计划详情")
public class PlanDetailPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    /**
     * 营养食谱小时数
     */
    @ApiModelProperty(value = "营养食谱小时数")
    private Integer num;
    /**
     * 营养食谱小时数类型（0  检验数据 1个性化饮食）
     */
    @ApiModelProperty(value = "营养食谱小时数类型（0  检验数据 1个性化饮食）")
    private Integer dietType;
    /**
     * 护理计划推送 指定 使用的模板
     */
    @ApiModelProperty(value = "护理计划推送 指定 使用的模板")
    @Length(max = 32, message = "护理计划推送 指定 使用的模板长度不能超过32")
    private String templateMessageId;

}
