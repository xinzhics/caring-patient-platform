package com.caring.sass.tenant.service.batchBuild;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.common.enums.BatchBuildTask;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.tenant.dao.AppConfigMapper;
import com.caring.sass.tenant.dao.BatchBuildApkTaskMapper;
import com.caring.sass.tenant.dao.BatchBuildTaskChildMapper;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.entity.AppConfig;
import com.caring.sass.tenant.entity.BatchBuildApkTask;
import com.caring.sass.tenant.entity.BatchBuildTaskChild;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.service.impl.ApkBuildService;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName batchBuild
 * 接收 redis 中监听的消息。将任务放入内存，按顺序执行打包任务
 * @Author yangShuai
 * @Date 2021/10/28 17:39
 * @Version 1.0
 */

@Slf4j
@Service
public class BatchBuildRedisMessageConsumption {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BatchBuildApkTaskMapper batchBuildApkTaskMapper;

    @Autowired
    private BatchBuildTaskChildMapper batchBuildTaskChildMapper;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private AppConfigMapper appConfigMapper;

    @Autowired
    DistributedLock distributedLock;

    /**
     * 为防止 循环注入问题。 此service 只在使用时从 spring 中获取
     */
    private ApkBuildService apkBuildService;
    /**
     * 项目启动时， 执行此方法，从redis中死循环拉取batchBuildTaskChild队列的消息
     * 当收到消息时，此方法退出。
     * 消息执行完毕，执行失败，或消息代表的任务已过期，不存在等。重新执行此任务状态
     */
    public void getMessage() {
        String message = null;
        while (true) {
            try {
                message = redisTemplate.opsForList().rightPop("batchBuildTaskChild", 3, TimeUnit.SECONDS);
            } catch (Exception e) {
            }
            if (message != null && message.length() > 0) {
                // 从message中解析出来执行任务需要的信息
                BatchBuildMessage batchBuildMessage = JSON.parseObject(message, BatchBuildMessage.class);
                startBatchBuildApkChildTask(batchBuildMessage);
                // 退出循环
                break;
            }
        }
    }

    /**
     * 检查总任务下的子任务是否 还有等待中的任务
     *
     * 业务结束后，再次开始 getMessage
     * @param taskId
     */
    public void getNextMessage(Long taskId) {
        try {
            if (taskId != null) {
                Integer waitOrRunning = batchBuildTaskChildMapper.selectCount(Wraps.<BatchBuildTaskChild>lbQ()
                        .eq(BatchBuildTaskChild::getBatchBuildApkTaskId, taskId)
                        .apply("( task_status = 'RUNNING' or task_status = 'WAIT' )"));
                if (waitOrRunning == null || waitOrRunning.equals(0)) {
                    BatchBuildApkTask buildApkTask = batchBuildApkTaskMapper.selectById(taskId);
                    buildApkTask.setTaskStatus(BatchBuildTask.SUCCESS);
                    buildApkTask.setLastUpdateTime(System.currentTimeMillis());
                    buildApkTask.setEndTime(LocalDateTime.now());
                    batchBuildApkTaskMapper.updateById(buildApkTask);
                }
            }
        } catch (Exception e) {

        } finally {
            getMessage();
        }
    }

    /**
     * 开始一个批量打包任务
     * 开始子任务之前，检查 总任务的状态
     */
    private void startBatchBuildApkChildTask(BatchBuildMessage message) {
        Long taskId = message.getBatchBuildTaskId();
        Long batchBuildChildId = message.getBatchBuildChildId();
        BatchBuildApkTask buildApkTask = batchBuildApkTaskMapper.selectById(taskId);
        if (buildApkTask == null) {
            getNextMessage(null);
            return;
        }
        Long localDateTime = buildApkTask.getLastUpdateTime();
        Long messageUpdateTime = message.getMessageUpdateTime();
        Long updateTime = message.getChildTaskMessageUpdateTime();
        if (localDateTime.equals(messageUpdateTime)) {
            if (BatchBuildTask.RUNNING.equals(buildApkTask.getTaskStatus())) {
                BatchBuildTaskChild taskChild = getNextBuildApkTask(batchBuildChildId, updateTime);
                // 没有查询到 可以运行的子任务时， 重新拉取一个消息
                if (taskChild != null) {
                    sendChildBuild(taskId, taskChild);
                } else {
                    getNextMessage(taskId);
                }
            } else {
                // 总任务 不在运行状态了, 重新拉取一个消息
                getNextMessage(taskId);
            }
        } else {
            // 消息已经过期了。
            getNextMessage(taskId);
        }


    }


