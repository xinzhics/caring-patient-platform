package com.caring.sass.tenant.dto.router;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 患者路由顺序
 * </p>
 *
 * @author leizhi
 * @since 2021-03-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "H5RouterSortUpdateDTO", description = "患者路由顺序")
public class H5RouterSortUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "lastRouterId")
    @NotNull(message = "lastRouterId不能为空")
    private Long lastRouterId;

    @ApiModelProperty(value = "nextRouterId")
    @NotNull(message = "nextRouterId不能为空")
    private Long nextRouterId;

    @ApiModelProperty(value = "lastRouterSort")
    @NotNull(message = "lastRouterSort不能为空")
    private Integer lastRouterSort;

    @ApiModelProperty(value = "nextRouterSort")
    @NotNull(message = "nextRouterSortSort不能为空")
    private Integer nextRouterSort;
}
