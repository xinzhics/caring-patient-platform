package com.caring.sass.ai.article.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.article.service.ArticleTaskService;
import com.caring.sass.ai.dto.*;
import com.caring.sass.ai.know.config.DifyApiConfig;
import com.caring.sass.ai.utils.I18nUtils;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 写作工具
 *
 * @author leizhi
 */
@Service
@Slf4j
public class WritingService {

    @Autowired
    DifyApiConfig difyApiConfig;

    private static final String API_KEY = "${COZE_API_KEY:}";
    private static final String API_KEY_V2 = "${COZE_API_KEY_V2:}";
//    public static final String DIFY_DOMAIN = "http://dify.caringopen.cn";

    private final CacheRepository cacheRepository;

    private final ArticleTaskService articleTaskService;


    public WritingService(CacheRepository cacheRepository, ArticleTaskService articleTaskService) {
        this.cacheRepository = cacheRepository;
        this.articleTaskService = articleTaskService;
    }

    /**
     * 写大纲
     */
    public void outline(OutLineReq outLineReq, Long taskId, Locale locale) {
        WritingReq writingReq = BeanUtil.copyProperties(outLineReq, WritingReq.class);
        writingReq.setQuery("写提纲");
        req(writingReq, outLineReq.getUid(), taskId, ArticleWritingType.outline, locale);
    }

    /**
     * 起标题
     */
    public void title(TitleReq titleReq, Long taskId, Locale locale) {
        WritingReq writingReq = BeanUtil.copyProperties(titleReq, WritingReq.class);
        writingReq.setQuery("写标题");
        req(writingReq, titleReq.getUid(), taskId, ArticleWritingType.title, locale);

    }

    /**
     * 写正文
     */
    public void article(ArticleReqDTO articleReqDTO, Long taskId, Locale locale) {
        WritingReq writingReq = BeanUtil.copyProperties(articleReqDTO, WritingReq.class);
        writingReq.setQuery("写正文");
        req(writingReq, articleReqDTO.getUid(), taskId, ArticleWritingType.text, locale);
    }


    public static String unicodeToChinese(String unicodeStr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < unicodeStr.length(); i++) {
            if (unicodeStr.charAt(i) == '\\' && i + 1 < unicodeStr.length() && unicodeStr.charAt(i + 1) == 'u') {
                // 提取四位十六进制数
                String hex = unicodeStr.substring(i + 2, i + 6);
                // 将十六进制数转换为字符
                sb.append((char) Integer.parseInt(hex, 16));
                // 跳过已经处理的Unicode转义序列
                i += 5;
            } else {
                sb.append(unicodeStr.charAt(i));
            }
        }
        return sb.toString();
    }


    private void req(WritingReq writingReq, String uid, Long taskId, ArticleWritingType articleWritingType, Locale locale) {
        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BizException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }

        JSONObject inputs = JSONObject.parseObject(JSONObject.toJSONString(writingReq));
        JSONObject jsonData = new JSONObject();
        jsonData.put("inputs", inputs);
        jsonData.put("query", writingReq.getQuery());
        jsonData.put("response_mode", "streaming");
        jsonData.put("user", "abc-123");

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, jsonData.toJSONString());
        String version = writingReq.getVersion();
        String api_key_token = API_KEY;
        if (StrUtil.isNotEmpty(version) && version.equals("v2")) {
            api_key_token = API_KEY_V2;
        }
        Request request = new Request.Builder().url(difyApiConfig.getApiDomain() + "/chat-messages")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + api_key_token).post(body)
                .build();

        // 当连接关闭时，存在缓存中的数据。包括了 任务ID， 任务类型， ai返回的内容。
        JSONObject result = new JSONObject();
        result.put("taskId", taskId);
        result.put("articleWritingType", articleWritingType);
        EventSourceListener sourceListener = new EventSourceListener() {

            // 用于接收服务器返回的数据
            StringBuilder stringBuilder = new StringBuilder();
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                log.info("和AI建立了sse连接...");
            }

            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                String decodeData = unicodeToChinese(data);
                log.info("id:{}, data:{}", id, decodeData);
//                stringBuilder.append(decodeData);
                JSONObject jsonObject = JSONObject.parseObject(decodeData);
                String event = jsonObject.getString("event");
                if (event.equals("message")) {
                    String answer = jsonObject.getString("answer");
                    if (StrUtil.isNotEmpty(answer)) {
                        stringBuilder.append(answer);
                    }
                } else if (event.equals("workflow_finished")) {
                    JSONObject workflowFinishedData = jsonObject.getJSONObject("data");
                    if (workflowFinishedData != null) {
                        JSONObject outputs = workflowFinishedData.getJSONObject("outputs");
                        if (Objects.nonNull(outputs)) {
                            String string = outputs.getString("answer");
                            if (StrUtil.isNotEmpty(string.trim())) {
                                stringBuilder.delete(0, stringBuilder.length());
                                stringBuilder.append(string);
                            }
                        }
                    }
                }
                // 将AI返回的内容存储起来。
                result.put("content", stringBuilder.toString());
                try {
                    sseEmitter.send(SseEmitter.event()
                            .id(UUID.fastUUID().toString())
                            .data(decodeData)
                            .reconnectTime(3000));
                } catch (Exception e) {
                    log.error("sse信息推送失败！");
                    eventSource.cancel();
                }
            }
            @Override
            public void onClosed(EventSource eventSource) {
                log.info("关闭sse连接...");
                try {
                    // 将AI返回的内容存储起来。
                    result.put("content", stringBuilder.toString());
                    cacheRepository.setExpire(uid, result.toJSONString(), 3600);

                    sseEmitter.send(SseEmitter.event()
                            .id("[DONE]")
                            .data("[DONE]")
                            .reconnectTime(3000));
                    // 传输完成后自动关闭sse
                    sseEmitter.complete();
                    articleTaskService.updateAiContent(result);
                    SseEmitterSession.remove(uid);
                } catch (IOException e) {
                    log.error("传输完成", e);
                }
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.error("AI写作请求异常{}", response);
                // 将AI返回的内容存储起来。
                result.put("content", stringBuilder.toString());
                cacheRepository.set(uid, result.toJSONString());

                articleTaskService.updateAiContent(result);
                try {
                    JSONObject error = new JSONObject();
                    error.put("event", "error");
                    error.put("content", I18nUtils.getMessage("AI.WRITING.ERROR", locale));
                    // 容错处理，不需要前端一直等待
                    sseEmitter.send(SseEmitter.event()
                            .id(UUID.fastUUID().toString())
                            .data(error)
                            .reconnectTime(3000));

                    sseEmitter.send(SseEmitter.event()
                            .id("[DONE]")
                            .data("[DONE]")
                            .reconnectTime(3000));
                    // 传输完成后自动关闭sse
                    sseEmitter.complete();
                } catch (Exception e) {
                    log.error("sse连接容错异常", e);
                }
                SseEmitterSession.remove(uid);
            }
        };

        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, sourceListener);
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
                    log.warn("[{}] 缓存为空，无法保存AI回复内容", uid);
                    return;
                }
                JSONObject result = JSONObject.parseObject(cacheValue);
                articleTaskService.updateAiContent(result);
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


}
