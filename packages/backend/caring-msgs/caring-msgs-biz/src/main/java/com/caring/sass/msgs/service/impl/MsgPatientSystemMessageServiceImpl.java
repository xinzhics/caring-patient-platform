package com.caring.sass.msgs.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dao.MsgPatientSystemMessageMapper;
import com.caring.sass.msgs.dao.PatientSystemMessageRemarkMapper;
import com.caring.sass.msgs.dto.PatientSystemMessageRemarkSaveDTO;
import com.caring.sass.msgs.entity.MsgPatientSystemMessage;
import com.caring.sass.msgs.entity.PatientSystemMessageRemark;
import com.caring.sass.msgs.service.MsgPatientSystemMessageService;
import com.caring.sass.msgs.service.PatientSystemMessageRemarkService;
import com.caring.sass.msgs.utils.I18nUtils;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.enumeration.DrugsOperateType;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.template.DoctorCommentReminderModel;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者系统消息
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-21
 */
@Slf4j
@Service

public class MsgPatientSystemMessageServiceImpl extends SuperServiceImpl<MsgPatientSystemMessageMapper, MsgPatientSystemMessage> implements MsgPatientSystemMessageService {


    @Autowired
    PatientSystemMessageRemarkMapper patientSystemMessageRemarkMapper;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    TemplateMsgApi templateMsgApi;

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    TenantApi tenantApi;

    @Override
    public void doctorQueryMessage(IPage<MsgPatientSystemMessage> builtPage, LbqWrapper<MsgPatientSystemMessage> lbqWrapper) {
        super.page(builtPage, lbqWrapper);
        List<MsgPatientSystemMessage> pageRecords = builtPage.getRecords();

        List<MsgPatientSystemMessage> interactiveMessage = new ArrayList<>();
        Map<Long, Doctor> doctorMap = new HashMap<>();
        pageRecords.forEach(item -> {
            item.setFunctionName("待办事项");
            item.setPushContent(i18nDoctorPushContent(item));
            // 把互动消息拿出来。 给互动消息设置医生的评论结果
            if (PlanFunctionTypeEnum.INTERACTIVE_MESSAGE.getCode().equals(item.getFunctionType())) {
                setDoctorInfo(doctorMap, item);
                if (CommonStatus.YES.equals(item.getDoctorCommentStatus())) {
                    interactiveMessage.add(item);
                }
            }
        });

        setMessageRemark(interactiveMessage);

    }

    /**
     * 设置医生的 头像和姓名
     * @param doctorMap
     * @param item
     */
    private void setDoctorInfo(Map<Long, Doctor> doctorMap, MsgPatientSystemMessage item) {
        if (Objects.nonNull(item.getDoctorId())) {
            Doctor doctor = doctorMap.get(item.getDoctorId());
            if (doctor == null) {
                R<Doctor> doctorR = doctorApi.getDoctorBaseInfoById(item.getDoctorId());
                if (doctorR.getIsSuccess() && doctorR.getData() != null) {
                    doctor = doctorR.getData();
                }
            }
            if (Objects.nonNull(doctor)) {
                item.setDoctorName(doctor.getName());
                item.setDoctorAvatar(doctor.getAvatar());
            }
        }
    }


    /**
     * 设置互动消息的评论
     * @param interactiveMessage
     */
    private void setMessageRemark(List<MsgPatientSystemMessage> interactiveMessage) {
        if (CollUtil.isNotEmpty(interactiveMessage)) {
            List<Long> msgIds = interactiveMessage.stream().map(SuperEntity::getId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(msgIds)) {
                List<PatientSystemMessageRemark> systemMessageRemarks = patientSystemMessageRemarkMapper.selectList(Wraps.<PatientSystemMessageRemark>lbQ()
                        .in(PatientSystemMessageRemark::getMessageId, msgIds));
                Map<Long, PatientSystemMessageRemark> messageRemarkMap = systemMessageRemarks.stream().collect(Collectors.toMap(PatientSystemMessageRemark::getMessageId, item -> item, (o1, o2) -> o1));
                interactiveMessage.forEach(item -> {
                    PatientSystemMessageRemark messageRemark = messageRemarkMap.get(item.getId());
                    if (Objects.nonNull(messageRemark)) {
                        item.setDoctorCommentContent(messageRemark);
                    }
                });
            }
        }
    }


