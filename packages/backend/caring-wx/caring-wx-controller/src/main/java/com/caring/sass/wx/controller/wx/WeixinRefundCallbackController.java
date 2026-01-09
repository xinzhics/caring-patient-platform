package com.caring.sass.wx.controller.wx;


import com.caring.sass.wx.service.config.WechatMinruiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping({"/refund/anno/callback"})
public class WeixinRefundCallbackController {


    @Autowired
    WechatMinruiOrderService wechatMinruiOrderService;

    @PostMapping(path = "/{merchantId}", produces = "text/plain;charset=utf-8")
    public ResponseEntity<Void> minruiRefundCallbackNotification(
            @PathVariable("merchantId") String merchantId,
            @RequestBody String requestBody, HttpServletRequest req, HttpServletResponse resp) {

        String wechatSignature = req.getHeader("Wechatpay-Signature");   // 支付签名
        String wechatPaySerial = req.getHeader("Wechatpay-Serial");   // 微信支付平台证书的序列号
        String wechatpayNonce = req.getHeader("Wechatpay-Nonce");   // 微信支付平台证书的序列号
        String wechatTimestamp = req.getHeader("Wechatpay-Timestamp");   // 微信支付平台证书的序列号

        if (wechatSignature == null || wechatPaySerial == null || wechatpayNonce == null || wechatTimestamp == null) {
            // 对于必要的请求头参数缺失的情况，返回错误状态码
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        HttpStatus httpStatus = wechatMinruiOrderService.refundNotificationParser(wechatSignature, wechatPaySerial, wechatpayNonce, wechatTimestamp, requestBody, merchantId);

        return ResponseEntity.status(httpStatus).build();
    }


}
