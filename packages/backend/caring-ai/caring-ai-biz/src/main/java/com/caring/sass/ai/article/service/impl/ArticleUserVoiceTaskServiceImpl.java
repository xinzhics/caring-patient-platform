package com.caring.sass.ai.article.service.impl;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.SaasCmsUtils;
import com.caring.sass.ai.article.config.ArticleUserPayConfig;
import com.caring.sass.ai.article.dao.ArticleUserVideoTemplateMapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceMapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceTaskMapper;
import com.caring.sass.ai.article.service.*;
import com.caring.sass.ai.dto.humanVideo.BaiduVideoDTO;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.humanVideo.task.VideoCreationRequest;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @date 2025-02-25
 */
@Slf4j
@Service
public class ArticleUserVoiceTaskServiceImpl extends SuperServiceImpl<ArticleUserVoiceTaskMapper, ArticleUserVoiceTask> implements ArticleUserVoiceTaskService {


    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    ArticleUserPayConfig articleUserPayConfig;

    @Autowired
    ArticleUserVoiceTaskMapper articleUserVoiceTaskMapper;

    @Autowired
    ArticleUserVoiceMapper articleUserVoiceMapper;


    @Autowired
    ArticleUserVideoTemplateMapper articleUserVideoTemplateMapper;

    @Autowired
    ArticleEventLogService articleEventLogService;

    @Override
    protected R<Boolean> handlerSave(ArticleUserVoiceTask model) {
        return super.handlerSave(model);
    }


    /**
     * 视频任务制作线程池。
     */
    @Override
    @Async
    public void startVideoTask() {

        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(redisTaskListKey + ":lock", "1", 10, TimeUnit.MINUTES);
        if (ifAbsent !=null && !ifAbsent) {
            return;
        }
        try {
            List<ArticleUserVoiceTask> articleUserVoiceTasks = articleUserVoiceTaskMapper.selectList(Wraps.<ArticleUserVoiceTask>lbQ()
                    .in(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.MAKE_VIDEO, HumanVideoTaskStatus.WAIT_VIDEO,
                            HumanVideoTaskStatus.WAIT_AUDIO, HumanVideoTaskStatus.MAKE_AUDIO));

            for (ArticleUserVoiceTask voiceTask : articleUserVoiceTasks) {
                // 如果 任务已经存在。那么不重复添加
                Boolean aBoolean = redisTemplate.boundSetOps(redisTaskSETKey).isMember(voiceTask.getId().toString());
                if (aBoolean != null && aBoolean) {
                    continue;
                }
                redisTemplate.boundListOps(redisTaskListKey).leftPush(voiceTask.getId().toString());
            }
        } catch (Exception e) {
            log.error("视频任务制作线程池异常", e);
        }
    }

    /**
     * 检查参数是否符合要求
     * @param params
     * @return
     */
    public boolean checkParams(VideoCreationRequest params) {
        String taskName = params.getTaskName();
        if (StringUtils.isEmpty(taskName)) {
            throw new BizException("任务名称不能为空");
        }
        Integer templateType = params.getTemplateType();
        if (Objects.isNull(templateType)) {
            throw new BizException("请选择形象类型");
        }
        if (Objects.isNull(params.getTemplateId())) {
            throw new BizException("请选择形象");
        }
        if (Objects.isNull(params.getCreateType())) {
            throw new BizException("请选择创作方式");
        }
        if (params.getCreateType().equals(1)) {
            if (Objects.isNull(params.getVoiceId())) {
                throw new BizException("请选择音色");
            }
            if (StringUtils.isEmpty(params.getTalkText())) {
                throw new BizException("请填写创作文本");
            }
        } else if (params.getCreateType().equals(2)) {

            if (StringUtils.isEmpty(params.getAudioUrl())) {
                throw new BizException("请上传录音文件");
            }

        } else if (params.getCreateType().equals(3)) {
            if (Objects.isNull(params.getPodcastId())) {
                throw new BizException("请选择博客");
            }
        }
        if (Objects.isNull(params.getUserId())) {
            throw new BizException("请选择用户");
        }
        return true;
    }

