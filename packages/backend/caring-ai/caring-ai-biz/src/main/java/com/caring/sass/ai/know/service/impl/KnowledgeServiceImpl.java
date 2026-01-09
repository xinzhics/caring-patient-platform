package com.caring.sass.ai.know.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.config.DifyApiConfig;
import com.caring.sass.ai.know.dao.KnowledgeMapper;
import com.caring.sass.ai.know.model.DifyDocument;
import com.caring.sass.ai.know.model.DifyFilemodel;
import com.caring.sass.ai.know.service.KnowledgeDailyCollectionService;
import com.caring.sass.ai.know.service.KnowledgeFileService;
import com.caring.sass.ai.know.service.KnowledgeService;
import com.caring.sass.ai.know.util.DifyKnowApi;
import com.caring.sass.ai.know.util.DifyWorkApi;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * dify知识库关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@Slf4j
@Service

public class KnowledgeServiceImpl extends SuperServiceImpl<KnowledgeMapper, Knowledge> implements KnowledgeService {

    @Autowired
    DifyKnowApi difyKnowApi;

    @Autowired
    DifyWorkApi difyWorkApi;

    @Autowired
    DifyApiConfig difyApiConfig;

    @Autowired
    KnowledgeFileService knowledgeFileService;

    @Autowired
    KnowledgeDailyCollectionService knowledgeDailyCollectionService;

    /**
     * 默认 KnowledgeDailyCollection 的文本 存在日常收集的知识库中。
     * @param model
     */
    @Override
    public void createText(KnowledgeDailyCollection model) {
        // 查询业务表 日常收集的知识库名称
        Knowledge knowledge = getOne(Wraps.<Knowledge>lbQ()
                .eq(Knowledge::getKnowType, KnowledgeType.DAILY_COLLECTION)
                .eq(Knowledge::getKnowUserId, BaseContextHandler.getUserId())
                .last(" limit 1 ")
        );
        String categoryId = difyApiConfig.getMetadataCategoryId();
        String ownershipId = difyApiConfig.getMetadataOwnershipId();

        if (knowledge != null) {
            String dataset_id = difyApiConfig.getDifyKnowledgeId();
            String name = model.getTextTitle();
            String text = model.getTextContent();
            DifyFilemodel difyFilemodel = difyKnowApi.createByText(dataset_id, name, text);
            String batch = difyFilemodel.getBatch();
            DifyDocument document = difyFilemodel.getDocument();
            String documentId = document.getId();
            model.setDifyBatch(batch);
            model.setDocumentId(documentId);
            model.setKnowId(knowledge.getId());
            model.setKnowDifyId(dataset_id);
            model.setViewPermissions(knowledge.getViewPermissions());
            model.setDownloadPermission(knowledge.getDownloadPermission());
            model.setDifyFileStatus(KnowledgeFileStatus.WAITING);
            List<Map<String, String>> list;
            Map<String, String> map;
            // 设置文档的元数据
            list = new ArrayList<>();
            map = new HashMap<>();
            map.put("id", categoryId);
            map.put("name", "category");
            map.put("value", KnowledgeType.DAILY_COLLECTION.toString());
            list.add(map);

            map = new HashMap<>();
            map.put("id", ownershipId);
            map.put("name", "ownership");
            map.put("value", model.getUserId().toString());
            list.add(map);
            difyKnowApi.datasetsDocumentsMetadata(dataset_id, documentId, list);

        } else  {
            model.setDifyFileStatus(KnowledgeFileStatus.UPLOADING);
        }
        model.setFileUploadTime(LocalDateTime.now());
        model.setDifyFileIndexProgress("0");
        model.setIsUpdatePermissions(false);



    }


