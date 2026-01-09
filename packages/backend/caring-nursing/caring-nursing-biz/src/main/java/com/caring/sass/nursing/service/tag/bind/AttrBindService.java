package com.caring.sass.nursing.service.tag.bind;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.nursing.constant.TagBindRedisKey;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用redis的list做消息队列
 * 在本地保存 剩余可用线程数。避免 redis 将 list 中的消息都拉取下来后，任务未执行完毕，服务宕机。
 * 由一个线程 从redis 中循环读取 任务，并发送给本地线程池 执行
 * 当本地线程池可使用线程数不足时，停止读取
 *
 *
 *
 */
@Slf4j
@Service
public class AttrBindService {

    /**
     * 标签变化后。手动同步用户的标签
     */
    private final AttrChangeHandle attrChangeHandle;

    /**
     * 表单 基本信息 处理
     */
    private final FormResultHandle formResultHandle;

    /**
     * 添加用药处理
     */
    private final DrugHandle drugHandle;

    /**
     * redis
     */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 本地最大可执行任务数
     */
    private final int MAXIMUM_RUNNING = 4;

    /**
     * 获取 redis 中 绑定标签任务 的线程 是否拉取任务
     */
    private volatile boolean getAttrBindTaskThreadRunStatus = true;

    /**
     * 当前正在运行的任务
     */
    private Set<String> RUNNING_TASK = new HashSet<>();

    /**
     * 线程工厂
     */
    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("attr-bind-task-", false);

    /**
     * 执行任务的 线程池
     */
    private static final ExecutorService ATTR_BIND_TASK_POOL_EXECUTOR = new ThreadPoolExecutor(2, 5,
            0L, TimeUnit.MILLISECONDS,
            new SaasLinkedBlockingQueue<>(30), THREAD_FACTORY);

    public AttrBindService(AttrChangeHandle attrChangeHandle,
                           FormResultHandle formResultHandle,
                           DrugHandle drugHandle) {
        this.attrChangeHandle = attrChangeHandle;
        this.formResultHandle = formResultHandle;
        this.drugHandle = drugHandle;
        ATTR_BIND_TASK_POOL_EXECUTOR.execute(this::runAttrBindTaskThread);
    }

    protected void runAttrBindTaskThread() {
        while (getAttrBindTaskThreadRunStatus) {
            String message = null;
            try {
                message = redisTemplate.opsForList().rightPop(TagBindRedisKey.TENANT_ATTR_BIND, 3, TimeUnit.SECONDS);
            } catch (Exception e) {
            }
            if (StrUtil.isNotEmpty(message)) {
                // 从message中解析出来执行任务需要的信息
                RUNNING_TASK.add(message);

                // 当 map 中的运行数量 大于 maximumRunning， 此 while 循环可以停止了
                if (RUNNING_TASK.size() >= MAXIMUM_RUNNING && getAttrBindTaskThreadRunStatus) {
                    getAttrBindTaskThreadRunStatus = false;
                }
                final String messageKey = message;
                ATTR_BIND_TASK_POOL_EXECUTOR.execute(() -> distributeTask(messageKey));
            }
        }
    }

    /**
     * 线程任务执行完毕后
     * 清除本地 runningTask 中的任务
     * 检查 while 是否还在 获取redis中的任务
     *
     * @param key
     */
    private synchronized void removeRunning(String key) {
        RUNNING_TASK.remove(key);

        // 防止多个线程执行完任务后， 调用 runNotificationSendRunnable() 方法
        if (getAttrBindTaskThreadRunStatus) {
            return;
        }
        if (RUNNING_TASK.size() < MAXIMUM_RUNNING) {
            getAttrBindTaskThreadRunStatus = true;
            runAttrBindTaskThread();
        }
    }


    private void distributeTask(String message ) {
        AttrBindChangeDto attrBindChangeDto = JSON.parseObject(message, AttrBindChangeDto.class);
        boolean canRun = true;
        if (StringUtils.isEmpty(attrBindChangeDto.getTenantCode())) {
            canRun = false;
        }
        if (canRun) {
            try {
                switch (attrBindChangeDto.getEvent()) {
                    case ATTR_CHANGE:
                        attrChangeHandle.handle(attrBindChangeDto);
                        break;
                    case ADD_DRUG:
                    case STOP_DRUG:
                        drugHandle.handle(attrBindChangeDto);
                        break;
                    case BASE_INFO:
                    case HEALTH_RECORD:
                    case MONITORING_PLAN:
                        formResultHandle.handle(attrBindChangeDto);
                        break;
                    default:
                        log.error("attr bind error message event not find {}", message);
                        break;
                }
            } finally {
                removeRunning(message);
            }
        }
    }


    // 标签更新了。

    // 处理标签更新后，项目下的用户那些可以绑定此标签

    // 用户提交了 基本信息。

    // 用户提交了 疾病信息

    // 用户提交了 监测数据表单

    // 用户添加了新的药品

    // 用户药品自动过期了。

    // 用户药品手动 停用了









}
