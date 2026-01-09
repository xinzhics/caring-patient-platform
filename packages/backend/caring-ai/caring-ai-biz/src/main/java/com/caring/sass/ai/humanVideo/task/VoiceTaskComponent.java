package com.caring.sass.ai.humanVideo.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.article.service.ArticleUserVoiceService;
import com.caring.sass.ai.dto.humanVideo.BaiduVideoDTO;
import com.caring.sass.ai.entity.humanVideo.*;
import com.caring.sass.ai.humanVideo.service.BusinessDigitalHumanVideoTaskService;
import com.caring.sass.ai.humanVideo.service.BusinessUserAudioResultService;
import com.caring.sass.ai.humanVideo.service.BusinessUserAudioTemplateService;
import com.caring.sass.ai.utils.AudioUploader;
import com.caring.sass.ai.utils.MiniMaxVoiceApi;
import com.caring.sass.ai.utils.miniMax.VoiceCloneResponse;
import com.caring.sass.ai.utils.miniMax.VoiceGenerationResponse;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 火山 音色处理任务
 * 训练音色任务
 * 查询音色训练状态任务
 * 语音合成任务
 *
 */
@Slf4j
@Component
public class VoiceTaskComponent {

    @Autowired
    BusinessUserAudioTemplateService businessUserAudioTemplateService;

    @Autowired
    VolcengineVoiceApi volcengineVoiceApi;

    @Autowired
    BusinessDigitalHumanVideoTaskService videoTaskService;

    @Autowired
    BusinessUserAudioResultService businessUserAudioResultService;

    @Autowired
    Baidu123DigitalHumanAPI baidu123DigitalHumanAPI;

    @Autowired
    BaiduDigitalHumanAPI baiduDigitalHumanAPI;

    @Autowired
    AudioUploader audioUploader;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static MiniMaxVoiceApi voiceApi;


    public VoiceTaskComponent() {
        voiceApi = new MiniMaxVoiceApi();
    }


    /**
     * 训练音色
     */
    public void trainingTimbre() {

        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("training_timbre_lock", "1", 5, TimeUnit.MINUTES);
        if (aBoolean == null || !aBoolean) {
            return;
        }

        // 查询等待训练的音频. 每次只拉 10个任务。防止下一次任务开始时，还没有制作完。
        List<BusinessUserAudioTemplate> templates = businessUserAudioTemplateService.list(Wraps.<BusinessUserAudioTemplate>lbQ()
                        .orderByAsc(SuperEntity::getCreateTime)
                        .last(" limit 0, 10 ")
                .eq(BusinessUserAudioTemplate::getTaskStatus, HumanAudioTaskStatus.WAITING));

        if (CollUtil.isEmpty(templates)) {
            redisTemplate.delete("training_timbre_lock");
            return;
        }


        try {
            for (BusinessUserAudioTemplate template : templates) {

                // 先将任务更新为制作中。
                Long voiceId = template.getId();
                template.setTaskStatus(HumanAudioTaskStatus.PROCESSING);
                String cloneVoiceId = "card_" + voiceId;
                template.setTimbreId(cloneVoiceId);
                businessUserAudioTemplateService.updateById(template);


                String fileUrl = template.getFileUrl();
                Long fileId = null;
                try {
                    // todo 后续使用个人
                    fileId = voiceApi.uploadFile(fileUrl, "voice_clone", template.getHAccount());
                } catch (IOException e) {
                    log.error("update file error: {}", e.getMessage());
                    template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                    template.setTaskMessage("上传声音到miniMaxVoice失败");
                }
                VoiceCloneResponse r = null;
                try {
                    r = voiceApi.cloneVoice(fileId, cloneVoiceId, null, template.getHAccount());
                    // 克隆成功
                    if (r.getBaseResp().getStatusCode() == 0) {
                        template.setTimbreId(cloneVoiceId);
                        template.setTaskStatus(HumanAudioTaskStatus.TRAINING_COMPLETED);
                        businessUserAudioTemplateService.updateById(template);
                    } else {
                        String ret = r.getBaseResp().getStatusMsg();
                        template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                        template.setTaskMessage(ret);

                        BusinessDigitalHumanVideoTask videoTask = new BusinessDigitalHumanVideoTask();
                        videoTask.setId(template.getVideoTaskId());
                        videoTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        videoTask.setTaskMessage(ret);
                        videoTaskService.updateById(videoTask);
                    }
                } catch (IOException e) {
                    log.error("cloneVoice error: {}", e.getMessage());
                    template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                    template.setTaskMessage("克隆声音失败");

                    BusinessDigitalHumanVideoTask videoTask = new BusinessDigitalHumanVideoTask();
                    videoTask.setId(template.getVideoTaskId());
                    videoTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                    videoTask.setTaskMessage("克隆声音失败");
                    videoTaskService.updateById(videoTask);
                }
                businessUserAudioTemplateService.updateById(template);
            }
        } finally {
            redisTemplate.delete("training_timbre_lock");
        }


    }


