package com.caring.sass.ai.card.service.impl;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.card.dao.BusinessCardMapper;
import com.caring.sass.ai.card.dao.BusinessCardStudioMapper;
import com.caring.sass.ai.card.service.BusinessCardService;
import com.caring.sass.ai.card.service.BusinessCardUseDayStatisticsService;
import com.caring.sass.ai.card.util.CardSharePhotoUtils;
import com.caring.sass.ai.config.CardConfig;
import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.ai.entity.card.BusinessCardStudio;
import com.caring.sass.ai.entity.card.BusinessCardUseDayStatistics;
import com.caring.sass.ai.know.util.DifyCardApi;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.wx.MiniAppApi;
import com.caring.sass.wx.dto.config.MiniQrCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * AI名片
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-22
 */
@Slf4j
@Service

public class BusinessCardServiceImpl extends SuperServiceImpl<BusinessCardMapper, BusinessCard> implements BusinessCardService {

    private static final String defaultDoctorAiDialogue = "https://genius.domain/chat/share?shareId=1a8ejoer2fhh53be45y3vhb9";

    /**默认卡片API秘钥**/
    private static final String DEFAULT_DOCTOR_AI_KEY = "fastgpt-yVx8KEn3zL014P6PUq2Jz4bkd6EU70N80";


    @Autowired
    BusinessCardUseDayStatisticsService businessCardUseDayStatisticsService;

    @Autowired
    BusinessCardStudioMapper businessCardStudioMapper;

    @Autowired
    MiniAppApi miniAppApi;

    @Autowired
    CardConfig cardConfig;

    @Autowired
    FileUploadApi fileUploadApi;

    @Autowired
    private DifyCardApi difyCardApi;

    @Override
    public boolean save(BusinessCard model) {
        if (model.getDoctorAiType() != null && model.getDoctorAiType().equals(0)) {
            model.setDoctorAiDialogue(defaultDoctorAiDialogue);
            model.setDoctorAiDialogueKey(DEFAULT_DOCTOR_AI_KEY);
        }
        if (model.getUserId() != null) {
            model.setActivationTime(LocalDateTime.now());
        }
        boolean save = super.save(model);
        if (save) {
            String dialogueQuestion = model.getDoctorAiDialogueQuestion();
            if (StrUtil.isEmpty(dialogueQuestion) || "[]".equals(dialogueQuestion)) {
                SaasGlobalThreadPool.execute(() -> generatorQuestion(model));
            }
            SaasGlobalThreadPool.execute(() -> mergeMiniAppCode(model));
        }
        return save;
    }


    /**
     * 生成AI对话
     * @param model
     */
    public void generatorQuestion(BusinessCard model) {
        String question = difyCardApi.generatorQuestion(model.getDoctorDepartment(), model.getDoctorBeGoodAt(), BaseContextHandler.getUserId());
        if (StrUtil.isNotEmpty(question)) {
            model.setDoctorAiDialogueQuestion(question);
        }
        BusinessCard businessCard = new BusinessCard();
        businessCard.setId(model.getId());
        businessCard.setDoctorAiDialogueQuestion(question);
        baseMapper.updateById(model);
    }



    /**
     * 异步合成小程序的分享码
     * @param model
     */
    public void mergeMiniAppCode(BusinessCard model) {
        String appId = cardConfig.getAppId();
        MiniQrCode qrCode = new MiniQrCode();
        qrCode.setPath("pages/index/index?card=" + model.getId());
        qrCode.setWidth(184);
        R<String> apiQRCode = miniAppApi.createQRCode(appId, qrCode);
        if (apiQRCode.getIsSuccess()) {
            String data = apiQRCode.getData();
            model.setMiniQrCode(data);
            baseMapper.updateById(model);
            createBusinessQrcode(model);
            baseMapper.updateById(model);

            businessCardUseDayStatisticsService.synchronizedQueryTodayStatisticsId(model.getId());
        }
    }



    @Override
    public void initQrCode() {

        List<BusinessCard> businessCards = baseMapper.selectList(Wraps.<BusinessCard>lbQ()
                .select(SuperEntity::getId, BusinessCard::getDoctorName)
                .isNull(BusinessCard::getMiniQrCode));

        String appId = cardConfig.getAppId();
        for (BusinessCard businessCard : businessCards) {
            MiniQrCode qrCode = new MiniQrCode();
            qrCode.setPath("pages/index/index?card=" + businessCard.getId());
            qrCode.setWidth(184);
            R<String> apiQRCode = miniAppApi.createQRCode(appId, qrCode);
            if (apiQRCode.getIsSuccess()) {
                String data = apiQRCode.getData();
                businessCard.setMiniQrCode(data);
                baseMapper.updateById(businessCard);
            }
        }

    }



