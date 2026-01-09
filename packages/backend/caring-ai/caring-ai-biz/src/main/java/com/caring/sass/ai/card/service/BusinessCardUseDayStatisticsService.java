package com.caring.sass.ai.card.service;

import com.caring.sass.ai.entity.card.BusinessCardUseDayStatistics;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 名片使用数据日统计
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
public interface BusinessCardUseDayStatisticsService extends SuperService<BusinessCardUseDayStatistics> {



    Long synchronizedQueryTodayStatisticsId(Long businessCardId);


    void initUserStatistics();


}
