package com.caring.sass.ai.know.service;

import com.caring.sass.ai.entity.know.KnowledgeDailyCollection;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.know.Knowledge;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * <p>
 * 业务接口
 * dify知识库关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
public interface KnowledgeService extends SuperService<Knowledge> {


    /**
     * 将日常收集小程序提交的文本存到dify知识库
     * @param model
     */
    void createText(KnowledgeDailyCollection model);


    /**
     * 初始化用户的知识库
     * @param knowledgeUser
     * @return
     */
    boolean initKnowledge(KnowledgeUser knowledgeUser,  boolean syncMiniUserData);


    String getTitle(String textContent);


    /**
     * 调用知识库删除文件
     * @param documentId
     */
    void deleteKnowledgeFile(String documentId, Long knowId );

    /**
     * 通过文本更新文档
     * @param documentId 文档ID
     * @param knowId 业务知识库ID
     * @param model 数据
     */
    void updateText(String documentId, Long knowId, KnowledgeDailyCollection model);

    /**
     * 更新知识库权限
     * @param knowledgeList
     * @param oldKnowList
     */
    void updatePermission(List<Knowledge> knowledgeList, List<Knowledge> oldKnowList);

    /**
     * 查询本地的已经存在的知识库
     * 给知识库新建元数据。并将知识库下的文件设置元数据标签
     */
    void initKnowledgeMetadata();


}
