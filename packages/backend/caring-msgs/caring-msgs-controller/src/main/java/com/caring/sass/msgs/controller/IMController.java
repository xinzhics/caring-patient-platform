package com.caring.sass.msgs.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.Direction;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.config.IMConfigWrapper;
import com.caring.sass.msgs.dao.ChatAtRecordMapper;
import com.caring.sass.msgs.dto.*;
import com.caring.sass.msgs.entity.*;
import com.caring.sass.msgs.service.*;
import com.caring.sass.msgs.service.impl.IMService;
import com.caring.sass.msgs.service.impl.JPushService;
import com.caring.sass.msgs.utils.im.api.impl.EasemobSendMessage;
import com.caring.sass.oauth.api.DoctorGroupApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.DoctorGroup;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.SpringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping({"im"})
public class IMController {

    @Autowired
    IMService imService;

    @Autowired
    ChatService chatService;

    @Autowired
    JPushService jpushService;

    @Autowired
    private ChatGroupSendService chatGroupSendService;

    @Autowired
    EasemobSendMessage sendMessage;

    @Autowired
    ChannelContentApi channelContentApi;

    @Autowired
    ChatUserNewMsgService chatUserNewMsgService;

    @Autowired
    DoctorGroupApi doctorGroupApi;

    @Autowired
    ChatBizService chatBizService;

    @Autowired
    ChatAtRecordMapper chatAtRecordMapper;

    @Autowired
    PatientApi patientApi;


    @ApiOperation("注册环信账号")
    @PostMapping("/account/register")
    public R<Boolean> registerAccount(@RequestBody IMUserDto user) {
        try {
            if (StringUtils.isEmpty(user.getPassword())) {
                user.setPassword(IMConfigWrapper.getUserPassword());
            }
            this.imService.register(user);
        } catch (Exception var3) {
        }
        return R.success();
    }

    @ApiOperation("移除环信账号")
    @PostMapping("/account/remove/{username}")
    public R<Boolean> removeAccount(@PathVariable("username") String username) {
        try {
            this.imService.delete(username);
        } catch (Exception var3) {
        }
        return R.success();
    }

    @ApiOperation("【聊天】 - 发送聊天信息")
    @PostMapping({"/sendChat"})
    public R<Chat> sendChat(@RequestBody Chat chat) {
        log.info("真正开始发送环信消息:{}", chat);
        try {
            String tenantCode = chat.getTenantCode();
            if (StrUtil.isNotEmpty(tenantCode)) {
                BaseContextHandler.setTenant(tenantCode);
            }
            if (Objects.isNull(chat.getCreateTime())) {
                chat.setCreateTime(LocalDateTime.now());
            }
            Integer registerMessage = chat.getRegisterMessage();
            Integer systemMessage = chat.getSystemMessage();
            List<ChatAtRecord> chatAtRecords = chat.getChatAtRecords();
            chatService.save(chat);

            // 发送消息
            imService.sendImMessage(chat);
            String tenant = BaseContextHandler.getTenant();
            if (registerMessage != null && registerMessage.equals(1)) {
                chatUserNewMsgService.updateMsg(chat, chat.getDoctorId(), chat.getNursingId(),
                        chat.getReceiverImAccount(), tenant, chatAtRecords);
                return R.success(chat);
            }

            // 发送的是系统自动回复。 不需要走下面的流程
            if (systemMessage != null && systemMessage.equals(1)) {
                return R.success(chat);
            }
            SaasGlobalThreadPool.execute(() ->
                    chatUserNewMsgService.updateMsg(chat, chat.getDoctorId(), chat.getNursingId(),
                            chat.getReceiverImAccount(), tenant, chatAtRecords));
//            List<ChatSendRead> sendReads = chat.getSendReads();
//            if (CollectionUtils.isEmpty(sendReads)) {
//                return R.success(chat);
//            }
//            String contentByType = MediaType.getImContentByType(chat.getType(), chat.getContent());
//            SaasGlobalThreadPool.execute(() -> imService.sendJpushMessage(tenant, contentByType, sendReads));

        } catch (Exception var15) {
            log.error("环信发送消息失败", var15);
            var15.printStackTrace();
        }
        return R.success(chat);
    }


    @Deprecated
    @ApiOperation("获取角色未读的消息")
    @GetMapping({"/chat/first/roleType"})
    public R<List<ChatSendRead>> chatFirstByRoleType(@RequestParam("receiverRoleType")  String receiverRoleType) {
        List<ChatSendRead> chatDto = this.chatService.chatFirstByRoleType(receiverRoleType);
        return R.success(chatDto);
    }


