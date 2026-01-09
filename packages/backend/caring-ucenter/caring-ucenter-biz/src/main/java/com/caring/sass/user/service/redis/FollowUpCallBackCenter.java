package com.caring.sass.user.service.redis;


import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.msgs.entity.ChatSendRead;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.dto.FollowUpChatDto;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.redis.FollowReplyRedisDTO;
import com.caring.sass.user.service.FollowReplyService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.user.service.impl.ImService;
import com.caring.sass.user.service.impl.WeiXinService;
import com.caring.sass.wx.dto.template.CommonTemplateServiceWorkModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 患者关注后的消息
 */
@Slf4j
@Component
public class FollowUpCallBackCenter {

    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("patient-follow-up-redis-callback-", false);


    private final RedisTemplate<String, String> redisTemplate;

    private final WeiXinService weiXinService;

    private final PatientService patientService;

    private final FollowReplyService followReplyService;

    private final ImService imService;

    private final NursingStaffService nursingStaffService;

    private final ChannelContentApi channelContentApi;

    private final TenantApi tenantApi;

    public FollowUpCallBackCenter(RedisTemplate<String, String> redisTemplate,
                                  WeiXinService weiXinService,
                                  PatientService patientService,
                                  FollowReplyService followReplyService,
                                  ImService imService,
                                  NursingStaffService nursingStaffService,
                                  ChannelContentApi channelContentApi,
                                  TenantApi tenantApi) {
        this.redisTemplate = redisTemplate;
        this.weiXinService = weiXinService;
        this.patientService = patientService;
        this.followReplyService = followReplyService;
        this.imService = imService;
        this.nursingStaffService = nursingStaffService;
        this.channelContentApi = channelContentApi;
        this.tenantApi = tenantApi;
        THREAD_FACTORY.newThread(this::running).start();
    }

    private void running() {
        String message = null;
        while (true) {
            try {
                message = redisTemplate.opsForList().rightPop(SaasRedisBusinessKey.PATIENT_FOLLOW_UP_EVENT, 3, TimeUnit.SECONDS);
            } catch (Exception e) {
            }
            if (StringUtils.isNotEmptyString(message)) {
                FollowUpChatDto followUpChatDto = JSONObject.parseObject(message, FollowUpChatDto.class);
                String tenantCode = followUpChatDto.getTenantCode();
                BaseContextHandler.setTenant(tenantCode);
                // 把消息交给 线程组去处理吧。
                // 先判断一下 项目有没有开启 关键字回复功能。 没有开启，则跳过任务
                FollowReplyRedisDTO followReplyRedis = followReplyService.getFollowReplyRedis(KeyWordEnum.Reply_after_following.toString());
                if (Objects.nonNull(followReplyRedis)) {
                    if (KeyWordEnum.open.toString().equals(followReplyRedis.getFunctionSwitch())) {
                        SaasGlobalThreadPool.execute(() -> handleMessage(followUpChatDto, followReplyRedis));
                    }
                }

            }
        }

    }


    private void handleMessage(FollowUpChatDto followUpChatDto, FollowReplyRedisDTO followReplyRedis) {
        BaseContextHandler.setTenant(followUpChatDto.getTenantCode());
        Long patientId = followUpChatDto.getPatientId();
        Patient patient = patientService.getBasePatientById(patientId);
        if (Objects.isNull(patient)) {
            return;
        }
        String keywordReplyForm = followReplyRedis.getReplyForm();
        if (StrUtil.isEmpty(keywordReplyForm)) {
            log.info("自动回复使用的身份未知。不进行发布");
            return;
        }
        NursingStaff nursingStaff = null;
        if (KeyWordEnum.medical_assistance.toString().equals(keywordReplyForm)) {
            // 查询这个 患者的医助
            nursingStaff = nursingStaffService.getBaseNursingStaffById(patient.getServiceAdvisorId());
        }

        Chat chat = new Chat();
        List<ChatSendRead> sendReads = new ArrayList<>();
        // 只把患者自己加入到 接收人中去。
        String firstTitle = null;
        String keyword1Title = null;
        if (KeyWordEnum.text.toString().equals(followReplyRedis.getReplyType())) {
            chat.setType(MediaType.text);
            chat.setContent(followReplyRedis.getReplyContent());
            firstTitle = "关注后自动回复";
            keyword1Title = followReplyRedis.getReplyContent();
        } else {
            chat.setType(MediaType.cms);
            // 是一篇文章
            // 查询文件内容
            R<ChannelContent> baseContent = channelContentApi.getBaseContent(Long.parseLong(followReplyRedis.getReplyContent()));
            if (!baseContent.getIsSuccess()) {
                return;
            }
            ChannelContent channelContent = baseContent.getData();
            if (channelContent == null) {
                return;
            }
            keyword1Title = channelContent.getTitle();
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

        imService.setOtherMessage(chat, patient);
        // 设置消息接收人的信息
        ChatSendRead sendRead = imService.createPatientSendRead(patient.getImAccount(), patient);
        sendReads.add(sendRead);
        chat.setSendReads(sendReads);

        imService.sendChat(chat);
        weiXinService.sendFollowUpMessage(patient.getOpenId(),
                followUpChatDto.getTenantCode(),
                firstTitle,
                keyword1Title,
                CommonTemplateServiceWorkModel.HEALTH_SERVICE_CLAIM_NOTIFICATION,
                patient.getId(),
                KeyWordEnum.Reply_after_following);


    }


}
