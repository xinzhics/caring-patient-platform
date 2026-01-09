package com.caring.sass.ai.call.service;

import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.dto.know.UserMemberPayOrder;
import com.caring.sass.ai.entity.call.CallConfig;
import com.caring.sass.ai.entity.call.CallRechargeOrder;
import com.caring.sass.ai.entity.know.KnowledgeUserOrder;
import com.caring.sass.base.service.SuperService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * 业务接口
 * 通话充值订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
public interface CallRechargeOrderService extends SuperService<CallRechargeOrder> {

    SseEmitter createdSee(String uid);

    /**
     * 创建一个H5微信支付订单
     *
     * @param clientIp       客户端IP
     * @return 支付订单号
     */
    CallRechargeOrder createdH5WechatOrder(Long userId, String uid, CallConfig callConfig, String clientIp);

    /**
     * 查询订单状态
     *
     * @return 订单状态
     */
    CallRechargeOrder queryOrderStatus(Long orderId);

    /**
     * 调用更新微信支付订单状态
     *
     * @param orderId 订单ID
     * @param status  支付状态
     */
    void callUpdateWechatOrder(Long orderId, PaymentStatus status);

     /**
      * 关闭SEE链接
      *
      * @param uid 用户ID
      */
    void closeSse(String uid);
}
