package com.caring.sass.ai.know.service;

import com.caring.sass.ai.entity.know.KnowledgeSystemFile;
import com.caring.sass.ai.entity.know.KnowledgeUserSubscribe;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 知识库用户
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
public interface KnowledgeUserService extends SuperService<KnowledgeUser> {



    void createKnowledgeUser(KnowledgeUser knowledgeUser, Boolean copyFile, Boolean createKnowledge);



    void uploadSystemFile(KnowledgeSystemFile systemFile);


    void initUserSystemFile();


    String createWxAuthUrl(String domain);

    void initMergePaper();

    void updateUserName(KnowledgeUser user);


    /**
     * 查询普通用户在这个域名下的会员信息
     * @param userId
     * @param domain
     * @return
     */
    KnowledgeUserSubscribe getKnowledgeUserSubscribe(Long userId, String domain, Boolean subscribeSwitch);


    List<KnowledgeUserSubscribe> subscribeList(LbqWrapper<KnowledgeUserSubscribe> eq);

    /**
     * 统计博主域名下的粉丝数
     * @param domain
     */
    Integer countFanNumber(String domain);


    String redirectDocKnowPayWxAuthUrl(String domain, String webSubDomain, String webPrimaryDomain);


    boolean updateCallDuration(Long userId, Integer minuteDuration);

}
