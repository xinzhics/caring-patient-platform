package com.caring.sass.tenant.dto;

import java.time.LocalDateTime;
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
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "TenantConfigurationSchedulePageDTO", description = "项目配置进度表")
public class TenantConfigurationSchedulePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 基础设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "基础设置状态 1 已配置。 0未配置")
    private Integer baseSetStatus;
    /**
     * 公众号设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "公众号设置状态 1 已配置。 0未配置")
    private Integer officialAccountStatus;
    /**
     * 功能设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "功能设置状态 1 已配置。 0未配置")
    private Integer functionSetStatus;
    /**
     * 界面设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "界面设置状态 1 已配置。 0未配置")
    private Integer interfaceSetStatus;
    /**
     * 运营设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "运营设置状态 1 已配置。 0未配置")
    private Integer operateSetStatus;
    /**
     * APP设置状态 1 已配置。 0未配置
     */
    @ApiModelProperty(value = "APP设置状态 1 已配置。 0未配置")
    private Integer appSetStatus;

}
