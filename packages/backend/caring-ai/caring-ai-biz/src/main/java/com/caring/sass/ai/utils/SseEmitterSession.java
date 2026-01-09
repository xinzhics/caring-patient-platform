package com.caring.sass.ai.utils;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SseEmitter 会话
 * SseEmitter会话序列化存在问题，暂不支持分布式
 *
 * @author leizhi
 */
public class SseEmitterSession {

    /**
     * SSE会话超时时间
     */
    public static final long SSE_TIME_OUT = 5 * DateUnit.MINUTE.getMillis();
    public static final long SSE_TIME_1_OUT = 1 * DateUnit.MINUTE.getMillis();

    public static void put(String uid, SseEmitter sseEmitter) {
        CACHE.put(uid, sseEmitter);
    }

    public static SseEmitter get(String uid) {
        return CACHE.get(uid);
    }

    public static void remove(String uid) {
        CACHE.remove(uid);
    }

    /**
     * 会话缓存5分钟
     */
    private static final long TIMEOUT = 5 * DateUnit.MINUTE.getMillis();

    /**
     * 清理间隔
     */
    private static final long CLEAN_TIMEOUT = 5 * DateUnit.MINUTE.getMillis();

    /**
     * 缓存对象
     */
    private static final TimedCache<String, SseEmitter> CACHE = CacheUtil.newTimedCache(TIMEOUT);

    static {
        //启动定时任务
        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }
}
