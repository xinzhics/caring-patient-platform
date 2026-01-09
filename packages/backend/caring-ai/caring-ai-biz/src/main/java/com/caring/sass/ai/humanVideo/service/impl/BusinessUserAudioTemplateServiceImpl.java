package com.caring.sass.ai.humanVideo.service.impl;


import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.dto.card.DifyHumanVideoDto;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.humanVideo.BusinessUserAudioTemplate;
import com.caring.sass.ai.humanVideo.dao.BusinessUserAudioTemplateMapper;
import com.caring.sass.ai.humanVideo.service.BusinessUserAudioTemplateService;
import com.caring.sass.ai.know.config.DifyApi;
import com.caring.sass.ai.know.config.DifyApiConfig;
import com.caring.sass.ai.know.util.DifyFlowControl;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.cache.repository.CacheRepository;
import com.caring.sass.exception.BizException;
import io.reactivex.annotations.Nullable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 用户提交的录音素材
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
@Slf4j
@Service

public class BusinessUserAudioTemplateServiceImpl extends SuperServiceImpl<BusinessUserAudioTemplateMapper, BusinessUserAudioTemplate> implements BusinessUserAudioTemplateService {

    @Autowired
    DifyApiConfig difyApiConfig;


    @Autowired
    DifyFlowControl difyFlowControl;

    @Autowired
    CacheRepository cacheRepository;


    @Override
    public String getAllResult(String uid) {

        Object object = cacheRepository.get(uid);
        if (Objects.nonNull(object)) {
            cacheRepository.del(uid);
            return object.toString();
        }
        return "";
    }

    @Override
    public void getText(Long userId, String uid, BusinessCard businessCard) {

        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BizException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }

        difyFlowControl.whenRedisValueIncrSuccess();
        DifyHumanVideoDto videoDto = new DifyHumanVideoDto();
        videoDto.setName(businessCard.getDoctorName());
        videoDto.setTitle(businessCard.getDoctorTitle());
        videoDto.setHospital(businessCard.getDoctorHospital());
        videoDto.setDepartment(businessCard.getDoctorDepartment());
        videoDto.setExpertise(businessCard.getDoctorBeGoodAt());
        videoDto.setBio(businessCard.getDoctorPersonal());

        JSONObject inputs = JSONObject.parseObject(JSONObject.toJSONString(videoDto));
        JSONObject jsonData = new JSONObject();
        jsonData.put("inputs", inputs);
        jsonData.put("query", "开始");
        jsonData.put("response_mode", "streaming");
        jsonData.put("user", userId.toString());

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, jsonData.toJSONString());
        Request request = new Request.Builder().url(difyApiConfig.getApiDomain() +  DifyApi.completionMessages.getPath())
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + difyApiConfig.getCard_human_video_text_create_key()).post(body)
                .build();

        EventSourceListener sourceListener = new EventSourceListener() {

            // 用于接收服务器返回的数据
            StringBuilder stringBuilder = new StringBuilder();
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                log.info("和AI建立了sse连接...");
            }

            @SneakyThrows
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {


                try {
                    JSONObject jsonObject = JSON.parseObject(data);
                    String event = jsonObject.getString("event");
                    if ("message".equals(event)) {
                        JSONObject object = new JSONObject();
                        String answer = jsonObject.getString("answer");
                        log.debug("dify answer：{}", answer);
                        if (answer.contains("\\u")) {
                            String unicodedToChinese = unicodeToChinese(answer);
                            stringBuilder.append(unicodedToChinese);
                            object.put("content", unicodedToChinese);
                            sseEmitter.send(SseEmitter.event()
                                    .id(UUID.fastUUID().toString())
                                    .data(object.toJSONString())
                                    .reconnectTime(3000));
                        } else {
                            stringBuilder.append(answer);
                            object.put("content", answer);
                            sseEmitter.send(SseEmitter.event()
                                    .id(UUID.fastUUID().toString())
                                    .data(object.toJSONString())
                                    .reconnectTime(3000));
                        }
                    }
                } catch (Exception e) {
                    log.error("sse信息推送失败！");
                    eventSource.cancel();
                }
            }
            @Override
            public void onClosed(EventSource eventSource) {
                log.info("关闭sse连接...");
                try {
                    cacheRepository.set(uid, stringBuilder.toString());
                    cacheRepository.setExpire(uid, stringBuilder.toString(), 6 * 60);
                    // 将AI返回的内容存储起来。
                    sseEmitter.send(SseEmitter.event()
                            .id("[DONE]")
                            .data("[DONE]")
                            .reconnectTime(3000));
                    // 传输完成后自动关闭sse
                    sseEmitter.complete();
                } catch (IOException e) {
                    log.error("传输完成", e);
                }
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                if (Objects.isNull(response)) {
                    return;
                }
                log.error("AI写作请求异常{}", response);
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
                    log.error("dify  sse连接容错异常", e);
                }
                eventSource.cancel();
            }
        };

        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, sourceListener);

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

    @Override
    public SseEmitter createSse(String uid) {
        // 默认2分钟超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);

        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);

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
