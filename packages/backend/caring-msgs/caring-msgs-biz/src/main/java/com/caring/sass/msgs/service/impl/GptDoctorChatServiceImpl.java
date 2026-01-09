package com.caring.sass.msgs.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.cms.CmsKeyWordApi;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.JsonUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.bot.BotContext;
import com.caring.sass.msgs.bot.SseEmitterSession;
import com.caring.sass.msgs.dao.GptDoctorChatMapper;
import com.caring.sass.msgs.dto.DoctorSubscribeKeyWordReply;
import com.caring.sass.msgs.dto.GptDoctorChatSaveDTO;
import com.caring.sass.msgs.entity.GptDoctorChat;
import com.caring.sass.msgs.entity.OpenAiChatRequest;
import com.caring.sass.msgs.entity.OpenAiChatResponse;
import com.caring.sass.msgs.service.GptDoctorChatService;
import com.caring.sass.msgs.utils.im.IMUtils;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.dto.TenantAiInfoDTO;
import com.unfbx.chatgpt.entity.chat.Message;
import io.swagger.client.model.Msg;
import io.swagger.client.model.UserName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * GPT医生聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2023-02-10
 */
@Slf4j
@Service

public class GptDoctorChatServiceImpl implements GptDoctorChatService {

    @Autowired
    GptDoctorChatMapper baseMapper;

    @Autowired
    private BotContext botContext;

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    IMUtils imUtils;

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    DoctorApi doctorApi;


    @Autowired
    private CacheRepository cacheRepository;

    private static volatile int MAXI_MUM_POOL_SIZE = 2;

    private static final NamedThreadFactory GPT_NAMED_THREAD_FACTORY = new NamedThreadFactory("gpt-msg-", false);

