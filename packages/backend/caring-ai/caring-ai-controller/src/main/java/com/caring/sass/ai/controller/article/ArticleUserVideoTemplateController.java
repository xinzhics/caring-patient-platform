package com.caring.sass.ai.controller.article;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleUserVideoTemplateService;
import com.caring.sass.ai.article.task.ArticleVideoTemplateWaterTask;
import com.caring.sass.ai.dto.article.ArticleUserVideoTemplatePageDTO;
import com.caring.sass.ai.dto.article.ArticleUserVideoTemplateSaveDTO;
import com.caring.sass.ai.dto.article.ArticleUserVideoTemplateUpdateDTO;
import com.caring.sass.ai.dto.article.ArticleVideoDTO;
import com.caring.sass.ai.entity.article.ArticleUserVideoTemplate;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 视频底板
 * </p>
 *
 * @author leizhi
 * @date 2025-02-26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleUserVideoTemplate")
@Api(value = "ArticleUserVideoTemplate", tags = "科普创作 形象管理")
public class ArticleUserVideoTemplateController extends SuperController<ArticleUserVideoTemplateService, Long, ArticleUserVideoTemplate,
        ArticleUserVideoTemplatePageDTO, ArticleUserVideoTemplateSaveDTO, ArticleUserVideoTemplateUpdateDTO> {


    @Override
    public R<IPage<ArticleUserVideoTemplate>> page(PageParams<ArticleUserVideoTemplatePageDTO> params) {
        ArticleUserVideoTemplatePageDTO model = params.getModel();
        model.setDeleteStatus(false);
        return super.page(params);
    }



    @ApiOperation("解析视频信息，获取视频中的音频 (并发 10)")
    @PutMapping("obtainAudioFromTheVideo")
    public R<ArticleVideoDTO> obtainAudioFromTheVideo(@RequestBody @Validated ArticleVideoDTO articleVideoDTO) {

        baseService.obtainAudioFromTheVideo(articleVideoDTO);
        return R.success(articleVideoDTO);

    }


    @Autowired
    ArticleVideoTemplateWaterTask articleVideoTemplateWaterTask;

    @ApiOperation("测试水印任务")
    @PutMapping("testWater")
    public R<String> testWater() {

        articleVideoTemplateWaterTask.submitWaterMarkTask();
        return R.success("测试成功");
    }


    @ApiOperation("测试查询水印任务")
    @PutMapping("testQueryWater")
    public R<String> testQueryWater() {

        articleVideoTemplateWaterTask.queryWaterMarkTask();
        return R.success("测试成功");
    }
}
