package com.caring.sass.ai.podcast.task;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceMapper;
import com.caring.sass.ai.config.MiniMaxVoiceConfig;
import com.caring.sass.ai.entity.article.ArticleUserVoice;
import com.caring.sass.ai.entity.podcast.*;
import com.caring.sass.ai.podcast.dao.PodcastAudioTaskMapper;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import com.caring.sass.ai.podcast.dao.PodcastSoundSetMapper;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.ai.utils.AudioUploader;
import com.caring.sass.ai.utils.DoubaoTTSApi;
import com.caring.sass.ai.utils.MiniMaxVoiceApi;
import com.caring.sass.ai.utils.miniMax.VoiceGenerationResponse;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 音频合成线程
 */
@Slf4j
@Component
public class PodcastAudioThread {

    @Autowired
    PodcastSoundSetMapper soundSetMapper;

    @Autowired
    PodcastAudioTaskMapper podcastAudioTaskMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    MiniMaxVoiceApi miniMaxVoiceApi;

    @Autowired
    AudioUploader audioUploader;

    @Autowired
    PodcastMapper podcastMapper;

    @Autowired
    MiniMaxVoiceConfig miniMaxVoiceConfig;

    private final String PODCAST_RUNNING_COUNT = "PODCAST_RUNNING_COUNT:";

    @Autowired
    ArticleUserVoiceMapper articleUserVoiceMapper;

    private final int maxRetries = 3; // 最大重试次数

    ExecutorService executor = new ThreadPoolExecutor(1, 3, 0L,
            TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(50), new NamedThreadFactory("create-audio-task", false));


