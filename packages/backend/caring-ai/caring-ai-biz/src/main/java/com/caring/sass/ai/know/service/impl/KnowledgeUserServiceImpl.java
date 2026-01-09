package com.caring.sass.ai.know.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.config.KnowChildDomainConfig;
import com.caring.sass.ai.know.config.KnowWxPayConfig;
import com.caring.sass.ai.know.dao.*;
import com.caring.sass.ai.know.service.KnowledgeService;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.ai.know.util.PaperSharePhotoUtils;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 知识库用户
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@Slf4j
@Service

public class KnowledgeUserServiceImpl extends SuperServiceImpl<KnowledgeUserMapper, KnowledgeUser> implements KnowledgeUserService {

    @Autowired
    KnowledgeService knowledgeService;

    @Autowired
    KnowledgeFileMapper knowledgeFileMapper;

    @Autowired
    KnowledgeSystemFileMapper knowledgeSystemFileMapper;

    @Autowired
    KnowWxPayConfig knowWxPayConfig;

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    KnowledgeUserSubscribeMapper knowledgeUserSubscribeMapper;

    @Autowired
    KnowChildDomainConfig knowChildDomainConfig;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    KnowledgeUserDomainMapper knowledgeUserDomainMapper;
    /**
     * 第三方平台 公众号使用 获取code 的授权路径
     */
    private static String AUTHORIZE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&component_appid=%s#wechat_redirect";


