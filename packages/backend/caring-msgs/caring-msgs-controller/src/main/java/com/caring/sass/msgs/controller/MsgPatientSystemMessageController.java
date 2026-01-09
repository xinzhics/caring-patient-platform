package com.caring.sass.msgs.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dao.ChatMapper;
import com.caring.sass.msgs.dao.ChatSendReadMapper;
import com.caring.sass.msgs.dto.*;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.msgs.entity.MsgPatientSystemMessage;

import java.util.*;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.msgs.entity.PatientSystemMessageRemark;
import com.caring.sass.msgs.service.MsgPatientSystemMessageService;
import com.caring.sass.msgs.utils.MediaTypeUtils;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.user.entity.Doctor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 患者系统消息
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/msgPatientSystemMessage")
@Api(value = "MsgPatientSystemMessage", tags = "患者系统消息")
public class MsgPatientSystemMessageController extends SuperController<MsgPatientSystemMessageService, Long, MsgPatientSystemMessage, MsgPatientSystemMessagePageDTO, MsgPatientSystemMessageSaveDTO, MsgPatientSystemMessageUpdateDTO> {

    @Autowired
    ChatSendReadMapper chatSendReadMapper;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    ChatMapper chatMapper;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<MsgPatientSystemMessage> msgPatientSystemMessageList = list.stream().map((map) -> {
            MsgPatientSystemMessage msgPatientSystemMessage = MsgPatientSystemMessage.builder().build();
            //TODO 请在这里完成转换
            return msgPatientSystemMessage;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(msgPatientSystemMessageList));
    }

    @ApiOperation("查询医生的待办消息")
    @PostMapping("doctorQueryMessage")
    public R<IPage<MsgPatientSystemMessage>> doctorQueryMessage(@RequestBody PageParams<Object> params) {

        Long userId = BaseContextHandler.getUserId();
        if (Objects.isNull(userId)) {
            return R.success(null);
        }
        IPage<MsgPatientSystemMessage> builtPage = params.buildPage();
        LbqWrapper<MsgPatientSystemMessage> lbqWrapper = Wraps.<MsgPatientSystemMessage>lbQ()
                .eq(MsgPatientSystemMessage::getFunctionType, PlanFunctionTypeEnum.INTERACTIVE_MESSAGE.getCode())
                .eq(MsgPatientSystemMessage::getDoctorId, userId);
        lbqWrapper.orderByDesc(SuperEntity::getCreateTime);
        baseService.doctorQueryMessage(builtPage, lbqWrapper);
        return R.success(builtPage);
    }

    @ApiOperation("医生查看某个待办消息")
    @GetMapping("doctorSeeMessage")
    public R<Boolean> doctorSeeMessage(@RequestParam(name = "messageId") Long messageId, String doctorName) {
        baseService.doctorSeeMessage(messageId, doctorName);
        return R.success(true);
    }


    @ApiOperation("医生评论某个待办消息")
    @PostMapping("doctorCommentMessage")
    public R<PatientSystemMessageRemark> doctorCommentMessage(@RequestBody @Validated PatientSystemMessageRemarkSaveDTO saveDTO) {
        PatientSystemMessageRemark remark = baseService.doctorCommentMessage(saveDTO);
        return R.success(remark);
    }

    @ApiOperation("医生删除某个待办消息评论")
    @DeleteMapping("doctorCommentMessage")
    public R<Boolean> doctorCommentMessage(@RequestParam Long commentId, @RequestParam(name = "messageId") Long messageId) {
        baseService.deleteDoctorCommentMessage(commentId, messageId);
        return R.success(true);
    }

    @ApiOperation("医生删除待办事项")
    @DeleteMapping("doctorMessage")
    public R<Boolean> doctorMessage(@RequestBody List<Long> messageIds) {
        baseService.doctorMessage(messageIds);
        return R.success(true);
    }

    @ApiOperation("保存患者消息记录")
    @PostMapping("saveSystemMessage")
    public R<Boolean> saveSystemMessage(@RequestBody @Validated MsgPatientSystemMessageSaveDTO systemMessageSaveDTO) {
        String tenantCode = systemMessageSaveDTO.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        MsgPatientSystemMessage systemMessage = new MsgPatientSystemMessage();
        BeanUtils.copyProperties(systemMessageSaveDTO, systemMessage);
        baseService.save(systemMessage);
        return R.success();
    }


