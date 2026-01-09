package com.caring.sass.tenant.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 项目复制实体类
 *
 * @author leizhi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TenantCopyDTO", description = "企业复制")
public class TenantCopyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "被复制的项目编码不能为空")
    @ApiModelProperty(value = "被复制的项目编码", required = true)
    private String fromTenantCode;

    /**
     * 企业名称
     */
    @NotEmpty(message = "项目名称不能为空")
    @ApiModelProperty(value = "企业名称", required = true)
    private String name;

    @ApiModelProperty(value = "域名", required = true)
    @NotEmpty(message = "域名不能为空")
    private String domainName;
}
