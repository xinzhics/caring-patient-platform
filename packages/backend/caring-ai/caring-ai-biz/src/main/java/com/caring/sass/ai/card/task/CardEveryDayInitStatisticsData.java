package com.caring.sass.ai.card.task;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.ai.card.dao.BusinessCardMapper;
import com.caring.sass.ai.card.service.BusinessCardUseDayStatisticsService;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.database.mybatis.conditions.Wraps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日凌晨生产日统计数据
 */
@Component
public class CardEveryDayInitStatisticsData {

    @Autowired
    BusinessCardMapper businessCardMapper;


    @Autowired
    BusinessCardUseDayStatisticsService statisticsService;


    public void initData() {
        int current = 1;
        int size = 200;
        Page<BusinessCard> page;
        while (true) {
            page = new Page<BusinessCard>(current, size);
            IPage<BusinessCard> selectPage = businessCardMapper.selectPage(page, Wraps.<BusinessCard>lbQ()
                    .select(SuperEntity::getId, BusinessCard::getCreateTime)
                    .orderByAsc(BusinessCard::getCreateTime));
            for (BusinessCard record : selectPage.getRecords()) {
                statisticsService.synchronizedQueryTodayStatisticsId(record.getId());
            }
            current++;
            if (current > selectPage.getPages()) {
                break;
            }
            if (selectPage.getPages() == 0) {
                break;
            }
        }
    }



}
