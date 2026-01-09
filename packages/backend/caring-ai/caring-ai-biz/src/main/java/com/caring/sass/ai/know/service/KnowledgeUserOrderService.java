package com.caring.sass.ai.know.service;

import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.dto.know.UserMemberPayOrder;
import com.caring.sass.ai.entity.know.KnowledgeUserOrder;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.entity.config.WechatOrders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * 业务接口
 * 知识库用户购买会员订单
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-11
 */
public interface KnowledgeUserOrderService extends SuperService<KnowledgeUserOrder> {



    SseEmitter createdSee(String uid);

    WechatOrders createdJSAPIOrder(UserMemberPayOrder memberPayOrder);

    String createdH5WechatOrder(UserMemberPayOrder memberPayOrder,  String clientIp);

    String createdWechatOrder(UserMemberPayOrder memberPayOrder);


    void callUpdateWechatOrder(Long orderId, PaymentStatus status);

    /**
     * 查询业务订单支付情况
     * @param memberPayOrder
     * @return
     */
    KnowledgeUserOrder queryOrderStatus(UserMemberPayOrder memberPayOrder);



    void closeSse(String uid);


    KnowledgeUserOrder saveFreeOrder(Long userId, String domain);
}
