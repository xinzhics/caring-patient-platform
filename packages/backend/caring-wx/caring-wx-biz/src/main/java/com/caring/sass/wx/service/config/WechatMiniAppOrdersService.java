package com.caring.sass.wx.service.config;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.dto.config.WechatOrdersSaveDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import org.springframework.http.HttpStatus;

/**
 * <p>
 * 业务接口
 * 微信支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-20
 */
public interface WechatMiniAppOrdersService extends SuperService<WechatOrders> {

    /**
     * 微信支付下单
     * @param dto
     * @return
     */
    WechatOrders createWechatOrders(WechatOrdersSaveDTO dto);

    /**
     * 微信支付回调
     *
     * @param wechatSignature
     * @param wechatPaySerial
     * @param wechatpayNonce
     * @param wechatTimestamp
     * @param requestBody
     */
    HttpStatus notificationParser(String wechatSignature, String wechatPaySerial, String wechatpayNonce, String wechatTimestamp, String requestBody);

    /**
     * 判断订单是否已经支付
     * @param orderId
     * @return
     */
    boolean checkOrderSuccess(Long orderId);

}
