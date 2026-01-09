package com.caring.sass.wx.controller.wx;


import com.caring.sass.wx.service.config.WechatMiniAppOrdersService;
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
@RequestMapping({"/pay/anno/callback"})
public class WeixinPayCallbackController {

    @Autowired
    WechatMiniAppOrdersService wechatMiniAppOrdersService;

    @Autowired
    WechatMinruiOrderService wechatMinruiOrderService;


    @PostMapping(produces = "text/plain;charset=utf-8")
    public ResponseEntity<Void> callbackNotification(@RequestBody String requestBody, HttpServletRequest req, HttpServletResponse resp) {

        String wechatSignature = req.getHeader("Wechatpay-Signature");   // 支付签名
        String wechatPaySerial = req.getHeader("Wechatpay-Serial");   // 微信支付平台证书的序列号
        String wechatpayNonce = req.getHeader("Wechatpay-Nonce");   // 微信支付平台证书的序列号
        String wechatTimestamp = req.getHeader("Wechatpay-Timestamp");   // 微信支付平台证书的序列号

        if (wechatSignature == null || wechatPaySerial == null || wechatpayNonce == null || wechatTimestamp == null) {
            // 对于必要的请求头参数缺失的情况，返回错误状态码
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        HttpStatus httpStatus = wechatMiniAppOrdersService.notificationParser(wechatSignature, wechatPaySerial, wechatpayNonce, wechatTimestamp, requestBody);

        return ResponseEntity.status(httpStatus).build();
    }


    @Deprecated
    @PostMapping(path = "minrui", produces = "text/plain;charset=utf-8")
    public ResponseEntity<Void> minruiCallbackNotification(@RequestBody String requestBody, HttpServletRequest req, HttpServletResponse resp) {

        String wechatSignature = req.getHeader("Wechatpay-Signature");   // 支付签名
        String wechatPaySerial = req.getHeader("Wechatpay-Serial");   // 微信支付平台证书的序列号
        String wechatpayNonce = req.getHeader("Wechatpay-Nonce");   // 微信支付平台证书的序列号
        String wechatTimestamp = req.getHeader("Wechatpay-Timestamp");   // 微信支付平台证书的序列号

        if (wechatSignature == null || wechatPaySerial == null || wechatpayNonce == null || wechatTimestamp == null) {
            // 对于必要的请求头参数缺失的情况，返回错误状态码
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        HttpStatus httpStatus = wechatMinruiOrderService.notificationParser(wechatSignature, wechatPaySerial, wechatpayNonce, wechatTimestamp, requestBody, "1695207060");

        return ResponseEntity.status(httpStatus).build();
    }

    @Deprecated
    @PostMapping(path = "docusCard", produces = "text/plain;charset=utf-8")
    public ResponseEntity<Void> docusCardCallbackNotification(@RequestBody String requestBody, HttpServletRequest req, HttpServletResponse resp) {

        String wechatSignature = req.getHeader("Wechatpay-Signature");   // 支付签名
        String wechatPaySerial = req.getHeader("Wechatpay-Serial");   // 微信支付平台证书的序列号
        String wechatpayNonce = req.getHeader("Wechatpay-Nonce");   // 微信支付平台证书的序列号
        String wechatTimestamp = req.getHeader("Wechatpay-Timestamp");   // 微信支付平台证书的序列号

        if (wechatSignature == null || wechatPaySerial == null || wechatpayNonce == null || wechatTimestamp == null) {
            // 对于必要的请求头参数缺失的情况，返回错误状态码
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        HttpStatus httpStatus = wechatMinruiOrderService.notificationParser(wechatSignature, wechatPaySerial, wechatpayNonce, wechatTimestamp, requestBody, "1705074319");

        return ResponseEntity.status(httpStatus).build();
    }


    @PostMapping(path = "/all/{merchantId}", produces = "text/plain;charset=utf-8")
    public ResponseEntity<Void> ckdCallbackNotification(
            @PathVariable("merchantId") String merchantId,
            @RequestBody String requestBody, HttpServletRequest req, HttpServletResponse resp) {
        log.info("wechat pay all Notification requestBody:{}", requestBody);
        String wechatSignature = req.getHeader("Wechatpay-Signature");   // 支付签名
        String wechatPaySerial = req.getHeader("Wechatpay-Serial");   // 微信支付平台证书的序列号
        String wechatpayNonce = req.getHeader("Wechatpay-Nonce");   // 微信支付平台证书的序列号
        String wechatTimestamp = req.getHeader("Wechatpay-Timestamp");   // 微信支付平台证书的序列号
        String wechatSignatureType = req.getHeader("Wechatpay-Signature-Type");   // 微信支付平台证书的序列号

        if (wechatSignature == null || wechatPaySerial == null || wechatpayNonce == null || wechatTimestamp == null) {
            // 对于必要的请求头参数缺失的情况，返回错误状态码
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        log.info("wechat pay all Notification wechatSignatureType:{},wechatSignature:{},wechatPaySerial:{},wechatpayNonce:{},wechatTimestamp:{}",
                wechatSignatureType, wechatSignature, wechatPaySerial, wechatpayNonce, wechatTimestamp);
        try {
            HttpStatus httpStatus = wechatMinruiOrderService.notificationParser(wechatSignature, wechatPaySerial, wechatpayNonce, wechatTimestamp, requestBody, merchantId);
            return ResponseEntity.status(httpStatus).build();
        } catch (Exception e) {
            log.error("wechat pay all Notification error:{}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).build();

    }


}
