package com.caring.sass.ai.article.service.impl;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.article.dao.ArticleUserVideoTemplateMapper;
import com.caring.sass.ai.article.service.*;
import com.caring.sass.ai.article.task.ArticleVideoTemplateWaterTask;
import com.caring.sass.ai.dto.article.ArticleVideoDTO;
import com.caring.sass.ai.dto.article.VoiceCloneStatus;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.humanVideo.task.BaiduDigitalHumanAPI;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 视频底板
 * </p>
 *
 * @author leizhi
 * @date 2025-02-26
 */
@Slf4j
@Service

public class ArticleUserVideoTemplateServiceImpl extends SuperServiceImpl<ArticleUserVideoTemplateMapper, ArticleUserVideoTemplate> implements ArticleUserVideoTemplateService {


    @Autowired
    BaiduDigitalHumanAPI digitalHumanAPI;

    @Autowired
    ArticleUserVoiceService articleUserVoiceService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;

    @Autowired
    ArticleEventLogService articleEventLogService;

    private static final String AUDIO_PATH = "/saas/article/audio";

    // 在类中添加信号量，限制最多3个并发执行
    private static final Semaphore videoProcessSemaphore = new Semaphore(10);

    @Override
    public void obtainAudioFromTheVideo(ArticleVideoDTO articleVideoDTO) {
        try {
            // 尝试获取许可，超时时间为30秒
            if (!videoProcessSemaphore.tryAcquire(30, TimeUnit.SECONDS)) {
                log.warn("获取视频处理许可超时，请求被拒绝");
                throw new BizException("排队的人太多了，请稍后再试");
            }

            // 执行原有逻辑
            doObtainAudioFromTheVideo(articleVideoDTO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException("请求被中断");
        } finally {
            // 释放许可
            videoProcessSemaphore.release();
        }
    }


    /**
     * 下载视频。提示视频的 分辨率和 音频
     * @param articleVideoDTO
     */
    private void doObtainAudioFromTheVideo(ArticleVideoDTO articleVideoDTO) {

        String videoUrl = articleVideoDTO.getVideoUrl();
        if (StrUtil.isBlank(videoUrl)) {
            return;
        }
        // 检查视频路径是否是 阿里云oss路径。如果是阿里云oss路径，将路径转换成内网路径。进行下载
        if (videoUrl.contains("oss-cn-beijing.aliyuncs.com")) {
            videoUrl = videoUrl.replace("oss-cn-beijing.aliyuncs.com", "oss-cn-beijing-internal.aliyuncs.com");
        }
        String dir = System.getProperty("java.io.tmpdir");
        // 输出音频文件路径
        UUID fastUUID = UUID.fastUUID();
        String fileName = fastUUID.toString().replace("-", "");
        String path = dir + AUDIO_PATH;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File target = new File(dir + AUDIO_PATH + File.separator + fileName +".mp3");

        try {
            // 使用FFmpeg命令行获取视频分辨率信息
            ProcessBuilder resolutionPb = new ProcessBuilder("/usr/bin/ffprobe", "-v", "error", "-select_streams", "v:0",
                    "-show_entries", "stream=width,height", "-of", "csv=p=0", videoUrl);
            Process resolutionProcess = resolutionPb.start();

            // 异步读取错误流，避免阻塞
            StringBuilder errorOutput = new StringBuilder();
            Thread errorThread = new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(resolutionProcess.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorOutput.append(errorLine).append("\n");
                    }
                } catch (IOException e) {
                    log.error("读取ffprobe错误流异常", e);
                }
            });
            errorThread.start();

            BufferedReader resolutionReader = new BufferedReader(new InputStreamReader(resolutionProcess.getInputStream()));
            String resolutionLine = resolutionReader.readLine();
            if (resolutionLine != null) {
                String[] dimensions = resolutionLine.trim().split(",");
                if (dimensions.length == 2) {
                    articleVideoDTO.setVideoWidth(Integer.parseInt(dimensions[0]));
                    articleVideoDTO.setVideoHeight(Integer.parseInt(dimensions[1]));
                }
            }

            errorThread.join(); // 等待错误流读取完成

