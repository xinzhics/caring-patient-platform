package com.caring.sass.nursing.service.statistics.impl;

import com.caring.sass.nursing.dto.statistics.StatisticsStatus;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * @ClassName ChartOrderRedisUtil
 * @Description
 * @Author yangShuai
 * @Date 2022/4/29 13:23
 * @Version 1.0
 */
public class ChartOrderRedisUtil {


    /**
     * 创建项目下 主统计图， 达标率统计图，基准值统计图的排序
     * @param redisTemplate
     * @param tenant
     * @return
     */
    public static Long redisTaskSortIncrement(RedisTemplate<String, String> redisTemplate, String tenant) {

        Boolean key = redisTemplate.boundHashOps(StatisticsStatus.redisOrderKey).hasKey(tenant);
        if (key == null || !key) {
            redisTemplate.boundHashOps(StatisticsStatus.redisOrderKey).put(tenant, "2");
            return 2L;
        }
        return redisTemplate.boundHashOps(StatisticsStatus.redisOrderKey).increment(tenant, 1);
    }

    /**
     * 当直接更新某个统计图的排序时，判断是否超出redis中最大的排序， 超出， 则更新redis
     * @param redisTemplate
     * @param tenant
     * @param currentOrder
     */
    public static void redisTaskSortMaxIncrement(RedisTemplate<String, String> redisTemplate, String tenant, Integer currentOrder) {

        Object o = redisTemplate.boundHashOps(StatisticsStatus.redisOrderKey).get(tenant);
        if (Objects.isNull(o)) {
            redisTemplate.boundHashOps(StatisticsStatus.redisOrderKey).put(tenant, currentOrder.toString());
        } else {
            int parseInt = Integer.parseInt(o.toString());
            int max = Math.max(parseInt, currentOrder);
            redisTemplate.boundHashOps(StatisticsStatus.redisOrderKey).put(tenant, max + "");
        }
    }

    /**
     * 设置项目初始的排序大小
     * 项目复制时， 可能已经有很多项目统计的情况。
     * @param redisTemplate
     * @param tenant
     * @param value
     */
    public static void setChartOrderInitValue(RedisTemplate<String, String> redisTemplate, String tenant, int value) {
        redisTemplate.boundHashOps(StatisticsStatus.redisOrderKey).put(tenant, value + "");
    }

    /**
     * 记录 每个排序对应的 统计图id
     * 避免在手动修改排序是，序号冲突问题
     * @param redisTemplate
     * @param tenant
     * @param chartOrder
     * @param chartId
     */
    public static void saveTaskChartOrder(RedisTemplate<String, String> redisTemplate, String tenant, Integer chartOrder, Long chartId) {
        redisTemplate.opsForHash().put(StatisticsStatus.statisticsTaskOrderKey + tenant, chartOrder.toString(), chartId.toString());
    }

    /**
     * 移除 排序
     *
     * @param redisTemplate
     * @param tenant
     * @param chartOrder
     */
    public static void delTaskChartOrder(RedisTemplate<String, String> redisTemplate, String tenant, Integer chartOrder) {
        redisTemplate.opsForHash().delete(StatisticsStatus.statisticsTaskOrderKey + tenant, chartOrder.toString());
    }

    /**
     * 获取排序对应的value
     *  @param redisTemplate
     * @param tenant
     * @param chartOrder
     * @return
     */
    public static String getTaskChartOrder(RedisTemplate<String, String> redisTemplate, String tenant, Integer chartOrder) {
        Object o = redisTemplate.opsForHash().get(StatisticsStatus.statisticsTaskOrderKey + tenant, chartOrder.toString());
        if (Objects.isNull(o)) {
            return null;
        } else {
            return o.toString();
        }
    }


}
