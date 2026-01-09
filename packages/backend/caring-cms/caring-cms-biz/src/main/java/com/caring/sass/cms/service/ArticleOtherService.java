package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.ArticleOther;

/**
 * @ClassName ArticleNewsService
 * @Description
 * @Author yangShuai
 * @Date 2021/11/17 16:55
 * @Version 1.0
 */
public interface ArticleOtherService extends SuperService<ArticleOther> {



    ArticleOther exitArticleOther(String src);


}
