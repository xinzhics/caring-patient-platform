package com.caring.sass.ai.article.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.SaasCmsUtils;
import com.caring.sass.ai.article.config.ArticleUserPayConfig;
import com.caring.sass.ai.article.dao.ArticlePodcastJoinMapper;
import com.caring.sass.ai.article.dao.ArticleTaskMapper;
import com.caring.sass.ai.article.service.*;
import com.caring.sass.ai.dto.*;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.podcast.PodcastAudioTask;
import com.caring.sass.ai.entity.podcast.PodcastSoundSet;
import com.caring.sass.ai.podcast.dao.PodcastAudioTaskMapper;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import com.caring.sass.ai.podcast.dao.PodcastSoundSetMapper;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * Ai创作任务
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
@Slf4j
@Service

public class ArticleTaskServiceImpl extends SuperServiceImpl<ArticleTaskMapper, ArticleTask> implements ArticleTaskService {

    @Autowired
    ArticleEventLogService articleEventLogService;

    @Autowired
    ArticleTitleService articleTitleService;

    @Autowired
    ArticleOutlineService articleOutlineService;

    @Autowired
    ArticlePodcastJoinMapper articlePodcastJoinMapper;

    @Autowired
    PodcastMapper podcastMapper;

    @Autowired
    PodcastAudioTaskMapper podcastAudioTaskMapper;

    @Autowired
    PodcastSoundSetMapper podcastSoundSetMapper;

    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    ArticleUserPayConfig articleUserPayConfig;

    @Transactional
    @Override
    public boolean removeById(Serializable id) {

        articleTitleService.remove(Wraps.<ArticleTitle>lbQ().eq(ArticleTitle::getTaskId, id));
        articleOutlineService.remove(Wraps.<ArticleOutline>lbQ().eq(ArticleOutline::getTaskId, id));
        articlePodcastJoinMapper.delete(Wraps.<ArticlePodcastJoin>lbQ()
                .eq(ArticlePodcastJoin::getTaskId, id)
                .eq(ArticlePodcastJoin::getTaskType, TaskType.ARTICLE));
        boolean codeCheck = super.removeById(id);
        if (codeCheck) {
            // 通知saas的cms。删除资源的引用
//            SaasCmsUtils.deleteSaasStudioCms(id.toString(), "CMS_TYPE_TEXT");
        }
        return codeCheck;
    }

    /**
     * 将临时用户的数据，转移到手机号登录后的用户
     * @param tempUserId
     * @param toUserId
     */
    @Override
    public void changeCreateUser(Long tempUserId, Long toUserId) {

        UpdateWrapper<ArticleTask> updateWrapper = new UpdateWrapper<>();

        updateWrapper.eq("create_user", tempUserId);
        updateWrapper.eq("update_user", tempUserId);
        updateWrapper.set("create_user", toUserId);
        updateWrapper.set("update_user", toUserId);
        baseMapper.update(new ArticleTask(), updateWrapper);


        UpdateWrapper<ArticlePodcastJoin> joinUpdateWrapper = new UpdateWrapper<>();
        joinUpdateWrapper.eq("user_id", tempUserId);
        joinUpdateWrapper.set("user_id", toUserId);
        joinUpdateWrapper.set("create_user", toUserId);
        joinUpdateWrapper.set("update_user", toUserId);
        articlePodcastJoinMapper.update(new ArticlePodcastJoin(), joinUpdateWrapper);

        UpdateWrapper<Podcast> podcastUpdateWrapper = new UpdateWrapper<>();
        podcastUpdateWrapper.eq("user_id", tempUserId);
        podcastUpdateWrapper.set("user_id", toUserId);
        podcastMapper.update(new Podcast(), podcastUpdateWrapper);

        UpdateWrapper<PodcastAudioTask> audioTaskMapperUpdateWrapper = new UpdateWrapper<>();
        audioTaskMapperUpdateWrapper.eq("user_id", tempUserId);
        audioTaskMapperUpdateWrapper.set("user_id", toUserId);
        podcastAudioTaskMapper.update(new PodcastAudioTask(), audioTaskMapperUpdateWrapper);

        UpdateWrapper<PodcastSoundSet> soundSetUpdateWrapper = new UpdateWrapper<>();
        soundSetUpdateWrapper.eq("user_id", tempUserId);
        soundSetUpdateWrapper.set("user_id", toUserId);
        podcastSoundSetMapper.update(new PodcastSoundSet(), soundSetUpdateWrapper);

    }


