package com.caring.sass.ai.know.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.config.DifyApiConfig;
import com.caring.sass.ai.know.dao.*;
import com.caring.sass.ai.know.model.DocumentText;
import com.caring.sass.ai.know.service.KnowledgeFileLabelService;
import com.caring.sass.ai.know.service.KnowledgeFileService;
import com.caring.sass.ai.know.util.DifyKnowApi;
import com.caring.sass.ai.know.util.DifyWorkApi;

import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 知识库文档标签表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-25
 */
@Slf4j
@Service
public class KnowledgeFileLabelServiceImpl implements KnowledgeFileLabelService {


    @Autowired
    KnowledgeDailyCollectionMapper knowledgeDailyCollectionMapper;

    @Autowired
    KnowledgeFileService knowledgeFileService;

    @Autowired
    KnowledgeFileCaseDatabaseLabelMapper knowledgeFileCaseDatabaseLabelMapper;

    @Autowired
    KnowledgeFilePersonalAchievementsLabelMapper knowledgeFilePersonalAchievementsLabelMapper;

    @Autowired
    KnowledgeFileAcademicMaterialsLabelMapper knowledgeFileAcademicMaterialsLabelMapper;

    @Autowired
    DifyKnowApi difyKnowApi;

    @Autowired
    DifyWorkApi difyWorkApi;

    @Autowired
    DifyApiConfig apiConfig;

    @Override
    public void asyncAnalyzeTags(KnowledgeDailyCollection collection) {
        String textContent = collection.getTextContent();   // 用户录音解析出来的文本

        // 调用AI 获取这个日常收集文本的返回的关键词
        String getKeyWord = difyWorkApi.callAiCreateKeywordGetKeyWord(textContent, collection.getUserId(), apiConfig.getRi_chang_shou_ji_key_word_apikey());
        String KEY_WORD = "";

        if (StrUtil.isNotEmpty(getKeyWord)) {
            JSONObject jsonObject = JSON.parseObject(getKeyWord);
            KEY_WORD = jsonObject.getString(KnowledgeLabelEnum.KEY_WORD.getCode());
        }
        collection.setKeyWord(KEY_WORD);
        collection.setDifyFileStatus(KnowledgeFileStatus.CHECK);
        knowledgeDailyCollectionMapper.updateById(collection);
    }

