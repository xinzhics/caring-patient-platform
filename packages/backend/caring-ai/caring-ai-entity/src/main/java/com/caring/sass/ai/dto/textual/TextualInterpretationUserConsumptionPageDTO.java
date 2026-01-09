package com.caring.sass.ai.dto.textual;

import com.caring.sass.ai.entity.article.ConsumptionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 文献解读能量豆消费记录
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TextualInterpretationUserConsumptionPageDTO", description = "文献解读能量豆消费记录")
public class TextualInterpretationUserConsumptionPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull
    private Long userId;

    /**
     * 消费类型
     */
    @ApiModelProperty(value = "消费类型")
    private ConsumptionType consumptionType;


}
