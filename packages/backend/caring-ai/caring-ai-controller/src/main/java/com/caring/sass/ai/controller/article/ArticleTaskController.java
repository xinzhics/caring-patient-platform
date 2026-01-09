package com.caring.sass.ai.controller.article;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleEventLogService;
import com.caring.sass.ai.article.service.ArticlePodcastJoinService;
import com.caring.sass.ai.article.service.ArticleTaskService;
import com.caring.sass.ai.article.service.impl.WritingService;
import com.caring.sass.ai.dto.*;
import com.caring.sass.ai.entity.article.ArticleEventLog;
import com.caring.sass.ai.entity.article.ArticlePodcastJoin;
import com.caring.sass.ai.entity.article.ArticleTask;
import com.caring.sass.ai.entity.article.ConsumptionType;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.utils.ImageDrawUtils;
import com.caring.sass.ai.utils.ImageFrameVideoUtils;
import com.caring.sass.ai.utils.VideoOverlay;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * <p>
 * 前端控制器
 * Ai创作任务
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleTask")
@Api(value = "ArticleTask", tags = "科普创作 任务")
public class ArticleTaskController {

    @Autowired
    WritingService writingService;

    @Autowired
    ArticleTaskService baseService;

    @Autowired
    ArticlePodcastJoinService articlePodcastJoinService;

    @Autowired
    ArticleEventLogService articleEventLogService;