    /**
     *
     * 查询 火山 音色训练状态
     */
    @Deprecated
    public void trainingTimbreStatus() {
        List<BusinessUserAudioTemplate> templates = businessUserAudioTemplateService.list(Wraps.<BusinessUserAudioTemplate>lbQ()
                .eq(BusinessUserAudioTemplate::getTaskStatus, HumanAudioTaskStatus.PROCESSING));

        if (CollUtil.isEmpty(templates)) {
            return;
        }

        for (BusinessUserAudioTemplate template : templates) {

            // 约定，使用 card_ 开头的音色。不使用火山的训练
            if (StrUtil.isNotEmpty(template.getTimbreId())) {
                if (template.getTimbreId().startsWith("card_")) {
                    continue;
                }
            }

            JSONObject jsonObject = volcengineVoiceApi.getStatus(template.getTimbreId());
            if (jsonObject.get("code") == null) {
                JSONObject baseResp = jsonObject.getJSONObject("BaseResp");
                int statusCode = (int) Double.parseDouble(baseResp.get("StatusCode").toString());
                int status = (int) Double.parseDouble(jsonObject.getString("status"));
                if (0 == statusCode) {
                    if (2 == status || 4 == status) {
                        String speakerId = jsonObject.getString("speaker_id");
                        template.setTimbreId(speakerId);
                        template.setTaskStatus(HumanAudioTaskStatus.TRAINING_COMPLETED);
                        businessUserAudioTemplateService.updateById(template);
                    }
                } else {
                    template.setTaskMessage(baseResp.toString());
                    template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                    businessUserAudioTemplateService.updateById(template);
                }
            } else {
                template.setTaskMessage(jsonObject.getString("message"));
                template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                businessUserAudioTemplateService.updateById(template);
            }

        }

    }


    @Autowired
    ArticleUserVoiceService articleUserVoiceService;


