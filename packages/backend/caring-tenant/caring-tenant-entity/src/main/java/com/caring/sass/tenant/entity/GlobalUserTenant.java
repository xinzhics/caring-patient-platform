package com.caring.sass.tenant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 用户项目管理表
 * </p>
 *
 * @author 杨帅
 * @since 2023-04-11
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_global_user_tenant")
@ApiModel(value = "GlobalUserTenant", description = "用户项目管理表")
@AllArgsConstructor
public class GlobalUserTenant extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static String AUTHORIZED_PROJECTS = "authorized_projects";

    public static String CREATED_PROJECTS = "created_projects";

    /**
     * 账号ID
     */
    @ApiModelProperty(value = "账号ID")
    @NotNull(message = "账号ID不能为空")
    @TableField(value = "account_id", condition = EQUAL)
    @Excel(name = "账号ID")
    private Long accountId;

    /**
     * 项目租户ID
     */
    @ApiModelProperty(value = "项目租户ID")
    @NotNull(message = "项目租户ID不能为空")
    @TableField("tenant_id")
    @Excel(name = "项目租户ID")
    private Long tenantId;

    /**
     * 授权项目(authorized_projects),自建项目(created_projects)
     */
    @ApiModelProperty(value = "授权项目(authorized_projects),自建项目(created_projects)")
    @Length(max = 255, message = "授权项目(authorized_projects),自建项目(created_projects)长度不能超过255")
    @TableField(value = "management_type", condition = LIKE)
    @Excel(name = "授权项目(authorized_projects),自建项目(created_projects)")
    private String managementType;

    @ApiModelProperty(value = "项目名称")
    @TableField(exist = false)
    private String tenantName;

    @ApiModelProperty(value = "公众号名称")
    @TableField(exist = false)
    private String wxName;

    @ApiModelProperty(value = "项目图标")
    @TableField(exist = false)
    private String tenantIcon;

    @ApiModelProperty(value = "状态")
    @TableField(exist = false)
    private TenantStatusEnum tenantStatus;

    @ApiModelProperty(value = "域名")
    @TableField(exist = false)
    private String tenantDomain;

    @Builder
    public GlobalUserTenant(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser, 
                    Long accountId, Long tenantId, String managementType) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.accountId = accountId;
        this.tenantId = tenantId;
        this.managementType = managementType;
    }

}
