package com.caring.sass.tenant.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
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
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 项目配置进度表
 * </p>
 *
 * @author 杨帅
 * @since 2023-07-18
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant_configuration_schedule")
@ApiModel(value = "TenantConfigurationSchedule", description = "项目配置进度表")
@AllArgsConstructor
public class TenantConfigurationSchedule extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 基础设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "基础设置状态 1 已配置。 0未配置")
    @TableField("base_set_status")
    @Excel(name = "基础设置状态 1 已配置。 0未配置")
    private Integer baseSetStatus;

    /**
     * 公众号设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "公众号设置状态 1 已配置。 0未配置")
    @TableField("official_account_status")
    @Excel(name = "公众号设置状态 1 已配置。 0未配置")
    private Integer officialAccountStatus;

    /**
     * 功能设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "功能设置状态 1 已配置。 0未配置")
    @TableField("function_set_status")
    @Excel(name = "功能设置状态 1 已配置。 0未配置")
    private Integer functionSetStatus;

    /**
     * 界面设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "界面设置状态 1 已配置。 0未配置")
    @TableField("interface_set_status")
    @Excel(name = "界面设置状态 1 已配置。 0未配置")
    private Integer interfaceSetStatus;

    /**
     * 运营设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "运营设置状态 1 已配置。 0未配置")
    @TableField("operate_set_status")
    @Excel(name = "运营设置状态 1 已配置。 0未配置")
    private Integer operateSetStatus;

    /**
     * APP设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "APP设置状态 1 已配置。 0未配置")
    @TableField("app_set_status")
    @Excel(name = "APP设置状态 1 已配置。 0未配置")
    private Integer appSetStatus;


    @Builder
    public TenantConfigurationSchedule(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    Integer baseSetStatus, Integer officialAccountTatus, Integer functionSetStatus, Integer interfaceSetStatus, Integer operateSetStatus, Integer appSetStatus) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.baseSetStatus = baseSetStatus;
        this.officialAccountStatus = officialAccountTatus;
        this.functionSetStatus = functionSetStatus;
        this.interfaceSetStatus = interfaceSetStatus;
        this.operateSetStatus = operateSetStatus;
        this.appSetStatus = appSetStatus;
    }

    public static void init(TenantConfigurationSchedule schedule, int status) {

        schedule.baseSetStatus = status;
        schedule.officialAccountStatus = status;
        schedule.functionSetStatus = status;
        schedule.interfaceSetStatus = status;
        schedule.operateSetStatus = status;
        schedule.appSetStatus = status;

    }

    public static void init(TenantConfigurationSchedule schedule) {

        init(schedule, 0);

    }
}
