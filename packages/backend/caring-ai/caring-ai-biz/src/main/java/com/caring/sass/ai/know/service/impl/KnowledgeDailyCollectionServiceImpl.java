package com.caring.sass.ai.know.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.know.CreateMedicalRecordsModel;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.config.DifyApiConfig;
import com.caring.sass.ai.know.dao.KnowledgeDailyCollectionMapper;
import com.caring.sass.ai.know.model.KnowledgeFileQuery;
import com.caring.sass.ai.know.service.KnowledgeDailyCollectionService;
import com.caring.sass.ai.know.service.KnowledgeService;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.ai.know.util.DifyWorkApi;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 日常收集文本内容
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@Slf4j
@Service

public class KnowledgeDailyCollectionServiceImpl extends SuperServiceImpl<KnowledgeDailyCollectionMapper, KnowledgeDailyCollection> implements KnowledgeDailyCollectionService {

    @Autowired
    KnowledgeService knowledgeService;

    @Autowired
    DifyWorkApi difyWorkApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean save(KnowledgeDailyCollection model) {

        // 将文本上传到知识库
        model.setUserId(BaseContextHandler.getUserId());
        knowledgeService.createText(model);
        baseMapper.insert(model);
        return true;
    }

    @Override
    public boolean update(KnowledgeDailyCollection model) {

        Long modelId = model.getId();
        KnowledgeDailyCollection collection = baseMapper.selectOne(Wraps.<KnowledgeDailyCollection>lbQ()
                .select(KnowledgeDailyCollection::getDocumentId, KnowledgeDailyCollection::getKnowId, KnowledgeDailyCollection::getDifyBatch, KnowledgeDailyCollection::getTextTitle, KnowledgeDailyCollection::getTextContent)
                .eq(SuperEntity::getId, modelId));

        if (Objects.isNull(collection)) {
            throw new BizException("数据不存在");
        }
        // 如果标题和内容都没有变化，那么不更新数据库
        if (!collection.getTextContent().equals(model.getTextContent()) || !collection.getTextTitle().equals(model.getTextTitle())) {
            if (collection.getKnowId() != null) {
                knowledgeService.updateText(collection.getDocumentId(), collection.getKnowId(), model);
            }
            baseMapper.updateById(model);
        }
        return true;
    }

    @Transactional
    @Override
    public void delete(Long id) {

        // 删除知识库的文档
        KnowledgeDailyCollection collection = baseMapper.selectById(id);
        if (Objects.nonNull(collection)) {
            if (collection.getKnowId() != null) {
                knowledgeService.deleteKnowledgeFile(collection.getDocumentId(), collection.getKnowId());
            }
            baseMapper.deleteById(id);
        }
    }

    /**
     * 调用AI创建标题
     * @param textContent
     * @return
     */
    @Override
    public String getTitle(String textContent) {

        return knowledgeService.getTitle(textContent);
    }


    @Override
    public void page(IPage<KnowledgeDailyCollection> builtPage, String query, Long userId) {

        LbqWrapper<KnowledgeDailyCollection> wrapper = Wraps.<KnowledgeDailyCollection>lbQ();
        wrapper.eq(KnowledgeDailyCollection::getUserId, userId);
        if (StrUtil.isNotBlank(query)) {
            wrapper.and(w-> w.like(KnowledgeDailyCollection::getTextTitle, query).or().like(KnowledgeDailyCollection::getKeyWord, query));
        }

        baseMapper.selectPage(builtPage, wrapper);
    }


    /**
     * 查询制作的AI病历。并返回结果
     * @param uid
     * @return
     */
    public String getMedicalContent(String uid) {

        return redisTemplate.opsForValue().get("ai_case:" + uid);

    }


    @Override
    public void createMedicalRecords(CreateMedicalRecordsModel createMedicalRecordsModel) {
        String uid = createMedicalRecordsModel.getUid();
        SseEmitter sseEmitter = SseEmitterSession.get(uid);
        if (sseEmitter == null) {
            log.info("uid sse not exist。 {}", uid);
            throw new BizException("连接已断开，请重试");
        }
        difyWorkApi.callDifyCompletionMessagesStreaming(sseEmitter, uid, createMedicalRecordsModel.getSoundRecordingText());
    }
}