    /**
     * 患者查询 系统消息
     * @param page         翻页对象
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @return
     * @param <E>
     */
    @Override
    public <E extends IPage<MsgPatientSystemMessage>> E page(E page, Wrapper<MsgPatientSystemMessage> queryWrapper) {
        super.page(page, queryWrapper);
        List<MsgPatientSystemMessage> pageRecords = page.getRecords();

        List<MsgPatientSystemMessage> interactiveMessage = new ArrayList<>();
        Map<Long, Doctor> doctorMap = new HashMap<>();
        pageRecords.forEach(item -> {
            item.setFunctionName(getFunctionName(item.getFunctionType(), item.getCaseDiscussionStatus()));
            item.setPushContent(i18nPushContent(item));
            // 把互动消息拿出来。 给互动消息设置医生的评论结果
            if (PlanFunctionTypeEnum.INTERACTIVE_MESSAGE.getCode().equals(item.getFunctionType())) {
                setDoctorInfo(doctorMap, item);
                if (CommonStatus.YES.equals(item.getDoctorCommentStatus())) {
                    interactiveMessage.add(item);
                }
            }
        });

        setMessageRemark(interactiveMessage);

        return page;
    }

    private String getTemplateMsgId(String tenantCode) {
        R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getDoctorCommentReminderMsg(tenantCode);
        if (templateMsgDtoR.getIsSuccess()) {
            TemplateMsgDto templateMsgDto = templateMsgDtoR.getData();
            if (templateMsgDto == null || templateMsgDto.getTemplateId() == null) {
                return null;
            }
            return templateMsgDto.getTemplateId();
        }
        return null;
    }

    /**
     * 发送模版消息
     * @param message 待办消息
     * @param type 1 查看， 2 评论
     */
    public void sendTemplateMsg(String tenantCode, MsgPatientSystemMessage message, Integer type) {
        BaseContextHandler.setTenant(tenantCode);
        String templateMsgId = getTemplateMsgId(tenantCode);

        SendTemplateMessageForm messageForm = new SendTemplateMessageForm();
        messageForm.setTenantCode(BaseContextHandler.getTenant());
        WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
        templateMessage.setTemplateId(templateMsgId);
        templateMessage.setToUser(message.getPatientOpenId());
        String remark = "";
        if (PlanFunctionTypeEnum.MEDICATION.getCode().equals( message.getInteractiveFunctionType())) {
            String name = message.getPlanName();
            if (StrUtil.isNotBlank(name)) {
                if (name.length() > 8) {
                    name = name.substring(0, 4) + "...";
                }
                if (type.equals(1)) {
                    remark = "您使用的药品 “" + name + "“医生已看";
                } else if (type.equals(2)) {
                    remark = "医生已对药品“"+ name +"”进行了反馈";;
                }
            } else {
                if (type.equals(1)) {
                    remark += "您使用的药品医生已看过";
                } else if (type.equals(2)) {
                    remark += "医生已对你使用的药品进行了反馈";
                }
            }
        } else {
            String name = message.getPlanName();
            if (StrUtil.isNotBlank(name)) {
                if (name.length() > 8) {
                    name = name.substring(0, 4)  + "...";
                }
                remark = "您提交的 “" + name + "“";
            } else {
                remark = "您提交的信息";
            }
            if (type.equals(1)) {
                remark += "医生已看过";
            } else if (type.equals(2)) {
                remark += "医生已回复";
            }
        }


        List<WxMpTemplateData> templateData = DoctorCommentReminderModel.buildWxMpTemplateData(message.getPushPerson(), remark);
        templateMessage.setData(templateData);

        R<Tenant> tenantR = tenantApi.queryDomainByCode(tenantCode);
        Tenant data = tenantR.getData();
        String url = ApplicationDomainUtil.wxPatientBizUrl(data.getDomainName(), Objects.nonNull(data.getWxBindTime()), "patient/systemMessage");
        templateMessage.setUrl(url);
        messageForm.setWxAppId(data.getWxAppId());
        messageForm.setTemplateMessage(templateMessage);
        weiXinApi.sendTemplateMessage(messageForm);

    }

