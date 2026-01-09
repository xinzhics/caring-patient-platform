package com.caring.sass.msgs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 患者系统消息
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-21
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_patient_system_message")
@ApiModel(value = "MsgPatientSystemMessage", description = "患者系统消息")
@AllArgsConstructor
public class MsgPatientSystemMessage extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)
     */
    @ApiModelProperty(value = "功能类型(健康日志，复查提醒，病例讨论，转诊管理，预约管理，用药提醒，学习计划，指标监测，随访提醒, 互动消息)")
    @Length(max = 255, message = "功能类型(健康日志，复查提醒，病例讨论，转诊管理，预约管理，用药提醒，学习计划，指标监测，随访提醒, 互动消息)长度不能超过255")
    @TableField(value = "function_type", condition = EQUAL)
    @Excel(name = "功能类型(健康日志，复查提醒，病例讨论，转诊管理，预约管理，用药提醒，学习计划，指标监测，自定义随访)")
    private String functionType;


    @ApiModelProperty(value = "互动功能类型(健康日志，疾病信息，检查报告，我的药箱，指标监测，随访表单)")
    @Length(max = 255, message = "互动功能类型长度不能超过255")
    @TableField(value = "interactive_function_type", condition = EQUAL)
    private String interactiveFunctionType;

    @ApiModelProperty(value = "患者是否可以看到（互动消息） 医生评论或看过后，消息变为1 患者可见，默认患者可见")
    @TableField(value = "patient_can_see", condition = EQUAL)
    private Integer patientCanSee;


    @ApiModelProperty(value = "药箱动作")
    @Length(max = 50, message = "add, updated")
    @TableField(value = "medicine_operation_type", condition = EQUAL)
    private String medicineOperationType;


    @ApiModelProperty(value = "功能类型(健康日志，复查提醒，病例讨论，转诊管理，预约管理，用药提醒，学习计划，指标监测，随访提醒, 互动消息)")
    @TableField(exist = false)
    private String functionName;

    @ApiModelProperty(value = "业务ID(随访计划ID，病例讨论的ID，预约记录id,表单结果ID，患者药品ID，)")
    @TableField(value = "business_id", condition = EQUAL)
    private Long businessId;

    /**
     * 点击消息跳转的地址
     */
    @ApiModelProperty(value = "点击消息跳转的地址")
    @Length(max = 255, message = "点击消息跳转的地址长度不能超过255")
    @TableField(value = "jump_url", condition = LIKE)
    @Excel(name = "点击消息跳转的地址")
    private String jumpUrl;

    /**
     * 跳转类型(网页链接)
     */
    @ApiModelProperty(value = "跳转类型(网页链接)")
    @Length(max = 255, message = "跳转类型(网页链接)长度不能超过255")
    @TableField(value = "jump_type", condition = EQUAL)
    @Excel(name = "跳转类型(网页链接)")
    private String jumpType;

    @ApiModelProperty(value = "医生ID")
    @TableField("doctor_id")
    private Long doctorId;

    @ApiModelProperty(value = "医生头像")
    @TableField(exist = false)
    private String doctorAvatar;

    @ApiModelProperty(value = "医生姓名")
    @TableField("doctor_name")
    private String doctorName;

    /**
     * 接收患者ID
     */
    @ApiModelProperty(value = "接收患者ID")
    @NotNull(message = "接收患者ID不能为空")
    @TableField("patient_id")
    @Excel(name = "接收患者ID")
    private Long patientId;


    @ApiModelProperty(value = "接收患者openID")
    @TableField("patient_open_id")
    private String patientOpenId;

    /**
     * 状态 1 已读， 0 未读
     */
    @ApiModelProperty(value = "患者看过状态 1 已读， 0 未读")
    @TableField("read_status")
    @Excel(name = "状态 1 已读， 0 未读", replace = {"是_true", "否_false", "_null"})
    private Integer readStatus;


    @ApiModelProperty(value = "医生看过状态 1 已读， 0 未读")
    @TableField("doctor_read_status")
    private Integer doctorReadStatus;

    @ApiModelProperty(value = "医生查看的时间")
    @TableField("doctor_read_time")
    private LocalDateTime doctorReadTime;


    @ApiModelProperty(value = "患者看过医生的已读状态 1 已读， 0 未读")
    @TableField("patient_read_doctor_status")
    private Integer patientReadDoctorStatus;


    @ApiModelProperty(value = "医生评论状态  1 评论， 0 未评论")
    @TableField("doctor_comment_status")
    private Integer doctorCommentStatus;


    @ApiModelProperty(value = "患者看过医生的评论状态 1 已读， 0 未读")
    @TableField("patient_read_doctor_comment_status")
    private Integer patientReadDoctorCommentStatus;


    /**
     * 消息推送时间
     */
    @ApiModelProperty(value = "消息推送时间")
    @TableField("push_time")
    @Excel(name = "消息推送时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime pushTime;

    /**
     * 消息推送人
     */
    @ApiModelProperty(value = "消息推送人")
    @Length(max = 50, message = "消息推送人长度不能超过50")
    @TableField(value = "push_person", condition = LIKE)
    @Excel(name = "消息推送人")
    private String pushPerson;

    /**
     * 系统消息推送的主要文本内容
     */
    @ApiModelProperty(value = "系统消息推送的主要文本内容")
    @Length(max = 300, message = "系统消息推送的主要文本内容长度不能超过300")
    @TableField(value = "push_content", condition = LIKE)
    @Excel(name = "系统消息推送的主要文本内容")
    private String pushContent;

    @ApiModelProperty(value = "病例讨论状态(0 开始, 1 进行中, 2结束 )")
    @TableField(value = "case_discussion_status")
    private Integer caseDiscussionStatus;

    @ApiModelProperty(value = "计划名称")
    @TableField(value = "plan_name")
    private String planName;


    @ApiModelProperty(value = "预约状态")
    @TableField(value = "appointment_status")
    private Integer appointmentStatus;


    @ApiModelProperty(value = "预约时间")
    @TableField(value = "appointment_time")
    private LocalDateTime appointmentTime;


    @ApiModelProperty(value = "医生评论的内容")
    @TableField(exist = false)
    private PatientSystemMessageRemark doctorCommentContent;

}
