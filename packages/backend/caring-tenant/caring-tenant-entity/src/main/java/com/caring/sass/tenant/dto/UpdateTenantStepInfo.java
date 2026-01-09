package com.caring.sass.tenant.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@ApiModel(value = "UpdateTenantStepInfo", description = "修改项目步骤信息")
public class UpdateTenantStepInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    @NotEmpty
    private String code;


    @ApiModelProperty(value = "当前顶级配置步骤，默认0即未开始")
    private Integer curTopStep;

    @NotNull(message = "当前顶级步骤的子步骤不能为空")
    private Integer curChildStep;
}