    /**
     * 医生查看待办消息
     * @param messageId
     * @param doctorName
     */
    @Override
    public void doctorSeeMessage(Long messageId, String doctorName) {

        MsgPatientSystemMessage systemMessage = baseMapper.selectById(messageId);
        if (systemMessage.getDoctorReadStatus() == null || systemMessage.getDoctorReadStatus().equals(CommonStatus.NO)) {
            systemMessage.setDoctorReadStatus(CommonStatus.YES);
            systemMessage.setPatientReadDoctorStatus(CommonStatus.NO);
            systemMessage.setDoctorReadTime(LocalDateTime.now());
            systemMessage.setDoctorName(doctorName);

            systemMessage.setPatientCanSee(CommonStatus.YES);

            baseMapper.updateById(systemMessage);

            // TODO: 发送模版消息，强提醒患者
            String tenant = BaseContextHandler.getTenant();
            SaasGlobalThreadPool.execute(() ->  sendTemplateMsg(tenant, systemMessage, 1));
        }

    }

    /**
     * 保存医生对 待办消息的评论。
     * 设置待办消息 医生已经评论。患者可见
     * @param saveDTO
     */
    @Override
    public PatientSystemMessageRemark doctorCommentMessage(PatientSystemMessageRemarkSaveDTO saveDTO) {

        PatientSystemMessageRemark messageRemark = new PatientSystemMessageRemark();
        BeanUtils.copyProperties(saveDTO, messageRemark);
        patientSystemMessageRemarkMapper.insert(messageRemark);

        MsgPatientSystemMessage systemMessage = new MsgPatientSystemMessage();
        systemMessage.setPatientCanSee(CommonStatus.YES);
        systemMessage.setDoctorCommentStatus(CommonStatus.YES);
        systemMessage.setPatientReadDoctorCommentStatus(CommonStatus.NO);
        systemMessage.setId(saveDTO.getMessageId());
        baseMapper.updateById(systemMessage);

        String tenant = BaseContextHandler.getTenant();
        MsgPatientSystemMessage message = baseMapper.selectById(saveDTO.getMessageId());
        SaasGlobalThreadPool.execute(() ->  sendTemplateMsg(tenant, message, 2));
        return messageRemark;
    }


    /**
     * 医生删除待办消息和消息下的评论
     * @param messageIds
     */
    @Override
    public void doctorMessage(List<Long> messageIds) {
        if (CollUtil.isEmpty(messageIds)) {
            return;
        }
        baseMapper.deleteBatchIds(messageIds);
        patientSystemMessageRemarkMapper.delete(Wraps.<PatientSystemMessageRemark>lbQ().in(PatientSystemMessageRemark::getMessageId, messageIds));
    }

    /**
     * 删除医生对待办消息的评估， 并设置消息为 未评 状态
     * @param commentId
     * @param messageId
     */
    @Override
    public void deleteDoctorCommentMessage(Long commentId, Long messageId) {

        patientSystemMessageRemarkMapper.deleteById(commentId);
        MsgPatientSystemMessage systemMessage = baseMapper.selectById(messageId);
        systemMessage.setDoctorCommentStatus(CommonStatus.NO);
        systemMessage.setPatientReadDoctorCommentStatus(CommonStatus.NO);
        baseMapper.updateById(systemMessage);

    }