    /**
     * 当一个小程序用户升级成 博主时， 生成博主的知识库后，将他的日常收集上传到知识库。
     * @param userId
     * @param knowledge
     */
    public void updateMiniUserDailyCollection(Long userId, Knowledge knowledge) {
        List<KnowledgeDailyCollection> dailyCollectionList = knowledgeDailyCollectionService.list(Wraps.<KnowledgeDailyCollection>lbQ()
                .eq(KnowledgeDailyCollection::getUserId, userId)
                .eq(KnowledgeDailyCollection::getDifyFileStatus, KnowledgeFileStatus.UPLOADING));

        String dataset_id = difyApiConfig.getDifyKnowledgeId();
        String categoryId = difyApiConfig.getMetadataCategoryId();
        String ownershipId = difyApiConfig.getMetadataOwnershipId();

        for (KnowledgeDailyCollection model : dailyCollectionList) {
            String name = model.getTextTitle();
            String text = model.getTextContent();
            DifyFilemodel difyFilemodel = difyKnowApi.createByText(dataset_id, name, text);
            String batch = difyFilemodel.getBatch();
            DifyDocument document = difyFilemodel.getDocument();
            String documentId = document.getId();
            model.setDifyBatch(batch);
            model.setDocumentId(documentId);
            model.setKnowId(knowledge.getId());
            model.setKnowDifyId(knowledge.getKnowDifyId());
            model.setViewPermissions(knowledge.getViewPermissions());
            model.setDownloadPermission(knowledge.getDownloadPermission());
            model.setDifyFileStatus(KnowledgeFileStatus.WAITING);
            knowledgeDailyCollectionService.updateById(model);

            List<Map<String, String>> list;
            Map<String, String> map;
            // 设置文档的元数据
            list = new ArrayList<>();
            map = new HashMap<>();
            map.put("id", ownershipId);
            map.put("name", "ownership");
            map.put("value", model.getUserId().toString());
            list.add(map);

            map = new HashMap<>();
            map.put("id", categoryId);
            map.put("name", "category");
            map.put("value", KnowledgeType.DAILY_COLLECTION.toString());
            list.add(map);
            difyKnowApi.datasetsDocumentsMetadata(knowledge.getKnowDifyId(), documentId, list);

        }


    }


    /**
     * 通过文本修改文档
     * @param documentId 文档ID
     * @param knowId 业务知识库ID
     * @param model 数据
     */
    @Override
    public void updateText(String documentId, Long knowId, KnowledgeDailyCollection model) {

        Knowledge knowledge = baseMapper.selectById(knowId);
        if (knowledge == null) {
            throw new BizException("用户没有日常收集知识库");
        }

        DifyFilemodel difyFilemodel = difyKnowApi.updateByText(knowledge.getKnowDifyId(), documentId, model.getTextTitle(), model.getTextContent());
        String batch = difyFilemodel.getBatch();
        model.setDifyBatch(batch);
        model.setKnowId(knowledge.getId());
        model.setDifyFileStatus(KnowledgeFileStatus.WAITING);
        model.setDifyFileIndexProgress("0");
    }

    /**
     * 调用AI创建文本的标题
     * @param textContent
     * @return
     */
    @Override
    public String getTitle(String textContent) {

        return difyWorkApi.callAiCreateTitle(textContent, BaseContextHandler.getUserId());
    }


    /**
     * 删除知识库的文档
     * @param documentId
     * @param knowId
     */
    @Override
    public void deleteKnowledgeFile(String documentId, Long knowId) {

        Knowledge knowledge = baseMapper.selectById(knowId);
        if (Objects.isNull(knowledge)) {
            throw new BizException("知识库不存在");
        }
        difyKnowApi.deleteKnowledgeFile(knowledge.getKnowDifyId(), documentId);
    }


    /**
     * 更新知识库和知识库内 未自定义权限的文档的权限
     * @param knowledgeList 前端传来的知识库数据
     * @param oldKnowList   数据库的旧数据
     */
    @Transactional
    @Override
    public void updatePermission(List<Knowledge> knowledgeList, List<Knowledge> oldKnowList) {

        Map<KnowledgeType, Knowledge> knowledgeTypeListMap = knowledgeList.stream()
                .collect(Collectors.toMap(Knowledge::getKnowType, item -> item, (o1, o2) -> o1));

        for (Knowledge knowledge : oldKnowList) {
            Knowledge newKnowledge = knowledgeTypeListMap.get(knowledge.getKnowType());
            Integer viewPermissions = knowledge.getViewPermissions();
            Integer downloadPermission = knowledge.getDownloadPermission();

            // 当 查询权限 或 下载权限变更时， 要更新数据库 和 知识库内文档的权限。
            if (!viewPermissions.equals(newKnowledge.getViewPermissions()) || !downloadPermission.equals(newKnowledge.getDownloadPermission())) {

                knowledge.setViewPermissions(newKnowledge.getViewPermissions());
                knowledge.setDownloadPermission(newKnowledge.getDownloadPermission());

                baseMapper.updateById(knowledge);
                // 更新知识库内的文档
                if (knowledge.getKnowType().equals(KnowledgeType.DAILY_COLLECTION)) {
                    UpdateWrapper<KnowledgeDailyCollection> wrapper = new UpdateWrapper<KnowledgeDailyCollection>();
                    wrapper.set("view_permissions", newKnowledge.getViewPermissions());
                    wrapper.set("download_permission", newKnowledge.getDownloadPermission());
                    wrapper.eq("is_update_permissions", false);
                    wrapper.eq("know_id", newKnowledge.getId());
                    knowledgeDailyCollectionService.update(wrapper);
                } else {
                    UpdateWrapper<KnowledgeFile> wrapper = new UpdateWrapper<KnowledgeFile>();
                    wrapper.set("view_permissions", newKnowledge.getViewPermissions());
                    wrapper.set("download_permission", newKnowledge.getDownloadPermission());
                    wrapper.eq("is_update_permissions", false);
                    wrapper.eq("know_id", newKnowledge.getId());
                    knowledgeFileService.update(wrapper);
                }
            }
        }



    }

