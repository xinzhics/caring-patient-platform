package com.caring.sass.ai.controller.article;


import com.caring.sass.ai.article.service.ArticleOutlineService;
import com.caring.sass.ai.dto.ArticleOutlineSaveDTO;
import com.caring.sass.ai.entity.article.ArticleOutline;
import com.caring.sass.ai.utils.I18nUtils;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * Ai创作文章正文
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleOutline")
@Api(value = "ArticleOutline", tags = "创作创作文章提纲正文")
public class ArticleOutlineController {

    @Autowired
    ArticleOutlineService baseService;

    @ApiOperation("用户查询提纲正文")
    @GetMapping("getOutLine")
    public R<ArticleOutline> getOutLine(@RequestBody @Validated Long taskId) {

        ArticleOutline serviceOne = baseService.getOne(Wraps.<ArticleOutline>lbQ()
                .eq(ArticleOutline::getTaskId, taskId).last(" limit 0,1 "));
        if (serviceOne == null) {
            return R.fail(I18nUtils.getMessage("parameter_error"));
        }
        return R.success(serviceOne);
    }


    @ApiOperation("用户修改提纲")
    @PostMapping("updateOutLine")
    public R<ArticleOutline> updateOutLine(@RequestBody @Validated ArticleOutlineSaveDTO outlineSaveDTO) {

        ArticleOutline serviceOne = baseService.getOne(Wraps.<ArticleOutline>lbQ().eq(ArticleOutline::getTaskId, outlineSaveDTO.getTaskId()).last(" limit 0,1 "));
        if (serviceOne == null) {
            return R.fail(I18nUtils.getMessage("parameter_error"));
        }
        serviceOne.setArticleOutline(outlineSaveDTO.getArticleOutline());
        baseService.updateById(serviceOne);
        return R.success(serviceOne);
    }


    @ApiOperation("用户修改正文")
    @PostMapping("updateContent")
    public R<ArticleOutline> updateContent(@RequestBody @Validated ArticleOutlineSaveDTO outlineSaveDTO) {

        ArticleOutline serviceOne = baseService.getOne(Wraps.<ArticleOutline>lbQ().eq(ArticleOutline::getTaskId, outlineSaveDTO.getTaskId()).last(" limit 0,1 "));
        if (serviceOne == null) {
            return R.fail(I18nUtils.getMessage("parameter_error"));
        }
        serviceOne.setArticleContent(outlineSaveDTO.getArticleContent());
        baseService.updateById(serviceOne);
        return R.success(serviceOne);
    }



}