    /**
     * 创作视频任务
     * @param creationRequest
     */
    @Override
    public void submitVideoTask(VideoCreationRequest creationRequest) {

        Integer videoSpend = articleUserPayConfig.getCreateVideoSpend();
        articleUserService.deductEnergy(creationRequest.getUserId(), videoSpend, ConsumptionType.VIDEO_CREATION);

        // 校验参数是否符合要求。
        checkParams(creationRequest);
        // 创建视频合成任务
        ArticleUserVoiceTask userVoiceTask = new ArticleUserVoiceTask();
        userVoiceTask.setTaskStatus(HumanVideoTaskStatus.NOT_START)
                .setUserId(creationRequest.getUserId())
                .setTaskName(creationRequest.getTaskName())
                .setTaskType(creationRequest.getTemplateType() == 1 ? "图片数字人" : "对口型数字人")
                .setTemplateType(creationRequest.getTemplateType())
                .setTemplateId(creationRequest.getTemplateId())
                .setVoiceId(creationRequest.getVoiceId())
                .setCreateType(creationRequest.getCreateType())
                .setTalkText(creationRequest.getTalkText())
                .setAudioUrl(creationRequest.getAudioUrl())
                .setPodcastId(creationRequest.getPodcastId());
        baseMapper.insert(userVoiceTask);
        redisTemplate.boundSetOps(redisTaskSETKey).add(userVoiceTask.getId().toString());
        redisTemplate.boundListOps(redisTaskListKey).leftPush(userVoiceTask.getId().toString());

        // 对使用音色，和形象。进行次数加1
        if (Objects.nonNull(creationRequest.getVoiceId())) {

            UpdateWrapper<ArticleUserVoice> wrapper = new UpdateWrapper<>();
            wrapper.setSql(" use_count = use_count + 1 ");
            wrapper.eq("id", creationRequest.getVoiceId());
            articleUserVoiceMapper.update(new ArticleUserVoice(), wrapper);
        }
        if (Objects.nonNull(creationRequest.getTemplateId())) {
            UpdateWrapper<ArticleUserVideoTemplate> wrapper = new UpdateWrapper<>();
            wrapper.setSql(" use_count = use_count + 1 ");
            wrapper.eq("id", creationRequest.getTemplateId());
            articleUserVideoTemplateMapper.update(new ArticleUserVideoTemplate(), wrapper);
        }
        articleEventLogService.save(ArticleEventLog.builder()
                .userId(creationRequest.getUserId())
                .eventType(ArticleEventLogType.HUMAN_VIDEO_START_CREATE)
                .taskId(userVoiceTask.getId())
                .build());
    }



    /**
     * 百度回调处理
     * @param taskId
     * @param videoUrl
     */
    @Override
    public void updateSuccess(String taskId, String videoUrl) {

        ArticleUserVoiceTask videoTask = baseMapper.selectOne(Wraps.<ArticleUserVoiceTask>lbQ()
                .eq(ArticleUserVoiceTask::getTaskId, taskId)
                .last(" limit 0,1 "));

        // 百度回调过来。只有任务状态
        if (Objects.nonNull(videoTask)) {
            if (videoTask.getTaskStatus().equals(HumanVideoTaskStatus.SUCCESS)
                    || videoTask.getTaskStatus().equals(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO)) {
                return;
            }

            videoTask.setGenerateAudioUrl(videoUrl);
            // 下载视频。 上传阿里云oss
            BaiduVideoDTO videoDTO = new BaiduVideoDTO();
            videoDTO.setBusinessId(videoTask.getId().toString());
            videoDTO.setBusinessClassName(ArticleUserVoiceTask.class.getSimpleName());
            redisTemplate.opsForList().leftPush("video_download_list_handle", videoDTO.toJSONString());

            videoTask.setTaskStatus(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO);
            baseMapper.updateById(videoTask);
        }

    }

    /**
     * 百度回调处理
     * @param taskId
     * @param errorMessage
     */
    @Override
    public void updateFailed(String taskId, String errorMessage) {

        ArticleUserVoiceTask videoTask = baseMapper.selectOne(Wraps.<ArticleUserVoiceTask>lbQ()
                .eq(ArticleUserVoiceTask::getTaskId, taskId)
                .last(" limit 0,1 "));
        if (Objects.nonNull(videoTask)) {
            videoTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
            videoTask.setErrorMessage(errorMessage);
            baseMapper.updateById(videoTask);
        }
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean codeCheck = super.removeById(id);
        if (codeCheck) {
//            SaasCmsUtils.deleteSaasStudioCms(id.toString(), "CMS_TYPE_VIDEO");
        }
        return true ;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            removeById(id);
        }
        return true;
    }
}

