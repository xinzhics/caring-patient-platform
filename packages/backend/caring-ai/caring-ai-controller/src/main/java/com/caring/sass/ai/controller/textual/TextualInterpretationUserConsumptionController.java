package com.caring.sass.ai.controller.textual;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleAccountConsumptionService;
import com.caring.sass.ai.article.service.ArticleUserConsumptionService;
import com.caring.sass.ai.dto.textual.TextualInterpretationUserConsumptionPageDTO;
import com.caring.sass.ai.entity.article.ArticleAccountConsumption;
import com.caring.sass.ai.entity.article.ArticleUserConsumption;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.ai.entity.textual.TextualInterpretationUserConsumption;
import com.caring.sass.ai.textual.service.TextualInterpretationUserConsumptionService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.base.R;
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
 * 文献解读能量豆消费记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/textualInterpretationUserConsumption")
@Api(value = "TextualInterpretationUserConsumption", tags = "文献解读能量豆消费记录")
public class TextualInterpretationUserConsumptionController {


    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;


    @Autowired
    ArticleAccountConsumptionService articleAccountConsumptionService;

    @ApiOperation("能量豆明细")
    @PostMapping("page")
    public R<IPage<ArticleAccountConsumption>> page(@RequestBody @Validated PageParams<TextualInterpretationUserConsumptionPageDTO> params) {

        IPage<ArticleAccountConsumption> builtPage = params.buildPage();
        TextualInterpretationUserConsumptionPageDTO model = params.getModel();
        Long userId = model.getUserId();
        TextualInterpretationUser user = textualInterpretationUserService.getById(userId);

        LbqWrapper<ArticleAccountConsumption> wrapper = null;
        try {
            wrapper = Wraps.<ArticleAccountConsumption>lbQ()
                    .orderByDesc(SuperEntity::getCreateTime)
                    .eq(ArticleAccountConsumption::getUserMobile, EncryptionUtil.encrypt(user.getUserMobile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        articleAccountConsumptionService.page(builtPage, wrapper);
        articleAccountConsumptionService.setDetails(builtPage.getRecords());

        return R.success(builtPage);

    }



}
