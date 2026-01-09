package com.caring.sass.ai.article.task;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.ai.article.config.ArticleUserPayConfig;
import com.caring.sass.ai.article.dao.ArticleUserVoiceMapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceTaskMapper;
import com.caring.sass.ai.article.service.ArticleAccountConsumptionService;
import com.caring.sass.ai.article.service.ArticleUserAccountService;
import com.caring.sass.ai.article.service.ArticleUserConsumptionService;
import com.caring.sass.ai.article.service.ArticleUserService;
import com.caring.sass.ai.dto.article.VoiceCloneStatus;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.podcast.PodcastTaskStatus;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.ai.entity.textual.TextualInterpretationUserConsumption;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import com.caring.sass.ai.textual.config.TextualUserPayConfig;
import com.caring.sass.ai.textual.service.TextualInterpretationUserConsumptionService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.database.mybatis.conditions.Wraps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 检测 有那些任务执行失败了。 给用户进行退能量豆
 */
@Component
public class TaskFailRefundServer {

    @Autowired
    PodcastMapper podcastMapper;

    @Autowired
    ArticleUserVoiceMapper articleUserVoiceMapper;

    @Autowired
    ArticleUserVoiceTaskMapper articleUserVoiceTaskMapper;

    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;

    @Autowired
    ArticleUserAccountService articleUserAccountService;

    @Autowired
    ArticleUserConsumptionService articleUserConsumptionService;

    @Autowired
    TextualInterpretationUserConsumptionService textualInterpretationUserConsumptionService;

    @Autowired
    ArticleAccountConsumptionService articleAccountConsumptionService;

    @Autowired
    ArticleUserPayConfig articleUserPayConfig;

    @Autowired
    TextualUserPayConfig textualUserPayConfig;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    public void refund(){

        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("article_task_fail_refund_lock", "1", 5, TimeUnit.MINUTES);
        if (aBoolean == null || !aBoolean) {
            return;
        }

        try {
            // 检查 播客制作失败。但是没有退费的记录
            refundPodcast("article");
            refundPodcast("textual");

            // 检查 音色克隆失败。但是没有退费的记录
            refundArticleUserVoice();

            // 检查数字人制作失败，但是没有退费的记录
            refundHumanVideo();

        } finally {
            redisTemplate.delete("article_task_fail_refund_lock");
        }



    }


    /**
     * 数字人制作失败的退费
     */
    private void refundHumanVideo() {


        List<ArticleUserVoiceTask> humanVideos = articleUserVoiceTaskMapper.selectList(Wraps.<ArticleUserVoiceTask>lbQ()
                .eq(ArticleUserVoiceTask::getFailRefund, false)
                .eq(ArticleUserVoiceTask::getTaskStatus, HumanVideoTaskStatus.FAIL));


        if (humanVideos.isEmpty()) {
            return;
        }
        Map<Long, List<ArticleUserVoiceTask>> listMap = humanVideos.stream().collect(Collectors.groupingBy(ArticleUserVoiceTask::getUserId));

        for (Map.Entry<Long, List<ArticleUserVoiceTask>> entry : listMap.entrySet()) {
            Long userId = entry.getKey();

            ArticleUser articleUser = articleUserService.getById(userId);
            List<ArticleUserVoiceTask> articleUserVoiceTasks = entry.getValue();
            if (articleUser == null) {
                for (ArticleUserVoiceTask articleUserVoiceTask : articleUserVoiceTasks) {
                    articleUserVoiceTask.setFailRefund(true);
                    articleUserVoiceTaskMapper.updateById(articleUserVoiceTask);
                }
                continue;
            }
            String userMobile = articleUser.getUserMobile();
            for (ArticleUserVoiceTask articleUserVoiceTask : articleUserVoiceTasks) {
                articleUserVoiceTask.setFailRefund(true);
                articleUserVoiceTaskMapper.updateById(articleUserVoiceTask);

                articleUserAccountService.addEnergyBeans(userMobile, articleUserPayConfig.getCreateVideoSpend());

                ArticleUserConsumption userConsumption = new ArticleUserConsumption();
                userConsumption.setUserId(userId);
                userConsumption.setConsumptionType(ConsumptionType.VIDEO_CREATION_REFUND);
                userConsumption.setMessageRemark(ConsumptionType.VIDEO_CREATION_REFUND.getDesc());
                userConsumption.setConsumptionAmount(articleUserPayConfig.getCreateVideoSpend());
                articleUserConsumptionService.save(userConsumption);

                ArticleAccountConsumption accountConsumption = new ArticleAccountConsumption();
                accountConsumption.setUserMobile(userMobile);
                accountConsumption.setSourceType(ArticleAccountConsumption.sourceTypeArticle);
                accountConsumption.setConsumptionId(userConsumption.getId());
                articleAccountConsumptionService.save(accountConsumption);
            }
        }
    }



