package com.caring.sass.tenant.service.batchBuild;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.enums.BatchBuildTask;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.tenant.dao.BatchBuildApkTaskMapper;
import com.caring.sass.tenant.dao.BatchBuildTaskChildMapper;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.dto.BatchBuildApkDto;
import com.caring.sass.tenant.entity.*;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.tenant.service.GlobalUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 */
@Slf4j
@Service
public class BatchBuildApkTaskService {

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    private BatchBuildApkTaskMapper batchBuildApkTaskMapper;

    @Autowired
    private BatchBuildTaskChildMapper batchBuildTaskChildMapper;

    @Autowired
    GlobalUserService globalUserService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 发送子打包任务到 batchBuildTaskChild 队列中去
     * @param updateTime
     * @param batchBuildTaskId
     * @param batchBuildChildTaskId
     */
    public void sendBatchBuildChildTask(Long updateTime, Long childUpdateTime,
                                        Long batchBuildTaskId, Long batchBuildChildTaskId) {
        BatchBuildMessage message = new BatchBuildMessage();
        message.setBatchBuildTaskId(batchBuildTaskId);
        message.setBatchBuildChildId(batchBuildChildTaskId);
        message.setMessageUpdateTime(updateTime);
        message.setChildTaskMessageUpdateTime(childUpdateTime);
        String string = JSON.toJSONString(message);
        redisTemplate.opsForList().leftPush("batchBuildTaskChild", string);
    }


    /**
     * 创建一个批量打包任务
     */
    public void createBatchBuildApkTask(BatchBuildApkDto batchBuildApkDto) {
        Boolean allBuild = batchBuildApkDto.getAllBuild();
        List<Tenant> tenantList;
        if (allBuild == null) {
            return;
        }
        if (allBuild) {
            Long userId = BaseContextHandler.getUserId();
            if (Objects.isNull(userId)) {
                throw new BizException("获取用户身份失败");
            }
            GlobalUser globalUser = globalUserService.getById(userId);
            String globalUserType = globalUser.getGlobalUserType();
            LbqWrapper<Tenant> lbqWrapper = Wraps.<Tenant>lbQ().select(Tenant::getCode).eq(Tenant::getStatus, TenantStatusEnum.NORMAL);
            if (BizConstant.THIRD_PARTY_CUSTOMERS.equals(globalUserType)) {
                List<GlobalUserTenant> globalUserTenants = globalUserService.selectTenantIds(userId);
                if (CollUtil.isEmpty(globalUserTenants)) {
                    tenantList = new ArrayList<>();
                } else {
                    List<Long> tenantIds = globalUserTenants.stream().map(GlobalUserTenant::getTenantId).collect(Collectors.toList());
                    lbqWrapper.in(SuperEntity::getId, tenantIds);
                    tenantList = tenantMapper.selectList(lbqWrapper);
                }
            } else {
                // 查询所有的正常状态的项目
                tenantList = tenantMapper.selectList(lbqWrapper);
            }

        } else {
            List<Long> tenantIds = batchBuildApkDto.getTenantIds();
            // 查询 tenantIds
            tenantList = tenantMapper.selectBatchIds(tenantIds);
        }

        BatchBuildApkTask task = BatchBuildApkTask.builder()
                .taskName(batchBuildApkDto.getTaskName())
                .allTask(tenantList.size())
                .lastUpdateTime(System.currentTimeMillis())
                .startTime(LocalDateTime.now())
                .fail(0).finish(0).taskStatus(BatchBuildTask.RUNNING).build();
        batchBuildApkTaskMapper.insert(task);
        Long taskUpdateTime = task.getLastUpdateTime();
        List<List<Tenant>> subList = ListUtils.subList(tenantList, 200);
        List<BatchBuildTaskChild> childList;
        Long taskId = task.getId();
        for (List<Tenant> tenants : subList) {
            childList = new ArrayList<>(200);
            for (Tenant tenant : tenants) {
                BatchBuildTaskChild build = BatchBuildTaskChild.builder()
                        .batchBuildApkTaskId(taskId)
                        .code(tenant.getCode())
                        .lastUpdateTime(System.currentTimeMillis())
                        .taskStatus(BatchBuildTask.WAIT)
                        .build();
                childList.add(build);
            }
            if (CollUtil.isNotEmpty(childList)) {
                batchBuildTaskChildMapper.insertBatchSomeColumn(childList);
            }
            for (BatchBuildTaskChild taskChild : childList) {
                sendBatchBuildChildTask(taskUpdateTime,
                        taskChild.getLastUpdateTime(), taskId, taskChild.getId());
            }
        }

    }

