package com.caring.sass.ai.article.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.ai.article.dao.ArticleConvergenceMediaMapper;
import com.caring.sass.ai.article.dao.ArticleTaskMapper;
import com.caring.sass.ai.article.dao.ArticleUserMapper;
import com.caring.sass.ai.article.dao.ArticleUserVoiceTaskMapper;
import com.caring.sass.ai.article.service.AiUserService;
import com.caring.sass.ai.dto.know.KnowsUserDataCountModel;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.textual.TextualInterpretationPptTask;
import com.caring.sass.ai.entity.textual.TextualInterpretationTextTask;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.ai.know.dao.KnowledgeDailyCollectionMapper;
import com.caring.sass.ai.know.dao.KnowledgeFileMapper;
import com.caring.sass.ai.know.dao.KnowledgeUserMapper;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import com.caring.sass.ai.textual.dao.TextualInterpretationPptTaskMapper;
import com.caring.sass.ai.textual.dao.TextualInterpretationTextTaskMapper;
import com.caring.sass.ai.textual.dao.TextualInterpretationUserMapper;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AiUserServiceImpl implements AiUserService {


    @Autowired
    KnowledgeUserMapper knowledgeUserMapper;

    @Autowired
    ArticleUserMapper articleUserMapper;

    @Autowired
    TextualInterpretationUserMapper textualInterpretationUserMapper;

    @Autowired
    KnowledgeDailyCollectionMapper dailyCollectionMapper;

    @Autowired
    KnowledgeFileMapper knowledgeFileMapper;

    @Autowired
    PodcastMapper podcastMapper;

    @Autowired
    ArticleTaskMapper articleTaskMapper;

    @Autowired
    ArticleUserVoiceTaskMapper articleUserVoiceTaskMapper;

    @Autowired
    ArticleConvergenceMediaMapper articleConvergenceMediaMapper;

    @Autowired
    TextualInterpretationTextTaskMapper textualInterpretationTextTaskMapper;

    @Autowired
    TextualInterpretationPptTaskMapper textualInterpretationPptTaskMapper;



    @Override
    public KnowsUserDataCountModel countUserAllData(String domain) {
        // 域名查询博主信息
        KnowledgeUser knowledgeUser = knowledgeUserMapper.selectOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .eq(KnowledgeUser::getUserDomain, domain)
                .last(" limit 0,1 "));
        if (knowledgeUser == null) {
            return null;
        }
        String userMobile = knowledgeUser.getUserMobile();
        KnowsUserDataCountModel countModel = new KnowsUserDataCountModel();

        QueryWrapper<KnowledgeFile> queryWrapper = Wrappers.query();
        queryWrapper.select("know_type as knowType", "count(*) as total");

        queryWrapper.eq("file_user_id", knowledgeUser.getId());
        queryWrapper.groupBy("know_type");


        List<Map<String, Object>> mapList = knowledgeFileMapper.selectMaps(queryWrapper);

        countModel.setAcademicMaterials(getCountResult(mapList, KnowledgeType.ACADEMIC_MATERIALS));
        countModel.setPersonalAchievements(getCountResult(mapList, KnowledgeType.PERSONAL_ACHIEVEMENTS));
        countModel.setCaseDatabase(getCountResult(mapList, KnowledgeType.CASE_DATABASE));

        countModel.setDailyCollection(dailyCollectionMapper.selectCount(Wraps.<KnowledgeDailyCollection>lbQ()
                .eq(KnowledgeDailyCollection::getUserId, knowledgeUser.getId())));

        // 查询融媒体数据
        countModel.setArticleConvergenceMedia(articleConvergenceMediaMapper.selectCount(Wraps.<ArticleConvergenceMedia>lbQ()
                .eq(ArticleConvergenceMedia::getUserId, knowledgeUser.getId())));

        if (StrUtil.isEmpty(userMobile)) {
            return null;
        }

        String encrypt = null;
        try {
            encrypt = EncryptionUtil.encrypt(userMobile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 使用手机号 查询用户的科普创作账号
        ArticleUser articleUser = articleUserMapper.selectOne(Wraps.<ArticleUser>lbQ()
                .eq(ArticleUser::getUserMobile, encrypt)
                .last(" limit 0, 1 "));
        if (articleUser != null) {
            // 查询科普创作用户的 文章
            countModel.setArticleText(articleTaskMapper.selectCount(Wraps.<ArticleTask>lbQ()
                    .eq(ArticleTask::getCreateUser, articleUser.getId())));
            //  视频
            countModel.setArticleVideo(articleUserVoiceTaskMapper.selectCount(Wraps.<ArticleUserVoiceTask>lbQ()
                    .eq(ArticleUserVoiceTask::getUserId, articleUser.getId())));
            // 音频
            countModel.setArticlePodcast(podcastMapper.selectCount(Wraps.<Podcast>lbQ()
                    .eq(Podcast::getPodcastType, "article")
                    .eq(Podcast::getUserId, articleUser.getId())));
        }

        TextualInterpretationUser interpretationUser = textualInterpretationUserMapper.selectOne(Wraps.<TextualInterpretationUser>lbQ()
                .eq(TextualInterpretationUser::getUserMobile, encrypt)
                .last(" limit 0,1 "));
        if (interpretationUser != null) {
            // 文件解读
            countModel.setTextualText(textualInterpretationTextTaskMapper.selectCount(Wraps.<TextualInterpretationTextTask>lbQ()
                    .eq(SuperEntity::getCreateUser, interpretationUser.getId())));

            // 音频解读
            countModel.setTextualAudio(podcastMapper.selectCount(Wraps.<Podcast>lbQ()
                    .eq(Podcast::getUserId,interpretationUser.getId())
                    .eq(Podcast::getPodcastType, "textual")));

            // ppt解读
            countModel.setTextualPpt(textualInterpretationPptTaskMapper.selectCount(Wraps.<TextualInterpretationPptTask>lbQ()
                    .eq(SuperEntity::getCreateUser, interpretationUser.getId())));

        }

        return countModel;
    }


    private Integer getCountResult(List<Map<String, Object>> mapList, KnowledgeType valueKey) {
        if (CollectionUtils.isEmpty(mapList)) {
            return 0;
        }
        for (Map<String, Object> stringObjectMap : mapList) {
            if (stringObjectMap == null) {
                continue;
            }
            Object o = stringObjectMap.get("knowType");
            if (Objects.nonNull(o) && o.toString().equals(valueKey.toString())) {
                Object count = stringObjectMap.get("total");
                if (Objects.isNull(count)) {
                    return 0;
                }
                return Integer.parseInt(count.toString());
            }
        }
        return 0;
    }
}
