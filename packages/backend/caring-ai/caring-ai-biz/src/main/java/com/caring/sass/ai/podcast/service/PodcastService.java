package com.caring.sass.ai.podcast.service;

import com.caring.sass.ai.dto.podcast.PodcastPreviewContentsDTO;
import com.caring.sass.ai.dto.podcast.PodcastSoundSetSaveDTO;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.podcast.Podcast;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 业务接口
 * 播客
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-12
 */
public interface PodcastService extends SuperService<Podcast> {

    Podcast submitSound(PodcastSoundSetSaveDTO soundSetSaveDTO);


    SseEmitter createdSee(Long podcastId);

    /**
     * 检查用户是否还有 可用的 制作播客次数
     *
     * @param podcastId
     */
    SseEmitter syncCreatedPodcastPreviewContents(@NotNull(message = "播客ID不能为空") Long podcastId);


    boolean closeSse(Long podcastId);

    /**
     * 更新预览内容。
     * 解析，并生成开始生产音频。
     * @param podcastPreviewContentsDTO
     */
    void podcastPreviewContents(PodcastPreviewContentsDTO podcastPreviewContentsDTO);

    /**
     * 只保存一下对话内容
     * @param podcastPreviewContentsDTO
     */
   void savePodcastContent(PodcastPreviewContentsDTO podcastPreviewContentsDTO);


    void syncSendCurrentContent(String uid);

    /**
     * 查询是否存在未完成的播客制作
     * @param userId
     * @return
     */
    Podcast queryExistNotFinishPodcast(Long userId);


    void checkPodcastName(Podcast podcast);

}
