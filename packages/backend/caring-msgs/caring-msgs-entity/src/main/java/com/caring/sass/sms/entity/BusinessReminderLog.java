package com.caring.sass.sms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.sms.enumeration.BusinessReminderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 
 * </p>
 *
 * @author 杨帅
 * @since 2025-03-17
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sms_business_reminder_log")
@ApiModel(value = "BusinessReminderLog", description = "短信提醒记录")
@AllArgsConstructor
public class BusinessReminderLog extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "患者Id")
    @TableField(value = "patient_Id", condition = EQUAL)
    private Long patientId;


    @ApiModelProperty(value = "手机号")
    @TableField(value = "mobile", condition = EQUAL)
    private String mobile;


    /**
     * 推送状态 0 未推送， 1 已推送 2 推送失败
     */
    @ApiModelProperty(value = "推送状态 0 未推送， 1 已推送 2 推送失败")
    @TableField("status_")
    @Excel(name = "推送状态 0 未推送， 1 已推送 2 推送失败")
    private Integer status;

    /**
     * 推送类型
     */
    @ApiModelProperty(value = "推送类型")
    @Length(max = 50, message = "推送类型长度不能超过50")
    @TableField(value = "type_", condition = LIKE)
    @Excel(name = "推送类型")
    private BusinessReminderType type;

    /**
     * 疾病讨论组id
     */
    @ApiModelProperty(value = "疾病讨论组id")
    @TableField("group_id")
    @Excel(name = "疾病讨论组id")
    private Long groupId;

    @ApiModelProperty(value = "短信参数")
    @Length(max = 65535, message = "长度不能超过65535")
    @TableField("query_params")
    private String queryParams;

    /**
     * 打开状态
     */
    @ApiModelProperty(value = "打开状态")
    @TableField("finish_this_check_in")
    @Excel(name = "打开状态")
    private Integer finishThisCheckIn;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @TableField("doctor_id")
    @Excel(name = "医生ID")
    private Long doctorId;

    /**
     * 专员ID
     */
    @ApiModelProperty(value = "专员ID")
    @TableField("nursing_id")
    @Excel(name = "专员ID")
    private Long nursingId;

    /**
     * 用户打开了此消息，比如打开表单
     */
    @ApiModelProperty(value = "用户打开了此消息，比如打开表单")
    @TableField("open_this_message")
    @Excel(name = "用户打开了此消息，比如打开表单")
    private Integer openThisMessage;

    /**
     * 失败原因
     */
    @ApiModelProperty(value = "失败原因")
    @Length(max = 65535, message = "失败原因长度不能超过65535")
    @TableField("error_message")
    @Excel(name = "失败原因")
    private String errorMessage;

    /**
     * 短信模版ID
     */
    @ApiModelProperty(value = "短信模版ID")
    @Length(max = 50, message = "短信模版ID长度不能超过50")
    @TableField(value = "template_id", condition = LIKE)
    @Excel(name = "短信模版ID")
    private String templateId;

    @ApiModelProperty(value = "短链接标识")
    @TableField(value = "sms_param_id", condition = EQUAL)
    private String smsParamId;

    @ApiModelProperty(value = "租户")
    @TableField(value = "tenant_code", condition = EQUAL)
    private String tenantCode;

    @ApiModelProperty(value = "业务跳转链接")
    @TableField(value = "wechat_redirect_url", condition = EQUAL)
    private String wechatRedirectUrl;



}
