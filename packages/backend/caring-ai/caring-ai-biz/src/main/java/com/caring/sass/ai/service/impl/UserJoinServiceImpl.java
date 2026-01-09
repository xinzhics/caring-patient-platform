package com.caring.sass.ai.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.article.service.ArticleUserService;
import com.caring.sass.ai.card.service.BusinessCardService;
import com.caring.sass.ai.dao.UserJoinMapper;
import com.caring.sass.ai.dto.userbiz.AiUserBizBo;
import com.caring.sass.ai.entity.UserJoin;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import com.caring.sass.ai.entity.know.KnowDoctorType;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.entity.know.KnowledgeUserQualification;
import com.caring.sass.ai.know.dao.KnowledgeUserQualificationMapper;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.ai.service.UserJoinService;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * ai用户关联表
 * </p>
 *
 * @author 杨帅
 * @date 2025-07-29
 */
@Slf4j
@Service

public class UserJoinServiceImpl extends SuperServiceImpl<UserJoinMapper, UserJoin> implements UserJoinService {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static final String domain = "capi.allercura.cn";


    @Autowired
    BusinessCardService businessCardService;


    @Autowired
    KnowledgeUserService knowledgeUserService;

    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    KnowledgeUserQualificationMapper knowledgeUserQualificationMapper;


    @Override
    public void updateAiStudioDoctorId(Long aiKnowUserId, Boolean openFunction, Long aiStudioDoctorId) {

        KnowledgeUser knowledgeUser = knowledgeUserService.getById(aiKnowUserId);
        if (!openFunction) {
            knowledgeUser.setAiStudioDoctorId(null);
            knowledgeUserService.updateAllById(knowledgeUser);
            return;
        }

        if (Objects.nonNull(knowledgeUser) && Objects.nonNull(aiStudioDoctorId)) {
            knowledgeUser.setAiStudioDoctorId(aiStudioDoctorId.toString());
            knowledgeUserService.updateById(knowledgeUser);
        }

    }

