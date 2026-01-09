package com.caring.sass.tenant.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 全局账号
 * </p>
 *
 * @author caring
 * @since 2019-10-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "GlobalUserSaveDTO", description = "全局账号")
public class GlobalUserPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    @NotEmpty(message = "租户编号不能为空")
    @Length(max = 10, message = "租户编号长度不能超过10")
    private String tenantCode;
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    @NotEmpty(message = "账号不能为空")
    @Length(max = 30, message = "账号长度不能超过30")
    private String account;
    /**
     * 手机
     */
    @ApiModelProperty(value = "手机")
    @Length(max = 20, message = "手机长度不能超过20")
    private String mobile;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 50, message = "姓名长度不能超过20")
    private String name;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @Length(max = 255, message = "邮箱长度不能超过255")
    private String email;

    /**
     * admin用户拥有所有菜单权限。
     * cms用户只有cms菜单权限，且只能看到设定的内容库
     */
    @ApiModelProperty(value = "全局用户类型 admin, cms")
    @TableField(value = "global_user_type", condition = EQUAL)
    private String globalUserType;

}
