package com.caring.sass.tenant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_global_user")
@ApiModel(value = "GlobalUser", description = "全局账号")
public class GlobalUser extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    @NotEmpty(message = "租户编号不能为空")
    @Length(max = 10, message = "租户编号长度不能超过10")
    @TableField(value = "tenant_code", condition = LIKE)
    private String tenantCode;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    @NotEmpty(message = "账号不能为空")
    @Length(max = 30, message = "账号长度不能超过30")
    @TableField(value = "account", condition = EQUAL)
    @Excel(name = "账号", width = 20)
    private String account;

    /**
     * 手机
     */
    @ApiModelProperty(value = "手机")
    @Length(max = 20, message = "手机长度不能超过20")
    @TableField(value = "mobile", condition = EQUAL)
    @Excel(name = "手机", width = 20)
    private String mobile;

    @ApiModelProperty(value = "是否只读")
    @TableField(value = "readonly")
    @Excel(name = "是否只读", width = 20)
    private Boolean readonly;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 50, message = "姓名长度不能超过20")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "姓名", width = 20)
    private String name;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @Length(max = 255, message = "邮箱长度不能超过255")
    @TableField(value = "email", condition = LIKE)
    @Excel(name = "邮箱", width = 20)
    private String email;

    @ApiModelProperty(value = "密码")
    @Length(max = 64, message = "密码长度不能超过64")
    @TableField(value = "password", condition = EQUAL)
    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private String password;

    /**
     * admin用户拥有所有菜单权限。
     * cms用户只有cms菜单权限，且只能看到设定的内容库
     */
    @ApiModelProperty(value = "全局用户类型 admin, cms, 第三方客户 third_party_customers, 运维只看项目统计页面： admin_operation ")
    @TableField(value = "global_user_type", condition = EQUAL)
    private String globalUserType;

    @ApiModelProperty(value = "企业名称")
    @TableField(value = "enterprise", condition = LIKE)
    private String enterprise;

    @ApiModelProperty(value = "企业logo")
    @TableField(value = "enterprise_logo")
    private String enterpriseLogo;

    @ApiModelProperty(value = "企业logo")
    @TableField(value = "user_remark")
    private String userRemark;

    @ApiModelProperty(value = "删除标记")
    @TableField(value = "user_deleted", condition = EQUAL)
    private Integer userDeleted;


    @ApiModelProperty(value = "首次登录时间")
    @TableField(value = "login_time")
    private LocalDateTime loginTime;

    @ApiModelProperty(value = "客户是否已经首次登录")
    @TableField(value = "first_login", condition = EQUAL)
    private Integer firstLogin;

    @ApiModelProperty(value = "最新访问日期")
    @TableField(value = "latest_access_time")
    private LocalDate latestAccessTime;

    @ApiModelProperty(value = "cms账号对应的内容库")
    @TableField(exist = false)
    private List<LibraryGlobalUser> libraryGlobalUsers;

    @JsonProperty
    public GlobalUser setPassword(String password) {
        this.password = password;
        return this;
    }



}