    @Override
    public void checkVideoInfo(BusinessCard businessCard) {
        if (businessCard.getOpenVideoAccount() != null && businessCard.getOpenVideoAccount()) {
            if (StrUtil.isEmpty(businessCard.getFinderUserName()) || StrUtil.isEmpty(businessCard.getFeedId())) {
                throw new BizException("关联视频号，视频号ID和视频ID不能为空");
            }
        }
        if (businessCard.getOpenContactMe() != null && businessCard.getOpenContactMe()) {
            if (StrUtil.isEmpty(businessCard.getContactImgUrl())) {
                throw new BizException("请上传联系我使用的图片");
            }
        }
    }

    /**
     * 制作商业分享名片
     * @param card
     */
    private void createBusinessQrcode(BusinessCard card) {

        String fileUrl = CardSharePhotoUtils.mergePhoto(card.getMiniQrCode(), card.getDoctorName(), card.getDoctorAvatar(),
                card.getDoctorHospital(), card.getDoctorDepartment(), card.getDoctorTitle());

        if (StrUtil.isNotEmpty(fileUrl)) {
            File file = new File(fileUrl);
            if (file.exists()) {
                org.springframework.mock.web.MockMultipartFile mockMultipartFile = FileUtils.fileToFileItem(file);
                R<com.caring.sass.file.entity.File> fileR = fileUploadApi.upload(0l, mockMultipartFile);
                if (fileR.getIsSuccess()) {
                    com.caring.sass.file.entity.File data = fileR.getData();
                    card.setId(card.getId());
                    card.setBusinessQrCode(data.getUrl());
                    baseMapper.updateById(card);
                    file.delete();
                }
            } else {
                throw new BizException("合成分享二维码失败");
            }
        }
    }

    @Override
    public void initQrCode(Long id) {

        BusinessCard card = baseMapper.selectById(id);
        createBusinessQrcode(card);

    }

    @Override
    public boolean removeById(Serializable id) {
        long cardId = Long.parseLong(id.toString());
        businessCardStudioMapper.delete(Wraps.<BusinessCardStudio>lbQ().eq(BusinessCardStudio::getBusinessCard, cardId));
        businessCardUseDayStatisticsService.remove(Wraps.<BusinessCardUseDayStatistics>lbQ().eq(BusinessCardUseDayStatistics::getBusinessCardId, cardId));
        return super.removeById(id);
    }


    @Transactional
    @Override
    public boolean updateAllById(BusinessCard model) {
        Boolean businessQrCode = model.getResetBusinessQrCode();
        defaultAiDialogue(model);

        super.updateAllById(model);

        if (businessQrCode) {
            createBusinessQrcode(model);
        }
        return true;
    }

    private void defaultAiDialogue(BusinessCard model) {
        try {
            if (model.getDoctorAiType() != null && model.getDoctorAiType().equals(0)) {
                model.setDoctorAiDialogue(defaultDoctorAiDialogue);
                model.setDoctorAiDialogueKey(DEFAULT_DOCTOR_AI_KEY);
                String dialogueQuestion = model.getDoctorAiDialogueQuestion();
                // 空字符串或空数组则需要重新生成
                if (StrUtil.isEmpty(dialogueQuestion) || "[]".equals(dialogueQuestion)) {
                    SaasGlobalThreadPool.execute(() -> {
                        generatorQuestion(model);
                    });
                }
            }
        } catch (Exception e) {
            log.error("设置默认AI对话失败", e);
        }
    }


    /**
     * 设置医生的多个工作室
     * @param businessCard
     */
    @Override
    public void setDoctorStudio(BusinessCard businessCard) {
        List<BusinessCardStudio> studios = businessCardStudioMapper.selectList(Wraps.<BusinessCardStudio>lbQ()
                .eq(BusinessCardStudio::getBusinessCard, businessCard.getId()));
        if (CollUtil.isNotEmpty(studios)) {
            List<String> doctorStudioList = new ArrayList<>();
            studios.forEach(item -> doctorStudioList.add(item.getDoctorStudio()));
            businessCard.setDoctorStudioList(doctorStudioList);
            businessCard.setStudios(studios);
        }
    }

    @Override
    public boolean updateById(BusinessCard model) {
        defaultAiDialogue(model);
        return super.updateById(model);
    }
}
