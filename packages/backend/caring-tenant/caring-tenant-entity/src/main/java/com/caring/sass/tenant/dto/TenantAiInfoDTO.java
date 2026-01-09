package com.caring.sass.tenant.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 修改或者编辑AI助手的名称和头像
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TenantAiInfoDTO", description = "修改或者编辑AI助手的名称和头像")
public class TenantAiInfoDTO {

    private String tenantCode;

    @ApiModelProperty(value = "AI助手的名称")
    @NotEmpty
    private String aiAssistantName;

    @ApiModelProperty(value = "AI助手的头像")
    @NotEmpty
    private String aiAssistantImage;


}
