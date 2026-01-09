package com.caring.sass.wx.service.config;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.dto.config.WechatMinruiOrdersSaveDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import org.springframework.http.HttpStatus;

public interface WechatMinruiOrderService extends SuperService<WechatOrders> {


    boolean checkOrderSuccess(String businessType, Long businessId);

    /**
     * 微信支付回调
     *
     * @param wechatSignature
     * @param wechatPaySerial
     * @param wechatpayNonce
     * @param wechatTimestamp
     * @param requestBody
     */
    HttpStatus notificationParser(String wechatSignature, String wechatPaySerial, String wechatpayNonce, String wechatTimestamp, String requestBody, String merchantId);


    /**
     * 创建native支付订单
     * @param dto
     * @return
     */
    String createWechatNativeOrders(WechatMinruiOrdersSaveDTO dto);


    /**
     * 创建H5支付下单
     * @param dto
     * @return
     */
    String createWechatH5Orders(WechatMinruiOrdersSaveDTO dto);

    /**
     * 敏瑞。jsapi支付
     *
     * @param dto
     * @return
     */
    WechatOrders createWechatJSAPIOrders(WechatMinruiOrdersSaveDTO dto);


    /**
     * 退款
     * @param dto
     * @return
     */
    WechatOrders refund(WechatMinruiOrdersSaveDTO dto);

    /**
     * 退款回调
     * @param wechatSignature
     * @param wechatPaySerial
     * @param wechatpayNonce
     * @param wechatTimestamp
     * @param requestBody
     * @param merchantId
     * @return
     */
    HttpStatus refundNotificationParser(String wechatSignature, String wechatPaySerial, String wechatpayNonce, String wechatTimestamp, String requestBody, String merchantId);
}