    @Deprecated
    @ApiOperation("创建sse链接")
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestParam(name = "uid", required = true) String uid) {
        if (StrUtil.isBlank(uid)) {
            throw new BizException("会话ID不存在");
        }
        return writingService.createSse(uid);
    }

    @ApiOperation("一次扣费")
    @GetMapping("/onlyDeduct")
    public R<Boolean> onlyDeduct(@RequestParam ConsumptionType consumptionType) {

        boolean save = baseService.onlyDeduct(consumptionType);
        return R.success(save);
    }

    @ApiOperation("直接保存创作结果")
    @PostMapping("/onlySave")
    public R<ArticleTask> onlySave(@RequestBody @Validated ArticleTaskSaveBo articleTaskSaveBo) {

        ArticleTask task = BeanUtil.toBean(articleTaskSaveBo, ArticleTask.class);
        baseService.onlySave(task);
        return R.success(task);
    }


    @ApiOperation("修改创作结果")
    @PostMapping("/onlyUpdate")
    public R<ArticleTask> onlyUpdate(@RequestBody @Validated ArticleTaskSaveBo articleTaskSaveBo) {

        ArticleTask task = BeanUtil.toBean(articleTaskSaveBo, ArticleTask.class);
        baseService.onlyUpdate(task);
        return R.success(task);
    }



    @ApiOperation("提要求")
    @PostMapping("/save")
    public R<ArticleTask> save(@RequestBody @Validated ArticleTaskSaveDTO articleTaskSaveDTO) {

        ArticleTask task = BeanUtil.toBean(articleTaskSaveDTO, ArticleTask.class);
        boolean save = baseService.save(task);
        return R.success(task);
    }

    @ApiOperation("只更新 文章字数要求和 自动配图开关")
    @PostMapping("/update/wordCount/matching")
    public R<ArticleTask> updateWordCountMatching(@RequestBody @Validated ArticleTaskUpdateOtherDTO articleTaskUpdateOtherDTO) {

        ArticleTask task = BeanUtil.toBean(articleTaskUpdateOtherDTO, ArticleTask.class);
        baseService.updateWordCountMatching(task);
        return R.success(task);
    }



    @ApiOperation("修改要求")
    @PostMapping("/update")
    public R<ArticleTask> update(@RequestBody @Validated ArticleTaskUpdateDTO articleTaskUpdateDTO) {

        ArticleTask task = BeanUtil.toBean(articleTaskUpdateDTO, ArticleTask.class);
        boolean save = baseService.updateById(task);
        return R.success(task);
    }


    @Deprecated
    @ApiOperation("重新生成提纲")
    @PostMapping("/resetOutline")
    public R<String> resetOutline(@RequestBody @Validated ArticleSee articleSee) {

        baseService.resetOutline(articleSee, null, articleSee.getRewrite());
        return R.success("success");
    }

    @Deprecated
    @ApiOperation("创建正文")
    @PostMapping("/writeContent")
    public R<String> writeContent(@RequestBody @Validated ArticleSee articleSee) {

        baseService.writeContent(articleSee);
        return R.success("success");


    }

    @Deprecated
    @ApiOperation("创建标题")
    @PostMapping("/writeTitle")
    public R<String> writeTitle(@RequestBody @Validated ArticleSee articleSee) {

        baseService.writeTitle(articleSee);
        return R.success("success");

    }

    @ApiOperation("sse生成提纲")
    @GetMapping("/sseResetOutline")
    public SseEmitter sseResetOutline(String uid, Long taskId, Boolean rewrite) {
        ArticleSee articleSee = ArticleSee.builder()
                .uid(uid)
                .taskId(taskId)
                .rewrite(rewrite)
                .build();
        SseEmitter emitter = baseService.resetOutline(articleSee, null, rewrite);
        return emitter;
    }

    @ApiOperation("sse创建正文")
    @GetMapping("/sseWriteContent")
    public SseEmitter sseWriteContent(String uid, Long taskId, Boolean rewrite) {
        ArticleSee articleSee = ArticleSee.builder()
                .uid(uid)
                .taskId(taskId)
                .rewrite(rewrite)
                .build();
        SseEmitter emitter = baseService.writeContent(articleSee);
        return emitter;

    }

    @ApiOperation("sse创建标题")
    @GetMapping("/sseWriteTitle")
    public SseEmitter sseWriteTitle(String uid, Long taskId, Boolean rewrite) {
        ArticleSee articleSee = ArticleSee.builder()
                .uid(uid)
                .taskId(taskId)
                .rewrite(rewrite)
                .build();
        SseEmitter emitter = baseService.writeTitle(articleSee);
        return emitter;

    }


    @ApiOperation("设置最终创作的标题")
    @PostMapping("/setTaskTitle")
    public R<String> setTaskTitle(@RequestBody @Validated ArticleTaskUpdateTitleDTO updateTitleDTO) {

        ArticleTask task = new ArticleTask();
        task.setId(updateTitleDTO.getId());
        task.setTitle(updateTitleDTO.getTitle());

        baseService.setTaskTitle(task);
        return R.success("success");

    }


    @ApiOperation("存为草稿")
    @PostMapping("/saveDraft/{taskId}")
    public R<String> saveDraft(@PathVariable("taskId") Long taskId) {

        ArticleTask task = new ArticleTask();
        task.setId(taskId);
        task.setTaskStatus(2);
        baseService.saveDraft(task);
        return R.success("success");

    }



    @ApiOperation("我的创作列表")
    @PostMapping("creation/page")
    public R<IPage<ArticlePodcastJoin>> creationPage(@RequestBody @Validated PageParams<ArticlePodcastJoin> params) {

        IPage<ArticlePodcastJoin> buildPage = params.buildPage();
        ArticlePodcastJoin model = params.getModel();

        if (model.getUserId() == null) {
            model.setUserId(BaseContextHandler.getUserId());
        }
        LbqWrapper<ArticlePodcastJoin> wrapper = Wraps.<ArticlePodcastJoin>lbQ()
                .eq(ArticlePodcastJoin::getUserId, model.getUserId());
        if (!StrUtil.isBlank(model.getTaskName())) {
            wrapper.like(ArticlePodcastJoin::getTaskName, model.getTaskName());
        }
        if (model.getTaskStatus() != null) {
            wrapper.eq(ArticlePodcastJoin::getTaskStatus, model.getTaskStatus());
        }
        if (model.getTaskType() != null) {
            wrapper.eq(ArticlePodcastJoin::getTaskType, model.getTaskType());
        }
        if (model.getCreateTime() != null) {
            wrapper.ge(ArticlePodcastJoin::getCreateTime, LocalDateTime.of(model.getCreateTime().toLocalDate(), LocalTime.MIN) );
        }
        if (model.getUpdateTime() != null) {
            wrapper.le(ArticlePodcastJoin::getCreateTime, LocalDateTime.of( model.getUpdateTime().toLocalDate(), LocalTime.MAX));
        }
        if (model.getShowAIKnowledge() != null) {
            wrapper.eq(ArticlePodcastJoin::getShowAIKnowledge, model.getShowAIKnowledge());
        }
        articlePodcastJoinService.page(buildPage, wrapper);
        return R.success(buildPage);
    }


    /**
     * 查播客的
     * @param params
     * @return
     */
    @ApiOperation("我的创作列表")
    @PostMapping("creation/page/anno")
    public R<IPage<ArticlePodcastJoin>> annoCreationPage(@RequestBody @Validated PageParams<ArticlePodcastJoin> params) {
        return creationPage(params);
    }


    /**
     * 查文章的
     * @param params
     * @return
     */
    @ApiOperation("文案创作操作性分页列表")
    @PostMapping("draftPage/anno")
    public R<IPage<ArticleTask>> annoDraftPage(@RequestBody @Validated PageParams<ArticleTaskPageDTO> params) {
        return draftPage(params);
    }

    @ApiOperation("文案创作操作性分页列表")
    @PostMapping("draftPage")
    public R<IPage<ArticleTask>> draftPage(@RequestBody @Validated PageParams<ArticleTaskPageDTO> params) {

        IPage<ArticleTask> buildPage = params.buildPage();
        ArticleTaskPageDTO model = params.getModel();
        LbqWrapper<ArticleTask> wrapper = Wraps.<ArticleTask>lbQ()
                .eq(SuperEntity::getCreateUser, model.getUserId())
                .eq(ArticleTask::getTaskStatus, model.getTaskStatus());
        if (model.getTitle() != null) {
            wrapper.like(ArticleTask::getTitle, model.getTitle());
        }
        if (model.getShowAIKnowledge() != null) {
            wrapper.eq(ArticleTask::getShowAIKnowledge, model.getShowAIKnowledge());
        }
        baseService.page(buildPage, wrapper);
        return R.success(buildPage);
    }

    @ApiOperation("发布文章在医生数字分身平台")
    @PutMapping("/showArticleInAIKnowledge")
    public R<Boolean> showArticleInAIKnowledge(@RequestBody List<Long> ids) {
        baseService.update(new ArticleTask(), new UpdateWrapper<ArticleTask>()
                .in("id", ids)
                .set("show_ai_knowledge", true));
        return R.success(true);
    }

    @ApiOperation("发布播客在医生数字分身平台")
    @PutMapping("/showPodcastInAIKnowledge")
    public R<Boolean> showPodcastInAIKnowledge(@RequestBody List<Long> ids) {
        articlePodcastJoinService.update(new ArticlePodcastJoin(), new UpdateWrapper<ArticlePodcastJoin>()
                .in("id", ids)
                .set("show_ai_knowledge", true));
        return R.success(true);
    }

    @ApiOperation("隐藏文章在医生数字分身平台")
    @PutMapping("/hideArticleInAIKnowledge/{id}")
    public R<Boolean> hideArticleInAIKnowledge(@PathVariable("id") Long id) {
        baseService.update(new ArticleTask(), new UpdateWrapper<ArticleTask>()
                .eq("id", id)
                .set("show_ai_knowledge", false));
        return R.success(true);
    }

    @ApiOperation("隐藏播客在医生数字分身平台")
    @PutMapping("/hidePodcastInAIKnowledge/{id}")
    public R<Boolean> hidePodcastInAIKnowledge(@PathVariable("id") Long id) {
        articlePodcastJoinService.update(new ArticlePodcastJoin(), new UpdateWrapper<ArticlePodcastJoin>()
                .eq("id", id)
                .set("show_ai_knowledge", false));
        return R.success(true);
    }



    @ApiOperation("查询AI文章的所有")
    @GetMapping("getArticleContentAll/{taskId}")
    public R<ArticleTaskAllDTO> getArticleContentAll(@PathVariable("taskId") Long taskId) {

        ArticleTaskAllDTO contentAll = baseService.getArticleContentAll(taskId);
        return R.success(contentAll);

    }


    @ApiOperation("删除草稿")
    @DeleteMapping("deleteTask/{taskId}")
    public R<Boolean> deleteTask(@PathVariable("taskId") Long taskId) {
        baseService.removeById(taskId);

        return R.success(true);
    }




}
