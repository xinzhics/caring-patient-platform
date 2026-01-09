package com.caring.sass.ai.call.service.impl;

import cn.hutool.core.lang.UUID;
import com.caring.sass.ai.call.config.CallCostConfig;
import com.caring.sass.ai.call.dao.CallConfigMapper;
import com.caring.sass.ai.call.dao.CallRechargeOrderMapper;
import com.caring.sass.ai.call.service.CallRechargeOrderService;
import com.caring.sass.ai.dto.know.KnowMemberType;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.call.CallConfig;
import com.caring.sass.ai.entity.call.CallRechargeOrder;
import com.caring.sass.ai.entity.know.KnowledgeUserOrder;
import com.caring.sass.ai.entity.know.KnowledgeUserSubscribe;
import com.caring.sass.ai.entity.know.MembershipLevel;
import com.caring.sass.ai.know.config.KnowWxPayConfig;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.wx.WechatOrdersApi;
import com.caring.sass.wx.dto.config.WechatMinruiOrdersSaveDTO;
import com.caring.sass.wx.dto.config.WechatOrdersPageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 通话充值订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@Slf4j
@Service

public class CallRechargeOrderServiceImpl extends SuperServiceImpl<CallRechargeOrderMapper, CallRechargeOrder> implements CallRechargeOrderService {


    @Autowired
    WechatOrdersApi wechatOrdersApi;

    @Autowired
    CallCostConfig callCostConfig;

    @Autowired
    CallConfigMapper callConfigMapper;

    @Autowired
    KnowWxPayConfig knowWxPayConfig;

    @Autowired
    KnowledgeUserService knowledgeUserService;

    @Autowired
    DistributedLock distributedLock;



    /**
     * 计算这些分钟数需要多少金额 单位分
     * @param minute
     * @return
     */
    public Long calculateAmount(Integer minute) {

        if (minute < 10) {
            throw new BizException("充值时间不能小于10分钟");
        }

        // 每分钟需要的金额
        Integer amountPerMinute = callCostConfig.getAmountPerMinute();
        int i = minute * amountPerMinute;
        return Long.valueOf(i);
    }

    @Override
    public CallRechargeOrder createdH5WechatOrder(Long userId, String uid, CallConfig callConfig, String clientIp) {

        CallConfig config = callConfigMapper.selectById(callConfig.getId());
        Long cost = 0L;
        String minuteQuantity;
        if (callConfig.getMinuteQuantity().equals("other")) {
            cost = calculateAmount(callConfig.getMinute());
            minuteQuantity = callConfig.getMinute() + "";
        } else {
            cost = config.getCost();
            minuteQuantity = config.getMinuteQuantity();
        }

        CallRechargeOrder userOrder = new CallRechargeOrder();
        userOrder.setUserId(userId);
        userOrder.setGoodsType("通话时长" + minuteQuantity + "分钟");
        userOrder.setMinuteDuration(Integer.valueOf(minuteQuantity));
        userOrder.setPaymentStatus(PaymentStatus.NO_PAY);
        userOrder.setSeeUid(uid);
        userOrder.setGoodsPrice(Integer.valueOf(cost + ""));

        baseMapper.insert(userOrder);

        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();

        saveDTO.setDescription(userOrder.getGoodsType());
        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setMerchantId(knowWxPayConfig.getMerchantId());
        saveDTO.setAppId(knowWxPayConfig.getAppId());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setBusinessType(CallRechargeOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/callRechargeOrder/anno/" + userOrder.getId() + "/status?status=");
        saveDTO.setPayerClientIp(clientIp);
        R<String> nativeOrders = wechatOrdersApi.createWechatNativeOrders(saveDTO);
        if (!nativeOrders.getIsSuccess() || Objects.isNull(nativeOrders.getData())) {
            baseMapper.deleteById(userOrder.getId());
            throw new BizException("创建订单失败");
        }
        userOrder.setWxPayUrl(nativeOrders.getData());
        return userOrder;
    }


    @Override
    public CallRechargeOrder queryOrderStatus(Long orderId) {
        CallRechargeOrder userOrder = baseMapper.selectOne(Wraps.<CallRechargeOrder>lbQ()
                .eq(CallRechargeOrder::getId, orderId));
        if (userOrder.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            return userOrder;
        }
        WechatOrdersPageDTO dto = new WechatOrdersPageDTO();
        dto.setBusinessId(userOrder.getId());
        dto.setBusinessType(CallRechargeOrder.class.getSimpleName());
        R<Boolean> nativeOrders = wechatOrdersApi.queryWechatNativeOrders(dto);
        if (nativeOrders.getIsSuccess()) {
            Boolean data = nativeOrders.getData();
            if (data != null && data) {
                userOrder = baseMapper.selectOne(Wraps.<CallRechargeOrder>lbQ()
                        .eq(CallRechargeOrder::getId, orderId));
            }
        }
        return userOrder;
    }


    @Override
    public void callUpdateWechatOrder(Long orderId, PaymentStatus status) {

        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(orderId.toString(), 5000L, 20);

            if (lockBoolean) {
                CallRechargeOrder userOrder = baseMapper.selectById(orderId);
                if (userOrder != null && PaymentStatus.SUCCESS.equals(status)) {
                    if (PaymentStatus.SUCCESS.equals(userOrder.getPaymentStatus())) {
                        return;
                    }
                    userOrder.setPaymentStatus(status);
                    userOrder.setPaymentTime(LocalDateTime.now());
                    baseMapper.updateById(userOrder);

                    knowledgeUserService.updateCallDuration(userOrder.getUserId(), userOrder.getMinuteDuration());
                    SseEmitter emitter = SseEmitterSession.get(userOrder.getSeeUid());
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
            }

        } catch (Exception c) {

        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(orderId.toString());
            }
        }

    }

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