    /**
     * 语音合成
     * 查询训练完成的音色
     * 将音色 与 文本 合成 为用户语音
     * 并将任务进入到 视频 语音合成状态
     *
     * 每次最多处理10个任务。 每3分钟执行一次
     */
    public void audioSynthesisTask() {

        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("audio_synthesis_lock", "1", 5, TimeUnit.MINUTES);
        if (aBoolean == null || !aBoolean) {
            return;
        }
        // 查询音色训练完毕的任务
        List<BusinessUserAudioTemplate> templates = businessUserAudioTemplateService.list(Wraps.<BusinessUserAudioTemplate>lbQ()
                .orderByAsc(SuperEntity::getCreateTime)
                .last(" limit 0, 20 ")
                .eq(BusinessUserAudioTemplate::getTaskStatus, HumanAudioTaskStatus.TRAINING_COMPLETED));

        if (CollUtil.isEmpty(templates)) {
            redisTemplate.delete("audio_synthesis_lock");
            return;
        }
        try {
            for (BusinessUserAudioTemplate template : templates) {

                // 音色ID
                String timbreId = template.getTimbreId();

                // 任务ID
                Long videoTaskId = template.getVideoTaskId();
                BusinessDigitalHumanVideoTask videoTask = videoTaskService.getById(videoTaskId);
                if (Objects.isNull(videoTask)) {
                    template.setTaskMessage("数字人任务不存在");
                    template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                    businessUserAudioTemplateService.updateById(template);
                }

                String videoTextContent = videoTask.getVideoTextContent();

                VoiceGenerationResponse generateVoice;
                try {
                    generateVoice = voiceApi.generateVoice(videoTextContent, timbreId);
                } catch (Exception e) {

                    template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                    template.setTaskMessage("合成语音失败");
                    businessUserAudioTemplateService.updateById(template);
                    log.error("generateVoice error: {}", e.getMessage());

                    videoTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                    videoTask.setTaskMessage("合成语音失败");
                    videoTaskService.updateById(videoTask);
                    continue;
                }
                String audioUrl;
                try {
                    audioUrl = audioUploader.uploadAudio(generateVoice.getData().getAudio(), 0L, videoTask.getTaskName() + videoTask.getId());
                } catch (IOException e) {

                    template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                    template.setTaskMessage("上传语音结果失败");
                    businessUserAudioTemplateService.updateById(template);

                    videoTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                    videoTask.setTaskMessage("上传语音结果失败");
                    videoTaskService.updateById(videoTask);
                    continue;
                }
                if (StrUtil.isNotEmpty(audioUrl)) {
                    BusinessUserAudioResult audioResult = new BusinessUserAudioResult();
                    audioResult.setApiStatus("SUCCESS");
                    audioResult.setVideoTaskId(videoTaskId);
                    audioResult.setFileUrl(audioUrl);
                    audioResult.setTimbreId(timbreId);
                    audioResult.setTextContent(videoTextContent);
                    businessUserAudioResultService.save(audioResult);
                    // 更新视频任务状态
                    videoTask.setTaskStatus(HumanVideoTaskStatus.WAIT_VIDEO);
                    videoTaskService.updateById(videoTask);
                    template.setTaskStatus(HumanAudioTaskStatus.SUCCESS);
                    businessUserAudioTemplateService.updateById(template);
                } else {
                    template.setTaskStatus(HumanAudioTaskStatus.TRAINING_FAILED);
                    template.setTaskMessage("未生产语音文件");
                    businessUserAudioTemplateService.updateById(template);

                    videoTask.setTaskStatus(HumanVideoTaskStatus.FAIL);
                    videoTask.setTaskMessage("未生产语音文件");
                    videoTaskService.updateById(videoTask);
                }

            }
        } finally {
            redisTemplate.delete("audio_synthesis_lock");
        }

    }


    /**
     * 查询进入等待制作视频的任务

     * 根据任务要求。调用百度接口，提交 数字人合成任务。
     *
     */
    public void submitVideoTask() {

        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("submit_baidu_video_lock", "1", 5, TimeUnit.MINUTES);
        if (!isLock) {
            return;
        }

        List<BusinessDigitalHumanVideoTask> tasks = videoTaskService.list(Wraps.<BusinessDigitalHumanVideoTask>lbQ()
                .eq(BusinessDigitalHumanVideoTask::getTaskStatus, HumanVideoTaskStatus.WAIT_VIDEO));

        if (tasks.isEmpty()) {
            redisTemplate.delete("submit_baidu_video_lock");
            return;
        }
        try {
            for (BusinessDigitalHumanVideoTask task : tasks) {
                String audioUrl = "";
                if (task.getCreateTimbre() != null && task.getCreateTimbre()) {
                    BusinessUserAudioResult audioResult = businessUserAudioResultService.getOne(Wraps.<BusinessUserAudioResult>lbQ()
                            .eq(BusinessUserAudioResult::getVideoTaskId, task.getId())
                            .eq(BusinessUserAudioResult::getApiStatus, "SUCCESS"));
                    if (Objects.isNull(audioResult)) {
                        task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        videoTaskService.updateById(task);
                        continue;
                    }
                    audioUrl = audioResult.getFileUrl();
                } else {
                    audioUrl = task.getAudioUrl();
                }
                if (StrUtil.isEmpty(audioUrl)) {
                    task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                    task.setTaskMessage("音频文件不存在");
                    videoTaskService.updateById(task);
                    continue;
                }

                HumanVideoMakeWay makeWay = task.getMakeWay();
                if (HumanVideoMakeWay.PHOTO.equals( makeWay)) {

                    photoCreateHumanVideo(task, audioUrl);
                } else if (HumanVideoMakeWay.VIDEO.equals(makeWay)) {
                    videoCreateHumanVideo(task, audioUrl);
                }
            }
        } finally {
            redisTemplate.delete("submit_baidu_video_lock");
        }

    }


