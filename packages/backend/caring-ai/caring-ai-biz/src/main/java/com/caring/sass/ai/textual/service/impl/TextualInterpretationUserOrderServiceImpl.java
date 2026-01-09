package com.caring.sass.ai.textual.service.impl;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.service.ArticleAccountConsumptionService;
import com.caring.sass.ai.article.service.ArticleUserAccountService;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.know.KnowledgeUserOrder;
import com.caring.sass.ai.entity.textual.*;
import com.caring.sass.ai.textual.config.TextualUserPayConfig;
import com.caring.sass.ai.textual.dao.TextualInterpretationUserOrderMapper;
import com.caring.sass.ai.textual.service.TextualInterpretationUserConsumptionService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserNoticeService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserOrderService;
import com.caring.sass.ai.textual.service.TextualInterpretationUserService;
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
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 文献解读用户支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
@Slf4j
@Service

public class TextualInterpretationUserOrderServiceImpl extends SuperServiceImpl<TextualInterpretationUserOrderMapper, TextualInterpretationUserOrder> implements TextualInterpretationUserOrderService {


    @Autowired
    TextualUserPayConfig textualUserPayConfig;

    @Autowired
    TextualInterpretationUserService textualInterpretationUserService;

    @Autowired
    WechatOrdersApi wechatOrdersApi;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    TextualInterpretationUserConsumptionService consumptionService;

    @Autowired
    TextualInterpretationUserNoticeService textualInterpretationUserNoticeService;

    @Autowired
    ArticleUserAccountService articleUserAccountService;

    @Autowired
    ArticleAccountConsumptionService articleAccountConsumptionService;


    public TextualInterpretationUserOrder createUserOrder(TextualInterpretationUser user, ArticleOrderGoodsType goodsType, String uid, SseEmitter emitter) {

        TextualInterpretationUserOrder userOrder = new TextualInterpretationUserOrder();
        userOrder.setCreateUser(user.getId());
        userOrder.setGoodsType(goodsType);
        userOrder.setGoodsPrice(textualUserPayConfig.getCost(goodsType));
        userOrder.setPaymentStatus(PaymentStatus.NO_PAY);
        userOrder.setUid(uid);
        baseMapper.insert(userOrder);
        return userOrder;
    }

