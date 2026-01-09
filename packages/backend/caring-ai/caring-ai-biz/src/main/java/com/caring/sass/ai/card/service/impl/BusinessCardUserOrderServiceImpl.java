package com.caring.sass.ai.card.service.impl;


import cn.hutool.core.lang.UUID;
import com.caring.sass.ai.card.config.CardWxPayConfig;
import com.caring.sass.ai.card.dao.BusinessCardUserOrderMapper;
import com.caring.sass.ai.card.service.BusinessCardMemberVersionService;
import com.caring.sass.ai.card.service.BusinessCardUserOrderService;
import com.caring.sass.ai.dto.card.BusinessCardUserOrderSaveDTO;
import com.caring.sass.ai.dto.know.KnowMemberType;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.card.BusinessCardMemberVersionEnum;
import com.caring.sass.ai.entity.card.BusinessCardUserOrder;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 科普名片会员支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
@Slf4j
@Service

public class BusinessCardUserOrderServiceImpl extends SuperServiceImpl<BusinessCardUserOrderMapper, BusinessCardUserOrder> implements BusinessCardUserOrderService {

    @Autowired
    CardWxPayConfig cardWxPayConfig;

    @Autowired
    WechatOrdersApi wechatOrdersApi;

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    BusinessCardMemberVersionService businessCardMemberVersionService;


    private BusinessCardUserOrder createUserOrder(BusinessCardUserOrderSaveDTO saveDTO) {

        BusinessCardUserOrder userOrder = new BusinessCardUserOrder();
        userOrder.setUserId(saveDTO.getUserId());
        userOrder.setOpenId(saveDTO.getOpenId());
        userOrder.setGoodsType(saveDTO.getGoodsType());
        userOrder.setPaymentStatus(PaymentStatus.NO_PAY);
        if (userOrder.getGoodsType().equals(BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION)) {
            userOrder.setGoodsPrice(cardWxPayConfig.getMembershipPrice());
        } else if (userOrder.getGoodsType().equals(BusinessCardMemberVersionEnum.BASIC_EDITION)) {
            userOrder.setGoodsPrice(cardWxPayConfig.getBasicMembershipPrice());
        } else {
            throw new BizException("不支持的会员类型");
        }
        baseMapper.insert(userOrder);
        return userOrder;
    }

    /**
     * 创建业务的订单。
     *
     * 创建小程序 的 微信支付订单
     * @param saveDTO
     * @return
     */
    @Override
    public WechatOrders createdWechatOrder(BusinessCardUserOrderSaveDTO saveDTO) {

        // 创建业务订单
        BusinessCardUserOrder userOrder = createUserOrder(saveDTO);

        WechatMinruiOrdersSaveDTO ordersSaveDTO = new WechatMinruiOrdersSaveDTO();

        if (userOrder.getGoodsType().equals(BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION)) {
            ordersSaveDTO.setDescription(cardWxPayConfig.getMembershipName());
        } else if (userOrder.getGoodsType().equals(BusinessCardMemberVersionEnum.BASIC_EDITION)) {
            ordersSaveDTO.setDescription(cardWxPayConfig.getBasicMembershipName());
        }
        ordersSaveDTO.setMerchantId(cardWxPayConfig.getMerchantId());
        ordersSaveDTO.setAmount(userOrder.getGoodsPrice());
        ordersSaveDTO.setBusinessId(userOrder.getId());
        ordersSaveDTO.setAppId(cardWxPayConfig.getAppId());
        ordersSaveDTO.setBusinessType(BusinessCardUserOrder.class.getSimpleName());
        ordersSaveDTO.setNotifyUrl("/api/ai/businessCardUserOrder/anno/" + userOrder.getId() + "/status?status=");
        ordersSaveDTO.setOpenId(userOrder.getOpenId());
        R<WechatOrders> wechatJSAPIOrders = wechatOrdersApi.createWechatJSAPIOrders(ordersSaveDTO);
        if (!wechatJSAPIOrders.getIsSuccess() || Objects.isNull(wechatJSAPIOrders.getData())) {
            throw new BizException("创建订单失败");
        }
        return wechatJSAPIOrders.getData();
    }


    /**
     * 微信支付成功后的回调方法
     * @param orderId
     * @param status
     */
    @Override
    public void callUpdateWechatOrder(Long orderId, PaymentStatus status) {


        // key 过去时间 5秒。 尝试重锁 20次
        boolean lockBoolean = false;
        String key = "business_card_member_order:" + orderId;
        try {
            lockBoolean = distributedLock.lock(key, 5000L, 20);
            if (lockBoolean) {
                BusinessCardUserOrder userOrder = baseMapper.selectById(orderId);
                if (userOrder != null && PaymentStatus.SUCCESS.equals(status)) {
                    if (PaymentStatus.SUCCESS.equals(userOrder.getPaymentStatus())) {
                        return;
                    }
                    userOrder.setPaymentStatus(status);
                    baseMapper.updateById(userOrder);

                    if (userOrder.getGoodsType().equals(BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION)) {
                        businessCardMemberVersionService.setBusinessCardUserMemberVersion(userOrder.getUserId(), BusinessCardMemberVersionEnum.MEMBERSHIP_VERSION);
                    } else if (userOrder.getGoodsType().equals(BusinessCardMemberVersionEnum.BASIC_EDITION)) {
                        businessCardMemberVersionService.setBusinessCardUserMemberVersion(userOrder.getUserId(), BusinessCardMemberVersionEnum.BASIC_EDITION);
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
     * 查询订单状态
     * @param businessCardId
     * @return
     */
    @Override
    public BusinessCardUserOrder queryOrderStatus(Long businessCardId) {

        BusinessCardUserOrder userOrder = baseMapper.selectById(businessCardId);
        if (userOrder.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            return userOrder;
        }
        WechatOrdersPageDTO dto = new WechatOrdersPageDTO();
        dto.setBusinessId(userOrder.getId());
        dto.setBusinessType(BusinessCardUserOrder.class.getSimpleName());
        R<Boolean> nativeOrders = wechatOrdersApi.queryWechatNativeOrders(dto);
        if (nativeOrders.getIsSuccess()) {
            Boolean data = nativeOrders.getData();
            if (data != null && data) {
                userOrder = baseMapper.selectById(businessCardId);
            }
        }
        return userOrder;
    }
}