    /**
     * 患者端使用的聊天记录接口。 过滤了 关注和取关的消息
     * @param receiverImAccount
     * @param createTime
     * @return
     */
    @ApiOperation("患者的聊天记录(不可见关注取关的自动推送)")
    @GetMapping({"anno/patient/chat/history"})
    public R chatPatientHistory (@RequestParam(value = "receiverImAccount") String receiverImAccount,
                                 @RequestParam(value = "size", required = false) Integer size,
                                 @RequestParam("createTime") Long createTime) {
        // 异步检查是否需要发送 欢迎患者的 话术: 您好，我是医生助理，为了让医生了解到您具体的需求，给到您对应的健康指导，请将您的症状或者需要咨询的内容直接发送过来！
        if (Objects.isNull(size)) {
            size = 5;
        }
        LocalDateTime localDateTime = new Date(createTime).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        List<Chat> chatDtos = this.chatService.getchatGroupHistory(receiverImAccount, "patient", localDateTime, size);
        return R.success(chatDtos);
    }

    @ApiOperation("查询@患者的记录")
    @GetMapping({"anno/chatAtPatientRecordList/{patientId}"})
    public R<List<ChatAtRecord>> chatAtPatientRecordList(@PathVariable("patientId") Long patientId) {

        LbqWrapper<ChatAtRecord> wrapper = Wraps.<ChatAtRecord>lbQ().eq(ChatAtRecord::getAtUserId, patientId)
                .eq(ChatAtRecord::getUserType, UserType.UCENTER_PATIENT)
                .orderByDesc(SuperEntity::getCreateTime);
        List<ChatAtRecord> chatAtRecords = chatAtRecordMapper.selectList(wrapper);
        return R.success(chatAtRecords);

    }

    /**
     * app 还在使用的 聊天记录的 信息
     * @param patientImAccount
     * @param createTime
     * @return
     */
    @ApiOperation("获取IM三人小组的聊天记录")
    @GetMapping({"anno/chat/group/history"})
    public R chatGroupHistory(@RequestParam(value = "patientImAccount") String patientImAccount,
                              @RequestParam("createTime") Long createTime,
                              @RequestParam(value = "createTimeString", required = false) String createTimeString) {
        LocalDateTime localDateTime = getMessageCreateTime(createTime, createTimeString);
        List<Chat> chatDtos = this.chatService.getchatGroupHistory(patientImAccount, localDateTime);
        return R.success(chatDtos);
    }

    /**
     * 医生端新的 聊天记录 详细
     * @param patientImAccount
     * @param doctorId
     * @param createTime
     * @return
     */
    @ApiOperation("获取图片聊天记录一次1条2.5")
    @GetMapping({"anno/chat/image"})
    public R chatImage(@RequestParam(value = "patientImAccount") String patientImAccount,
                               @RequestParam(value = "doctorId", required = false) Long doctorId,
                               @RequestParam(value = "currentUserId", required = false) Long currentUserId,
                               @RequestParam(value = "createTime", required = false) Long createTime,
                               @RequestParam(value = "createTimeString", required = false) String createTimeString,
                               @RequestParam("direction") Direction direction) {
        LocalDateTime localDateTime = getMessageCreateTime(createTime, createTimeString);
        // 需要查询医生可见的最早的消息时间
        LocalDateTime groupTime = null;
        if (Objects.nonNull(doctorId)) {
            R<DoctorGroup> doctorGroupTime = doctorGroupApi.getDoctorGroupTime(doctorId, patientImAccount);
            if (doctorGroupTime.getIsSuccess().equals(true)) {
                DoctorGroup doctorGroup = doctorGroupTime.getData();
                if (doctorGroup != null) {
                    groupTime = doctorGroup.getJoinGroupTime();
                }
            }
        }
        if (currentUserId != null) {
            BaseContextHandler.setUserId(currentUserId);
        }
        Chat chat = this.chatService.getChatImage(patientImAccount, localDateTime, groupTime, direction);
        return R.success(chat);
    }

    /**
     * 获取消息参数的一个创建时间
     * @param createTime
     * @param createTimeString
     * @return
     */
    private LocalDateTime getMessageCreateTime(Long createTime, String createTimeString) {
        LocalDateTime localDateTime;
        if (StrUtil.isNotEmpty(createTimeString)) {
            DateTimeFormatter timeFormatter;
            if (createTimeString.length() == 23) {
                timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            } else {
                timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            }
            localDateTime = LocalDateTime.parse(createTimeString, timeFormatter);
        } else {
            localDateTime = new Date(createTime).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        }
        return localDateTime;
    }



