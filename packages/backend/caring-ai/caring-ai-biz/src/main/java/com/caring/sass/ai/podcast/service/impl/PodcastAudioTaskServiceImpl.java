package com.caring.sass.ai.podcast.service.impl;



import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.entity.podcast.*;
import com.caring.sass.ai.podcast.dao.PodcastAudioTaskMapper;
import com.caring.sass.ai.podcast.service.PodcastAudioTaskService;
import com.caring.sass.ai.podcast.service.PodcastService;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 播客音频任务
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-12
 */
@Slf4j
@Service

public class PodcastAudioTaskServiceImpl extends SuperServiceImpl<PodcastAudioTaskMapper, PodcastAudioTask> implements PodcastAudioTaskService {

    private static final String FILE_PATH = "/saas/podcast/audio/downloads/";


    @Autowired
    PodcastService podcastService;

    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;

    private final AtomicInteger maximumPoolSize = new AtomicInteger(0);
    NamedThreadFactory threadFactory = new NamedThreadFactory("ai-podcast-audio-", false);
    private ExecutorService executor = new ThreadPoolExecutor(0, 1, 0L,
            TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(10),
            threadFactory);;



    @Override
    public void createAudioTask(Long podcastId, List<PodcastAudioTask> tasks) {
        baseMapper.delete(Wraps.<PodcastAudioTask>lbQ().eq(PodcastAudioTask::getPodcastId, podcastId));
        baseMapper.insertBatchSomeColumn(tasks);
    }


    /**
     * 定时任务 30秒执行一次。
     */
    @Override
    public void startMergeAudioTask() {

        executor.execute(() -> syncStartMergeAudioTask());

    }


    public void syncStartMergeAudioTask() {

        // 上一轮的任务没有执行完毕。 不开启下一轮
        if (maximumPoolSize.get() > 0) {
            log.info("上一轮任务没有执行完毕。 不开启下一轮");
            return;
        }


        List<Podcast> podcastList = podcastService.list(Wraps.<Podcast>lbQ()
                .select(SuperEntity::getId, Podcast::getTaskStatus, Entity::getUpdateTime, Podcast::getPodcastName)
                .eq(Podcast::getTaskStatus, PodcastTaskStatus.CREATE_AUDIO)
                .orderByAsc(Entity::getUpdateTime)
                .last(" limit 0, 20 "));
        List<Podcast> tempPodcastList = new ArrayList<>();

        // 先标记可以合并音频的播客。 以防定时任务调用集群其他服务，避免重复执行。
        for (Podcast podcast : podcastList) {
            // 查询 这个播客的 子任务 是否全部完成。没有等待中的，没有异常的。
            Integer count = baseMapper.selectCount(Wraps.<PodcastAudioTask>lbQ()
                    .eq(PodcastAudioTask::getPodcastId, podcast.getId())
                    .in(PodcastAudioTask::getTaskStatus,
                            PodcastAudioTaskStatus.WAIT_SUBMIT,
                            PodcastAudioTaskStatus.CREATE_AUDIO_ING,
                            PodcastAudioTaskStatus.CREATE_AUDIO_ERROR));
            if (count == 0) {
                // 表示这个任务已经完成，可以合并音频了。
                maximumPoolSize.incrementAndGet();
                podcast.setTaskStatus(PodcastTaskStatus.Merge_AUDIO);
                podcastService.updateById(podcast);
                tempPodcastList.add(podcast);
            }
        }

        for (Podcast podcast : tempPodcastList) {
            try {
                startMergeAudioTask(podcast);
            } finally {
                maximumPoolSize.decrementAndGet();
            }
        }
    }

