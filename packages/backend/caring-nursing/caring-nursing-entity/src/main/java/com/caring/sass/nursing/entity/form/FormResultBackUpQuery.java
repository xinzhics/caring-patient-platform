package com.caring.sass.nursing.entity.form;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * TODO:: 加密后删除
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_custom_form_result_back_up")
@ApiModel(value = "FormResultBackUp", description = "表单填写修改备份表")
@AllArgsConstructor
public class FormResultBackUpQuery extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 表单Id
     */
    @ApiModelProperty(value = "表单结果Id")
    @NotNull(message = "表单结果Id不能为空")
    @TableField("form_result_id")
    private Long formResultId;

    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    private Long patientId;

    /**
     * 填写人Id
     */
    @ApiModelProperty(value = "修改人ID")
    @TableField("update_user_id")
    private Long updateUserId;


    @ApiModelProperty(value = "修改人名称")
    @TableField(exist = false)
    private String updateUserName;

    /**
     * 用户角色
     */
    @ApiModelProperty(value = "修改人角色")
    @TableField("user_type")
    private String userType;

    /**
     * 填写结果数据
     */
    @ApiModelProperty(value = "填写结果数据")
    @TableField("json_content")
    @Excel(name = "填写结果数据")
    private String jsonContent;


    @TableField("tenant_code")
    private String tenantCode;

    @ApiModelProperty(value = "数据是否加密")
    @TableField("encrypted")
    private Integer encrypted;

}
