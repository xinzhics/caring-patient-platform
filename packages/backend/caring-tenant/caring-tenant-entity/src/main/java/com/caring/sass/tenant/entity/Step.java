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
 * 项目配置步骤信息
 * </p>
 *
 * @author leizhi
 * @since 2020-11-03
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("d_tenant_step")
@ApiModel(value = "Step", description = "项目配置步骤信息")
@AllArgsConstructor
public class Step extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 当前顶级配置步骤，默认0即未开始
     */
    @ApiModelProperty(value = "当前顶级配置步骤，默认0即未开始")
    @NotNull(message = "当前顶级配置步骤，默认0即未开始不能为空")
    @TableField("cur_top_step")
    @Excel(name = "当前顶级配置步骤，默认0即未开始")
    private Integer curTopStep;

    /**
     * 当前顶级步骤的子步骤，默认0即未开始
     */
    @ApiModelProperty(value = "当前顶级步骤的子步骤，默认0即未开始")
    @NotNull(message = "当前顶级步骤的子步骤，默认0即未开始不能为空")
    @TableField("cur_child_step")
    @Excel(name = "当前顶级步骤的子步骤，默认0即未开始")
    private Integer curChildStep;


}