    /**
     * 音色克隆失败的退费
     */
    private void refundArticleUserVoice(){

        List<ArticleUserVoice> articleUserVoices = articleUserVoiceMapper.selectList(Wraps.<ArticleUserVoice>lbQ()
                .eq(ArticleUserVoice::getFailRefund, false)
                .eq(ArticleUserVoice::getCloneStatus, VoiceCloneStatus.FAILED)
                .eq(ArticleUserVoice::getHasClone, false));

        if (articleUserVoices.isEmpty()) {
            return;
        }

        Map<String, List<ArticleUserVoice>> listMap = articleUserVoices.stream().collect(Collectors.groupingBy(ArticleUserVoice::getUserMobile));
        for (Map.Entry<String, List<ArticleUserVoice>> entry : listMap.entrySet()) {
            String userMobile = entry.getKey();
            List<ArticleUserVoice> articleUserVoiceList = entry.getValue();
            for (ArticleUserVoice userVoice : articleUserVoiceList) {
                Long userId = userVoice.getUserId();
                Boolean textual = userVoice.getTextual();
                userVoice.setFailRefund(true);
                articleUserVoiceMapper.updateById(userVoice);
                if (!textual) {
                    articleUserAccountService.addEnergyBeans(userMobile, articleUserPayConfig.getSoundCloningSpend());

                    ArticleUserConsumption userConsumption = new ArticleUserConsumption();
                    userConsumption.setUserId(userId);
                    userConsumption.setConsumptionType(ConsumptionType.CUSTOM_VOICE_CREATION_REFUND);
                    userConsumption.setMessageRemark(ConsumptionType.CUSTOM_VOICE_CREATION_REFUND.getDesc());
                    userConsumption.setConsumptionAmount(articleUserPayConfig.getSoundCloningSpend());
                    articleUserConsumptionService.save(userConsumption);

                    ArticleAccountConsumption accountConsumption = new ArticleAccountConsumption();
                    accountConsumption.setUserMobile(userMobile);
                    accountConsumption.setSourceType(ArticleAccountConsumption.sourceTypeArticle);
                    accountConsumption.setConsumptionId(userConsumption.getId());
                    articleAccountConsumptionService.save(accountConsumption);

                } else {
                    articleUserAccountService.addEnergyBeans(userMobile, textualUserPayConfig.getSoundCloningSpend());

                    TextualInterpretationUserConsumption userConsumption = new TextualInterpretationUserConsumption();
                    userConsumption.setUserId(userId);
                    userConsumption.setConsumptionType(TextualConsumptionType.CUSTOM_VOICE_CREATION_REFUND);
                    userConsumption.setMessageRemark(TextualConsumptionType.CUSTOM_VOICE_CREATION_REFUND.getDesc());
                    userConsumption.setConsumptionAmount(textualUserPayConfig.getSoundCloningSpend());
                    textualInterpretationUserConsumptionService.save(userConsumption);

                    ArticleAccountConsumption accountConsumption = new ArticleAccountConsumption();
                    accountConsumption.setUserMobile(userMobile);
                    accountConsumption.setSourceType(ArticleAccountConsumption.sourceTypeTextual);
                    accountConsumption.setConsumptionId(userConsumption.getId());
                    articleAccountConsumptionService.save(accountConsumption);

                }


            }


        }

    }



