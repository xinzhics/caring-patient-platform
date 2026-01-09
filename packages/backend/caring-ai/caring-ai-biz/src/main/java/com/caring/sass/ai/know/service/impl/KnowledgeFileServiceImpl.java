package com.caring.sass.ai.know.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.know.KnowledgeFileSaveDTO;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.config.DifyApiConfig;
import com.caring.sass.ai.know.dao.*;
import com.caring.sass.ai.know.model.DifyDocument;
import com.caring.sass.ai.know.model.DifyFilemodel;
import com.caring.sass.ai.know.model.KnowledgeFileQuery;
import com.caring.sass.ai.know.service.KnowledgeFileService;
import com.caring.sass.ai.know.service.KnowledgeService;
import com.caring.sass.ai.know.util.DifyKnowApi;
import com.caring.sass.ai.know.util.DifyWorkApi;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * dify知识库文档关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@Slf4j
@Service

public class KnowledgeFileServiceImpl extends SuperServiceImpl<KnowledgeFileMapper, KnowledgeFile> implements KnowledgeFileService {


    @Autowired
    KnowledgeService knowledgeService;


    @Autowired
    KnowledgeFileAcademicMaterialsLabelMapper academicMaterialsLabelMapper;

    @Autowired
    KnowledgeFilePersonalAchievementsLabelMapper personalAchievementsLabelMapper;

    @Autowired
    KnowledgeFileCaseDatabaseLabelMapper caseDatabaseLabelMapper;

    @Autowired
    KnowledgeUserMapper knowledgeUserMapper;

    @Autowired
    DifyKnowApi difyKnowApi;

    @Autowired
    KnowledgeDailyCollectionMapper dailyCollectionMapper;

    @Autowired
    DifyWorkApi difyWorkApi;

    @Autowired
    DifyApiConfig difyApiConfig;

    @Override
    public void deleteKnowledgeFile(Long fileId) {

        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserMapper.selectById(userId);
        KnowledgeFile knowledgeFile = baseMapper.selectById(fileId);
        if (knowledgeFile != null) {
            if (knowledgeUser == null || KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
                throw new BizException("权限不足");
            }
            // 系统文件不需要删除dify， 直接删除业务上的信息
            if (KnowledgeFile.DOCUMENT_BELONGS_SYSTEM.equals(knowledgeFile.getDocumentBelongs())) {
                baseMapper.deleteById(fileId);
                academicMaterialsLabelMapper.delete(Wraps.<KnowledgeFileAcademicMaterialsLabel>lbQ().eq(KnowledgeFileAcademicMaterialsLabel::getFileId, fileId));
                personalAchievementsLabelMapper.delete(Wraps.<KnowledgeFilePersonalAchievementsLabel>lbQ().eq(KnowledgeFilePersonalAchievementsLabel::getFileId, fileId));
                caseDatabaseLabelMapper.delete(Wraps.<KnowledgeFileCaseDatabaseLabel>lbQ().eq(KnowledgeFileCaseDatabaseLabel::getFileId, fileId));
                return;
            }
            // 删除dify中的文件
            difyKnowApi.deleteKnowledgeFile(knowledgeFile.getKnowDifyId(), knowledgeFile.getDifyFileId());
            // 删除本地文件
            baseMapper.deleteById(fileId);
            academicMaterialsLabelMapper.delete(Wraps.<KnowledgeFileAcademicMaterialsLabel>lbQ().eq(KnowledgeFileAcademicMaterialsLabel::getFileId, fileId));
            personalAchievementsLabelMapper.delete(Wraps.<KnowledgeFilePersonalAchievementsLabel>lbQ().eq(KnowledgeFilePersonalAchievementsLabel::getFileId, fileId));
            caseDatabaseLabelMapper.delete(Wraps.<KnowledgeFileCaseDatabaseLabel>lbQ().eq(KnowledgeFileCaseDatabaseLabel::getFileId, fileId));
        }

    }