    /**
     * 将总任务标记为 删除状态
     * @param batchBuildId
     */
    public void deleteBatchBuildTask(Long batchBuildId) {
        BatchBuildApkTask apkTask = batchBuildApkTaskMapper.selectById(batchBuildId);
        if (apkTask == null) {
            throw new BizException("此任务已经不存在");
        }
        apkTask.setTaskStatus(BatchBuildTask.DELETE);
        apkTask.setLastUpdateTime(System.currentTimeMillis());
        batchBuildApkTaskMapper.updateById(apkTask);

    }

    /**
     * 停止批量打包任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void stopBatchBuildApkTask(Long batchApkTaskId) {
        BatchBuildApkTask buildApkTask = batchBuildApkTaskMapper.selectById(batchApkTaskId);
        if (BatchBuildTask.RUNNING.equals(buildApkTask.getTaskStatus()) ||
                BatchBuildTask.WAIT.equals(buildApkTask.getTaskStatus())) {
            buildApkTask.setTaskStatus(BatchBuildTask.STOP);
            buildApkTask.setLastUpdateTime(System.currentTimeMillis());
            batchBuildApkTaskMapper.updateById(buildApkTask);

            // 批量更新 等待中的子任务为 STOP 状态
            UpdateWrapper<BatchBuildTaskChild> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("task_status", BatchBuildTask.STOP);
            updateWrapper.eq("batch_build_apk_task_id", batchApkTaskId);
            updateWrapper.eq("task_status", BatchBuildTask.WAIT);
            batchBuildTaskChildMapper.update(new BatchBuildTaskChild(), updateWrapper);
        }
    }

    /**
     * 只有 停止了的 批量任务才可以使用开始
     * @param batchApkTaskId
     */
    public void restartBatchBuildApkTask(Long batchApkTaskId) {
        BatchBuildApkTask buildApkTask = batchBuildApkTaskMapper.selectById(batchApkTaskId);
        if (BatchBuildTask.STOP.equals(buildApkTask.getTaskStatus())) {
            buildApkTask.setTaskStatus(BatchBuildTask.RUNNING);
            buildApkTask.setLastUpdateTime(System.currentTimeMillis());
            batchBuildApkTaskMapper.updateById(buildApkTask);

            Long updateTime = buildApkTask.getLastUpdateTime();

            // 总任务重新开始， 将停止的 子任务 修改为 等待状态，刷新最后更新时间。
            long currentTimeMillis = System.currentTimeMillis();
            UpdateWrapper<BatchBuildTaskChild> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("task_status", BatchBuildTask.WAIT);
            updateWrapper.set("last_update_time", currentTimeMillis);
            updateWrapper.eq("batch_build_apk_task_id", batchApkTaskId);
            updateWrapper.eq("task_status", BatchBuildTask.STOP);
            batchBuildTaskChildMapper.update(new BatchBuildTaskChild(), updateWrapper);

            // 查询 等待执行中的任务。将任务发送给 队列
            List<BatchBuildTaskChild> taskChildList = batchBuildTaskChildMapper.selectList(Wraps
                    .<BatchBuildTaskChild>lbQ()
                    .select(BatchBuildTaskChild::getId)
                    .eq(BatchBuildTaskChild::getBatchBuildApkTaskId, batchApkTaskId)
                    .eq(BatchBuildTaskChild::getTaskStatus, BatchBuildTask.WAIT));
            if (CollUtil.isNotEmpty(taskChildList)) {
                for (BatchBuildTaskChild taskChild : taskChildList) {
                    sendBatchBuildChildTask(updateTime, currentTimeMillis, batchApkTaskId, taskChild.getId());
                }
            }
        }

    }