    /**
     * 新建一个任务  关键词，受众， 写作风格， 字数 是否开启配图
     * @param model 实体对象
     * @return
     */
    @Override
    public boolean save(ArticleTask model) {

        Long userId = BaseContextHandler.getUserId();
        articleUserService.deductEnergy(userId, articleUserPayConfig.getImageTextSpend(), ConsumptionType.IMAGE_TEXT_CREATION);

        String uid = model.getUid();
        model.setTaskType(1);
        model.setCreativeApproach(1);
        model.setStep(1);
        model.setTaskStatus(0);
        super.save(model);
        articleEventLogService.save(ArticleEventLog.builder()
                .taskId(model.getId())
                .userId(userId)
                .eventType(ArticleEventLogType.SCIENCE_ORIGINAL_REQUIREMENT)
                .build());

        ArticleSee articleSee = new ArticleSee();
        articleSee.setUid(uid);
        articleSee.setTaskId(model.getId());
        if (StrUtil.isNotEmpty(uid)) {
            resetOutline(articleSee, model, false);
        }

        return true;
    }

    /**
     * 更新一个的配置 关键词，受众， 写作风格， 字数, 是否开启配图
     * @param model 实体对象
     * @return
     */
    @Override
    public boolean updateById(ArticleTask model) {

        String uid = model.getUid();
        model.setStep(1);
        super.updateById(model);

        ArticleSee articleSee = new ArticleSee();
        articleSee.setUid(uid);
        articleSee.setTaskId(model.getId());
        if (StrUtil.isNotEmpty(uid)) {
            resetOutline(articleSee, model, true);
        }
        return true;
    }

    /**
     * 生产提纲
     * @param articleSee
     * @param model
     * @return
     */
    @Override
    public SseEmitter resetOutline(ArticleSee articleSee, ArticleTask model, boolean isRewrite) {

        if (Objects.isNull(model)) {
            model = baseMapper.selectById(articleSee.getTaskId());
        }
        if (Objects.isNull(model)) {
            throw new BizException("任务不存在");
        }
        WritingService service = SpringUtils.getBean(WritingService.class);
        OutLineReq outLineReq = new OutLineReq();
        outLineReq.setUid(articleSee.getUid());
        outLineReq.setKeyword(model.getKeyWords());
        outLineReq.setAudience(model.getAudience());
        outLineReq.setWritingStyle(model.getWritingStyle());
        outLineReq.setVersion(model.getVersion());
        Locale locale = LocaleContextHolder.getLocale();
        if (isRewrite) {
            // 重写提纲
            articleEventLogService.save(ArticleEventLog.builder()
                    .taskId(model.getId())
                    .userId(model.getCreateUser())
                    .eventType(ArticleEventLogType.SCIENCE_ORIGINAL_REWRITE_OUTLINE)
                    .build());
        }
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(articleSee.getUid(), sseEmitter);
        service.outline(outLineReq, model.getId(), locale);
        return sseEmitter;

    }

