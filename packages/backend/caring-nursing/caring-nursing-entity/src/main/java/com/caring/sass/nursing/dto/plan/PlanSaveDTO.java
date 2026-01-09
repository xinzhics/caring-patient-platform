package com.caring.sass.nursing.dto.plan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

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
@ApiModel(value = "PlanSaveDTO", description = "护理计划（随访服务）")
public class PlanSaveDTO implements Serializable {

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

    @ApiModelProperty(value = "0 普通计划 1 注射计划")
    private Integer planModel;

    @ApiModelProperty(value = "下次提醒间隔天数")
    private Integer nextRemind;

    @ApiModelProperty(value = "超时提醒天数")
    private Integer timeOutRemind;

    @ApiModelProperty(value = "超时模版")
    private Long timeOutRemindTemplate;

    /**
     * 系统模板
     */
    @ApiModelProperty(value = "系统模板 0 否, 1 是", required = true)
    @NonNull
    private Integer systemTemplate;

    /**
     * 随访计划类型  护理计划 care_plan， 监测数据 monitoring_data
     */
    @ApiModelProperty(value = "随访计划类型  护理计划 care_plan， 监测数据 monitoring_data", required = true)
    @NonNull
    private String followUpPlanType;

    /**
     * 护理计划类型（单条 多条）
     */
    @ApiModelProperty(value = "护理计划类型（单条 多条）")
    private Integer type;
    /**
     * 系统或其他计划  0：系统计划   1：其他计划
     */
    @ApiModelProperty(value = "系统或其他计划  0：系统计划   1：其他计划")
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

    @ApiModelProperty(value = "计划图标")
    private String planIcon;

    @ApiModelProperty(value = "计划的描述")
    private String planDesc;


}
