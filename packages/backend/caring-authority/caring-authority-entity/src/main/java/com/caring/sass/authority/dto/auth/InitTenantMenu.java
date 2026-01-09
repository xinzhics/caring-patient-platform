package com.caring.sass.authority.dto.auth;

import com.caring.sass.authority.entity.auth.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName InitTenantMenu
 * @Description
 * @Author yangShuai
 * @Date 2021/11/25 10:55
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "InitTenantMenu", description = "初始菜单使用")
public class InitTenantMenu {

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    @NotNull
    @Length(max = 20, message = "名称长度不能超过20")
    private String label;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 200, message = "描述长度不能超过200")
    private String describe;
    /**
     * 是否公开菜单
     * 就是无需分配就可以访问的。所有人可见
     */
    @ApiModelProperty(value = "公共菜单")
    private Boolean isPublic;
    /**
     * 路径
     */
    @ApiModelProperty(value = "路径")
    @NotNull
    @Length(max = 255, message = "路径长度不能超过255")
    private String path;
    /**
     * 组件
     */
    @ApiModelProperty(value = "组件")
    @NotNull
    @Length(max = 255, message = "组件长度不能超过255")
    private String component;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Boolean isEnable;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortValue;
    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标")
    private String icon;
    /**
     * 分组
     */
    @ApiModelProperty(value = "分组")
    @Length(max = 20, message = "分组长度不能超过20")
    private String group;
    /**
     * 父级菜单id
     */
    @ApiModelProperty(value = "上级菜单的路径")
    private String parentPath;

    @ApiModelProperty(value = "资源")
    private List<Resource> resources;

    @ApiModelProperty(value = "此菜单下的子菜单")
    private List<InitTenantMenu> menuChildList;

}
