package com.caring.sass.cms.entity;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * banner
 * </p>
 *
 * @author 杨帅
 * @since 2023-12-08
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_banner_switch")
@ApiModel(value = "BannerSwitch", description = "banner")
@AllArgsConstructor
public class BannerSwitch extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户角色
     */
    @ApiModelProperty(value = "用户角色")
    @Length(max = 50, message = "用户角色长度不能超过50")
    @TableField(value = "user_role", condition = EQUAL)
    @Excel(name = "用户角色")
    private String userRole;

    /**
     * 开关状态
     */
    @ApiModelProperty(value = "开关状态")
    @TableField("switch_status")
    @Excel(name = "开关状态")
    private Integer switchStatus;

    @TableField(exist = false)
    private String tenantCode;



}
