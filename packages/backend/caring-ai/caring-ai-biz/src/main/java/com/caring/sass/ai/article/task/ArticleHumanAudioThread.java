package com.caring.sass.ai.article.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.dao.ArticleUserVideoTemplateMapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceMapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceTaskMapper;
import com.caring.sass.ai.article.dao.ArticleVolcengineVoiceTaskMapper;
import com.caring.sass.ai.article.service.ArticleUserVoiceTaskService;
import com.caring.sass.ai.dto.humanVideo.BaiduVideoDTO;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.entity.podcast.*;
import com.caring.sass.ai.humanVideo.task.Baidu123DigitalHumanAPI;
import com.caring.sass.ai.humanVideo.task.BaiduDigitalHumanAPI;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import com.caring.sass.ai.service.HumanVideoDownloadUtil;
import com.caring.sass.ai.utils.AudioUploader;
import com.caring.sass.ai.utils.MiniMaxVoiceApi;
import com.caring.sass.ai.utils.miniMax.VoiceGenerationResponse;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.file.api.ScreenshotTaskApi;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 科普创作数字人制作线程
 */
@Slf4j
@Component
public class ArticleHumanAudioThread {

    PodcastMapper podcastMapper;

    ArticleUserVoiceTaskMapper articleUserVoiceTaskMapper;

    RedisTemplate<String, String> redisTemplate;

    MiniMaxVoiceApi miniMaxVoiceApi;

    AudioUploader audioUploader;

    ArticleUserVoiceMapper articleUserVoiceMapper;

    BaiduDigitalHumanAPI baiduDigitalHumanAPI;

    ArticleUserVideoTemplateMapper articleUserVideoTemplateMapper;

    Baidu123DigitalHumanAPI baidu123DigitalHumanAPI;

    FileUploadApi fileUploadApi;

    ScreenshotTaskApi screenshotTaskApi;

    ExecutorService executor;

   HumanVideoDownloadUtil humanVideoDownloadUtil;

    @Autowired
    ArticleVolcengineVoiceTaskMapper articleVolcengineVoiceTaskMapper;

    private final AtomicInteger maximumPoolSize = new AtomicInteger(0);


    public ArticleHumanAudioThread(PodcastMapper podcastMapper,
                                   ArticleUserVoiceTaskMapper articleUserVoiceTaskMapper,
                                   RedisTemplate<String, String> redisTemplate,
                                   MiniMaxVoiceApi miniMaxVoiceApi,
                                   AudioUploader audioUploader,
                                   ArticleUserVoiceMapper articleUserVoiceMapper,
                                   BaiduDigitalHumanAPI baiduDigitalHumanAPI,
                                   ArticleUserVideoTemplateMapper articleUserVideoTemplateMapper,
                                   Baidu123DigitalHumanAPI baidu123DigitalHumanAPI,
                                   FileUploadApi fileUploadApi,
                                   HumanVideoDownloadUtil humanVideoDownloadUtil,
                                   ScreenshotTaskApi screenshotTaskApi) {
        this.podcastMapper = podcastMapper;
        this.articleUserVideoTemplateMapper = articleUserVideoTemplateMapper;
        this.redisTemplate = redisTemplate;
        this.miniMaxVoiceApi = miniMaxVoiceApi;
        this.audioUploader = audioUploader;
        this.articleUserVoiceMapper = articleUserVoiceMapper;
        this.baiduDigitalHumanAPI = baiduDigitalHumanAPI;
        this.articleUserVoiceTaskMapper = articleUserVoiceTaskMapper;
        this.baidu123DigitalHumanAPI = baidu123DigitalHumanAPI;
        this.fileUploadApi = fileUploadApi;
        this.screenshotTaskApi = screenshotTaskApi;
        this.humanVideoDownloadUtil = humanVideoDownloadUtil;
        executor = new ThreadPoolExecutor(2, 5, 0L,
                TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(100), new NamedThreadFactory("article-video-human-", false));
    }




