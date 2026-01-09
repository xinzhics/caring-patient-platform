package com.caring.sass.ai.know.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.know.KnowledgeFileSaveDTO;
import com.caring.sass.ai.entity.know.KnowFileType;
import com.caring.sass.ai.entity.know.KnowledgeType;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.know.KnowledgeFile;

import java.util.List;

/**
 * <p>
 * 业务接口
 * dify知识库文档关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
public interface KnowledgeFileService extends SuperService<KnowledgeFile> {


    void saveKnowledgeFile(KnowledgeType knowledgeType, List<KnowledgeFileSaveDTO> fileList);


    void deleteKnowledgeFile(Long fileId);


    void page(IPage<KnowledgeFile> builtPage, KnowledgeType knowledgeType, String query, Long userId, String userDomain);


    void saveKnowledgeFile(KnowledgeType knowledgeType, KnowFileType fileType, KnowledgeFileSaveDTO fileSaveDTO);


    void importKnowledgeFile(Long userId, KnowledgeType knowledgeType, List<KnowledgeFileSaveDTO> fileList);


    void initKnowledgeFileMetadata();


    void initKnowledgeFileDailyMetadata();


}
