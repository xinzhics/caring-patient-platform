package com.caring.sass.ai.ckd.server;

import com.caring.sass.ai.dto.ckd.CkdUserOrderSaveDTO;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.ckd.CkdUserOrder;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.entity.config.WechatOrders;

/**
 * <p>
 * 业务接口
 * ckd会员订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
public interface CkdUserOrderService extends SuperService<CkdUserOrder> {

    WechatOrders createdWechatOrder(CkdUserOrderSaveDTO orderSaveDTO);

    CkdUserOrder queryOrderStatus(Long ckdOrderId);

    void callUpdateWechatOrder(Long orderId, PaymentStatus status);


    CkdUserOrder refund(Long ckdOrderId);
}
