package com.caring.sass.ai.podcast.service.impl;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.SaasCmsUtils;
import com.caring.sass.ai.article.config.ArticleUserPayConfig;
import com.caring.sass.ai.article.dao.ArticlePodcastJoinMapper;
import com.caring.sass.ai.article.service.ArticleEventLogService;
import com.caring.sass.ai.article.service.ArticleUserService;
import com.caring.sass.ai.dto.podcast.PodcastPreviewContentsDTO;
import com.caring.sass.ai.dto.podcast.PodcastSoundSetSaveDTO;
import com.caring.sass.ai.dto.podcast.PodcastSoundSetUpdateDTO;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.podcast.*;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.podcast.PodcastDifyWordApi;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import com.caring.sass.ai.podcast.dao.PodcastSoundSetMapper;
import com.caring.sass.ai.podcast.service.PodcastAudioTaskService;
import com.caring.sass.ai.podcast.service.PodcastService;
import com.caring.sass.ai.textual.config.TextualUserPayConfig;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 播客
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-12
 */
@Slf4j
@Service

public class PodcastServiceImpl extends SuperServiceImpl<PodcastMapper, Podcast> implements PodcastService {

    @Autowired
    PodcastSoundSetMapper podcastSoundSetMapper;

    @Autowired
    PodcastAudioTaskService podcastAudioTaskService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    PodcastDifyWordApi podcastDifyWordApi;

    @Autowired
    ArticlePodcastJoinMapper articlePodcastJoinMapper;

    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    ArticleUserPayConfig articleUserPayConfig;

    @Autowired
    TextualUserPayConfig textualUserPayConfig;

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;

    @Autowired
    ArticleEventLogService articleEventLogService;

    @Transactional
    @Override
    public boolean save(Podcast model) {


        String podcastType = model.getPodcastType();
        if ("article".equals(podcastType)) {
            articleUserService.deductEnergy(model.getUserId(), articleUserPayConfig.getCreatePodcastSpend(), ConsumptionType.POADCAST_CREATION);
        }

        if ("yi_zhisheng".equals(podcastType)) {

            if (PodcastModel.DEEP_DISCOVERY.equals(model.getPodcastModel())) {

                // 深度发现扣费
                articleUserService.deductEnergy(model.getUserId(), articleUserPayConfig.getPodcastDeepDiscovery(), ConsumptionType.podcastDeepDiscovery);
            } else if (PodcastModel.QUICK_LISTENING_ESSENCE.equals(model.getPodcastModel())) {

                // 速听精华扣费
                articleUserService.deductEnergy(model.getUserId(), articleUserPayConfig.getPodcastQuickListeningEssence(), ConsumptionType.podcastQuickListeningEssence);
            }

        }

        super.save(model);
        ArticlePodcastJoin podcastJoin = new ArticlePodcastJoin(model);
        articlePodcastJoinMapper.insert(podcastJoin);

        if ("article".equals(podcastType)) {
            articleEventLogService.save(ArticleEventLog.builder()
                    .userId(model.getUserId())
                    .eventType(ArticleEventLogType.BLOG_INPUT_TEXT_REQUIREMENT)
                    .taskId(model.getId())
                    .build());
        }
        return true;
    }


    @Override
    public boolean removeById(Serializable id) {

        articlePodcastJoinMapper.delete(Wraps.<ArticlePodcastJoin>lbQ()
                .eq(ArticlePodcastJoin::getTaskId, id)
                .eq(ArticlePodcastJoin::getTaskType, TaskType.AUDIO));
        boolean codeCheck = super.removeById(id);
        if (codeCheck) {
            // 通知saas的cms。删除资源的引用
//            SaasCmsUtils.deleteSaasStudioCms(id.toString(), "CMS_TYPE_VOICE");
        }
        return codeCheck;
    }

    @Override
    public Podcast getById(Serializable id) {
        Podcast podcast = super.getById(id);
        List<PodcastSoundSet> soundSets = podcastSoundSetMapper.selectList(Wraps.<PodcastSoundSet>lbQ().eq(PodcastSoundSet::getPodcastId, id).orderByAsc(SuperEntity::getCreateTime));
        if (Objects.nonNull(podcast)) {
            podcast.setSoundSet(soundSets);
        }
        return podcast;
    }

