package com.caring.sass.user.service.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.entity.ArticleOther;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.dto.KeywordChatDto;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.redis.KeywordProjectSettingsRedisDTO;
import com.caring.sass.user.redis.KeywordReplyRedisDTO;
import com.caring.sass.user.redis.KeywordSettingRedisDTO;
import com.caring.sass.user.service.*;
import com.caring.sass.user.service.impl.ImService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName KeywordChatRedisCallBackCenter
 * @Description 处理患者发送的文本消息。如果触发了关键字。则发送关键字对应的消息内容。并记录关键字触发次数
 * @Author yangShuai
 * @Date 2021/11/19 18:16
 * @Version 1.0
 */
@Slf4j
@Component
public class KeywordChatRedisCallBackCenter {

    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("patient-chat-redis-keyword-callback-", false);


    private final RedisTemplate<String, String> redisTemplate;

    private final KeywordProjectSettingsService keywordProjectSettingsService;

    private final KeywordSettingService keywordSettingService;

    private final KeywordReplyService keywordReplyService;

    private final ImService imService;

    private final PatientService patientService;

    private final NursingStaffService nursingStaffService;

    private final ChannelContentApi channelContentApi;

    public KeywordChatRedisCallBackCenter( RedisTemplate<String, String> redisTemplate,
                                           KeywordProjectSettingsService keywordProjectSettingsService,
                                           KeywordReplyService keywordReplyService,
                                           KeywordSettingService keywordSettingService,
                                           PatientService patientService,
                                           NursingStaffService nursingStaffService,
                                           ChannelContentApi channelContentApi,
                                           ImService imService) {

        this.redisTemplate = redisTemplate;
        this.keywordProjectSettingsService = keywordProjectSettingsService;
        this.keywordSettingService = keywordSettingService;
        this.imService = imService;
        this.patientService = patientService;
        this.nursingStaffService = nursingStaffService;
        this.keywordReplyService = keywordReplyService;
        this.channelContentApi = channelContentApi;
        THREAD_FACTORY.newThread(this::running).start();
    }

    private void running() {
        String message = null;
        while (true) {
            try {
                message = redisTemplate.opsForList().rightPop(SaasRedisBusinessKey.PATIENT_MESSAGE_KEYWORD_LIST, 3, TimeUnit.SECONDS);
            } catch (Exception e) {
            }
            if (StringUtils.isNotEmptyString(message)) {
                log.info("收到患者发送的消息。 {}", message);
                KeywordChatDto keywordChatDto = JSONObject.parseObject(message, KeywordChatDto.class);
                String tenantCode = keywordChatDto.getTenantCode();
                BaseContextHandler.setTenant(tenantCode);
                // 把消息交给 线程组去处理吧。
                // 先判断一下 项目有没有开启 关键字回复功能。 没有开启，则跳过任务
                KeywordProjectSettingsRedisDTO projectSetting = keywordProjectSettingsService.getKeywordProjectSetting();
                if (Objects.nonNull(projectSetting)) {
                    if (KeyWordEnum.open.toString().equals(projectSetting.getKeywordReplySwitch())) {
                        SaasGlobalThreadPool.execute(() -> handleMessage(projectSetting, keywordChatDto));
                    }
                }

            }
        }

    }


    /**
     * 一个规则 只计算一次触发次数
     * @param projectSetting
     * @param keywordChatDto
     */
    private void handleMessage(KeywordProjectSettingsRedisDTO projectSetting, KeywordChatDto keywordChatDto) {

        String tenantCode = keywordChatDto.getTenantCode();
        BaseContextHandler.setTenant(tenantCode);
        Long patientId = keywordChatDto.getPatientId();
        Patient patient = patientService.getBasePatientById(patientId);
        if (Objects.isNull(patient)) {
            return;
        }
        String keywordReplyForm = projectSetting.getKeywordReplyForm();
        if (StrUtil.isEmpty(keywordReplyForm)) {
            log.info("自动回复使用的身份未知。不进行发布");
            return;
        }
        NursingStaff nursingStaff = null;
        if (KeyWordEnum.medical_assistance.toString().equals(keywordReplyForm)) {
            // 查询这个 患者的医助
            nursingStaff = nursingStaffService.getBaseNursingStaffById(patient.getServiceAdvisorId());
        }
        // 检查患者发送的消息里面有没有项目符合的关键字
        List<KeywordSettingRedisDTO> redisDTOS = keywordSettingService.matchingKeywordReturn(keywordChatDto.getContent());
        if (CollUtil.isEmpty(redisDTOS)) {
            return;
        }
        Set<Long> keywordReplyIdSet = new HashSet<>();
        for (KeywordSettingRedisDTO redisDTO : redisDTOS) {
            if (redisDTO.getKeywordReplyId() == null) {
                continue;
            }
            // 获取规则的回复内容。
            KeywordReplyRedisDTO keywordReplyRedis = keywordReplyService.getKeywordReplyRedis(redisDTO.getKeywordReplyId());
            if (Objects.isNull(keywordReplyRedis)) {
                continue;
            }
            if (StrUtil.isEmpty(keywordReplyRedis.getReplyContent())) {
                continue;
            }

            if (!keywordReplyIdSet.contains(redisDTO.getKeywordReplyId())) {
                // 规则触发次数 增加
                // TODO:本地记录一下规则触发记录
                // 根据回复内容。 接收人。 发送人角色。 发送一条环信消息
                JSONArray chatSendReads = keywordChatDto.getChatSendReads();
                List<ChatSendRead> sendReads = JSONArray.parseArray(chatSendReads.toJSONString(), ChatSendRead.class);
                Chat chat = new Chat();
                if (KeyWordEnum.text.toString().equals(keywordReplyRedis.getReplyType())) {
                    chat.setType(MediaType.text);
                    chat.setContent(keywordReplyRedis.getReplyContent());
                } else {
                    chat.setType(MediaType.cms);
                    // 是一篇文章
                    // 查询文件内容
                    R<ChannelContent> baseContent = channelContentApi.getBaseContent(Long.parseLong(keywordReplyRedis.getReplyContent()));
                    if (!baseContent.getIsSuccess()) {
                        continue;
                    }
                    ChannelContent channelContent = baseContent.getData();
                    if (channelContent == null) {
                        continue;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", channelContent.getId().toString());
                    jsonObject.put("icon", channelContent.getIcon());
                    jsonObject.put("title", channelContent.getTitle());
                    jsonObject.put("summary", channelContent.getSummary());
                    chat.setContent(jsonObject.toJSONString());
                }
                chat.setSystemMessage(1);
                chat.setHistoryVisible(1);
                // 设置im 发送人的信息
                imService.setSenderMessage(chat, keywordReplyForm, nursingStaff);
                // 设置消息接收人的信息
                imService.setOtherMessage(chat, patient);
                chat.setSendReads(sendReads);

                imService.sendChat(chat);
                keywordReplyService.createPatientTriggerRecord(redisDTO.getKeywordReplyId(), patientId);
            }
            keywordReplyIdSet.add(redisDTO.getKeywordReplyId());
            // 对关键字 设置触发次数记录
            keywordSettingService.createPatientTriggerRecord(redisDTO.getId(), patientId);
        }


    }


}
