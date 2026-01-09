package com.caring.sass.ai.article.service.impl;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.config.ArticleUserPayConfig;
import com.caring.sass.ai.article.dao.ArticleUserOrderMapper;
import com.caring.sass.ai.article.service.*;
import com.caring.sass.ai.dto.know.KnowMemberType;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.know.KnowDoctorType;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.entity.know.KnowledgeUserOrder;
import com.caring.sass.ai.entity.know.MembershipLevel;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.utils.DateUtils;
import com.caring.sass.wx.WechatOrdersApi;
import com.caring.sass.wx.dto.config.WechatMinruiOrdersSaveDTO;
import com.caring.sass.wx.dto.config.WechatOrdersPageDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 科普创作用户支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
@Slf4j
@Service

public class ArticleUserOrderServiceImpl extends SuperServiceImpl<ArticleUserOrderMapper, ArticleUserOrder> implements ArticleUserOrderService {


    @Autowired
    ArticleUserPayConfig articleUserPayConfig;

    @Autowired
    ArticleUserService articleUserService;

    @Autowired
    WechatOrdersApi wechatOrdersApi;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    ArticleUserConsumptionService consumptionService;

    @Autowired
    ArticleUserNoticeService articleUserNoticeService;

    @Autowired
    ArticleUserFreeLimitService articleUserFreeLimitService;

    public ArticleUserOrder createUserOrder(ArticleUser user, ArticleOrderGoodsType goodsType, String uid, SseEmitter emitter) {

        ArticleUserOrder userOrder = new ArticleUserOrder();
        userOrder.setCreateUser(user.getId());
        userOrder.setGoodsType(goodsType);
        userOrder.setGoodsPrice(articleUserPayConfig.getCost(goodsType));
        userOrder.setPaymentStatus(PaymentStatus.NO_PAY);
        userOrder.setUid(uid);
        baseMapper.insert(userOrder);
        return userOrder;
    }