    /**
     * 设置播客的声音设置。
     *
     * @param soundSetSaveDTO
     * @return
     */
    @Transactional
    @Override
    public Podcast submitSound(PodcastSoundSetSaveDTO soundSetSaveDTO) {

        Podcast podcast = baseMapper.selectById(soundSetSaveDTO.getPodcastId());
        if (podcast == null) {
            throw new BizException("播客不存在");
        }
        if (podcast.getStep() == 1) {
            podcast.setStep(2);
            podcast.setTaskStatus(PodcastTaskStatus.SET_SOUND);
        }
        String podcastName = soundSetSaveDTO.getPodcastName();
        if (StrUtil.isNotBlank(podcastName)) {
            podcast.setPodcastName(podcastName.trim());
        }

        podcast.setPodcastStyle(soundSetSaveDTO.getPodcastStyle());
        this.updateById(podcast);
        if (podcast.getPodcastType().equals("textual")) {
            textualInterpretationUserService.deductEnergy(podcast.getUserId(), textualUserPayConfig.getCreatePodcastSpend(), TextualConsumptionType.POADCAST_CREATION);
        }

        List<PodcastSoundSetUpdateDTO> soundList = soundSetSaveDTO.getSoundList();

        List<Long> soundIds = soundList.stream()
                .map(PodcastSoundSetUpdateDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 清除掉前端没有提交的声音设置
        LbqWrapper<PodcastSoundSet> wrapper = Wraps.<PodcastSoundSet>lbQ()
                .eq(PodcastSoundSet::getPodcastId, podcast.getId());
        if (!soundIds.isEmpty()) {
            wrapper.notIn(PodcastSoundSet::getId, soundIds);
        }
        podcastSoundSetMapper.delete(wrapper);

        Set<String> stringSet = new HashSet<>();
        // 增加或者修改声音设置
        List<PodcastSoundSet> soundSets = new ArrayList<>();
        for (PodcastSoundSetUpdateDTO soundSetUpdateDTO : soundList) {
            PodcastSoundSet soundSet = new PodcastSoundSet();
            BeanUtils.copyProperties(soundSetUpdateDTO, soundSet);
            if (stringSet.contains(soundSetUpdateDTO.getRoleName())) {
                throw new BizException("角色名字不能重复");
            }
            stringSet.add(soundSetUpdateDTO.getRoleName());

            soundSet.setPodcastId(podcast.getId());
            soundSet.setUserId(podcast.getUserId());
            if (Objects.nonNull(soundSetUpdateDTO.getId())) {
                soundSet.setId(soundSetUpdateDTO.getId());
                podcastSoundSetMapper.updateById(soundSet);
            } else {
                podcastSoundSetMapper.insert(soundSet);
            }
            soundSets.add(soundSet);
        }
        podcast.setSoundSet(soundSets);
        // 提交给dify。去生成对话。
        // SaasGlobalThreadPool.execute(() -> syncCreatedPodcastPreviewContents(podcast.getId()));
        if ("article".equals(podcast.getPodcastType())) {
            articleEventLogService.save(ArticleEventLog.builder()
                    .userId(podcast.getUserId())
                    .eventType(ArticleEventLogType.BLOG_SET_VOICE)
                    .taskId(podcast.getId())
                    .build());
        }
        return podcast;

    }


    @Override
    public boolean updateById(Podcast model) {

        UpdateWrapper<ArticlePodcastJoin> updateWrapper = new UpdateWrapper<>();
        ArticlePodcastJoin podcastJoin = new ArticlePodcastJoin();
        updateWrapper.eq("task_id", model.getId());
        updateWrapper.eq("task_type", TaskType.AUDIO);
        if (!StrUtil.isBlank(model.getPodcastName())) {
            updateWrapper.set("task_name", model.getPodcastName());
        }
        if (PodcastTaskStatus.TASK_FINISH.equals(model.getTaskStatus())) {
            updateWrapper.set("task_status", TaskStatus.FINISHED);
        }
        updateWrapper.set("update_time", LocalDateTime.now());
        articlePodcastJoinMapper.update(podcastJoin, updateWrapper);

        return super.updateById(model);
    }

    /**
     * 生产播客使用的文本
     * @param podcastId
     */
    @Override
    public SseEmitter syncCreatedPodcastPreviewContents(Long podcastId) {
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_1_OUT);
        // 调用dify模型。生成播客对话的内容
        Podcast podcast = baseMapper.selectById(podcastId);
        List<PodcastSoundSet> soundSets = podcastSoundSetMapper.selectList(Wraps.<PodcastSoundSet>lbQ()
                .orderByAsc(SuperEntity::getCreateTime)
                .eq(PodcastSoundSet::getPodcastId, podcastId));
        if (soundSets.isEmpty()) {
            error(sseEmitter, "请先设置声音");
            return sseEmitter;
        }
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(podcastId + ":PODCAST_DIFY_TASK_STATUS", "1", 10, TimeUnit.SECONDS);
        if (aBoolean != null && !aBoolean) {
            return sseEmitter;
        }
        podcast.setTaskStatus(PodcastTaskStatus.CREATE_DIALOGUE);
        this.updateById(podcast);
        if ("article".equals(podcast.getPodcastType())) {
            articleEventLogService.save(ArticleEventLog.builder()
                    .userId(podcast.getUserId())
                    .eventType(ArticleEventLogType.BLOG_PREVIEW_CREATE_CONTENT)
                    .taskId(podcast.getId())
                    .build());
        }
        podcastDifyWordApi.callDifyCompletionMessagesStreaming(podcast, soundSets,sseEmitter);
        return sseEmitter;
    }

