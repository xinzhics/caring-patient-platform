package com.caring.sass.ai.article.task;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mts20140618.models.QueryJobListResponseBody;
import com.aliyun.mts20140618.models.SubmitJobsResponseBody;
import com.caring.sass.ai.article.service.ArticleUserVideoTemplateService;
import com.caring.sass.ai.entity.article.ArticleUserVideoTemplate;
import com.caring.sass.ai.entity.article.WatermarkStatus;
import com.caring.sass.ai.service.AliYunOssFileUpload;
import com.caring.sass.ai.utils.AliWaterMark;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ImageUtils;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 科普创作视频水印任务
 * // 视频模版直接使用阿里云媒体处理。
 * // 图片模版直接使用画布处理。
 *
 */
@Slf4j
@Service
public class ArticleVideoTemplateWaterTask {

    @Autowired
    ArticleUserVideoTemplateService articleUserVideoTemplateService;

    @Autowired
    AliYunOssFileUpload aliYunOssFileUpload;

    @Autowired
    AliWaterMark aliWaterMark;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    static String fontName = "Noto Sans CJK";


    String referer = "https://article.caringsaas.cn/";


    /**
     * 查询没有水印的视频模版。
     */
    public List<ArticleUserVideoTemplate> queryNoWaterTemplate() {

        LbqWrapper<ArticleUserVideoTemplate> wrapper = Wraps.<ArticleUserVideoTemplate>lbQ()
                .orderByAsc(SuperEntity::getCreateTime)
                .eq(ArticleUserVideoTemplate::getType, 2)
                .eq(ArticleUserVideoTemplate::getWaterJobStatus, WatermarkStatus.WAITING)
                .last(" limit 0, 30");
        List<ArticleUserVideoTemplate> templates = articleUserVideoTemplateService.list(wrapper);
        return templates;

    }


    /**
     * 提交水印任务
     * 30秒提交一次。最快15秒提交一次，最多提交100个
     */
    public void submitWaterMarkTask() {

        // 增加一个任务锁。
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("article_video_template_water_task_submit_lock", "1", 1, TimeUnit.MINUTES);
        if (ifAbsent !=null && !ifAbsent) {
            return;
        }
        try {
            List<ArticleUserVideoTemplate> videoTemplates = queryNoWaterTemplate();
            if (videoTemplates.isEmpty()) {
                return;
            }
            for (ArticleUserVideoTemplate videoTemplate : videoTemplates) {

                String videoUrl = videoTemplate.getVideoUrl();

                SubmitJobsResponseBody responseBody = aliWaterMark.submitTextWaterMarkJobs(videoUrl, "AI生成", referer, videoTemplate.getVideoWidth(), videoTemplate.getVideoHeight());
                if (responseBody == null) {
                    videoTemplate.setWaterJobStatus(WatermarkStatus.FAILED);
                } else {
                    String requestId = responseBody.getRequestId();
                    SubmitJobsResponseBody.SubmitJobsResponseBodyJobResultList jobResultList = responseBody.getJobResultList();
                    List<SubmitJobsResponseBody.SubmitJobsResponseBodyJobResultListJobResult> jobResult = jobResultList.getJobResult();
                    if (CollUtil.isNotEmpty(jobResult)) {
                        SubmitJobsResponseBody.SubmitJobsResponseBodyJobResultListJobResult listJobResult = jobResult.get(0);
                        SubmitJobsResponseBody.SubmitJobsResponseBodyJobResultListJobResultJob job = listJobResult.getJob();
                        String jobId = job.getJobId();
                        videoTemplate.setWaterJobId(jobId);
                        videoTemplate.setWaterJobStatus(WatermarkStatus.PROCESSING);
                    } else {
                        videoTemplate.setWaterJobStatus(WatermarkStatus.FAILED);
                    }
                    videoTemplate.setWaterJobResultListJson(JSON.toJSONString(jobResultList));
                    videoTemplate.setWaterJobRequestId(requestId);
                    videoTemplate.setWaterJobStartTime(LocalDateTime.now());
                }
                articleUserVideoTemplateService.updateById(videoTemplate);
            }
        } finally {
            redisTemplate.delete("article_video_template_water_task_submit_lock");
        }


    }


