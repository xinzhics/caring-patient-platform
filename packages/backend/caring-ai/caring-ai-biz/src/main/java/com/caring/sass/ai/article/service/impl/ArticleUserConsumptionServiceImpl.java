package com.caring.sass.ai.article.service.impl;


import com.caring.sass.ai.article.dao.ArticleUserConsumptionMapper;
import com.caring.sass.ai.article.service.ArticleUserConsumptionService;
import com.caring.sass.ai.entity.article.ArticleUserConsumption;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 科普创作用户能量豆消费记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
@Slf4j
@Service

public class ArticleUserConsumptionServiceImpl extends SuperServiceImpl<ArticleUserConsumptionMapper, ArticleUserConsumption> implements ArticleUserConsumptionService {
}
