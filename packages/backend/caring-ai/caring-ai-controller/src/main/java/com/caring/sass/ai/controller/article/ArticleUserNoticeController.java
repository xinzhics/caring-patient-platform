package com.caring.sass.ai.controller.article;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleUserNoticeService;
import com.caring.sass.ai.dto.ArticleTaskPageDTO;
import com.caring.sass.ai.dto.article.ArticleUserNoticePageDTO;
import com.caring.sass.ai.entity.article.ArticleTask;
import com.caring.sass.ai.entity.article.ArticleUserNotice;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;


/**
 * <p>
 * 前端控制器
 * 科普用户系统通知
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleUserNotice")
@Api(value = "ArticleUserNotice", tags = "科普创作用户系统通知")
public class ArticleUserNoticeController {


    @Autowired
    ArticleUserNoticeService articleUserNoticeService;

    @ApiOperation("消息分页查询")
    @PostMapping("page")
    public R<IPage<ArticleUserNotice>> page(@RequestBody @Validated PageParams<ArticleUserNoticePageDTO> params) {

        IPage<ArticleUserNotice> builtPage = params.buildPage();

        ArticleUserNoticePageDTO model = params.getModel();
        LbqWrapper<ArticleUserNotice> wrapper = Wraps.<ArticleUserNotice>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(ArticleUserNotice::getUserId, model.getUserId());
        if (model.getNoticeType() != null) {
            wrapper.eq(ArticleUserNotice::getNoticeType, model.getNoticeType());
        }
        articleUserNoticeService.page(builtPage, wrapper);
        return R.success(builtPage);

    }


    @ApiOperation("设置消息已读")
    @PutMapping("updateStatus")
    public R<Boolean> updateStatus(@RequestParam Long id) {

        ArticleUserNotice notice = new ArticleUserNotice();
        notice.setId(id);
        notice.setReadStatus(1);
        articleUserNoticeService.updateById(notice);
        return R.success(true);

    }

    @ApiOperation("设置消息全部已读")
    @PutMapping("updateAllStatus")
    public R<Boolean> updateAllStatus(@RequestParam Long userId) {

        ArticleUserNotice notice = new ArticleUserNotice();
        UpdateWrapper<ArticleUserNotice> wrapper = new UpdateWrapper<>();
        wrapper.set("read_status", 1)
                .eq("read_status", 0)
                .eq("user_id", userId);
        articleUserNoticeService.update(notice, wrapper);
        return R.success(true);

    }



}