    private void updatePodcastFailRefund(List<Podcast> podcasts){

        for (Podcast podcast : podcasts) {
            podcast.setFailRefund(true);
            podcastMapper.updateById(podcast);
        }
    }

    /**
     * 给科普创作和文献解读 错误的任务，退费
     * @param article
     */
    private void refundPodcast(String article){

        // 检查 播客制作失败。但是没有退费的记录
        List<Podcast> podcastList = podcastMapper.selectList(Wraps.<Podcast>lbQ()
                .eq(Podcast::getPodcastType, article)
                .in(Podcast::getTaskStatus, PodcastTaskStatus.CREATE_ERROR, PodcastTaskStatus.CREATE_DIALOGUE_ERROR)
                .eq(Podcast::getFailRefund, false));

        if (CollUtil.isEmpty(podcastList)) {
            return;
        }

        Map<Long, List<Podcast>> longListMap = podcastList.stream().collect(Collectors.groupingBy(Podcast::getUserId));

        for (Map.Entry<Long, List<Podcast>> entry : longListMap.entrySet()) {
            Long userId = entry.getKey();
            List<Podcast> podcasts = entry.getValue();
            String userMobile = null;

            // 给科普创作用户退费
            if (article.equals("article")) {
                ArticleUser articleUser = articleUserService.getById(userId);
                if (articleUser == null) {
                    updatePodcastFailRefund(podcasts);
                    continue;
                }
                userMobile = articleUser.getUserMobile();
            } else if (article.equals("textual")) {
             // 给文献解读用户退费
                TextualInterpretationUser textualInterpretationUser = textualInterpretationUserService.getById(userId);
                if (textualInterpretationUser == null) {
                    updatePodcastFailRefund(podcasts);
                    continue;
                }
                userMobile = textualInterpretationUser.getUserMobile();
            }
            if (userMobile == null) {
                continue;
            }
            for (Podcast podcast : podcasts) {

                podcast.setFailRefund(true);
                podcastMapper.updateById(podcast);


                // 给用户退费
                if (article.equals("article")) {

                    articleUserAccountService.addEnergyBeans(userMobile, articleUserPayConfig.getCreatePodcastSpend());

                    ArticleUserConsumption userConsumption = new ArticleUserConsumption();
                    userConsumption.setUserId(userId);
                    userConsumption.setConsumptionType(ConsumptionType.PODCAST_REFUND);
                    userConsumption.setMessageRemark(ConsumptionType.PODCAST_REFUND.getDesc());
                    userConsumption.setConsumptionAmount(articleUserPayConfig.getCreatePodcastSpend());
                    articleUserConsumptionService.save(userConsumption);

                    ArticleAccountConsumption accountConsumption = new ArticleAccountConsumption();
                    accountConsumption.setUserMobile(userMobile);
                    accountConsumption.setSourceType(ArticleAccountConsumption.sourceTypeArticle);
                    accountConsumption.setConsumptionId(userConsumption.getId());
                    articleAccountConsumptionService.save(accountConsumption);

                } else {
                    articleUserAccountService.addEnergyBeans(userMobile, textualUserPayConfig.getCreatePodcastSpend());

                    TextualInterpretationUserConsumption userConsumption = new TextualInterpretationUserConsumption();
                    userConsumption.setUserId(userId);
                    userConsumption.setConsumptionType(TextualConsumptionType.PODCAST_REFUND);
                    userConsumption.setMessageRemark(TextualConsumptionType.PODCAST_REFUND.getDesc());
                    userConsumption.setConsumptionAmount(textualUserPayConfig.getCreatePodcastSpend());
                    textualInterpretationUserConsumptionService.save(userConsumption);

                    ArticleAccountConsumption accountConsumption = new ArticleAccountConsumption();
                    accountConsumption.setUserMobile(userMobile);
                    accountConsumption.setSourceType(ArticleAccountConsumption.sourceTypeTextual);
                    accountConsumption.setConsumptionId(userConsumption.getId());
                    articleAccountConsumptionService.save(accountConsumption);
                }

            }

        }
    }


}
