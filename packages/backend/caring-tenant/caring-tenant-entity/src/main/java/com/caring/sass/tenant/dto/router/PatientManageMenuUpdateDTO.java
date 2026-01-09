package com.caring.sass.tenant.dto.router;

import com.caring.sass.tenant.entity.router.PatientManageMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 实体类
 * 患者管理平台菜单
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PatientManageMenuUpdateDTO", description = "患者管理平台菜单")
public class PatientManageMenuUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("租户code")
    private String tenantCode;

    @ApiModelProperty("患者管理平台菜单")
    private List<PatientManageMenu> menuList;

}
