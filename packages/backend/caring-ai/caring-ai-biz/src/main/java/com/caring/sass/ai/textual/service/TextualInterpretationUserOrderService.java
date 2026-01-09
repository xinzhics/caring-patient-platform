package com.caring.sass.ai.textual.service;

import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.article.ArticleUserOrderGoods;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.textual.TextualInterpretationUserOrder;
import com.caring.sass.wx.dto.config.WechatMinruiOrdersSaveDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * 业务接口
 * 文献解读用户支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-05
 */
public interface TextualInterpretationUserOrderService extends SuperService<TextualInterpretationUserOrder> {

    WechatOrders createdJSAPIOrder(ArticleUserOrderGoods articleUserOrderGoods);

    String createdWechatOrder(ArticleUserOrderGoods articleUserOrderGoods);

    TextualInterpretationUserOrder queryOrderStatus(ArticleUserOrderGoods memberPayOrder);

    @Transactional
    void callUpdateWechatOrder(Long orderId, PaymentStatus status);

    void closeSse(String uid);

    SseEmitter createdSee(String uid);
}