    /**
     * 创建科普创作用户
     * @param userMobile
     * @return
     */
    @Override
    public ArticleUser createArticleUser(String userMobile) {

        try {
            ArticleUser one = articleUserService.getOne(Wraps.<ArticleUser>lbQ()
                    .eq(ArticleUser::getUserMobile, EncryptionUtil.encrypt(userMobile)));
            if (Objects.nonNull(one)) {
                if (one.getPassword() == null) {
                    one.setPassword(SecureUtil.md5("123456asd"));
                    articleUserService.updateById(one);
                }
                return one;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ArticleUser articleUser = new ArticleUser();
        articleUser.setUserMobile(userMobile);
        articleUser.setPassword(SecureUtil.md5("123456asd"));
        articleUserService.save(articleUser);
        return articleUser;
    }

    @Override
    public boolean checkUserDomain(String pinyin) {
        LbqWrapper<KnowledgeUser> lbqWrapper = null;
        try {
            lbqWrapper = Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserDomain, pinyin)
                    .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                    .last(" limit 0,1 ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        KnowledgeUser user = null;
        try {
            user = knowledgeUserService.getOne(lbqWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user == null;
    }

    @Override
    public KnowledgeUser checkUserExist(String phone) {
        LbqWrapper<KnowledgeUser> lbqWrapper = null;
        try {
            lbqWrapper = Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(phone))
                    .in(KnowledgeUser::getUserType, KnowDoctorType.MINI_USER, KnowDoctorType.CHIEF_PHYSICIAN)
                    .last(" limit 0,1 ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        KnowledgeUser user = null;
        try {
            user = knowledgeUserService.getOne(lbqWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;

    }

    /**
     * 获取ai工作室token
     * @return
     */
    @Override
    public JSONObject getAiStudioToken(Long userId, String tenantId) {

        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        // 请求 URL
        String url = "https://"+domain+"/auth/docuknow";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("tenantId", tenantId);
        jsonObject.put("clientId", System.getenv().getOrDefault("AI_WORKSHOP_CLIENT_ID", ""));
        jsonObject.put("clientSecret", System.getenv().getOrDefault("AI_WORKSHOP_CLIENT_SECRET", ""));


        RequestBody body = RequestBody.create(JSON, jsonObject.toJSONString());

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("clientid", "f72c4ec9da59a38fec6c8991c68daadb")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Host", domain)
                .addHeader("Connection", "keep-alive")
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (response.isSuccessful() && response.body() != null) {
                System.out.println("Response Code: " + response.code());
                return JSONObject.parseObject(responseBody);
            } else {
                System.err.println("Request failed: " + response.code());
                if (response.body() != null) {
                    System.err.println("Error Body: " + responseBody);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public JSONObject createAiStudioDoctor(String accessToken, AiUserBizBo aiUserBizBo, String phone, Long assistantId, String menuDoamin, String userDomain) {

        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        // 请求 URL
        String url = "https://"+domain+"/studio/studioDoctor";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", aiUserBizBo.getUserName());
        jsonObject.put("avatar", aiUserBizBo.getUserAvatar());
        jsonObject.put("realHumanAvatar", aiUserBizBo.getRealHumanAvatar());
        jsonObject.put("hospital", aiUserBizBo.getWorkUnit());
        jsonObject.put("department", aiUserBizBo.getDepartment());
        jsonObject.put("title", aiUserBizBo.getDoctorTitle());
        jsonObject.put("phone", phone);
        jsonObject.put("introduction", aiUserBizBo.getPersonalProfile());
        jsonObject.put("assistantId", assistantId);
        jsonObject.put("greetingVideo", aiUserBizBo.getGreetingVideo());
        jsonObject.put("greetingVideoCover", aiUserBizBo.getGreetingVideoCover());
        jsonObject.put("knowledgeLink", "https://"+menuDoamin+".caringsaas.cn/doctorDetail?domain="+userDomain);


        RequestBody body = RequestBody.create(JSON, jsonObject.toJSONString());

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("clientid", "f72c4ec9da59a38fec6c8991c68daadb")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Host", domain)
                .addHeader("Connection", "keep-alive")
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (response.isSuccessful() && response.body() != null) {
                System.out.println("Response Code: " + response.code());
                System.out.println("Response Body: " + responseBody);
                return JSONObject.parseObject(responseBody);
            } else {
                System.err.println("Request failed: " + response.code());
                if (response.body() != null) {
                    System.err.println("Error Body: " + responseBody);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    @Override
    public void updateAiStudioDoctor(String tenantId, AiUserBizBo aiUserBizBo, Long aiStudioDoctorId) {

        if (Objects.nonNull(aiStudioDoctorId)) {
            JSONObject studioToken = getAiStudioToken(aiStudioDoctorId, tenantId);
            JSONObject tokenJSONObject = studioToken.getJSONObject("data");
            String accessToken = tokenJSONObject.getString("access_token");

            // 创建 OkHttpClient 实例
            OkHttpClient client = new OkHttpClient();

            // 请求 URL
            String url = "https://"+domain+"/studio/studioDoctor";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", aiStudioDoctorId);
            jsonObject.put("name", aiUserBizBo.getUserName());
            jsonObject.put("avatar", aiUserBizBo.getUserAvatar());
            jsonObject.put("realHumanAvatar", aiUserBizBo.getRealHumanAvatar());
            jsonObject.put("hospital", aiUserBizBo.getWorkUnit());
            jsonObject.put("department", aiUserBizBo.getDepartment());
            jsonObject.put("title", aiUserBizBo.getDoctorTitle());
            jsonObject.put("introduction", aiUserBizBo.getPersonalProfile());
            jsonObject.put("greetingVideo", aiUserBizBo.getGreetingVideo());
            jsonObject.put("greetingVideoCover", aiUserBizBo.getGreetingVideoCover());
            jsonObject.put("knowledgeMenuId", aiUserBizBo.getKnowledgeMenuId());
            jsonObject.put("specialty", aiUserBizBo.getSpecialty());

            RequestBody body = RequestBody.create(JSON, jsonObject.toJSONString());

            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("clientid", "f72c4ec9da59a38fec6c8991c68daadb")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "*/*")
                    .addHeader("Host", domain)
                    .addHeader("Connection", "keep-alive")
                    .build();

            // 发送请求并处理响应
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("Response Code: " + response.code());
                    System.out.println("Response Body: " + response.body().string());
                } else {
                    System.err.println("Request failed: " + response.code());
                    if (response.body() != null) {
                        System.err.println("Error Body: " + response.body().string());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 更新或者创建名片 基本信息
     * @param userJoin
     * @param aiUserBizBo
     */
    @Override
    public void updateCreateOrUpdateBusinessCard(UserJoin userJoin, AiUserBizBo aiUserBizBo) {
        BusinessCard businessCard;

        if (userJoin.getBusinessCardId() == null) {
            businessCard = businessCardService.getOne(Wraps.<BusinessCard>lbQ()
                    .select(SuperEntity::getId,BusinessCard::getDoctorName)
                    .eq(BusinessCard::getDoctorName, aiUserBizBo.getUserName())
                    .isNull(BusinessCard::getUserId)
                    .last(" limit 0, 1 "));
        } else {
            businessCard = businessCardService.getById(userJoin.getBusinessCardId());
            if (businessCard == null) {
                return;
            }
        }
        if (businessCard == null) {
            businessCard = new BusinessCard();
            businessCard.setOpenContactMe(false);
            businessCard.setOpenVideoAccount(false);
            businessCard.setDoctorAiType(0);
            businessCard.setMemberVersion(BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION);
        }
        businessCard.setDoctorName(aiUserBizBo.getUserName());
        businessCard.setDoctorTitle(aiUserBizBo.getDoctorTitle());
        businessCard.setDoctorDepartment(aiUserBizBo.getDepartment());
        businessCard.setDoctorHospital(aiUserBizBo.getWorkUnit());
        businessCard.setDoctorPersonal(aiUserBizBo.getPersonalProfile());
        businessCard.setDoctorMetaHuman(aiUserBizBo.getGreetingVideo());
        businessCard.setDoctorMetaHumanPoster(aiUserBizBo.getGreetingVideoCover());
        businessCard.setDoctorAvatar(aiUserBizBo.getRealHumanAvatar());
        businessCard.setDoctorBeGoodAt(aiUserBizBo.getSpecialty());

        if (businessCard.getId() == null) {
            businessCardService.save(businessCard);
            userJoin.setBusinessCardId(businessCard.getId());
        } else {
            businessCardService.updateById(businessCard);
            userJoin.setBusinessCardId(businessCard.getId());
        }
    }


    /**
     * 更新知识库用户信息
     * @param aiUserBizBo
     */
    @Transactional
    @Override
    public void updateKnowledgeUser(AiUserBizBo aiUserBizBo) {


        KnowledgeUser user = new KnowledgeUser();
        user.setId(aiUserBizBo.getUserId());
        user.setUserName(aiUserBizBo.getUserName());
        user.setUserAvatar(aiUserBizBo.getUserAvatar());
        user.setWorkUnit(aiUserBizBo.getWorkUnit());
        user.setDepartment(aiUserBizBo.getDepartment());
        user.setDoctorTitle(aiUserBizBo.getDoctorTitle());
        user.setPersonalProfile(aiUserBizBo.getPersonalProfile());
        user.setGreetingVideo(aiUserBizBo.getGreetingVideo());
        user.setGreetingVideoCover(aiUserBizBo.getGreetingVideoCover());
        user.setRealHumanAvatar(aiUserBizBo.getRealHumanAvatar());
        user.setSpecialty(aiUserBizBo.getSpecialty());
        user.setAiInteractiveAudioTrialListening(aiUserBizBo.getAiInteractiveAudioTrialListening());

        if (StrUtil.isNotEmpty(aiUserBizBo.getAiStudioDoctorId())) {
            user.setAiStudioDoctorId(aiUserBizBo.getAiStudioDoctorId());
        }
        if (aiUserBizBo.getKnowledgeMenuId() != null) {
            user.setKnowledgeMenuId(aiUserBizBo.getKnowledgeMenuId());
        }
        List<KnowledgeUserQualification> userQualification = aiUserBizBo.getUserQualification();
        List<Long> userQualificationIds = aiUserBizBo.getDeleteUserQualificationIds();

        if (CollUtil.isNotEmpty(userQualificationIds)) {
            knowledgeUserQualificationMapper.deleteBatchIds(userQualificationIds);
        }

        if (CollUtil.isNotEmpty(userQualification)) {
            for (KnowledgeUserQualification qualification : userQualification) {
                qualification.setKnowUserId(user.getId());
                if (qualification.getId() != null) {
                    knowledgeUserQualificationMapper.updateById(qualification);
                } else {
                    knowledgeUserQualificationMapper.insert(qualification);
                }
            }
        }

        knowledgeUserService.updateUserName(user);
    }


    @Override
    public void createKnowledgeUser(KnowledgeUser knowledgeUser, Boolean createKnowledge) {

        knowledgeUserService.createKnowledgeUser(knowledgeUser, false, createKnowledge);
    }

    @Override
    public void updateKnowledgeUser(KnowledgeUser knowledgeUser) {
        knowledgeUserService.updateById(knowledgeUser);
    }
}
