package com.caring.sass.ai.card.service.impl;


import com.caring.sass.ai.card.dao.BusinessCardMapper;
import com.caring.sass.ai.card.dao.BusinessCardUseDayStatisticsMapper;
import com.caring.sass.ai.card.service.BusinessCardUseDayStatisticsService;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardUseDayStatistics;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 名片使用数据日统计
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Slf4j
@Service

public class BusinessCardUseDayStatisticsServiceImpl extends SuperServiceImpl<BusinessCardUseDayStatisticsMapper, BusinessCardUseDayStatistics> implements BusinessCardUseDayStatisticsService {

    @Autowired
    BusinessCardMapper businessCardMapper;

    /**
     * 一个名片。每日只能有一个统计数据。
     * 不存在则创建
     * @param businessCardId
     */
    @Override
    public synchronized Long synchronizedQueryTodayStatisticsId(Long businessCardId) {

        BusinessCardUseDayStatistics statistics = baseMapper.selectOne(Wraps.<BusinessCardUseDayStatistics>lbQ()
                .eq(BusinessCardUseDayStatistics::getBusinessCardId, businessCardId)
                .eq(BusinessCardUseDayStatistics::getUseDate, LocalDate.now()));
        if (statistics == null) {
            BusinessCard businessCard = businessCardMapper.selectById(businessCardId);
            if (businessCard == null) {
                return null;
            }
            return synchronizedQueryTodayStatisticsId(businessCard, LocalDate.now());
        } else {
            return statistics.getId();
        }
    }


    public Long synchronizedQueryTodayStatisticsId(BusinessCard businessCard, LocalDate localDate) {
        BusinessCardUseDayStatistics statistics = BusinessCardUseDayStatistics.builder()
                .hospitalName(businessCard.getDoctorHospital())
                .departmentName(businessCard.getDoctorDepartment())
                .doctorName(businessCard.getDoctorName())
                .businessCardId(businessCard.getId())
                .organId(businessCard.getOrganId())
                .useDate(localDate)
                .numberOfViews(0)
                .peopleOfViews(0)
                .forwardingFrequency(0)
                .aiDialogueClickCount(0)
                .aiDialogueNumberCount(0)
                .build();
        baseMapper.insert(statistics);
        return statistics.getId();
    }



    @Override
    public void initUserStatistics() {

        List<BusinessCard> businessCards = businessCardMapper.selectList(Wraps.lbQ());

        LocalDate now = LocalDate.now();
        for (BusinessCard businessCard : businessCards) {
            LocalDateTime createTime = businessCard.getCreateTime();
            LocalDate localDate = createTime.toLocalDate();
            while (!localDate.isAfter(now)) {
                BusinessCardUseDayStatistics statistics = baseMapper.selectOne(Wraps.<BusinessCardUseDayStatistics>lbQ()
                        .eq(BusinessCardUseDayStatistics::getBusinessCardId, businessCard.getId())
                        .eq(BusinessCardUseDayStatistics::getUseDate, localDate));
                if (statistics == null) {
                    synchronizedQueryTodayStatisticsId(businessCard, localDate);
                }
                localDate = localDate.plusDays(1);
            }
        }

    }
}