    @Override
    public WechatOrders createdJSAPIOrder(ArticleUserOrderGoods articleUserOrderGoods) {

        TextualInterpretationUser user = textualInterpretationUserService.getById(BaseContextHandler.getUserId());
        ArticleOrderGoodsType orderGoodsType = articleUserOrderGoods.getOrderGoodsType();
        // 下单不是会员产品。优先要求购买会员产品
        if (!orderGoodsType.equals(ArticleOrderGoodsType.annual)) {
            ArticleMembershipLevel level = user.getMembershipLevel();
            if (level == null || user.getMembershipExpiration().isBefore(LocalDateTime.now())) {
                throw new BizException("您不是会员，请先购买会员");
            }
        }

        TextualInterpretationUserOrder userOrder = createUserOrder(user, orderGoodsType, null, null);

        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        saveDTO.setDescription(textualUserPayConfig.getCostName(orderGoodsType));

        saveDTO.setOpenId(articleUserOrderGoods.getOpenId());
        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setMerchantId(textualUserPayConfig.getMerchantId());
        saveDTO.setAppId(textualUserPayConfig.getGongnZhifuAppId());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setBusinessType(TextualInterpretationUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/textualInterpretationUserOrder/anno/" + userOrder.getId() + "/status?status=");
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

        TextualInterpretationUser user = textualInterpretationUserService.getById(BaseContextHandler.getUserId());
        ArticleOrderGoodsType orderGoodsType = articleUserOrderGoods.getOrderGoodsType();
        // 下单不是会员产品。优先要求购买会员产品
        if (!orderGoodsType.equals(ArticleOrderGoodsType.annual)) {
            ArticleMembershipLevel level = user.getMembershipLevel();
            if (level == null || user.getMembershipExpiration().isBefore(LocalDateTime.now())) {
                throw new BizException("您不是会员，请先购买会员");
            }
        }

        String uid = articleUserOrderGoods.getUid();
        TextualInterpretationUserOrder userOrder = createUserOrder(user, orderGoodsType, uid, null);

        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        saveDTO.setDescription(textualUserPayConfig.getCostName(orderGoodsType));

        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setMerchantId(textualUserPayConfig.getMerchantId());
        saveDTO.setAppId(textualUserPayConfig.getAppId());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setBusinessType(TextualInterpretationUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/textualInterpretationUserOrder/anno/" + userOrder.getId() + "/status?status=");
        R<String> nativeOrders = wechatOrdersApi.createWechatNativeOrders(saveDTO);
        if (!nativeOrders.getIsSuccess() || Objects.isNull(nativeOrders.getData())) {
            throw new BizException("创建订单失败");
        }
        return nativeOrders.getData();
    }


    @Override
    public TextualInterpretationUserOrder queryOrderStatus(ArticleUserOrderGoods memberPayOrder) {

        TextualInterpretationUserOrder userOrder = baseMapper.selectOne(Wraps.<TextualInterpretationUserOrder>lbQ()
                .eq(TextualInterpretationUserOrder::getUid, memberPayOrder.getUid())
                .eq(TextualInterpretationUserOrder::getGoodsType, memberPayOrder.getOrderGoodsType())
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
                userOrder = baseMapper.selectOne(Wraps.<TextualInterpretationUserOrder>lbQ()
                        .eq(TextualInterpretationUserOrder::getUid, memberPayOrder.getUid())
                        .eq(TextualInterpretationUserOrder::getGoodsType, memberPayOrder.getOrderGoodsType())
                        .orderByDesc(SuperEntity::getCreateTime)
                        .last("limit 1"));
            }
        }
        return userOrder;
    }




    /**
     * 购买 充值 能量豆
     */
    @Transactional
    public void buyEnergyBeans(TextualInterpretationUserOrder userOrder, Long userId) {
        ArticleOrderGoodsType goodsType = userOrder.getGoodsType();

        Integer energyBeansNumber = textualUserPayConfig.getEnergyBeansNumber(goodsType);

        TextualInterpretationUser articleUser = textualInterpretationUserService.getById(userId);
        articleUserAccountService.addEnergyBeans(articleUser.getUserMobile(), energyBeansNumber);

        // 发送系统消息
        TextualInterpretationUserNotice notice = new TextualInterpretationUserNotice();
        notice.setReadStatus(0);
        notice.setUserId(userId);
        if (StrUtil.isNotBlank(articleUser.getUserName())) {
            notice.setNoticeContent(articleUser.getUserName() + "，您的能量豆充值成功，本次充值能量豆" + energyBeansNumber + "个。");
        } else {
            notice.setNoticeContent("您的能量豆充值成功，本次充值能量豆" + energyBeansNumber + "个。");
        }
        notice.setNoticeType(TextualNoticeType.recharge);
        textualInterpretationUserNoticeService.save(notice);

        // 更新能量豆明细
        TextualInterpretationUserConsumption consumption = new TextualInterpretationUserConsumption();
        consumption.setUserId(userId);
        consumption.setConsumptionType(TextualConsumptionType.ENERGY_RECHARGE);
        consumption.setConsumptionAmount(energyBeansNumber);
        consumption.setMessageRemark("购买能量豆包");
        consumptionService.save(consumption);

        articleAccountConsumptionService.save(ArticleAccountConsumption.builder()
                .userMobile(articleUser.getUserMobile())
                .sourceType(ArticleAccountConsumption.sourceTypeTextual)
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
                TextualInterpretationUserOrder userOrder = baseMapper.selectById(orderId);
                if (userOrder != null && PaymentStatus.SUCCESS.equals(status)) {
                    ArticleOrderGoodsType goodsType = userOrder.getGoodsType();
                    if (goodsType.equals(ArticleOrderGoodsType.annual)) {
//                        annualMember(userOrder, status);
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
