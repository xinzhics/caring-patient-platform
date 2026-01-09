package com.caring.sass.ai.controller.textual;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleUserVoiceService;
import com.caring.sass.ai.dto.article.ArticleUserVoicePageDTO;
import com.caring.sass.ai.dto.article.ArticleUserVoiceSaveDTO;
import com.caring.sass.ai.dto.article.ArticleUserVoiceUpdateDTO;
import com.caring.sass.ai.entity.article.ArticleUserVoice;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 文献解读用户声音
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/textualInterpretationUserVoice")
@Api(value = "TextualInterpretationUserVoice", tags = "文献解读用户声音")
public class TextualInterpretationUserVoiceController extends SuperController<ArticleUserVoiceService, Long, ArticleUserVoice, ArticleUserVoicePageDTO, ArticleUserVoiceSaveDTO, ArticleUserVoiceUpdateDTO>  {

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;


    @Override
    public R<List<ArticleUserVoice>> query(ArticleUserVoice data) {

        Long userId = data.getUserId();
        TextualInterpretationUser textualInterpretationUser = textualInterpretationUserService.getById(userId);
        // 查询用户自己的音色，或者符合条件的 默认音色
        LbqWrapper<ArticleUserVoice> eq = null;
        try {
            eq = Wraps.<ArticleUserVoice>lbQ()
                    .eq(ArticleUserVoice::getUserMobile, EncryptionUtil.encrypt(textualInterpretationUser.getUserMobile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eq.eq(ArticleUserVoice::getDeleteStatus, false);
        eq.eq(ArticleUserVoice::getHasClone, true);
        eq.or(w ->w.eq(ArticleUserVoice::getDefaultVoice, true));
        eq.orderByDesc(SuperEntity::getCreateTime);
        List<ArticleUserVoice> listed = baseService.list(eq);
        return R.success(listed);
    }

    @Override
    public R<ArticleUserVoice> save(ArticleUserVoiceSaveDTO articleUserVoiceSaveDTO) {
        ArticleUserVoice userVoice = new ArticleUserVoice();
        BeanUtils.copyProperties(articleUserVoiceSaveDTO, userVoice);
        userVoice.setTextual(true);
        baseService.save(userVoice);
        return R.success(userVoice);
    }

    /**
     * 查询时，将系统的默认音色也查询出来。
     * @param params
     * @return
     */
    @Override
    public R<IPage<ArticleUserVoice>> page(PageParams<ArticleUserVoicePageDTO> params) {

        ArticleUserVoicePageDTO model = params.getModel();
        IPage<ArticleUserVoice> builtPage = params.buildPage();
        TextualInterpretationUser textualInterpretationUser = textualInterpretationUserService.getById(model.getUserId());
        // 查询用户自己的音色，或者符合条件的 默认音色
        LbqWrapper<ArticleUserVoice> eq = null;
        try {
            eq = Wraps.<ArticleUserVoice>lbQ()
                    .eq(ArticleUserVoice::getUserMobile, EncryptionUtil.encrypt(textualInterpretationUser.getUserMobile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eq.eq(ArticleUserVoice::getCloneStatus, model.getCloneStatus());
        eq.eq(ArticleUserVoice::getDeleteStatus, false);
        eq.eq(ArticleUserVoice::getDefaultVoice, false);
        if (StringUtils.isNotEmptyString(model.getName())) {
            eq.like(ArticleUserVoice::getName, model.getName());
        }
        eq.orderByDesc(SuperEntity::getCreateTime);
        eq.or(w -> w.like(ArticleUserVoice::getName, model.getName())
                .eq(ArticleUserVoice::getDefaultVoice, true));
        baseService.page(builtPage, eq);
        return R.success(builtPage);
    }




}