    /**
     * 创建一个主任级别的账号。
     * 调用业务知识库表，给主任生成 四个知识库
     * @param knowledgeUser
     */
    @Override
    public void createKnowledgeUser(KnowledgeUser knowledgeUser, Boolean copyFile, Boolean createKnowledge) {


        LbqWrapper<KnowledgeUser> lbqWrapper = null;
        try {
            lbqWrapper = Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(knowledgeUser.getUserMobile()))
                    .last(" limit 0,1 ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        KnowledgeUser user = baseMapper.selectOne(lbqWrapper);
        boolean syncMiniUserData = false;
        if (user != null) {
            syncMiniUserData = true;

            // 注意不能覆盖用户的密码
            if (user.getPassword() != null)  {
                knowledgeUser.setPassword(null);
            }
            BeanUtils.copyProperties(knowledgeUser, user);
            user.setUserType(KnowDoctorType.CHIEF_PHYSICIAN);
            baseMapper.updateById(user);
            knowledgeUser = user;

            knowledgeUserDomainMapper.delete(Wraps.<KnowledgeUserDomain>lbQ().eq(KnowledgeUserDomain::getKnowUserId, user.getId()));
            List<KnowledgeUserDomain> userDomains = knowledgeUser.getKnowledgeUserDomains();
            if (CollUtil.isNotEmpty(userDomains)) {
                for (KnowledgeUserDomain userDomain : userDomains) {
                    userDomain.setKnowUserId(knowledgeUser.getId());
                    userDomain.setSort(0);
                    knowledgeUserDomainMapper.insert(userDomain);
                }
            }
        } else {

            knowledgeUser.setUserType(KnowDoctorType.CHIEF_PHYSICIAN);
            knowledgeUser.setCallSwitch(1);
            knowledgeUser.setTotalCallDuration(100);
            baseMapper.insert(knowledgeUser);

            List<KnowledgeUserDomain> userDomains = knowledgeUser.getKnowledgeUserDomains();
            if (CollUtil.isNotEmpty(userDomains)) {
                for (KnowledgeUserDomain userDomain : userDomains) {
                    userDomain.setKnowUserId(knowledgeUser.getId());
                    userDomain.setSort(0);
                    knowledgeUserDomainMapper.insert(userDomain);
                }
            }
        }

        if (!createKnowledge) {
            return;
        }

        // 给医生合成分享海报
//        setPoster(knowledgeUser);

        // 初始化知识库
        boolean status = knowledgeService.initKnowledge(knowledgeUser, syncMiniUserData);
        if (!status) {
            baseMapper.deleteById(knowledgeUser.getId());
            throw new BizException("创建知识库失败，请检查");
        }
        // 查询系统知识库文档。克隆到医生的知识库

        // 克隆专业学术资料
        if (!copyFile) {
            return;
        }
        List<KnowledgeSystemFile> systemFiles = knowledgeSystemFileMapper.selectList(Wraps.<KnowledgeSystemFile>lbQ()
                .eq(KnowledgeSystemFile::getKnowType, KnowledgeType.ACADEMIC_MATERIALS));
        if (CollUtil.isNotEmpty(systemFiles)) {
            Knowledge knowledge = knowledgeService.getOne(Wraps.<Knowledge>lbQ()
                    .eq(Knowledge::getKnowType, KnowledgeType.ACADEMIC_MATERIALS)
                    .eq(Knowledge::getKnowUserId, knowledgeUser.getId()));
            cloneKnowsFile(knowledge, systemFiles, knowledgeUser.getId());
        }
        systemFiles = knowledgeSystemFileMapper.selectList(Wraps.<KnowledgeSystemFile>lbQ()
                .eq(KnowledgeSystemFile::getKnowType, KnowledgeType.PERSONAL_ACHIEVEMENTS));
        if (CollUtil.isNotEmpty(systemFiles)) {
            Knowledge knowledge = knowledgeService.getOne(Wraps.<Knowledge>lbQ()
                    .eq(Knowledge::getKnowType, KnowledgeType.PERSONAL_ACHIEVEMENTS)
                    .eq(Knowledge::getKnowUserId, knowledgeUser.getId()));
            cloneKnowsFile(knowledge, systemFiles, knowledgeUser.getId());
        }
        systemFiles = knowledgeSystemFileMapper.selectList(Wraps.<KnowledgeSystemFile>lbQ()
                .eq(KnowledgeSystemFile::getKnowType, KnowledgeType.CASE_DATABASE));
        if (CollUtil.isNotEmpty(systemFiles)) {
            Knowledge knowledge = knowledgeService.getOne(Wraps.<Knowledge>lbQ()
                    .eq(Knowledge::getKnowType, KnowledgeType.CASE_DATABASE)
                    .eq(Knowledge::getKnowUserId, knowledgeUser.getId()));
            cloneKnowsFile(knowledge, systemFiles, knowledgeUser.getId());
        }

    }

    /**
     * 克隆系统文档到博主的知识库
     * @param knowledge
     * @param systemFiles
     * @param userId
     */
    public void cloneKnowsFile(Knowledge knowledge, List<KnowledgeSystemFile> systemFiles, Long userId ) {
        if (CollUtil.isEmpty(systemFiles)) {
            return;
        }
        List<KnowledgeFile> files = new ArrayList<>(systemFiles.size());
        for (KnowledgeSystemFile file : systemFiles) {
            KnowledgeFile knowledgeFile =  KnowledgeFile.builder()
                    .knowType(knowledge.getKnowType())
                    .knowId(knowledge.getId())
                    .fileUserId(userId)
                    .fileName(file.getFileName())
                    .fileSize(file.getFileSize())
                    .fileUrl(file.getFileUrl())
                    .fileUploadTime(file.getFileUploadTime())
                    .difyFileStatus(file.getDifyFileStatus())
                    .initialFileName(file.getFileName())
                    .viewPermissions(knowledge.getViewPermissions())
                    .downloadPermission(knowledge.getDownloadPermission())
                    .systemFileId(file.getId())
                    .isUpdatePermissions(false)
                    .documentBelongs(KnowledgeFile.DOCUMENT_BELONGS_USER)
                    .difyFileId(file.getDifyFileId())
                    .difyBatch(file.getDifyBatch())
                    .build();
            files.add(knowledgeFile);
        }
        knowledgeFileMapper.insertBatchSomeColumn(files);
    }


    /**
     * 系统文档上传了一个。给所有博主都同步到。
     * @param systemFile
     */
    @Override
    public void uploadSystemFile(KnowledgeSystemFile systemFile) {

//        knowledgeSystemFileMapper.insert(systemFile);
//
//
//        List<KnowledgeUser> knowledgeUsers = baseMapper.selectList(Wraps.<KnowledgeUser>lbQ()
//                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN));
//        if (CollUtil.isEmpty(knowledgeUsers)) {
//            return;
//        }
//        List<Long> userIds = knowledgeUsers.stream().map(SuperEntity::getId).collect(Collectors.toList());
//
//        List<Knowledge> knowledgeList = knowledgeService.list(Wraps.<Knowledge>lbQ()
//                .eq(Knowledge::getKnowType, systemFile.getKnowType())
//                .in(Knowledge::getKnowUserId, userIds));
//        List<KnowledgeFile> knowledgeFiles = new ArrayList<>();
//        for (Knowledge knowledge : knowledgeList) {
//
//            KnowledgeFile knowledgeFile = KnowledgeFile.builder()
//                    .knowType(knowledge.getKnowType())
//                    .knowId(knowledge.getId())
//                    .fileUserId(knowledge.getKnowUserId())
//                    .fileName(systemFile.getFileName())
//                    .fileSize(systemFile.getFileSize())
//                    .fileUrl(systemFile.getFileUrl())
//                    .fileUploadTime(systemFile.getFileUploadTime())
//                    .difyFileStatus(systemFile.getDifyFileStatus())
//                    .difyFileId(systemFile.getDifyFileId())
//                    .difyBatch(systemFile.getDifyBatch())
//                    .documentBelongs(KnowledgeFile.DOCUMENT_BELONGS_SYSTEM)
//                    .systemFileId(systemFile.getId())
//                    .viewPermissions(knowledge.getViewPermissions())
//                    .downloadPermission(knowledge.getDownloadPermission())
//                    .isUpdatePermissions(false)
//                    .initialFileName(systemFile.getFileName())
//                    .build();
//            knowledgeFiles.add(knowledgeFile);
//        }
//
//        if (CollUtil.isNotEmpty(knowledgeFiles)) {
//            knowledgeFileMapper.insertBatchSomeColumn(knowledgeFiles);
//        }

    }

    /**
     * 给所有博主。初始化系统文档
     */
    @Override
    public void initUserSystemFile() {
        // 查询系统文档
//        List<KnowledgeSystemFile> systemFiles = knowledgeSystemFileMapper.selectList(Wraps.<KnowledgeSystemFile>lbQ()
//                .eq(KnowledgeSystemFile::getKnowType, KnowledgeType.ACADEMIC_MATERIALS));
//
//        // 查询博主列表
//        List<KnowledgeUser> knowledgeUsers = baseMapper.selectList(Wraps.<KnowledgeUser>lbQ().eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN));
//        if (CollUtil.isEmpty(knowledgeUsers)) {
//            return;
//        }
//        List<Long> userIds = knowledgeUsers.stream().map(SuperEntity::getId).collect(Collectors.toList());
//
//        // 查询博主的  专业学术资料 知识库
//        List<Knowledge> knowledgeList = knowledgeService.list(Wraps.<Knowledge>lbQ()
//                .eq(Knowledge::getKnowType, KnowledgeType.ACADEMIC_MATERIALS)
//                .in(Knowledge::getKnowUserId, userIds));
//        List<KnowledgeFile> knowledgeFiles = new ArrayList<>();
//        for (KnowledgeSystemFile systemFile : systemFiles) {
//            for (Knowledge knowledge : knowledgeList) {
//                KnowledgeFile knowledgeFile = KnowledgeFile.builder()
//                        .knowType(knowledge.getKnowType())
//                        .knowId(knowledge.getId())
//                        .fileUserId(knowledge.getKnowUserId())
//                        .fileName(systemFile.getFileName())
//                        .fileSize(systemFile.getFileSize())
//                        .fileUrl(systemFile.getFileUrl())
//                        .fileUploadTime(systemFile.getFileUploadTime())
//                        .difyFileStatus(systemFile.getDifyFileStatus())
//                        .difyFileId(systemFile.getDifyFileId())
//                        .difyBatch(systemFile.getDifyBatch())
//                        .documentBelongs(KnowledgeFile.DOCUMENT_BELONGS_SYSTEM)
//                        .systemFileId(systemFile.getId())
//                        .viewPermissions(knowledge.getViewPermissions())
//                        .downloadPermission(knowledge.getDownloadPermission())
//                        .isUpdatePermissions(false)
//                        .initialFileName(systemFile.getFileName())
//                        .build();
//                knowledgeFiles.add(knowledgeFile);
//            }
//        }
//
//        if (CollUtil.isNotEmpty(knowledgeFiles)) {
//            knowledgeFileMapper.insertBatchSomeColumn(knowledgeFiles);
//        }


    }


    @Override
    public void updateUserName(KnowledgeUser model) {
        KnowledgeUser knowledgeUser = baseMapper.selectById(model.getId());
        super.updateById(model);
        if (knowledgeUser.getUserType().equals(KnowDoctorType.CHIEF_PHYSICIAN)) {
            if (model.getUserName() != knowledgeUser.getUserName() ||
                model.getUserAvatar() != knowledgeUser.getUserAvatar()){
                setPoster(knowledgeUser);
            }
        }
    }

    public void setPoster(KnowledgeUser user) {

        String wxAuthUrl = ApplicationDomainUtil.apiUrl() + "/api/ai/knowledgeUser/anno/redirect/" + user.getUserDomain();
        String domainName = ApplicationProperties.getDomainName();
        String redirectSaasUrl = String.format("%s://%s.%s", "https", user.getUserDomain(), domainName);
        String fileUrl = PaperSharePhotoUtils.mergePhoto(wxAuthUrl, user.getUserName(), user.getUserAvatar(), redirectSaasUrl);

        if (StrUtil.isNotEmpty(fileUrl)) {
            File file = new File(fileUrl);
            if (file.exists()) {
                org.springframework.mock.web.MockMultipartFile mockMultipartFile = FileUtils.fileToFileItem(file);
                R<com.caring.sass.file.entity.File> fileR = fileUploadApi.upload(0l, mockMultipartFile);
                if (fileR.getIsSuccess()) {
                    com.caring.sass.file.entity.File data = fileR.getData();
                    KnowledgeUser knowledgeUser1 = new KnowledgeUser();
                    knowledgeUser1.setId(user.getId());
                    knowledgeUser1.setSharePoster(data.getUrl());
                    baseMapper.updateById(knowledgeUser1);

                    file.delete();
                }
            } else {
                throw new BizException("合成海报失败");
            }
        }
    }


    @Override
    public void initMergePaper() {

        List<KnowledgeUser> knowledgeUsers = baseMapper.selectList(Wraps.<KnowledgeUser>lbQ()
                .select(SuperEntity::getId, KnowledgeUser::getUserDomain, KnowledgeUser::getUserName, KnowledgeUser::getUserAvatar)
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN));

        for (KnowledgeUser user : knowledgeUsers) {

            setPoster(user);
        }

    }




