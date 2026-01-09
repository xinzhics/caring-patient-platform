package com.caring.sass.sms.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.sms.enumeration.BusinessReminderType;
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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessReminderLogSaveDTO", description = "")
public class BusinessReminderLogSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "患者ID")
    private Long patientId;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "项目的疾病类型")
    private String diseasesType;

    /**
     * 推送状态 0 未推送， 1 已推送 2 推送失败
     */
    @ApiModelProperty(value = "推送状态 0 未推送， 1 已推送 2 推送失败")
    private Integer status;

    /**
     * 推送类型
     */
    @ApiModelProperty(value = "推送类型")
    private BusinessReminderType type;
    /**
     * 疾病讨论组id
     */
    @ApiModelProperty(value = "疾病讨论组id")
    private Long groupId;


    @ApiModelProperty(value = "短信参数")
    @Length(max = 65535, message = "长度不能超过65,535")
    private String queryParams;
    /**
     * 打开状态
     */
    @ApiModelProperty(value = "打开状态")
    private Integer finishThisCheckIn;
    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Long doctorId;
    /**
     * 专员ID
     */
    @ApiModelProperty(value = "专员ID")
    private Long nursingId;
    /**
     * 用户打开了此消息，比如打开表单
     */
    @ApiModelProperty(value = "用户打开了此消息，比如打开表单")
    private Integer openThisMessage;
    /**
     * 失败原因
     */
    @ApiModelProperty(value = "失败原因")
    @Length(max = 65535, message = "失败原因长度不能超过65,535")
    private String errorMessage;


    @ApiModelProperty(value = "租户编码")
    private String tenantCode;

    @ApiModelProperty(value = "业务跳转链接")
    private String wechatRedirectUrl;
}
