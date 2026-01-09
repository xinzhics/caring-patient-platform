package com.caring.sass.nursing.entity.drugs;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * <p>
 * 实体类
 * 患者管理-用药预警-预警结果表
 * </p>
 *
 * @author yangshuai
 * @since 2022-06-22
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_drugs_result_information")
@ApiModel(value = "DrugsResultInformation", description = "患者管理-用药预警-预警结果表")
@AllArgsConstructor
public class DrugsResultInformation extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    @TableField("create_user")
    @Excel(name = "创建人id")
    private Long createUser;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新人id
     */
    @ApiModelProperty(value = "更新人id")
    @TableField("update_user")
    @Excel(name = "更新人id")
    private Long updateUser;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    @Excel(name = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 预警类型 (1 余药不足， 2 购药逾期)
     */
    @ApiModelProperty(value = "预警类型 (1 余药不足， 2 购药逾期)")
    @TableField("warning_type")
    @Excel(name = "预警类型 (1 余药不足， 2 购药逾期)")
    private Integer warningType;

    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码")
    @TableField("tenant_code")
    @Excel(name = "租户编码")
    private String tenantCode;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    @Length(max = 40, message = "患者名称长度不能超过40")
    @TableField(value = "patient_name", condition = LIKE)
    @Excel(name = "患者名称")
    private String patientName;

    /**
     * 患者头像
     */
    @ApiModelProperty(value = "患者头像")
    @Length(max = 255, message = "患者头像长度不能超过255")
    @TableField(value = "avatar", condition = LIKE)
    @Excel(name = "患者头像")
    private String avatar;

    /**
     * 患者所属医生ID
     */
    @ApiModelProperty(value = "患者所属医生ID")
    @TableField("doctor_id")
    @Excel(name = "患者所属医生ID")
    private Long doctorId;

    /**
     * 患者所属医生名称
     */
    @ApiModelProperty(value = "患者所属医生名称")
    @Length(max = 40, message = "患者所属医生名称长度不能超过40")
    @TableField(value = "doctor_name", condition = LIKE)
    @Excel(name = "患者所属医生名称")
    private String doctorName;

    /**
     * 剩余药量可用（逾期）天数
     */
    @ApiModelProperty(value = "剩余药量可用（逾期）天数")
    @TableField("drugs_available_day")
    @Excel(name = "剩余药量可用（逾期）天数")
    private Integer drugsAvailableDay;

    /**
     * 药量用完时间
     */
    @ApiModelProperty(value = "药量用完时间")
    @TableField("drugs_end_time")
    @Excel(name = "药量用完时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDate drugsEndTime;

    /**
     * 预警条件ID
     */
    @ApiModelProperty(value = "预警条件ID")
    @TableField("drugs_condition_id")
    @Excel(name = "预警条件ID")
    private Long drugsConditionId;

    /**
     * 药品ID
     */
    @ApiModelProperty(value = "药品ID")
    @TableField("drugs_id")
    @Excel(name = "药品ID")
    private Long drugsId;

    /**
     * 药品名称
     */
    @ApiModelProperty(value = "药品名称")
    @Length(max = 200, message = "药品名称长度不能超过200")
    @TableField(value = "drugs_name", condition = LIKE)
    @Excel(name = "药品名称")
    private String drugsName;

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    @Length(max = 2000, message = "规格长度不能超过2000")
    @TableField(value = "spec", condition = LIKE)
    @Excel(name = "规格")
    private String spec;

    /**
     * 厂商
     */
    @ApiModelProperty(value = "厂商")
    @Length(max = 100, message = "厂商长度不能超过100")
    @TableField(value = "manufactor", condition = LIKE)
    @Excel(name = "厂商")
    private String manufactor;

    /**
     * 提醒时间
     */
    @ApiModelProperty(value = "提醒时间")
    @TableField("reminder_time")
    @Excel(name = "提醒时间")
    private Long reminderTime;

    /**
     * 购药地址
     */
    @ApiModelProperty(value = "购药地址")
    @Length(max = 1000, message = "购药地址长度不能超过1000")
    @TableField(value = "buying_medicine_url", condition = LIKE)
    @Excel(name = "购药地址")
    private String buyingMedicineUrl;

    /**
     * 模板消息ID
     */
    @ApiModelProperty(value = "模板消息ID")
    @TableField("template_msg_id")
    @Excel(name = "模板消息ID")
    private Long templateMsgId;

    /**
     * 模板消息推送状态
     */
    @ApiModelProperty(value = "模板消息推送状态")
    @TableField("template_msg_send_status")
    @Excel(name = "模板消息推送状态")
    private Integer templateMsgSendStatus;

    /**
     * 医助id
     */
    @ApiModelProperty(value = "医助id")
    @TableField("nursing_id")
    @Excel(name = "医助id")
    private Long nursingId;

    @Builder
    public DrugsResultInformation(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime,
                                  Integer warningType, Long patientId, String patientName, String avatar, Long doctorId,
                                  String doctorName, Integer drugsAvailableDay, LocalDate drugsEndTime, Long drugsConditionId, Long drugsId, String drugsName,
                                  String spec, String manufactor, Long reminderTime, String buyingMedicineUrl, Long templateMsgId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.warningType = warningType;
        this.patientId = patientId;
        this.patientName = patientName;
        this.avatar = avatar;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.drugsAvailableDay = drugsAvailableDay;
        this.drugsEndTime = drugsEndTime;
        this.drugsConditionId = drugsConditionId;
        this.drugsId = drugsId;
        this.drugsName = drugsName;
        this.spec = spec;
        this.manufactor = manufactor;
        this.reminderTime = reminderTime;
        this.buyingMedicineUrl = buyingMedicineUrl;
        this.templateMsgId = templateMsgId;
    }

}