    @ApiOperation("患者设置消息已读")
    @PutMapping("setMessageStatus")
    public R<Boolean> setMessageStatus(@RequestBody List<Long> messageIds) {

        if (CollUtil.isNotEmpty(messageIds)) {
            UpdateWrapper<MsgPatientSystemMessage> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", messageIds);
            updateWrapper.set("read_status", CommonStatus.YES);
            updateWrapper.set("patient_read_doctor_comment_status", CommonStatus.YES);
            updateWrapper.set("patient_read_doctor_status", CommonStatus.YES);
            baseService.update(updateWrapper);
        }

        return R.success();
    }


    @ApiOperation("删除某业务的系统消息")
    @DeleteMapping("deleteByBusinessId")
    public R<Boolean> deleteByBusinessId(
            @RequestParam("functionType") String functionType,
            @RequestParam("businessId") Long businessId) {
        baseService.remove(Wraps.<MsgPatientSystemMessage>lbQ()
                .eq(MsgPatientSystemMessage::getFunctionType, functionType)
                .eq(MsgPatientSystemMessage::getBusinessId, businessId));
        return R.success();
    }

    @ApiOperation("统计医生还没看的待办消息")
    @GetMapping("doctorCountMessage")
    public  R<Integer> doctorCountMessage(@RequestParam Long doctorId) {
        LbqWrapper<MsgPatientSystemMessage> wrapper = Wraps.<MsgPatientSystemMessage>lbQ()
                .eq(MsgPatientSystemMessage::getDoctorId, doctorId)
                .eq(MsgPatientSystemMessage::getFunctionType, PlanFunctionTypeEnum.INTERACTIVE_MESSAGE.getCode())
                .eq(MsgPatientSystemMessage::getDoctorReadStatus, CommonStatus.NO);
        int countSystemMessage = baseService.count(wrapper);
        return R.success(countSystemMessage);
    }


    @ApiOperation("消息角标上的未读消息总和")
    @GetMapping("countMessage")
    public R<Integer> countMessage(@RequestParam Long patientId) {
        LbqWrapper<MsgPatientSystemMessage> wrapper = Wraps.<MsgPatientSystemMessage>lbQ()
                .eq(MsgPatientSystemMessage::getPatientId, patientId)
                .eq(MsgPatientSystemMessage::getPatientCanSee, CommonStatus.YES)
                .and(w -> w.eq(MsgPatientSystemMessage::getReadStatus, CommonStatus.NO)
                        .or()
                        .eq(MsgPatientSystemMessage::getPatientReadDoctorStatus, CommonStatus.NO)
                        .or()
                        .eq(MsgPatientSystemMessage::getPatientReadDoctorCommentStatus, CommonStatus.NO));
        int countSystemMessage = baseService.count(wrapper);

        // 统计咨询 未读消息
        Integer countIm = chatSendReadMapper.selectCount(Wraps.<ChatSendRead>lbQ()
                .eq(ChatSendRead::getUserId, patientId)
                .eq(ChatSendRead::getIs_delete, CommonStatus.NO)
                .eq(ChatSendRead::getRoleType, UserType.UCENTER_PATIENT));
        if (countIm != null) {
            return R.success(countSystemMessage + countIm);
        }
        return R.success(countSystemMessage);
    }


    @Override
    public R<IPage<MsgPatientSystemMessage>> page(@RequestBody PageParams<MsgPatientSystemMessagePageDTO> params) {
        IPage<MsgPatientSystemMessage> buildPage = params.buildPage();
        MsgPatientSystemMessagePageDTO paramsModel = params.getModel();
        LbqWrapper<MsgPatientSystemMessage> lbqWrapper = Wraps.<MsgPatientSystemMessage>lbQ()
                .eq(MsgPatientSystemMessage::getPatientCanSee, CommonStatus.YES)
                .eq(MsgPatientSystemMessage::getPatientId, paramsModel.getPatientId());
        if (StrUtil.isNotBlank(paramsModel.getFunctionType())) {
            if (paramsModel.getFunctionType().equals("INDICATOR_MONITORING")) {
                lbqWrapper.in(MsgPatientSystemMessage::getFunctionType, "INDICATOR_MONITORING", "BLOOD_SUGAR", "BLOOD_PRESSURE");
            } else {
                lbqWrapper.eq(MsgPatientSystemMessage::getFunctionType, paramsModel.getFunctionType());
            }
        }
        baseService.page(buildPage, lbqWrapper);
        return R.success(buildPage);
    }