    /**
     * 查询数字人的制作状态
     */
    public void queryCreateVideoStatus() {

        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("query_baidu_video_lock", "1", 5, TimeUnit.MINUTES);
        if (aBoolean != null && !aBoolean) {
            return;
        }


        try {
            List<BusinessDigitalHumanVideoTask> tasks = videoTaskService.list(Wraps.<BusinessDigitalHumanVideoTask>lbQ()
                    .eq(BusinessDigitalHumanVideoTask::getTaskStatus, HumanVideoTaskStatus.MAKE_VIDEO));

            if (tasks.isEmpty()) {
                return;
            }
            for (BusinessDigitalHumanVideoTask task : tasks) {
                JsonObject jsonObject;
                if (task.getMakeWay().equals(HumanVideoMakeWay.VIDEO)) {
                    jsonObject = baidu123DigitalHumanAPI.queryTaskStatus(task.getBaiduTaskId());
                } else {
                    jsonObject = baiduDigitalHumanAPI.queryTaskStatus(task.getBaiduTaskId());
                }
                int code = jsonObject.get("code").getAsInt();
                if (code == 0) {
                    JsonObject result = jsonObject.getAsJsonObject("result");
                    String status = result.get("status").getAsString();
                    if (status.equals("SUCCESS")) {
                        String videoUrl = result.get("videoUrl").getAsString();
                        if (videoUrl != null) {
                            // 下载视频之前，在进行一次查询状态。防止百度回调，过来状态已经改变
                            task = videoTaskService.getById(task.getId());
                            if (task.getTaskStatus().equals(HumanVideoTaskStatus.SUCCESS) || task.getTaskStatus().equals(HumanVideoTaskStatus.FAIL)) {
                                continue;
                            }
                            // 标记视频可以下载
                            BaiduVideoDTO videoDTO = new BaiduVideoDTO();
                            videoDTO.setBusinessId(task.getId().toString());
                            videoDTO.setBusinessClassName(BusinessDigitalHumanVideoTask.class.getSimpleName());
                            redisTemplate.opsForList().leftPush("video_download_list_handle", videoDTO.toJSONString());
                            task.setTaskStatus(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO);
                            task.setBaiduVideoUrl(videoUrl);
                            videoTaskService.updateById(task);
                        }
                    } else if (status.equals("FAILED")) {
                        task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                        task.setTaskMessage(jsonObject.toString());
                        videoTaskService.updateById(task);
                    }
                } else {
                    task.setTaskStatus(HumanVideoTaskStatus.FAIL);
                    task.setTaskMessage(jsonObject.toString());
                    videoTaskService.updateById(task);
                }
            }

        } finally {
            redisTemplate.delete("query_baidu_video_lock");
        }


    }

    /**
     * 使用数字人照片 制作数字人
     * @param task
     */
    private void photoCreateHumanVideo(BusinessDigitalHumanVideoTask task, String audioUrl) {

        JSONObject jsonObject = baiduDigitalHumanAPI.callBaidu(task.getPhotoHumanUrl(), audioUrl);
        if (jsonObject.getInteger("code").equals(0)) {
            String taskId = jsonObject.getString("taskId");
            task.setBaiduTaskId(taskId);
            task.setTaskStatus(HumanVideoTaskStatus.MAKE_VIDEO);
            videoTaskService.updateById(task);
        } else {
            task.setTaskStatus(HumanVideoTaskStatus.FAIL);
            task.setTaskMessage(jsonObject.toJSONString());
            videoTaskService.updateById(task);
        }

    }


    /**
     * 提交 使用视频合成 数字人视频
     * @param task
     */
    private void videoCreateHumanVideo(BusinessDigitalHumanVideoTask task, String audioUrl) {

        String templateVideoUrl = task.getTemplateVideoUrl();

        JSONObject jsonObject;
        jsonObject = baidu123DigitalHumanAPI.callBaiduApi(templateVideoUrl, audioUrl, task.getVideoWidth(), task.getVideoHeight());
        if (jsonObject.get("code").equals(0)) {
            String taskId = jsonObject.getString("taskId");
            task.setBaiduTaskId(taskId);
            task.setTaskStatus(HumanVideoTaskStatus.MAKE_VIDEO);
            videoTaskService.updateById(task);
        } else {
            task.setTaskStatus(HumanVideoTaskStatus.FAIL);
            task.setTaskMessage(jsonObject.toJSONString());
            videoTaskService.updateById(task);
        }


    }


}
