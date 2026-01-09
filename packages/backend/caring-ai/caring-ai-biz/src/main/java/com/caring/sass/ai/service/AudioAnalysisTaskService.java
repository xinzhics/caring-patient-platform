package com.caring.sass.ai.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.AudioAnalysisTask;
import com.caring.sass.ai.dto.AudioAnalysisTaskTranscriptUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskAnalysisUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskChatUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskPublishDTO;

/**
 * <p>
 * 业务接口
 * 音频解析任务表
 * </p>
 *
 * @author leizhi
 * @date 2025-12-12
 */
public interface AudioAnalysisTaskService extends SuperService<AudioAnalysisTask> {

    /**
     * 更新转录数据
     * @param dto 转录数据更新DTO
     * @return 是否更新成功
     */
    boolean updateTranscript(AudioAnalysisTaskTranscriptUpdateDTO dto);

    /**
     * 更新AI解析数据
     * @param dto AI解析数据更新DTO
     * @return 是否更新成功
     */
    boolean updateAnalysis(AudioAnalysisTaskAnalysisUpdateDTO dto);

    /**
     * 保存聊天记录(增量)
     * @param dto 聊天记录更新DTO
     * @return 是否保存成功
     */
    boolean updateChatHistory(AudioAnalysisTaskChatUpdateDTO dto);

    /**
     * 保存到我的作品
     * @param dto 发布作品DTO
     * @return 是否保存成功
     */
    boolean publishWork(AudioAnalysisTaskPublishDTO dto);

    /**
     * 根据主键ID查询任务
     * @param id 数据库主键ID
     * @return 任务实体
     */
    AudioAnalysisTask getById(Long id);
}
