package com.caring.sass.cms.service;

import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.ArticleNews;

/**
 * @ClassName ArticleNewsService
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 16:55
 * @Version 1.0
 */
public interface ArticleNewsService extends SuperService<ArticleNews> {


    /**
     * 预览图文
     * @param articleNews
     * @param weiXinName
     * @return
     */
    R previewArticleNews(ArticleNews articleNews, String weiXinName);

    /**
     * 替换文章内容中的url
     * @param articleNews
     */
    void replaceContentUrl(ArticleNews articleNews);
}
