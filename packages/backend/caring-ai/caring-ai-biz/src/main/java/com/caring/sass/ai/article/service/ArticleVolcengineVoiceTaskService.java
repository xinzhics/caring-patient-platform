package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleVolcengineVoiceTask;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 火山方案视频任务
 * </p>
 *
 * @author 杨帅
 * @date 2025-08-13
 */
public interface ArticleVolcengineVoiceTaskService extends SuperService<ArticleVolcengineVoiceTask> {


    /**
     * 2分钟调用一次
     * 处理 等待创建火山形象的任务
     */
    void handleCreateImageTask();


    /**
     * 处理正在制作形象中的火山任务
     *
     * 形象制作完毕后，
     * 直接开启 视频制作 任务
     */
    void handleMakeImageTask();


    /**
     * 处理正在等待制作视频的火山任务
     */
    void handleWaitMakeVideoTask();

    /**
     * 处理正在制作视频的火山任务
     */
    void handleMakingVideoTask();
}
