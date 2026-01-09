package com.caring.sass.wx.service.config;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.R;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.nursing.api.PlanReminderLogApi;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 此类 redis 消息队列处理类
 *
 * 主要处理推送进入 redis 消息队列的 模版消息推送任务
 *  一个实例。 最多有4个线程正在发送任务，16个任务在队列中等待执行。
 *
 */
@Slf4j
@Component
public class TemplateMessageSendTask {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ConfigService configService;

    @Autowired
    PlanReminderLogApi planReminderLogApi;

    private final AtomicInteger maximumPoolSize = new AtomicInteger(0);

    NamedThreadFactory threadFactory = new NamedThreadFactory("weixin-template-task", false);
    private ExecutorService executor = new ThreadPoolExecutor(0, 4, 0L,
            TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(100),
            threadFactory);;


    /**
     * 定时任务触发。
     * 每5秒调度一次。
     */
    public void run() {
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent("template_message_task_lock", "1", 5, TimeUnit.MINUTES);
        if (lockAcquired == null || !lockAcquired) {
            log.warn("Another instance is running this task.");
            return;
        }
        try {
            while (true) {
                if (maximumPoolSize.get() >= 20) {
                    log.info("Maximum pool size reached. Exiting loop.");
                    break;
                }

                try {
                    String value = redisTemplate.opsForList().rightPop(SaasRedisBusinessKey.PUSH_TEMPLATE_MESSAGE_TASK);
                    if (StrUtil.isNotEmpty(value)) {
                        maximumPoolSize.incrementAndGet();
                        executor.execute(() -> handleTask(value));
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 或者使用日志框架记录日志
                    log.error("Error occurred while handling message", e);
                }
            }
        } finally {
            redisTemplate.delete("template_message_task_lock");
            log.info("Template message task lock released.");
        }
    }


    /**
     * 处理任务
     * @param task
     */
    private void handleTask(String task) {
        try {
            log.info("Processing task: {}", task);
            SendTemplateMessageForm messageForm = JSON.parseObject(task, SendTemplateMessageForm.class);
            R templateMessage = configService.sendTemplateMessage(messageForm);
            String reminderLogPush = messageForm.getReminderLogPush();
            // 回调。通知护理计划推送记录。微信推送成功
            if (StrUtil.isNotEmpty(reminderLogPush)) {
                // 进行回调
                if (templateMessage.getIsSuccess()) {
                    planReminderLogApi.weixinMessageCallback(Long.parseLong(reminderLogPush), 1, messageForm.getTenantCode());
                } else {
                    planReminderLogApi.weixinMessageCallback(Long.parseLong(reminderLogPush), -3, messageForm.getTenantCode());
                }
            }
        } catch (Exception e) {
            log.error("Error handling task: {}", task, e);
        } finally {
            maximumPoolSize.decrementAndGet();
        }
    }


}
