package com.caring.sass.nursing.dto.drugs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 患者管理-用药预警-预警处理历史表
 * </p>
 *
 * @author yangshuai
 * @since 2022-06-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DrugsResultHandleHisSaveDTO", description = "患者管理-用药预警-预警处理历史表")
public class DrugsResultHandleHisSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预警类型 (1 余药不足， 2 购药逾期)
     */
    @ApiModelProperty(value = "预警类型 (1 余药不足， 2 购药逾期)")
    private Integer warningType;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Long patientId;
    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    @Length(max = 40, message = "患者名称长度不能超过40")
    private String patientName;
    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    @Length(max = 255, message = "患者头像长度不能超过255")
    private String avatar;
    /**
     * 患者所属医生ID
     */
    @ApiModelProperty(value = "患者所属医生ID")
    private Long doctorId;
    /**
     * 患者所属医生名称
     */
    @ApiModelProperty(value = "患者所属医生名称")
    @Length(max = 40, message = "患者所属医生名称长度不能超过40")
    private String doctorName;
    /**
     * 异常处理时间
     */
    @ApiModelProperty(value = "异常处理时间")
    private LocalDateTime handleTime;
    /**
     * 异常处理人id
     */
    @ApiModelProperty(value = "异常处理人id")
    private Long handleUser;
    /**
     * 清理状态 (1 未清理， 2 已清理)
     */
    @ApiModelProperty(value = "清理状态 (1 未清理， 2 已清理)")
    private Integer clearStatus;
    /**
     * 预警条件ID
     */
    @ApiModelProperty(value = "预警条件ID")
    private Long drugsConditionId;
    /**
     * 药品ID
     */
    @ApiModelProperty(value = "药品ID")
    private Long drugsId;
    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 200, message = "药品名称长度不能超过200")
    private String drugsName;
    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    @Length(max = 2000, message = "规格长度不能超过2000")
    private String spec;
    /**
     * 厂商
     */
    @ApiModelProperty(value = "厂商")
    @Length(max = 100, message = "厂商长度不能超过100")
    private String manufactor;
    /**
     * 提醒时间
     */
    @ApiModelProperty(value = "提醒时间")
    private Long reminderTime;
    /**
     * 购药地址
     */
    @ApiModelProperty(value = "购药地址")
    @Length(max = 1000, message = "购药地址长度不能超过1000")
    private String buyingMedicineUrl;
    /**
     * 模板消息ID
     */
    @ApiModelProperty(value = "模板消息ID")
    private Long templateMsgId;

}
