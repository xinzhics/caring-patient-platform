package com.caring.sass.nursing.entity.follow;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_function_configuration")
@ApiModel(value = "FunctionConfiguration", description = "功能配置")
@AllArgsConstructor
public class FunctionConfiguration extends Entity<Long> {

    @ApiModelProperty("计划的ID")
    @TableField("plan_id")
    private Long planId;

    @ApiModelProperty("计划类型")
    @TableField("plan_type")
    private Integer planType;

    @ApiModelProperty("功能名称")
    @TableField("function_name")
    private String functionName;

    @ApiModelProperty("功能类型")
    @TableField(value = "function_type", condition = EQUAL)
    private PlanFunctionTypeEnum functionType;

    @ApiModelProperty("功能的状态 1, 开启， 0关闭")
    @TableField(value = "function_status", condition = EQUAL)
    private Integer functionStatus;

    @ApiModelProperty("是否有推送配置")
    @TableField(value = "has_push_config", condition = EQUAL)
    private Integer hasPushConfig;

    @ApiModelProperty("是否有功能配置")
    @TableField(value = "has_function_config", condition = EQUAL)
    private Integer hasFunctionConfig;

    @ApiModelProperty("caring模板ID")
    @TableField(value = "caring_template_id", condition = EQUAL)
    private Long caringTemplateId;

    @ApiModelProperty("微信模板ID")
    @TableField(value = "wei_xin_template_id", condition = EQUAL)
    private String weiXinTemplateId;

    @ApiModelProperty("是否选择微信模板")
    @TableField(value = "has_wei_xin_template", condition = EQUAL)
    private Integer hasWeiXinTemplate;




}
