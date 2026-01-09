package com.caring.sass.ai.card.service.impl;



import com.caring.sass.ai.card.dao.BusinessCardHumanLimitMapper;
import com.caring.sass.ai.card.service.BusinessCardHumanLimitService;
import com.caring.sass.ai.entity.card.BusinessCardHumanLimit;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 用户数字人额度
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-14
 */
@Slf4j
@Service

public class BusinessCardHumanLimitServiceImpl extends SuperServiceImpl<BusinessCardHumanLimitMapper, BusinessCardHumanLimit> implements BusinessCardHumanLimitService {



    @Transactional
    @Override
    public boolean deductLimit(Long userId) {

        BusinessCardHumanLimit humanLimit = baseMapper.selectOne(Wraps.<BusinessCardHumanLimit>lbQ()
                .last(" limit 0, 1 ")
                .eq(BusinessCardHumanLimit::getUserId, userId)
                .gt(BusinessCardHumanLimit::getRemainingTimes, 0)
                .gt(BusinessCardHumanLimit::getExpirationDate, LocalDateTime.now())
                .orderByAsc(BusinessCardHumanLimit::getExpirationDate));
        if (Objects.isNull(humanLimit)) {
            return false;
        }
        humanLimit.setRemainingTimes(humanLimit.getRemainingTimes() - 1);
        baseMapper.updateById(humanLimit);
        return true;
    }
}