    @ApiOperation("更新IM提醒，设置他已填, 并回执")
    @GetMapping("updateImRemind")
    public R<Boolean> updateImRemind(@RequestParam(name = "chatId") Long chatId) {
        Chat chat = chatService.updateImRemind(chatId);
        if (Objects.isNull(chat)) {
            return R.success(true);
        }
        // 将 发送人 调整为 群组im账号(患者IM 账号。) 接收人为 患者的医生们。后续增加医助
        String imAccount = chat.getReceiverImAccount();
        String senderId = chat.getSenderId();
        String senderRoleType = chat.getSenderRoleType();

        Long doctorId = null;
        List<String> messageData = new ArrayList<>();
        if (UserType.UCENTER_NURSING_STAFF.equals(senderRoleType)) {

            // 发送人是医助本身。 接收的是患者的IM账号。
            // 回执消息直接 使用患者的IM 账号给医助的IM账号发送消息。
            messageData.add(chat.getSenderImAccount());

            R<Patient> patientR = patientApi.findPatientName(chat.getReceiverImAccount());
            if (patientR.getIsSuccess() && Objects.nonNull(patientR.getData())) {
                Patient data = patientR.getData();
                doctorId = data.getDoctorId();
            }
        } else if (UserType.UCENTER_DOCTOR.equals(senderRoleType)) {
            // 医生自己是发送人。
            doctorId = Long.parseLong(senderId);
        }
        if (Objects.nonNull(doctorId)) {
            // 查询医生 和 医生所在群组 的医生们的im账号。
            R<List<String>> readMyMessage = doctorGroupApi.getGroupDoctorListReadMyMessage(doctorId);
            if (readMyMessage.getIsSuccess()) {
                List<String> data = readMyMessage.getData();
                if (CollUtil.isNotEmpty(data)) {
                    messageData.addAll(data);
                }
            }
        }
        if (CollUtil.isNotEmpty(messageData)) {
            // 因为是患者发送给 医助医生的。所有接收消息的群组是患者的IM账号。发送人也是患者自己。
            imService.sendImRemindReceipt(messageData, chatId, imAccount, imAccount);
        }
        return R.success(true);

    }

    /**
     * 医生端新的 聊天记录 详细
     * @param patientImAccount
     * @param doctorId
     * @param createTime
     * @return
     */
    @ApiOperation("获取医生和患者群聊天记录2.4")
    @GetMapping({"anno/chat/history"})
    public R chatDoctorHistory(@RequestParam(value = "patientImAccount") String patientImAccount,
                               @RequestParam(value = "doctorId") Long doctorId,
                               @RequestParam(value = "size", required = false) Integer size,
                               @RequestParam(value = "createTime", required = false) Long createTime,
                               @RequestParam(value = "createTimeString", required = false) String createTimeString) {
        LocalDateTime localDateTime = getMessageCreateTime(createTime, createTimeString);
        // 需要查询医生可见的最早的消息时间
        LocalDateTime groupTime = null;
        R<DoctorGroup> doctorGroupTime = doctorGroupApi.getDoctorGroupTime(doctorId, patientImAccount);
        if (doctorGroupTime.getIsSuccess().equals(true)) {
            DoctorGroup doctorGroup = doctorGroupTime.getData();
            if (doctorGroup != null) {
                groupTime = doctorGroup.getJoinGroupTime();
            }
        }
        if (Objects.isNull(size)) {
            size = 5;
        }
        List<Chat> chatDtos = this.chatService.getDoctorChatGroupHistory(patientImAccount, localDateTime, groupTime, size);
        return R.success(chatDtos);
    }

    /**
     * 清除医生 所有的未读消息记录
     * 这个接口 清除的范围太广了， 不建议使用 yangshuai
     * @param doctorId
     * @return
     */
    @Deprecated
    @ApiOperation("修改医生未读消息为已读")
    @PostMapping({"/chat/refreshDoctorMsg/{doctorId}"})
    public R refreshDoctorNoReadMsgStatus(@PathVariable("doctorId") Long doctorId) {
        this.chatService.refreshMsgStatus(doctorId, UserType.UCENTER_DOCTOR);
        return R.success();
    }

    /**
     * 清除 患者 所有的未读消息记录
     * @param patientId
     * @return
     */
    @ApiOperation("修改患者未读消息为已读")
    @PostMapping({"/chat/refreshMsgStatus/{patientId}"})
    public R refreshPatientNoReadMsgStatus(@PathVariable("patientId") Long patientId) {
        this.chatService.refreshMsgStatus(patientId, UserType.UCENTER_PATIENT);
        return R.success();
    }

