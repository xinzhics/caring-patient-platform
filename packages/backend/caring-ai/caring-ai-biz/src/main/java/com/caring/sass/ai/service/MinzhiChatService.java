package com.caring.sass.ai.service;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.assistant.SearchAssistant;
import com.caring.sass.ai.dto.ChatDTO;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * 敏智聊天服务
 *
 * @author leizhi
 */
@Slf4j
@Service
public class MinzhiChatService {

    private final CacheRepository cacheRepository;

    private final SearchAssistant searchAssistant;

    public MinzhiChatService(CacheRepository cacheRepository, SearchAssistant searchAssistant) {
        this.cacheRepository = cacheRepository;
        this.searchAssistant = searchAssistant;
    }

    public SseEmitter createSse(String uid) {
        // 默认2分钟超时,设置为0L则永不超时
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
                // todo 一些聊天记录等业务逻辑实现
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
                        SseEmitterSession.put(uid, sseEmitter);
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


    public void sseChat(ChatDTO chatDTO) {
        String uid = chatDTO.getUid();
        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BizException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }

        searchAssistant.chat(chatDTO.getContent())
                .onNext(ret -> send(uid, sseEmitter, ret))
                .onComplete(ret -> {
                    send(uid, sseEmitter, "[DONE]");
                    sseEmitter.complete();
                })
                .onError(ret -> {
                    send(uid, sseEmitter, "{\"content\":\"AI服务繁忙，请稍后再试\"}");
                    send(uid, sseEmitter, "[DONE]");
                }).start();
    }

    public void send(String uid, SseEmitter sseEmitter, String msg) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(uid)
                    .data(msg)
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("发送消息失败uid:[{}],msg:[{}]", uid, msg, e);
        }
    }

}