    private void error(SseEmitter sseEmitter, String message) {
        try {
            message = StrUtil.isEmpty(message) ? "[Error]" : message;
            sseEmitter.send(SseEmitter.event()
                    .id("[Error]")
                    .data(message)
                    .reconnectTime(3000));
            sseEmitter.complete();
        } catch (Exception e) {
            log.error("error", e);
        }
    }


    /**
     * 处理一个字符串。长度不能超过1000字节，如果超过将其截取为多个1000字节内的字符串
     * @param input
     * @param maxLength
     * @return
     */
    public static List<String> splitStringByByteLength(String input, int maxLength) {
        List<String> result = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();
        int currentLength = 0;

        for (char c : input.toCharArray()) {
            int charLength = String.valueOf(c).getBytes(StandardCharsets.UTF_8).length;

            if (currentLength + charLength > maxLength) {
                // Add the current segment to the result and start a new one
                result.add(currentSegment.toString());
                currentSegment = new StringBuilder();
                currentLength = 0;
            }

            currentSegment.append(c);
            currentLength += charLength;
        }

        // Add the last segment if it's not empty
        if (currentSegment.length() > 0) {
            result.add(currentSegment.toString());
        }

        return result;
    }

    /**
     * 将 对话 根据 \n 去拆开。然后提交给火山生产音频
     * @param podcastId
     * @param previewContents
     */
    public void createAudioTask(Long podcastId, String previewContents) {

        // 开始异步 解析 文本。
        // 分析角色 分析对话。 生产任务，生成音频。
        List<PodcastSoundSet> soundSets = podcastSoundSetMapper.selectList(Wraps.<PodcastSoundSet>lbQ()
                .eq(PodcastSoundSet::getPodcastId, podcastId)
                .orderByAsc(SuperEntity::getCreateTime));
        List<PodcastAudioTask> tasks = new ArrayList<>(30);
        int i = 1;
        if (soundSets.size() == 1) {
            PodcastSoundSet soundSet = soundSets.get(0);
            // 通过角色的名称，获取声音的设置
            if (soundSet == null) {
                return;
            }
            String voiceType = soundSet.getRoleSoundSet();
            boolean isMiniMaxVoice = voiceType.startsWith("h_");
            // 使用海螺的音色 单人说话的 任务内容长度 可以长一点。
            List<String> segments;
            if (isMiniMaxVoice) {
                segments = splitStringByByteLength(previewContents, 3000);
            } else {
                segments = splitStringByByteLength(previewContents, 800);
            }
            for (String segment : segments) {
                if (StrUtil.isBlank(segment)) {
                    continue;
                }
                segment = segment.trim().replaceAll("\u00A0", " ").replaceAll("\\s+", " ");
                if (StrUtil.isBlank(segment)) {
                    continue;
                }
                PodcastAudioTask podcastAudioTask = new PodcastAudioTask();
                podcastAudioTask.setPodcastId(podcastId);
                podcastAudioTask.setAudioText(segment);
                podcastAudioTask.setSoundSetId(soundSet.getId());
                podcastAudioTask.setTaskStatus(PodcastAudioTaskStatus.WAIT_SUBMIT);
                podcastAudioTask.setUserId(soundSet.getUserId());
                podcastAudioTask.setTaskSort(i);
                tasks.add(podcastAudioTask);
                i++;
            }
        } else {
            String[] previewContentList = previewContents.split("\n");
            Map<String, PodcastSoundSet> soundSetMap = soundSets.stream().collect(Collectors.toMap(PodcastSoundSet::getRoleName, item -> item));
            PodcastSoundSet soundSet = null;
            for (String previewContent : previewContentList) {
                if (previewContent.trim().isEmpty()) {
                    continue;
                }
                String name;
                String text;
                if (previewContent.contains("：")) {
                    name = previewContent.substring(0, previewContent.indexOf("："));
                    text = previewContent.substring(previewContent.indexOf("：") + 1);
                } else {
                    name = "";
                    text = previewContent.trim();
                }
                text = text.trim();
                if (StrUtil.isEmpty(text)) {
                    continue;
                }
                name = name.trim();
                if (StrUtil.isNotBlank(name)) {
                    soundSet = soundSetMap.get(name);
                }
                if (name.equals("name1")) {
                    soundSet = soundSets.get(0);
                }
                if (name.equals("name2")) {
                    soundSet = soundSets.get(1);
                }
                if (name.equals("name3")) {
                    soundSet = soundSets.get(2);
                }
                // 通过角色的名称，获取声音的设置
                if (soundSet == null) {
                    continue;
                }

                List<String> segments = splitStringByByteLength(text, 1000);
                for (String segment : segments) {
                    if (StrUtil.isBlank(segment)) {
                        continue;
                    }
                    segment = segment.trim().replaceAll("\u00A0", " ").replaceAll("\\s+", " ");
                    if (StrUtil.isBlank(segment)) {
                        continue;
                    }
                    PodcastAudioTask podcastAudioTask = new PodcastAudioTask();
                    podcastAudioTask.setPodcastId(podcastId);
                    podcastAudioTask.setAudioText(segment);
                    podcastAudioTask.setSoundSetId(soundSet.getId());
                    podcastAudioTask.setTaskStatus(PodcastAudioTaskStatus.WAIT_SUBMIT);
                    podcastAudioTask.setUserId(soundSet.getUserId());
                    podcastAudioTask.setTaskSort(i);
                    tasks.add(podcastAudioTask);
                    i++;
                }
            }
        }
        // 创建制作音频任务 并上传到 火山。
        if (CollUtil.isNotEmpty(tasks)) {
            podcastAudioTaskService.createAudioTask(podcastId, tasks);
        }
    }

