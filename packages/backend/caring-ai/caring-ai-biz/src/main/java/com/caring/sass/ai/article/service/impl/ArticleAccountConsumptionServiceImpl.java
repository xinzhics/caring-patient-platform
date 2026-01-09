package com.caring.sass.ai.article.service.impl;



import com.caring.sass.ai.article.dao.ArticleAccountConsumptionMapper;
import com.caring.sass.ai.article.dao.ArticleUserConsumptionMapper;
import com.caring.sass.ai.article.service.ArticleAccountConsumptionService;
import com.caring.sass.ai.entity.article.ArticleAccountConsumption;
import com.caring.sass.ai.entity.article.ArticleUserConsumption;
import com.caring.sass.ai.entity.textual.TextualInterpretationUserConsumption;
import com.caring.sass.ai.textual.dao.TextualInterpretationUserConsumptionMapper;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 能量豆明细关联表
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-12
 */
@Slf4j
@Service

public class ArticleAccountConsumptionServiceImpl extends SuperServiceImpl<ArticleAccountConsumptionMapper, ArticleAccountConsumption> implements ArticleAccountConsumptionService {
    @Autowired
    TextualInterpretationUserConsumptionMapper textualInterpretationUserConsumptionMapper;

    @Autowired
    ArticleUserConsumptionMapper articleUserConsumptionMapper;

    @Override
    public void setDetails(List<ArticleAccountConsumption> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        Map<String, List<ArticleAccountConsumption>> collected = records.stream().collect(Collectors.groupingBy(ArticleAccountConsumption::getSourceType));


        List<ArticleAccountConsumption> consumptions = collected.get(ArticleAccountConsumption.sourceTypeArticle);
        if (CollectionUtils.isNotEmpty(consumptions)) {
            List<Long> ids = consumptions.stream().map(ArticleAccountConsumption::getConsumptionId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                List<ArticleUserConsumption> articleList = articleUserConsumptionMapper.selectBatchIds(ids);

                Map<Long, ArticleUserConsumption> collect = articleList.stream().collect(Collectors.toMap(ArticleUserConsumption::getId, a -> a));
                for (ArticleAccountConsumption consumption : consumptions) {
                    ArticleUserConsumption userConsumption = collect.get(consumption.getConsumptionId());

                    consumption.setUserId(userConsumption.getUserId());
                    consumption.setMessageRemark(userConsumption.getMessageRemark());
                    consumption.setConsumptionType(userConsumption.getConsumptionType());
                    consumption.setConsumptionAmount(userConsumption.getConsumptionAmount());
                    consumption.setCreateTime(userConsumption.getCreateTime());
                    consumption.setUpdateTime(userConsumption.getUpdateTime());
                }

            }
        }


        List<ArticleAccountConsumption> accountConsumptions = collected.get(ArticleAccountConsumption.sourceTypeTextual);
        if (CollectionUtils.isNotEmpty(accountConsumptions)) {
            List<Long> ids = accountConsumptions.stream().map(ArticleAccountConsumption::getConsumptionId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                List<TextualInterpretationUserConsumption> textualList = textualInterpretationUserConsumptionMapper.selectBatchIds(ids);
                Map<Long, TextualInterpretationUserConsumption> collect = textualList.stream().collect(Collectors.toMap(TextualInterpretationUserConsumption::getId, a -> a));
                for (ArticleAccountConsumption consumption : accountConsumptions) {
                    TextualInterpretationUserConsumption textualInterpretationUserConsumption = collect.get(consumption.getConsumptionId());
                    consumption.setUserId(textualInterpretationUserConsumption.getUserId());
                    consumption.setMessageRemark(textualInterpretationUserConsumption.getMessageRemark());
                    consumption.setConsumptionType(textualInterpretationUserConsumption.getConsumptionType());
                    consumption.setConsumptionAmount(textualInterpretationUserConsumption.getConsumptionAmount());
                    consumption.setCreateTime(textualInterpretationUserConsumption.getCreateTime());
                    consumption.setUpdateTime(textualInterpretationUserConsumption.getUpdateTime());
                }
            }
        }

    }
}