    /**
     * 使用任务ID 将任务上锁。
     * 查询 子任务
     * 修改任务的状态
     * 返回任务
     * @return
     */
    private BatchBuildTaskChild getNextBuildApkTask(Long batchApkTaskChildId, Long childUpdateTime) {

        BatchBuildTaskChild returnObj = null;

        BatchBuildTaskChild taskChild;
        // 上锁
        // 获取 子任务锁
        String lock = "batchBuild" + batchApkTaskChildId + ":lock";
        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 5000L, 20);
            if (lockBoolean) {
                taskChild = batchBuildTaskChildMapper.selectById(batchApkTaskChildId);
                if (Objects.nonNull(taskChild)) {
                    Long lastUpdateTime = taskChild.getLastUpdateTime();
                    if (childUpdateTime.equals(lastUpdateTime)) {
                        if (BatchBuildTask.WAIT.equals(taskChild.getTaskStatus())) {
                            taskChild.setStartTime(LocalDateTime.now());
                            taskChild.setTaskStatus(BatchBuildTask.RUNNING);
                            batchBuildTaskChildMapper.updateById(taskChild);
                            returnObj = taskChild;
                        } else {
                            taskChild.setMessage("任务处于" + taskChild.getTaskStatus() + "状态, 取消执行");
                            batchBuildTaskChildMapper.updateById(taskChild);
                        }
                    }
                }
            }
        } catch (Exception e) {

        } finally {
            // 解锁
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }
        return returnObj;
    }



    /**
     * 发送下一个子任务 到 打包任务中去执行
     * 需要将app包的版本包自动升一级
     *
     */
    private void sendChildBuild(Long batchApkTaskId, BatchBuildTaskChild taskChild) {
        if (apkBuildService == null) {
            apkBuildService = SpringUtils.getBean(ApkBuildService.class);
        }
        if (apkBuildService == null) {
            log.error("BatchApkBuild_error， 为获取到打包任务bean");
            // 批量打包任务 修改为 异常
            childTaskMessage(batchApkTaskId, taskChild.getCode(), false, "未获取到打包任务");
            return;
        }
        String code = taskChild.getCode();
        BaseContextHandler.setTenant(code);
        Tenant t = tenantMapper.getByCode(code);
        AppConfig appConfig = appConfigMapper.selectOne(Wraps.lbQ());
        if (appConfig == null) {
            childTaskMessage(batchApkTaskId, taskChild.getCode(), false, "app配置文件不存在");
            return;
        }
        // 对 appConfig 的版本号进行升级
        String appVersionName = appConfig.getAppVersionName();
        // 记录之前的app版本号
        taskChild.setAppVersionNameLast(appVersionName);
        String[] split = appVersionName.split("\\.");
        String s = split[split.length - 1];
        try {
            int version = Integer.parseInt(s);
            version++;
            split[split.length - 1] = version + "";
            appVersionName = String.join(".", split);
            appConfig.setAppVersionName(appVersionName);
            // 记录修改后的app版本号
            taskChild.setAppVersionName(appVersionName);
            batchBuildTaskChildMapper.updateById(taskChild);
        } catch (Exception e) {
            log.error("版本号修改异常");
            childTaskMessage(batchApkTaskId, taskChild.getCode(), false, "版本号修改异常格式需要为1.0.0");
            return ;
        }
        try {
            apkBuildService.doBuild(t.getDomainName(), code, appConfig, batchApkTaskId, Objects.nonNull(t.getWxBindTime()));
        } catch (Exception e) {
            childTaskMessage(batchApkTaskId, taskChild.getCode(), false, e.getMessage());
        }

    }


    /**
     * 更新批量任务下的子任务执行状态
     * 并检查批量任务下剩余为执行任务，更新批量任务执行进度，发出新的打包通知，。
     * 批量任务下没有为执行过的任务，则批量任务结束。
     * @param batchApkTaskId 批量任务ID
     * @param code 项目code
     * @param buildStatus 子任务打包状态
     */
    public void childTaskMessage(Long batchApkTaskId, String code, Boolean buildStatus, String message) {

        if (Objects.isNull(batchApkTaskId)) {
            return;
        }
        if (StringUtils.isEmpty(code)) {
            return;
        }
        try {
            List<BatchBuildTaskChild> taskChildList = batchBuildTaskChildMapper.selectList(Wraps.<BatchBuildTaskChild>lbQ()
                    .eq(BatchBuildTaskChild::getBatchBuildApkTaskId, batchApkTaskId)
                    .eq(BatchBuildTaskChild::getCode, code));
            if (CollUtil.isNotEmpty(taskChildList)) {
                // 更新任务子任务状态 和 信息
                BatchBuildTaskChild taskChild = taskChildList.get(0);
                setBatchBuildTask(taskChild, buildStatus, message);
            }
        } catch (Exception e) {
            log.error("BatchBuildApkTaskService.childTaskMessage error: {}", e.getMessage());
        } finally {
            // 开始接收器。继续接受任务
            getNextMessage(batchApkTaskId);
        }
    }

    /**
     * 设置 批量任务的 任务进度。
     * 更新 子任务的 打包状态
     * @param taskChild
     * @param buildStatus
     */
    private void setBatchBuildTask(BatchBuildTaskChild taskChild, Boolean buildStatus, String message) {

        if (buildStatus == null) {
            return;
        }
        if (buildStatus) {
            taskChild.setTaskStatus(BatchBuildTask.SUCCESS);
        } else {
            taskChild.setTaskStatus(BatchBuildTask.ERROR);
            taskChild.setMessage(message);
            // 任务失败。将app版本号还原
            String code = taskChild.getCode();
            BaseContextHandler.setTenant(code);
            AppConfig appConfig = appConfigMapper.selectOne(Wraps.lbQ());
            if (Objects.nonNull(appConfig) && StringUtils.isNotEmptyString(taskChild.getAppVersionNameLast())) {
                appConfig.setAppVersionName(taskChild.getAppVersionNameLast());
                appConfigMapper.updateById(appConfig);
            }
        }
        taskChild.setEndTime(LocalDateTime.now());
        batchBuildTaskChildMapper.updateById(taskChild);

    }

}