    @ApiOperation("获取IM的各种配置信息")
    @GetMapping({"/_config_"})
    public R<IMConfigDto> getIMConfig() {
        IMConfigDto imConfigDto = new IMConfigDto();
        imConfigDto.setAppName(IMConfigWrapper.getAppName())
                .setAccessToken(IMConfigWrapper.getAccessToken())
                .setClientId(IMConfigWrapper.getClientId())
                .setGrantType(IMConfigWrapper.getGrantType())
                .setOrgName(IMConfigWrapper.getOrgName());
        return R.success(imConfigDto);
    }

    @ApiOperation("获取Im是否在线")
    @GetMapping("imOnline/{imAccount}")
    public R<Boolean> imOnline(@PathVariable("imAccount") String imAccount) {

        boolean userOnline = imService.isUserOnline(imAccount);
        return R.success(userOnline);

    }

    @ApiOperation("获取医生首页待办的消息列表")
    @PostMapping("anno/doctorDealtWith")
    public R<IPage<ChatUserNewMsg>> doctorDealtWith(@RequestBody @Validated PageParams<ChatDoctorSharedMsgDTO> params) {

        IPage<ChatUserNewMsg> buildPage = params.buildPage();
        ChatDoctorSharedMsgDTO paramsModel = params.getModel();
        LbqWrapper<ChatUserNewMsg> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_DOCTOR);

        Long requestId = paramsModel.getRequestId();
        R<List<DoctorGroup>> otherDoctor = doctorGroupApi.getDoctorGroupOtherDoctor(requestId);
        List<DoctorGroup> data = null;
        // 当前医生和其他医生成为同一小组时 的入组时间
        IPage<ChatUserNewMsg> page = new Page();
        List<String> doctorIds = null;
        if (otherDoctor.getIsSuccess().equals(true)) {
            data = otherDoctor.getData();
            // 接口约定 null 说明医生没有小组 医生是个独立医生
            if (data == null) {
                doctorIds = new ArrayList<>(1);
                doctorIds.add(requestId.toString());
            } else {
                // 医生 要看的所有 的医患 消息
                if (!data.isEmpty()) {
                    doctorIds = new ArrayList<>(data.size());
                    for (DoctorGroup datum : data) {
                        doctorIds.add(datum.getDoctorId().toString());
                    }
                } else {
                    // 医生谁的聊天消息都不想看了。
                    return R.success(page);
                }
            }
        }
        page = chatUserNewMsgService.doctorDealtWith(buildPage,  doctorIds, paramsModel);
        // 处理消息列表中，医生对自己患者的备注。
        List<ChatUserNewMsg> records = page.getRecords();
        setPatientRemark(records, UserType.UCENTER_DOCTOR, requestId.toString());
        return R.success(page);

    }


    /**
     * 查询医生可以看到的 共享消息列表
     * @param params
     * @return
     */
    @ApiOperation("获取医生最新的共享消息列表2.4")
    @PostMapping("anno/doctorNewChatUser/shared")
    public R<IPage<ChatUserNewMsg>> getDoctorSharedMsgList(@RequestBody @Validated PageParams<ChatDoctorSharedMsgDTO> params) {

        IPage<ChatUserNewMsg> buildPage = params.buildPage();
        ChatDoctorSharedMsgDTO paramsModel = params.getModel();
        LbqWrapper<ChatUserNewMsg> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_DOCTOR);

        Long requestId = paramsModel.getRequestId();
        R<List<DoctorGroup>> otherDoctor = doctorGroupApi.getDoctorGroupOtherDoctor(requestId);
        List<DoctorGroup> data = null;
        // 当前医生和其他医生成为同一小组时 的入组时间
        Map<String, LocalDateTime> map = null;
        IPage<ChatUserNewMsg> page = new Page();
        List<String> doctorIds = null;
        if (otherDoctor.getIsSuccess().equals(true)) {
            data = otherDoctor.getData();
            // 接口约定 null 说明医生没有小组 医生是个独立医生
            if (data == null) {
                doctorIds = new ArrayList<>(1);
                doctorIds.add(requestId.toString());
            } else {
                // 医生 要看的所有 的医患 消息
                if (!data.isEmpty()) {
                    doctorIds = new ArrayList<>(data.size());
                    map = new HashMap<>(data.size());
                    for (DoctorGroup datum : data) {
                        doctorIds.add(datum.getDoctorId().toString());
                        map.put(datum.getDoctorId().toString(), datum.getJoinGroupTime());
                    }
                } else {

                    // 医生谁的聊天消息都不想看了。
                    return R.success(page);
                }
            }
        }
