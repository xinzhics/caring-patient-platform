package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleUserVoiceTask;
import com.caring.sass.ai.humanVideo.task.VideoCreationRequest;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @date 2025-02-25
 */
public interface ArticleUserVoiceTaskService extends SuperService<ArticleUserVoiceTask> {

    String redisTaskListKey = "ARTICLE_USER_VIDEO_TEMPLATE";
    String redisTaskSETKey = "ARTICLE_USER_VIDEO_TEMPLATE_SET";


    /**
     * 提交视频合成任务
     */
    void submitVideoTask(VideoCreationRequest creationRequest);


    void startVideoTask();


    /**
     * 百度回调
     * @param taskId
     * @param string
     */
    void updateFailed(String taskId, String string);

    /**
     * 百度回调
     * @param taskId
     * @param videoUrl
     */
    void updateSuccess(String taskId, String videoUrl);
}
