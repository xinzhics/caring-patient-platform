package com.caring.sass.ai.article.service.impl;



import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.dao.ArticleUserFreeLimitMapper;
import com.caring.sass.ai.article.service.ArticleUserFreeLimitService;
import com.caring.sass.ai.entity.article.ArticleUserFreeLimit;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 科普创作会员免费额度
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-27
 */
@Slf4j
@Service

public class ArticleUserFreeLimitServiceImpl extends SuperServiceImpl<ArticleUserFreeLimitMapper, ArticleUserFreeLimit> implements ArticleUserFreeLimitService {


    /**
     * 用户每次购买会员。
     * 增加 3次形象制作次数和 1次声音克隆次数
     */
    @Override
    public boolean save(ArticleUserFreeLimit model) {

        return super.save(model);
    }


    /**
     * 扣除用户免费额度
     * @param userId
     * @param type 1表示扣除声音次数， 2 表示扣除形象次数
     * @return
     */
    @Transactional
    @Override
    public boolean deductLimit(Long userId, int type) {

        LbqWrapper<ArticleUserFreeLimit> wrapper = Wraps.<ArticleUserFreeLimit>lbQ()
                .last(" limit 0, 1 ")
                .eq(ArticleUserFreeLimit::getUserId, userId)
                .gt(ArticleUserFreeLimit::getExpirationDate, LocalDateTime.now())
                .orderByAsc(ArticleUserFreeLimit::getExpirationDate);

        if (type == 1) {
            wrapper.gt(ArticleUserFreeLimit::getVoiceRemainingTimes, 0);
        } else if (type == 2) {
            wrapper.gt(ArticleUserFreeLimit::getHumanRemainingTimes, 0);
        } else {
            throw new BizException("不存在的业务");
        }

        ArticleUserFreeLimit humanLimit = baseMapper.selectOne(wrapper);
        if (Objects.isNull(humanLimit)) {
            return false;
        }
        UpdateWrapper<ArticleUserFreeLimit> updateWrapper = new UpdateWrapper<>();
        if (type == 1) {
            updateWrapper.gt("voice_remaining_times", 0);
            updateWrapper.setSql("voice_remaining_times = voice_remaining_times -1");
        } else {
            updateWrapper.gt("human_remaining_times", 0);
            updateWrapper.setSql("human_remaining_times = human_remaining_times -1");
        }
        updateWrapper.eq("id", humanLimit.getId());
        int update = baseMapper.update(humanLimit, updateWrapper);
        if (update > 0) {
            return true;
        }
        return false;
    }

}