//        R<List<Long>> patientList = patientApi.doctorExitChatPatientList(requestId);
        List<Long> doctorExitChatPatientIds = new ArrayList<>();
//        if (patientList.getIsSuccess()) {
//            doctorExitChatPatientIds = patientList.getData();
//        }
        page = chatUserNewMsgService.page(buildPage, map, doctorIds, paramsModel, doctorExitChatPatientIds);
        // 处理消息列表中，医生对自己患者的备注。
        List<ChatUserNewMsg> records = page.getRecords();
        setPatientRemark(records, UserType.UCENTER_DOCTOR, requestId.toString());
        return R.success(page);

    }

    /**
     * 设置医生对自己的患者。或者医助对患者的备注
     * @param records
     * @param userType
     * @param requestId
     */
    public void setPatientRemark(List<ChatUserNewMsg> records, String userType, String requestId) {
        List<Long> patientIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(records)) {
            for (ChatUserNewMsg record : records) {
                record.setPatientRemark(null);
                if (requestId.equals(record.getRequestId())) {
                    patientIds.add(record.getPatientId());
                }
            }
        }
        if (CollUtil.isNotEmpty(patientIds)) {
            R<Map<Long, Patient>> patientBaseInfoByIds = patientApi.findPatientBaseInfoByIds(patientIds);
            if (patientBaseInfoByIds != null && patientBaseInfoByIds.getIsSuccess()) {
                Map<Long, Patient> patientMap = patientBaseInfoByIds.getData();
                for (ChatUserNewMsg record : records) {
                    Patient patient = patientMap.get(record.getPatientId());
                    if (Objects.nonNull(patient)) {
                        String doctorExitChatListJson = patient.getDoctorExitChatListJson();
                        record.setDoctorExitChat(0);
                        record.setNursingExitChat(patient.getNursingExitChat());
                        record.setSex(patient.getSex());
                        record.setBirthday(patient.getBirthday());
                        record.setStatus(patient.getStatus());
                        record.setPatientName(patient.getName());
                        record.setDiagnosisName(patient.getDiagnosisName());
                        if (userType.equals(UserType.UCENTER_DOCTOR)) {
                            if (StrUtil.isNotEmpty(doctorExitChatListJson)) {
                                if (doctorExitChatListJson.contains(requestId)) {
                                    record.setDoctorExitChat(1);
                                }
                            }
                            if (StrUtil.isNotEmpty(patient.getDoctorRemark())) {
                                record.setPatientRemark(patient.getDoctorRemark());
                            }
                        } else if (userType.equals(UserType.UCENTER_NURSING_STAFF)) {
                            if (StrUtil.isNotEmpty(patient.getRemark())) {
                                record.setPatientRemark(patient.getRemark());
                            }
                        }
                    }
                }
            }
        }

    }

    @ApiOperation("获取App最新的消息列表2.4")
    @PostMapping("anno/getAppMsgList")
    public R<IPage<ChatUserNewMsg>> getAppMsgList(@RequestBody @Validated PageParams<ChatUserNewMsgPageDTO> params) {

        IPage<ChatUserNewMsg> buildPage = params.buildPage();
        ChatUserNewMsgPageDTO paramsModel = params.getModel();
        String requestId = paramsModel.getRequestId();
        IPage<ChatUserNewMsg> page = chatUserNewMsgService.page(buildPage, paramsModel);
        List<ChatUserNewMsg> records = page.getRecords();
        setPatientRemark(records, UserType.UCENTER_NURSING_STAFF, requestId);
        return R.success(page);

    }

    @Deprecated
    @ApiOperation("获取im账号最新的消息列表2.3")
    @PostMapping("anno/newChatUser")
    public R<IPage<ChatUserNewMsg>> getDoctorMsgList(@RequestBody @Validated PageParams<ChatUserNewMsgPageDTO> params) {

        IPage<ChatUserNewMsg> buildPage = params.buildPage();
        ChatUserNewMsgPageDTO paramsModel = params.getModel();
        LbqWrapper<ChatUserNewMsg> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ChatUserNewMsg::getRequestId, paramsModel.getRequestId());
        if (StringUtils.isEmpty(paramsModel.getRequestRoleType())) {
            lbqWrapper.eq(ChatUserNewMsg::getRequestRoleType, UserType.UCENTER_DOCTOR);
        } else {
            lbqWrapper.eq(ChatUserNewMsg::getRequestRoleType, paramsModel.getRequestRoleType());
        }
        if (!StringUtils.isEmpty(paramsModel.getPatientName())) {
            lbqWrapper.like(ChatUserNewMsg::getPatientName, paramsModel.getPatientName());
        }
        if (Objects.nonNull(paramsModel.getDoctorId())) {
            lbqWrapper.apply(" patient_id in (select id from u_user_patient where doctor_id = " + paramsModel.getDoctorId() + ")");
        }
        if (Objects.nonNull(paramsModel.getGroupId())) {
            lbqWrapper.apply(" patient_id in (select id from u_user_patient where group_id = " + paramsModel.getGroupId() + ")");
        }
        lbqWrapper.orderByDesc(ChatUserNewMsg::getUpdateTime);
        IPage<ChatUserNewMsg> page = chatUserNewMsgService.page(buildPage, lbqWrapper);
        return R.success(page);

    }

    @ApiOperation("修改专员与患者未读消息为已读")
    @PostMapping({"/chat/refreshNursingStaffAndPatientMsg/{nursingId}"})
    public R refreshNursingStaffNoReadMsgStatus(@PathVariable("nursingId") Long nursingId, @RequestParam("groupIm") String groupIm) {
        this.chatService.refreshMsgStatus(nursingId, UserType.UCENTER_NURSING_STAFF, groupIm);
        return R.success();
    }

    @ApiOperation("修改医生与患者未读消息为已读")
    @PostMapping({"/chat/refreshDoctorAndPatientMsg/{doctorId}"})
    public R refreshDoctorMsg(@PathVariable("doctorId") Long doctorId, @RequestParam("groupIm") String groupIm) {
        this.chatService.refreshMsgStatus(doctorId, UserType.UCENTER_DOCTOR, groupIm);
        return R.success();
    }

    @ApiOperation("统计专员所有的未读消息")
    @GetMapping("countNursingStaffMsgNumber/{nursingId}")
    public R<Integer> countNursingStaffMsgNumber(@PathVariable("nursingId") Long nursingId) {
        Integer integer = chatService.countMsgNumber(nursingId, UserType.UCENTER_NURSING_STAFF);
        return R.success(integer);
    }

    @ApiOperation("统计医生所有的未读消息")
    @GetMapping("countDoctorMsgNumber/{doctorId}")
    public R<Integer> countDoctorMsgNumber(@PathVariable(name = "doctorId") Long doctorId) {
        Integer integer = chatService.countMsgNumber(doctorId, UserType.UCENTER_DOCTOR);
        return R.success(integer);
    }

    @ApiOperation("统计某时间之后有多少患者消息未读")
    @GetMapping("countPatientNumber/{userId}/{userType}")
    public R<Integer> countPatientNumber(@PathVariable(name = "userId") Long userId,
                                         @PathVariable(name = "userType") String userType,
                                         @RequestParam(name = "tenantCode") String tenantCode,
                                         @RequestParam(name = "createTimeString") String createTimeString) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(createTimeString, formatter);
        Integer integer = chatService.countPatientNumber(userId, userType, dateTime);
        return R.success(integer);
    }

    @ApiOperation("删除患者时，删除患者的IM账号和聊天记录")
    @DeleteMapping("cleanChatAll/{patientImAccount}")
    public R<Boolean> cleanChatAll(@PathVariable("patientImAccount") String patientImAccount) {
        chatUserNewMsgService.deleteByPatientImAccount(patientImAccount);
        return R.success(true);
    }


    @ApiOperation("web端使用的聊天记录")
    @PostMapping("getPatientChat")
    public R<IPage<Chat>> getChatSend(@RequestBody PageParams<ChatPatientPageDTO> params) {

        ChatPatientPageDTO paramsModel = params.getModel();
        PageParams<Chat> pageParams = new PageParams<>();
        pageParams.setSize(params.getSize());
        pageParams.setCurrent(params.getCurrent());
        IPage<Chat> buildPage = pageParams.buildPage();
        LbqWrapper<Chat> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.and(wrapper -> wrapper.eq(Chat::getReceiverImAccount, paramsModel.getImAccount()));
        if (paramsModel.getType() != null || !StringUtils.isEmpty(paramsModel.getContent())
                || paramsModel.getStartCreateTime() != null || paramsModel.getEndCreateTime() != null) {
            lbqWrapper.and(wrapper -> {
                if (paramsModel.getType() != null) {
                    wrapper.eq(Chat::getType, paramsModel.getType());
                }
                if (!StringUtils.isEmpty(paramsModel.getContent())) {
                    wrapper.like(Chat::getContent, paramsModel.getContent());
                }
                if (paramsModel.getStartCreateTime() != null && paramsModel.getEndCreateTime() != null) {
                    wrapper.between(Chat::getCreateTime, paramsModel.getStartCreateTime(), paramsModel.getEndCreateTime());
                } else if (paramsModel.getStartCreateTime() != null) {
                    wrapper.ge(Chat::getCreateTime, paramsModel.getStartCreateTime());
                } else if (paramsModel.getEndCreateTime() != null) {
                    wrapper.le(Chat::getCreateTime, paramsModel.getEndCreateTime());
                }
            });
        }
        lbqWrapper.orderByDesc(Chat::getCreateTime);
        IPage<Chat> page = chatService.page(buildPage, lbqWrapper);
        String currentUserType = paramsModel.getCurrentUserType();
        if (!StringUtils.isEmpty(currentUserType)) {
            BaseContextHandler.setUserType(currentUserType);
        }
        chatService.desensitization(buildPage.getRecords());
        return R.success(page);

    }

    @ApiOperation("批量清除医生和群组的未读记录")
    @PostMapping("clearChatNoReadHistory")
    public R<Boolean> clearChatNoReadHistory(@RequestBody ChatClearHistory chatClearHistory) {
        if (chatClearHistory.getUserId() == null ||
            StringUtils.isEmpty(chatClearHistory.getRoleType()) ||
            CollectionUtils.isEmpty(chatClearHistory.getGroupIds())) {
            return R.success(false);
        }
        chatService.refreshMsgStatus(chatClearHistory.getUserId(), chatClearHistory.getRoleType(), chatClearHistory.getGroupIds());
        return R.success(true);
    }

    @ApiOperation("清除非医生患者的未读消息（医生变更小组使用）")
    @PostMapping("clearChatNoReadHistoryForNoMyPatient")
    public R<Boolean> clearChatNoReadHistoryForNoMyPatient(@RequestBody ChatClearHistory chatClearHistory) {

        chatService.refreshMsgStatusNotInGroupIds(chatClearHistory.getUserId(), chatClearHistory.getRoleType(), chatClearHistory.getGroupIds());
        return R.success(true);
    }

    @ApiOperation("更新跟患者相关的所有信息（姓名， 头像）")
    @PostMapping("batchUpdatePatientForAllTable/{tenantCode}")
    public R<Boolean> batchUpdatePatientForAllTable(@PathVariable("tenantCode") String tenantCode, @RequestBody JSONObject patient) {

        BaseContextHandler.setTenant(tenantCode);
        chatBizService.batchUpdatePatientForAllTable(patient);

        return R.success(true);
    }

    @ApiOperation("更新消息为撤回状态，并发送通知给群组成员")
    @PutMapping("withdrawChat/{patientId}/{chatId}")
    public R<Boolean> withdrawChat(@PathVariable("patientId") Long patientId,
                                   @PathVariable("chatId") Long chatId) {

        Boolean withdrawChat = chatBizService.withdrawChat(patientId, chatId);
        return R.success(withdrawChat);
    }


    @ApiOperation("删除与某患者的会话")
    @DeleteMapping("deleteChatMsg/{chatNewMsgId}")
    public R<Boolean> deleteChatMsg(@PathVariable("chatNewMsgId") Long chatNewMsgId) {

        chatBizService.deleteChatMsg(chatNewMsgId);
        return R.success(true);
    }

    @ApiOperation("删除我的列表中的消息")
    @PutMapping("updateDeleteStatusChat/{userId}/{chatId}")
    public R<Boolean> updateDeleteStatusChat(@PathVariable("userId") Long userId,
                                   @PathVariable("chatId") Long chatId) {

        Boolean withdrawChat = chatBizService.updateDeleteStatusChat(userId, chatId);
        return R.success(withdrawChat);
    }

    @ApiOperation("获取消息记录中最新的信息")
    @PutMapping("getChatUserNewMsg/{userNewMsgId}")
    public R<ChatUserNewMsg> getChatUserNewMsg(@PathVariable("userNewMsgId") Long userNewMsgId) {

        ChatUserNewMsg userNewMsg = chatUserNewMsgService.findOne(userNewMsgId);
        return R.success(userNewMsg);

    }


    @ApiOperation("获取当前角色和患者群组的消息列表记录")
    @GetMapping("findChatUserNewMsg/{receiverImAccount}")
    public R<ChatUserNewMsg> findChatUserNewMsg(@PathVariable("receiverImAccount") String receiverImAccount) {

        ChatUserNewMsg userNewMsg = chatUserNewMsgService.findChatUserNewMsg(receiverImAccount);
        return R.success(userNewMsg);

    }

    @ApiOperation("删除医助或医生和患者的消息列表")
    @DeleteMapping("removeChatUserNewMsg/{receiverImAccount}")
    public R<Boolean> removeChatUserNewMsg(@PathVariable("receiverImAccount") String receiverImAccount,
                                           @RequestParam("requestId") String requestId,
                                           @RequestParam("requestRoleType") String requestRoleType) {
        Boolean status = chatUserNewMsgService.removeChatUserNewMsg(receiverImAccount, requestId, requestRoleType);
        return R.success(status);
    }


    /* 群发消息接口部分  */

    @ApiOperation("医助端 群发消息")
    @PostMapping({"/chat/sendGroup"})
    public R sendGroup(@RequestBody ChatGroupSend chatGroupSendDto) {
        try {
            this.chatGroupSendService.save(chatGroupSendDto);
            return R.success();
        } catch (Exception var5) {
            return R.fail("获取失败");
        }
    }


    @ApiOperation("群发消息记录")
    @PostMapping("getChatGroupSend")
    public R<IPage<ChatGroupSend>> getChatGroupSend(@RequestBody PageParams<ChatGroupSend> params) {

        IPage<ChatGroupSend> page = params.buildPage();

        ChatGroupSend paramsModel = params.getModel();
        LbqWrapper<ChatGroupSend> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ChatGroupSend::getSenderId, paramsModel.getSenderId());
        lbqWrapper.orderByDesc(ChatGroupSend::getCreateTime);
        chatGroupSendService.page(page, lbqWrapper);
        List<ChatGroupSend> chatGroupSends = page.getRecords();
        for (ChatGroupSend send : chatGroupSends) {
            try {
                if (send.getType().equals(MediaType.cms)) {
                    R<ChannelContent> baseContent = channelContentApi.getBaseContent(Long.parseLong(send.getContent()));
                    if (baseContent.getIsSuccess()) {
                        send.setChannelContent(baseContent.getData());
                    }
                }
            } catch (Exception e) {
            }
        }
        return R.success(page);

    }

    @ApiOperation("医生群发记录查询 小于5条则数据到底")
    @PostMapping("doctorGroupChatList")
    public R<List<ChatGroupSend>> getChatGroupSend(@RequestBody DoctorGroupSendPageDTO groupSendPageDTO) {

        List<ChatGroupSend> groupSends = chatGroupSendService.page(groupSendPageDTO);
        return R.success(groupSends);
    }


    @ApiOperation("删除一条群发消息")
    @DeleteMapping("deleteGroupSend/{id}")
    public R<Boolean> deleteGroupSend(@PathVariable Long id) {

        chatGroupSendService.deleteGroupSend(id);
        return R.success(true);

    }

    @ApiOperation("医生新的 群发消息")
    @PostMapping("saveChatGroupSend")
    public R<ChatGroupSend> saveChatGroupSend(@RequestBody ChatGroupSend chatGroupSend) {

        chatGroupSendService.saveChatGroupSend(chatGroupSend);
        return R.success(chatGroupSend);
    }

    @ApiOperation("回复超时的患者信息")
    @PostMapping("/replyPatientMessage")
    public R<String> replyPatientMessage() {
        ChatAutomaticReplyService service = SpringUtils.getBean(ChatAutomaticReplyService.class);
        service.replyPatientMessage();
        return R.success("success");
    }

    @ApiOperation("发送医助系统通知")
    @PostMapping("/sendAssistanceNotice")
    public R<String> sendAssistanceNotice(@RequestBody SendAssistanceNoticeDto sendAssistanceNoticeDto) {
        boolean notice = imService.sendAssistanceNotice(sendAssistanceNoticeDto.getAssistanceImAccount(), sendAssistanceNoticeDto.getMsgContent(), sendAssistanceNoticeDto.getMsgType());
        if (notice) {
            return R.success("success");
        } else {
            return R.fail("发送失败");
        }
    }

    @ApiOperation("查询患者给医生发送的最后一条消息")
    @GetMapping("/queryLastMsg")
    R<Chat> queryLastMsg(@RequestParam(name = "senderId") Long senderId,
                         @RequestParam("userType") String userType,
                         @RequestParam("receiverImAccount") String receiverImAccount) {

        Chat chat = chatService.queryLastMsg(senderId, userType, receiverImAccount);
        return R.success(chat);
    }


}
