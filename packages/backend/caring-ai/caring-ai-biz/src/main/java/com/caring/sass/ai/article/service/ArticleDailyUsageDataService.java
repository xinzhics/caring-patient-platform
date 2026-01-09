package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleDailyUsageData;
import com.caring.sass.base.service.SuperService;

import java.time.LocalDateTime;

/**
 * <p>
 * 业务接口
 * AI创作日使用数据累计
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-02
 */
public interface ArticleDailyUsageDataService extends SuperService<ArticleDailyUsageData> {

    /**
     * 昨日数据汇总
     *
     * 定时任务执行
     * 凌晨 1点开始执行。 处理昨日生成的所有数据
     *
     */
    void handleYesterdayData(LocalDateTime now);

    // 导出逻辑
    // 下载模版表格模版到本地
    // 统计 汇总 近三个月数据
    String exportTemplate();
}