    /**
     * 根据消息的类型获取消息的所属的业务分类名称
     * @param functionType 消息类型
     * @param caseDiscussionStatus 病例讨论的状态
     * @return
     */
    @Override
    public String getFunctionName(String functionType, Integer caseDiscussionStatus) {

        String name = "";
        switch (functionType) {
            case "BOOKING_MANAGEMENT" : name = "预约管理提醒"; break;
            case "BUY_DRUGS_REMINDER" : name = "用药预警提醒"; break;
            case "COMPLETENESS_INFORMATION" : name = "完善个人信息提醒"; break;
            case "MEDICATION" : name = "用药提醒"; break;
            case "CUSTOM_FOLLOW_UP" : name = "随访计划提醒"; break;
            case "INDICATOR_MONITORING" :
            case "BLOOD_PRESSURE":
            case "BLOOD_SUGAR" :
                name = "健康监测提醒"; break;
            case "HEALTH_LOG" : name = "健康日志提醒"; break;
            case "REVIEW_MANAGE" : name = "复查消息提醒"; break;
            case "LEARNING_PLAN" : name = "科普知识推送提醒"; break;
            case "CASE_DISCUSSION_START" : name = "病例讨论开始通知提醒"; break;
            case "CASE_DISCUSSION_END" : name = "病例讨论结束通知提醒"; break;
            case "CASE_DISCUSSION_RUNNING" : name = "病例讨论进行中提醒"; break;
            case "REFERRAL_SERVICE" : name = "转诊管理提醒"; break;
            case "Reply_after_following" : name = "关注提醒"; break;
            case "unregistered_reply" : name = "注册提醒"; break;
            case "INTERACTIVE_MESSAGE" : name = "互动消息"; break;
            default:
        }
        return name;

    }

    /**
     * 翻译医生 待办消息 的内容
     * @param message
     * @return
     */
    public String i18nDoctorPushContent(MsgPatientSystemMessage message) {
        LocalDateTime pushTime = message.getPushTime();
        String planName = message.getPlanName();
        String pushContent = "";
        String interactiveFunctionType = message.getInteractiveFunctionType();
        if (PlanFunctionTypeEnum.MEDICATION.getCode().equals(interactiveFunctionType)) {
            // 患者XXX于20XX-XX-XX修改了药品“XXX”信息，点击此处查看详情
            if (DrugsOperateType.UPDATED.operateType.equals(message.getMedicineOperationType())) {
                pushContent = I18nUtils.getMessage("INTERACTIVE_MESSAGE_MEDICATION_DOCTOR_UPDATED",
                        message.getPushPerson(),
                        pushTime.toLocalDate().toString(),
                        planName);
            } else if (DrugsOperateType.CREATED.operateType.equals(message.getMedicineOperationType())) {
                // 患者XXX于20XX-XX-XX 00:00添加了药品“XXXX”，点击此处查看详情
                pushContent = I18nUtils.getMessage("INTERACTIVE_MESSAGE_MEDICATION_DOCTOR_ADDED",
                        message.getPushPerson(),
                        pushTime.toLocalDate().toString(),
                        pushTime.toLocalTime().withSecond(0).withNano(0).toString(),
                        planName);
            }
        } else if (PlanFunctionTypeEnum.INDICATOR_MONITORING.getCode().equals(interactiveFunctionType)) {
            // 患者XXX与20XX-XX-XX 00:00提交了“XXXX”监测信息，点击此处查看详情
            pushContent = I18nUtils.getMessage("INTERACTIVE_MESSAGE_INDICATOR_MONITORING_DOCTOR",
                    message.getPushPerson(),
                    pushTime.toLocalDate().toString(), pushTime.toLocalTime().withSecond(0).withNano(0).toString(), planName);
        } else {
            // 疾病信息、过敏日志、检查报告
            // 患者XXX与20XX-XX-XX 00:00提交了“XXX”，点击此处查看详情；
            pushContent = I18nUtils.getMessage("INTERACTIVE_MESSAGE_OTHER_FORM_DOCTOR",
                    message.getPushPerson(),
                    pushTime.toLocalDate().toString(), pushTime.toLocalTime().withSecond(0).withNano(0).toString(), planName);
        }
        return pushContent;

    }


