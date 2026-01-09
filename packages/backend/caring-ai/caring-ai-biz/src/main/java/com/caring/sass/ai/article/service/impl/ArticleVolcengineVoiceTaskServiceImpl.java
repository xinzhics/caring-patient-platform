package com.caring.sass.ai.article.service.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.article.dao.ArticleUserVideoTemplateMapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceTaskMapper;
import com.caring.sass.ai.article.dao.ArticleVolcengineVoiceTaskMapper;
import com.caring.sass.ai.article.service.ArticleVolcengineVoiceTaskService;
import com.caring.sass.ai.dto.humanVideo.BaiduVideoDTO;
import com.caring.sass.ai.entity.article.ArticleUserVideoTemplate;
import com.caring.sass.ai.entity.article.ArticleUserVoiceTask;
import com.caring.sass.ai.entity.article.ArticleVolcengineVoiceTask;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.utils.VolcengineBasicVisualApi;
import com.caring.sass.ai.utils.VolcengineVisualApi;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 火山方案视频任务
 * </p>
 *
 * @author 杨帅
 * @date 2025-08-13
 */
@Slf4j
@Service

public class ArticleVolcengineVoiceTaskServiceImpl extends SuperServiceImpl<ArticleVolcengineVoiceTaskMapper, ArticleVolcengineVoiceTask> implements ArticleVolcengineVoiceTaskService {

    @Autowired
    ArticleUserVideoTemplateMapper articleUserVideoTemplateMapper;

    @Autowired
    ArticleUserVoiceTaskMapper articleUserVoiceTaskMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public void handleCreateImageTask() {

        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("VOLCENGINE_CREATE_IMAGE_TASK", "1", 5, TimeUnit.MINUTES);
        if (ifAbsent == null || !ifAbsent) {
            return;
        }

        try {
            // 查询数据库中 等待 创建形象的任务
            List<ArticleVolcengineVoiceTask> voiceTasks = baseMapper.selectList(Wraps.<ArticleVolcengineVoiceTask>lbQ()
                    .eq(ArticleVolcengineVoiceTask::getTaskStatus, HumanVideoTaskStatus.WAIT_IMAGE));

            for (ArticleVolcengineVoiceTask voiceTask : voiceTasks) {
                createImageTask(voiceTask);
            }
        } finally {
            redisTemplate.delete("VOLCENGINE_CREATE_IMAGE_TASK");
        }


    }

    /**
     * 创建形象
     * @param voiceTask
     */
    public void createImageTask(ArticleVolcengineVoiceTask voiceTask) {
        Long templateId = voiceTask.getTemplateId();
        ArticleUserVideoTemplate videoTemplate = articleUserVideoTemplateMapper.selectById(templateId);
        if (videoTemplate == null) {
            return;
        }
        String avatarUrl = videoTemplate.getAvatarUrl();
        try {
            String image = VolcengineVisualApi.createImage(avatarUrl);
            if (StrUtil.isNotBlank(image)) {
                videoTemplate.setVolcengineImageTaskId(image);
                articleUserVideoTemplateMapper.updateById(videoTemplate);
            }
            voiceTask.setTaskStatus(HumanVideoTaskStatus.MAKE_IMAGE);
            baseMapper.updateById(voiceTask);
        } catch (Exception e) {
            log.info("handleCreateImageTask, task Id: {}, reason {}", voiceTask.getId(), e.getMessage());
            videoTemplate.setVolcengineImageErrorMessage(e.getMessage());
            articleUserVideoTemplateMapper.updateById(videoTemplate);
        }
    }