    /**
     * 更新预览内容。
     * 解析，并生成开始生产音频。
     * @param podcastPreviewContentsDTO
     */
    @Override
    public void podcastPreviewContents(PodcastPreviewContentsDTO podcastPreviewContentsDTO) {

        String previewContents = podcastPreviewContentsDTO.getPodcastPreviewContents();
        Long podcastId = podcastPreviewContentsDTO.getPodcastId();
        Podcast podcast = new Podcast();
        podcast.setId(podcastId);
        podcast.setPodcastPreviewContents(previewContents);
        podcast.setTaskStatus(PodcastTaskStatus.CREATE_AUDIO);
        podcast.setStep(4);
        this.updateById(podcast);
        Podcast selected = baseMapper.selectById(podcastId);

        SaasGlobalThreadPool.execute(() -> createAudioTask(podcastId, previewContents));
        // 记录事件日志
        articleEventLogService.save(ArticleEventLog.builder()
                .userId(selected.getUserId())
                .eventType(ArticleEventLogType.BLOG_SUBMIT_AUDIO_CONTENT)
                .taskId(podcastId)
                .build());
    }

    /**
     * 只保存一下对话内容
     * @param podcastPreviewContentsDTO
     */
    @Override
    public void savePodcastContent(PodcastPreviewContentsDTO podcastPreviewContentsDTO) {
        String previewContents = podcastPreviewContentsDTO.getPodcastPreviewContents();
        Long podcastId = podcastPreviewContentsDTO.getPodcastId();
        Podcast podcast = new Podcast();
        podcast.setId(podcastId);
        podcast.setPodcastPreviewContents(previewContents);
        this.updateById(podcast);
    }

    @Override
    public boolean closeSse(Long podcastId) {

        SseEmitter emitter = SseEmitterSession.get(podcastId.toString());
        if (emitter == null) {
            return true;
        }
        emitter.complete();
        SseEmitterSession.remove(podcastId.toString());
        return true;
    }

