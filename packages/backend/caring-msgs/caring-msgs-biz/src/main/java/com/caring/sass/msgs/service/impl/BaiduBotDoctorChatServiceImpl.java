package com.caring.sass.msgs.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.bot.BaiduThirdCommonV1DiagnoseBot;
import com.caring.sass.msgs.bot.SseEmitterSession;
import com.caring.sass.msgs.dao.BaiduBotDoctorChatMapper;
import com.caring.sass.msgs.dto.BaiduBotDoctorChatSaveDTO;
import com.caring.sass.msgs.entity.BaiduBotDoctorChat;
import com.caring.sass.msgs.entity.GptDoctorChat;
import com.caring.sass.msgs.entity.OpenAiChatRequest;
import com.caring.sass.msgs.service.BaiduBotDoctorChatService;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 百度灵医BOT医生聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-02-29
 */
@Slf4j
@Service

public class BaiduBotDoctorChatServiceImpl extends SuperServiceImpl<BaiduBotDoctorChatMapper, BaiduBotDoctorChat> implements BaiduBotDoctorChatService {


    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private BaiduThirdCommonV1DiagnoseBot baiduThirdCommonV1DiagnoseBot;

    /**
     * 获取会话上一次内容
     *
     * @param sessionId 会话标识
     */
    public BaiduBotDoctorChat queryLastChatBySessionId(String sessionId) {
        List<BaiduBotDoctorChat> chats = baseMapper.selectList(Wraps.<BaiduBotDoctorChat>lbQ()
                .eq(BaiduBotDoctorChat::getSessionId, sessionId)
                .orderByDesc(BaiduBotDoctorChat::getCreateTime));
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
                BaiduBotDoctorChat chat = queryLastChatBySessionId(uid);
                if (chat == null) {
                    return;
                }
                BaiduBotDoctorChat doctorChat = BaiduBotDoctorChat.builder()
                        .sessionId(uid)
                        .senderRoleType(BaiduBotDoctorChat.AiRole)
                        .content(array.get(1))
                        .type(MediaType.text.toString())
                        .replyStatus(BaiduBotDoctorChat.sendSuccess)
                        .replyMsgId(chat.getId())
                        .senderId(chat.getSenderId())
                        .sendStatus(BaiduBotDoctorChat.sendSuccess)
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

    /**
     * 保存用户请求的会话
     */
    private BaiduBotDoctorChat saveChat(BaiduBotDoctorChatSaveDTO doctorChatSaveDTO) {
        BaiduBotDoctorChat botDoctorChat = new BaiduBotDoctorChat();
        BeanUtils.copyProperties(doctorChatSaveDTO, botDoctorChat);
        botDoctorChat.setSendStatus(GptDoctorChat.sending);
        botDoctorChat.setSessionId(doctorChatSaveDTO.getSessionId());
        baseMapper.insert(botDoctorChat);
        return botDoctorChat;
    }

    @Override
    public BaiduBotDoctorChat sseChat(BaiduBotDoctorChatSaveDTO baiduBotDoctorChatSaveDTO) {
        // 保存提问内容
        BaiduBotDoctorChat botDoctorChat = saveChat(baiduBotDoctorChatSaveDTO);

        String sessionId = baiduBotDoctorChatSaveDTO.getSessionId();

        // 添加几轮上下文 ？
        List<BaiduBotDoctorChat> doctorChats = baseMapper.selectList(Wraps.<BaiduBotDoctorChat>lbQ()
                .eq(BaiduBotDoctorChat::getSenderId, baiduBotDoctorChatSaveDTO.getSenderId())
                .orderByDesc(BaiduBotDoctorChat::getCreateTime)
                .last(" limit 0, 5 "));
        if (CollUtil.isEmpty(doctorChats)) {
            return botDoctorChat;
        }
        // 如果上下文是双数。则去掉最早的一条。
        if (doctorChats.size() % 2 == 0) {
            doctorChats.remove(doctorChats.size() -1);
        }
        doctorChats.sort((o1, o2) -> {
            if (o1.getCreateTime().isAfter(o2.getCreateTime())) {
                return 1;
            } else {
                return -1;
            }
        });
        try {
            baiduThirdCommonV1DiagnoseBot.sendBaiduBot(sessionId, doctorChats);
            botDoctorChat.setSendStatus(BaiduBotDoctorChat.sendSuccess);
        } catch (Exception e) {
            botDoctorChat.setSendStatus(BaiduBotDoctorChat.sendError);
            botDoctorChat.setSendErrorMsg(StrUtil.sub(e.getMessage(), 0, 2000));
            e.printStackTrace();
        }
        baseMapper.updateById(botDoctorChat);
        return botDoctorChat;
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
    public List<BaiduBotDoctorChat> chatListPage(Long doctorId, LocalDateTime createTime) {

        IPage<BaiduBotDoctorChat> buildPage = new Page(1, 10);
        LbqWrapper<BaiduBotDoctorChat> lbqWrapper = Wraps.<BaiduBotDoctorChat>lbQ()
                .eq(BaiduBotDoctorChat::getSenderId, doctorId)
                .lt(BaiduBotDoctorChat::getCreateTime, createTime)
                .orderByDesc(BaiduBotDoctorChat::getCreateTime);
        IPage<BaiduBotDoctorChat> selectPage = baseMapper.selectPage(buildPage, lbqWrapper);
        List<BaiduBotDoctorChat> records = selectPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return records;
    }

    @Override
    public BaiduBotDoctorChat lastNewMessage(Long doctorId) {

        BaiduBotDoctorChat doctorChat = baseMapper.selectOne(Wraps.<BaiduBotDoctorChat>lbQ()
                .eq(BaiduBotDoctorChat::getSenderId, doctorId)
                .orderByDesc(BaiduBotDoctorChat::getCreateTime)
                .last(" limit 0,1 "));
        return doctorChat;
    }
}