    /**
     * 查询形象创建结果
     */
    @Override
    public void handleMakeImageTask() {
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("VOLCENGINE_MAKE_IMAGE_TASK", "1", 5, TimeUnit.MINUTES);
        if (ifAbsent == null || !ifAbsent) {
            return;
        }
        try {
            List<ArticleVolcengineVoiceTask> voiceTasks = baseMapper.selectList(Wraps.<ArticleVolcengineVoiceTask>lbQ()
                    .orderByAsc(SuperEntity::getCreateTime)
                    .last( "limit 100")
                    .eq(ArticleVolcengineVoiceTask::getTaskStatus, HumanVideoTaskStatus.MAKE_IMAGE));


            for (ArticleVolcengineVoiceTask voiceTask : voiceTasks) {
                Long templateId = voiceTask.getTemplateId();
                ArticleUserVideoTemplate videoTemplate = articleUserVideoTemplateMapper.selectById(templateId);
                if (videoTemplate == null) {
                    continue;
                }
                String resource_id;
                try {
                    resource_id = VolcengineVisualApi.getCreateImageResult(videoTemplate.getVolcengineImageTaskId());
                } catch (Exception e) {
                    String message = e.getMessage();
                    if (message.equals("expired") || message.equals("not_found")) {
                        // 任务过期了。重试一下
                        createImageTask(voiceTask);
                    } else {
                        log.info("handleMakeImageTask, task Id: {}, reason {}", voiceTask.getId(), message);
                        videoTemplate.setVolcengineImageErrorMessage(message);
                        articleUserVideoTemplateMapper.updateById(videoTemplate);

                        voiceTask.setVolcengineTaskErrorMessage("形象创建失败");
                        baseMapper.updateById(voiceTask);
                    }
                    return;
                }

                if (StrUtil.isNotBlank(resource_id)) {
                    videoTemplate.setVolcengineImageResult(resource_id);
                    articleUserVideoTemplateMapper.updateById(videoTemplate);

                    // 形象创建有结果了。
                    // 直接开启视频制作任务
                    openMakeVideoTake(voiceTask, videoTemplate);
                }

            }
        } finally {
            redisTemplate.delete("VOLCENGINE_MAKE_IMAGE_TASK");
        }



    }


    /**
     * 开启 视频制作
     * @param voiceTask
     * @param videoTemplate
     */
    public void openMakeVideoTake(ArticleVolcengineVoiceTask voiceTask, ArticleUserVideoTemplate videoTemplate) {
        voiceTask.setTaskStatus(HumanVideoTaskStatus.MAKE_VIDEO);
        baseMapper.updateById(voiceTask);

        Integer templateType = voiceTask.getTemplateType();
        if (templateType == 1) {
            // 图片数字人
            try {
                String videoTask = VolcengineVisualApi.createVideoTask(videoTemplate.getVolcengineImageResult(), voiceTask.getAudioUrl());
                if (videoTask == null) {
                    voiceTask.setVolcengineTaskErrorMessage("视频制作任务创建失败");
                } else {
                    voiceTask.setVolcengineTaskId(videoTask);
                }
            } catch (Exception e) {
                log.error("handleMakeVideoTask, task Id: {}, reason {}", voiceTask.getId(), e.getMessage());
                voiceTask.setVolcengineTaskErrorMessage(e.getMessage());
            }
            baseMapper.updateById(voiceTask);

        } else if (templateType == 2) {
            // 视频数字人
            try {
                String videoTask = VolcengineBasicVisualApi.createVideoTask(videoTemplate.getVideoUrl(), voiceTask.getAudioUrl());
                if (videoTask == null) {
                    voiceTask.setVolcengineTaskErrorMessage("视频制作任务创建失败");
                } else {
                    voiceTask.setVolcengineTaskId(videoTask);
                }

            } catch (Exception e) {
                log.error("handleMakeVideoTask, task Id: {}, reason {}", voiceTask.getId(), e.getMessage());
                voiceTask.setVolcengineTaskErrorMessage(e.getMessage());
            }
            baseMapper.updateById(voiceTask);
        }


    }


    /**
     * 发起 视频制作任务
     */
    @Override
    public void handleWaitMakeVideoTask() {

        Boolean isRunning = redisTemplate.opsForValue().setIfAbsent("VOLCENGINE_WAIT_MAKE_VIDEO_TASK", "1", 5, TimeUnit.MINUTES);
        if (isRunning == null || !isRunning) {
            return;
        }

        try {
            List<ArticleVolcengineVoiceTask> voiceTasks = baseMapper.selectList(Wraps.<ArticleVolcengineVoiceTask>lbQ()
                    .eq(ArticleVolcengineVoiceTask::getTaskStatus, HumanVideoTaskStatus.WAIT_VIDEO));


            for (ArticleVolcengineVoiceTask voiceTask : voiceTasks) {

                Long templateId = voiceTask.getTemplateId();
                ArticleUserVideoTemplate videoTemplate = articleUserVideoTemplateMapper.selectById(templateId);

                openMakeVideoTake(voiceTask, videoTemplate);

            }
        } finally {
            redisTemplate.delete("VOLCENGINE_WAIT_MAKE_VIDEO_TASK");
        }

    }


