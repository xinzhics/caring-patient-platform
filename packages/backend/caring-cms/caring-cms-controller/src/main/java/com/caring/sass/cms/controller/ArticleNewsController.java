package com.caring.sass.cms.controller;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.cms.dto.ArticleNewsPageDto;
import com.caring.sass.cms.dto.ArticleNewsPreviewDto;
import com.caring.sass.cms.dto.ArticleNewsSaveDto;
import com.caring.sass.cms.dto.ArticleNewsUpdateDto;
import com.caring.sass.cms.entity.ArticleNews;
import com.caring.sass.cms.service.ArticleNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangshuai
 *
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleNews")
@Api(value = "ArticleNewsController", tags = "图文素材")
public class ArticleNewsController extends SuperController<ArticleNewsService, Long, ArticleNews, ArticleNewsPageDto, ArticleNewsSaveDto, ArticleNewsUpdateDto> {



    @ApiOperation("预览图文")
    @PostMapping("preview/news")
    public R preview(@RequestBody @Validated ArticleNewsPreviewDto articleNewsPreviewDto) {

        ArticleNews articleNews = new ArticleNews();
        BeanUtils.copyProperties(articleNewsPreviewDto, articleNews);
        R news = baseService.previewArticleNews(articleNews, articleNewsPreviewDto.getWeiXinName());
        return news;

    }

}