    /**
     * 查询水印任务
     * 每20秒执行一次这个任务
     */
    public void queryWaterMarkTask() {

        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("article_video_template_water_task_query_lock", "1", 1, TimeUnit.MINUTES);
        if (ifAbsent !=null && !ifAbsent) {
            return;
        }

        try {

            LbqWrapper<ArticleUserVideoTemplate> wrapper = Wraps.<ArticleUserVideoTemplate>lbQ()
                    .orderByAsc(ArticleUserVideoTemplate::getWaterJobStartTime)
                    .eq(ArticleUserVideoTemplate::getType, 2)
                    .eq(ArticleUserVideoTemplate::getWaterJobStatus, WatermarkStatus.PROCESSING)
                    .last(" limit 0, 50");
            List<ArticleUserVideoTemplate> templates = articleUserVideoTemplateService.list(wrapper);
            if (CollUtil.isNotEmpty(templates)) {

                List<String> jobsId = templates.stream().map(ArticleUserVideoTemplate::getWaterJobId).collect(Collectors.toList());

                List<List<String>> listList = ListUtils.subList(jobsId, 10);
                for (List<String> strings : listList) {
                    QueryJobListResponseBody responseBody = aliWaterMark.queryJobList(strings);
                    handelQueryJobResult(responseBody, templates);
                }
            }

        } finally {
            redisTemplate.delete("article_video_template_water_task_query_lock");
        }

    }


    public void handelQueryJobResult(QueryJobListResponseBody responseBody, List<ArticleUserVideoTemplate> templates) {

        QueryJobListResponseBody.QueryJobListResponseBodyNonExistJobIds nonExistJobIds = responseBody.getNonExistJobIds();
        if (nonExistJobIds != null) {
            List<String> nonExistJobIdsList = nonExistJobIds.getString();
            if (CollUtil.isNotEmpty(nonExistJobIdsList)) {
                for (String string : nonExistJobIdsList) {
                    // 不存在的任务，更新状态为失败
                    Optional<ArticleUserVideoTemplate> first = templates.stream().filter(t -> t.getWaterJobId().equals(string)).findFirst();
                    first.ifPresent(t -> {
                        t.setWaterJobStatus(WatermarkStatus.FAILED);
                        articleUserVideoTemplateService.updateById(t);
                    });
                }
            }
        }


        QueryJobListResponseBody.QueryJobListResponseBodyJobList jobList = responseBody.getJobList();
        if (jobList == null) {
            return;
        }
        List<QueryJobListResponseBody.QueryJobListResponseBodyJobListJob> jobListJobs = jobList.getJob();

        if (CollUtil.isEmpty(jobListJobs)) {
            return;
        }
        for (QueryJobListResponseBody.QueryJobListResponseBodyJobListJob jobListJob : jobListJobs) {
            String jobId = jobListJob.getJobId();
            // Submitted：作业已提交。
            //Transcoding：转码中。
            //TranscodeSuccess：转码成功。
            //TranscodeFail：转码失败。
            //TranscodeCancelled：转码取消。
            String state = jobListJob.getState();
            if ("TranscodeSuccess".equals(state)) {
                Optional<ArticleUserVideoTemplate> first = templates.stream().filter(t -> t.getWaterJobId().equals(jobId)).findFirst();
                first.ifPresent(t -> {
                    t.setWaterJobQueryResultJson(JSON.toJSONString(jobListJob));
                    t.setWaterJobStatus(WatermarkStatus.SUCCESS);
                    String videoUrl = t.getVideoUrl();
                    t.setFileUrlBack(videoUrl);
                    QueryJobListResponseBody.QueryJobListResponseBodyJobListJobOutput jobOutput = jobListJob.getOutput();
                    QueryJobListResponseBody.QueryJobListResponseBodyJobListJobOutputOutputFile outputFile = jobOutput.getOutputFile();
                    String bucket = outputFile.getBucket();
                    String location = outputFile.getLocation();
                    String object = outputFile.getObject();
                    // 根据 obs 桶，对象名称，地域，拼接出obs文件路径
                    t.setVideoUrl(String.format("https://%s.%s.aliyuncs.com/%s", bucket, location, object));
                    articleUserVideoTemplateService.updateById(t);
                });

            } else if ("TranscodeFail".equals(state)) {
                // 转码失败，更新状态为失败
                Optional<ArticleUserVideoTemplate> first = templates.stream().filter(t -> t.getWaterJobId().equals(jobId)).findFirst();
                first.ifPresent(t -> {
                    t.setWaterJobStatus(WatermarkStatus.FAILED);
                    t.setWaterJobQueryResultJson(JSON.toJSONString(jobListJob));
                    articleUserVideoTemplateService.updateById(t);
                });
            } else if ("TranscodeCancelled".equals(state)) {
                // 转码取消，更新状态为取消
                Optional<ArticleUserVideoTemplate> first = templates.stream().filter(t -> t.getWaterJobId().equals(jobId)).findFirst();
                first.ifPresent(t -> {
                    t.setWaterJobStatus(WatermarkStatus.CANCELLED);
                    t.setWaterJobQueryResultJson(JSON.toJSONString(jobListJob));
                    articleUserVideoTemplateService.updateById(t);
                });
            }


        }


    }