    @Override
    public void asyncAnalyzeTags(KnowledgeFile collection) {

        KnowledgeType knowType = collection.getKnowType();
        String knowDifyId = collection.getKnowDifyId();
        String difyFileId = collection.getDifyFileId();

        List<DocumentText> fileSegments = difyKnowApi.getFileSegments(knowDifyId, difyFileId);
        if (CollUtil.isEmpty(fileSegments)) {
            collection.setDifyFileStatus(KnowledgeFileStatus.CHECK);
            knowledgeFileService.updateById(collection);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        fileSegments.forEach(
                item -> stringBuilder.append(item.getContent())
        );

        if (KnowledgeType.ACADEMIC_MATERIALS.equals(knowType)) {
            String getKeyWord = difyWorkApi.callAiCreateKeywordGetKeyWord(stringBuilder.toString(), collection.getFileUserId(), apiConfig.getZhuan_ye_xue_shu_key_word_apikey());
            getZhuanYeXueShuKeyWord(collection, getKeyWord);
        }

        if (KnowledgeType.PERSONAL_ACHIEVEMENTS.equals(knowType)) {
            String getKeyWord = difyWorkApi.callAiCreateKeywordGetKeyWord(stringBuilder.toString(), collection.getFileUserId(), apiConfig.getGe_ren_cheng_guo_key_word_apikey());
            getGeRenChengGuoKeyWord(collection, getKeyWord);
        }

        if (KnowledgeType.CASE_DATABASE.equals(knowType)) {
            String getKeyWord = difyWorkApi.callAiCreateKeywordGetKeyWord(stringBuilder.toString(), collection.getFileUserId(), apiConfig.getBing_li_key_word_apikey());
            bingLiKuKeyWord(collection, getKeyWord);
        }

        collection.setDifyFileStatus(KnowledgeFileStatus.CHECK);
        knowledgeFileService.updateById(collection);

    }

    private void bingLiKuKeyWord(KnowledgeFile collection, String getKeyWord) {
        String LANGUAGE = "";
        String DIAGNOSTIC_RESULTS = "";
        Object AUTHOR = "";
        String JI_LU_TIME = "";
        String TREATMENT_PLAN = "";
        String KEY_SYMPTOMS = "";
        String CASE_TYPE = "";
        String TREATMENT_OUTCOME = "";
        String CASE_SOURCE = "";
        String POST_STATUS = "";
        String FOLLOW_UP_RESULTS = "";
        if (StrUtil.isNotEmpty(getKeyWord)) {
            JSONObject jsonObject = JSON.parseObject(getKeyWord);
            LANGUAGE = jsonObject.getString(KnowledgeLabelEnum.LANGUAGE.getCode());
            AUTHOR = jsonObject.get(KnowledgeLabelEnum.AUTHOR.getCode());
            JI_LU_TIME = jsonObject.getString(KnowledgeLabelEnum.JI_LU_TIME.getCode());
            DIAGNOSTIC_RESULTS = jsonObject.getString(KnowledgeLabelEnum.DIAGNOSTIC_RESULTS.getCode());

            TREATMENT_PLAN = jsonObject.getString(KnowledgeLabelEnum.TREATMENT_PLAN.getCode());
            KEY_SYMPTOMS = jsonObject.getString(KnowledgeLabelEnum.KEY_SYMPTOMS.getCode());
            CASE_TYPE = jsonObject.getString(KnowledgeLabelEnum.CASE_TYPE.getCode());
            TREATMENT_OUTCOME = jsonObject.getString(KnowledgeLabelEnum.TREATMENT_OUTCOME.getCode());
            CASE_SOURCE = jsonObject.getString(KnowledgeLabelEnum.CASE_SOURCE.getCode());
            POST_STATUS = jsonObject.getString(KnowledgeLabelEnum.POST_STATUS.getCode());
            FOLLOW_UP_RESULTS = jsonObject.getString(KnowledgeLabelEnum.FOLLOW_UP_RESULTS.getCode());

        }
        KnowledgeFileCaseDatabaseLabel build = KnowledgeFileCaseDatabaseLabel.build(collection);
        build.setLanguage(LANGUAGE);
        build.setCaseSource(CASE_SOURCE);
        build.setDiagnosticResults(DIAGNOSTIC_RESULTS);
        build.setKeySymptoms(KEY_SYMPTOMS);
        build.setJiLuTime(JI_LU_TIME);
        build.setTreatmentPlan(TREATMENT_PLAN);
        build.setCaseType(CASE_TYPE);
        build.setTreatmentOutcome(TREATMENT_OUTCOME);
        build.setPostStatus(POST_STATUS);
        build.setFollowUpResults(FOLLOW_UP_RESULTS);
        if (AUTHOR == null) {
            AUTHOR = "";
        }
        if (AUTHOR instanceof List) {
            JSONArray arrayAUTHOR = JSON.parseArray(AUTHOR.toString());
            List<String> listAUTHOR = new ArrayList<>();
            for (Object object : arrayAUTHOR) {
                listAUTHOR.add(object.toString());
            }
            build.setAuthor(String.join(",", listAUTHOR));
        } else if (AUTHOR instanceof String) {
            build.setAuthor(AUTHOR.toString());
        }
        KnowledgeFileCaseDatabaseLabel materialsLabel = knowledgeFileCaseDatabaseLabelMapper.selectOne(Wraps.<KnowledgeFileCaseDatabaseLabel>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .last(" limit 1 ")
                .eq(KnowledgeFileCaseDatabaseLabel::getFileId, collection.getId()));

        if (Objects.nonNull(materialsLabel)) {
            BeanUtils.copyProperties(build, materialsLabel);
            knowledgeFileCaseDatabaseLabelMapper.updateById(build);
        } else {
            knowledgeFileCaseDatabaseLabelMapper.insert(build);
        }

    }

    private void getGeRenChengGuoKeyWord(KnowledgeFile collection, String getKeyWord) {
        String LANGUAGE = "";
        String RELEASE_TIME = "";
        Object AUTHOR = "";
        String PERSONAL_ACHIEVEMENT_TYPE = "";
        String KEY_WORD = "";
        if (StrUtil.isNotEmpty(getKeyWord)) {
            JSONObject jsonObject = JSON.parseObject(getKeyWord);
            LANGUAGE = jsonObject.getString(KnowledgeLabelEnum.LANGUAGE.getCode());
            PERSONAL_ACHIEVEMENT_TYPE = jsonObject.getString(KnowledgeLabelEnum.PERSONAL_ACHIEVEMENT_TYPE.getCode());
            KEY_WORD = jsonObject.getString(KnowledgeLabelEnum.KEY_WORD.getCode());
            RELEASE_TIME = jsonObject.getString(KnowledgeLabelEnum.RELEASE_TIME.getCode());
            AUTHOR = jsonObject.get(KnowledgeLabelEnum.AUTHOR.getCode());

        }
        KnowledgeFilePersonalAchievementsLabel build = KnowledgeFilePersonalAchievementsLabel.build(collection);
        build.setLanguage(LANGUAGE);
        build.setReleaseTime(RELEASE_TIME);
        build.setPersonalAchievementType(PERSONAL_ACHIEVEMENT_TYPE);
        build.setKeyWord(KEY_WORD);

        if (AUTHOR == null) {
            AUTHOR = "";
        }
        if (AUTHOR instanceof List) {
            JSONArray arrayAUTHOR = JSON.parseArray(AUTHOR.toString());
            List<String> listAUTHOR = new ArrayList<>();
            for (Object object : arrayAUTHOR) {
                listAUTHOR.add(object.toString());
            }
            build.setAuthor(String.join(",", listAUTHOR));
        } else if (AUTHOR instanceof String) {
            build.setAuthor(AUTHOR.toString());
        }
        KnowledgeFilePersonalAchievementsLabel materialsLabel = knowledgeFilePersonalAchievementsLabelMapper.selectOne(Wraps.<KnowledgeFilePersonalAchievementsLabel>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .last(" limit 1 ")
                .eq(KnowledgeFilePersonalAchievementsLabel::getFileId, collection.getId()));

        if (Objects.nonNull(materialsLabel)) {
            BeanUtils.copyProperties(build, materialsLabel);
            knowledgeFilePersonalAchievementsLabelMapper.updateById(build);
        } else {
            knowledgeFilePersonalAchievementsLabelMapper.insert(build);
        }

    }


    /**
     * 专业学术资料要生产的关键词
     * @param collection
     */
    private void getZhuanYeXueShuKeyWord(KnowledgeFile collection, String getKeyWord) {
        String LANGUAGE = "";
        String RESEARCH_TYPE = "";
        String KEY_WORD = "";
        String RELEASE_TIME = "";
        String CONFERENCE_JOURNAL_NAME = "";
        Object AUTHOR = "";
        if (StrUtil.isNotEmpty(getKeyWord)) {
            JSONObject jsonObject = JSON.parseObject(getKeyWord);
            LANGUAGE = jsonObject.getString(KnowledgeLabelEnum.LANGUAGE.getCode());
            RESEARCH_TYPE = jsonObject.getString(KnowledgeLabelEnum.RESEARCH_TYPE.getCode());
            KEY_WORD = jsonObject.getString(KnowledgeLabelEnum.KEY_WORD.getCode());
            RELEASE_TIME = jsonObject.getString(KnowledgeLabelEnum.RELEASE_TIME.getCode());
            AUTHOR = jsonObject.get(KnowledgeLabelEnum.AUTHOR.getCode());
            CONFERENCE_JOURNAL_NAME = jsonObject.getString(KnowledgeLabelEnum.CONFERENCE_JOURNAL_NAME.getCode());
        }

        KnowledgeFileAcademicMaterialsLabel build = KnowledgeFileAcademicMaterialsLabel.build(collection);

        build.setLanguage(LANGUAGE);
        build.setResearchType(RESEARCH_TYPE);
        build.setKeyWord(KEY_WORD);
        build.setReleaseTime(RELEASE_TIME);
        build.setConferenceJournalName(CONFERENCE_JOURNAL_NAME);
        if (AUTHOR == null) {
            AUTHOR = "";
        }
        if (AUTHOR instanceof List) {
            JSONArray arrayAUTHOR = JSON.parseArray(AUTHOR.toString());
            List<String> listAUTHOR = new ArrayList<>();
            for (Object object : arrayAUTHOR) {
                listAUTHOR.add(object.toString());
            }
            build.setAuthor(String.join(",", listAUTHOR));
        } else if (AUTHOR instanceof String) {
            build.setAuthor(AUTHOR.toString());
        }
        KnowledgeFileAcademicMaterialsLabel materialsLabel = knowledgeFileAcademicMaterialsLabelMapper.selectOne(Wraps.<KnowledgeFileAcademicMaterialsLabel>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .last(" limit 1 ")
                .eq(KnowledgeFileAcademicMaterialsLabel::getFileId, collection.getId()));

        if (Objects.nonNull(materialsLabel)) {
            BeanUtils.copyProperties(build, materialsLabel);
            knowledgeFileAcademicMaterialsLabelMapper.updateById(build);
        } else {
            knowledgeFileAcademicMaterialsLabelMapper.insert(build);
        }

    }

}