    /**
     * 音频合并完毕后，清除本地的临时文件
     * @param mp3Files
     */
    private void clearTempFiles(List<String> mp3Files) {
        for (String mp3File : mp3Files) {
            File file = new File(mp3File);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 下载 音频文件。 合并音频
     * @param podcast
     */
    public void startMergeAudioTask(Podcast podcast) {
        List<PodcastAudioTask> tasks = baseMapper.selectList(Wraps.<PodcastAudioTask>lbQ()
                .eq(PodcastAudioTask::getPodcastId, podcast.getId())
                .orderByAsc(PodcastAudioTask::getTaskSort));
        List<String> mp3Files = new ArrayList<>();
        for (PodcastAudioTask task : tasks) {
            String audioUrl = task.getAudioUrl();
            if (audioUrl.contains("oss-cn-beijing.aliyuncs.com")) {
                // 替换成阿里云内网路径。 加速下载操作。
                audioUrl = audioUrl.replace("oss-cn-beijing.aliyuncs.com", "oss-cn-beijing-internal.aliyuncs.com");

                // 下载文件到本地。
                String fileName = "temp_audio_" + Thread.currentThread().getId() + "_" + System.nanoTime() + ".mp3";
                try {
                    String string = aliYunOssFileUpload.downLoadFromUrl(audioUrl, fileName, FILE_PATH);
                    mp3Files.add(string);
                } catch (IOException e) {
                    log.error("mergeMP3Files downLoadFromUrl error {} {}", podcast.getId(), e.getMessage());
                    podcast.setTaskStatus(PodcastTaskStatus.CREATE_ERROR);
                    podcast.setMergeAudioStatus("error");
                    podcast.setMergeAudioInfo(podcast.getMergeAudioInfo() + "mergeMP3Files downLoadFromUrl error ");
                    podcastService.updateById(podcast);

                    clearTempFiles(mp3Files);
                }
            }
        }

        // 如果只有一个音频文件。那么不需要合并。
        if (tasks.size() == 1) {
            podcast.setPodcastFinalAudioUrl(tasks.get(0).getAudioUrl());
            podcast.setTaskStatus(PodcastTaskStatus.TASK_FINISH);
            podcastService.updateById(podcast);
            return;
        }

        String outputFile = "/saas/podcast/audio/downloads/" + podcast.getPodcastName() + podcast.getId() + ".mp3"; // 输出文件名
        podcast.setPodcastAudioTaskNumber(tasks.size());
        try {
            mergeMP3FilesWithFFmpeg(mp3Files, outputFile);
        } catch (IOException e) {
            log.error("mergeMP3Files IOException error {} {}", podcast.getId(), e.getMessage());
            podcast.setTaskStatus(PodcastTaskStatus.CREATE_ERROR);
            podcast.setMergeAudioStatus("error");
            String string = "mergeMP3Files IOException error" + e.getMessage();
            if (string.length() > 10000) {
                podcast.setMergeAudioInfo(string.substring(0, 10000));
            } else {
                podcast.setMergeAudioInfo(string);
            }
            podcastService.updateById(podcast);
            return;
        } catch (Exception e) {
            log.error("mergeMP3Files JavaLayerException error {} {}", podcast.getId(), e.getMessage());
            podcast.setTaskStatus(PodcastTaskStatus.CREATE_ERROR);
            podcast.setMergeAudioStatus("error");
            podcast.setMergeAudioInfo(podcast.getMergeAudioInfo() + "mergeMP3Files JavaLayerException error ");
            podcastService.updateById(podcast);
            return;
        } finally {
            clearTempFiles(mp3Files);
        }

        File file = new File(outputFile);
        if (file.exists()) {
            String fileName = podcast.getPodcastName() + podcast.getId();
            try {
                JSONObject jsonObject = aliYunOssFileUpload.updateFile(fileName, "mp3", file, false);
                String fileUrl = jsonObject.getString("fileUrl");
                podcast.setPodcastFinalAudioUrl(fileUrl);
                podcast.setTaskStatus(PodcastTaskStatus.TASK_FINISH);
                podcastService.updateById(podcast);
                for (PodcastAudioTask task : tasks) {
                    // 从阿里云oss删除资源
                    aliYunOssFileUpload.deleteByUrl(task.getAudioUrl());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                file.delete();
            }
        }

    }


    /**
     * 使用 ffmpeg 合并多个 MP3 文件
     * @param mp3Files 输入 MP3 文件路径列表
     * @param outputFile 输出文件路径
     * @throws IOException 文件操作或 ffmpeg 执行失败时抛出
     * @throws InterruptedException 进程被中断时抛出
     */
    public static void mergeMP3FilesWithFFmpeg(List<String> mp3Files, String outputFile)
            throws IOException, InterruptedException {

        // 1. 检查 ffmpeg 是否可用
        if (!isFFmpegAvailable()) {
            throw new IOException("ffmpeg 未安装或不在 PATH 中（请确保 /usr/bin/ffmpeg 存在）");
        }

        // 2. 创建临时文件列表（ffmpeg 需要）
        Path tempListFile = Files.createTempFile("ffmpeg-list-", ".txt");
        try {
            // 写入文件列表（格式：file 'path/to/file.mp3'）
            Files.write(tempListFile,
                    mp3Files.stream()
                            .map(file -> "file '" + file + "'")
                            .collect(Collectors.toList())
            );

            // 3. 构建 ffmpeg 命令  重新编码为统一参数
            List<String> command = Arrays.asList(
                    "/usr/bin/ffmpeg",
                    "-f", "concat",
                    "-safe", "0",
                    "-i", tempListFile.toString(),
                    "-c:a", "libmp3lame",
                    "-b:a", "192k",
                    "-ar", "44100",
                    "-ac", "2",
                    "-y",  // 覆盖输出文件（可选）
                    outputFile
            );

            // 4. 执行 ffmpeg
            log.info("执行 ffmpeg 命令: {}", String.join(" ", command));
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); // 合并错误流和标准流
            Process process = pb.start();

            // 可选：打印 ffmpeg 输出（调试用）
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[ffmpeg] " + line);
                }
            }

            // 等待完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("ffmpeg 合并失败，退出码: " + exitCode);
            }

        } finally {
            // 5. 清理临时文件
            Files.deleteIfExists(tempListFile);
        }
    }

    /** 检查 ffmpeg 是否可用 */
    private static boolean isFFmpegAvailable() {
        try {
            Process process = new ProcessBuilder("/usr/bin/ffmpeg", "-version").start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }


}
