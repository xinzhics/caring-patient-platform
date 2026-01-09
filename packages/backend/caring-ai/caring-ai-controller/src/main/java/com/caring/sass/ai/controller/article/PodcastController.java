package com.caring.sass.ai.controller.article;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.article.service.ArticleEventLogService;
import com.caring.sass.ai.dto.podcast.PodcastPageDTO;
import com.caring.sass.ai.dto.podcast.PodcastPreviewContentsDTO;
import com.caring.sass.ai.dto.podcast.PodcastSoundSetSaveDTO;
import com.caring.sass.ai.dto.podcast.PodcastUpdateDTO;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.podcast.PodcastInputType;
import com.caring.sass.ai.entity.podcast.PodcastTaskStatus;
import com.caring.sass.ai.entity.textual.TextualInterpretationPptTask;
import com.caring.sass.ai.podcast.service.PodcastService;
import com.caring.sass.ai.podcast.service.PodcastSoundSetService;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.utils.SpringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.base.R;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * <p>
 * 前端控制器
 * 播客
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-12
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/podcast")
@Api(value = "Podcast", tags = "科普创作-播客")
public class PodcastController {


    @Autowired
    PodcastService podcastService;


    @Autowired
    PodcastSoundSetService podcastSoundSetService;


    @ApiOperation("提交一个播客的输入文本设置")
    @PostMapping("submitPodcast")
    public R<Podcast> addPodcast(@RequestBody @Validated PodcastUpdateDTO podcastUpdateDTO) {

        Podcast podcast = new Podcast();
        String podcastType = podcastUpdateDTO.getPodcastType();
        if (StrUtil.isBlank(podcastType)) {
            podcastUpdateDTO.setPodcastType("article");
        }
        BeanUtils.copyProperties(podcastUpdateDTO, podcast);

        if ("article".equals(podcast.getPodcastType())) {
            podcast.setPodcastName(podcast.getPodcastName().trim());
            podcastService.checkPodcastName(podcast);
        }

        if (podcastUpdateDTO.getId() == null) {
            podcast.setTaskStatus(PodcastTaskStatus.INPUT_TEXT);
            podcast.setStep(1);
            podcast.setUserId(BaseContextHandler.getUserId());
            podcastService.save(podcast);
        } else {
            podcast.setId(podcastUpdateDTO.getId());
            podcastService.updateById(podcast);
        }
        return R.success(podcast);

    }

    @ApiOperation("易智声 作品列表")
    @PostMapping("anno/creation/page")
    public R<IPage<Podcast>> annoCreationPage(@RequestBody @Validated PageParams<PodcastPageDTO> params) {
        IPage<Podcast> buildPage = params.buildPage();
        PodcastPageDTO model = params.getModel();

        LbqWrapper<Podcast> wrapper = Wraps.<Podcast>lbQ()
                .eq(Podcast::getUserId, model.getUserId());
        if (!StrUtil.isBlank(model.getPodcastName())) {
            wrapper.like(Podcast::getPodcastName, model.getPodcastName());
        }
        if (StrUtil.isNotBlank(model.getPodcastName())) {
            wrapper.and(wrapper1 -> wrapper1.like(Podcast::getPodcastName, model.getPodcastName())
                    .or(wrapper2 -> wrapper2.like(Podcast::getPodcastInputFileName, model.getPodcastName())
                            .eq(Podcast::getPodcastInputType, PodcastInputType.DOCUMENT)
                            .isNull(Podcast::getPodcastName)
                    )

            );
        }

        if (model.getTaskStatus() != null) {
            if (model.getTaskStatus().equals(TaskStatus.NOT_FINISHED)) {
                wrapper.ne(Podcast::getTaskStatus, PodcastTaskStatus.TASK_FINISH);
            } else {
                wrapper.eq(Podcast::getTaskStatus, PodcastTaskStatus.TASK_FINISH);
            }
        }
        if (model.getCreateTime() != null) {
            wrapper.ge(Podcast::getCreateTime, LocalDateTime.of(model.getCreateTime().toLocalDate(), LocalTime.MIN) );
        }
        if (model.getUpdateTime() != null) {
            wrapper.le(Podcast::getCreateTime, LocalDateTime.of( model.getUpdateTime().toLocalDate(), LocalTime.MAX));
        }
        if (model.getShowAIKnowledge() != null) {
            wrapper.eq(Podcast::getShowAIKnowledge, model.getShowAIKnowledge());
        }
        if (model.getReleaseStatus() != null) {
            wrapper.eq(Podcast::getReleaseStatus, model.getReleaseStatus());
        }
        podcastService.page(buildPage, wrapper);
        return R.success(buildPage);
    }