    public String redirectDocKnowPayWxAuthUrl(String domain, String webSubDomain, String webPrimaryDomain) {

        if (StrUtil.isBlank(webSubDomain)) {
            webSubDomain = knowChildDomainConfig.getDocuknowMainDoamin();
        }
        if (StrUtil.isBlank(webPrimaryDomain)) {
            webPrimaryDomain = ApplicationProperties.getDomainName();
        }
        String redirectSaasUrl = String.format("%s://%s.%s", "https", webSubDomain, webPrimaryDomain);
        String encode = null;

        String gongZhongHaoAppId = knowWxPayConfig.getGong_zhong_hao_app_id();
        String string = "https://api."+ webPrimaryDomain + "/api/wx/wxUserAuth/anno/thirdRedirectCodeOpenId?" +
                "wxAppId=" + gongZhongHaoAppId + "&redirectSaasUrl=" + redirectSaasUrl + "/mobilePay/" + domain;
        try {
            encode = URLEncoder.encode(string, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String state = "1";
        String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();

        return String.format(AUTHORIZE, gongZhongHaoAppId, encode, "snsapi_base", state, componentAppId);
    }


    @Override
    public String createWxAuthUrl(String domain) {

        String domainName = ApplicationProperties.getDomainName();
        String redirectSaasUrl = String.format("%s://%s.%s", "https", "docuknowsmobile", domainName);
        String encode = null;

        String gongZhongHaoAppId = knowWxPayConfig.getGong_zhong_hao_app_id();
        String string = ApplicationDomainUtil.apiUrl() + "/api/wx/wxUserAuth/anno/thirdRedirectCodeOpenId?" +
                "wxAppId=" + gongZhongHaoAppId + "&redirectSaasUrl=" + redirectSaasUrl + "?domain=" + domain;
        try {
            encode = URLEncoder.encode(string, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String state = "1";
        String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();

        return String.format(AUTHORIZE, gongZhongHaoAppId, encode, "snsapi_base", state, componentAppId);
    }



    /**
     * 查询普通用户在这个域名下的会员信息
     * @param userId
     * @param domain
     * @return
     */
    @Override
    public KnowledgeUserSubscribe getKnowledgeUserSubscribe(Long userId, String domain, Boolean subscribeSwitch) {

        if (subscribeSwitch != null && subscribeSwitch) {
            return knowledgeUserSubscribeMapper.selectOne(Wraps.<KnowledgeUserSubscribe>lbQ()
                    .eq(KnowledgeUserSubscribe::getKnowledgeUserId, userId)
                    .eq(KnowledgeUserSubscribe::getMembershipLevel, MembershipLevel.PROFESSIONAL_EDITION)
                    .eq(KnowledgeUserSubscribe::getUserDomain, domain)
                    .last(" limit 0,1 "));
        }
        KnowledgeUserSubscribe userSubscribe = knowledgeUserSubscribeMapper.selectOne(Wraps.<KnowledgeUserSubscribe>lbQ()
                .eq(KnowledgeUserSubscribe::getKnowledgeUserId, userId)
                .eq(KnowledgeUserSubscribe::getMembershipLevel, MembershipLevel.FREE_VERSION)
                .eq(KnowledgeUserSubscribe::getUserDomain, domain)
                .last(" limit 0,1 "));
        if (userSubscribe == null) {
            userSubscribe = knowledgeUserSubscribeMapper.selectOne(Wraps.<KnowledgeUserSubscribe>lbQ()
                    .eq(KnowledgeUserSubscribe::getKnowledgeUserId, userId)
                    .eq(KnowledgeUserSubscribe::getMembershipLevel, MembershipLevel.PROFESSIONAL_EDITION)
                    .eq(KnowledgeUserSubscribe::getUserDomain, domain)
                    .last(" limit 0,1 "));
        }
        return userSubscribe;
    }

    @Override
    public List<KnowledgeUserSubscribe> subscribeList(LbqWrapper<KnowledgeUserSubscribe> eq) {
        return knowledgeUserSubscribeMapper.selectList(eq);
    }


    @Override
    public Integer countFanNumber(String domain) {

        Integer selectedCount = knowledgeUserSubscribeMapper.selectCount(Wraps.<KnowledgeUserSubscribe>lbQ()
                .eq(KnowledgeUserSubscribe::getUserDomain, domain));
        return selectedCount == null ? 0 : selectedCount;

    }



    @Override
    public boolean updateCallDuration(Long userId, Integer minuteDuration) {

        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        String key = "ai_call_duration:" + userId;
        try {
            lockBoolean = distributedLock.lock(key, 5000L, 20);
            if (lockBoolean) {
                if (minuteDuration > 0) {

                    KnowledgeUser knowledgeUser = new KnowledgeUser();
                    UpdateWrapper<KnowledgeUser> wrapper = new UpdateWrapper<>();
                    wrapper.setSql("total_call_duration = total_call_duration + " + minuteDuration);
                    wrapper.eq("id", userId);
                    baseMapper.update(knowledgeUser, wrapper);

                } else {

                    KnowledgeUser user = baseMapper.selectOne(Wraps.<KnowledgeUser>lbQ()
                            .eq(KnowledgeUser::getId, userId)
                            .select(SuperEntity::getId, KnowledgeUser::getTotalCallDuration));
                    if (user.getTotalCallDuration() <= 0) {

                        return false;
                    }

                    KnowledgeUser knowledgeUser = new KnowledgeUser();
                    UpdateWrapper<KnowledgeUser> wrapper = new UpdateWrapper<>();
                    wrapper.setSql("total_call_duration = total_call_duration + " + minuteDuration);
                    wrapper.eq("id", userId);
                    baseMapper.update(knowledgeUser, wrapper);
                }
            }

        } catch (Exception c) {

        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(key);
            }
        }
        return true;

    }
}
