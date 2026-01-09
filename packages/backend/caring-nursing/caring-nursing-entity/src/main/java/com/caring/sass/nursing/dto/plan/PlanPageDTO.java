package com.caring.sass.nursing.dto.plan;

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
 * 护理计划（随访服务）
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "PlanPageDTO", description = "护理计划（随访服务）")
public class PlanPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Length(max = 100, message = "名称长度不能超过100")
    private String name;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;
    /**
     * 项目Id
     */
    @ApiModelProperty(value = "项目Id")
    private Long projectId;
    /**
     * 护理计划类型（单条 多条）
     */
    @ApiModelProperty(value = "护理计划类型（单条 多条）")
    private Integer type;
    /**
     * 是否是系统模板  0：是   1：不是
     */
    @ApiModelProperty(value = "是否是系统模板  0：是   1：不是")
    private Integer isAdminTemplate;
    /**
     * 第几天开始执行
     */
    @ApiModelProperty(value = "第几天开始执行")
    private Integer execute;
    /**
     * 有效时间（0：长期  N：具体天数）
     */
    @ApiModelProperty(value = "有效时间（0：长期  N：具体天数）")
    private Integer effectiveTime;
    /**
     * 护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）
     */
    @ApiModelProperty(value = "护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）")
    private Integer planType;

}
