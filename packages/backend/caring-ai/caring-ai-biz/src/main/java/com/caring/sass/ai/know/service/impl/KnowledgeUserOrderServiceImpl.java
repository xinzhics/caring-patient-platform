package com.caring.sass.ai.know.service.impl;



import cn.hutool.core.lang.UUID;

import com.caring.sass.ai.dto.know.KnowMemberType;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.dto.know.UserMemberPayOrder;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.config.KnowWxPayConfig;
import com.caring.sass.ai.know.dao.KnowledgeUserOrderMapper;
import com.caring.sass.ai.know.dao.KnowledgeUserSubscribeMapper;
import com.caring.sass.ai.know.service.KnowledgeUserOrderService;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.wx.WechatOrdersApi;
import com.caring.sass.wx.dto.config.WechatMinruiOrdersSaveDTO;
import com.caring.sass.wx.dto.config.WechatOrdersPageDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
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
 * 知识库用户购买会员订单
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-11
 */
@Slf4j
@Service

public class KnowledgeUserOrderServiceImpl extends SuperServiceImpl<KnowledgeUserOrderMapper, KnowledgeUserOrder> implements KnowledgeUserOrderService {

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    WechatOrdersApi wechatOrdersApi;

    @Autowired
    KnowWxPayConfig knowWxPayConfig;

    @Autowired
    KnowledgeUserService knowledgeUserService;

    @Autowired
    KnowledgeUserSubscribeMapper knowledgeUserSubscribeMapper;


    /**
     * 计算给定的LocalDateTime与当前时间相差的天数，不足1天按0天计算。
     * @param dateTime 给定的时间
     * @return 相差的天数
     */
//    public static long daysBetweenNowAnd(LocalDateTime dateTime) {
//        // 获取当前时间
//        LocalDateTime now = LocalDateTime.now();
//
//        // 计算天数
//        long days = ChronoUnit.DAYS.between(now.toLocalDate(), dateTime.toLocalDate());
//
//        // 不足一天则返回0
//        return days > 0 ? days : 0;
//    }


    /**
     * 计算剩余天数对应的可抵扣金额。
     * @param daysed 剩余天数
     * @return 可抵扣金额
     */
//    public int calculateDeductibleAmount(long daysed, KnowledgeUser knowledgeUser) {
//        int basicMembershipPrice = knowWxPayConfig.getBasicMembershipPrice(knowledgeUser);
//        BigDecimal dailyPrice = new BigDecimal(basicMembershipPrice).divide(new BigDecimal(365), 2, RoundingMode.HALF_UP);
//        BigDecimal deductibleAmount = dailyPrice.multiply(new BigDecimal(daysed));
//
//        return deductibleAmount.intValue();
//    }

    /**
     * 检查购买会员的这个人是否可以下单
     * @param memberPayOrder
     */
    public void checkUserInfo(UserMemberPayOrder memberPayOrder, KnowledgeUser user) {
        // 检查用户是否还是专业版会员。并且会员还没到期。
        user = knowledgeUserService.getById(BaseContextHandler.getUserId());

        if (user == null) {
            throw new BizException("用户不存在");
        }
        // 博主在自己的网站，不需要购买会员
        if (KnowDoctorType.CHIEF_PHYSICIAN.equals(user.getUserType())
                && memberPayOrder.getUserDomain().equals(user.getUserDomain())) {
            throw new BizException("尊贵的博主，您不需要购买会员");
        }

        // 查询用户在博主的订阅表
//        KnowledgeUserSubscribe userSubscribe = getKnowledgeUserSubscribe(user.getId(), memberPayOrder.getUserDomain());
//        if (userSubscribe != null) {
//            // 专业版会员 未到期，不能购买 体验版会员
//            if (MembershipLevel.PROFESSIONAL_EDITION.equals(userSubscribe.getMembershipLevel())) {
//                LocalDateTime expiration = userSubscribe.getMembershipExpiration();
//                if (expiration != null && expiration.isAfter(LocalDateTime.now())) {
//                    if (memberPayOrder.getMemberType().equals(KnowMemberType.trialVersion)) {
//                        throw new BizException("您现在是专业版会员，到期后才可购买体验版会员");
//                    }
//                }
//            }
//        }


    }


