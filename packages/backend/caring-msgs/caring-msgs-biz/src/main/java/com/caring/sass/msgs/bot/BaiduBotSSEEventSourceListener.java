package com.caring.sass.msgs.bot;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.msgs.bot.baiduModel.BaiduBotResponseBody;
import com.caring.sass.msgs.bot.baiduModel.Content;
import com.caring.sass.msgs.bot.baiduModel.Message;
import com.caring.sass.msgs.bot.baiduModel.MessageBean;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * OpenAI SSE返回监听器
 *
 * @author leizhi
 */
@Slf4j
public class BaiduBotSSEEventSourceListener extends EventSourceListener {

    private StringBuilder content = new StringBuilder();

    private final SseEmitter sseEmitter;

    private final CacheRepository cacheRepository;

    private final String uid;

    private final String tenant;

    public BaiduBotSSEEventSourceListener(SseEmitter sseEmitter, CacheRepository cacheRepository, String uid, String tenant) {
        this.sseEmitter = sseEmitter;
        this.cacheRepository = cacheRepository;
        this.uid = uid;
        this.tenant = tenant;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("Baidu建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.debug("Baidu返回数据：{}", data);
        BaiduBotResponseBody responseBody = JSONUtil.toBean(data, BaiduBotResponseBody.class);
        String currentContent = "";
        String error_msg = responseBody.getError_msg();
        if (StrUtil.isNotEmpty(error_msg)) {
            // 百度AI异常
            log.error(error_msg);
            this.content.append("AI服务繁忙");
        } else {
            List<MessageBean> results = responseBody.getResult();
            if (CollUtil.isNotEmpty(results)) {
                for (MessageBean result : results) {
                    List<Message> messages = result.getMessages();
                    if (CollUtil.isNotEmpty(messages)) {
                        for (Message message : messages) {
                            List<Content> contents = message.getContent();
                            if (CollUtil.isNotEmpty(contents)) {
                                for (Content conResult : contents) {
                                    currentContent = conResult.getBody();
                                    if (StrUtil.isEmpty(currentContent)) {
                                        currentContent = "你好，我可以基于输入的症状、体征等描述，推荐疑似诊断、建议检验检查。请问我有什么可以帮到你的？";
                                    } else if (currentContent.equals("0")) {
                                        currentContent = "你好，我可以基于输入的症状、体征等描述，推荐疑似诊断、建议检验检查。请问我有什么可以帮到你的？";
                                    }
                                    this.content.append(conResult.getBody());
                                }
                            }
                        }
                    }
                }
            }
            currentContent = currentContent.replace("\n", "");
            try {
                if (StrUtil.isNotEmpty(currentContent)) {
                    sseEmitter.send(SseEmitter.event()
                            .id(UUID.fastUUID().toString())
                            .data(currentContent)
                            .reconnectTime(3000));
                }
            } catch (Exception e) {
                log.error("sse信息推送失败！");
                eventSource.cancel();
            }
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("Baidu关闭sse连接...");
        try {
            // 缓存租户编码和回复内容
            String cacheValue = tenant + '#' + content;
            cacheRepository.set(uid, cacheValue);
        } catch (Exception e) {
            log.error("缓存回复内容异常", e);
        }
        try {
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]")
                    .reconnectTime(3000));
            // 传输完成后自动关闭sse
            sseEmitter.complete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("Baidu  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("Baidu  sse连接异常data：{}，异常：{}", response, t);
        }

        try {
            // 容错处理，不需要前端一直等待
            sseEmitter.send(SseEmitter.event()
                    .id(UUID.fastUUID().toString())
                    .data("{\"content\":\"AI服务繁忙，请稍后再试\"}")
                    .reconnectTime(3000));

            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]")
                    .reconnectTime(3000));
            // 传输完成后自动关闭sse
            sseEmitter.complete();
        } catch (Exception e) {
            log.error("Baidu  sse连接容错异常", e);
        }
        eventSource.cancel();
    }



}