    @ApiOperation("我的创作列表")
    @PostMapping("creation/page")
    public R<IPage<Podcast>> creationPage(@RequestBody @Validated PageParams<PodcastPageDTO> params) {

        IPage<Podcast> buildPage = params.buildPage();
        PodcastPageDTO model = params.getModel();

        if (model.getUserId() == null) {
            model.setUserId(BaseContextHandler.getUserId());
        }
        LbqWrapper<Podcast> wrapper = Wraps.<Podcast>lbQ()
                .eq(Podcast::getUserId, model.getUserId());
        if (!StrUtil.isBlank(model.getPodcastName())) {
            wrapper.like(Podcast::getPodcastName, model.getPodcastName());
        }
        if (StrUtil.isNotBlank(model.getPodcastName())) {
            wrapper.and(wrapper1 -> wrapper1.like(Podcast::getPodcastName, model.getPodcastName())
                .or(wrapper2 -> wrapper2.like(Podcast::getPodcastInputFileName, model.getPodcastName())
                                .eq(Podcast::getPodcastInputType, PodcastInputType.DOCUMENT)
                                .isNull(Podcast::getPodcastName)
                )

            );
        }

        if (model.getTaskStatus() != null) {
            if (model.getTaskStatus().equals(TaskStatus.NOT_FINISHED)) {
                wrapper.ne(Podcast::getTaskStatus, PodcastTaskStatus.TASK_FINISH);
            } else {
                wrapper.eq(Podcast::getTaskStatus, PodcastTaskStatus.TASK_FINISH);
            }
        }
        if (model.getCreateTime() != null) {
            wrapper.ge(Podcast::getCreateTime, LocalDateTime.of(model.getCreateTime().toLocalDate(), LocalTime.MIN) );
        }
        if (model.getUpdateTime() != null) {
            wrapper.le(Podcast::getCreateTime, LocalDateTime.of( model.getUpdateTime().toLocalDate(), LocalTime.MAX));
        }
        if (model.getShowAIKnowledge() != null) {
            wrapper.eq(Podcast::getShowAIKnowledge, model.getShowAIKnowledge());
        }
        if (model.getReleaseStatus() != null) {
            wrapper.eq(Podcast::getReleaseStatus, model.getReleaseStatus());
        }
        podcastService.page(buildPage, wrapper);
        return R.success(buildPage);
    }

    @ApiOperation("易智声 发布")
    @PutMapping("/release//{id}")
    public R<Boolean> releasePodcast(@PathVariable("id") Long id) {
        podcastService.update(new Podcast(), new UpdateWrapper<Podcast>()
                .eq("id", id)
                .set("show_ai_knowledge", true));
        return R.success(true);
    }

    @ApiOperation("易智声 取消发布")
    @PutMapping("/cancelRelease/podcast/{id}")
    public R<Boolean> cancelRelease(@PathVariable("id") Long id) {
        podcastService.update(new Podcast(), new UpdateWrapper<Podcast>()
                .eq("id", id)
                .set("show_ai_knowledge", false));
        return R.success(true);
    }


    @ApiOperation("显示播客在医生数字分身平台")
    @PutMapping("/showPodcastInAIKnowledge/{id}")
    public R<Boolean> showPodcastInAIKnowledge(@PathVariable("id") Long id) {
        podcastService.update(new Podcast(), new UpdateWrapper<Podcast>()
                .eq("id", id)
                .set("show_ai_knowledge", true));
        return R.success(true);
    }

    @ApiOperation("隐藏播客在医生数字分身平台")
    @PutMapping("/hidePodcastInAIKnowledge/{id}")
    public R<Boolean> hidePodcastInAIKnowledge(@PathVariable("id") Long id) {
        podcastService.update(new Podcast(), new UpdateWrapper<Podcast>()
                .eq("id", id)
                .set("show_ai_knowledge", false));
        return R.success(true);
    }


    @ApiOperation("检查名称是否重复")
    @PostMapping("checkPodcastName")
    public R<Boolean> checkPodcastName(@RequestBody @Validated PodcastUpdateDTO podcastUpdateDTO) {

        Podcast podcast = new Podcast();
        BeanUtils.copyProperties(podcastUpdateDTO, podcast);

        try {
            podcastService.checkPodcastName(podcast);
            return R.success(true);
        } catch (BizException e) {
            return R.success(false);
        }

    }