    @ApiOperation("患者消息页面的所有数据")
    @GetMapping("messagePageData")
    public R<PatientMessageCountDto> messagePageData(@RequestParam Long patientId, @RequestParam String patientImAccount) {
        PatientMessageCountDto countDto = new PatientMessageCountDto();
        LbqWrapper<MsgPatientSystemMessage> wrapper = Wraps.<MsgPatientSystemMessage>lbQ()
                .eq(MsgPatientSystemMessage::getPatientId, patientId)
                .eq(MsgPatientSystemMessage::getPatientCanSee, CommonStatus.YES)
                .and(w -> w.eq(MsgPatientSystemMessage::getReadStatus, CommonStatus.NO)
                        .or()
                        .eq(MsgPatientSystemMessage::getPatientReadDoctorStatus, CommonStatus.NO)
                        .or()
                        .eq(MsgPatientSystemMessage::getPatientReadDoctorCommentStatus, CommonStatus.NO));
        int countSystemMessage = baseService.count(wrapper);
        countDto.setSystemMessageCount(countSystemMessage);
        countDto.setAllMessageCount(countSystemMessage);
        // 统计咨询 未读消息
        Integer countIm = chatSendReadMapper.selectCount(Wraps.<ChatSendRead>lbQ()
                .eq(ChatSendRead::getUserId, patientId)
                .eq(ChatSendRead::getIs_delete, CommonStatus.NO)
                .eq(ChatSendRead::getRoleType, UserType.UCENTER_PATIENT));

        Chat chat = chatMapper.selectOne(Wraps.<Chat>lbQ().eq(Chat::getReceiverImAccount, patientImAccount)
                .eq(Chat::getHistoryVisible, 1)
                .eq(Chat::getWithdrawChatStatus, 0)
                .notLike(Chat::getDeleteThisMessageUserIdJsonArrayString, patientId.toString())
                .orderByDesc(Chat::getCreateTime)
                .last(" limit 0,1 "));

        List<PatientMessageGroupListDTO> messageList = new ArrayList<>();
        countDto.setMessageList(messageList);
        PatientMessageGroupListDTO imDto = new PatientMessageGroupListDTO();
        if (countIm != null && countIm > 0) {
            countDto.setImMessageCount(countIm);
            countDto.setAllMessageCount(countSystemMessage + countIm);
            R<Doctor> infoByPatientId = doctorApi.getDoctorBaseInfoByPatientId(patientId);
            if (infoByPatientId.getIsSuccess()) {
                Doctor doctor = infoByPatientId.getData();
                if (Objects.nonNull(doctor)) {
                    imDto.setDoctorAvatar(doctor.getAvatar());
                    imDto.setDoctorName(doctor.getName());
                    imDto.setDoctorTitle(doctor.getTitle());
                    String contentByType = MediaTypeUtils.getImContentByType(chat.getType(), chat.getContent());
                    imDto.setMessageContent(contentByType);
                    imDto.setMessageSendTime(chat.getCreateTime());
                    imDto.setNoReadCount(countIm);
                }
            }
            messageList.add(imDto);
        }

        List<String> functionTypeList = new ArrayList<>();

        // 功能类型中未读消息统计
        QueryWrapper<MsgPatientSystemMessage> countLbqWrapper = new QueryWrapper();
        countLbqWrapper.select("function_type, count(*) countNum");
        countLbqWrapper.eq("patient_id", patientId);
        countLbqWrapper.eq("patient_can_see", CommonStatus.YES);
        countLbqWrapper.and(w -> w.eq("read_status", 0)
                .or()
                .eq("patient_read_doctor_status", 0)
                .or()
                .eq("patient_read_doctor_comment_status", 0));
        countLbqWrapper.groupBy("function_type");
        List<Map<String, Object>> listMaps = baseService.listMaps(countLbqWrapper);
        Map<String, Integer> countMap = new HashMap<>();
        if (CollUtil.isNotEmpty(listMaps)) {
            for (Map<String, Object> objectMap : listMaps) {
                Object function_type = objectMap.get("function_type");
                Object countNum = objectMap.get("countNum");
                if (function_type != null && countNum != null) {
                    countMap.put(function_type.toString(), Integer.parseInt(countNum.toString()));
                }
            }
        }


        // 查询每个功能 最新的一条消息的ID。
        QueryWrapper<MsgPatientSystemMessage> lastCreateMessageId = new QueryWrapper();
        lastCreateMessageId.select("max(id) maxId, function_type");
        lastCreateMessageId.eq("patient_id", patientId);
        lastCreateMessageId.eq("patient_can_see", CommonStatus.YES);
        lastCreateMessageId.groupBy("function_type");
        List<Map<String, Object>> lastCreateMessageIdMap = baseService.listMaps(lastCreateMessageId);
        List<Long> lastMessageId = new ArrayList<>();
        Map<String, Long> lastMessageIdMap = new HashMap<>();
        if (CollUtil.isNotEmpty(lastCreateMessageIdMap)) {
            for (Map<String, Object> objectMap : lastCreateMessageIdMap) {
                Object maxId = objectMap.get("maxId");
                Object function_type = objectMap.get("function_type");
                if (function_type != null && maxId != null) {
                    long aLong = Long.parseLong(maxId.toString());
                    functionTypeList.add(function_type.toString());
                    lastMessageIdMap.put(function_type.toString(), aLong);
                    lastMessageId.add(aLong);
                }
            }
        }
        Map<Long, MsgPatientSystemMessage> systemMessageMap = new HashMap<>();
        if (CollUtil.isNotEmpty(lastMessageId)) {
            List<MsgPatientSystemMessage> systemMessages = baseService.list(Wraps.<MsgPatientSystemMessage>lbQ().in(SuperEntity::getId, lastMessageId));
            if (CollUtil.isNotEmpty(systemMessages)) {
                for (MsgPatientSystemMessage message : systemMessages) {
                    message.setPushContent(baseService.i18nPushContent(message));
                    systemMessageMap.put(message.getId(), message);
                }
            }
        }
        PatientMessageGroupListDTO message;

        int healthMonitorCount = 0;
        PatientMessageGroupListDTO healthMonitor = new PatientMessageGroupListDTO();
        boolean add = false;
        for (String s : functionTypeList) {
            Long messageId = lastMessageIdMap.get(s);
            MsgPatientSystemMessage systemMessage = systemMessageMap.get(messageId);
            if (systemMessage == null) {
                continue;
            }
            Integer count = countMap.get(s);

            // 将 血压 血糖 指标监测 合并为 健康监测提醒
            if ("INDICATOR_MONITORING".equals(s) || "BLOOD_SUGAR".equals(s) || "BLOOD_PRESSURE".equals(s)) {
                if (count != null) {
                    healthMonitorCount += count;
                }
                healthMonitor.setNoReadCount(healthMonitorCount);
                if (healthMonitor.getMessageSendTime() == null || healthMonitor.getMessageSendTime().isBefore(systemMessage.getPushTime())) {
                    healthMonitor.setMessageSendTime(systemMessage.getPushTime());
                    healthMonitor.setMessageContent(systemMessage.getPushContent());
                    healthMonitor.setMessageName(baseService.getFunctionName(s, systemMessage.getCaseDiscussionStatus()));
                }
                if (!add) {
                    add= true;
                    healthMonitor.setMessageType("INDICATOR_MONITORING");
                    messageList.add(healthMonitor);
                }
            } else {
                message = new PatientMessageGroupListDTO();
                message.setMessageType(s);
                message.setNoReadCount(count);
                message.setMessageSendTime(systemMessage.getPushTime());
                message.setMessageContent(systemMessage.getPushContent());
                message.setMessageName(baseService.getFunctionName(s, systemMessage.getCaseDiscussionStatus()));
                messageList.add(message);
            }
        }

        return R.success(countDto);

    }



}
