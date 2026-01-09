package com.caring.sass.nursing.dto.plan;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ReminderLogPageDTO", description = "推送记录")
@AllArgsConstructor
public class ReminderLogPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者Id
     */
    @ApiModelProperty(value = "患者Id")
    private Long patientId;

    @ApiModelProperty(value = "患者的机构码")
    private String classCode;

    @ApiModelProperty(value = "患者的机构ID")
    private Long orgId;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "医助ID")
    private Long nursingId;

    /**
     * 推送状态
     */
    @ApiModelProperty(value = "推送状态")
    private Integer status;

    @ApiModelProperty(value = "护理计划")
    private Long planId;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private Integer type;
    /**
     * 任务Id
     */
    @ApiModelProperty(value = "任务Id")
    private String workId;
    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    private String comment;

    @ApiModelProperty(value = "用户打开了此消息， 比如打开表单")
    private Integer openThisMessage;
    /**
     * 完成打卡
     */
    @ApiModelProperty(value = "完成打卡")
    private Integer finishThisCheckIn;

}