    /**
     * 检查视频任务。
     * 下载视频
     */
    public void checkAndDownloadVideoTask() {

        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("baidu_human_video_task_list_if_absent", "1", 5, TimeUnit.MINUTES);
        if (aBoolean != null && !aBoolean) {
            return;
        }
        List<ArticleUserVoiceTask> voiceTasks = articleUserVoiceTaskMapper.selectList(Wraps.<ArticleUserVoiceTask>lbQ()
                .eq(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.MAKE_VIDEO));
        if (CollUtil.isEmpty(voiceTasks)) {
            redisTemplate.delete("baidu_human_video_task_list_if_absent");
        }

        for (ArticleUserVoiceTask voiceTask : voiceTasks) {
            try {
                if (HumanVideoTaskStatus.SUCCESS.equals(voiceTask.getTaskStatus())
                        || HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO.equals(voiceTask.getTaskStatus())
                        || HumanVideoTaskStatus.FAIL.equals(voiceTask.getTaskStatus())) {
                    continue;
                }

                Integer templateType = voiceTask.getTemplateType();
                String taskId = voiceTask.getTaskId();

                if (templateType.equals(1) && StringUtils.isNotEmptyString(taskId)) {
                    JsonObject taskStatus = baiduDigitalHumanAPI.queryTaskStatus(taskId);
                    if (taskStatus.has("result")
                            && taskStatus.has("result") && !taskStatus.get("result").isJsonNull()
                            && taskStatus.getAsJsonObject("result").has("status")) {
                        JsonObject result = taskStatus.getAsJsonObject("result");
                        String status = result.get("status").getAsString();
                        if ("SUCCESS".equals(status)) {
                            String videoUrl = result.get("videoUrl").getAsString();
                            log.info("Task ID: {}, Video URL: {}", taskId, videoUrl);
                            BaiduVideoDTO videoDTO = new BaiduVideoDTO();
                            videoDTO.setBusinessId(voiceTask.getId().toString());
                            videoDTO.setBusinessClassName(ArticleUserVoiceTask.class.getSimpleName());
                            redisTemplate.opsForList().leftPush("video_download_list_handle", videoDTO.toJSONString());

                            voiceTask.setGenerateAudioUrl(videoUrl)
                                    .setTaskStatus(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO);
                            articleUserVoiceTaskMapper.updateById(voiceTask);
                        } else if ("FAILED".equals(status)) {
                            log.info("Task ID合成失败: {},原因为: {}", taskId, result.get("failedMessage").getAsString());
                            voiceTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                            voiceTask.setErrorMessage("视频合成失败：" + result.get("failedMessage").getAsString());
                            articleUserVoiceTaskMapper.updateById(voiceTask);
                        }
                    }
                }


                if (templateType.equals(2) && StringUtils.isNotEmptyString(taskId)) {
                    JsonObject taskStatus = baidu123DigitalHumanAPI.queryTaskStatus(taskId);
                    if (taskStatus.has("result")
                            && taskStatus.has("result") && !taskStatus.get("result").isJsonNull()
                            && taskStatus.getAsJsonObject("result").has("status")) {
                        JsonObject result = taskStatus.getAsJsonObject("result");
                        String status = result.get("status").getAsString();
                        if ("SUCCESS".equals(status)) {
                            String videoUrl = result.get("videoUrl").getAsString();
                            BaiduVideoDTO videoDTO = new BaiduVideoDTO();
                            videoDTO.setBusinessId(voiceTask.getId().toString());
                            videoDTO.setBusinessClassName(ArticleUserVoiceTask.class.getSimpleName());
                            redisTemplate.opsForList().leftPush("video_download_list_handle", videoDTO.toJSONString());

                            voiceTask.setGenerateAudioUrl(videoUrl)
                                    .setTaskStatus(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO);
                            articleUserVoiceTaskMapper.updateById(voiceTask);
                        } else if ("FAILED".equals(status)) {
                            log.info("Task ID合成失败: {},原因为: {}", taskId, result.get("failedMessage").getAsString());
                            voiceTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                            voiceTask.setErrorMessage("视频合成失败：" + result.get("failedMessage").getAsString());
                            articleUserVoiceTaskMapper.updateById(voiceTask);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("checkAndDownloadVideoTask error: {}", e.getMessage());
            }
        }

        redisTemplate.delete("baidu_human_video_task_list_if_absent");

    }


    /**
     * 开始处理数字人制作
     * @param task
     */
    public void submitAudio(ArticleUserVoiceTask task) {

        try {
            // 防止一个任务被重复执行
            Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("baidu_human_video_task_" + task.getId() + ":lock", "1", 5, TimeUnit.MINUTES);
            if (ifAbsent !=null && !ifAbsent) {
                return;
            }
            // 这里任务不在细分。
            HumanVideoTaskStatus taskStatus = task.getTaskStatus();
            // 声音合成。
            Integer createType = task.getCreateType();
            String callBack = ApplicationDomainUtil.apiUrl() +"/api/ai/articleUserVoiceTask/anno/callback";
            if (taskStatus == HumanVideoTaskStatus.WAIT_AUDIO || taskStatus == HumanVideoTaskStatus.MAKE_AUDIO) {
                // 判断程序是否需要 制作音频
                String talkText = task.getTalkText();
                Long voiceId = task.getVoiceId();
                ArticleUserVoice articleUserVoice = articleUserVoiceMapper.selectById(voiceId);
                // 这里比较快。直接一步完成
                if (createType == 1) {
                    VoiceGenerationResponse generateVoice;
                    try {
                        generateVoice = miniMaxVoiceApi.generateVoice(talkText, articleUserVoice.getVoiceId());
                        task.setTaskStatus(HumanVideoTaskStatus.MAKE_AUDIO);
                        articleUserVoiceTaskMapper.updateById(task);
                    } catch (Exception e) {
                        task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        task.setErrorMessage("克隆音频失败：" + e.getMessage());
                        articleUserVoiceTaskMapper.updateById(task);
                        return;
                    }
                    try {
                        String audioUrl = audioUploader.uploadAudio(generateVoice.getData().getAudio(), 0L, task.getTaskName());
                        task.setTaskStatus(HumanVideoTaskStatus.WAIT_VIDEO);
                        task.setAudioUrl(audioUrl);
                        articleUserVoiceTaskMapper.updateById(task);
                    } catch (Exception e) {
                        task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        task.setErrorMessage("音频转码存储obs失败：" + e.getMessage());
                        articleUserVoiceTaskMapper.updateById(task);
                        return;
                    }
                }
            }

            // 请求百度 进行数字人制作
            if (createType == 2 || createType == 3 || task.getTaskStatus() == HumanVideoTaskStatus.WAIT_VIDEO) {
                // 根据要求，提交数据到百度接口。
                Integer templateType = task.getTemplateType();
                ArticleUserVideoTemplate videoTemplate = articleUserVideoTemplateMapper.selectById(task.getTemplateId());
                String audioUrl = getAudioUrl(task);

                if (audioUrl == null) {
                    return;
                }

                // 照片数字人
                if (templateType.equals(1)) {
                    JSONObject baiduR = baiduDigitalHumanAPI.submitVideoTask(videoTemplate.getAvatarUrl(), "VOICE", audioUrl, callBack);
                    String code = baiduR.getString("code");
                    if (!"0".equals(code)) {
                        task.setErrorMessage("视频合成失败："+ baiduR.getJSONObject("message").getString("global"));
                        task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        articleUserVoiceTaskMapper.updateById(task);
                    }else {
                        // 保存任务id
                        String taskId = baiduR.getJSONObject("result").getString("taskId");
                        task.setTaskId(taskId);
                        task.setTaskStatus(HumanVideoTaskStatus.MAKE_VIDEO);
                        task.setMakeVideoTime(LocalDateTime.now());
                        articleUserVoiceTaskMapper.updateById(task);
                        redisTemplate.opsForList().leftPush("baidu_human_video_task_list", task.getId().toString());
                    }
                } else if (templateType.equals(2)) {

                    Map<String, Integer> videoParams = new HashMap<>();
                    videoParams.put("height", videoTemplate.getVideoHeight());
                    videoParams.put("width", videoTemplate.getVideoWidth());
                    JsonObject baidu123Ret;
                    String templateVideoId = videoTemplate.getVideoUrl();
                    WatermarkStatus waterJobStatus = videoTemplate.getWaterJobStatus();
                    if (WatermarkStatus.NO_USE.equals(waterJobStatus)) {
                        // 修改视频形象状态为需要打水印状态。然后加入到任务队列中。
                        videoTemplate.setWaterJobStatus(WatermarkStatus.WAITING);

                        UpdateWrapper<ArticleUserVideoTemplate> update = new UpdateWrapper<>();
                        update.set("water_job_status", WatermarkStatus.WAITING)
                                .eq("water_job_status", WatermarkStatus.NO_USE)
                                .eq("id", videoTemplate.getId());

                        articleUserVideoTemplateMapper.update(null, update);

                        redisTemplate.boundSetOps(ArticleUserVoiceTaskService.redisTaskSETKey).add(task.getId().toString());
                        redisTemplate.opsForList().leftPush(ArticleUserVoiceTaskService.redisTaskListKey, task.getId().toString());
                        return;
                    }
                    // 水印处理处于等待或者进行中时， 视频先不做。
                    if (WatermarkStatus.WAITING.equals(waterJobStatus) || WatermarkStatus.PROCESSING.equals(waterJobStatus)) {
                        redisTemplate.boundSetOps(ArticleUserVoiceTaskService.redisTaskSETKey).add(task.getId().toString());
                        redisTemplate.opsForList().leftPush(ArticleUserVoiceTaskService.redisTaskListKey, task.getId().toString());
                        return;
                    }
                    baidu123Ret = baidu123DigitalHumanAPI.submitVideoTask(templateVideoId, "VOICE", audioUrl, videoParams, callBack);
                    String code = baidu123Ret.get("code").getAsString();
                    if (!"0".equals(code)) {
                        task.setErrorMessage("视频合成失败："+ baidu123Ret.getAsJsonObject("message").get("global").getAsString());
                        task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        articleUserVoiceTaskMapper.updateById(task);
                    } else {
                        // 保存任务id
                        String taskId = baidu123Ret.getAsJsonObject("result").get("taskId").getAsString();
                        task.setTaskId(taskId);
                        task.setTaskStatus(HumanVideoTaskStatus.MAKE_VIDEO);
                        task.setMakeVideoTime(LocalDateTime.now());
                        articleUserVoiceTaskMapper.updateById(task);
                        redisTemplate.opsForList().leftPush("baidu_human_video_task_list", task.getId().toString());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error in submitAudio for task {}: {}", task.getId(), e.getMessage(), e);
            // 设置任务状态为失败
            task.setTaskStatus(HumanVideoTaskStatus.FAIL);
            task.setErrorMessage("Task execution failed: " + e.getMessage());
            articleUserVoiceTaskMapper.updateById(task);
        } finally {
            redisTemplate.delete("baidu_human_video_task_" + task.getId() + ":lock");
            maximumPoolSize.decrementAndGet();
        }
    }


    /**
     * 获取制作数字人使用的 音频
     * @param task
     * @return
     */
    private String getAudioUrl(ArticleUserVoiceTask task) {

        String audioUrl;
        Integer createType = task.getCreateType();
        // 判断用户使用的音频 是什么方式。
        if (createType.equals(2) || createType.equals(1)) {
            audioUrl = task.getAudioUrl();
        } else {
            Podcast podcast = podcastMapper.selectOne(Wraps.<Podcast>lbQ().eq(SuperEntity::getId, task.getPodcastId())
                    .select(SuperEntity::getId, Podcast::getPodcastFinalAudioUrl));
            if (Objects.isNull(podcast)) {
                task.setErrorMessage("播客音频不存在");
                task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                articleUserVoiceTaskMapper.updateById(task);
                return null;
            }
            audioUrl = podcast.getPodcastFinalAudioUrl();
        }
        return audioUrl;
    }

    /**
     * 查询开始制作视频后。 15后还没完成的任务。
     *
     * 根据任务条件。创建火山视频制作任务
     */
    public void checkTimeOut() {

        List<ArticleUserVoiceTask> voiceTasks = articleUserVoiceTaskMapper.selectList(Wraps.<ArticleUserVoiceTask>lbQ()
                .eq(ArticleUserVoiceTask::getOpenVolcengineTake, false)
                .eq(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.MAKE_VIDEO)
                .lt(ArticleUserVoiceTask::getMakeVideoTime, LocalDateTime.now().plusMinutes(-15)));

        if (CollUtil.isEmpty(voiceTasks)) {
            return;
        }

        for (ArticleUserVoiceTask voiceTask : voiceTasks) {
            ArticleUserVoiceTask task = new ArticleUserVoiceTask();
            task.setId(voiceTask.getId());
            task.setOpenVolcengineTake(true);
            articleUserVoiceTaskMapper.updateById(task);

            Integer templateType = voiceTask.getTemplateType();
            ArticleVolcengineVoiceTask volcengineVoiceTask = new ArticleVolcengineVoiceTask();
            String audioUrl = getAudioUrl(voiceTask);
            if (audioUrl == null) {
                volcengineVoiceTask.setVolcengineTaskErrorMessage("音频不存在");
            }
            volcengineVoiceTask.setUserId(voiceTask.getUserId());
            volcengineVoiceTask.setVoiceTaskId(voiceTask.getId());
            volcengineVoiceTask.setTemplateId(voiceTask.getTemplateId());
            volcengineVoiceTask.setAudioUrl(audioUrl);
            volcengineVoiceTask.setTemplateType(templateType);

            if (templateType.equals(1)) {
                // 图片数字人
                // 查询图片数字人是否有火山形象。 有形象直接进入视频制作。 没有形象进入等待制作形象阶段。
                ArticleUserVideoTemplate videoTemplate = articleUserVideoTemplateMapper.selectById(voiceTask.getTemplateId());
                String volcengineImageResult = videoTemplate.getVolcengineImageResult();
                if (StrUtil.isNotBlank(volcengineImageResult)) {
                    volcengineVoiceTask.setTaskStatus(HumanVideoTaskStatus.WAIT_IMAGE);
                } else {
                    volcengineVoiceTask.setTaskStatus(HumanVideoTaskStatus.WAIT_VIDEO);
                }
            } else if (templateType.equals(2)) {
                // 视频数字人
                volcengineVoiceTask.setTaskStatus(HumanVideoTaskStatus.WAIT_VIDEO);
            }
            articleVolcengineVoiceTaskMapper.insert(volcengineVoiceTask);

        }


    }



    public void run() {

        // 启动一个子线程。定时去检查 数据库中 正在制作视频的 任务是否制作完成
        executor.execute(this::checkAndDownloadVideoTask);


        while (true) {
            try {
                if (maximumPoolSize.get() >= 3) {
                    break;
                }
                // 优先检查数据库中已经提交给百度的数字人 任务制作情况
                String taskId = null;
                try {
                    taskId = redisTemplate.opsForList().rightPop(ArticleUserVoiceTaskService.redisTaskListKey);
                } catch (Exception e) {
                    break;
                }
                if (StrUtil.isBlank(taskId)) {
                    break;
                } else {
                    ArticleUserVoiceTask audioTask = articleUserVoiceTaskMapper.selectById(Long.parseLong(taskId));
                    if (audioTask != null) {
                        redisTemplate.boundSetOps(ArticleUserVoiceTaskService.redisTaskSETKey).remove(taskId);
                        maximumPoolSize.incrementAndGet();
                        // 设置音频进入制作中状态
                        if (audioTask.getTaskStatus().equals(HumanVideoTaskStatus.NOT_START)) {
                            audioTask.setTaskStatus(HumanVideoTaskStatus.WAIT_AUDIO);
                            articleUserVoiceTaskMapper.updateById(audioTask);
                        }
                        // 提交任务到子线程，去进行处理。
                        executor.execute(() -> submitAudio(audioTask));
                    }
                }

            } catch (Exception e) {
                log.error("Error in run method: {}", e.getMessage(), e);
            }
        }

    }
}