    @ApiOperation("提交播客的声音设置")
    @PostMapping("sound")
    public R<Podcast> submitSound(@RequestBody @Validated PodcastSoundSetSaveDTO soundSetSaveDTO) {

        Podcast submitted = podcastService.submitSound(soundSetSaveDTO);
        return R.success(submitted);

    }


    @ApiOperation("生成对话")
    @GetMapping("sound")
    public SseEmitter createSound(@RequestParam Long podcastId) {

        return podcastService.syncCreatedPodcastPreviewContents(podcastId);
    }


    @ApiOperation("查询未完成的播客")
    @GetMapping("queryExistNotFinishPodcast")
    public R<Podcast> queryExistNotFinishPodcast() {

        Long userId = BaseContextHandler.getUserId();
        Podcast podcast = podcastService.queryExistNotFinishPodcast(userId);
        return R.success(podcast);
    }


    @ApiOperation("预览对话时开始新创作")
    @GetMapping("startNewPodcast")
    public R<Podcast> startNewPodcast(@RequestParam Long podcastId) {

        Long userId = BaseContextHandler.getUserId();
        Podcast podcast = podcastService.getById(podcastId);
        podcast.setTaskStatus(PodcastTaskStatus.DIALOGUE_NOT_CONFIRMED);
        podcastService.updateById(podcast);
        if ("article".equals(podcast.getPodcastType())) {
            ArticleEventLogService articleEventLogService = SpringUtils.getBean(ArticleEventLogService.class);
            articleEventLogService.save(ArticleEventLog.builder()
                    .userId(podcast.getUserId())
                    .eventType(ArticleEventLogType.BLOG_ABANDON_PREVIEW_CONTENT)
                    .taskId(podcast.getId())
                    .build());
        }
        return R.success(podcast);
    }

    @ApiOperation("删除播客")
    @DeleteMapping()
    public R<Boolean> deletePodcast(@RequestParam Long podcastId) {

        podcastService.removeById(podcastId);
        return R.success(true);
    }

    @ApiOperation("只保存对话内容")
    @PostMapping("savePodcastContent")
    public R<Boolean> savePodcastContent(@RequestBody @Validated PodcastPreviewContentsDTO podcastPreviewContentsDTO) {

        podcastService.savePodcastContent(podcastPreviewContentsDTO);
        return R.success(true);

    }


    @ApiOperation("编辑预览对话，开始制作音频")
    @PostMapping("podcastPreviewContents")
    public R<Boolean> podcastPreviewContents(@RequestBody @Validated PodcastPreviewContentsDTO podcastPreviewContentsDTO) {

        podcastService.podcastPreviewContents(podcastPreviewContentsDTO);
        return R.success(true);

    }



    @ApiOperation("获取播客音频")
    @GetMapping("getPodcastAudio")
    public R<String> getPodcastAudio(@RequestParam Long podcastId) {

        String audioUrl = podcastService.getById(podcastId).getPodcastFinalAudioUrl();
        return R.success(audioUrl);

    }

    @ApiOperation("获取播客详细")
    @GetMapping("anno/getPodcast")
    public R<Podcast> annoGetPodcast(@RequestParam Long podcastId) {
        return getPodcast(podcastId);
    }

    @ApiOperation("获取播客")
    @GetMapping("getPodcast")
    public R<Podcast> getPodcast(@RequestParam Long podcastId) {
        Podcast podcast = podcastService.getById(podcastId);
        if (podcast.getShowNumber() == null) {
            podcast.setShowNumber(1);
        } else {
            podcast.setShowNumber(podcast.getShowNumber() + 1);
        }
        podcastService.updateById(podcast);
        return R.success(podcast);
    }


    @ApiOperation("创建sse,接收预览对话")
    @GetMapping("createdSse")
    public SseEmitter createdSee(@RequestParam Long podcastId) {

        return podcastService.createdSee(podcastId);
    }

    @ApiOperation("通知链接成功")
    @GetMapping("sseSuccess")
    public R<Boolean> sseSuccess(@RequestParam Long podcastId) {
        podcastService.syncSendCurrentContent(podcastId.toString());
        return R.success(true);
    }



    @ApiOperation("关闭sse响应")
    @GetMapping("closeSse")
    public R<Boolean> closeSse(@RequestParam Long podcastId) {
        podcastService.closeSse(podcastId);
        return R.success(true);
    }



}
