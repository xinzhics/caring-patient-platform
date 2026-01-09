package com.caring.sass.ai.entity.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "ArticleUserConsumptionPageDto", description = "科普创作用户能量豆消费记录")
@AllArgsConstructor
public class ArticleUserConsumptionPageDto {



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