    /**
     * 分页查询 批量任务列表
     * @param buildPage
     * @param lbqWrapper
     * @return
     */
    public IPage<BatchBuildApkTask> pageBatchBuildTask(IPage<BatchBuildApkTask> buildPage, LbqWrapper<BatchBuildApkTask> lbqWrapper) {

        return batchBuildApkTaskMapper.selectPage(buildPage, lbqWrapper);
    }

    /**
     * 分页查询 子任务列表
     * @param buildPage
     * @param lbqWrapper
     * @return
     */
    public IPage<BatchBuildTaskChild> pageBatchBuildChildTask(IPage<BatchBuildTaskChild> buildPage, LbqWrapper<BatchBuildTaskChild> lbqWrapper) {

        return batchBuildTaskChildMapper.selectPage(buildPage, lbqWrapper);

    }


    /**
     * 尝试停止一个 子任务
     * @param childTaskId
     */
    public void stopBatchChildTask(Long childTaskId) {

        // 获取 子任务锁
        String lock = "batchBuild" + childTaskId + ":lock";
        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 5000L, 20);
            if (lockBoolean) {

                BatchBuildTaskChild taskChild = batchBuildTaskChildMapper.selectById(childTaskId);
                // 判断子任务状态
                if (BatchBuildTask.WAIT.equals(taskChild.getTaskStatus())) {
                    // 更新子任务状态
                    taskChild.setTaskStatus(BatchBuildTask.STOP);
                    taskChild.setLastUpdateTime(System.currentTimeMillis());
                    batchBuildTaskChildMapper.updateById(taskChild);
                } else {
                    throw new BizException("任务状态已经变化, 无法取消");
                }
            } else {
                throw new BizException("请求超时，请重试");
            }
        } catch (Exception e) {
            throw new BizException("停止任务失败");
        } finally {
            // 释放锁
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }
    }

    /**
     * 将一个 非运行状态的子任务 重新添加到打包队列
     * @param childTaskId
     */
    public void restartBatchChildTask(Long childTaskId) {

        // 获取 子任务锁
        String lock = "batchBuild" + childTaskId + ":lock";
        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(lock, 5000L, 20);
            if (lockBoolean) {

                BatchBuildTaskChild taskChild = batchBuildTaskChildMapper.selectById(childTaskId);
                // 判断子任务状态
                if (BatchBuildTask.SUCCESS.equals(taskChild.getTaskStatus())) {
                    throw new BizException("任务已经打包完成");
                }
                if (BatchBuildTask.RUNNING.equals(taskChild.getTaskStatus())) {
                    throw new BizException("任务已经正在运行中，不可操作");
                }
                // 更新子任务状态
                taskChild.setLastUpdateTime(System.currentTimeMillis());
                taskChild.setTaskStatus(BatchBuildTask.WAIT);
                batchBuildTaskChildMapper.updateById(taskChild);
                BatchBuildApkTask buildApkTask = batchBuildApkTaskMapper.selectById(taskChild.getBatchBuildApkTaskId());
                if (BatchBuildTask.RUNNING.equals(buildApkTask.getTaskStatus())) {
                    sendBatchBuildChildTask(buildApkTask.getLastUpdateTime(), taskChild.getLastUpdateTime(),
                            buildApkTask.getId(), taskChild.getId());
                }
            } else {
                throw new BizException("请求超时，请重试");
            }
        } catch (BizException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BizException("启动任务失败");
        } finally {
            // 释放锁
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }

    }

    /**
     * 统计任务的 子任务状态
     * @param records
     */
    public void statisticsBuildStatus(List<BatchBuildApkTask> records) {

        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<Long> collect = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
        QueryWrapper<BatchBuildTaskChild> wrapper = Wrappers.<BatchBuildTaskChild>query()
                .select("batch_build_apk_task_id as batchBuildApkTaskId", "count(*) as total")
                .groupBy("batch_build_apk_task_id")
                .eq("task_status", BatchBuildTask.SUCCESS)
                .in("batch_build_apk_task_id", collect);
        List<Map<String, Object>> buildSuccessList = batchBuildTaskChildMapper.selectMaps(wrapper);
        wrapper = Wrappers.<BatchBuildTaskChild>query()
                .select("batch_build_apk_task_id as batchBuildApkTaskId", "count(*) as total")
                .groupBy("batch_build_apk_task_id")
                .eq("task_status", BatchBuildTask.ERROR)
                .in("batch_build_apk_task_id", collect);
        List<Map<String, Object>> buildErrorList = batchBuildTaskChildMapper.selectMaps(wrapper);
        wrapper = Wrappers.<BatchBuildTaskChild>query()
                .select("batch_build_apk_task_id as batchBuildApkTaskId", "count(*) as total")
                .groupBy("batch_build_apk_task_id")
                .eq("task_status", BatchBuildTask.STOP)
                .in("batch_build_apk_task_id", collect);
        List<Map<String, Object>> buildStopList = batchBuildTaskChildMapper.selectMaps(wrapper);
        Map<Long, Integer> successMap = new HashMap<>(buildSuccessList.size());
        for (Map<String, Object> map : buildSuccessList) {
            Long groupingId = Convert.toLong(map.get("batchBuildApkTaskId"));
            Integer total = Convert.toInt(map.get("total"));
            if (Objects.nonNull(groupingId) && Objects.nonNull(total)) {
                successMap.put(groupingId, total);
            }
        }
        Map<Long, Integer> errorMap = new HashMap<>(buildErrorList.size());
        for (Map<String, Object> map : buildErrorList) {
            Long groupingId = Convert.toLong(map.get("batchBuildApkTaskId"));
            Integer total = Convert.toInt(map.get("total"));
            if (Objects.nonNull(groupingId) && Objects.nonNull(total)) {
                errorMap.put(groupingId, total);
            }
        }
        Map<Long, Integer> stopMap = new HashMap<>(buildErrorList.size());
        for (Map<String, Object> map : buildStopList) {
            Long groupingId = Convert.toLong(map.get("batchBuildApkTaskId"));
            Integer total = Convert.toInt(map.get("total"));
            if (Objects.nonNull(groupingId) && Objects.nonNull(total)) {
                stopMap.put(groupingId, total);
            }
        }

        for (BatchBuildApkTask record : records) {
            Integer integer = successMap.get(record.getId());
            if (Objects.isNull(integer)) {
                record.setFinish(0);
            } else {
                record.setFinish(integer);
            }
            Integer error = errorMap.get(record.getId());
            if (Objects.isNull(error)) {
                record.setFail(0);
            } else {
                record.setFail(error);
            }
            Integer stop = stopMap.get(record.getId());
            if (Objects.isNull(stop)) {
                record.setStop(0);
            } else {
                record.setStop(stop);
            }
        }

    }

    public void setTenantName(List<BatchBuildTaskChild> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<String> codeList = records.stream().map(BatchBuildTaskChild::getCode).collect(Collectors.toList());

        List<Tenant> tenants = tenantMapper.selectList(Wraps.<Tenant>lbQ().in(Tenant::getCode, codeList).select(Tenant::getName,Tenant::getCode));
        Map<String, String> collect = tenants.stream().collect(Collectors.toMap(Tenant::getCode, Tenant::getName, (o1, o2) -> o2));
        for (BatchBuildTaskChild record : records) {
            String s = collect.get(record.getCode());
            record.setTenantName(s);
        }
    }
}