    /**
     * 请求AI 写正文
     * @param articleSee
     */
    @Override
    public SseEmitter writeContent(ArticleSee articleSee) {

        String uid = articleSee.getUid();
        Long taskId = articleSee.getTaskId();
        ArticleTask task = baseMapper.selectById(taskId);
        ArticleOutline serviceOne = articleOutlineService.getOne(Wraps.<ArticleOutline>lbQ()
                .eq(ArticleOutline::getTaskId, taskId).last(" limit 0,1 "));
        WritingService service = SpringUtils.getBean(WritingService.class);
        if (Objects.isNull(serviceOne)) {
            throw new BizException("提纲不存在， 请检查");
        }
        ArticleReqDTO articleReqDTO = new ArticleReqDTO();
        articleReqDTO.setUid(uid);
        articleReqDTO.setWritingStyle(task.getWritingStyle());
        articleReqDTO.setVersion(task.getVersion());
        articleReqDTO.setKeyword(task.getKeyWords());
        articleReqDTO.setAudience(task.getAudience());
        articleReqDTO.setOutline(serviceOne.getArticleOutline());
        articleReqDTO.setWordNumber(task.getArticleWordCount().toString());
        articleReqDTO.setAutoMap(task.getAutomaticPictureMatching() == null || task.getAutomaticPictureMatching() == 0 ? "否" : "是");
        Locale locale = LocaleContextHolder.getLocale();
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);
        service.article(articleReqDTO, taskId, locale);
        Boolean rewrite = articleSee.getRewrite();
        if (rewrite != null && rewrite) {
            // 重写正文
            articleEventLogService.save(ArticleEventLog.builder()
                    .taskId(taskId)
                    .userId(task.getCreateUser())
                    .eventType(ArticleEventLogType.SCIENCE_ORIGINAL_REWRITE_CONTENT)
                    .build());
        } else {
            // 写正文
            articleEventLogService.save(ArticleEventLog.builder()
                    .taskId(taskId)
                    .userId(task.getCreateUser())
                    .eventType(ArticleEventLogType.SCIENCE_ORIGINAL_WRITE_CONTENT)
                    .build());
        }
        return sseEmitter;


    }

    /**
     * 请求AI重新创作标题
     * @param articleSee
     */
    @Override
    public SseEmitter writeTitle(ArticleSee articleSee) {

        String uid = articleSee.getUid();
        Long taskId = articleSee.getTaskId();
        ArticleTask task = baseMapper.selectById(taskId);
        ArticleOutline serviceOne = articleOutlineService.getOne(Wraps.<ArticleOutline>lbQ().eq(ArticleOutline::getTaskId, taskId).last(" limit 0,1 "));
        WritingService service = SpringUtils.getBean(WritingService.class);
        if (Objects.isNull(serviceOne)) {
            throw new BizException("正文不存在， 请检查");
        }
        TitleReq titleReq = new TitleReq();
        titleReq.setUid(uid);
        titleReq.setKeyword(task.getKeyWords());
        titleReq.setVersion(task.getVersion());
        titleReq.setAudience(task.getAudience());
        titleReq.setWritingStyle(task.getWritingStyle());
        titleReq.setArticle(serviceOne.getArticleContent());
        Locale locale = LocaleContextHolder.getLocale();

        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);
        service.title(titleReq, taskId, locale);
        Boolean rewrite = articleSee.getRewrite();
        if (rewrite != null && rewrite) {
            // 重写标题
            articleEventLogService.save(ArticleEventLog.builder()
                    .taskId(taskId)
                    .userId(task.getCreateUser())
                    .eventType(ArticleEventLogType.SCIENCE_ORIGINAL_REWRITE_TITLE)
                    .build());
        } else {
            // 写标题
            articleEventLogService.save(ArticleEventLog.builder()
                    .taskId(taskId)
                    .userId(task.getCreateUser())
                    .eventType(ArticleEventLogType.SCIENCE_ORIGINAL_WRITE_TITLE)
                    .build());
        }
        return sseEmitter;
    }


    @Override
    public void setTaskTitle(ArticleTask task) {

        task.setStep(5);
        ArticleTask selected = baseMapper.selectById(task.getId());
        if (selected.getTaskStatus() == 2) {
            task.setTaskStatus(2);
        } else {
            task.setTaskStatus(1);
        }
        baseMapper.updateById(task);

        ArticlePodcastJoin articlePodcastJoin = articlePodcastJoinMapper.selectOne(Wraps.<ArticlePodcastJoin>lbQ()
                .eq(ArticlePodcastJoin::getTaskId, task.getId())
                .eq(ArticlePodcastJoin::getTaskType, TaskType.ARTICLE));
        if (Objects.nonNull(articlePodcastJoin)) {
            articlePodcastJoin.setTaskName(task.getTitle());
            articlePodcastJoinMapper.updateById(articlePodcastJoin);
        }
        articleEventLogService.save(ArticleEventLog.builder()
                .taskId(task.getId())
                .userId(task.getCreateUser())
                .eventType(ArticleEventLogType.SCIENCE_ORIGINAL_SET_TITLE)
                .build());
    }


    @Transactional
    @Override
    public void saveDraft(ArticleTask task) {

        ArticleTask articleTask = baseMapper.selectById(task.getId());
        task.setTaskStatus(2);
        task.setCreateUser(articleTask.getCreateUser());
        baseMapper.updateById(task);

        ArticlePodcastJoin articlePodcastJoin = articlePodcastJoinMapper.selectOne(Wraps.<ArticlePodcastJoin>lbQ()
                .eq(ArticlePodcastJoin::getTaskId, task.getId())
                .eq(ArticlePodcastJoin::getTaskType, TaskType.ARTICLE));
        if (articlePodcastJoin == null) {
            articlePodcastJoin = new ArticlePodcastJoin(articleTask);
            articlePodcastJoin.setTaskStatus(TaskStatus.FINISHED);
            articlePodcastJoinMapper.insert(articlePodcastJoin);
        } else {
            ArticlePodcastJoin podcastJoin = new ArticlePodcastJoin(articleTask);
            BeanUtils.copyProperties(podcastJoin, articlePodcastJoin);
            podcastJoin.setTaskStatus(TaskStatus.FINISHED);
            articlePodcastJoinMapper.updateById(podcastJoin);
        }
        articleEventLogService.save(ArticleEventLog.builder()
                .taskId(task.getId())
                .userId(articleTask.getCreateUser())
                .eventType(ArticleEventLogType.SCIENCE_ORIGINAL_SAVE_PORTFOLIO)
                .build());

    }

    /**
     * 直接扣除一次图文创作的能量豆
     * @return
     */
    @Override
    public boolean onlyDeduct(ConsumptionType consumptionType) {
        Long userId = BaseContextHandler.getUserId();
        articleUserService.deductEnergy(userId, articleUserPayConfig.getImageTextSpend(), consumptionType);
        return true;
    }

    /**
     * 直接保存 图文创作 小红书文案，视频口播的数据
     * @param model 实体对象
     * @return
     */
    @Override
    public boolean onlySave(ArticleTask model) {

        Integer creativeApproach = model.getCreativeApproach();
        if (creativeApproach == null) {
            throw new BizException("请选择创作方式");
        }
        Integer imitativeWritingType = model.getImitativeWritingType();
        if (imitativeWritingType == null) {
            throw new BizException("请选择仿写方式");
        }
        String imitativeWritingMaterial = model.getImitativeWritingMaterial();
        if (StrUtil.isBlank(imitativeWritingMaterial)) {
            throw new BizException("请设置原创仿写素材");
        }
        Integer taskStatus = model.getTaskStatus();
        if (taskStatus == null) {
            model.setStep(5);
            model.setTaskStatus(2);
        }
        super.save(model);

        Integer taskType = model.getTaskType();
        if (taskType.equals(1)) {
            if (creativeApproach.equals(2)) {
                articleEventLogService.save(ArticleEventLog.builder()
                        .taskId(model.getId())
                        .userId(model.getCreateUser())
                        .eventType(ArticleEventLogType.SCIENCE_COPY_TEXT_SUBMIT_SETTING)
                        .build());
            }
        } else if (taskType.equals(2)) {
            if (creativeApproach.equals(1)) {
                articleEventLogService.save(ArticleEventLog.builder()
                        .taskId(model.getId())
                        .userId(model.getCreateUser())
                        .eventType(ArticleEventLogType.MEDIA_ORIGINAL_SUBMIT_SETTING)
                        .build());
            } else {
                articleEventLogService.save(ArticleEventLog.builder()
                        .taskId(model.getId())
                        .userId(model.getCreateUser())
                        .eventType(ArticleEventLogType.MEDIA_COPY_TEXT_SUBMIT)
                        .build());
            }
        } else if (taskType.equals(3)) {
            if (creativeApproach.equals(1)) {
                articleEventLogService.save(ArticleEventLog.builder()
                        .taskId(model.getId())
                        .userId(model.getCreateUser())
                        .eventType(ArticleEventLogType.VOICE_ORIGINAL_SUBMIT_SETTING)
                        .build());
            } else {
                articleEventLogService.save(ArticleEventLog.builder()
                        .taskId(model.getId())
                        .userId(model.getCreateUser())
                        .eventType(ArticleEventLogType.VOICE_COPY_TEXT_SUBMIT_SETTING)
                        .build());
            }
        }

        return true;
    }


    @Override
    public void onlyUpdate(ArticleTask model) {
        Integer creativeApproach = model.getCreativeApproach();
        if (creativeApproach == null) {
            throw new BizException("请选择创作方式");
        }
        Integer imitativeWritingType = model.getImitativeWritingType();
        if (imitativeWritingType == null) {
            throw new BizException("请选择仿写方式");
        }
        String imitativeWritingMaterial = model.getImitativeWritingMaterial();
        if (StrUtil.isBlank(imitativeWritingMaterial)) {
            throw new BizException("请设置原创仿写素材");
        }
        String content = model.getContent();
        if (StrUtil.isBlank(content)) {
            model.setStep(4);
            model.setTaskStatus(0);
        } else {
            model.setStep(5);
            model.setTaskStatus(2);
            Integer taskType = model.getTaskType();
            if (taskType.equals(1)) {
                if (creativeApproach.equals(2)) {
                    articleEventLogService.save(ArticleEventLog.builder()
                            .taskId(model.getId())
                            .userId(model.getCreateUser())
                            .eventType(ArticleEventLogType.SCIENCE_COPY_TEXT_SAVE_PORTFOLIO)
                            .build());
                }
            } else if (taskType.equals(2)) {
                if (creativeApproach.equals(1)) {
                    articleEventLogService.save(ArticleEventLog.builder()
                            .taskId(model.getId())
                            .userId(model.getCreateUser())
                            .eventType(ArticleEventLogType.MEDIA_ORIGINAL_SAVE_PORTFOLIO)
                            .build());
                } else {
                    articleEventLogService.save(ArticleEventLog.builder()
                            .taskId(model.getId())
                            .userId(model.getCreateUser())
                            .eventType(ArticleEventLogType.MEDIA_COPY_TEXT_SAVE_PORTFOLIO)
                            .build());
                }
            } else if (taskType.equals(3)) {
                if (creativeApproach.equals(1)) {
                    articleEventLogService.save(ArticleEventLog.builder()
                            .taskId(model.getId())
                            .userId(model.getCreateUser())
                            .eventType(ArticleEventLogType.VOICE_ORIGINAL_SAVE_PORTFOLIO)
                            .build());
                } else {
                    articleEventLogService.save(ArticleEventLog.builder()
                            .taskId(model.getId())
                            .userId(model.getCreateUser())
                            .eventType(ArticleEventLogType.VOICE_COPY_TEXT_SAVE_PORTFOLIO)
                            .build());
                }
            }
        }
        if (model.getId() != null) {
            baseMapper.updateById(model);
        }
    }

    /**
     * 根据AI返回的结果处理。
     * @param result
     */
    @Override
    public void updateAiContent(JSONObject result) {
        Object taskId = result.get("taskId");
        Object articleWritingType = result.get("articleWritingType");
        Object content = result.get("content");

        if (taskId != null && articleWritingType != null && content != null) {
            long taskLongId = Long.parseLong(taskId.toString());
            String contentString = content.toString();
            // 提纲
            if (ArticleWritingType.outline.toString().equals(articleWritingType.toString())) {
                try {
                    if (contentString.contains("####")) {
                        if (contentString.contains("---")) {
                            contentString = contentString.substring(contentString.indexOf("####"), contentString.indexOf("---"));
                        } else {
                            contentString = contentString.substring(contentString.indexOf("####"));
                        }
                    }
                    // 即使格式不符合预期，也保存原始内容
                    saveOrUpdateOutLine(taskLongId, contentString);
                } catch (Exception e) {
                    log.error("处理提纲内容异常: {}", e.getMessage());
                    // 保存原始内容作为兜底
                    saveOrUpdateOutLine(taskLongId, contentString);
                }
            } else if (ArticleWritingType.title.toString().equals(articleWritingType.toString())) {

                // 更新覆盖标题
                saveOrUpdateTitle(taskLongId, contentString);
            } else if (ArticleWritingType.text.toString().equals(articleWritingType.toString())) {

                // 更新覆盖正文
                saveOrUpdateContent(taskLongId, contentString);
            }
        }
    }

    /**
     * 覆盖更新提纲
     * @param taskId
     * @param content
     */
    public void saveOrUpdateOutLine(Long taskId, String content) {

        ArticleOutline serviceOne = articleOutlineService.getOne(Wraps.<ArticleOutline>lbQ()
                .eq(ArticleOutline::getTaskId, taskId)
                .last(" limit 0,1 "));
        if (serviceOne == null) {
            serviceOne = new ArticleOutline();
            serviceOne.setArticleOutline(content);
            serviceOne.setTaskId(taskId);
            articleOutlineService.save(serviceOne);
        } else {
            serviceOne.setArticleOutline(content);
            articleOutlineService.updateById(serviceOne);
        }
        updateStep(taskId,2);
    }

    /**
     * 更新步骤
     */
    public void updateStep(Long taskId, Integer step) {
        ArticleTask task = new ArticleTask();
        task.setStep(step);
        task.setId(taskId);
        baseMapper.updateById(task);
    }

    /**
     * 覆盖更新AI生成的标题
     * @param taskId
     * @param content
     */
    public void saveOrUpdateTitle(Long taskId, String content) {

        ArticleTitle articleTitle = articleTitleService.getOne(Wraps.<ArticleTitle>lbQ().eq(ArticleTitle::getTaskId, taskId).last(" limit 0,1 "));
        if (articleTitle == null) {
            articleTitle = new ArticleTitle();
            articleTitle.setArticleTitle(content);
            articleTitle.setTaskId(taskId);
            articleTitleService.save(articleTitle);
        } else {
            articleTitle.setArticleTitle(content);
            articleTitleService.updateById(articleTitle);
        }
        updateStep(taskId,4);
    }

    /**
     * 覆盖更新AI生成的正文
     * @param taskId
     * @param content
     */
    public void saveOrUpdateContent(Long taskId, String content) {

        ArticleOutline serviceOne = articleOutlineService.getOne(Wraps.<ArticleOutline>lbQ().eq(ArticleOutline::getTaskId, taskId).last(" limit 0,1 "));
        if (serviceOne == null) {
            serviceOne = new ArticleOutline();
            serviceOne.setArticleContent(content);
            serviceOne.setTaskId(taskId);
            articleOutlineService.save(serviceOne);
        } else {
            serviceOne.setArticleContent(content);
            articleOutlineService.updateById(serviceOne);
        }
        updateStep(taskId,3);
    }


    /**
     * 查询文章的所有
     * @param taskId
     * @return
     */
    @Override
    public ArticleTaskAllDTO getArticleContentAll(Long taskId) {

        ArticleTaskAllDTO taskAllDTO = new ArticleTaskAllDTO();
        ArticleTask articleTask = baseMapper.selectById(taskId);
        taskAllDTO.setArticleTask(articleTask);

        ArticleOutline serviceOne = articleOutlineService.getOne(Wraps.<ArticleOutline>lbQ().eq(ArticleOutline::getTaskId, taskId).last(" limit 0,1 "));
        taskAllDTO.setArticleOutline(serviceOne);

        ArticleTitle articleTitle = articleTitleService.getOne(Wraps.<ArticleTitle>lbQ().eq(ArticleTitle::getTaskId, taskId).last(" limit 0,1 "));
        taskAllDTO.setArticleTitle(articleTitle);

        return taskAllDTO;
    }


    /**
     * 只更新 文章字数要求和 自动配图开关。
     * @param task
     * @return
     */
    @Override
    public boolean updateWordCountMatching(ArticleTask task) {

        baseMapper.updateById(task);

        return true;
    }



}
