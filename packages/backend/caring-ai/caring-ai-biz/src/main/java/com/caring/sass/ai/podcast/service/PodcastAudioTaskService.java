package com.caring.sass.ai.podcast.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.podcast.PodcastAudioTask;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 播客音频任务
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-12
 */
public interface PodcastAudioTaskService extends SuperService<PodcastAudioTask> {


    /**
     * 创建制作音频任务 并上传到 火山。
     * @param podcastId
     * @param tasks
     */
    void createAudioTask(Long podcastId, List<PodcastAudioTask> tasks);


    void startMergeAudioTask();


}
