package com.caring.sass.msgs.bot;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.cache.repository.CacheRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;


/**
 * OpenAI SSE返回监听器
 *
 * @author leizhi
 */
@Slf4j
public class OpenAISSEEventSourceListener extends EventSourceListener {

    private long tokens;
    private String content = "";

    private final SseEmitter sseEmitter;

    private final CacheRepository cacheRepository;

    private final String uid;

    private final String tenant;

    public OpenAISSEEventSourceListener(SseEmitter sseEmitter, CacheRepository cacheRepository, String uid, String tenant) {
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
        log.info("OpenAI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.debug("OpenAI返回数据：{}", data);
        tokens += 1;
        if ("[DONE]".equals(data)) {
            log.info("OpenAI返回数据结束了");
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]")
                    .reconnectTime(3000));
            // 传输完成后自动关闭sse
            sseEmitter.complete();
            return;
        }
        // 读取Json
        ChatCompletionResponse completionResponse = JSONUtil.toBean(data, ChatCompletionResponse.class);
        try {
            Message delta = completionResponse.getChoices().get(0).getDelta();
            if (Objects.nonNull(delta)
                    && StrUtil.isNotBlank(delta.getContent())) {
                content += delta.getContent();
            }
            sseEmitter.send(SseEmitter.event()
                    .id(completionResponse.getId())
                    .data(delta)
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("sse信息推送失败！");
            eventSource.cancel();
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("流式输出返回值总共{}tokens", tokens() - 2);
        log.info("OpenAI关闭sse连接...");
        try {
            // 缓存租户编码和回复内容
            String cacheValue = tenant + '#' + content;
            cacheRepository.set(uid, cacheValue);
        } catch (Exception e) {
            log.error("缓存回复内容异常", e);
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
            log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
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
            log.error("OpenAI  sse连接容错异常", e);
        }
        eventSource.cancel();
    }


    /**
     * tokens
     */
    public long tokens() {
        return tokens;
    }

}
