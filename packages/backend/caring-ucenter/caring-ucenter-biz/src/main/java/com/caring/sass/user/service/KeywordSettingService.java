package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.entity.KeywordSetting;
import com.caring.sass.user.redis.KeywordSettingRedisDTO;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 关键字设置
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
public interface KeywordSettingService extends SuperService<KeywordSetting> {

    /**
     * 将 关键字 key 存储到redis中去。
     * @param model
     */
    void updateKeywordSettingRedis(KeywordSetting model);

    /**
     * 移出redis 中缓存的关键字
     * @param id
     */
    void removeKeywordSettingRedis(Serializable id);

    void removeByReplyId(Serializable replyId);

    /**
     * 查询回复规则下的关键字
     * @param replyId
     * @return
     */
    List<KeywordSetting> listByReplyId(Long replyId);

    /**
     * 查询消息中符合的关键字，并返回
     * @param message
     * @return
     */
    List<KeywordSettingRedisDTO> matchingKeywordReturn(String message);

    /**
     * 记录患者触发 关键字的信息
     * 并在redis中增加 触发计数
     * @param keywordId
     * @param patientId
     */
    void createPatientTriggerRecord(Long keywordId, Long patientId);

    void syncUpdateKeywordLeaderboard();
}