            int resolutionExitCode = resolutionProcess.waitFor();
            if (resolutionExitCode != 0) {
                log.error("获取视频分辨率失败，退出码: {}，错误输出: {}", resolutionExitCode, errorOutput.toString());
            }
        } catch (Exception e) {
            log.error("获取视频分辨率异常", e);
        }

        // 如果不需要音频。那么可以直接返回了
        if (!articleVideoDTO.isNeedAudio()) {
            return;
        }

        // TODO: 提取音色的逻辑。下个版本删除。
        Long userId = BaseContextHandler.getUserId();

        articleEventLogService.save(ArticleEventLog.builder()
                .userId(userId)
                .eventType(ArticleEventLogType.HUMAN_VIDEO_EXTRACT_VOICE)
                .build());

        try {
            // 使用FFmpeg命令行提取音频
            ProcessBuilder audioPb = new ProcessBuilder("/usr/bin/ffmpeg", "-i", videoUrl,
                    "-vn", "-ar", "44100", "-ac", "2", "-ab", "128k", "-f", "mp3", target.getAbsolutePath());
            Process audioProcess = audioPb.start();

            // 异步读取输出流和错误流，避免阻塞
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(audioProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 可以选择不保存或只保存部分输出
                        if (output.length() < 5000) { // 限制保存的输出长度
                            output.append(line).append("\n");
                        }
                    }
                } catch (IOException e) {
                    log.error("读取ffmpeg输出流异常", e);
                }
            });

            Thread errorThread = new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(audioProcess.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        // 可以选择不保存或只保存部分错误输出
                        if (error.length() < 5000) { // 限制保存的错误输出长度
                            error.append(errorLine).append("\n");
                        }
                    }
                } catch (IOException e) {
                    log.error("读取ffmpeg错误流异常", e);
                }
            });

            outputThread.start();
            errorThread.start();

            // 等待音频提取完成
            int audioExitCode = audioProcess.waitFor();
            outputThread.join();
            errorThread.join();

            if (audioExitCode == 0 && target.exists()) {
                JSONObject jsonObject = aliYunOssFileUpload.updateFile(fileName, "mp3", target, false);
                articleVideoDTO.setAudioUrl(jsonObject.getString("fileUrl"));
            } else {
                log.error("音频提取失败，退出码: {}，输出: {}，错误: {}", audioExitCode, output.toString(), error.toString());
            }
        } catch (Exception e) {
            log.error("音频提取异常", e);
        } finally {
            try {
                if (target.exists()) {
                    target.delete();
                }
            } catch (Exception e) {
                log.error("删除临时文件异常", e);
            }
        }

    }


    /**
     * 每30秒检测一下任务是否空闲
     */
    @Override
    public void xxjobObtainAudioFromTheVideo() {


        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("xxjobObtainAudioFromTheVideo", "1", 5, TimeUnit.MINUTES);
        if (aBoolean != null && !aBoolean) {
            return;
        }

        // 查询等待提取音色的任务。
        List<ArticleUserVideoTemplate> videoTemplates = baseMapper.selectList(Wraps.<ArticleUserVideoTemplate>lbQ()
                .eq(ArticleUserVideoTemplate::getCloneVoiceStatus, false)
                .eq(ArticleUserVideoTemplate::getCloneVoice, true)
                .last("limit 0, 10"));

        try {
            for (ArticleUserVideoTemplate videoTemplate : videoTemplates) {
                ArticleUserVoice articleUserVoice = articleUserVoiceService.getOne(Wraps.<ArticleUserVoice>lbQ()
                        .eq(ArticleUserVoice::getUserId, videoTemplate.getUserId())
                        .eq(ArticleUserVoice::getVideoTemplateId, videoTemplate.getId()));
                getVideoVoice(videoTemplate, articleUserVoice);
                videoTemplate.setCloneVoiceStatus(true);
                baseMapper.updateById(videoTemplate);
            }
        } finally {
            redisTemplate.delete("xxjobObtainAudioFromTheVideo");
        }


    }


    /**
     * 获取视频的声音。
     */
    private void getVideoVoice(ArticleUserVideoTemplate videoTemplate, ArticleUserVoice articleUserVoice) {

        if (articleUserVoice == null) {
            return;
        }
        if (videoTemplate == null) {
            return;
        }

        String videoUrl = videoTemplate.getVideoUrl();
        // 检查视频路径是否是 阿里云oss路径。如果是阿里云oss路径，将路径转换成内网路径。进行下载
        if (videoUrl.contains("oss-cn-beijing.aliyuncs.com")) {
            videoUrl = videoUrl.replace("oss-cn-beijing.aliyuncs.com", "oss-cn-beijing-internal.aliyuncs.com");
        }
        UUID fastUUID = UUID.fastUUID();
        String fileName = fastUUID.toString().replace("-", "");
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + AUDIO_PATH;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File target = new File(dir + AUDIO_PATH + File.separator + fileName +".mp3");
        try {
            // 使用FFmpeg命令行提取音频
            ProcessBuilder audioPb = new ProcessBuilder("/usr/bin/ffmpeg", "-i", videoUrl,
                    "-vn", "-ar", "44100", "-ac", "2", "-ab", "128k", "-f", "mp3", target.getAbsolutePath());
            Process audioProcess = audioPb.start();

            // 异步读取输出流和错误流，避免阻塞
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(audioProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 可以选择不保存或只保存部分输出
                        if (output.length() < 5000) { // 限制保存的输出长度
                            output.append(line).append("\n");
                        }
                    }
                } catch (IOException e) {
                    log.error("读取ffmpeg输出流异常", e);
                }
            });

            Thread errorThread = new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(audioProcess.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        // 可以选择不保存或只保存部分错误输出
                        if (error.length() < 5000) { // 限制保存的错误输出长度
                            error.append(errorLine).append("\n");
                        }
                    }
                } catch (IOException e) {
                    log.error("读取ffmpeg错误流异常", e);
                }
            });

            outputThread.start();
            errorThread.start();

            // 等待音频提取完成
            int audioExitCode = audioProcess.waitFor();
            outputThread.join();
            errorThread.join();

            if (audioExitCode == 0 && target.exists()) {
                JSONObject jsonObject = aliYunOssFileUpload.updateFile(fileName, "mp3", target, false);
                articleUserVoice.setVoiceUrl(jsonObject.getString("fileUrl"));
                articleUserVoice.setCloneStatus(VoiceCloneStatus.WAITING);
                articleUserVoiceService.updateById(articleUserVoice);
            } else {
                log.error("音频提取失败，退出码: {}，输出: {}，错误: {}", audioExitCode, output.toString(), error.toString());
                articleUserVoice.setCloneStatus(VoiceCloneStatus.FAILED);
                articleUserVoiceService.updateById(articleUserVoice);
            }
        } catch (Exception e) {
            log.error("音频提取异常", e);
            articleUserVoice.setCloneStatus(VoiceCloneStatus.FAILED);
            articleUserVoiceService.updateById(articleUserVoice);
        } finally {
            try {
                if (target.exists()) {
                    target.delete();
                }
            } catch (Exception e) {
                log.error("删除临时文件异常", e);
            }
        }
    }

    @Autowired
    ArticleVideoTemplateWaterTask articleVideoTemplateWaterTask;

    /**
     * 下载视频底板。上传到百度。
     *
     * @param model
     * @return
     */
    @Transactional
    @Override
    protected R<Boolean> handlerSave(ArticleUserVideoTemplate model) {
        Integer type = model.getType();
        if (type == 1) {
            if (StrUtil.isEmpty(model.getAvatarUrl())) {
                throw new BizException("图片地址不能为空");
            }
            JSONObject jsonObject = digitalHumanAPI.verifyImage(model.getAvatarUrl());
            if (!jsonObject.get("code").toString().equals("0")) {
                throw new BizException(jsonObject.getString("message"));
            }
            baseMapper.insert(model);
            // 异步给图片打上水印。
            SaasGlobalThreadPool.execute( () ->  articleVideoTemplateWaterTask.syncWaterMarkTaskPhoto(model));
        } else {

            String videoUrl = model.getVideoUrl();
            if (StringUtils.isEmpty(videoUrl)) {
                log.error("视频 URL 为空，无法进行上传处理");
                return R.fail("视频 URL 为空，无法进行上传处理");
            }
            model.setWaterJobStatus(WatermarkStatus.NO_USE);
            baseMapper.insert(model);
        }

        Boolean cloneVoice = model.getCloneVoice();
        if (cloneVoice != null && cloneVoice) {
            ArticleUserVoice userVoice = new ArticleUserVoice();
            userVoice.setUserId(model.getUserId());
            userVoice.setCloneStatus(VoiceCloneStatus.WAITING_TO_EXTRACT_AUDIO);
            userVoice.setVideoTemplateId(model.getId());
            userVoice.setName(model.getVideoName()+ "的声音");
            userVoice.setTextual(false);
            articleUserVoiceService.save(userVoice);

            articleEventLogService.save(ArticleEventLog.builder()
                    .userId(model.getUserId())
                    .eventType(ArticleEventLogType.HUMAN_VIDEO_EXTRACT_VOICE)
                    .build());
        }

        articleEventLogService.save(ArticleEventLog.builder()
                .userId(model.getUserId())
                .taskId(model.getId())
                .eventType(ArticleEventLogType.HUMAN_VIDEO_TRAINING)
                .build());
        return R.success(true);


    }


    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {
        ArticleUserVideoTemplate videoTemplate = baseMapper.selectById(id);
        videoTemplate.setDeleteStatus(true);
        int i = baseMapper.updateById(videoTemplate);
        return i > 0;
    }

}