    private static final ThreadPoolExecutor GPT_EXECUTOR = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(true), GPT_NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());


    /**
     * 根据配置文件中的限制的数量。
     * 动态更新线程池最大线程数量
     */
    private synchronized void monitorDoctorGptMaxiMumPoolSize() {
        int doctorGptMaxiMumPoolSize = ApplicationProperties.getDoctorGptMaxiMumPoolSize();
        if (doctorGptMaxiMumPoolSize != MAXI_MUM_POOL_SIZE && doctorGptMaxiMumPoolSize > 0) {
            log.info("doctorGptMaxiMumPoolSize change {}", doctorGptMaxiMumPoolSize);
            MAXI_MUM_POOL_SIZE = doctorGptMaxiMumPoolSize;
            GPT_EXECUTOR.setMaximumPoolSize(doctorGptMaxiMumPoolSize);
        }
    }

    @Autowired
    CmsKeyWordApi cmsKeyWordApi;


    public static void main(String[] args) {
        String a = "取消订阅#皮炎";

        System.out.println(a.substring(5));
    }

    /**
     * 医生发送消息
     *
     * @param chatSaveDTO
     * @return
     */
    @Override
    public GptDoctorChat sendMsg(GptDoctorChatSaveDTO chatSaveDTO) {
        if (!doctorCanSendMsg(chatSaveDTO.getSenderId())) {
            throw new BizException("正在回复，请稍等");
        }

        if (StrUtil.isEmpty(chatSaveDTO.getContent())) {
            throw new BizException("消息内容不能为空");
        }

        if (StrUtil.isEmpty(chatSaveDTO.getContent().trim())) {
            throw new BizException("消息内容不能为空");
        }

        // 保存提问内容
        GptDoctorChat gptDoctorChat = saveChat(chatSaveDTO);

        // 是否为取消订阅
        String content = gptDoctorChat.getContent();
        GptDoctorChat unSubscribeChat = unSubscribe(gptDoctorChat, content);
        if (unSubscribeChat != null) {
            return unSubscribeChat;
        }

        // 设置医生发送消息的redis key
        saveGptDoctorRedisLock(chatSaveDTO.getSenderId());
        String tenant = BaseContextHandler.getTenant();
        monitorDoctorGptMaxiMumPoolSize();

        String aiAssistantName = queryAssistant(tenant);
        String greetings = String.format("你是友好的%s，精通过敏医学领域知识。", aiAssistantName);

        try {
            GPT_EXECUTOR.execute(() ->
                    gptSendMsg(chatSaveDTO.getSenderId(), greetings, chatSaveDTO.getContent(),
                            gptDoctorChat.getId(), tenant, chatSaveDTO.getSenderImAccount(), chatSaveDTO.getContent()));
        } catch (Exception e) {
            gptDoctorChat.setSendStatus(GptDoctorChat.sendError);
            if (StrUtil.isNotEmpty(e.getMessage())) {
                if (e.getMessage().length() > 2000) {
                    gptDoctorChat.setSendErrorMsg(e.getMessage().substring(0, 2000));
                } else {
                    gptDoctorChat.setSendErrorMsg(e.getMessage());
                }
            }
            baseMapper.updateById(gptDoctorChat);

            // 很可能是线程数量不够用了
            String text = "不好意思，信息量太大，我没有反应过来，您等会儿再试一下";
            GptDoctorChat doctorChat = GptDoctorChat.builder()
                    .imGroupId(chatSaveDTO.getSenderId())
                    .senderImAccount(chatSaveDTO.getSenderImAccount())
                    .senderRoleType(GptDoctorChat.AiRole)
                    .content(text)
                    .type(MediaType.text.toString())
                    .replyMsgId(gptDoctorChat.getId())
                    .replyStatus(GptDoctorChat.sendError)
                    .sendStatus(GptDoctorChat.sendError)
                    .build();
            baseMapper.insert(doctorChat);
            sendHxImMsg(chatSaveDTO.getSenderImAccount(), doctorChat);
        }

        return gptDoctorChat;
    }

    /**
     * 取消订阅
     * 医生发送的内容是否 格式为 取消订阅# XXX
     *
     * @param content 会话内容
     */
    private GptDoctorChat unSubscribe(GptDoctorChat gptDoctorChat, String content) {
        if (!content.contains("取消订阅#")) {
            return null;
        }

        String trim = content.trim();
        R<Boolean> booleanR = cmsKeyWordApi.cancelSubscribeKeyWord(gptDoctorChat.getSenderId(), trim.substring(5));
        if (booleanR.getIsSuccess()) {
            Boolean data = booleanR.getData();
            if (data != null && data) {
                gptDoctorChat.setCancelKeyWordMsg(1);
            } else {
                gptDoctorChat.setCancelKeyWordMsg(2);
            }
        }
        return gptDoctorChat;
    }

    private String queryAssistant(String tenant) {
        R<TenantAiInfoDTO> aiInfoDTOR = tenantApi.queryAiInfo(tenant);
        String aiAssistantName = "AI助手";
        if (aiInfoDTOR.getIsSuccess()) {
            TenantAiInfoDTO infoDTORData = aiInfoDTOR.getData();
            if (Objects.nonNull(infoDTORData) && StrUtil.isNotEmpty(infoDTORData.getAiAssistantName())) {
                aiAssistantName = infoDTORData.getAiAssistantName();
            }
        }
        return aiAssistantName;
    }

    /**
     * 当 GPT异常的时候， 收集异常信息
     *
     * @param chatId
     * @param e
     * @return
     */
    private void gptErrorHandle(Long chatId, Exception e) {
        // 收集异常信息
        GptDoctorChat doctorChat = new GptDoctorChat();
        doctorChat.setId(chatId);
        doctorChat.setSendStatus(GptDoctorChat.sendError);
        if (StrUtil.isNotEmpty(e.getMessage())) {
            if (e.getMessage().length() > 2000) {
                doctorChat.setSendErrorMsg(e.getMessage().substring(0, 2000));
            } else {
                doctorChat.setSendErrorMsg(e.getMessage());
            }
        }
        baseMapper.updateById(doctorChat);
    }

    /**
     * gpt 发送消息
     *
     * @param user
     * @param content
     * @param chatId
     * @param tenantCode
     * @param imAccount
     * @return
     */
    public Boolean gptSendMsg(Long user, String greeting, String content, Long chatId, String tenantCode, String imAccount, String question) {
        BaseContextHandler.setTenant(tenantCode);
        String text;
        String replyStatus = null;
        try {
            text = botContext.chat(greeting, content, Convert.toStr(user));
            if (StrUtil.isNotEmpty(text)) {
                GptDoctorChat doctorChat = new GptDoctorChat();
                doctorChat.setId(chatId);
                doctorChat.setSendStatus(GptDoctorChat.sendSuccess);
                replyStatus = GptDoctorChat.sendSuccess;
                baseMapper.updateById(doctorChat);
            }
        } catch (Exception e) {
            gptErrorHandle(chatId, e);
            replyStatus = GptDoctorChat.sendError;
            text = "不好意思，信息量太大，我没有反应过来，您等会儿再试一下";
        } finally {
            clearDoctorSendMsgKey(user);
        }
        if (StrUtil.isNotEmpty(text)) {
            // 通过环信工具。发送给医生聊天页面。
            GptDoctorChat doctorChat = GptDoctorChat.builder()
                    .imGroupId(user)
                    .senderImAccount(imAccount)
                    .senderRoleType(GptDoctorChat.AiRole)
                    .content(text)
                    .type(MediaType.text.toString())
                    .replyStatus(replyStatus)
                    .replyMsgId(chatId)
                    .sendStatus(GptDoctorChat.sendSuccess)
                    .build();
            baseMapper.insert(doctorChat);
            sendHxImMsg(imAccount, doctorChat);
        }
        return true;
    }

    /**
     * 收到gpt回复。通过环信发送给用户
     *
     * @param imAccount
     * @param doctorChat
     * @return
     */
    private boolean sendHxImMsg(String imAccount, GptDoctorChat doctorChat) {
        Msg msg = new Msg();
        UserName userName = new UserName();
        userName.add(imAccount);
        if (userName.isEmpty()) {
            return true;
        }
        msg.targetType("users").target(userName);
        JSONObject m = JsonUtils.bean2JSONObject(doctorChat);
        m.put("type", "txt");
        m.put("msg", doctorChat.getContent());
        msg.msg(m);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(doctorChat));
        jsonObject.put("id", doctorChat.toString());
        msg.ext(jsonObject);
        msg.from(imAccount);
        return imUtils.sendMsg(msg);
    }

    /**
     * 清除医生发送环信消息的key
     *
     * @param doctorId
     */
    private void clearDoctorSendMsgKey(Long doctorId) {
        String redisKey = getRedisKey(doctorId);
        redisTemplate.delete(redisKey);
    }

    /**
     * 检查这个医生是否可以继续发送消息
     *
     * @param doctorId
     * @return
     */
    private boolean doctorCanSendMsg(Long doctorId) {
        String redisKey = getRedisKey(doctorId);
        String createTime = redisTemplate.opsForValue().get(redisKey);
        if (StrUtil.isNotEmpty(createTime)) {
            // 上次缓存的时间， 小于当前时间。 则拒绝此处请求。
            long parseLong = Convert.toLong(createTime);
            long timeMillis = System.currentTimeMillis();
            if (timeMillis < parseLong) {
                return false;
            }
            redisTemplate.delete(redisKey);
        }
        return true;
    }

    /**
     * 医生发送了一条消息，在redis 中增加一个过期时间
     *
     * @param doctorId
     */
    private void saveGptDoctorRedisLock(Long doctorId) {
        long timeMillis = System.currentTimeMillis() + (1000 * 120);
        String redisKey = getRedisKey(doctorId);
        redisTemplate.opsForValue().set(redisKey, timeMillis + "", 1000 * 120, TimeUnit.MILLISECONDS);
    }


    /**
     * 计算 redis 使用的 key
     *
     * @param doctorId
     * @return
     */
    private String getRedisKey(Long doctorId) {
        String tenant = BaseContextHandler.getTenant();
        return tenant + doctorId;
    }

    /**
     * 查询20条最新的消息给医生
     *
     * @param doctorId
     * @param createTime
     * @return
     */
    @Override
    public List<GptDoctorChat> chatListPage(Long doctorId, LocalDateTime createTime) {
        IPage<GptDoctorChat> buildPage = new Page(1, 20);
        LbqWrapper<GptDoctorChat> lbqWrapper = Wraps.<GptDoctorChat>lbQ()
                .eq(GptDoctorChat::getImGroupId, doctorId)
                .lt(GptDoctorChat::getCreateTime, createTime)
                .orderByDesc(GptDoctorChat::getCreateTime);
        IPage<GptDoctorChat> selectPage = baseMapper.selectPage(buildPage, lbqWrapper);
        List<GptDoctorChat> records = selectPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return records;
    }

    /**
     * 查询最新的一条消息
     * @param doctorId
     * @return
     */
    @Override
    public GptDoctorChat lastNewMessage(Long doctorId) {

        GptDoctorChat doctorChat = baseMapper.selectOne(Wraps.<GptDoctorChat>lbQ()
                .eq(GptDoctorChat::getImGroupId, doctorId)
                .orderByDesc(GptDoctorChat::getCreateTime)
                .last(" limit 0,1 "));
        return doctorChat;
    }

    /**
     * 确认医生可以发送消息的状态
     *
     * @param doctorId
     * @return
     */
    @Override
    public boolean doctorSendMsgStatus(Long doctorId) {
        return doctorCanSendMsg(doctorId);
    }


    /**
     * 检查医生是否收过 敏智介绍。未收过 则推送一条
     *
     * @param doctorId
     */
    @Override
    public void checkSendAiIntroduction(Long doctorId, String imAccount) {

        LbqWrapper<GptDoctorChat> wrapper = Wraps.<GptDoctorChat>lbQ().eq(GptDoctorChat::getSenderImAccount, imAccount)
                .eq(GptDoctorChat::getSenderRoleType, GptDoctorChat.AiRole)
                .eq(GptDoctorChat::getContent, "ASSISTANT_INTRODUCTION");
        Integer count = baseMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return;
        }

        // 发送 ai介绍 提醒
        GptDoctorChat doctorChat = GptDoctorChat.builder()
                .imGroupId(doctorId)
                .senderId(doctorId)
                .senderImAccount(imAccount)
                .senderRoleType(GptDoctorChat.AiRole)
                .content("ASSISTANT_INTRODUCTION")
                .type(MediaType.text.toString())
                .replyStatus(GptDoctorChat.sendSuccess)
                .sendStatus(GptDoctorChat.sendSuccess)
                .build();
        baseMapper.insert(doctorChat);
        sendHxImMsg(imAccount, doctorChat);

    }

    /**
     * 保存医生订阅关键字的文字消息，并im推送如何取消订阅
     *
     * @param subscribeKeyWordReply
     */
    @Override
    public void doctorSubscribeReply(DoctorSubscribeKeyWordReply subscribeKeyWordReply) {
        @NotNull Long doctorId = subscribeKeyWordReply.getDoctorId();
        @NotEmpty String imAccount = subscribeKeyWordReply.getImAccount();
        @NotEmpty String keyWord = subscribeKeyWordReply.getKeyWord();
        GptDoctorChat doctorChat = GptDoctorChat.builder()
                .imGroupId(doctorId)
                .senderImAccount(imAccount)
                .senderRoleType(UserType.UCENTER_DOCTOR)
                .content(keyWord)
                .type(MediaType.text.toString())
                .replyStatus(GptDoctorChat.sendSuccess)
                .sendStatus(GptDoctorChat.sendSuccess)
                .build();
        baseMapper.insert(doctorChat);
        doctorChat.setCreateTime(null);
        doctorChat.setUpdateTime(null);

        String[] split = keyWord.split("、");
        String s = split[0];
        doctorChat.setReplyMsgId(doctorChat.getId());
        doctorChat.setId(null);
        doctorChat.setSenderRoleType(GptDoctorChat.AiRole);
        doctorChat.setType("HTML");
        doctorChat.setContent("<div>" +
                "  已为您订阅，敏智将会整理关键词下最新的医学内容推送给到您!若需取消订阅，回复:取消订阅+\"#\"+关键词，如:取消订阅" +
                "  <span style=\"color: #5592FC\">#" + s + "</span>\n" +
                "</div>");
        baseMapper.insert(doctorChat);

        sendHxImMsg(imAccount, doctorChat);

    }

    /**
     * 发送给医生 一篇文章
     *
     * @param chatSaveDTO
     */
    @Override
    public void sendCms(GptDoctorChatSaveDTO chatSaveDTO) {
        GptDoctorChat doctorChat = GptDoctorChat.builder()
                .imGroupId(chatSaveDTO.getSenderId())
                .senderImAccount(chatSaveDTO.getSenderImAccount())
                .senderRoleType(GptDoctorChat.AiRole)
                .content(chatSaveDTO.getContent())
                .type("KEY_WORD_CMS")
                .replyStatus(GptDoctorChat.sendSuccess)
                .sendStatus(GptDoctorChat.sendSuccess)
                .build();
        baseMapper.insert(doctorChat);

        // 可能要增加 微信公众号模板

        sendHxImMsg(chatSaveDTO.getSenderImAccount(), doctorChat);

    }

    /**
     * 获取会话上一次内容
     *
     * @param sessionId 会话标识
     */
    public GptDoctorChat queryLastChatBySessionId(String sessionId) {
        List<GptDoctorChat> chats = baseMapper.selectList(Wraps.<GptDoctorChat>lbQ()
                .eq(GptDoctorChat::getSessionId, sessionId)
                .orderByDesc(GptDoctorChat::getCreateTime));
        if (CollUtil.isNotEmpty(chats)) {
            return chats.get(0);
        }
        return null;
    }

    @Override
    public SseEmitter createSse(String uid) {
        //默认2分钟超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);
        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            SseEmitterSession.remove(uid);
            try {
                String cacheValue = cacheRepository.get(uid);
                if (StrUtil.isBlank(cacheValue)) {
                    return;
                }
                List<String> array = StrUtil.split(cacheValue, '#', 2);
                BaseContextHandler.setTenant(array.get(0));
                GptDoctorChat chat = queryLastChatBySessionId(uid);
                if (chat == null) {
                    return;
                }
                GptDoctorChat doctorChat = GptDoctorChat.builder()
                        .imGroupId(chat.getSenderId())
                        .sessionId(uid)
                        .senderImAccount(chat.getSenderImAccount())
                        .senderRoleType(GptDoctorChat.AiRole)
                        .content(array.get(1))
                        .type(MediaType.text.toString())
                        .replyStatus(GptDoctorChat.sendSuccess)
                        .replyMsgId(chat.getId())
                        .sendStatus(GptDoctorChat.sendSuccess)
                        .build();
                baseMapper.insert(doctorChat);
                cacheRepository.del(uid);
            } catch (Exception e) {
                log.error("保存ai回复内容异常", e);
            }
        });

        //超时回调
        sseEmitter.onTimeout(() -> {
            log.info("[{}]连接超时...................", uid);
        });
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.info("[{}]连接异常,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data(Message.builder().content("发生异常请重试！").build())
                                .reconnectTime(3000));
                        SseEmitterSession.put(uid, sseEmitter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("[{}]创建sse连接成功！", uid);
        return sseEmitter;
    }


    @Override
    public void closeSse(String uid) {
        SseEmitter sse = SseEmitterSession.get(uid);
        if (Objects.isNull(sse)) {
            return;
        }
        sse.complete();
        //移除
        SseEmitterSession.remove(uid);
    }


    @Override
    public GptDoctorChat sseChat(GptDoctorChatSaveDTO doctorChatSaveDTO) {
        // 保存提问内容
        GptDoctorChat gptDoctorChat = saveChat(doctorChatSaveDTO);

        // 是否为取消订阅
        String content = gptDoctorChat.getContent();
        GptDoctorChat unSubscribeChat = unSubscribe(gptDoctorChat, content);
        String uid = doctorChatSaveDTO.getUid();
        if (unSubscribeChat != null) {
            // 关闭流，此处不需要
            closeSse(uid);
            return gptDoctorChat;
        }

        OpenAiChatRequest chatRequest = new OpenAiChatRequest();
        String aiAssistantName = queryAssistant(doctorChatSaveDTO.getTenantCode());
        String greetings = String.format("你叫%s，是一名服务于医生的人工智能助手，精通过敏等医学领域知识。", aiAssistantName);
        chatRequest.setGreeting(greetings);
        chatRequest.setMessage(doctorChatSaveDTO.getContent());
        chatRequest.setUser(Convert.toStr(doctorChatSaveDTO.getSenderId()));
        try {
            OpenAiChatResponse openAiChatResponse = botContext.sseChat(uid, chatRequest);
            gptDoctorChat.setQuestionTokens(openAiChatResponse.getQuestionTokens());
        } catch (Exception e) {
            gptDoctorChat.setSendStatus(GptDoctorChat.sendError);
            gptDoctorChat.setSendErrorMsg(StrUtil.sub(e.getMessage(), 0, 2000));
        }

        gptDoctorChat.setSendStatus(GptDoctorChat.sendSuccess);
        baseMapper.updateById(gptDoctorChat);
        return gptDoctorChat;
    }

    /**
     * 保存用户请求的会话
     */
    private GptDoctorChat saveChat(GptDoctorChatSaveDTO doctorChatSaveDTO) {
        GptDoctorChat gptDoctorChat = new GptDoctorChat();
        BeanUtils.copyProperties(doctorChatSaveDTO, gptDoctorChat);
        gptDoctorChat.setSendStatus(GptDoctorChat.sending);
        gptDoctorChat.setSessionId(doctorChatSaveDTO.getUid());
        baseMapper.insert(gptDoctorChat);
        return gptDoctorChat;
    }

}
