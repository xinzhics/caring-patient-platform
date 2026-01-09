package com.caring.sass.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.article.dao.ArticleUserVoiceTaskMapper;
import com.caring.sass.ai.entity.article.ArticleUserVoiceTask;
import com.caring.sass.ai.entity.humanVideo.BusinessDigitalHumanVideoTask;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.utils.*;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ArticleVideoMergeCoverService {

    static NamedThreadFactory threadFactory = new NamedThreadFactory("saas-article-video-merge-cover-task-", false);
    private static ExecutorService executor = new ThreadPoolExecutor(1, 2, 0L,
            TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(10), threadFactory);


    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;

    @Autowired
    ArticleUserVoiceTaskMapper articleUserVoiceTaskMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    public String downLoadFromFile(String u, String fileName, String dir) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(3000);
            conn.setReadTimeout(600000); // 读取超时时间，这里设置为10分钟
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            inputStream = conn.getInputStream();

            File saveDir = new File(dir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String fileType = u.substring(u.lastIndexOf(".") + 1);
            if (fileType.equals("null")) {
                fileType = "mp4";
            }
            String path = new StringBuilder().append(dir).append(fileName).append(".").append(fileType).toString();
            File file = new File(path);
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096]; // 使用4KB的缓冲区
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("download file success. file type: " + fileType);

            return path;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }



    public void syncArticleVideoMergeCoverTask() {
        executor.execute(() -> articleVideoMergeCoverTask());
    }



    /**
     * 查询数据库中。 已经结束的 科普视频。
     *
     * 使用阿里云的内网下载视频到本地。
     *
     * 制作视频的封免费。 制作视频的封底。
     *
     * 合成新视频。
     */
    public void articleVideoMergeCoverTask() {

        // 查询数据库中等待合成封面的任务。
        String redisLock = "article:video_merge_cover_task:lock";
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(redisLock, "1", 30, TimeUnit.MINUTES);
        if (ifAbsent !=null && !ifAbsent) {
            return;
        }
        List<ArticleUserVoiceTask> voiceTasks = articleUserVoiceTaskMapper.selectList(Wraps.<ArticleUserVoiceTask>lbQ()
                .orderByAsc(SuperEntity::getCreateTime)
                .eq(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.SUCCESS)
                .last(" limit 10 ")
                .eq(ArticleUserVoiceTask::getWaitMergeCover, true));

        if (CollUtil.isEmpty(voiceTasks)) {
            redisTemplate.delete(redisLock);
            return;
        }
        String dir = System.getProperty("java.io.tmpdir");
        String path = "/saas/article/merge/video";
        File folder = new File(dir + path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            for (ArticleUserVoiceTask task : voiceTasks) {
                log.error("articleVideoMergeCoverTask start task: {}, taskName: {}", task.getId(), task.getTaskName());
                // 进行 封面 封底制作。
                String coverImage = null;
                String coverVideoPath = dir + path + File.separator + UUID.randomUUID() + "_cover_video.mp4";
                String backVideo = null;
                String mergedVideo = null;
                boolean clearFile = true;

                String generateAudioUrl = task.getGenerateAudioUrl();
                // 下载阿里云的视频到本地
                if (generateAudioUrl.contains("oss-cn-beijing.aliyuncs.com")) {
                    // 把阿里云的公网链接。换成内网链接
                    generateAudioUrl = generateAudioUrl.replace("oss-cn-beijing.aliyuncs.com","oss-cn-beijing-internal.aliyuncs.com");
                }
                String videoPath = downLoadFromFile(generateAudioUrl, UUID.randomUUID().toString(), dir + path);
                if (videoPath == null) {
                    log.error("articleVideoMergeCoverTask download video error");
                    continue;
                }

                VideoAudioParams audioParams = VideoMergeUtil.getVideoAudioParams(videoPath);
                double fps ;
                if (audioParams == null) {
                    fps = 25.0;
                } else {
                    fps = audioParams.getFps();
                }
                File file = new File(videoPath);

                try {
                    // 制作封面视频
                    if (task.getHumanVideoCover() != null) {
                        try {
                            coverImage = ImageDrawUtils.createCoverImage(task.getHumanVideoCover(), task.getTaskName());
                        } catch (IOException e) {
                            log.error("Error creating cover image: {}", e.getMessage(), e);
                        }
                        if (coverImage != null) {
                            try {
                                ImageFrameVideoUtils.createShortFrameVideo(coverImage, coverVideoPath, fps);
                                // 把视频上传阿里云
                                JSONObject jsonObject = aliYunOssFileUpload.updateFile(UUID.randomUUID().toString(), "mp4", new File(coverVideoPath), false);
                                task.setCoverVideo(jsonObject.getString("fileUrl"));
                                articleUserVoiceTaskMapper.updateById(task);
                            } catch (Exception e) {
                                log.error("Error creating cover video: {}", e.getMessage(), e);
                            }
                        }
                    }

                    // 获取当前视频的宽高
                    int width;
                    int height;
                    // 获取当前视频的宽高
                    if (StrUtil.isNotBlank(coverImage) && coverImage != null) {
                        BufferedImage read = ImageIO.read(new File(coverImage));
                        width = read.getWidth();
                        height = read.getHeight();
                    } else {
                        throw new BizException("封面不存在");
                    }

                    backVideo = VideoOverlay.createBackVideo(width, height, task.getId(), fps);
                    if (backVideo != null && StrUtil.isNotBlank(backVideo)) {
                        JSONObject jsonObject = aliYunOssFileUpload.updateFile(UUID.randomUUID().toString(), "mp4", new File(backVideo), false);
                        task.setBackCoverVideo(jsonObject.getString("fileUrl"));
                    }

                    // 视频合成。将封面。原视频。封底进行合成。
                    mergedVideo = VideoOverlay.mergeVideo(coverVideoPath, videoPath, backVideo, task.getId());
                    if (mergedVideo != null && StrUtil.isNotBlank(mergedVideo)) {
                        JSONObject jsonObject = aliYunOssFileUpload.updateFile(UUID.randomUUID().toString(), "mp4", new File(mergedVideo), true);
                        Object fileUrl = jsonObject.get("fileUrl");
                        Object presignedUrl = jsonObject.get("presignedUrl");
                        task.setFinalVideoResult(fileUrl.toString());
                        if (Objects.nonNull(presignedUrl)) {
                            task.setFinalVideoResultCover(presignedUrl.toString());
                        }
                    }
                } catch (Exception e) {
                    clearFile = false;
                    log.error("articleVideoMergeCoverTask error", e);
                } finally {
                    if (file.exists()) {
                        file.delete();
                    }
                    if (!clearFile) {
                        return;
                    }
                    // 处理失败的。先不删除素材。用于查找原因
                    if (mergedVideo != null) {
                        if (new File(coverImage).exists()) {
                            new File(coverImage).delete();
                        }
                        if (new File(coverVideoPath).exists()) {
                            new File(coverVideoPath).delete();
                        }
                        if (backVideo != null && new File(backVideo).exists()) {
                            new File(backVideo).delete();
                        }
                        if (new File(mergedVideo).exists()) {
                            new File(mergedVideo).delete();
                        }
                    }

                    ArticleUserVoiceTask voiceTask = new ArticleUserVoiceTask();
                    voiceTask.setId(task.getId());
                    voiceTask.setWaitMergeCover(false);
                    articleUserVoiceTaskMapper.updateById(voiceTask);
                }

                String finalVideoResult = task.getFinalVideoResult();
                if (finalVideoResult != null) {
                    ArticleUserVoiceTask voiceTask = new ArticleUserVoiceTask();
                    voiceTask.setId(task.getId());
                    voiceTask.setHumanVideoCover(task.getHumanVideoCover());
                    voiceTask.setCoverVideo(task.getCoverVideo());
                    voiceTask.setBackCoverVideo(task.getBackCoverVideo());
                    voiceTask.setFinalVideoResult(task.getFinalVideoResult());
                    voiceTask.setFinalVideoResultCover(task.getFinalVideoResultCover());
                    articleUserVoiceTaskMapper.updateById(voiceTask);
                }

            }

        } catch (Exception e) {
            log.error("articleVideoMergeCoverTask error {}", e.getMessage());
        } finally {
            redisTemplate.delete(redisLock);
        }


    }





}
