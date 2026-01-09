package com.caring.sass.ai.ckd.server.impl;



import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.ckd.config.CkdWxPayConfig;
import com.caring.sass.ai.ckd.dao.CkdUserOrderMapper;
import com.caring.sass.ai.ckd.server.CkdUserInfoService;
import com.caring.sass.ai.ckd.server.CkdUserOrderService;
import com.caring.sass.ai.dto.ckd.CkdMembershipLevel;
import com.caring.sass.ai.dto.ckd.CkdUserGoodsType;
import com.caring.sass.ai.dto.ckd.CkdUserOrderSaveDTO;
import com.caring.sass.ai.dto.know.KnowMemberType;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import com.caring.sass.ai.entity.ckd.CkdUserOrder;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * ckd会员订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Service

public class CkdUserOrderServiceImpl extends SuperServiceImpl<CkdUserOrderMapper, CkdUserOrder> implements CkdUserOrderService {

    @Autowired
    CkdUserInfoService ckdUserInfoService;

    @Autowired
    CkdWxPayConfig ckdWxPayConfig;

    @Autowired
    WechatOrdersApi wechatOrdersApi;

    @Autowired
    DistributedLock distributedLock;



    public CkdUserOrder createUserOrder(String openId, CkdUserGoodsType goodsType) {

        CkdUserOrder userOrder = new CkdUserOrder();
        userOrder.setOpenId(openId);
        userOrder.setGoodsType(goodsType);
        if (CkdUserGoodsType.member_9_9.equals(goodsType)) {
            userOrder.setGoodsPrice(ckdWxPayConfig.getMonthlyExpenses());
        } else if (CkdUserGoodsType.member99.equals(goodsType)) {
            userOrder.setGoodsPrice(ckdWxPayConfig.getAnnualCost());
        } else if (CkdUserGoodsType.member199.equals(goodsType)) {
            userOrder.setGoodsPrice(ckdWxPayConfig.getLifetime());
        } else {
            throw new BizException("未知的会员类型");
        }
        userOrder.setPaymentStatus(PaymentStatus.NO_PAY);
        baseMapper.insert(userOrder);
        return userOrder;
    }

