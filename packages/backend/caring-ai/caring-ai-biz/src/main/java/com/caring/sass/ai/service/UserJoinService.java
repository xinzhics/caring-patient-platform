package com.caring.sass.ai.service;

import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.dto.userbiz.AiUserBizBo;
import com.caring.sass.ai.entity.UserJoin;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.base.service.SuperService;
import org.hibernate.validator.constraints.Length;

/**
 * <p>
 * 业务接口
 * ai用户关联表
 * </p>
 *
 * @author 杨帅
 * @date 2025-07-29
 */
public interface UserJoinService extends SuperService<UserJoin> {


    KnowledgeUser checkUserExist(String phone);

    JSONObject getAiStudioToken(Long userId, String tenantId);


    /**
     * 创建ai studio医生信息
     * @param accessToken
     * @param aiUserBizBo
     * @param phone
     * @param assistantId
     * @param menuDoamin
     * @param userDomain
     */
    JSONObject createAiStudioDoctor(String accessToken, AiUserBizBo aiUserBizBo, String phone, Long assistantId, String menuDoamin, String userDomain);

    /**
     * 修改ai studio医生信息
     * @param tenantId
     * @param aiUserBizBo
     * @param aiStudioDoctorId
     */
    void updateAiStudioDoctor(String tenantId, AiUserBizBo aiUserBizBo, Long aiStudioDoctorId);

    /**
     * 更新或者创建名片
     * @param userJoin
     * @param aiUserBizBo
     */
    void updateCreateOrUpdateBusinessCard(UserJoin userJoin, AiUserBizBo aiUserBizBo);

    /**
     * 更新知识库用户信息
     * @param aiUserBizBo
     */
    void updateKnowledgeUser(AiUserBizBo aiUserBizBo);


    void updateKnowledgeUser(KnowledgeUser knowledgeUser);

    void createKnowledgeUser(KnowledgeUser knowledgeUser, Boolean createKnowledge);


    boolean checkUserDomain(String pinyin);

    ArticleUser createArticleUser(String userMobile);


    void updateAiStudioDoctorId(Long aiKnowUserId, Boolean openFunction, Long aiStudioDoctorId);
}
