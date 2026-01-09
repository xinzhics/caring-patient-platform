package com.caring.sass.tenant.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author xinzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "RecommendDrugsSaveDTO", description = "推荐用药保存参数")
public class RecommendDrugsUpsertDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    @NotNull
    private Integer order;

    /**
     * 药品id
     */
    @ApiModelProperty(value = "药品id")
    @NotNull
    private Long drugId;

    @ApiModelProperty(value = "推荐用药id，新增不传，修改传")
    private Long id;
}