    /**
     * 上传其他类型的文件。但是不上传到 dify
     * @param knowledgeType
     * @param fileType
     * @param fileSaveDTO
     */
    @Override
    public void saveKnowledgeFile(KnowledgeType knowledgeType, KnowFileType fileType, KnowledgeFileSaveDTO fileSaveDTO) {

        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserMapper.selectById(userId);
        if (knowledgeUser == null || KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
            throw new BizException("权限不足");
        }
        // 查询用户 这个知识库类型的 知识库信息
        Knowledge knowledge = knowledgeService.getOne(Wraps.<Knowledge>lbQ()
                .eq(Knowledge::getKnowType, knowledgeType)
                .eq(Knowledge::getKnowUserId, userId));

        if (KnowFileType.VIDEO_FILE.equals(fileType)) {
            KnowledgeFile file = new KnowledgeFile();
            file.setFileName(fileSaveDTO.getFileName());
            file.setFileUserId(BaseContextHandler.getUserId());
            file.setFileSize(fileSaveDTO.getFileSize());
            file.setFileCover(fileSaveDTO.getFileCover());
            file.setFileUrl(fileSaveDTO.getFileUrl());
            file.setFileType(fileType);

            file.setInitialFileName(file.getFileName());
            file.setDifyFileStatus(KnowledgeFileStatus.CHECKED);
            file.setFileUploadTime(LocalDateTime.now());
            file.setKnowId(knowledge.getId());
            file.setKnowType(knowledgeType);
            file.setViewPermissions(knowledge.getViewPermissions());
            file.setDownloadPermission(knowledge.getDownloadPermission());
            file.setDocumentBelongs(KnowledgeFile.DOCUMENT_BELONGS_USER);
            file.setIsUpdatePermissions(false);

            baseMapper.insert(file);
        }


    }


    @Override
    public void page(IPage<KnowledgeFile> builtPage, KnowledgeType knowledgeType, String query, Long userId, String userDomain) {

//        KnowledgeUser knowledgeUser = knowledgeUserMapper.selectById(userId);
//        Integer viewPermissions = knowledgeUser.getViewPermissions();
//        if (knowledgeUser.getUserType().equals(KnowDoctorType.GENERAL_PRACTITIONER)) {
//            // 检验普通医生的会员权限是否已经到期， 会员到期，只能看免费内容
//            if (knowledgeUser.getMembershipExpiration() == null || knowledgeUser.getMembershipExpiration().isBefore(LocalDateTime.now())) {
//                viewPermissions = 1;
//            }
//            KnowledgeUser selectedOne = knowledgeUserMapper.selectOne(Wraps.<KnowledgeUser>lbQ()
//                    .eq(KnowledgeUser::getUserDomain, userDomain)
//                    .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
//                    .last(" limit 1 "));
//            if (Objects.isNull(selectedOne)) {
//                return;
//            }
//            userId = selectedOne.getId();
//        }

        KnowledgeUser selectedOne = knowledgeUserMapper.selectOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserDomain, userDomain)
                    .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                    .last(" limit 1 "));
        userId = selectedOne.getId();

        // 专业学术资料
        KnowledgeFileQuery fileQuery = null;

        LbqWrapper<KnowledgeFile> wrapper;
        if (knowledgeType.equals(KnowledgeType.ACADEMIC_MATERIALS) ) {
            wrapper = Wraps.<KnowledgeFile>lbQ()
                    .eq(KnowledgeFile::getKnowType, knowledgeType)
                    .eq(KnowledgeFile::getFileUserId, userId);
        } else {
            wrapper = Wraps.<KnowledgeFile>lbQ()
                    .eq(KnowledgeFile::getKnowType, knowledgeType)
                    .eq(KnowledgeFile::getFileUserId, userId);
        }

        if (StrUtil.isNotBlank(query)) {
            fileQuery = new KnowledgeFileQuery();
            fileQuery.buildLikeWrapper(knowledgeType, userId, wrapper, query);

        }
        // 增加 普通 用户的权限条件。
