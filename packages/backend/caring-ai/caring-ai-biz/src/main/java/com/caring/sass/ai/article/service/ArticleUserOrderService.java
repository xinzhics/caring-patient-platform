package com.caring.sass.ai.article.service;

import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.article.ArticleUserOrder;
import com.caring.sass.ai.entity.article.ArticleUserOrderGoods;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.entity.config.WechatOrders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * 业务接口
 * 科普创作用户支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
public interface ArticleUserOrderService extends SuperService<ArticleUserOrder> {

    void closeSse(String uid);

    SseEmitter createdSee(String uid);


    WechatOrders createdJSAPIOrder(ArticleUserOrderGoods memberPayOrder);

    String createdWechatOrder(ArticleUserOrderGoods memberPayOrder);

    ArticleUserOrder queryOrderStatus(ArticleUserOrderGoods memberPayOrder);

    void callUpdateWechatOrder(Long orderId, PaymentStatus status);
}
