package com.caring.sass.user.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.KeywordReply;
import com.caring.sass.user.entity.KeywordSetting;
import com.caring.sass.user.service.KeywordReplyService;
import com.caring.sass.user.service.KeywordSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;


/**
 * <p>
 * 前端控制器
 * 关键字回复内容
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/keywordReply")
@Api(value = "KeywordReply", tags = "关键字回复内容")
//@PreAuth(replace = "keywordReply:")
public class KeywordReplyController extends SuperController<KeywordReplyService, Long, KeywordReply, KeywordReplyPageDTO, KeywordReplySaveDTO, KeywordReplyUpdateDTO> {

    @Autowired
    KeywordSettingService keywordSettingService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<KeywordReply> keywordReplyList = list.stream().map((map) -> {
            KeywordReply keywordReply = KeywordReply.builder().build();
            //TODO 请在这里完成转换
            return keywordReply;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(keywordReplyList));
    }

    @Override
    public R<KeywordReply> handlerSave(KeywordReplySaveDTO model) {
        KeywordReply reply = baseService.save(model);
        R ok = new R(0, reply, "ok");
        ok.setDefExec(false);
        return ok;
    }

    @Override
    public R<KeywordReply> handlerUpdate(KeywordReplyUpdateDTO model) {
        KeywordReply update = baseService.update(model);
        R ok = new R(0, update, "ok");
        ok.setDefExec(false);
        return ok;
    }

    @ApiOperation("获取一个关键字回复的详细")
    @GetMapping("getOne/{id}")
    public R<KeywordReplyPageList> getOne(@PathVariable Long id) {
        KeywordReply keywordReply = baseService.getById(id);
        KeywordReplyPageList replyPageList = new KeywordReplyPageList();
        BeanUtils.copyProperties(keywordReply, replyPageList);
        replyPageList.setId(id);
        replyPageList.setKeywordSettingList(keywordSettingService.listByReplyId(keywordReply.getId()));
        return R.success(replyPageList);
    }

    @ApiOperation("更新规则的状态")
    @PutMapping("updateReplyStatus")
    public R<Boolean> update(@RequestBody @Validated KeywordReplyStatusUpdateDto params) {
        boolean status = baseService.updateStatus(params);
        return R.success(status);
    }

    @ApiOperation("获取文章的查看链接")
    @GetMapping("getCmsContentLink/{replyId}")
    public R<String> getCmsContentLink(@PathVariable("replyId") Long replyId) {
        String contentLink = baseService.getCmsContentLink(replyId);
        return R.success(contentLink);
    }


    @ApiOperation("规则分页列表")
    @PostMapping("replyPage")
    public R<IPage<KeywordReplyPageList>> replyPage(@RequestBody @Validated PageParams<KeywordReplyPageDTO> params) {

        IPage page = params.buildPage();
        KeywordReplyPageDTO model = params.getModel();
        String ruleName = model.getRuleName();
        LbqWrapper<KeywordReply> wrapper = Wraps.<KeywordReply>lbQ();
        String replyType = model.getReplyType();
        if (StrUtil.isNotEmpty(replyType)) {
            wrapper.eq(KeywordReply::getReplyType, replyType);
        }
        if (StrUtil.isNotEmpty(ruleName)) {
            wrapper.and(lbqWrapper -> lbqWrapper.apply(" rule_name like '%" + ruleName + "%' or " +
                    " id in (select ks.keyword_reply_id from u_user_keyword_setting as ks where ks.keyword_name like  '%" + ruleName + "%')" ));
        }
        IPage<KeywordReply> iPage = baseService.page(page, wrapper);
        IPage<KeywordReplyPageList> result = new Page<>();
        result.setCurrent(iPage.getCurrent());
        result.setTotal(iPage.getTotal());
        result.setPages(iPage.getPages());
        result.setSize(iPage.getSize());
        List<KeywordReplyPageList> keywordReplyPageLists = new ArrayList<>();
        List<KeywordReply> records = iPage.getRecords();
        KeywordReplyPageList replyPageList;
        if (CollUtil.isNotEmpty(records)) {
            for (KeywordReply record : records) {
                replyPageList = new KeywordReplyPageList();
                BeanUtils.copyProperties(record, replyPageList);
                replyPageList.setKeywordSettingList(keywordSettingService.listByReplyId(record.getId()));
                replyPageList.setId(record.getId());
                // 统计昨天触发次数，
                // 今天触发次数
                baseService.setTodayAndYesterdayTriggerTimes(replyPageList, record.getId());
                keywordReplyPageLists.add(replyPageList);
            }
        }
        result.setRecords(keywordReplyPageLists);
        return R.success(result);
    }


}