    /**
     * 给用户生成四个知识库在dify
     * @param knowledgeUser
     * @return
     */
    @Override
    public boolean initKnowledge(KnowledgeUser knowledgeUser,  boolean syncMiniUserData) {
        String userName = knowledgeUser.getUserName();

        Knowledge knowledge = new Knowledge();
        knowledge.setKnowName(userName + "_专业学术资料");
        knowledge.setKnowType(KnowledgeType.ACADEMIC_MATERIALS);
        knowledge.setKnowUserId(knowledgeUser.getId());
//        ACADEMIC_MATERIALS = difyKnowApi.initKnowledge(userName + "_专业学术资料");
        knowledge.setKnowDifyId(difyApiConfig.getDifyKnowledgeId());
        knowledge.setViewPermissions(2);
        knowledge.setDownloadPermission(3);


        Knowledge knowledge1 = new Knowledge();
        knowledge1.setKnowName(userName + "_个人成果");
        knowledge1.setKnowType(KnowledgeType.PERSONAL_ACHIEVEMENTS);
        knowledge1.setKnowUserId(knowledgeUser.getId());
//        PERSONAL_ACHIEVEMENTS = difyKnowApi.initKnowledge(userName + "_个人成果");
        knowledge1.setKnowDifyId(difyApiConfig.getDifyKnowledgeId());
        knowledge1.setViewPermissions(2);
        knowledge1.setDownloadPermission(3);


        Knowledge knowledge2 = new Knowledge();
        knowledge2.setKnowName(userName + "_病例库");
        knowledge2.setKnowType(KnowledgeType.CASE_DATABASE);
        knowledge2.setKnowUserId(knowledgeUser.getId());
//        CASE_DATABASE = difyKnowApi.initKnowledge(userName + "_病例库");
        knowledge2.setKnowDifyId(difyApiConfig.getDifyKnowledgeId());
        knowledge2.setViewPermissions(3);
        knowledge2.setDownloadPermission(3);

        Knowledge knowledge3 = new Knowledge();
        knowledge3.setKnowName(userName + "_日常收集");
        knowledge3.setKnowType(KnowledgeType.DAILY_COLLECTION);
        knowledge3.setKnowUserId(knowledgeUser.getId());
//        DAILY_COLLECTION = difyKnowApi.initKnowledge(userName + "_日常收集");
        knowledge3.setKnowDifyId(difyApiConfig.getDifyKnowledgeId());
        knowledge2.setViewPermissions(4);
        knowledge2.setDownloadPermission(4);

        // 检查四个知识库 是否都生成。 否则 则回滚已经存在的知识库。
        save(knowledge);
        save(knowledge1);
        save(knowledge2);
        save(knowledge3);
        // 由于之前是点记小程序用户。，用户升级为博主时，将用户的日常收集数据，同步到知识库。
        if (syncMiniUserData) {
            updateMiniUserDailyCollection(knowledgeUser.getId(), knowledge3);
        }
        return true;
    }


    @Override
    public void initKnowledgeMetadata() {

        // 查询所有的知识库列表
        List<Knowledge> knowledgeList = baseMapper.selectList(Wraps.<Knowledge>lbQ().isNotNull(Knowledge::getKnowDifyId));
        for (Knowledge knowledge : knowledgeList) {

            String knowDifyId = knowledge.getKnowDifyId();
            String ownership = difyKnowApi.datasetsMetadata(knowDifyId, "ownership");
            knowledge.setMetadataOwnershipId(ownership);
            String category = difyKnowApi.datasetsMetadata(knowDifyId, "category");
            knowledge.setMetadataCategoryId(category);
            baseMapper.updateById(knowledge);
        }
    }




}
