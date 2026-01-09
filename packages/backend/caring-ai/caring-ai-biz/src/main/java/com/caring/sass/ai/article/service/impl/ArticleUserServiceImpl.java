package com.caring.sass.ai.article.service.impl;


import com.caring.sass.ai.article.config.ArticleUserPayConfig;
import com.caring.sass.ai.article.dao.ArticleUserConsumptionMapper;
import com.caring.sass.ai.article.dao.ArticleUserMapper;
import com.caring.sass.ai.article.service.*;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.know.KnowDoctorType;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.know.service.KnowledgeUserService;
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
 * Ai创作用户
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-01
 */
@Slf4j
@Service

public class ArticleUserServiceImpl extends SuperServiceImpl<ArticleUserMapper, ArticleUser> implements ArticleUserService {


    @Autowired
    KnowledgeUserService knowledgeUserService;

    @Autowired
    ArticleUserConsumptionMapper consumptionMapper;

    @Autowired
    ArticleUserPayConfig articleUserPayConfig;

    @Autowired
    ArticleUserFreeLimitService articleUserFreeLimitService;

    @Autowired
    WorksPresetService worksPresetService;


    @Autowired
    ArticleUserAccountService articleUserAccountService;

    @Autowired
    ArticleAccountConsumptionService articleAccountConsumptionService;

    @Transactional
    @Override
    public boolean save(ArticleUser model) {
        model.setMembershipLevel(ArticleMembershipLevel.Annual_Membership);
        model.setMembershipExpiration(LocalDateTime.now().plusYears(99));
        model.setLookGuide(false);
        // 新用户注册。增加60能量豆
        model.setFreeEnergyBeans(Long.parseLong(articleUserPayConfig.getNewUserRegister().toString()));
        // 查询手机号是不是一个知识库博主。 知识库博主 送 500 豆
        Integer consumptionAmount = articleUserPayConfig.getNewUserRegister();
        try {
            KnowledgeUser user = knowledgeUserService.getOne(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserMobile, EncryptionUtil.encrypt(model.getUserMobile()))
                    .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                    .last(" limit 0,1 "));
            if (user != null) {
                model.setFreeEnergyBeans(Long.parseLong(articleUserPayConfig.getChiefPhysicianUseRegister().toString()));
                consumptionAmount = articleUserPayConfig.getChiefPhysicianUseRegister();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.save(model);

        if (consumptionAmount > 0) {
            articleUserAccountService.initAccount(model.getUserMobile(), consumptionAmount);

            ArticleUserConsumption consumption = new ArticleUserConsumption();
            consumption.setUserId(model.getId());
            consumption.setConsumptionType(ConsumptionType.NEW_USER_REGISTER);
            consumption.setConsumptionAmount(consumptionAmount);
            consumption.setMessageRemark(ConsumptionType.NEW_USER_REGISTER.getDesc());
            consumptionMapper.insert(consumption);

            articleAccountConsumptionService.save(ArticleAccountConsumption.builder()
                    .userMobile(model.getUserMobile())
                    .sourceType(ArticleAccountConsumption.sourceTypeArticle)
                    .consumptionId(consumption.getId())
                    .build());
        } else {
            articleUserAccountService.initAccount(model.getUserMobile(), 0);
        }


        // 新用户注册，赠送一次形象定制
        ArticleUserFreeLimit freeLimit = new ArticleUserFreeLimit();
        freeLimit.setUserId(model.getId());
        freeLimit.setHumanRemainingTimes(1);
        freeLimit.setVoiceRemainingTimes(0);
        freeLimit.setExpirationDate(LocalDateTime.now().plusYears(30));
        articleUserFreeLimitService.save(freeLimit);

        worksPresetService.presetMyWorks(model.getId());
        return true;
    }


    /**
     * 扣除用户的能量豆
     *
     * @param userId
     * @param i
     * @return
     */
    @Transactional
    @Override
    public boolean deductEnergy(Long userId, int i, ConsumptionType desc) {

        // 校验用户的会员是否有效
        ArticleUser articleUser = baseMapper.selectById(userId);

        ArticleUserAccount account = null;
        try {
            account = articleUserAccountService.getOne(Wraps.<ArticleUserAccount>lbQ()
                    .eq(ArticleUserAccount::getUserMobile, EncryptionUtil.encrypt(articleUser.getUserMobile())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (account == null) {
            account = articleUserAccountService.initAccount(articleUser.getUserMobile(), articleUser.getEnergyBeans().intValue());
        }
        // 优先使用注册时赠送的能量豆。
        if (account.getFreeEnergyBeans() >= i) {
            account.setFreeEnergyBeans(account.getFreeEnergyBeans() - i);
            articleUserAccountService.updateById(account);

            ArticleUserConsumption consumption = new ArticleUserConsumption();
            consumption.setUserId(userId);
            consumption.setConsumptionType(desc);
            consumption.setConsumptionAmount(-i);
            consumption.setMessageRemark(desc.getDesc());
            consumptionMapper.insert(consumption);
            articleAccountConsumptionService.save(ArticleAccountConsumption.builder()
                    .userMobile(articleUser.getUserMobile())
                    .sourceType(ArticleAccountConsumption.sourceTypeArticle)
                    .consumptionId(consumption.getId())
                    .build());
            return true;
        }
        int energyBeansNumber = i;

        if (account.getEnergyBeans() + account.getFreeEnergyBeans() < i) {
            throw new BizException("能量豆余额不足");
        }
        if (account.getFreeEnergyBeans() > 0) {
            i = (int) (i - account.getFreeEnergyBeans());
        }
        int update = articleUserAccountService.useEnergyBeans(articleUser.getUserMobile(), i);
        if (update > 0) {
            // 能量豆使用记录
            ArticleUserConsumption consumption = new ArticleUserConsumption();
            consumption.setUserId(userId);
            consumption.setConsumptionType(desc);
            consumption.setConsumptionAmount(-energyBeansNumber);
            consumption.setMessageRemark(desc.getDesc());
            consumptionMapper.insert(consumption);

            articleAccountConsumptionService.save(ArticleAccountConsumption.builder()
                    .userMobile(articleUser.getUserMobile())
                    .sourceType(ArticleAccountConsumption.sourceTypeArticle)
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
        String gongZhongHaoAppId = articleUserPayConfig.getGongnZhifuAppId();
        String string = "https://api." + webPrimaryDomain + "/api/wx/wxUserAuth/anno/thirdRedirectCodeOpenId?" +
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
