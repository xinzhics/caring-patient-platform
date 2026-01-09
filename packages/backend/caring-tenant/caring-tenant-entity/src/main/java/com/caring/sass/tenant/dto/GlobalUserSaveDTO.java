package com.caring.sass.tenant.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.tenant.entity.LibraryGlobalUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
public class GlobalUserSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 企业编码
     */
    @ApiModelProperty(value = "企业编码")
    @NotEmpty(message = "企业编码不能为空")
    @Length(max = 10, message = "企业编码长度不能超过10")
    private String tenantCode;
    /**
     * 账号
     */
    @ApiModelProperty(value = "客户账号默认为手机号")
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
     * 密码
     */
    @ApiModelProperty(value = "密码，客户密码传123456")
    @Length(max = 64, message = "密码长度不能超过64")
    @NotEmpty(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "确认密码，客户密码传123456")
    @Length(max = 64, message = "确认密码长度不能超过76")
    @NotEmpty(message = "确认密码不能为空")
    private String confirmPassword;


    /**
     * admin用户拥有所有菜单权限。
     * cms用户只有cms菜单权限，且只能看到设定的内容库
     */
    @ApiModelProperty(value = "全局用户类型 admin, cms, 第三方客户 third_party_customers ")
    private String globalUserType;

    @ApiModelProperty(value = "企业名称")
    @Length(max = 255, message = "企业名称长度不能超过255")
    private String enterprise;

    @ApiModelProperty(value = "企业logo")
    private String enterpriseLogo;

    @ApiModelProperty(value = "用户备注")
    @Length(max = 500, message = "用户备注长度不能超过500")
    private String userRemark;

    @ApiModelProperty(value = "cms账号对应的内容库")
    private List<LibraryGlobalUser> libraryGlobalUsers;


}
