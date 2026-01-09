package com.caring.sass.user.service.impl;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.dao.KeywordSettingMapper;
import com.caring.sass.user.entity.KeywordSetting;
import com.caring.sass.user.entity.KeywordTriggerRecord;
import com.caring.sass.user.redis.KeywordSettingRedisDTO;
import com.caring.sass.user.redis.UcenterRedisKeyConstant;
import com.caring.sass.user.service.KeywordSettingService;
import com.caring.sass.user.service.KeywordTriggerRecordService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 业务实现类
 * 关键字设置
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Service

public class KeywordSettingServiceImpl extends SuperServiceImpl<KeywordSettingMapper, KeywordSetting> implements KeywordSettingService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    KeywordTriggerRecordService keywordTriggerRecordService;

    @Override
    public boolean save(KeywordSetting model) {
        // 将关键字放入到项目的缓存中去。
        super.save(model);
        updateKeywordSettingRedis(model);
        return true;
    }

    /**
     * 将 key 存储到redis中去。
     * @param model
     */
    @Override
    public void updateKeywordSettingRedis(KeywordSetting model) {
        String redisKey = String.format(UcenterRedisKeyConstant.KeywordSettingTenant, BaseContextHandler.getTenant());
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(redisKey);
        KeywordSettingRedisDTO redisDTO = new KeywordSettingRedisDTO();
        BeanUtils.copyProperties(model, redisDTO);
        redisDTO.setId(model.getId());
        boundHashOps.put(model.getId().toString(), JSON.toJSONString(redisDTO));
    }


    /**
     * 移出redis 中缓存的关键字
     * @param id
     */
    @Override
    public void removeKeywordSettingRedis(Serializable id) {
        String redisKey = String.format(UcenterRedisKeyConstant.KeywordSettingTenant, BaseContextHandler.getTenant());
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(redisKey);
        boundHashOps.delete(id.toString());
    }

    @Override
    public boolean updateById(KeywordSetting model) {
        // 更新项目的关键字
        super.updateById(model);
        KeywordSetting setting = baseMapper.selectById(model.getId());
        updateKeywordSettingRedis(setting);
        return true;

    }

    @Override
    public boolean removeById(Serializable id) {
        // 清除关键字
        baseMapper.deleteById(id);

        // 清除 关键字的触发记录
        keywordTriggerRecordService.remove(Wraps.<KeywordTriggerRecord>lbQ().eq(KeywordTriggerRecord::getKeywordId, id));

        // 将关键字从项目的缓存中移除
        removeKeywordSettingRedis(id);
        String tenant = BaseContextHandler.getTenant();
        Long keywordId = Long.parseLong(id.toString());
        // 关键字的全部触发次数
        try {
            redisTemplate.boundZSetOps(getKeywordTenantAllDayOneKeywordTriggerFrequency(tenant)).remove(keywordId.toString());
        } catch (Exception e) {
            log.info("移除 关键字的全部触发次数失败 {}", keywordId);
        }

        // 关键字今日触发次数
        try {
            redisTemplate.boundZSetOps(getKeywordTenantDayOneKeywordTriggerFrequency(tenant, LocalDate.now())).remove(keywordId.toString());
        } catch (Exception e) {
            log.info("移除 关键字的今日触发次数失败 {}", keywordId);
        }

        // 关键字7天触发次数
        try {
            redisTemplate.boundZSetOps(getKeywordTenant7DayKeywordTriggerFrequency(tenant)).remove(keywordId.toString());
        } catch (Exception e) {
            log.info("移除 关键字的7天触发次数失败 {}", keywordId);
        }

        // 关键字30天触发次数
        try {
            redisTemplate.boundZSetOps(getKeywordTenant30DayKeywordTriggerFrequency(tenant)).remove(keywordId.toString());
        } catch (Exception e) {
            log.info("移除 关键字的30天触发次数失败 {}", keywordId);
        }


        return true;
    }

    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    /**
     * 移除回复规则下的关键字
     *
     * @param replyId
     */
    @Override
    public void removeByReplyId(Serializable replyId) {

        List<KeywordSetting> keywordSettings = listByReplyId(Long.parseLong(replyId.toString()));
        if (CollUtil.isNotEmpty(keywordSettings)) {
            for (KeywordSetting setting : keywordSettings) {
                removeById(setting.getId());
            }
        }

    }

    /**
     * 查询回复规则下的关键字
     * @param replyId
     * @return
     */
    @Override
    public List<KeywordSetting> listByReplyId(Long replyId) {

        return baseMapper.selectList(Wraps.<KeywordSetting>lbQ().eq(KeywordSetting::getKeywordReplyId, replyId));

    }

    /**
     * 查询消息中符合的关键字，并返回
     * @param message
     * @return
     */
    @Override
    public List<KeywordSettingRedisDTO> matchingKeywordReturn(String message) {
        // 拉去 项目缓存的所有的 关键字信息
        String redisKey = String.format(UcenterRedisKeyConstant.KeywordSettingTenant, BaseContextHandler.getTenant());
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(redisKey);
        List<Object> objects = boundHashOps.values();
        List<KeywordSettingRedisDTO> keywordSettingRedisDTOS = new ArrayList<>();
        if (CollUtil.isNotEmpty(objects)) {
            for (Object object : objects) {
                KeywordSettingRedisDTO redisDTO = JSON.parseObject(object.toString(), KeywordSettingRedisDTO.class);
                String keywordName = redisDTO.getKeywordName();
                String matchType = redisDTO.getMatchType();
                if (matchingKeyword(message, keywordName, matchType)) {
                    keywordSettingRedisDTOS.add(redisDTO);
                }
            }
        }
        return keywordSettingRedisDTOS;

    }

    /**
     * 校验消息中 是否符合关键字要求
     * @param message
     * @param keywordName
     * @param matchType
     * @return
     */
    protected boolean matchingKeyword(String message, String keywordName, String matchType) {
        if (StrUtil.isEmpty(message) || StrUtil.isEmpty(keywordName) || StrUtil.isEmpty(matchType)) {
            return false;
        }
        // 全匹配
        if (KeyWordEnum.full_match.toString().equals(matchType)) {
            if (keywordName.equals(message)) {
                return true;
            }
        // 半匹配
        } else if (KeyWordEnum.semi_match.toString().equals(matchType)) {
            if (message.contains(keywordName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 关键字总触发次数
     * 关键字全部触发排行榜
     * %s 租户。使用租户分隔
     */
    public String getKeywordTenantAllDayOneKeywordTriggerFrequency(String tenantCode) {
        return String.format(UcenterRedisKeyConstant.keywordTenantAllDayOneKeywordTriggerFrequency, tenantCode);
    }


    /**
     * 每天将 40天之前那天的 关键字触发次数记录移除
     * 按关键字 每日触发排行榜
     * 关键字每日触发排行榜
     * %s 租户。使用租户分隔 %s 日期  field 为 关键字ID
     */
    public String getKeywordTenantDayOneKeywordTriggerFrequency(String tenantCode, LocalDate localDate) {
        return String.format(UcenterRedisKeyConstant.keywordTenantDayOneKeywordTriggerFrequency, tenantCode, localDate.toString());
    }

    /**
     * 关键字7天排行榜 每天晚上7点 将关键字的触发次数从 当天关键字中减去
     * 关键字每日触发排行榜
     * %s 租户。使用租户分隔  field 为 关键字ID
     */
    public String getKeywordTenant7DayKeywordTriggerFrequency(String tenantCode) {
        return String.format(UcenterRedisKeyConstant.keywordTenant7DayKeywordTriggerFrequency, tenantCode);
    }

    /**
     * 每天晚上1点。将30天前的触发次数从当前关键字中减去。
     * 关键字30天排行榜
     * 关键字每日触发排行榜
     * %s 租户。使用租户分隔  field 为 关键字ID
     */
    public String getKeywordTenant30DayKeywordTriggerFrequency(String tenantCode) {
        return String.format(UcenterRedisKeyConstant.keywordTenant30DayKeywordTriggerFrequency, tenantCode);
    }

    /**
     * 生成一个 关键字触发记录时。 维护redis上的关键字触发次数
     * @param keywordId
     * @param patientId
     */
    @Override
    public void createPatientTriggerRecord(Long keywordId, Long patientId) {

        boolean save = keywordTriggerRecordService.save(KeywordTriggerRecord.builder()
                .keywordId(keywordId)
                .patientId(patientId)
                .triggerDate(LocalDate.now())
                .triggerDateTime(LocalDateTime.now())
                .build());
        if (save) {
            String tenant = BaseContextHandler.getTenant();
            // 关键字的全部触发次数
            redisTemplate.boundZSetOps(getKeywordTenantAllDayOneKeywordTriggerFrequency(tenant)).incrementScore(keywordId.toString(), 1);


            // 关键字每日触发次数
            redisTemplate.boundZSetOps(getKeywordTenantDayOneKeywordTriggerFrequency(tenant, LocalDate.now())).incrementScore(keywordId.toString(), 1);

            // 关键字7天触发次数
            redisTemplate.boundZSetOps(getKeywordTenant7DayKeywordTriggerFrequency(tenant)).incrementScore(keywordId.toString(), 1);

            // 关键字30天触发次数
            redisTemplate.boundZSetOps(getKeywordTenant30DayKeywordTriggerFrequency(tenant)).incrementScore(keywordId.toString(), 1);

        }

    }

    /**
     * 每日 1点30执行
     * 对 7天触发次数减 最早的一天
     * 对 30天触发次数减 去最早的一天
     * 移出 40天之前的 触发次数记录
     */
    @Override
    public void syncUpdateKeywordLeaderboard() {
        List<KeywordSetting> keywordSettingList = baseMapper.selectAllTenantCode();
        LocalDate now = LocalDate.now();

        LocalDate local7DayDate = now.plusDays(-7);
        LocalDate local30DayDate = now.plusDays(-30);
        LocalDate local40DayDate = now.plusDays(-40);
        for (KeywordSetting setting : keywordSettingList) {
            String tenant = setting.getTenantCode();

            // 关键字今日前第七天的触发次数
            Set<ZSetOperations.TypedTuple<String>> typed7Tuples = redisTemplate.boundZSetOps(getKeywordTenantDayOneKeywordTriggerFrequency(tenant, local7DayDate)).rangeWithScores(0, -1);
            // 关键字今日前第30天的触发次数
            Set<ZSetOperations.TypedTuple<String>> typed30TupleSet = redisTemplate.boundZSetOps(getKeywordTenantDayOneKeywordTriggerFrequency(tenant, local30DayDate)).rangeWithScores(0, -1);

            try {
                redisTemplate.delete(getKeywordTenantDayOneKeywordTriggerFrequency(tenant, local40DayDate));
            } catch (Exception r) {
                log.info("往前第40天的触发记录删除失败");
            }
            // 关键字7天触发次数
            if (typed7Tuples != null && typed7Tuples.size() > 0) {
                BoundZSetOperations<String, String> zSet7DayOperations = redisTemplate.boundZSetOps(getKeywordTenant7DayKeywordTriggerFrequency(tenant));
                for (ZSetOperations.TypedTuple<String> typed7Tuple : typed7Tuples) {
                    String value = typed7Tuple.getValue();
                    Double score = typed7Tuple.getScore();

                    if (value != null && score != null) {
                        zSet7DayOperations.incrementScore(value, -score);
                    }
                }
            }

            // 关键字30天触发次数
            if (typed30TupleSet != null && typed30TupleSet.size() > 0) {
                BoundZSetOperations<String, String> zSet30Operations = redisTemplate.boundZSetOps(getKeywordTenant30DayKeywordTriggerFrequency(tenant));
                for (ZSetOperations.TypedTuple<String> typed30Tuple : typed30TupleSet) {
                    String value = typed30Tuple.getValue();
                    Double score = typed30Tuple.getScore();
                    if (value != null && score != null) {
                        zSet30Operations.incrementScore(value, -score);
                    }
                }
            }

        }
    }


    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        LocalDate local7DayDate = now.plusDays(-7);
        LocalDate local31DayDate = now.plusDays(-30);

        System.out.println(local7DayDate);
        System.out.println(local31DayDate);
    }


}
