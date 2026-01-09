package com.caring.sass.nursing.entity.unfinished;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 未完成推送的患者记录
 * </p>
 *
 * @author 杨帅
 * @since 2024-05-27
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_unfinished_patient_record")
@ApiModel(value = "UnfinishedPatientRecord", description = "未完成推送的患者记录")
@AllArgsConstructor
public class UnfinishedPatientRecord extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    @TableField("patient_id")
    @Excel(name = "患者ID")
    private Long patientId;

    /**
     * 患者所属医助ID
     */
    @ApiModelProperty(value = "患者所属医助ID")
    @TableField("nursing_id")
    @Excel(name = "患者所属医助ID")
    private Long nursingId;

    /**
     * 患者所属医生ID
     */
    @ApiModelProperty(value = "患者所属医生ID")
    @NotNull(message = "患者所属医生ID不能为空")
    @TableField("doctor_id")
    @Excel(name = "患者所属医生ID")
    private Long doctorId;

    /**
     */
    @ApiModelProperty(value = "查看状态 (0 未查看， 1 已查看)")
    @TableField("see_status")
    private Integer seeStatus;

    @ApiModelProperty(value = "查看时间")
    @TableField("see_time")
    private LocalDateTime seeTime;

    /**
     */
    @ApiModelProperty(value = "处理状态 (0 未处理， 1 已处理)")
    @TableField("handle_status")
    private Integer handleStatus;

    /**
     */
    @ApiModelProperty(value = "清理状态 (0 未清理， 1 已清理)")
    @TableField("clear_status")
    private Integer clearStatus;

    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    @TableField("handle_time")
    @Excel(name = "处理时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime handleTime;

    /**
     * 处理人id
     */
    @ApiModelProperty(value = "处理人id")
    @TableField("handle_user")
    @Excel(name = "处理人id")
    private Long handleUser;

    /**
     * 未完成表单设置ID
     */
    @ApiModelProperty(value = "未完成表单设置ID")
    @TableField("unfinished_form_setting_id")
    @Excel(name = "未完成表单设置ID")
    private Long unfinishedFormSettingId;

    /**
     * 计划ID
     */
    @ApiModelProperty(value = "计划ID")
    @TableField("plan_id")
    @Excel(name = "计划ID")
    private Long planId;

    /**
     * 表单ID
     */
    @ApiModelProperty(value = "表单ID")
    @TableField("form_id")
    @Excel(name = "表单ID")
    private Long formId;

    /**
     * 1表单，2用药日历
     */
    @ApiModelProperty(value = "1表单，2用药日历")
    @TableField("medicine_or_form")
    @Excel(name = "1表单，2用药日历")
    private Integer medicineOrForm;

    /**
     * 推送提醒的消息ID
     */
    @ApiModelProperty(value = "推送提醒的消息ID")
    @TableField("remind_message_id")
    @Excel(name = "推送提醒的消息ID")
    private Long remindMessageId;

    /**
     * 推送提醒的时间
     */
    @ApiModelProperty(value = "推送提醒的时间")
    @TableField("remind_time")
    @Excel(name = "推送提醒的时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime remindTime;


    @ApiModelProperty(value = "移除标记： 0 未移除， 1 移除")
    @TableField("delete_flag")
    private Integer deleteFlag;


    @ApiModelProperty(value = "计划类型")
    @TableField(value = "plan_type", condition = EQUAL)
    private Integer planType;



}
