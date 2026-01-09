package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.dto.KeywordReplyPageList;
import com.caring.sass.user.dto.KeywordReplySaveDTO;
import com.caring.sass.user.dto.KeywordReplyStatusUpdateDto;
import com.caring.sass.user.dto.KeywordReplyUpdateDTO;
import com.caring.sass.user.entity.KeywordReply;
import com.caring.sass.user.redis.KeywordReplyRedisDTO;

/**
 * <p>
 * 业务接口
 * 关键字回复内容
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
public interface KeywordReplyService extends SuperService<KeywordReply> {

    KeywordReply save(KeywordReplySaveDTO keywordReplySaveDTO);

    KeywordReply update(KeywordReplyUpdateDTO keywordReplyUpdateDTO);

    boolean updateStatus(KeywordReplyStatusUpdateDto params);

    KeywordReplyRedisDTO getKeywordReplyRedis(Long keywordReplyId);

    String getCmsContentLink(Long replyId);

    /**
     * 记录一条 关键字规则的 触发记录。
     * 并在redis中增加计数
     * @param keywordReplyId
     * @param patientId
     */
    void createPatientTriggerRecord(Long keywordReplyId, Long patientId);

    /**
     * 吧昨日触发次数。和进入触发次数 加上
     * @param replyPageList
     * @param replyId
     */
    void setTodayAndYesterdayTriggerTimes(KeywordReplyPageList replyPageList, Long replyId);
}
