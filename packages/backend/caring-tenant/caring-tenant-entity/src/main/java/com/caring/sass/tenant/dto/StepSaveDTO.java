package com.caring.sass.tenant.dto;

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
 * 项目配置步骤信息
 * </p>
 *
 * @author leizhi
 * @since 2020-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "StepSaveDTO", description = "项目配置步骤信息")
public class StepSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前顶级配置步骤，默认0即未开始
     */
    @ApiModelProperty(value = "当前顶级配置步骤，默认0即未开始")
    @NotNull(message = "当前顶级配置步骤，默认0即未开始不能为空")
    private Integer curTopStep;
    /**
     * 当前顶级步骤的子步骤，默认0即未开始
     */
    @ApiModelProperty(value = "当前顶级步骤的子步骤，默认0即未开始")
    @NotNull(message = "当前顶级步骤的子步骤，默认0即未开始不能为空")
    private Integer curChildStep;

}
