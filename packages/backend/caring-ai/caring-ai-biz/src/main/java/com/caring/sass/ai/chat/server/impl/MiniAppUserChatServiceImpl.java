package com.caring.sass.ai.chat.server.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.ai.assistant.SearchAssistant;
import com.caring.sass.ai.chat.dao.MiniAppUserChatHistoryGroupMapper;
import com.caring.sass.ai.chat.dao.MiniAppUserChatMapper;
import com.caring.sass.ai.chat.server.MiniAppUserChatService;
import com.caring.sass.ai.entity.chat.MiniAppUserChat;
import com.caring.sass.ai.entity.chat.MiniAppUserChatHistoryGroup;
import com.caring.sass.ai.entity.dto.MiniAppUserChatSaveDTO;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 敏智用户聊天记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-28
 */
@Slf4j
@Service

public class MiniAppUserChatServiceImpl extends SuperServiceImpl<MiniAppUserChatMapper, MiniAppUserChat> implements MiniAppUserChatService {

    @Autowired
    CacheRepository cacheRepository;

    @Autowired
    SearchAssistant searchAssistant;

    @Autowired
    MiniAppUserChatHistoryGroupMapper miniAppUserChatHistoryGroupMapper;


    /**
     * 提交一次会话
     * @param miniAppUserChatSaveDTO
     * @return
     */
    @Override
    public MiniAppUserChat submitChat(MiniAppUserChatSaveDTO miniAppUserChatSaveDTO) {
        Long historyId = miniAppUserChatSaveDTO.getHistoryId();
        if (Objects.isNull(historyId)) {
            // 新建一个历史对话
            MiniAppUserChatHistoryGroup historyGroup = MiniAppUserChatHistoryGroup.builder()
                    .contentTitle(miniAppUserChatSaveDTO.getContent())
                    .currentUserType(miniAppUserChatSaveDTO.getCurrentUserType())
                    .senderId(miniAppUserChatSaveDTO.getSenderId())
                    .build();
            miniAppUserChatHistoryGroupMapper.insert(historyGroup);
            miniAppUserChatSaveDTO.setHistoryId(historyGroup.getId());
        }
        MiniAppUserChat appUserChat = new MiniAppUserChat();
        BeanUtils.copyProperties(miniAppUserChatSaveDTO, appUserChat);
        baseMapper.insert(appUserChat);

        // 提交给AI 。等待AI的回复
        String uid = miniAppUserChatSaveDTO.getSessionId();
        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BizException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BaseContextHandler.getTenant()).append("#");
        searchAssistant.chat(appUserChat.getContent())
                .onNext(ret -> {
                    stringBuilder.append(ret);
                    send(uid, sseEmitter, ret);
                })
                .onComplete(ret -> {
                    cacheRepository.set(uid, stringBuilder.toString());
                    send(uid, sseEmitter, "[DONE]");
                    sseEmitter.complete();
                })
                .onError(ret -> {
                    send(uid, sseEmitter, "{\"content\":\"AI服务繁忙，请稍后再试\"}");
                    send(uid, sseEmitter, "[DONE]");
                }).start();
        return appUserChat;
    }


    /**
     * 获取会话上一次内容
     *
     * @param sessionId 会话标识
     */
    public MiniAppUserChat queryLastChatBySessionId(String sessionId) {
        return baseMapper.selectOne(Wraps.<MiniAppUserChat>lbQ()
                .eq(MiniAppUserChat::getSessionId, sessionId)
                .last(" limit 0,1 ")
                .orderByDesc(MiniAppUserChat::getCreateTime));
    }

    /**
     * 创建一个sse会话
     * @param uid
     * @return
     */
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
                log.info("[{}]回复的内容 {}...................", uid, cacheValue);
                if (StrUtil.isBlank(cacheValue)) {
                    return;
                }
                List<String> array = StrUtil.split(cacheValue, '#', 2);
                BaseContextHandler.setTenant(array.get(0));
                // todo 一些聊天记录等业务逻辑实现
                MiniAppUserChat chat = queryLastChatBySessionId(uid);
                if (chat == null) {
                    return;
                }
                MiniAppUserChat miniAppUserChat = MiniAppUserChat.builder()
                        .sessionId(uid)
                        .senderRoleType(MiniAppUserChat.AiRole)
                        .content(array.get(1))
                        .type(MediaType.text.toString())
                        .replyStatus(MiniAppUserChat.sendSuccess)
                        .replyMsgId(chat.getId())
                        .historyId(chat.getHistoryId())
                        .senderId(chat.getSenderId())
                        .sendStatus(MiniAppUserChat.sendSuccess)
                        .build();
                baseMapper.insert(miniAppUserChat);

                cacheRepository.del(uid);
            } catch (Exception e) {
                log.error("保存ai回复内容异常", e);
            }
        });

        //超时回调
        sseEmitter.onTimeout(() -> log.info("[{}]连接超时...................", uid));
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data("")
                                .reconnectTime(3000));
                        com.caring.sass.ai.utils.SseEmitterSession.put(uid, sseEmitter);
                    } catch (IOException e) {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                    }
                }
        );

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            log.error("", e);
        }
        log.info("[{}]创建sse连接成功！", uid);
        return sseEmitter;
    }



    public void send(String uid, SseEmitter sseEmitter, String msg) {
        try {
            log.info("发送消息 {}", msg);
            String encode = URLEncoder.encode(msg, "UTF-8");
            String replace = encode.replace("+", "%20");
            sseEmitter.send(SseEmitter.event()
                    .id(uid)
                    .data(replace)
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("发送消息失败uid:[{}],msg:[{}]", uid, msg, e);
        }
    }

    /**
     * 查询一个聊天列表
     * @param doctorId
     * @param historyId
     * @param createTime
     * @return
     */
    @Override
    public List<MiniAppUserChat> chatListPage(Long doctorId, Long historyId, LocalDateTime createTime) {


        IPage<MiniAppUserChat> buildPage = new Page(1, 10);
        LbqWrapper<MiniAppUserChat> lbqWrapper = Wraps.<MiniAppUserChat>lbQ()
                .eq(MiniAppUserChat::getSenderId, doctorId)
                .eq(MiniAppUserChat::getHistoryId, historyId)
                .lt(MiniAppUserChat::getCreateTime, createTime)
                .orderByDesc(MiniAppUserChat::getCreateTime);
        IPage<MiniAppUserChat> selectPage = baseMapper.selectPage(buildPage, lbqWrapper);
        List<MiniAppUserChat> records = selectPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        return records;
    }
}