    /**
     * 查询正在制作视频 的结果
     */
    @Override
    public void handleMakingVideoTask() {

        Boolean isRunning = redisTemplate.opsForValue().setIfAbsent("VOLCENGINE_MAKING_VIDEO_TASK", "1", 5, TimeUnit.MINUTES);
        if (isRunning == null || !isRunning) {
            return;
        }

        try {
            List<ArticleVolcengineVoiceTask> voiceTasks = baseMapper.selectList(Wraps.<ArticleVolcengineVoiceTask>lbQ()
                    .eq(ArticleVolcengineVoiceTask::getTaskStatus, HumanVideoTaskStatus.MAKE_VIDEO));

            for (ArticleVolcengineVoiceTask voiceTask : voiceTasks) {

                Integer templateType = voiceTask.getTemplateType();
                if (templateType == 1) {
                    // 图片数字人
                    try {
                        JSONObject videoResult = VolcengineVisualApi.getCreateVideoResult(voiceTask.getVolcengineTaskId());
                        if (videoResult == null) {
                        } else {
                            voiceTask.setVolcengineTaskResult(videoResult.toString());
                            String preview_url = videoResult.getString("preview_url");
                            if (StrUtil.isNotBlank(preview_url)) {
                                updateVideoTask(voiceTask.getVoiceTaskId(), preview_url);
                                voiceTask.setTaskStatus(HumanVideoTaskStatus.SUCCESS);
                            } else {
                                voiceTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                                voiceTask.setVolcengineTaskErrorMessage("没有返回视频链接");
                            }
                        }
                    } catch (Exception e) {
                        voiceTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        String message = e.getMessage();
                        if (message.equals("expired") || message.equals("not_found")) {
                            // 任务过期了。重试一下
                            ArticleUserVideoTemplate videoTemplate = articleUserVideoTemplateMapper.selectById(voiceTask.getTemplateId());
                            openMakeVideoTake(voiceTask, videoTemplate);
                        } else {
                            log.info("handleMakeImageTask, task Id: {}, reason {}", voiceTask.getId(), message);
                            voiceTask.setVolcengineTaskErrorMessage(message);
                        }
                    }
                    baseMapper.updateById(voiceTask);
                } else if (templateType == 2) {
                    // 视频数字人
                    try {
                        JSONObject videoResult = VolcengineBasicVisualApi.getCreateVideoResult(voiceTask.getVolcengineTaskId());
                        if (videoResult == null) {
                        } else {
                            voiceTask.setVolcengineTaskResult(videoResult.toString());
                            String video_url = videoResult.getString("url");
                            if (StrUtil.isNotBlank(video_url)) {
                                updateVideoTask(voiceTask.getVoiceTaskId(), video_url);
                                voiceTask.setTaskStatus(HumanVideoTaskStatus.SUCCESS);
                            } else {
                                voiceTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                                voiceTask.setVolcengineTaskErrorMessage("没有返回视频链接");
                            }
                        }
                    } catch (Exception e) {
                        voiceTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        String message = e.getMessage();
                        if (message.equals("expired") || message.equals("not_found")) {
                            // 任务过期了。重试一下
                            ArticleUserVideoTemplate videoTemplate = articleUserVideoTemplateMapper.selectById(voiceTask.getTemplateId());
                            openMakeVideoTake(voiceTask, videoTemplate);
                        } else {
                            log.info("handleMakeImageTask, task Id: {}, reason {}", voiceTask.getId(), message);
                            voiceTask.setVolcengineTaskErrorMessage(message);
                        }
                    }
                    baseMapper.updateById(voiceTask);
                }
            }
        } finally {
            redisTemplate.delete("VOLCENGINE_MAKING_VIDEO_TASK");
        }



    }


    /**
     * 修改数字人任务 视频url
     * @param taskId
     * @param videoUrl
     */
    public void updateVideoTask(Long taskId, String videoUrl) {

        ArticleUserVoiceTask voiceTask = articleUserVoiceTaskMapper.selectById(taskId);
        if (voiceTask.getTaskStatus().equals(HumanVideoTaskStatus.MAKE_VIDEO)
                || voiceTask.getTaskStatus().equals(HumanVideoTaskStatus.FAIL)) {
            voiceTask.setGenerateAudioUrl(videoUrl);
            voiceTask.setTaskStatus(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO);
            articleUserVoiceTaskMapper.updateById(voiceTask);

            BaiduVideoDTO videoDTO = new BaiduVideoDTO();
            videoDTO.setBusinessId(taskId.toString());
            videoDTO.setBusinessClassName(ArticleUserVoiceTask.class.getSimpleName());
            redisTemplate.opsForList().leftPush("video_download_list_handle", videoDTO.toJSONString());

        }


    }





}