//        if (knowledgeUser.getUserType().equals(KnowDoctorType.GENERAL_PRACTITIONER)) {
//            wrapper.le(KnowledgeFile::getViewPermissions, viewPermissions);
//        }

        baseMapper.selectPage(builtPage, wrapper);

        List<KnowledgeFile> records = builtPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<Long> longList = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
        Map<Long, KnowledgeFile> knowledgeFileMap = records.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
        if (knowledgeType.equals(KnowledgeType.ACADEMIC_MATERIALS) ) {
            for (KnowledgeFileAcademicMaterialsLabel materialsLabel : academicMaterialsLabelMapper.selectList(Wraps.<KnowledgeFileAcademicMaterialsLabel>lbQ()
                    .in(KnowledgeFileAcademicMaterialsLabel::getFileId, longList))) {
                KnowledgeFile file = knowledgeFileMap.get(materialsLabel.getFileId());
                if (Objects.nonNull(file)) {
                    file.setAcademicMaterialsLabel(materialsLabel);
                }
            }
            // 设置病例的 标签
        } else if (KnowledgeType.CASE_DATABASE.equals(knowledgeType)) {
            List<KnowledgeFileCaseDatabaseLabel> caseDatabaseLabels = caseDatabaseLabelMapper.selectList(Wraps.<KnowledgeFileCaseDatabaseLabel>lbQ().in(KnowledgeFileCaseDatabaseLabel::getFileId, longList));
            for (KnowledgeFileCaseDatabaseLabel databaseLabel : caseDatabaseLabels) {
                KnowledgeFile file = knowledgeFileMap.get(databaseLabel.getFileId());
                if (Objects.nonNull(file)) {
                    file.setCaseDatabaseLabel(databaseLabel);
                }
            }
            // 设置文档的个人成果的标签
        } else if (KnowledgeType.PERSONAL_ACHIEVEMENTS.equals(knowledgeType)) {
            for (KnowledgeFilePersonalAchievementsLabel achievementsLabel : personalAchievementsLabelMapper.selectList(Wraps.<KnowledgeFilePersonalAchievementsLabel>lbQ().in(KnowledgeFilePersonalAchievementsLabel::getFileId, longList))) {
                KnowledgeFile file = knowledgeFileMap.get(achievementsLabel.getFileId());
                if (Objects.nonNull(file)) {
                    file.setPersonalAchievementsLabel(achievementsLabel);
                }
            }
        }


    }

    /**
     * 保存文件到本地。
     * 上传给dify进行解析
     *
     * @param knowledgeType
     * @param fileList
     */
    @Override
    public void saveKnowledgeFile(KnowledgeType knowledgeType, List<KnowledgeFileSaveDTO> fileList) {

        if (CollUtil.isEmpty(fileList)) {
            return;
        }
        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserMapper.selectById(userId);
        if (knowledgeUser == null || KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
            throw new BizException("权限不足");
        }

        importKnowledgeFile(userId, knowledgeType, fileList);
    }

    /**
     * 导入知识库文件
     * @param userId
     * @param knowledgeType
     * @param fileList
     */
    @Override
    public void importKnowledgeFile(Long userId, KnowledgeType knowledgeType, List<KnowledgeFileSaveDTO> fileList) {
        // 查询用户 这个知识库类型的 知识库信息
        Knowledge knowledge = knowledgeService.getOne(Wraps.<Knowledge>lbQ()
                .eq(Knowledge::getKnowType, knowledgeType)
                .eq(Knowledge::getKnowUserId, userId));
        List<KnowledgeFile> knowledgeFiles = new ArrayList<>();

        String difyKnowledgeId = difyApiConfig.getDifyKnowledgeId();
        fileList.forEach(file -> {
            KnowledgeFile knowledgeFile = new KnowledgeFile();
            BeanUtils.copyProperties(file, knowledgeFile);
            knowledgeFile.setFileUserId(userId);
            knowledgeFile.setInitialFileName(file.getFileName());
            knowledgeFile.setDifyFileStatus(KnowledgeFileStatus.UPLOADING);
            knowledgeFile.setFileUploadTime(LocalDateTime.now());
            knowledgeFile.setKnowId(knowledge.getId());
            knowledgeFile.setKnowType(knowledgeType);
            knowledgeFile.setKnowDifyId(difyKnowledgeId);
            knowledgeFile.setViewPermissions(knowledge.getViewPermissions());
            knowledgeFile.setDownloadPermission(knowledge.getDownloadPermission());
            knowledgeFile.setDocumentBelongs(KnowledgeFile.DOCUMENT_BELONGS_USER);
            knowledgeFile.setIsUpdatePermissions(false);
            knowledgeFile.setTryCount(0);
            knowledgeFiles.add(knowledgeFile);
        });

        // 保存到数据库。
        baseMapper.insertBatchSomeColumn(knowledgeFiles);

        // 异步 下载文件， 然后上传到dify去
        SaasGlobalThreadPool.execute(() -> callAiCreateFile(knowledgeFiles));
    }


    /**
     * 文件上传失败的怎么办。。
     *
     * @param knowledgeFiles
     */
    public void callAiCreateFile(List<KnowledgeFile> knowledgeFiles) {
        List<Map<String, String>> list;
        Map<String, String> map;
        String difyKnowledgeId = difyApiConfig.getDifyKnowledgeId();
        String categoryId = difyApiConfig.getMetadataCategoryId();
        String ownershipId = difyApiConfig.getMetadataOwnershipId();

        for (KnowledgeFile file : knowledgeFiles) {

            DifyFilemodel difyFilemodel = null;
            difyFilemodel = difyKnowApi.createByFile(file);
            if (difyFilemodel == null) {
                file.setDifyFileStatus(KnowledgeFileStatus.FAIL);
                baseMapper.updateById(file);
            } else {
                DifyDocument document = difyFilemodel.getDocument();
                file.setDifyFileId(document.getId());
                file.setDifyFileIndexProgress("0");
                String indexingStatus = document.getIndexing_status();
                if ("waiting".equals(indexingStatus)) {
                    file.setDifyFileStatus(KnowledgeFileStatus.WAITING);
                } else if ("indexing".equals(indexingStatus) || "parsing".equals(indexingStatus)) {
                    file.setDifyFileStatus(KnowledgeFileStatus.ANALYSIS);
                } else if ("completed".equals(indexingStatus)) {
                    file.setDifyFileStatus(KnowledgeFileStatus.CHECK);
                } else {
                    file.setDifyFileStatus(KnowledgeFileStatus.WAITING);
                }
                file.setDifyBatch(difyFilemodel.getBatch());

                // 设置文档的元数据
                list = new ArrayList<>();
                map = new HashMap<>();
                map.put("id", ownershipId);
                map.put("name", "ownership");
                map.put("value", file.getFileUserId().toString());
                list.add(map);

                map = new HashMap<>();
                map.put("id", categoryId);
                map.put("name", "category");
                map.put("value", file.getKnowType().toString());
                list.add(map);
                String difyFileId = file.getDifyFileId();
                difyKnowApi.datasetsDocumentsMetadata(difyKnowledgeId, difyFileId, list);

                file.setSetMetadata(true);
                baseMapper.updateById(file);
            }
        }

    }

    public void initKnowledgeFileDailyMetadata() {

        List<KnowledgeDailyCollection> dailyCollections = dailyCollectionMapper.selectList(Wraps.<KnowledgeDailyCollection>lbQ()
                .isNotNull(KnowledgeDailyCollection::getDocumentId));
        String difyKnowledgeId = difyApiConfig.getDifyKnowledgeId();
        String categoryId = difyApiConfig.getMetadataCategoryId();
        String ownershipId = difyApiConfig.getMetadataOwnershipId();
        String datasetId;
        List<Map<String, String>> list;
        Map<String, String> map;
        String metadataCategoryId;
        String metadataOwnershipId;
        for (KnowledgeDailyCollection file : dailyCollections) {
            String knowDifyId = file.getKnowDifyId();
            // 发现文件是上传到公共库中了。那么使用公共库的参数来设置元数据
            if (knowDifyId.equals(difyKnowledgeId)) {
                metadataCategoryId = categoryId;
                metadataOwnershipId = ownershipId;
                datasetId = difyKnowledgeId;
            } else {
                Knowledge knowledge = knowledgeService.getById(file.getKnowId());
                if (knowledge == null) {
                    continue;
                }
                metadataCategoryId = knowledge.getMetadataCategoryId();
                metadataOwnershipId = knowledge.getMetadataOwnershipId();
                datasetId = knowledge.getKnowDifyId();
            }
            list = new ArrayList<>();
            map = new HashMap<>();
            map.put("id", metadataOwnershipId);
            map.put("name", "ownership");
            map.put("value", file.getUserId().toString());
            list.add(map);

            map = new HashMap<>();
            map.put("id", metadataCategoryId);
            map.put("name", "category");
            map.put("value", KnowledgeType.DAILY_COLLECTION.toString());
            list.add(map);
            String difyFileId = file.getDocumentId();
            difyKnowApi.datasetsDocumentsMetadata(datasetId, difyFileId, list);
        }


    }


    /**
     * 初始化已有文档的元数据
     */
    @Override
    public void initKnowledgeFileMetadata() {


        List<Knowledge> knowledgeList = knowledgeService.list(Wraps.<Knowledge>lbQ());

        String difyKnowledgeId = difyApiConfig.getDifyKnowledgeId();
        String categoryId = difyApiConfig.getMetadataCategoryId();
        String ownershipId = difyApiConfig.getMetadataOwnershipId();

        List<Map<String, String>> list;
        Map<String, String> map;
        String datasetId;
        String metadataCategoryId;
        String metadataOwnershipId;
        for (Knowledge knowledge : knowledgeList) {

            if (knowledge.getKnowType().equals(KnowledgeType.DAILY_COLLECTION)) {
//                List<KnowledgeDailyCollection> dailyCollections = dailyCollectionMapper.selectList(Wraps.<KnowledgeDailyCollection>lbQ()
//                        .isNotNull(KnowledgeDailyCollection::getDocumentId)
//                        .eq(KnowledgeDailyCollection::getKnowId, knowledge.getId()));
//                if (CollUtil.isEmpty(dailyCollections)) {
//                    continue;
//                }
//                for (KnowledgeDailyCollection file : dailyCollections) {
//                    String knowDifyId = file.getKnowDifyId();
//                    // 发现文件是上传到公共库中了。那么使用公共库的参数来设置元数据
//                    if (knowDifyId.equals(difyKnowledgeId)) {
//                        metadataCategoryId = categoryId;
//                        metadataOwnershipId = ownershipId;
//                        datasetId = difyKnowledgeId;
//                    } else {
//                        metadataCategoryId = knowledge.getMetadataCategoryId();
//                        metadataOwnershipId = knowledge.getMetadataOwnershipId();
//                        datasetId = knowledge.getKnowDifyId();
//                    }
//                    list = new ArrayList<>();
//                    map = new HashMap<>();
//                    map.put("id", metadataOwnershipId);
//                    map.put("name", "ownership");
//                    map.put("value", file.getUserId().toString());
//                    list.add(map);
//
//                    map = new HashMap<>();
//                    map.put("id", metadataCategoryId);
//                    map.put("name", "category");
//                    map.put("value", KnowledgeType.DAILY_COLLECTION.toString());
//                    list.add(map);
//                    String difyFileId = file.getDocumentId();
//                    difyKnowApi.datasetsDocumentsMetadata(datasetId, difyFileId, list);
//                }
            } else {
                while (true) {

                    List<KnowledgeFile> knowledgeFiles = baseMapper.selectList(Wraps.<KnowledgeFile>lbQ()
                            .eq(KnowledgeFile::getSetMetadata, false)
                            .eq(KnowledgeFile::getKnowId, knowledge.getId())
                            .isNotNull(KnowledgeFile::getDifyFileId)
                            .last("limit 0, 100"));
                    if (CollUtil.isEmpty(knowledgeFiles)) {
                        break;
                    }
                    for (KnowledgeFile file : knowledgeFiles) {
                        String knowDifyId = file.getKnowDifyId();
                        // 发现文件是上传到公共库中了。那么使用公共库的参数来设置元数据
                        if (knowDifyId == null || knowDifyId.equals(difyKnowledgeId)) {
                            metadataCategoryId = categoryId;
                            metadataOwnershipId = ownershipId;
                            datasetId = difyKnowledgeId;
                        } else {
                            metadataCategoryId = knowledge.getMetadataCategoryId();
                            metadataOwnershipId = knowledge.getMetadataOwnershipId();
                            datasetId = knowledge.getKnowDifyId();
                        }

                        list = new ArrayList<>();
                        map = new HashMap<>();
                        map.put("id", metadataOwnershipId);
                        map.put("name", "ownership");
                        map.put("value", file.getFileUserId().toString());
                        list.add(map);

                        map = new HashMap<>();
                        map.put("id", metadataCategoryId);
                        map.put("name", "category");
                        map.put("value", file.getKnowType().toString());
                        list.add(map);
                        String difyFileId = file.getDifyFileId();
                        difyKnowApi.datasetsDocumentsMetadata(datasetId, difyFileId, list);

                        file.setSetMetadata(true);
                        baseMapper.updateById(file);
                    }
                }
            }
        }
    }
}
