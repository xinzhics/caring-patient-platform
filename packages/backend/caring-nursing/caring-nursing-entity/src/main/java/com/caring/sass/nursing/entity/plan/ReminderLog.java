package com.caring.sass.nursing.entity.plan;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE_RIGHT;

/**
 * @ClassName ReminderLog
 * @Description
 * @Author yangShuai
 * @Date 2020/10/27 11:54
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_plan_reminder_log")
@ApiModel(value = "ReminderLog", description = "推送记录")
@AllArgsConstructor
public class ReminderLog extends Entity<Long> {

    /**
     * 患者Id
     */
    @ApiModelProperty(value = "患者Id")
    @TableField(value = "patient_Id", condition = EQUAL)
    private Long patientId;

    @ApiModelProperty(value = "患者的机构码")
    @TableField(value = "class_code", condition = LIKE_RIGHT)
    private String classCode;

    @ApiModelProperty(value = "患者的机构ID")
    @TableField(value = "org_id", condition = EQUAL)
    private Long orgId;

    @ApiModelProperty(value = "医生ID")
    @TableField(value = "doctor_id", condition = EQUAL)
    private Long doctorId;

    @ApiModelProperty(value = "医助ID")
    @TableField(value = "nursing_id", condition = EQUAL)
    private Long nursingId;

    /**
     * 推送状态
     */
    @ApiModelProperty(value = "推送状态 -4 正在处理中， -3 推送失败,  -2 等待微信结果， -1 等待推送, 0 推送失败, 1 推送成功")
    @TableField(value = "status_", condition = EQUAL)
    private Integer status;

    @ApiModelProperty(value = "推送失败原因")
    @TableField(value = "error_message")
    private String errorMessage;

    @ApiModelProperty(value = "推送时间 精确到分钟")
    @TableField(value = "wait_push_time")
    private LocalDateTime waitPushTime;


    @ApiModelProperty(value = "推送时间 成功时间")
    @TableField(value = "push_time_success")
    private LocalDateTime pushTimeSuccess;


    @ApiModelProperty(value = "护理计划")
    @TableField(value = "plan_id", condition = EQUAL)
    private Long planId;

    @ApiModelProperty(value = "护理计划")
    @TableField(value = "plan_detail_id", condition = EQUAL)
    private Long planDetailId;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @TableField(value = "type_", condition = EQUAL)
    private Integer type;
    /**
     * 任务Id
     */
    @ApiModelProperty(value = "任务Id")
    @TableField(value = "work_id", condition = EQUAL)
    private String workId;
    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    @TableField(value = "comment_", condition = EQUAL)
    private String comment;

    @ApiModelProperty(value = "用户打开了此消息， 比如打开表单")
    @TableField(value = "open_this_message", condition = EQUAL)
    private Integer openThisMessage;
    /**
     * 完成打卡
     */
    @ApiModelProperty(value = "完成打卡")
    @TableField(value = "finish_this_check_in", condition = EQUAL)
    private Integer finishThisCheckIn;

}
