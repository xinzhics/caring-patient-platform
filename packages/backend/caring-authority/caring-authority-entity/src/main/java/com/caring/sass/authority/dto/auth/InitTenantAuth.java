package com.caring.sass.authority.dto.auth;

import com.caring.sass.authority.entity.auth.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @ClassName InitTenantAuth
 * @Description
 * @Author yangShuai
 * @Date 2021/11/25 10:47
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "InitTenantAuth", description = "菜单")
public class InitTenantAuth {

    /**
     * 拥有此菜单的角色code
     */
    @ApiModelProperty(value = "角色code")
    @Length(max = 20, message = "角色code")
    private List<String> roleCode;

    @ApiModelProperty(value = "要初始化的项目code, 不传则是全部项目")
    private List<String> tenantCode;

    @ApiModelProperty(value = "菜单（菜单路径已经存在项目菜单中时，将会过滤）")
    private List<InitTenantMenu> menuList;


}
