package com.caring.sass.ai.controller.article;


import com.caring.sass.ai.article.service.ArticleTitleService;
import com.caring.sass.ai.dto.ArticleTitleSaveDTO;
import com.caring.sass.ai.entity.article.ArticleTitle;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * Ai创作文章大纲
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleTitle")
@Api(value = "ArticleTitle", tags = "创作创作文章标题")
public class ArticleTitleController {

    @Autowired
    ArticleTitleService baseService;

    @ApiOperation("用户自己修改标题")
    @PostMapping("/updateTitle")
    public R<String> updateTitle(@RequestBody @Validated ArticleTitleSaveDTO articleTitleSaveDTO) {

        ArticleTitle articleTitle = baseService.getOne(Wraps.<ArticleTitle>lbQ().eq(ArticleTitle::getTaskId, articleTitleSaveDTO.getTaskId()));
        articleTitle.setArticleTitle(articleTitleSaveDTO.getArticleTitle());
        baseService.updateById(articleTitle);
        return R.success("success");

    }

}
