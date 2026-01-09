package com.caring.sass.tenant.dto.router;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

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
@ApiModel(value = "PatientManageMenuSaveDTO", description = "患者管理平台菜单")
public class PatientManageMenuSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Length(max = 50, message = "长度不能超过50")
    private String name;

    @Length(max = 400, message = "长度不能超过400")
    private String icon;

    private Integer menuSort;
    /**
     * 显示或隐藏(0 隐藏 1显示)
     */
    @ApiModelProperty(value = "显示或隐藏(0 隐藏 1显示)")
    private Integer showStatus;
    /**
     * 菜单类型(信息完整度，监测数据，用药预警)
     */
    @ApiModelProperty(value = "菜单类型(信息完整度，监测数据，用药预警)")
    @Length(max = 100, message = "菜单类型(信息完整度，监测数据，用药预警)长度不能超过100")
    private String menuType;

}
