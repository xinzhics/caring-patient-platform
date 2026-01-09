package com.caring.sass.ai.podcast;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.podcast.PodcastTaskStatus;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * dify生产播客对话 SSE返回监听器
 *
 * @author leizhi
 */
@Slf4j
public class Podcast2DifyBotSSEEventSourceListener extends EventSourceListener {

    private StringBuilder stringBuilder = new StringBuilder();

    private RedisTemplate<String, String> redisTemplate;

    private final String uid;


    private final PodcastMapper podcastMapper;

    private final SseEmitter sseEmitter;

    public Podcast2DifyBotSSEEventSourceListener(PodcastMapper podcastMapper,
                                                 RedisTemplate<String, String> redisTemplate,
                                                 String uid,
                                                 SseEmitter sseEmitter) {
        this.uid = uid;
        this.redisTemplate = redisTemplate;
        this.podcastMapper = podcastMapper;
        this.sseEmitter = sseEmitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("dify create sse...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        try {
            JSONObject jsonObject = JSON.parseObject(data);
            String event = jsonObject.getString("event");
            log.error("dify message：{}", data);
            if ("message".equals(event)) {
                String answer = jsonObject.getString("answer");
                log.error("dify answer：{}", answer);
                if (answer.contains("\\u")) {
                    String unicodedToChinese = unicodeToChinese(answer);
                    stringBuilder.append(unicodedToChinese);
                    sendMessage(unicodedToChinese);
                } else {
                    stringBuilder.append(answer);
                    sendMessage(answer);
                }
            }
        } catch (Exception e) {
            log.error("sse信息推送失败！");
            eventSource.cancel();
        }
    }


    public void sendMessage(String message) {
        if ("[DONE]".equals(message)) {
            sseEmitter.complete();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", message);
            sseEmitter.send(SseEmitter.event()
                    .id(UUID.fastUUID().toString())
                    .data(jsonObject.toJSONString())
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("sse信息推送失败！");
        }
    }

    /**
     * 【Unicode转中文】
     * @param unicode
     * @return 返回转码后的字符串 - 中文格式
     */
    public static String unicodeToChinese(final String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 0; i < hex.length; i++) {
            try {
                // 汉字范围 \u4e00-\u9fa5 (中文)
                if(hex[i].length()>=4){//取前四个，判断是否是汉字
                    String chinese = hex[i].substring(0, 4);
                    try {
                        int chr = Integer.parseInt(chinese, 16);
                        boolean isChinese = isChinese((char) chr);
                        //转化成功，判断是否在  汉字范围内
                        if (isChinese){//在汉字范围内
                            // 追加成string
                            string.append((char) chr);
                            //并且追加  后面的字符
                            String behindString = hex[i].substring(4);
                            string.append(behindString);
                        }else {
                            string.append(hex[i]);
                        }
                    } catch (NumberFormatException e1) {
                        string.append(hex[i]);
                    }
                }else{
                    string.append(hex[i]);
                }
            } catch (NumberFormatException e) {
                string.append(hex[i]);
            }
        }
        return string.toString();
    }

    /**
     * 【判断是否为中文字符】
     * @param c
     * @return 返回判断结果 - boolean类型
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("dify closed sse...");
        sendMessage("[DONE]");
        updatePodcast(PodcastTaskStatus.CREATE_DIALOGUE_SUCCESS);
    }

    public void updatePodcast(PodcastTaskStatus status) {
        Podcast podcast = new Podcast();
        podcast.setId(Long.parseLong(uid));
        podcast.setPodcastPreviewContents(stringBuilder.toString());
        podcast.setStep(3);
        podcast.setTaskStatus(status);
        podcastMapper.updateById(podcast);
        redisTemplate.expire("podcast:" + uid, 20, TimeUnit.MINUTES);
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("dify sse error data：{}，异常：{}", body.string(), t);
        } else {
            log.error("dify sse error data：{}，异常：{}", response, t);
        }
        sendMessage("[DONE]");
        updatePodcast(PodcastTaskStatus.CREATE_DIALOGUE_ERROR);

//        try {
//            redisTemplate.expire("podcast:" + uid, 20, TimeUnit.MINUTES);
//            updatePodcast(PodcastTaskStatus.CREATE_DIALOGUE_ERROR);
//            // 容错处理，不需要前端一直等待
//            SseEmitter sseEmitter = SseEmitterSession.get(uid + "openSuccess");
//            if (sseEmitter == null) {
//                return;
//            }
//            String string = redisTemplate.opsForValue().get("podcast_emitter:" + uid);
//            if (StrUtil.isNotBlank(string)) {
//                sseEmitter.send(SseEmitter.event()
//                        .id(UUID.fastUUID().toString())
//                        .data("{\"content\":\"AI服务繁忙，请稍后再试\"}")
//                        .reconnectTime(3000));
//
//                sseEmitter.send(SseEmitter.event()
//                        .id("[DONE]")
//                        .data("[DONE]")
//                        .reconnectTime(3000));
//                // 传输完成后自动关闭sse
//                sseEmitter.complete();
//            }
//        } catch (Exception e) {
//            log.error("Baidu  sse连接容错异常", e);
//        }
        eventSource.cancel();
    }



}
