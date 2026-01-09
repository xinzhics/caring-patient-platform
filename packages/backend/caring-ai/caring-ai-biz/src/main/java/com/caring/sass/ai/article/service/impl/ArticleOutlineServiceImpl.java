package com.caring.sass.ai.article.service.impl;



import com.caring.sass.ai.article.dao.ArticleOutlineMapper;
import com.caring.sass.ai.article.dao.ArticlePodcastJoinMapper;
import com.caring.sass.ai.article.service.ArticleOutlineService;
import com.caring.sass.ai.entity.article.ArticleOutline;
import com.caring.sass.ai.entity.article.ArticlePodcastJoin;
import com.caring.sass.ai.entity.article.TaskType;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * Ai创作文章正文
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
@Slf4j
@Service

public class ArticleOutlineServiceImpl extends SuperServiceImpl<ArticleOutlineMapper, ArticleOutline> implements ArticleOutlineService {

    @Autowired
    ArticlePodcastJoinMapper articlePodcastJoinMapper;

    @Override
    @Transactional
    public boolean updateById(ArticleOutline model) {
        super.updateById(model);

        ArticlePodcastJoin articlePodcastJoin = articlePodcastJoinMapper.selectOne(Wraps.<ArticlePodcastJoin>lbQ()
                .eq(ArticlePodcastJoin::getTaskId, model.getTaskId())
                .eq(ArticlePodcastJoin::getTaskType, TaskType.ARTICLE));
        if (Objects.nonNull(articlePodcastJoin)) {
            articlePodcastJoin.setUpdateTime(LocalDateTime.now());
            articlePodcastJoinMapper.updateById(articlePodcastJoin);
        }
        return true;
    }
}
