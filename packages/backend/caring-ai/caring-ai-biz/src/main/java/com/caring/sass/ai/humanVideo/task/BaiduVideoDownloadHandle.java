package com.caring.sass.ai.humanVideo.task;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.article.dao.ArticleUserMapper;
import com.caring.sass.ai.article.dao.ArticleUserNoticeMapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceTaskMapper;
import com.caring.sass.ai.dto.humanVideo.BaiduVideoDTO;
import com.caring.sass.ai.entity.article.ArticleNoticeType;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.article.ArticleUserNotice;
import com.caring.sass.ai.entity.article.ArticleUserVoiceTask;
import com.caring.sass.ai.entity.humanVideo.BusinessDigitalHumanVideoTask;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.humanVideo.dao.BusinessDigitalHumanVideoTaskMapper;
import com.caring.sass.ai.service.HumanVideoDownloadUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 单线程下载百度做好的 数字人视频。
 * 并内网上传到阿里云
 */
@Slf4j
@Component
public class BaiduVideoDownloadHandle {

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    @Autowired
    HumanVideoDownloadUtil humanVideoDownloadUtil;

    @Autowired
    ArticleUserMapper articleUserMapper;

    @Autowired
    BusinessDigitalHumanVideoTaskMapper businessDigitalHumanVideoTaskMapper;

    @Autowired
    ArticleUserVoiceTaskMapper articleUserVoiceTaskMapper;

    @Autowired
    ArticleUserNoticeMapper articleUserNoticeMapper;

    private static final String REDIS_KEY = "video_download_list_handle_if_absent";

    public void downloadVideoTaskHandle() {
        try {
            while (true) {
                try {
                    log.error("BaiduVideoDownloadHandle downloadVideoTask");
                    String string = redisTemplate.opsForList().rightPop("video_download_list_handle");
                    if (StrUtil.isEmpty(string)) {
                        // 缓存队列中没有任务，检查数据库中是否存在任务
                        for (BusinessDigitalHumanVideoTask videoTask : businessDigitalHumanVideoTaskMapper.selectList(Wraps.<BusinessDigitalHumanVideoTask>lbQ()
                                .eq(BusinessDigitalHumanVideoTask::getTaskStatus, HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO))) {
                            BaiduVideoDTO baiduVideoDTO = new BaiduVideoDTO();
                            baiduVideoDTO.setBusinessClassName(BusinessDigitalHumanVideoTask.class.getSimpleName());
                            baiduVideoDTO.setBusinessId(videoTask.getId().toString());
                            redisTemplate.opsForList().leftPush("video_download_list_handle", baiduVideoDTO.toJSONString());
                        }


                        for (ArticleUserVoiceTask videoTask : articleUserVoiceTaskMapper.selectList(Wraps.<ArticleUserVoiceTask>lbQ()
                                .eq(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO))) {
                            BaiduVideoDTO baiduVideoDTO = new BaiduVideoDTO();
                            baiduVideoDTO.setBusinessClassName(ArticleUserVoiceTask.class.getSimpleName());
                            baiduVideoDTO.setBusinessId(videoTask.getId().toString());
                            redisTemplate.opsForList().leftPush("video_download_list_handle", baiduVideoDTO.toJSONString());
                        }
                        redisTemplate.delete(REDIS_KEY);
                        break;
                    }
                    BaiduVideoDTO baiduVideoDTO = BaiduVideoDTO.fromJSONString(string);
                    String businessClassName = baiduVideoDTO.getBusinessClassName();

                    if (businessClassName.equals(BusinessDigitalHumanVideoTask.class.getSimpleName())) {
                        BusinessDigitalHumanVideoTask videoTask = businessDigitalHumanVideoTaskMapper.selectById(Long.parseLong(baiduVideoDTO.getBusinessId()));
                        if (!videoTask.getTaskStatus().equals(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO)) {
                            continue;
                        }
                        log.error("BaiduVideoDownloadHandle downloadVideoTask: {}", videoTask.getBaiduVideoUrl());
                        humanVideoDownloadUtil.downloadVideo(videoTask.getBaiduVideoUrl(), videoTask);
                        businessDigitalHumanVideoTaskMapper.updateById(videoTask);

                    } else if (businessClassName.equals(ArticleUserVoiceTask.class.getSimpleName())) {

                        ArticleUserVoiceTask voiceTask = articleUserVoiceTaskMapper.selectById(Long.parseLong(baiduVideoDTO.getBusinessId()));
                        if (!voiceTask.getTaskStatus().equals(HumanVideoTaskStatus.WAIT_DOWNLOAD_VIDEO)) {
                            continue;
                        }
                        log.error("BaiduVideoDownloadHandle downloadVideoTask: {}", voiceTask.getGenerateAudioUrl());
                        humanVideoDownloadUtil.downloadVideo(voiceTask.getGenerateAudioUrl(), voiceTask);
                        articleUserVoiceTaskMapper.updateById(voiceTask);

                        ArticleUser articleUser = articleUserMapper.selectById(voiceTask.getUserId());
                        String userName = articleUser.getUserName();
                        String noticeContent = "您的数字人视频已生成，请前往作品集查看。";
                        if (StrUtil.isNotBlank(userName)) {
                            noticeContent = userName + "，" + noticeContent ;
                        }
                        ArticleUserNotice userNotice = new ArticleUserNotice();
                        userNotice.setNoticeContent(noticeContent);
                        userNotice.setNoticeType(ArticleNoticeType.produceTaskComplete);
                        userNotice.setUserId(voiceTask.getUserId());
                        userNotice.setReadStatus(0);
                        articleUserNoticeMapper.insert(userNotice);
                    }
                } catch (Exception e) {
                    log.error("BaiduVideoDownloadHandle downloadVideoTask error", e);
                    break;
                }
            }
        } finally {
            redisTemplate.delete(REDIS_KEY);
        }

    }

    /**
     * 检查视频任务。
     * 下载视频
     */
    public void downloadVideoTask() {
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(REDIS_KEY, "1", 10, TimeUnit.MINUTES);
        if (aBoolean != null && !aBoolean) {
            return;
        }
        downloadVideoTaskHandle();
    }
}