    /**
     * 创建一个 用户 在 博主网站购买会员的订单
     * @param userId
     * @param memberPayOrder
     * @param memberType
     * @param uid
     * @param emitter
     * @param chiefPhysician
     * @return
     */
    public KnowledgeUserOrder createUserOrder(Long userId, UserMemberPayOrder memberPayOrder, KnowMemberType memberType, String uid, SseEmitter emitter, KnowledgeUser chiefPhysician) {

//        int deduction = 0;
//        KnowledgeUserSubscribe userSubscribe = getKnowledgeUserSubscribe(userId, memberPayOrder.getUserDomain());

//        // 如果是体验版会员， 还未到期，要购买专业版会员， 计算 体验版会员剩余时长，可抵扣金额。
//        if (userSubscribe != null) {
//            if (MembershipLevel.TRIAL_VERSION.equals(userSubscribe.getMembershipLevel())) {
//                // 会员到期时间
//                LocalDateTime expiration = userSubscribe.getMembershipExpiration();
//                if (expiration != null && expiration.isAfter(LocalDateTime.now())) {
//                    // 计算会员到期时间和当前时间相差天数，不足1天按0天计算。
//                    // 体验版会员剩余天数，可抵扣金额。
//                    long daysed = daysBetweenNowAnd(expiration);
//                    if (daysed > 0) {
//                        // 剩余天数，可抵扣金额。
//                        deduction = calculateDeductibleAmount(daysed, chiefPhysician);
//                    }
//                }
//            }
//        }

        KnowledgeUserOrder userOrder = new KnowledgeUserOrder();
        userOrder.setUserId(userId);
        userOrder.setUserDomain(memberPayOrder.getUserDomain());
        userOrder.setGoodsType(memberType);
        if (KnowMemberType.professionalEdition.equals(memberType)) {
            userOrder.setGoodsPrice(knowWxPayConfig.getProfessionalVersionPrice(chiefPhysician));
        } else if (KnowMemberType.trialVersion.equals(memberType)) {
            userOrder.setGoodsPrice(knowWxPayConfig.getBasicMembershipPrice(chiefPhysician));
        } else {
            if (emitter != null) {
                emitter.complete();
            }
            throw new BizException("未知的会员类型");
        }
        userOrder.setPaymentStatus(PaymentStatus.NO_PAY);
        userOrder.setSeeUid(uid);
        baseMapper.insert(userOrder);
        return userOrder;
    }