    private final AtomicInteger maximumPoolSize = new AtomicInteger(0);
    /**
     * 检查任务线程 是否有空闲
     */
    public void checkPoolSize() {
        while (maximumPoolSize.get() >= 3) {
            try {
                log.info("Maximum pool size reached, waiting for 2 seconds...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("Thread interrupted while waiting for pool size: {}", e.getMessage());
            }
        }
    }

    /**
     * 检查当前这一秒接口qps是否有可用的。
     *
     */
    public void checkQps() {
        while (true) {
            LocalDateTime now = LocalDateTime.now();
            now.withNano(0);
            String key = PODCAST_RUNNING_COUNT + now;
            redisTemplate.opsForValue().setIfAbsent(key, (Long.MAX_VALUE - 10) + "", 10, TimeUnit.SECONDS);
            try {
                redisTemplate.opsForValue().increment(key);
                break;
            } catch (Exception e) {
                log.error("No QPS available, waiting for 2 seconds...", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    log.error("Thread interrupted while waiting for QPS: {}", ex.getMessage());
                }
            }
        }

    }

    public void getRunning() {

        // 先判断当前是是否有 可用的线程。
        checkPoolSize();

        // 在判断当前是否有可用的qps
        checkQps();
    }


    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;


    /**
     * 提交音频到火山。并将音频的路径保存到本地。
     *
     * 为应对集群问题，将音频提交到阿里云oss去。
     * @param task
     */
    public void submitAudio(PodcastAudioTask task) {
        try {
            Long soundSetId = task.getSoundSetId();
            PodcastSoundSet soundSet = getSoundSetWithRetry(soundSetId);
            String voiceType = soundSet.getRoleSoundSet();
            String audioText = task.getAudioText();

            PodcastAudioTask selected = podcastAudioTaskMapper.selectById(task.getId());
            if (selected.getTaskStatus().equals(PodcastAudioTaskStatus.CREATE_AUDIO_ERROR)) {
                return;
            }

            // 生成音频
            String tmpAudioPath = null;
            boolean isMiniMaxVoice = voiceType.startsWith("h_");
            if (isMiniMaxVoice) {
                try{
                    VoiceGenerationResponse generateVoice = miniMaxVoiceApi.generateVoice(audioText, voiceType);
                    if (generateVoice.getBaseResp().getStatusCode() == 0) {
                        tmpAudioPath = audioUploader.generateTmpFile(generateVoice.getData().getAudio());
                    }else {
                        log.error("生成音频时发生异常，{}, 声音id：{}", generateVoice.getBaseResp().getStatusMsg(), voiceType);
                        task.setTaskStatus(PodcastAudioTaskStatus.CREATE_AUDIO_ERROR);
                        task.setTaskErrorMessage(generateVoice.getBaseResp().getStatusMsg());
                    }
                }catch (Exception e) {
                    log.error("生成音频时发生异常，正在重试...", e);
                    task.setTaskStatus(PodcastAudioTaskStatus.CREATE_AUDIO_ERROR);
                    task.setTaskErrorMessage(e.getMessage());
                }
            } else {
                // 使用豆包音色生成音频
                DoubaoTTSApi ttsApi = new DoubaoTTSApi();
                String string = UUID.fastUUID().toString();
                tmpAudioPath = generateAudioWithRetry(ttsApi, DoubaoVoiceType.getVoiceType(voiceType), string, audioText);
            }

            String fileUrl = null;
            if (StrUtil.isNotEmpty(tmpAudioPath) && tmpAudioPath != null) {
                File file = new File(tmpAudioPath);
                if (file.exists()) {
                    String fileName = "temp_audio_"+task.getPodcastId()+"_" + Thread.currentThread().getId() + "_" + System.nanoTime();
                    JSONObject updateFile = aliYunOssFileUpload.updateFile(fileName, "mp3", file, false);
                    fileUrl = updateFile.getString("fileUrl");
                    if (StrUtil.isEmpty(fileUrl)) {
                        task.setTaskStatus(PodcastAudioTaskStatus.CREATE_AUDIO_ERROR);
                        task.setTaskErrorMessage("上传音频到阿里云oss失败");
                    }
                    file.delete();
                }
            }

            if (StrUtil.isEmpty(fileUrl)) {
                updateTaskStatusWithRetry(task);

                Podcast podcast = podcastMapper.selectById(task.getPodcastId());
                podcast.setTaskStatus(PodcastTaskStatus.CREATE_ERROR);
                podcast.setMergeAudioInfo("生成音频失败");
                podcastMapper.updateById(podcast);

                // 失败了一个声音。其他的声音任务也不需要做了。
                UpdateWrapper<PodcastAudioTask> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("task_status", PodcastAudioTaskStatus.CREATE_AUDIO_ERROR);
                updateWrapper.eq("podcast_id", task.getPodcastId());
                updateWrapper.ne("task_status", PodcastAudioTaskStatus.DOWNLOAD_AUDIO_SUCCESS);
                podcastAudioTaskMapper.update(new PodcastAudioTask(), updateWrapper);

            } else {
                task.setAudioUrl(fileUrl);
                task.setTaskStatus(PodcastAudioTaskStatus.DOWNLOAD_AUDIO_SUCCESS);
                updateTaskStatusWithRetry(task);
            }

        } catch (Exception e) {
            log.error("提交音频时发生异常，正在重试...", e);
        } finally {
            maximumPoolSize.decrementAndGet();
        }
    }

    private PodcastSoundSet getSoundSetWithRetry(Long soundSetId) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                return soundSetMapper.selectById(soundSetId);
            } catch (Exception e) {
                log.error("获取音效集时发生异常，正在重试... (重试次数: {})", retryCount + 1, e);
                retryCount++;
                try {
                    Thread.sleep(1000); // 等待1秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("线程中断", ie);
                }
            }
        }
        log.error("达到最大重试次数，获取音效集失败");
        throw new RuntimeException("达到最大重试次数，获取音效集失败");
    }

    private String generateAudioWithRetry(DoubaoTTSApi ttsApi, DoubaoVoiceType voiceType, String string, String audioText) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                return ttsApi.generateAudio(voiceType, string, audioText);
            } catch (Exception e) {
                log.error("生成音频时发生连接重置，正在重试... (重试次数: {})", retryCount + 1, e);
                retryCount++;
                try {
                    Thread.sleep(1000); // 等待1秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("线程中断", ie);
                }
            }
        }
        log.error("达到最大重试次数，生成音频失败");
        return null;
    }


    private void updateTaskStatusWithRetry(PodcastAudioTask audioTask) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                podcastAudioTaskMapper.updateById(audioTask);
                return;
            } catch (Exception e) {
                log.error("更新任务状态时发生异常，正在重试... (重试次数: {})", retryCount + 1, e);
                retryCount++;
                try {
                    Thread.sleep(1000); // 等待1秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("线程中断", ie);
                }
            }
        }
        log.error("达到最大重试次数，更新任务状态失败");
    }


    public void run() {
        Integer maxRpm = miniMaxVoiceConfig.getMaxRpm();
        try {
            List<PodcastAudioTask> audioTasks = podcastAudioTaskMapper.selectList(Wraps.<PodcastAudioTask>lbQ()
                    .last(" limit 0," + maxRpm)
                    .orderByAsc(SuperEntity::getCreateTime)
                    .eq(PodcastAudioTask::getTaskStatus, PodcastAudioTaskStatus.WAIT_SUBMIT));
            if (audioTasks.isEmpty()) {
                return;
            }
            for (PodcastAudioTask audioTask : audioTasks) {
                getRunning();
                maximumPoolSize.incrementAndGet();
                // 设置音频进入制作中状态
                audioTask.setTaskStatus(PodcastAudioTaskStatus.CREATE_AUDIO_ING);
                podcastAudioTaskMapper.updateById(audioTask);
                // 提交任务到子线程，去进行处理。
                executor.execute(() -> submitAudio(audioTask));
            }

        } catch (Exception e) {
            log.error("Error in processing task: {}", e.getMessage(), e);
        }
    }
}