    public static void main(String[] args) {
        ArticleUserVideoTemplate templateWaterTask = new ArticleUserVideoTemplate();
        templateWaterTask.setAvatarUrl("https://caring-saas-video.oss-cn-beijing.aliyuncs.com/video/1766734546596.png");
        ArticleVideoTemplateWaterTask task = new ArticleVideoTemplateWaterTask();
        task.syncWaterMarkTaskPhoto(templateWaterTask);

    }

    /**
     * 下载用户上传的 图片形象
     *
     * 在图片形象的上 添加水印文字。
     *
     * @param model
     */
    public void syncWaterMarkTaskPhoto(ArticleUserVideoTemplate model) {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + "/saas/userAvatar";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        String avatarUrl = model.getAvatarUrl();
        String qrCodeLocalPath;
        try {
            qrCodeLocalPath = FileUtils.downLoadFromUrl(avatarUrl, UUID.randomUUID().toString().replace("-", ""), path);

            // 获取图片的宽高
            File file = new File(qrCodeLocalPath);
            if (!file.exists()) {
                return;
            }
            Rectangle imageRect = ImageUtils.getImageRect(file);
            int width = imageRect.width;
            int height = imageRect.height;
            BufferedImage background = ImageUtils.resizeImage(width, height, ImageIO.read(file));

            Graphics2D g = background.createGraphics();
            Composite originalComposite = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g.setColor(Color.white);
            Font fontUserName = new Font(fontName, Font.PLAIN, 20);
            g.setFont(fontUserName);
            FontMetrics userNameMetrics = g.getFontMetrics(fontUserName);
            int userNameWidth = userNameMetrics.stringWidth("AI生成");
            int nameY = height - 66;  // 之前是36
            int nameX = width - userNameWidth - 60;    // 之前是 10
            g.drawString("AI生成", nameX, nameY);
            g.setComposite(originalComposite);

            String ext = avatarUrl.substring(avatarUrl.lastIndexOf(".") + 1);

            String fileName = UUID.randomUUID().toString().replace("-", "");

            String imgPath = path + File.separator + fileName + "." + ext;
            File image = new File(imgPath);
            ImageIO.write(background, ext, image);

            String outFileName = UUID.randomUUID().toString().replace("-", "");
            String outFile = path + File.separator + outFileName + "." + ext;

            if (ext.equals("png")) {
                Thumbnails.of(imgPath).scale(0.8f).toFile(outFile);
            } else {
                Thumbnails.of(imgPath).scale(1.0f).outputQuality(0.8f).toFile(outFile);
            }
            File outImage = new File(outFile);
            try {
                JSONObject jsonObject = aliYunOssFileUpload.updateFile(fileName, ext, outImage, false);
                String fileUrl = jsonObject.getString("fileUrl");
                model.setFileUrlBack(model.getAvatarUrl());
                model.setAvatarUrl(fileUrl);
                articleUserVideoTemplateService.updateById(model);
            } catch (Exception e) {
                log.error("同步水印图片异常", e);
            }
            file.delete();
            image.delete();
            outImage.delete();
        } catch (IOException e) {
            log.error("同步水印图片异常", e);
        }


    }










}