    @Override
    public WechatOrders createdWechatOrder(CkdUserOrderSaveDTO orderSaveDTO) {

        // 如果用户已经时终身版。就不需要购买了。
        CkdUserInfo userInfo = ckdUserInfoService.getOne(Wraps.<CkdUserInfo>lbQ().eq(CkdUserInfo::getOpenId, orderSaveDTO.getOpenId()).last("limit 1"));
        if (userInfo != null && userInfo.getMembershipLevel().equals(CkdMembershipLevel.PERMANENT)) {
            throw new BizException("您已经是终身会员了");
        }

        CkdUserGoodsType goodsType = orderSaveDTO.getGoodsType();


        CkdUserOrder userOrder = createUserOrder(orderSaveDTO.getOpenId(), goodsType);
        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        if (CkdUserGoodsType.member_9_9.equals(goodsType)) {
            saveDTO.setDescription(ckdWxPayConfig.getMonthlyExpensesName());

        } else if (CkdUserGoodsType.member99.equals(goodsType)) {
            saveDTO.setDescription(ckdWxPayConfig.getAnnualCostName());

        } else if (CkdUserGoodsType.member199.equals(goodsType)) {
            saveDTO.setDescription(ckdWxPayConfig.getLifeTimeName());
        } else {
            throw new BizException("未知的会员类型");
        }


        saveDTO.setAmount(userOrder.getGoodsPrice());
        saveDTO.setBusinessId(userOrder.getId());
        saveDTO.setMerchantId(ckdWxPayConfig.getMerchantId());
        saveDTO.setAppId(ckdWxPayConfig.getAppId());
        saveDTO.setBusinessType(CkdUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/ckdUserOrder/anno/" + userOrder.getId() + "/status?status=");
        saveDTO.setOpenId(orderSaveDTO.getOpenId());
        R<WechatOrders> wechatJSAPIOrders = wechatOrdersApi.createWechatJSAPIOrders(saveDTO);
        if (!wechatJSAPIOrders.getIsSuccess() || Objects.isNull(wechatJSAPIOrders.getData())) {
            throw new BizException("创建订单失败");
        }
        return wechatJSAPIOrders.getData();
    }


    @Override
    public CkdUserOrder queryOrderStatus(Long ckdOrderId) {


        CkdUserOrder userOrder = baseMapper.selectById(ckdOrderId);
        if (userOrder.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            return userOrder;
        }
        WechatOrdersPageDTO dto = new WechatOrdersPageDTO();
        dto.setBusinessId(ckdOrderId);
        dto.setBusinessType(CkdUserOrder.class.getSimpleName());
        R<Boolean> nativeOrders = wechatOrdersApi.queryWechatNativeOrders(dto);
        if (nativeOrders.getIsSuccess()) {
            Boolean data = nativeOrders.getData();
            if (data != null && data) {
                userOrder = baseMapper.selectById(ckdOrderId);;
            }
        }
        return userOrder;
    }



    /**
     * 支付状态回调
     * @param orderId
     * @param status
     */
    @Transactional
    @Override
    public void callUpdateWechatOrder(Long orderId, PaymentStatus status) {

        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        try {
            lockBoolean = distributedLock.lock(orderId.toString(), 5000L, 20);
            if (lockBoolean) {
                CkdUserOrder userOrder = baseMapper.selectById(orderId);
                if (userOrder != null && PaymentStatus.SUCCESS.equals(status)) {
                    if (PaymentStatus.SUCCESS.equals(userOrder.getPaymentStatus())) {
                        return;
                    }
                    userOrder.setPaymentStatus(status);
                    baseMapper.updateById(userOrder);

                    CkdUserInfo ckdUserInfo = ckdUserInfoService.getByOpenId(userOrder.getOpenId());

                    if (userOrder.getGoodsType().equals(CkdUserGoodsType.member199)) {
                        ckdUserInfo.setMembershipLevel(CkdMembershipLevel.PERMANENT);
                    } else {
                        ckdUserInfo.setMembershipLevel(CkdMembershipLevel.TIME_LIMITED);

                        LocalDateTime expirationDate = ckdUserInfo.getExpirationDate();
                        if (expirationDate != null && expirationDate.isAfter(LocalDateTime.now())) {
                            if (userOrder.getGoodsType().equals(CkdUserGoodsType.member_9_9)) {
                                ckdUserInfo.setExpirationDate(ckdUserInfo.getExpirationDate().plusDays(30));
                            } else if (userOrder.getGoodsType().equals(CkdUserGoodsType.member99)) {
                                ckdUserInfo.setExpirationDate(ckdUserInfo.getExpirationDate().plusYears(365));
                            }
                        } else {
                            if (userOrder.getGoodsType().equals(CkdUserGoodsType.member_9_9)) {
                                ckdUserInfo.setExpirationDate(LocalDateTime.now().plusDays(30));
                            } else if (userOrder.getGoodsType().equals(CkdUserGoodsType.member99)) {
                                ckdUserInfo.setExpirationDate(LocalDateTime.now().plusYears(365));
                            }
                        }
                    }
                    ckdUserInfoService.updateById(ckdUserInfo);
                } else if (userOrder != null && PaymentStatus.REFUNDED.equals(status)) {
                    if (PaymentStatus.REFUNDED.equals(userOrder.getPaymentStatus())) {
                        return;
                    }
                    CkdUserInfo ckdUserInfo = ckdUserInfoService.getByOpenId(userOrder.getOpenId());
                    userOrder.setPaymentStatus(status);
                    userOrder.setRefundTime(LocalDateTime.now());
                    baseMapper.updateById(userOrder);
                    if (ckdUserInfo.getExpirationDate() != null && ckdUserInfo.getExpirationDate().isAfter(LocalDateTime.now())) {
                        ckdUserInfo.setMembershipLevel(CkdMembershipLevel.TIME_LIMITED);
                    } else {
                        ckdUserInfo.setMembershipLevel(CkdMembershipLevel.LOSE_EFFICACY);
                    }
                    ckdUserInfoService.updateById(ckdUserInfo);
                }
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(orderId.toString());
            }
        }


    }


    @Override
    public CkdUserOrder refund(Long ckdOrderId) {

        CkdUserOrder ckdUserOrder = baseMapper.selectById(ckdOrderId);
        CkdUserGoodsType goodsType = ckdUserOrder.getGoodsType();
        if (!goodsType.equals(CkdUserGoodsType.member199)) {
            throw new BizException("此订单不支持退款");
        }
        if (ckdUserOrder.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            LocalDateTime payTime = ckdUserOrder.getPayTime();
            if (payTime != null && payTime.plusDays(7).isAfter(LocalDateTime.now())) {
                throw new BizException("超过可退款时间");
            }
            ckdUserOrder.setPaymentStatus(PaymentStatus.WAITING_FOR_REFUND);
            baseMapper.updateById(ckdUserOrder);
        } else {
            throw new BizException("此订单不支持退款");
        }

        WechatMinruiOrdersSaveDTO saveDTO = new WechatMinruiOrdersSaveDTO();
        saveDTO.setDescription(ckdWxPayConfig.getLifeTimeName());


        saveDTO.setAmount(ckdUserOrder.getGoodsPrice());
        saveDTO.setBusinessId(ckdUserOrder.getId());
        saveDTO.setMerchantId(ckdWxPayConfig.getMerchantId());
        saveDTO.setAppId(ckdWxPayConfig.getAppId());
        saveDTO.setBusinessType(CkdUserOrder.class.getSimpleName());
        saveDTO.setNotifyUrl("/api/ai/ckdUserOrder/anno/" + ckdUserOrder.getId() + "/status?status=");
        saveDTO.setOpenId(ckdUserOrder.getOpenId());

        wechatOrdersApi.refund(saveDTO);

        return null;
    }
}