    @Override
    public SseEmitter createdSee(Long podcastId) {
        String uid = podcastId.toString();
        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter != null) {
            return emitter;
        }
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);

        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            SseEmitterSession.remove(uid);
        });

        //超时回调
        sseEmitter.onTimeout(() -> log.info("[{}]连接超时...................", uid));
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.error("[{}] error ,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .name("发生异常！")
                                .data("ERROR")
                                .reconnectTime(3000));
                    } catch (IOException e) {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                    }
                }
        );

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            log.error("", e);
        }
        log.info("[{}]创建sse连接成功！", uid);
        return sseEmitter;

    }


    /**
     * 建立连接后。直接发送已经生成的预览内容
     * @param uid
     */
    @Override
    public void syncSendCurrentContent(String uid) {

        Podcast podcast;
        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter == null) {
            throw new BizException("请创建链接");
        }
        podcast = baseMapper.selectById(Long.parseLong(uid));
        if (podcast.getStep() >= 3) {
            String previewContents = podcast.getPodcastPreviewContents();
            if (StrUtil.isNotBlank(previewContents)) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("content", previewContents);
                    emitter.send(SseEmitter.event()
                            .id(UUID.fastUUID().toString())
                            .data(jsonObject.toJSONString())
                            .reconnectTime(3000));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", "[DONE]");
                emitter.send(SseEmitter.event()
                        .id(UUID.fastUUID().toString())
                        .data(jsonObject.toJSONString())
                        .reconnectTime(3000));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            emitter.complete();
            redisTemplate.delete("podcast:" + uid);
        } else {
            SaasGlobalThreadPool.execute(() -> syncGetRedisMessageSendWeb(uid));
        }

    }

    /**
     * 如果用户连接进来。博客任务状态还没有变为 完成对话 。
     * 那么从redis消息队列中 获取对话内容。推送给前端
     * 最大超时时间 5分钟
     * @param uid
     */
    public void syncGetRedisMessageSendWeb(String uid) {
        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = now.plusMinutes(3);
        while (true) {
            String string = null;
            try {
                string = redisTemplate.opsForList().rightPop("podcast:" + uid, 3, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("", e);
            }
            if (StrUtil.isEmpty(string)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", string);
                emitter.send(SseEmitter.event()
                        .id(UUID.fastUUID().toString())
                        .data(jsonObject.toJSONString())
                        .reconnectTime(3000));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if ("[DONE]".equals(string)) {
                emitter.complete();
                break;
            }
            // 如果5分钟还没有结束。那么自动关闭连接
            if (LocalDateTime.now().isAfter(dateTime)) {
                emitter.complete();
                break;
            }
        }
    }


    /**
     * 查询是否有有未完成的播客
     * @param userId
     * @return
     */
    @Override
    public Podcast queryExistNotFinishPodcast(Long userId) {

        LbqWrapper<Podcast> wrapper = Wraps.<Podcast>lbQ()
                .in(Podcast::getTaskStatus, PodcastTaskStatus.CREATE_DIALOGUE,
                        PodcastTaskStatus.CREATE_DIALOGUE_SUCCESS,
                        PodcastTaskStatus.CREATE_DIALOGUE_ERROR)
                .eq(Podcast::getUserId, userId)
                .last(" limit 1 ");
        Podcast podcast = baseMapper.selectOne(wrapper);
        if (Objects.nonNull(podcast)) {
            List<PodcastSoundSet> soundSets = podcastSoundSetMapper.selectList(Wraps.<PodcastSoundSet>lbQ()
                    .eq(PodcastSoundSet::getPodcastId, podcast.getId())
                    .orderByAsc(SuperEntity::getCreateTime));
            podcast.setSoundSet(soundSets);
        }
        return podcast;
    }


    @Override
    public void checkPodcastName(Podcast podcast) {

        String podcastName = podcast.getPodcastName();
        Long podcastId = podcast.getId();

        LbqWrapper<Podcast> wrapper = Wraps.<Podcast>lbQ()
                .select(SuperEntity::getId, Podcast::getPodcastName)
                .eq(Podcast::getUserId, BaseContextHandler.getUserId())
                .eq(Podcast::getPodcastType, podcast.getPodcastType())
                .eq(Podcast::getPodcastName, podcastName.trim())
                .last(" limit 0, 1");
        if (Objects.nonNull(podcastId)) {
            wrapper.ne(SuperEntity::getId, podcastId);
        }
        Podcast one = baseMapper.selectOne(wrapper);
        if (Objects.nonNull(one)) {
            throw new BizException("音频名称已经存在");
        }
    }
}
