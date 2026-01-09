package com.caring.sass.ai.card.service;

import com.caring.sass.ai.dto.card.BusinessCardUserOrderSaveDTO;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.card.BusinessCardUserOrder;
import com.caring.sass.wx.entity.config.WechatOrders;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 科普名片会员支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
public interface BusinessCardUserOrderService extends SuperService<BusinessCardUserOrder> {


    /**
     * 创建一个微信订单
     * @param saveDTO
     * @return
     */
    WechatOrders createdWechatOrder(BusinessCardUserOrderSaveDTO saveDTO);


    /**
     * 微信支付成功后的回调
     * @param orderId
     * @param status
     */
    void callUpdateWechatOrder(Long orderId, PaymentStatus status);

    /**
     * 查询微信支付订单状态
     * @param businessCardId
     * @return
     */
    BusinessCardUserOrder queryOrderStatus(Long businessCardId);
}