    @Override
    public String i18nPushContent(MsgPatientSystemMessage message) {
        String functionType = message.getFunctionType();
        LocalDateTime pushTime = message.getPushTime();
        String pushPerson = message.getPushPerson();
        String planName = message.getPlanName();
        Integer appointmentStatus = message.getAppointmentStatus();
        LocalDateTime appointmentTime = message.getAppointmentTime();
        String pushContent = null;
        if ("MEDICATION".equals(functionType)) {
            // 用药提醒 1
            // "您将于"+ pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().toString() +"有一次用药，点击此处可查看详情！"
            String params = pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().toString();
            pushContent = I18nUtils.getMessage("MEDICATION_MESSAGE", params);
        } else if ("REVIEW_MANAGE".equals(functionType)) {
            // 复查提醒 1
            // "您在"+ pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().toString() +"有一次复查，请准时进行复查，点击此处可查看详情！"
            String params = pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().toString();
            pushContent = I18nUtils.getMessage("REVIEW_MANAGE_MESSAGE", params);
        } else if ("HEALTH_LOG".equals(functionType)) {
            // 健康日志 1
            // "您有一条健康日志待记录，请点击此处可记录信息！"
            pushContent = I18nUtils.getMessage("HEALTH_LOG_MESSAGE", pushPerson);
        } else if ("REFERRAL_SERVICE".equals(functionType)) {
            // 转诊管理 1
            // "您现在可以与"+ pushPerson +"医生进行线上沟通了，点击此处可获取转诊卡！"
            pushContent = I18nUtils.getMessage("REFERRAL_SERVICE_MESSAGE", pushPerson);
        } else if ("LEARNING_PLAN".equals(functionType)) {
            // 学习计划 1
            // 医生向您推送了文章，点击此处可查看详情！
            pushContent = I18nUtils.getMessage("LEARNING_PLAN_MESSAGE");
        } else if ("CASE_DISCUSSION_START".equals(functionType)) {
            //"您的病例讨论开始啦！点击此处进入群组！";
            pushContent = I18nUtils.getMessage("CASE_DISCUSSION_START_MESSAGE");
        } else if ("CASE_DISCUSSION_END".equals(functionType)) {
            // "您的病例讨论已结束，如有疑问可在线咨询医生！"
            pushContent = I18nUtils.getMessage("CASE_DISCUSSION_END_MESSAGE");
        } else if ("CASE_DISCUSSION_RUNNING".equals(functionType)) {
            //"您的病例讨论正在进行中！点击此处进入群组！";
            pushContent = I18nUtils.getMessage("CASE_DISCUSSION_RUNNING_MESSAGE");
        } else if ("INDICATOR_MONITORING".equals(functionType) || "BLOOD_SUGAR".equals(functionType) || "BLOOD_PRESSURE".equals(functionType)) {
            // 指标监测 1
            if (planName == null) {
                return message.getPushContent();
            }
            //  "您于"+ pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().withNano(0).toString() +"需进行"+planName+"的检测，记得不要遗忘哟！";
            String params = pushTime.toLocalDate().toString() +" "+ pushTime.toLocalTime().withNano(0).toString();
            pushContent = I18nUtils.getMessage("INDICATOR_MONITORING_MESSAGE", params, planName);
        } else if ("CUSTOM_FOLLOW_UP".equals(functionType)) {
            // 自定义随访 1
            // "您有一条随访信息待查看，点击此处可查看详情！"
            pushContent = I18nUtils.getMessage("CUSTOM_FOLLOW_UP_MESSAGE");
        } else if ("BOOKING_MANAGEMENT".equals(functionType)) {
            if (appointmentTime == null) {
                return message.getPushContent();
            }
            // 预约 1
            int month = appointmentTime.getMonth().getValue();
            int dayOfMonth = appointmentTime.getDayOfMonth();
            String time = month + "-" + dayOfMonth + " ";
            int hour = appointmentTime.getHour();
            if (hour == 8) {
                time += I18nUtils.getMessage("morning");
            }
            if (hour == 14) {
                time += I18nUtils.getMessage("afternoon");
            }
            if (appointmentStatus.equals(0)) {
                // "您预约"+ pushPerson +"医生"+appointmentTime+"的预约申请已审核成功，请点击此处查看预约详情"
                pushContent = I18nUtils.getMessage("BOOKING_MANAGEMENT_SUCCESS_MESSAGE", pushPerson, time) ;
            }
            if (appointmentStatus.equals(3) || appointmentStatus.equals(4)) {
                // "您预约"+ pushPerson +"医生"+appointmentTime+"的预约申请已审核失败，请点击此处查看预约详情"
                pushContent = I18nUtils.getMessage("BOOKING_MANAGEMENT_FAIL_MESSAGE", pushPerson, time) ;
            }
        } else if ("COMPLETENESS_INFORMATION".equals(functionType)) {
            // 信息完整度 1
            // "为了让医生更好的了解您的情况，请及时完善相关信息的填写！"
            pushContent = I18nUtils.getMessage("COMPLETENESS_INFORMATION") ;
        } else if ("BUY_DRUGS_REMINDER".equals(functionType)) {
            // 用药预警 1
            // "您的"+pushPerson+"即将用完，后续如需继续使用，请及时购买哟！"
            pushContent = I18nUtils.getMessage("BUY_DRUGS_REMINDER", pushPerson);
        }  else if ("Reply_after_following".equals(functionType)) {
            // 关注后自动回复 1
            // "感谢您的关注，本账号将会不定期更新关于健康的知识，并可与医生一对一沟通交流，点击此处赶快来解锁更多内容吧！"
            pushContent = I18nUtils.getMessage("Reply_after_following");
        }  else if ("unregistered_reply".equals(functionType)) {
            // 未注册自动回复 1
            // "您还未注册个人账号，注册后可与医生在线沟通获得更多健康服务，点击此处即可注册，只需一步即可获取健康内容！";
            pushContent = I18nUtils.getMessage("unregistered_reply");
        } else if ("INTERACTIVE_MESSAGE".equals(functionType)) {
            String interactiveFunctionType = message.getInteractiveFunctionType();
            if (PlanFunctionTypeEnum.MEDICATION.getCode().equals(interactiveFunctionType)) {
                // 您于20XX-XX-XX  XX:XX修改了“XXXX”，为了让医生了解您的健康情况，一定要记得按时用药按时记录哟！
                if (DrugsOperateType.UPDATED.operateType.equals(message.getMedicineOperationType())) {
                    pushContent = I18nUtils.getMessage("INTERACTIVE_MESSAGE_MEDICATION",
                            pushTime.toLocalDate().toString(), pushTime.toLocalTime().withSecond(0).withNano(0).toString(),
                            I18nUtils.getMessage("INTERACTIVE_MESSAGE_MEDICATION_OPERATION_TYPE_UPDATED"),
                            planName);
                } else if (DrugsOperateType.CREATED.operateType.equals(message.getMedicineOperationType())) {
                    // 您于20XX-XX-XX  XX:XX添加了“XXXX”，为了让医生了解您的健康情况，一定要记得按时用药按时记录哟！
                    pushContent = I18nUtils.getMessage("INTERACTIVE_MESSAGE_MEDICATION",
                            pushTime.toLocalDate().toString(), pushTime.toLocalTime().withSecond(0).withNano(0).toString(),
                            I18nUtils.getMessage("INTERACTIVE_MESSAGE_MEDICATION_OPERATION_TYPE_ADD"),
                            planName);
                }
            } else if (PlanFunctionTypeEnum.INDICATOR_MONITORING.getCode().equals(interactiveFunctionType)) {
                // 指标监测 您于20XX-XX-XX  XX:XX提交了“XXXX”监测信息，继续保持，坚持记录，让医生更好的了解您的身体情况
                pushContent = I18nUtils.getMessage("INTERACTIVE_MESSAGE_INDICATOR_MONITORING",
                        pushTime.toLocalDate().toString(), pushTime.toLocalTime().withSecond(0).withNano(0).toString(), planName);
            } else {
                // 疾病信息、过敏日志、检查报告
                // 您于20XX-XX-XX  XX:XX提交了“XXXX”，坚持上传健康信息有助于医生更好的了解您的健康情况，继续加油！
                pushContent = I18nUtils.getMessage("INTERACTIVE_MESSAGE_OTHER_FORM",
                        pushTime.toLocalDate().toString(), pushTime.toLocalTime().withSecond(0).withNano(0).toString(), planName);
            }

        }
        return pushContent;
    }


}