    /**
     * 创建一个jsApi订单
     * @param memberPayOrder
     * @return
     */
    @Override
    public WechatOrders createdJSAPIOrder(UserMemberPayOrder memberPayOrder) {
        KnowledgeUser user = knowledgeUserService.getById(BaseContextHandler.getUserId());

        KnowledgeUser chiefPhysician = knowledgeUserService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserDomain, memberPayOrder.getUserDomain())
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last(" limit 0,1 "));
        if (chiefPhysician == null) {
            throw new BizException("您所在的项目没有主任医生");
        }


        checkUserInfo(memberPayOrder, user);
        KnowMemberType memberType = memberPayOrder.getMemberType();
        String uid = memberPayOrder.getUid();

        KnowledgeUserOrder userOrder = createUserOrder(user.getId(), memberPayOrder, memberType, uid, null, chiefPhysician);
        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        if (KnowMemberType.professionalEdition.equals(memberType)) {
            saveDTO.setDescription(knowWxPayConfig.getProfessionalVersionName());
        } else if (KnowMemberType.trialVersion.equals(memberType)) {
            saveDTO.setDescription(knowWxPayConfig.getBasicMembershipName());
        } else {
            throw new BizException("未知的会员类型");
        }

        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setMerchantId(knowWxPayConfig.getMerchantId());
        saveDTO.setAppId(memberPayOrder.getAppId());
        saveDTO.setBusinessType(KnowledgeUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/knowledgeUserOrder/anno/" + userOrder.getId() + "/status?status=");
        saveDTO.setOpenId(memberPayOrder.getOpenId() != null ? memberPayOrder.getOpenId() : user.getOpenId());
        R<WechatOrders> wechatJSAPIOrders = wechatOrdersApi.createWechatJSAPIOrders(saveDTO);
        if (!wechatJSAPIOrders.getIsSuccess() || Objects.isNull(wechatJSAPIOrders.getData())) {
            throw new BizException("创建订单失败");
        }
        return wechatJSAPIOrders.getData();

    }

    @Override
    public String createdH5WechatOrder(UserMemberPayOrder memberPayOrder, String clientIp) {
        KnowledgeUser user = knowledgeUserService.getById(BaseContextHandler.getUserId());
        checkUserInfo(memberPayOrder, user);
        KnowMemberType memberType = memberPayOrder.getMemberType();
        String uid = memberPayOrder.getUid();
        KnowledgeUser chiefPhysician = knowledgeUserService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserDomain, memberPayOrder.getUserDomain())
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last(" limit 0,1 "));
        if (chiefPhysician == null) {
            throw new BizException("您所在的项目没有博主");
        }
        KnowledgeUserOrder userOrder = createUserOrder(user.getId(), memberPayOrder, memberType, uid, null, chiefPhysician);
        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        if (KnowMemberType.professionalEdition.equals(memberType)) {
            saveDTO.setDescription(knowWxPayConfig.getProfessionalVersionName());
        } else if (KnowMemberType.trialVersion.equals(memberType)) {
            saveDTO.setDescription(knowWxPayConfig.getBasicMembershipName());
        } else {
            throw new BizException("未知的会员类型");
        }

        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setMerchantId(knowWxPayConfig.getMerchantId());
        saveDTO.setAppId(knowWxPayConfig.getAppId());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setBusinessType(KnowledgeUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/knowledgeUserOrder/anno/" + userOrder.getId() + "/status?status=");
        saveDTO.setPayerClientIp(clientIp);
        R<String> nativeOrders = wechatOrdersApi.createWechatH5Orders(saveDTO);
        if (!nativeOrders.getIsSuccess() || Objects.isNull(nativeOrders.getData())) {
            throw new BizException("创建订单失败");
        }
        return nativeOrders.getData();

    }


    /**
     * 创建一个微信支付的订单
     * @param memberPayOrder
     * @return
     */
    @Override
    public String createdWechatOrder(UserMemberPayOrder memberPayOrder) {
        KnowMemberType memberType = memberPayOrder.getMemberType();
        String uid = memberPayOrder.getUid();
        KnowledgeUser user = knowledgeUserService.getById(BaseContextHandler.getUserId());
        SseEmitter emitter = SseEmitterSession.get(uid);

        try {
            checkUserInfo(memberPayOrder, user);
        } catch (Exception e) {
            if (emitter != null) {
                emitter.complete();
            }
            throw new BizException(e.getMessage());
        }

        KnowledgeUser chiefPhysician = knowledgeUserService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserDomain, memberPayOrder.getUserDomain())
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last(" limit 0,1 "));
        if (chiefPhysician == null) {
            throw new BizException("您所在的项目没有博主");
        }

        KnowledgeUserOrder userOrder = createUserOrder(user.getId(), memberPayOrder, memberType, uid, emitter, chiefPhysician);
        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        if (KnowMemberType.professionalEdition.equals(memberType)) {
            saveDTO.setDescription(knowWxPayConfig.getProfessionalVersionName());
        } else if (KnowMemberType.trialVersion.equals(memberType)) {
            saveDTO.setDescription(knowWxPayConfig.getBasicMembershipName());
        } else {
            throw new BizException("未知的会员类型");
        }
        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setMerchantId(knowWxPayConfig.getMerchantId());
        saveDTO.setAppId(knowWxPayConfig.getAppId());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setBusinessType(KnowledgeUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/knowledgeUserOrder/anno/" + userOrder.getId() + "/status?status=");
        R<String> nativeOrders = wechatOrdersApi.createWechatNativeOrders(saveDTO);
        if (!nativeOrders.getIsSuccess() || Objects.isNull(nativeOrders.getData())) {
            if (emitter != null) {
                emitter.complete();
            }
            throw new BizException("创建订单失败");
        }
        return nativeOrders.getData();

    }


    /**
     * 主动查询微信订单的支付情况
     * @param memberPayOrder
     * @return
     */
    @Override
    public KnowledgeUserOrder queryOrderStatus(UserMemberPayOrder memberPayOrder) {

        KnowledgeUserOrder userOrder = baseMapper.selectOne(Wraps.<KnowledgeUserOrder>lbQ()
                .eq(KnowledgeUserOrder::getSeeUid, memberPayOrder.getUid())
                .eq(KnowledgeUserOrder::getGoodsType, memberPayOrder.getMemberType())
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
                userOrder = baseMapper.selectOne(Wraps.<KnowledgeUserOrder>lbQ()
                        .eq(KnowledgeUserOrder::getSeeUid, memberPayOrder.getUid())
                        .eq(KnowledgeUserOrder::getGoodsType, memberPayOrder.getMemberType())
                        .orderByDesc(SuperEntity::getCreateTime)
                        .last("limit 1"));
            }
        }
        return userOrder;
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
     * 查询用户和博主网站的订阅 会员关系
     * @param userId
     * @param userDomain
     * @return
     */
    public KnowledgeUserSubscribe getKnowledgeUserSubscribe(Long userId, String userDomain) {
        return knowledgeUserSubscribeMapper.selectOne(Wraps.<KnowledgeUserSubscribe>lbQ()
                .eq(KnowledgeUserSubscribe::getKnowledgeUserId, userId)
                .eq(KnowledgeUserSubscribe::getMembershipLevel, MembershipLevel.PROFESSIONAL_EDITION)
                .eq(KnowledgeUserSubscribe::getUserDomain, userDomain)
                .last(" limit 0,1 "));
    }

    /**
     * 支付状态回调
     * @param orderId
     * @param status
     */
    @Override
    public void callUpdateWechatOrder(Long orderId, PaymentStatus status) {

        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(orderId.toString(), 5000L, 20);
            if (lockBoolean) {
                KnowledgeUserOrder userOrder = baseMapper.selectById(orderId);
                if (userOrder != null && PaymentStatus.SUCCESS.equals(status)) {
                    if (PaymentStatus.SUCCESS.equals(userOrder.getPaymentStatus())) {
                        return;
                    }
                    userOrder.setPaymentStatus(status);
                    userOrder.setPaymentTime(LocalDateTime.now());
                    baseMapper.updateById(userOrder);

                    KnowledgeUserSubscribe userSubscribe = getKnowledgeUserSubscribe(userOrder.getUserId(), userOrder.getUserDomain());
                    if (userSubscribe == null) {
                        userSubscribe = new KnowledgeUserSubscribe();
                        userSubscribe.setKnowledgeUserId(userOrder.getUserId());
                        userSubscribe.setUserDomain(userOrder.getUserDomain());
                        knowledgeUserSubscribeMapper.insert(userSubscribe);
                    }
                    // 购买 年度 会员
                    if (userOrder.getGoodsType().equals(KnowMemberType.professionalEdition)) {
                        // 如果会员还没到期
                        if (userSubscribe.getMembershipExpiration() != null &&
                                userSubscribe.getMembershipExpiration().isAfter(LocalDateTime.now())) {
                            //  会员没到期。那么就是会员续费。
                            userSubscribe.setMembershipExpiration(userSubscribe.getMembershipExpiration().plusDays(365));
                        } else {
                            // 会员到期了。直接从现在开始计算
                            userSubscribe.setMembershipExpiration(LocalDateTime.now().plusDays(365));
                        }

                        userSubscribe.setMembershipLevel(MembershipLevel.PROFESSIONAL_EDITION);
                    } else if (userOrder.getGoodsType().equals(KnowMemberType.trialVersion)) {
                        // 用户购买的 体验版会员， 会员没到期 那就续费。
                        if (userSubscribe.getMembershipExpiration() != null &&
                                userSubscribe.getMembershipExpiration().isAfter(LocalDateTime.now())) {
                            userSubscribe.setMembershipExpiration(userSubscribe.getMembershipExpiration().plusDays(30));
                        } else {
                            // 会员到期了。直接从现在开始计算
                            userSubscribe.setMembershipExpiration(LocalDateTime.now().plusDays(30));
                        }

                        userSubscribe.setMembershipLevel(MembershipLevel.PROFESSIONAL_EDITION);
                    }
                    userSubscribe.setSubscribeStatus(true);
                    userSubscribe.setSubscribeTime(LocalDateTime.now());
                    userOrder.setMembershipExpiration(userSubscribe.getMembershipExpiration());
                    baseMapper.updateById(userOrder);

                    knowledgeUserSubscribeMapper.updateById(userSubscribe);

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
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(orderId.toString());
            }
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



    @Override
    public KnowledgeUserOrder saveFreeOrder(Long userId, String domain) {


        KnowledgeUserOrder userOrder = new KnowledgeUserOrder();
        userOrder.setUserId(userId);
        userOrder.setUserDomain(domain);
        userOrder.setGoodsType(KnowMemberType.Free);
        userOrder.setGoodsPrice(0);
        userOrder.setPaymentStatus(PaymentStatus.SUCCESS);
        userOrder.setPaymentTime(LocalDateTime.now());
        userOrder.setMembershipExpiration(LocalDateTime.now().plusYears(40));
        baseMapper.insert(userOrder);

        KnowledgeUserSubscribe userSubscribe = knowledgeUserSubscribeMapper.selectOne(Wraps.<KnowledgeUserSubscribe>lbQ()
                .eq(KnowledgeUserSubscribe::getKnowledgeUserId, userId)
                .eq(KnowledgeUserSubscribe::getMembershipLevel, MembershipLevel.FREE_VERSION)
                .eq(KnowledgeUserSubscribe::getUserDomain, userOrder.getUserDomain())
                .last(" limit 0,1 "));
        if (userSubscribe == null) {
            userSubscribe = new KnowledgeUserSubscribe();
            userSubscribe.setKnowledgeUserId(userOrder.getUserId());
            userSubscribe.setUserDomain(userOrder.getUserDomain());
            userSubscribe.setSubscribeTime(LocalDateTime.now());
            knowledgeUserSubscribeMapper.insert(userSubscribe);
        }
        if (userSubscribe.getMembershipLevel() == null) {
            userSubscribe.setMembershipLevel(MembershipLevel.FREE_VERSION);
        }
        userSubscribe.setSubscribeStatus(true);
        knowledgeUserSubscribeMapper.updateById(userSubscribe);

        return userOrder;
    }
}
