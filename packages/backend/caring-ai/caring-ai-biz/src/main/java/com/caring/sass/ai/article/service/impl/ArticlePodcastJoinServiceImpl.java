package com.caring.sass.ai.article.service.impl;


import com.caring.sass.ai.article.dao.ArticlePodcastJoinMapper;
import com.caring.sass.ai.article.service.ArticlePodcastJoinService;
import com.caring.sass.ai.entity.article.ArticlePodcastJoin;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 播客和创作文章关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-18
 */
@Slf4j
@Service

public class ArticlePodcastJoinServiceImpl extends SuperServiceImpl<ArticlePodcastJoinMapper, ArticlePodcastJoin> implements ArticlePodcastJoinService {
}
