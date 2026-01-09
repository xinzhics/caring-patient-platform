package com.caring.sass.ai.card.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.card.dao.BusinessCardHumanLimitMapper;
import com.caring.sass.ai.card.dao.BusinessCardMapper;
import com.caring.sass.ai.card.dao.BusinessCardMemberRedemptionCodeMapper;
import com.caring.sass.ai.card.dao.BusinessCardMemberVersionMapper;
import com.caring.sass.ai.card.service.BusinessCardMemberVersionService;
import com.caring.sass.ai.entity.card.*;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 用户的会员版本
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
@Slf4j
@Service

public class BusinessCardMemberVersionServiceImpl extends SuperServiceImpl<BusinessCardMemberVersionMapper, BusinessCardMemberVersion> implements BusinessCardMemberVersionService {

    @Autowired
    BusinessCardMemberVersionMapper businessCardMemberVersionMapper;

    @Autowired
    BusinessCardMemberRedemptionCodeMapper redemptionCodeMapper;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    BusinessCardMapper businessCardMapper;

    @Override
    public void useCodeExchange(Long userId, String redemptionCode) {

        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        String key = "business_card_member_redemption:" + redemptionCode;
        try {
            lockBoolean = distributedLock.lock(key, 5000L, 20);
            if (lockBoolean) {

                // 查询兑换码有没有被使用
                BusinessCardMemberRedemptionCode memberRedemptionCode = redemptionCodeMapper.selectOne(Wraps.<BusinessCardMemberRedemptionCode>lbQ()
                        .eq(BusinessCardMemberRedemptionCode::getRedemptionCode, redemptionCode));
                if (Objects.isNull(memberRedemptionCode) || memberRedemptionCode.getDeleteFlag()) {
                    throw new BizException("兑换码不存在");
                }
                if (memberRedemptionCode.getExchangeStatus().equals(BusinessCardMemberRedemptionCode.EXCHANGE_STATUS_USED)) {
                    throw new BizException("兑换码已经被使用");
                }

                memberRedemptionCode.setExchangeStatus(BusinessCardMemberRedemptionCode.EXCHANGE_STATUS_USED);
                memberRedemptionCode.setExchangeTime(LocalDateTime.now());
                memberRedemptionCode.setUserId(userId);

                redemptionCodeMapper.updateById(memberRedemptionCode);

                setBusinessCardUserMemberVersion(userId, BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION);
            } else {
                throw new BizException("请稍后再试");
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(key);
            }
        }

    }

    @Autowired
    BusinessCardHumanLimitMapper humanLimitMapper;

    /**
     * 设置用户会员版本
     * @param userId
     */
    @Transactional
    @Override
    public void setBusinessCardUserMemberVersion(Long userId, BusinessCardMemberVersionEnum cardMemberVersionEnum) {
        BusinessCardMemberVersion memberVersion = businessCardMemberVersionMapper.selectOne(Wraps.<BusinessCardMemberVersion>lbQ()
                .eq(BusinessCardMemberVersion::getUserId, userId)
                .eq(BusinessCardMemberVersion::getMemberVersion, cardMemberVersionEnum));

        // 用户每购买一次会员版， 则给他充值 3次的数字人的制作次数，有效期为365天。 消耗时 优先消耗有效期最短的
        if (cardMemberVersionEnum.equals(BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION)) {
            BusinessCardHumanLimit humanLimit = new BusinessCardHumanLimit();
            humanLimit.setExpirationDate(LocalDateTime.now().plusDays(365));
            humanLimit.setRemainingTimes(3);
            humanLimit.setUserId(userId);
            humanLimitMapper.insert(humanLimit);
        } else if (cardMemberVersionEnum.equals(BusinessCardMemberVersionEnum.BASIC_EDITION)) {
            BusinessCardHumanLimit humanLimit = new BusinessCardHumanLimit();
            humanLimit.setExpirationDate(LocalDateTime.now().plusDays(365));
            humanLimit.setRemainingTimes(1);
            humanLimit.setUserId(userId);
            humanLimitMapper.insert(humanLimit);
        }

        if (Objects.isNull(memberVersion)) {
            memberVersion = new BusinessCardMemberVersion();
            memberVersion.setUserId(userId);
            memberVersion.setMemberVersion(cardMemberVersionEnum);
            memberVersion.setExpirationDate(LocalDateTime.now().plusDays(365));
            businessCardMemberVersionMapper.insert(memberVersion);
        } else {
            if (memberVersion.getExpirationDate().isBefore(LocalDateTime.now())) {
                memberVersion.setExpirationDate(LocalDateTime.now().plusDays(365));
            } else {
                memberVersion.setExpirationDate(memberVersion.getExpirationDate().plusDays(365));
            }
            businessCardMemberVersionMapper.updateById(memberVersion);
        }

    }


    /**
     * 查询用户的会员版本
     * @param userId
     * @return
     */
    @Override
    public BusinessCardMemberVersion queryUserVersion(Long userId) {


        BusinessCardMemberVersion memberVersion = baseMapper.selectOne(Wraps.<BusinessCardMemberVersion>lbQ()
                .eq(BusinessCardMemberVersion::getUserId, userId)
                .eq(BusinessCardMemberVersion::getMemberVersion, BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION));
        if (memberVersion == null || memberVersion.getExpirationDate().isBefore(LocalDateTime.now())) {
            memberVersion = baseMapper.selectOne(Wraps.<BusinessCardMemberVersion>lbQ()
                    .eq(BusinessCardMemberVersion::getUserId, userId)
                    .eq(BusinessCardMemberVersion::getMemberVersion, BusinessCardMemberVersionEnum.BASIC_EDITION));
        }
        if (memberVersion == null || memberVersion.getExpirationDate().isBefore(LocalDateTime.now())) {
            UpdateWrapper<BusinessCard> businessCardUpdateWrapper = new UpdateWrapper<>();
            businessCardUpdateWrapper.set("member_version", null)
                    .eq("user_id", userId);
            businessCardMapper.update(new BusinessCard(), businessCardUpdateWrapper);
            throw new BizException("用户没有购买会员或会员过期");
        }
        UpdateWrapper<BusinessCard> businessCardUpdateWrapper = new UpdateWrapper<>();
        businessCardUpdateWrapper.set("member_version", memberVersion.getMemberVersion())
                .eq("user_id", userId);
        businessCardMapper.update(new BusinessCard(), businessCardUpdateWrapper);
        return memberVersion;
    }
}
