package com.caring.sass.ai.service.impl;


import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.dao.AudioAnalysisTaskMapper;
import com.caring.sass.ai.dto.AudioAnalysisTaskAnalysisUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskChatUpdateDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskPublishDTO;
import com.caring.sass.ai.dto.AudioAnalysisTaskTranscriptUpdateDTO;
import com.caring.sass.ai.entity.AudioAnalysisTask;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.service.AudioAnalysisTaskService;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;

import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 业务实现类
 * 音频解析任务表
 * </p>
 *
 * @author leizhi
 * @date 2025-12-12
 */
@Slf4j
@Service
public class AudioAnalysisTaskServiceImpl extends SuperServiceImpl<AudioAnalysisTaskMapper, AudioAnalysisTask> implements AudioAnalysisTaskService {

    private final TextualInterpretationUserService textualInterpretationUserService;

    public AudioAnalysisTaskServiceImpl(TextualInterpretationUserService textualInterpretationUserService) {
        this.textualInterpretationUserService = textualInterpretationUserService;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTranscript(AudioAnalysisTaskTranscriptUpdateDTO dto) {
        log.info("更新转录数据, id: {}", dto.getId());
        
        AudioAnalysisTask task = getById(dto.getId());
        if (task == null) {
            throw new BizException("任务不存在");
        }
        
        task.setTranscriptData(dto.getTranscriptData());
        task.setStep(dto.getStep());
        task.setLastAnalysisTime(LocalDateTime.now());

        return updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAnalysis(AudioAnalysisTaskAnalysisUpdateDTO dto) {
        log.info("更新AI解析数据, id: {}", dto.getId());
        
        AudioAnalysisTask task = getById(dto.getId());
        if (task == null) {
            throw new BizException("任务不存在");
        }
        
        task.setSummaryData(dto.getSummaryData());
        if (StrUtil.isNotBlank(dto.getMindmapData())) {
            task.setMindmapData(dto.getMindmapData());
        }
        task.setStep(dto.getStep());
        task.setTaskStatus(dto.getTaskStatus());
        task.setLastAnalysisTime(LocalDateTime.now());

        // 扣除20个能量豆，描述就是播客解读
        if (task.getCreateUser() != null) {
            textualInterpretationUserService.deductEnergy(task.getCreateUser(), 20, TextualConsumptionType.PODCAST_INTERPRETATION);
        }

        return updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateChatHistory(AudioAnalysisTaskChatUpdateDTO dto) {
        log.info("保存聊天记录, id: {}", dto.getId());
        
        AudioAnalysisTask task = getById(dto.getId());
        if (task == null) {
            throw new BizException("任务不存在");
        }
        
        task.setChatHistory(dto.getChatHistory());
        
        return updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishWork(AudioAnalysisTaskPublishDTO dto) {
        log.info("保存到我的作品, id: {}", dto.getId());
        
        AudioAnalysisTask task = getById(dto.getId());
        if (task == null) {
            throw new BizException("任务不存在");
        }
        
        task.setTitle(dto.getTitle());
        task.setIsPublic(dto.getIsPublic());
        if (StrUtil.isNotBlank(dto.getCategory())) {
            task.setCategory(dto.getCategory());
        }
        if (StrUtil.isNotBlank(dto.getKeywords())) {
            task.setKeywords(dto.getKeywords());
        }
        task.setTaskStatus(dto.getTaskStatus());
        
        return updateById(task);
    }

    @Override
    public AudioAnalysisTask getById(Long id) {
        if (id == null) {
            throw new BizException("任务ID不能为空");
        }
        
        return super.getById(id);
    }
}
