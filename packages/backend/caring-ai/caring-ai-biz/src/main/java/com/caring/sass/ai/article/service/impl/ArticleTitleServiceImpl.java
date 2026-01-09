package com.caring.sass.ai.article.service.impl;


import com.caring.sass.ai.article.dao.ArticleTitleMapper;
import com.caring.sass.ai.article.service.ArticleTitleService;
import com.caring.sass.ai.entity.article.ArticleTitle;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * Ai创作文章大纲
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
@Slf4j
@Service

public class ArticleTitleServiceImpl extends SuperServiceImpl<ArticleTitleMapper, ArticleTitle> implements ArticleTitleService {
}
