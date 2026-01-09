package com.caring.sass.ai.article.service;

import com.caring.sass.ai.dto.article.ArticleVideoDTO;
import com.caring.sass.ai.entity.article.ArticleUserVideoTemplate;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 视频底板
 * </p>
 *
 * @author leizhi
 * @date 2025-02-26
 */
public interface ArticleUserVideoTemplateService extends SuperService<ArticleUserVideoTemplate> {

    /**
     * 提取视频中的音频信息和视频参数
     * @param articleVideoDTO
     */
    void obtainAudioFromTheVideo(ArticleVideoDTO articleVideoDTO);



    void xxjobObtainAudioFromTheVideo();

}
