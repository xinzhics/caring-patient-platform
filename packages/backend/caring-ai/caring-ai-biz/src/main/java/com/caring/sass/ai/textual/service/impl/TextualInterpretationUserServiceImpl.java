package com.caring.sass.ai.textual.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.service.ArticleAccountConsumptionService;
import com.caring.sass.ai.article.service.ArticleUserAccountService;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.know.KnowDoctorType;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
import com.caring.sass.ai.entity.textual.TextualInterpretationUser;
import com.caring.sass.ai.entity.textual.TextualInterpretationUserConsumption;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.ai.textual.config.TextualUserPayConfig;
import com.caring.sass.ai.textual.dao.TextualInterpretationUserConsumptionMapper;
import com.caring.sass.ai.textual.dao.TextualInterpretationUserMapper;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

/**
 * <p>
 * 业务实现类
 * 文献解读用户表
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Service

public class TextualInterpretationUserServiceImpl extends SuperServiceImpl<TextualInterpretationUserMapper, TextualInterpretationUser> implements TextualInterpretationUserService {



    @Autowired
    KnowledgeUserService knowledgeUserService;

    @Autowired
    TextualInterpretationUserConsumptionMapper consumptionMapper;

    @Autowired
    TextualUserPayConfig textualUserPayConfig;

    @Autowired
    ArticleUserAccountService articleUserAccountService;

    @Autowired
    ArticleAccountConsumptionService articleAccountConsumptionService;

    @Transactional
    @Override
    public boolean save(TextualInterpretationUser model) {
        model.setMembershipLevel(ArticleMembershipLevel.Annual_Membership);
        model.setMembershipExpiration(LocalDateTime.now().plusYears(99));
        // 新用户注册。增加60能量豆
        model.setFreeEnergyBeans(Long.parseLong(textualUserPayConfig.getNewUserRegister().toString()));

        // 查询手机号是不是一个知识库博主。 知识库博主 送 500 豆
        Integer consumptionAmount = textualUserPayConfig.getNewUserRegister();
        try {
            KnowledgeUser user = knowledgeUserService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(model.getUserMobile()))
                    .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                    .last(" limit 0,1 "));
            if (user != null) {
                model.setFreeEnergyBeans(Long.parseLong(textualUserPayConfig.getChiefPhysicianUseRegister().toString()));
                consumptionAmount = textualUserPayConfig.getChiefPhysicianUseRegister();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.save(model);
        if (consumptionAmount != null && consumptionAmount > 0) {
            articleUserAccountService.initAccount(model.getUserMobile(), consumptionAmount);

            TextualInterpretationUserConsumption consumption = new TextualInterpretationUserConsumption();
            consumption.setUserId(model.getId());
            consumption.setConsumptionType(TextualConsumptionType.NEW_USER_REGISTER);
            consumption.setConsumptionAmount(consumptionAmount);
            consumption.setMessageRemark(TextualConsumptionType.NEW_USER_REGISTER.getDesc());
            consumptionMapper.insert(consumption);

            articleAccountConsumptionService.save(ArticleAccountConsumption.builder()
                    .userMobile(model.getUserMobile())
                    .sourceType("textual")
                    .consumptionId(consumption.getId())
                    .build());

        } else {
            articleUserAccountService.initAccount(model.getUserMobile(), 0);
        }

        return true;
    }





    /**
     * 扣除用户的能量豆
     * @param userId
     * @param i
     * @return
     */
    @Transactional
    @Override
    public boolean deductEnergy(Long userId, int i, TextualConsumptionType desc) {

        // 校验用户的会员是否有效
        TextualInterpretationUser user = baseMapper.selectById(userId);

        // 查询用户能量豆账户
        ArticleUserAccount articleUserAccount = null;
        try {
            articleUserAccount = articleUserAccountService.getOne(Wraps.<ArticleUserAccount>lbQ()
                    .eq(ArticleUserAccount::getUserMobile, EncryptionUtil.encrypt( user.getUserMobile())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 优先使用注册时赠送的能量豆。
        if (articleUserAccount.getFreeEnergyBeans() >= i) {
            articleUserAccount.setFreeEnergyBeans(articleUserAccount.getFreeEnergyBeans() - i);
            articleUserAccountService.updateById(articleUserAccount);

            TextualInterpretationUserConsumption consumption = new TextualInterpretationUserConsumption();
            consumption.setUserId(userId);
            consumption.setConsumptionType(desc);
            consumption.setConsumptionAmount(-i);
            consumption.setMessageRemark(desc.getDesc());
            consumptionMapper.insert(consumption);

            articleAccountConsumptionService.save(ArticleAccountConsumption.builder()
                    .userMobile(user.getUserMobile())
                    .sourceType(ArticleAccountConsumption.sourceTypeTextual)
                    .consumptionId(consumption.getId())
                    .build());
            return true;
        }
        int energyBeansNumber = i;

        if (articleUserAccount.getEnergyBeans() + articleUserAccount.getFreeEnergyBeans() < i) {
            throw new BizException("能量豆余额不足");
        }
        if (articleUserAccount.getFreeEnergyBeans() > 0) {
            i = (int) (i - articleUserAccount.getFreeEnergyBeans());
        }
        int update = articleUserAccountService.useEnergyBeans(user.getUserMobile(), i);
        if (update > 0) {
            // 能量豆使用记录
            TextualInterpretationUserConsumption consumption = new TextualInterpretationUserConsumption();
            consumption.setUserId(userId);
            consumption.setConsumptionType(desc);
            consumption.setConsumptionAmount(-energyBeansNumber);
            consumption.setMessageRemark(desc.getDesc());
            consumptionMapper.insert(consumption);

            articleAccountConsumptionService.save(ArticleAccountConsumption.builder()
                    .userMobile(user.getUserMobile())
                    .sourceType(ArticleAccountConsumption.sourceTypeTextual)
                    .consumptionId(consumption.getId())
                    .build());
            return true;
        } else {
            throw new BizException("扣除能量豆失败");
        }

    }

    /**
     * 第三方平台 公众号使用 获取code 的授权路径
     */
    private static String AUTHORIZE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&component_appid=%s#wechat_redirect";


    @Override
    public String redirectWxAuthUrl(String redirectUri) {
        String webPrimaryDomain = ApplicationProperties.getDomainName();
        String encode = null;
        try {
            redirectUri = URLEncoder.encode(redirectUri, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String gongZhongHaoAppId = textualUserPayConfig.getGongnZhifuAppId();
        String string = "https://api."+ webPrimaryDomain + "/api/wx/wxUserAuth/anno/thirdRedirectCodeOpenId?" +
                "wxAppId=" + gongZhongHaoAppId + "&redirectSaasUrl=" + redirectUri;
        try {
            encode = URLEncoder.encode(string, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String state = "1";
        String componentAppId = ApplicationProperties.getThirdPlatformComponentAppId();

        return String.format(AUTHORIZE, gongZhongHaoAppId, encode, "snsapi_base", state, componentAppId);
    }
}
