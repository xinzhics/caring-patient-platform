package com.caring.sass.ai.card.service.impl;



import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.card.dao.BusinessCardAdminMapper;
import com.caring.sass.ai.card.dao.BusinessCardMapper;
import com.caring.sass.ai.card.dao.BusinessCardUseDayStatisticsMapper;
import com.caring.sass.ai.card.service.BusinessCardAdminService;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardAdmin;
import com.caring.sass.ai.entity.card.BusinessCardUseDayStatistics;
import com.caring.sass.ai.entity.card.BusinessCardUserType;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 科普名片管理员
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Slf4j
@Service

public class BusinessCardAdminServiceImpl extends SuperServiceImpl<BusinessCardAdminMapper, BusinessCardAdmin> implements BusinessCardAdminService {


    @Autowired
    BusinessCardMapper businessCardMapper;

    @Autowired
    BusinessCardUseDayStatisticsMapper statisticsMapper;

    @Override
    public void updateCardOrgan(Long id, Long organId) {

        Long userId = BaseContextHandler.getUserId();
        BusinessCardAdmin admin = baseMapper.selectById(userId);
        if (Objects.isNull(admin)) {
            throw new RuntimeException("您没有权限");
        }
        if (!BusinessCardUserType.SYSTEM_ADMIN.equals(admin.getUserType())) {
            throw new RuntimeException("您没有权限");
        }

        BusinessCard businessCard = new BusinessCard();
        businessCard.setId(id);
        businessCard.setOrganId(organId);
        businessCardMapper.updateById(businessCard);

        UpdateWrapper<BusinessCardUseDayStatistics> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("organ_id", organId);
        updateWrapper.eq("business_card_id", id);
        statisticsMapper.update(new BusinessCardUseDayStatistics(), updateWrapper);


    }


    @Override
    public void clearUserId(Long id) {

        Long userId = BaseContextHandler.getUserId();
        BusinessCardAdmin admin = baseMapper.selectById(userId);
        if (Objects.isNull(admin)) {
            throw new RuntimeException("您没有权限");
        }
        if (!BusinessCardUserType.SYSTEM_ADMIN.equals(admin.getUserType())) {
            throw new RuntimeException("您没有权限");
        }

        BusinessCard card = businessCardMapper.selectById(id);
        card.setUserId(null);
        businessCardMapper.updateAllById(card);

    }

}
