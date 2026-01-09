package com.caring.sass.ai.controller.textual;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.entity.textual.TextualInterpretationUserNotice;
import com.caring.sass.ai.textual.service.TextualInterpretationUserNoticeService;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 文献解读系统通知
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/textualInterpretationUserNotice")
@Api(value = "TextualInterpretationUserNotice", tags = "文献解读系统通知")
public class TextualInterpretationUserNoticeController {


    @Autowired
    TextualInterpretationUserNoticeService textualInterpretationUserNoticeService;

    @ApiOperation("消息分页查询")
    @PostMapping("page")
    public R<IPage<TextualInterpretationUserNotice>> page(@RequestBody @Validated PageParams<TextualInterpretationUserNotice> params) {

        IPage<TextualInterpretationUserNotice> builtPage = params.buildPage();

        TextualInterpretationUserNotice model = params.getModel();
        LbqWrapper<TextualInterpretationUserNotice> wrapper = Wraps.<TextualInterpretationUserNotice>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(TextualInterpretationUserNotice::getUserId, model.getUserId());
        if (model.getNoticeType() != null) {
            wrapper.eq(TextualInterpretationUserNotice::getNoticeType, model.getNoticeType());
        }
        textualInterpretationUserNoticeService.page(builtPage, wrapper);
        return R.success(builtPage);

    }


    @ApiOperation("设置消息已读")
    @PutMapping("updateStatus")
    public R<Boolean> updateStatus(@RequestParam Long id) {

        TextualInterpretationUserNotice notice = new TextualInterpretationUserNotice();
        notice.setId(id);
        notice.setReadStatus(1);
        textualInterpretationUserNoticeService.updateById(notice);
        return R.success(true);

    }

    @ApiOperation("设置消息全部已读")
    @PutMapping("updateAllStatus")
    public R<Boolean> updateAllStatus(@RequestParam Long userId) {

        TextualInterpretationUserNotice notice = new TextualInterpretationUserNotice();
        UpdateWrapper<TextualInterpretationUserNotice> wrapper = new UpdateWrapper<>();
        wrapper.set("read_status", 1)
                .eq("read_status", 0)
                .eq("user_id", userId);
        textualInterpretationUserNoticeService.update(notice, wrapper);
        return R.success(true);

    }



}
