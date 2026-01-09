//package com.caring.sass.ai.writing;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.lang.UUID;
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.caring.sass.ai.dto.ArticleReqDTO;
//import com.caring.sass.ai.dto.OutLineReq;
//import com.caring.sass.ai.dto.TitleReq;
//import com.caring.sass.ai.dto.WritingReq;
//import com.caring.sass.ai.utils.SseEmitterSession;
//import com.caring.sass.cache.repository.CacheRepository;
//import com.caring.sass.context.BaseContextHandler;
//import com.caring.sass.exception.BizException;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
//import okhttp3.sse.EventSource;
//import okhttp3.sse.EventSourceListener;
//import okhttp3.sse.EventSources;
//import org.jetbrains.annotations.Nullable;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
//
///**
// * 写作工具
// *
// * @author leizhi
// */
//@Service
//@Slf4j
//public class WritingService {
//
//    private static final String API_KEY = "${DIFY_API_KEY:your-api-key-here}";
//    public static final String DIFY_DOMAIN = "http://dify.caringopen.cn";
//
//    private final CacheRepository cacheRepository;
//
//    public WritingService(CacheRepository cacheRepository) {
//        this.cacheRepository = cacheRepository;
//    }
//
//    /**
//     * 写大纲
//     */
//    public void outline(OutLineReq outLineReq) {
//        WritingReq writingReq = BeanUtil.copyProperties(outLineReq, WritingReq.class);
//        writingReq.setQuery("写提纲");
//        req(writingReq, outLineReq.getUid());
//    }
//
//    /**
//     * 起标题
//     */
//    public void title(TitleReq titleReq) {
//        WritingReq writingReq = BeanUtil.copyProperties(titleReq, WritingReq.class);
//        writingReq.setQuery("写标题");
//        req(writingReq, titleReq.getUid());
//
//    }
//
//    /**
//     * 写正文
//     */
//    public void article(ArticleReqDTO articleReqDTO) {
//        WritingReq writingReq = BeanUtil.copyProperties(articleReqDTO, WritingReq.class);
//        writingReq.setQuery("写正文");
//        req(writingReq, articleReqDTO.getUid());
//    }
//
//
//    public static String unicodeToChinese(String unicodeStr) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < unicodeStr.length(); i++) {
//            if (unicodeStr.charAt(i) == '\\' && i + 1 < unicodeStr.length() && unicodeStr.charAt(i + 1) == 'u') {
//                // 提取四位十六进制数
//                String hex = unicodeStr.substring(i + 2, i + 6);
//                // 将十六进制数转换为字符
//                sb.append((char) Integer.parseInt(hex, 16));
//                // 跳过已经处理的Unicode转义序列
//                i += 5;
//            } else {
//                sb.append(unicodeStr.charAt(i));
//            }
//        }
//        return sb.toString();
//    }
//
//
//    private void req(WritingReq writingReq, String uid) {
//        SseEmitter sseEmitter = SseEmitterSession.get(uid);
//        if (sseEmitter == null) {
//            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
//            throw new BizException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
//        }
//
//        JSONObject inputs = JSONObject.parseObject(JSONObject.toJSONString(writingReq));
//        JSONObject jsonData = new JSONObject();
//        jsonData.put("inputs", inputs);
//        jsonData.put("query", writingReq.getQuery());
//        jsonData.put("response_mode", "streaming");
//        // jsonData.put("conversation_id", "");
//        jsonData.put("user", "abc-123");
//
//        //  添加文件数据（可选）
////        JSONObject fileData = new JSONObject();
////        fileData.put("type", "image");
////        fileData.put("transfer_method", "remote_url");
////        fileData.put("url", "https://cloud.dify.ai/logo/logo-site.png");
////        jsonData.put("files", new JSONObject[]{fileData});
//
//        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//
//        MediaType json = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(json, jsonData.toJSONString());
//        Request request = new Request.Builder().url(DIFY_DOMAIN + "/v1/chat-messages")
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer " + API_KEY).post(body)
//                .build();
//
//        EventSourceListener sourceListener = new EventSourceListener() {
//            @Override
//            public void onOpen(EventSource eventSource, Response response) {
//                log.info("和前端建立了sse连接...");
//            }
//
//            @Override
//            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
//                String decodeData = unicodeToChinese(data);
//                log.info("id:{}, data:{}", id, decodeData);
//
//                try {
//                    sseEmitter.send(SseEmitter.event()
//                            .id(UUID.fastUUID().toString())
//                            .data(decodeData)
//                            .reconnectTime(3000));
//                } catch (Exception e) {
//                    log.error("sse信息推送失败！");
//                    eventSource.cancel();
//                }
//            }
//
//            @Override
//            public void onClosed(EventSource eventSource) {
//                log.info("关闭sse连接...");
//                try {
//                    sseEmitter.send(SseEmitter.event()
//                            .id("[DONE]")
//                            .data("[DONE]")
//                            .reconnectTime(3000));
//                    // 传输完成后自动关闭sse
//                    sseEmitter.complete();
//                } catch (IOException e) {
//                    log.error("传输完成", e);
//                }
//            }
//
//            @Override
//            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
//                if (Objects.isNull(response)) {
//                    return;
//                }
//                log.error("AI写作请求异常{}", response);
//
//                try {
//                    // 容错处理，不需要前端一直等待
//                    sseEmitter.send(SseEmitter.event()
//                            .id(UUID.fastUUID().toString())
//                            .data("{\"content\":\"AI写作服务繁忙，请稍后再试\"}")
//                            .reconnectTime(3000));
//
//                    sseEmitter.send(SseEmitter.event()
//                            .id("[DONE]")
//                            .data("[DONE]")
//                            .reconnectTime(3000));
//                    // 传输完成后自动关闭sse
//                    sseEmitter.complete();
//                } catch (Exception e) {
//                    log.error("sse连接容错异常", e);
//                }
//            }
//        };
//
//        EventSource.Factory factory = EventSources.createFactory(client);
//        factory.newEventSource(request, sourceListener);
//    }
//
//    public SseEmitter createSse(String uid) {
//        // 默认2分钟超时,设置为0L则永不超时
//        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
//
//        //完成后回调
//        sseEmitter.onCompletion(() -> {
//            log.info("[{}]结束连接...................", uid);
//            SseEmitterSession.remove(uid);
//            try {
//                String cacheValue = cacheRepository.get(uid);
//                if (StrUtil.isBlank(cacheValue)) {
//                    return;
//                }
//                List<String> array = StrUtil.split(cacheValue, '#', 2);
//                BaseContextHandler.setTenant(array.get(0));
//                // todo 一些聊天记录等业务逻辑实现
//                cacheRepository.del(uid);
//            } catch (Exception e) {
//                log.error("保存ai回复内容异常", e);
//            }
//        });
//
//        //超时回调
//        sseEmitter.onTimeout(() -> log.info("[{}]连接超时...................", uid));
//        //异常回调
//        sseEmitter.onError(
//                throwable -> {
//                    try {
//                        log.error("[{}]连接异常,{}", uid, throwable.toString());
//                        sseEmitter.send(SseEmitter.event()
//                                .id(uid)
//                                .name("发生异常！")
//                                .data("")
//                                .reconnectTime(3000));
//                        SseEmitterSession.put(uid, sseEmitter);
//                    } catch (IOException e) {
//                        log.error("[{}]连接异常,{}", uid, throwable.toString());
//                    }
//                }
//        );
//
//        try {
//            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
//        } catch (IOException e) {
//            log.error("", e);
//        }
//        SseEmitterSession.put(uid, sseEmitter);
//        log.info("[{}]创建sse连接成功！", uid);
//        return sseEmitter;
//    }
//
//
//}
