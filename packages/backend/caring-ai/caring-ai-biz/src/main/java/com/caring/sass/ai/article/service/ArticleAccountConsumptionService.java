package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleAccountConsumption;
import com.caring.sass.base.service.SuperService;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 能量豆明细关联表
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-12
 */
public interface ArticleAccountConsumptionService extends SuperService<ArticleAccountConsumption> {

    /**
     * 设置明细
     *
     * @param records
     */
    void setDetails(List<ArticleAccountConsumption> records);

}
