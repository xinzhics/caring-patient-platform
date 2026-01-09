package com.caring.sass.nursing.entity.plan;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_plan")
@ApiModel(value = "Plan", description = "护理计划（随访服务）")
@AllArgsConstructor
public class Plan extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称 必传")
    @Length(max = 100, message = "名称长度不能超过100")
    @TableField(value = "name_", condition = LIKE)
    @Excel(name = "名称")
    private String name;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态 必传 新增传0")
    @TableField("status_")
    @Excel(name = "状态")
    private Integer status;

    /**
     * 项目Id
     */
    @Deprecated
    @ApiModelProperty(value = "项目Id 不传")
    @TableField("project_id")
    @Excel(name = "项目Id")
    private Long projectId;

    @ApiModelProperty(value = "0 普通计划 1 注射计划 2 病情评估")
    @TableField("plan_model")
    @Excel(name = "计划模式")
    private Integer planModel;

    @ApiModelProperty(value = "首次提醒工单模板的类型标题")
    @TableField("remind_template_title")
    private String remindTemplateTitle;

    @ApiModelProperty(value = "超时提醒工单模板的类型标题")
    @TableField("time_out_remind_template_title")
    private String timeOutRemindTemplateTitle;

    @ApiModelProperty(value = "超时恢复提醒工单模板的类型标题")
    @TableField("time_out_recovery_remind_template_title")
    private String timeOutRecoveryRemindTemplateTitle;

    @ApiModelProperty(value = "超时填写提交的表单计划")
    @TableField("time_out_plan_id")
    private Long timeOutPlanId;

    @ApiModelProperty(value = "下次提醒间隔天数")
    @TableField("next_remind")
    private Integer nextRemind;

    @ApiModelProperty(value = "超时提醒天数")
    @TableField("time_out_remind")
    private Integer timeOutRemind;


    @ApiModelProperty(value = "超时提醒后多久恢复间隔")
    @TableField("timeout_recovery")
    private Integer timeoutRecovery;


    @ApiModelProperty(value = "超时模版")
    @TableField("time_out_remind_template")
    private Long timeOutRemindTemplate;

    /**
     * 护理计划类型（单条 多条）
     */
    @Deprecated
    @ApiModelProperty(value = "护理计划类型（单条 多条）")
    @TableField("type_")
    @Excel(name = "护理计划类型（单条 多条）")
    private Integer type;

    /**
     * 系统模板
     */
    @ApiModelProperty(value = "作为系统模板 0 否, 1 是")
    @TableField("system_template")
    @Excel(name = "系统模板")
    private Integer systemTemplate;

    /**
     * 随访计划类型  护理计划 care_plan， 监测数据 monitoring_data
     */
    @ApiModelProperty(value = "随访计划类型  护理计划 学习计划 care_plan， 监测数据 monitoring_data")
    @TableField("follow_up_plan_type")
    @Excel(name = "随访计划类型")
    private String followUpPlanType;


    @ApiModelProperty(value = "系统或其他计划  0：系统计划(复查提醒，健康日志，用药,血压血糖， 学习计划) 1：自定义其他计划 (监测计划，自定义随访)")
    @TableField("is_admin_template")
    @Excel(name = "系统或其他计划  0：系统计划   1：其他计划")
    private Integer isAdminTemplate;

    /**
     * 第几天开始执行
     */
    @ApiModelProperty(value = "第几天开始执行 必传")
    @TableField("execute_")
    @Excel(name = "第几天开始执行")
    private Integer execute;

    /**
     * 有效时间（0：长期  N：具体天数）
     */
    @ApiModelProperty(value = "有效时间（0：长期  N：具体天数）必传")
    @TableField("effective_time")
    @Excel(name = "有效时间（0：长期  N：具体天数）")
    private Integer effectiveTime;

    /**
     * 护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）
     */
    @ApiModelProperty(value = "护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱, 只有监测计划自定义随访可以不传）")
    @TableField(value = "plan_type", condition = EQUAL)
    @Excel(name = "护理计划类型（1血压监测 2血糖监测 3复查提醒 4用药提醒 5健康日志 6学习计划 7营养食谱）", replace = {"是_true", "否_false", "_null"})
    private Integer planType;

    @ApiModelProperty(value = "学习计划角色 医生 doctor, 患者 patient")
    @TableField(value = "learn_plan_role", condition = EQUAL)
    private String learnPlanRole;

    @ApiModelProperty(value = "计划图标")
    @TableField("plan_icon")
    private String planIcon;

    @ApiModelProperty(value = "计划的描述")
    @TableField("plan_desc")
    private String planDesc;

    @ApiModelProperty("触发对象的Id 可以不传")
    @TableField(exist = false)
    private Long tagId;

    @ApiModelProperty("触发对象")
    @TableField(exist = false)
    private PlanTag planTag;

    @ApiModelProperty("推送类型设置")
    @TableField(exist = false)
    private List<PlanDetailDTO> planDetailList;

    @ApiModelProperty("推送数量")
    @TableField(exist = false)
    private Integer planDetailTimeNumber;

    public Plan(String name,
                Integer type, Integer isAdminTemplate,
                Integer execute, Integer effectiveTime, Integer planType, String followUpPlanType) {
        this.name = name;
        this.status = 1;
        this.type = type;
        this.isAdminTemplate = isAdminTemplate;
        this.execute = execute;
        this.effectiveTime = effectiveTime;
        this.planType = planType;
        this.systemTemplate = 0;
        this.followUpPlanType = followUpPlanType;
    }

    @Builder
    public Plan(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String name, Integer status, Long projectId, Integer type, Integer isAdminTemplate, 
                    Integer execute, Integer effectiveTime, Integer planType) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.name = name;
        this.status = status;
        this.projectId = projectId;
        this.type = type;
        this.isAdminTemplate = isAdminTemplate;
        this.execute = execute;
        this.effectiveTime = effectiveTime;
        this.planType = planType;
    }

}
