package com.caring.sass.ai.card.service.impl;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.card.dao.BusinessCardMapper;
import com.caring.sass.ai.card.dao.BusinessCardUseDataMapper;
import com.caring.sass.ai.card.service.BusinessCardUseDataService;
import com.caring.sass.ai.card.service.BusinessCardUseDayStatisticsService;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardUseData;
import com.caring.sass.ai.entity.card.BusinessCardUseDataType;
import com.caring.sass.ai.entity.card.BusinessCardUseDayStatistics;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.LoginDecryption;
import com.caring.sass.database.mybatis.conditions.Wraps;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 名片使用数据收集
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Slf4j
@Service

public class BusinessCardUseDataServiceImpl extends SuperServiceImpl<BusinessCardUseDataMapper, BusinessCardUseData> implements BusinessCardUseDataService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    BusinessCardUseDayStatisticsService dayStatisticsService;

    @Autowired
    BusinessCardMapper businessCardMapper;

    @Override
    public boolean save(BusinessCardUseData model) {


        Long businessCardId = model.getBusinessCardId();
        BusinessCardUseDataType dataType = model.getDataType();

        String openId = model.getOpenId();
        if (!LoginDecryption.isValidOpenId(openId)) {
            return false;
        }

        // 查询 名片今日的统计ID
        Long statisticsId = dayStatisticsService.synchronizedQueryTodayStatisticsId(businessCardId);
        UpdateWrapper<BusinessCardUseDayStatistics> updateWrapper = new UpdateWrapper<>();
        // 根据提交的 数据类型。 处理。
        if (dataType == BusinessCardUseDataType.browse) {
            // 浏览
            // 判断今日这个openId 是否已经产生过 浏览数据。
            Boolean absent = redisTemplate.boundValueOps("businessCard_browse:" + businessCardId + "_" + openId + '_' + LocalDate.now())
                    .setIfAbsent(LocalDateTime.now().toString(), 24, TimeUnit.HOURS);
            if (absent != null  && absent) {
                updateWrapper.setSql("people_of_views = people_of_views + 1");
            }
            updateWrapper.setSql("number_of_views = number_of_views + 1");
            updateWrapper.eq("id", statisticsId);
            dayStatisticsService.update(updateWrapper);

            UpdateWrapper<BusinessCard> cardUpdateWrapper = new UpdateWrapper<>();
            updateWrapper.setSql("number_of_views = number_of_views + 1");
            updateWrapper.eq("id", businessCardId);
            businessCardMapper.update(new BusinessCard(), cardUpdateWrapper);
        }

        if (dataType == BusinessCardUseDataType.forward) {
            // 转发
            updateWrapper.setSql("forwarding_frequency = forwarding_frequency + 1");
            updateWrapper.eq("id", statisticsId);
            dayStatisticsService.update(updateWrapper);
        }

        if (dataType == BusinessCardUseDataType.click_conversation) {
            // 点击会话

            Boolean absent = redisTemplate.boundValueOps("businessCard_click_conversation:" + businessCardId + "_" + openId + '_' + LocalDate.now())
                    .setIfAbsent(LocalDateTime.now().toString(), 24, TimeUnit.HOURS);
            if (absent != null  && absent) {
                updateWrapper.setSql("ai_dialogue_number_count = ai_dialogue_number_count + 1");
            }
            updateWrapper.setSql("ai_dialogue_click_count = ai_dialogue_click_count + 1");
            updateWrapper.eq("id", statisticsId);
            dayStatisticsService.update(updateWrapper);
        }

        if (dataType == BusinessCardUseDataType.give_the_thumbs_up) {
            // 点赞名片 一个openID 对 一个名片只能点赞一次。 不能取消。
            Boolean absent = redisTemplate.boundValueOps("businessCard_give_thumbs_up:" + businessCardId + "_" + openId)
                    .setIfAbsent(LocalDateTime.now().toString(), 10, TimeUnit.MINUTES);
            if (absent != null && absent) {
                Integer count = baseMapper.selectCount(Wraps.<BusinessCardUseData>lbQ()
                        .eq(BusinessCardUseData::getOpenId, openId)
                        .eq(BusinessCardUseData::getDataType, dataType)
                        .eq(BusinessCardUseData::getBusinessCardId, businessCardId));
                if (count == null || count == 0) {

                    super.save(model);
                    updateWrapper.setSql("give_thumbs_up_count = give_thumbs_up_count + 1");
                    updateWrapper.eq("id", statisticsId);
                    dayStatisticsService.update(updateWrapper);


                    UpdateWrapper<BusinessCard> cardUpdateWrapper = new UpdateWrapper<>();
                    updateWrapper.setSql("give_thumbs_up_count = give_thumbs_up_count + 1");
                    updateWrapper.eq("id", businessCardId);
                    businessCardMapper.update(new BusinessCard(), cardUpdateWrapper);
                }
            }
            return true;
        }


        return super.save(model);
    }
}
