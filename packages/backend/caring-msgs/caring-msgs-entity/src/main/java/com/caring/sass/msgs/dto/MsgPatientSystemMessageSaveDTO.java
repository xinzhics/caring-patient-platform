package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "MsgPatientSystemMessageSaveDTO", description = "患者系统消息")
public class MsgPatientSystemMessageSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)
     */
    @ApiModelProperty(value = "功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)")
    @Length(max = 255, message = "功能类型(健康日志，复查提醒，病例讨论，转诊，预约，用药提醒，学习计划，指标监测，自定义随访)长度不能超过255")
    private String functionType;

    @ApiModelProperty(value = "业务ID(随访计划ID，病例讨论的ID，预约记录id)")
    private Long businessId;


    @ApiModelProperty(value = "互动功能类型(健康日志，疾病信息，检查报告，我的药箱，指标监测，随访表单)")
    @Length(max = 255, message = "互动功能类型长度不能超过255")
    private String interactiveFunctionType;

    @ApiModelProperty(value = "患者是否可以看到（互动消息） 医生评论或看过后，消息变为1 患者可见，默认患者可见")
    private Integer patientCanSee;


    @ApiModelProperty(value = "药箱动作")
    @Length(max = 50, message = "CREATED, UPDATED")
    private String medicineOperationType;

    /**
     * 点击消息跳转的地址
     */
    @ApiModelProperty(value = "点击消息跳转的地址")
    @Length(max = 255, message = "点击消息跳转的地址长度不能超过255")
    private String jumpUrl;
    /**
     * 跳转类型(网页链接)
     */
    @ApiModelProperty(value = "跳转类型(网页链接)")
    @Length(max = 255, message = "跳转类型(网页链接)长度不能超过255")
    private String jumpType;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    /**
     * 接收患者ID
     */
    @ApiModelProperty(value = "接收患者ID")
    @NotNull(message = "接收患者ID不能为空")
    private Long patientId;


    @ApiModelProperty(value = "接收患者openID")
    private String patientOpenId;

    /**
     * 状态 1 已读， 0 未读
     */
    @ApiModelProperty(value = "状态 1 已读， 0 未读")
    private Integer readStatus;


    @ApiModelProperty(value = "医生看过状态 1 已读， 0 未读")
    private Integer doctorReadStatus;

    @ApiModelProperty(value = "医生评论状态  1 评论， 0 未评论")
    private Integer doctorCommentStatus;
    /**
     * 消息推送时间
     */
    @ApiModelProperty(value = "消息推送时间")
    private LocalDateTime pushTime;
    /**
     * 消息推送人
     */
    @ApiModelProperty(value = "消息推送人")
    @Length(max = 50, message = "消息推送人长度不能超过50")
    private String pushPerson;
    /**
     * 系统消息推送的主要文本内容
     */
    @ApiModelProperty(value = "系统消息推送的主要文本内容")
    @Length(max = 300, message = "系统消息推送的主要文本内容长度不能超过300")
    private String pushContent;

    @ApiModelProperty(value = "租户")
    private String tenantCode;

    @ApiModelProperty(value = "病例讨论状态(0 开始, 1 进行中, 2结束 )")
    private Integer caseDiscussionStatus;

    @ApiModelProperty(value = "计划名称")
    String planName;

    @ApiModelProperty(value = "预约状态")
    Integer appointmentStatus;


    @ApiModelProperty(value = "预约时间")
    LocalDateTime appointmentTime;


    public MsgPatientSystemMessageSaveDTO() {
    }

    public MsgPatientSystemMessageSaveDTO(String functionType,
                                          Long businessId,
                                          String jumpUrl,
                                          Long patientId,
                                          LocalDateTime pushTime,
                                          String pushPerson,
                                          String tenantCode,
                                          Integer caseDiscussionStatus) {
        this.functionType = functionType;
        this.businessId = businessId;
        this.jumpUrl = jumpUrl;
        this.patientId = patientId;
        this.readStatus = 0;
        this.pushTime = pushTime;
        this.pushPerson = pushPerson;
        this.tenantCode = tenantCode;
        this.caseDiscussionStatus = caseDiscussionStatus;
    }

    public MsgPatientSystemMessageSaveDTO(String functionType,
                                          Long businessId,
                                          String jumpUrl,
                                          Long patientId,
                                          LocalDateTime pushTime,
                                          String pushPerson,
                                          String tenantCode) {
        this(functionType, businessId, jumpUrl, patientId, pushTime, pushPerson, tenantCode, null);
    }




    /**
     * 创建推送内容
     * @param planName 指标监测，自定义随访的名称
     * @param appointmentStatus 就诊状态  未就诊：0  已就诊：1,  取消就诊 2，审核中 -2，(预约失败)审核失败 3， 就诊过期：4
     * @return
     */
    public void createPushContent ( String planName, Integer appointmentStatus, String appointmentTime) {

        if ("MEDICATION".equals(functionType)) {
            // 用药提醒 1
            this.pushContent = "您将于"+ pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().toString() +"有一次用药，点击此处可查看详情！";
        } else if ("REVIEW_MANAGE".equals(functionType)) {
            // 复查提醒 1
            this.pushContent = "您在"+ pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().toString() +"有一次复查，请准时进行复查，点击此处可查看详情！";
        } else if ("HEALTH_LOG".equals(functionType)) {
            // 健康日志 1
            this.pushContent = "您有一条健康日志待记录，请点击此处可记录信息！";
        } else if ("REFERRAL_SERVICE".equals(functionType)) {
            // 转诊管理 1
            this.pushContent = "您现在可以与"+ pushPerson +"医生进行线上沟通了，点击此处可获取转诊卡！";
        } else if ("LEARNING_PLAN".equals(functionType)) {
            // 学习计划 1
            this.pushContent = "医生向您推送了文章，点击此处可查看详情！";
        } else if ("CASE_DISCUSSION_START".equals(functionType)) {
            this.pushContent = "您的病例讨论开始啦！点击此处进入群组！";
        } else if ("CASE_DISCUSSION_END".equals(functionType)) {
            this.pushContent = "您的病例讨论已结束，如有疑问可在线咨询医生！";
        } else if ("CASE_DISCUSSION_RUNNING".equals(functionType)) {
            this.pushContent = "您的病例讨论正在进行中！点击此处进入群组！";
        } else if ("INDICATOR_MONITORING".equals(functionType) || "BLOOD_SUGAR".equals(functionType) || "BLOOD_PRESSURE".equals(functionType)) {
            // 指标监测 1
            this.pushContent = "您于"+ pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().withNano(0).toString() +"需进行"+planName+"的检测，记得不要遗忘哟！";
        } else if ("CUSTOM_FOLLOW_UP".equals(functionType)) {
            // 自定义随访 1
            this.pushContent = "您有一条随访信息待查看，点击此处可查看详情！";
        } else if ("BOOKING_MANAGEMENT".equals(functionType)) {
            // 预约 1
            if (appointmentStatus.equals(0)) {
                this.pushContent = "您预约"+ pushPerson +"医生"+appointmentTime+"的预约申请已审核成功，请点击此处查看预约详情";
            }
            if (appointmentStatus.equals(3) || appointmentStatus.equals(4)) {
                this.pushContent = "您预约"+ pushPerson +"医生"+appointmentTime+"的预约申请已审核失败，请点击此处查看预约详情";
            }
        } else if ("COMPLETENESS_INFORMATION".equals(functionType)) {
            // 信息完整度 1
            this.pushContent = "为了让医生更好的了解您的情况，请及时完善相关信息的填写！";
        } else if ("BUY_DRUGS_REMINDER".equals(functionType)) {
            // 用药预警 1
            this.pushContent = "您的"+pushPerson+"即将用完，后续如需继续使用，请及时购买哟！";
        }  else if ("Reply_after_following".equals(functionType)) {
            // 关注后自动回复 1
            this.pushContent = "感谢您的关注，本账号将会不定期更新关于健康的知识，并可与医生一对一沟通交流，点击此处赶快来解锁更多内容吧！";
        }  else if ("unregistered_reply".equals(functionType)) {
            // 未注册自动回复 1
            this.pushContent = "您还未注册个人账号，注册后可与医生在线沟通获得更多健康服务，点击此处即可注册，只需一步即可获取健康内容！";
        }

        // 互动消息
    }


}
