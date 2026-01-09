package com.caring.sass.ai.controller.article;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleEventLogService;
import com.caring.sass.ai.article.service.ArticleUserService;
import com.caring.sass.ai.article.service.ArticleUserVoiceService;
import com.caring.sass.ai.dto.article.ArticleUserVoicePageDTO;
import com.caring.sass.ai.dto.article.ArticleUserVoiceSaveDTO;
import com.caring.sass.ai.dto.article.ArticleUserVoiceUpdateDTO;
import com.caring.sass.ai.dto.article.ArticleVoiceCloneDTO;
import com.caring.sass.ai.entity.article.ArticleEventLog;
import com.caring.sass.ai.entity.article.ArticleEventLogType;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.article.ArticleUserVoice;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
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
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @date 2025-02-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleUserVoice")
@Api(value = "ArticleUserVoice", tags = "科普创作声音管理")
public class ArticleUserVoiceController extends SuperController<ArticleUserVoiceService, Long, ArticleUserVoice, ArticleUserVoicePageDTO, ArticleUserVoiceSaveDTO, ArticleUserVoiceUpdateDTO> {


    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    ArticleEventLogService articleEventLogService;

    @Override
    public R<ArticleUserVoice> save(ArticleUserVoiceSaveDTO articleUserVoiceSaveDTO) {
        R<ArticleUserVoice> saved = super.save(articleUserVoiceSaveDTO);
        ArticleUserVoice data = saved.getData();
        articleEventLogService.save(ArticleEventLog.builder()
                .userId(data.getUserId())
                .eventType(ArticleEventLogType.VOICE_CLONE_START)
                .taskId(data.getId())
                .build());
        return saved;
    }

    @Override
    public R<List<ArticleUserVoice>> query(ArticleUserVoice data) {

        Long userId = data.getUserId();
        ArticleUser articleUser = articleUserService.getById(userId);
        // 查询用户自己的音色，或者符合条件的 默认音色
        LbqWrapper<ArticleUserVoice> eq = null;
        try {
            eq = Wraps.<ArticleUserVoice>lbQ()
                    .eq(ArticleUserVoice::getUserMobile, EncryptionUtil.encrypt(articleUser.getUserMobile()));
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

    /**
     * 查询时，将系统的默认音色也查询出来。
     * @param params
     * @return
     */
    @Override
    public R<IPage<ArticleUserVoice>> page(PageParams<ArticleUserVoicePageDTO> params) {

        ArticleUserVoicePageDTO model = params.getModel();
        IPage<ArticleUserVoice> builtPage = params.buildPage();
        ArticleUser articleUser = articleUserService.getById(model.getUserId());
        // 查询用户自己的音色，或者符合条件的 默认音色
        LbqWrapper<ArticleUserVoice> eq = null;
        try {
            eq = Wraps.<ArticleUserVoice>lbQ()
                    .eq(ArticleUserVoice::getUserMobile, EncryptionUtil.encrypt(articleUser.getUserMobile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eq.eq(ArticleUserVoice::getCloneStatus, model.getCloneStatus());
        eq.eq(ArticleUserVoice::getDeleteStatus, false);
        eq.eq(ArticleUserVoice::getDefaultVoice, false);
        if (StringUtils.isNotEmptyString(model.getName())) {
            eq.like(ArticleUserVoice::getName, model.getName());
        }
        eq.or(w -> w.like(ArticleUserVoice::getName, model.getName())
                .eq(ArticleUserVoice::getDefaultVoice, true));
        baseService.page(builtPage, eq);
        eq.orderByDesc(SuperEntity::getCreateTime);
        return R.success(builtPage);
    }
}