    @Override
    public WechatOrders createdJSAPIOrder(ArticleUserOrderGoods articleUserOrderGoods) {

        ArticleUser user = articleUserService.getById(BaseContextHandler.getUserId());
        ArticleOrderGoodsType orderGoodsType = articleUserOrderGoods.getOrderGoodsType();
        // 下单不是会员产品。优先要求购买会员产品
        if (!orderGoodsType.equals(ArticleOrderGoodsType.annual)) {
            ArticleMembershipLevel level = user.getMembershipLevel();
            if (level == null || user.getMembershipExpiration().isBefore(LocalDateTime.now())) {
                throw new BizException("您不是会员，请先购买会员");
            }
        }

        ArticleUserOrder userOrder = createUserOrder(user, orderGoodsType, null, null);

        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        saveDTO.setDescription(articleUserPayConfig.getCostName(orderGoodsType));

        saveDTO.setOpenId(articleUserOrderGoods.getOpenId());
        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setMerchantId(articleUserPayConfig.getMerchantId());
        saveDTO.setAppId(articleUserPayConfig.getGongnZhifuAppId());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setBusinessType(ArticleUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/articleUserOrder/anno/" + userOrder.getId() + "/status?status=");
        R<WechatOrders> wechatJSAPIOrders = wechatOrdersApi.createWechatJSAPIOrders(saveDTO);
        if (!wechatJSAPIOrders.getIsSuccess() || Objects.isNull(wechatJSAPIOrders.getData())) {
            throw new BizException("创建订单失败");
        }
        return wechatJSAPIOrders.getData();
    }


    /**
     * web端创建一个微信支付订单
     * @param articleUserOrderGoods
     * @return
     */
    @Override
    public String createdWechatOrder(ArticleUserOrderGoods articleUserOrderGoods) {

        ArticleUser user = articleUserService.getById(BaseContextHandler.getUserId());
        ArticleOrderGoodsType orderGoodsType = articleUserOrderGoods.getOrderGoodsType();
        // 下单不是会员产品。优先要求购买会员产品
        if (!orderGoodsType.equals(ArticleOrderGoodsType.annual)) {
            ArticleMembershipLevel level = user.getMembershipLevel();
            if (level == null || user.getMembershipExpiration().isBefore(LocalDateTime.now())) {
                throw new BizException("您不是会员，请先购买会员");
            }
        }

        String uid = articleUserOrderGoods.getUid();
        ArticleUserOrder userOrder = createUserOrder(user, orderGoodsType, uid, null);

        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        saveDTO.setDescription(articleUserPayConfig.getCostName(orderGoodsType));

        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setMerchantId(articleUserPayConfig.getMerchantId());
        saveDTO.setAppId(articleUserPayConfig.getAppId());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setBusinessType(ArticleUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/articleUserOrder/anno/" + userOrder.getId() + "/status?status=");
        R<String> nativeOrders = wechatOrdersApi.createWechatNativeOrders(saveDTO);
        if (!nativeOrders.getIsSuccess() || Objects.isNull(nativeOrders.getData())) {
            throw new BizException("创建订单失败");
        }
        return nativeOrders.getData();
    }


    @Override
    public ArticleUserOrder queryOrderStatus(ArticleUserOrderGoods memberPayOrder) {

        ArticleUserOrder userOrder = baseMapper.selectOne(Wraps.<ArticleUserOrder>lbQ()
                .eq(ArticleUserOrder::getUid, memberPayOrder.getUid())
                .eq(ArticleUserOrder::getGoodsType, memberPayOrder.getOrderGoodsType())
                .orderByDesc(SuperEntity::getCreateTime)
                .last("limit 1"));
        if (userOrder.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            return userOrder;
        }
        WechatOrdersPageDTO dto = new WechatOrdersPageDTO();
        dto.setBusinessId(userOrder.getId());
        dto.setBusinessType(KnowledgeUserOrder.class.getSimpleName());
        R<Boolean> nativeOrders = wechatOrdersApi.queryWechatNativeOrders(dto);
        if (nativeOrders.getIsSuccess()) {
            Boolean data = nativeOrders.getData();
            if (data != null && data) {
                userOrder = baseMapper.selectOne(Wraps.<ArticleUserOrder>lbQ()
                        .eq(ArticleUserOrder::getUid, memberPayOrder.getUid())
                        .eq(ArticleUserOrder::getGoodsType, memberPayOrder.getOrderGoodsType())
                        .orderByDesc(SuperEntity::getCreateTime)
                        .last("limit 1"));
            }
        }
        return userOrder;
    }

    /**
     * 购买会员
     * @param userOrder
     * @param status
     */
    @Transactional
    public void annualMember(ArticleUserOrder userOrder, PaymentStatus status) {
        // 充值会员
        if (PaymentStatus.SUCCESS.equals(userOrder.getPaymentStatus())) {
            return;
        }
        userOrder.setPaymentStatus(status);
        baseMapper.updateById(userOrder);

        ArticleUser articleUser = articleUserService.getById(userOrder.getCreateUser());
        // 如果会员还没到期
        if (articleUser.getMembershipExpiration() != null &&
                articleUser.getMembershipExpiration().isAfter(LocalDateTime.now())) {
            articleUser.setMembershipExpiration(articleUser.getMembershipExpiration().plusDays(365));
        } else {
            // 会员到期了。直接从现在开始计算
            articleUser.setMembershipExpiration(LocalDateTime.now().plusDays(365));
        }
        // 设置会员的有限期
        articleUser.setMembershipLevel(ArticleMembershipLevel.Annual_Membership);
        articleUser.setEnergyBeans(null);
        articleUserService.updateById(articleUser);

        // 赠送用户2000能量豆
        UpdateWrapper<ArticleUser> wrapper = new UpdateWrapper<ArticleUser>();
        wrapper.setSql("energy_beans = energy_beans + 2000");
        wrapper.eq("id", articleUser.getId());
        articleUserService.update(new ArticleUser(), wrapper);

        // 发送系统消息
        String format = DateUtils.format(articleUser.getMembershipExpiration(), DateUtils.DEFAULT_DATE_TIME_FORMAT);
        ArticleUserNotice notice = new ArticleUserNotice();
        notice.setReadStatus(0);
        notice.setUserId(articleUser.getId());
        notice.setNoticeContent("购买会员成功，会员有效期截止" + format);
        notice.setNoticeType(ArticleNoticeType.recharge);
        articleUserNoticeService.save(notice);

        // 更新能量豆明细
        ArticleUserConsumption consumption = new ArticleUserConsumption();
        consumption.setUserId(articleUser.getId());
        consumption.setConsumptionType(ConsumptionType.MEMBERSHIP_RECHARGE);
        consumption.setConsumptionAmount(articleUserPayConfig.getAnnualGetEnergyBeans());
        consumption.setMessageRemark("购买会员成功");
        consumptionService.save(consumption);

        // 买会员送一次声音克隆
        ArticleUserFreeLimit freeLimit = new ArticleUserFreeLimit();
        freeLimit.setUserId(articleUser.getId());
        freeLimit.setHumanRemainingTimes(0);
        freeLimit.setVoiceRemainingTimes(1);
        freeLimit.setExpirationDate(LocalDateTime.now().plusYears(20));
        articleUserFreeLimitService.save(freeLimit);

        SseEmitter emitter = SseEmitterSession.get(userOrder.getUid());
        if  (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(UUID.fastUUID().toString())
                        .data(status)
                        .reconnectTime(3000));
                emitter.complete();
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Autowired
    ArticleAccountConsumptionService articleAccountConsumptionService;

    @Autowired
    ArticleUserAccountService articleUserAccountService;

    /**
     * 购买 充值 能量豆
     */
    @Transactional
    public void buyEnergyBeans(ArticleUserOrder userOrder, Long userId) {
        ArticleOrderGoodsType goodsType = userOrder.getGoodsType();

        Integer energyBeansNumber = articleUserPayConfig.getEnergyBeansNumber(goodsType);

        ArticleUser articleUser = articleUserService.getById(userId);
        articleUserAccountService.addEnergyBeans(articleUser.getUserMobile(), energyBeansNumber);

        // 发送系统消息
        ArticleUserNotice notice = new ArticleUserNotice();
        notice.setReadStatus(0);
        notice.setUserId(userId);
        if (StrUtil.isNotBlank(articleUser.getUserName())) {
            notice.setNoticeContent(articleUser.getUserName() + "，您的能量豆充值成功，本次充值能量豆" + energyBeansNumber + "个。");
        } else {
            notice.setNoticeContent("您的能量豆充值成功，本次充值能量豆" + energyBeansNumber + "个。");
        }
        notice.setNoticeType(ArticleNoticeType.recharge);
        articleUserNoticeService.save(notice);

        // 更新能量豆明细
        ArticleUserConsumption consumption = new ArticleUserConsumption();
        consumption.setUserId(userId);
        consumption.setConsumptionType(ConsumptionType.ENERGY_RECHARGE);
        consumption.setConsumptionAmount(energyBeansNumber);
        consumption.setMessageRemark("购买能量豆包");
        consumptionService.save(consumption);

        articleAccountConsumptionService.save(ArticleAccountConsumption.builder()
                .userMobile(articleUser.getUserMobile())
                .sourceType(ArticleAccountConsumption.sourceTypeArticle)
                .consumptionId(consumption.getId())
                .build());

        SseEmitter emitter = SseEmitterSession.get(userOrder.getUid());
        if  (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(UUID.fastUUID().toString())
                        .data(PaymentStatus.SUCCESS)
                        .reconnectTime(3000));
                emitter.complete();
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }


    @Transactional
    @Override
    public void callUpdateWechatOrder(Long orderId, PaymentStatus status) {

        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(orderId.toString(), 5000L, 20);
            if (lockBoolean) {
                ArticleUserOrder userOrder = baseMapper.selectById(orderId);
                if (userOrder != null && PaymentStatus.SUCCESS.equals(status)) {
                    ArticleOrderGoodsType goodsType = userOrder.getGoodsType();
                    if (goodsType.equals(ArticleOrderGoodsType.annual)) {
                        annualMember(userOrder, status);
                    } else {
                        // 购买能量豆
                        buyEnergyBeans(userOrder, userOrder.getCreateUser());
                    }
                }
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(orderId.toString());
            }
        }

    }

    /**
     * 关闭连接
     * @param uid
     */
    @Override
    public void closeSse(String uid) {

        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter != null) {
            emitter.complete();
            SseEmitterSession.remove(uid);
        }

    }

    /**
     * 创建一个sse 和前端建立联系，等待支付状态变化
     * @param uid
     * @return
     */
    @Override
    public SseEmitter createdSee(String uid) {
        // 默认30分钟超时,设置为0L则永不超时
        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter != null) {
            SseEmitterSession.put(uid, emitter);
            return emitter;
        }
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);

        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            SseEmitterSession.remove(uid);
        });

        //超时回调
        sseEmitter.onTimeout(() -> log.info("[{}]连接超时...................", uid));
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data("ERROR")
                                .reconnectTime(3000));
                        SseEmitterSession.put(uid, sseEmitter);
                    } catch (IOException e) {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                    }
                }
        );

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            log.error("", e);
        }
        log.info("[{}]创建sse连接成功！", uid);
        return sseEmitter;
    }

}
