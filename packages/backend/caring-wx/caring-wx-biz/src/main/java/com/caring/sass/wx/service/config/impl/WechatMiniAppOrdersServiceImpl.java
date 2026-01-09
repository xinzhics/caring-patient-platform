package com.caring.sass.wx.service.config.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.exception.BizException;
import com.caring.sass.wx.dao.config.WechatOrdersMapper;
import com.caring.sass.wx.dto.config.PrepayWithRequestPaymentResponseDTO;
import com.caring.sass.wx.dto.config.WechatOrdersSaveDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import com.caring.sass.wx.service.config.WechatMiniAppOrdersService;
import com.caring.sass.wx.service.config.wxPay.DocusCuiteMerchantConfig;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.partnerpayments.model.TransactionAmount;
import com.wechat.pay.java.service.partnerpayments.model.TransactionPayer;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 该微信支付页面用的 哆咔叽DoucusCuite
 *
 * @author 杨帅
 * @date 2024-06-20
 */
@Slf4j
@Service

public class WechatMiniAppOrdersServiceImpl extends SuperServiceImpl<WechatOrdersMapper, WechatOrders> implements WechatMiniAppOrdersService {



    @Autowired
    DocusCuiteMerchantConfig docusCuiteMerchantConfig;


    /**
     * 小程序下单。
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public WechatOrders createWechatOrders(WechatOrdersSaveDTO dto) {
        Config config = docusCuiteMerchantConfig.getConfig();
        if (config == null) {
            throw new BizException("微信支付失败");
        }
        String merchantId = docusCuiteMerchantConfig.getMerchantId();
        JsapiServiceExtension jsapiServiceExtension = new JsapiServiceExtension.Builder().config(config).build();
        WechatOrders orders = new WechatOrders();
        BeanUtil.copyProperties(dto, orders);
        orders.setMchid(merchantId);
        if (StrUtil.isEmpty(orders.getAmountCurrency())) {
            orders.setAmountCurrency("CNY");
        }
        orders.setPayWay("MINIAPP");
        baseMapper.insert(orders);

        Long ordersId = orders.getId();

        String string = ApplicationDomainUtil.apiUrl();

        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(dto.getAmount());
        Payer payer = new Payer();
        payer.setOpenid(dto.getOpenId());
        request.setPayer(payer);
        request.setAmount(amount);
        request.setAppid(dto.getAppId());
        request.setMchid(merchantId);
        request.setDescription(dto.getDescription());
        request.setNotifyUrl(string + docusCuiteMerchantConfig.getNotifyUrl());
        request.setOutTradeNo(ordersId.toString());
        PrepayWithRequestPaymentResponse paymentResponse = jsapiServiceExtension.prepayWithRequestPayment(request);
        String packageVal = paymentResponse.getPackageVal();
        String prepayId = packageVal.substring(10);
        orders.setPrepayId(prepayId);

        WechatOrders wechatOrders = new WechatOrders();
        wechatOrders.setId(orders.getId());
        wechatOrders.setPrepayId(prepayId);
        baseMapper.updateById(wechatOrders);

        PrepayWithRequestPaymentResponseDTO jsonObject = new PrepayWithRequestPaymentResponseDTO();
        BeanUtil.copyProperties(paymentResponse, jsonObject);
        wechatOrders.setPaymentResponse(jsonObject);
        return wechatOrders;
    }



    /**
     * 检查订单是否支付
     * 订单状态为空，则等待一秒。
     * 订单状态为成功，返回true
     * 否则，返回false
     * @param orderId
     * @return
     */
    @Override
    public boolean checkOrderSuccess(Long orderId) {
        WechatOrders wechatOrders = baseMapper.selectById(orderId);
        if (wechatOrders == null) {
            log.error(" wechat order not find {}", orderId);
            return false;
        }
        String tradeState = wechatOrders.getTradeState();
        if (Transaction.TradeStateEnum.SUCCESS.name().equals(tradeState)) {
            return true;
        } else if (StrUtil.isEmpty(tradeState)) {
            JsapiServiceExtension jsapiServiceExtension = new JsapiServiceExtension.Builder().config(docusCuiteMerchantConfig.getConfig()).build();
            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(docusCuiteMerchantConfig.getMerchantId());
            request.setOutTradeNo(orderId.toString());
            com.wechat.pay.java.service.payments.model.Transaction transaction = jsapiServiceExtension.queryOrderByOutTradeNo(request);
            com.wechat.pay.java.service.payments.model.Transaction.TradeStateEnum stateEnum = transaction.getTradeState();
            if (com.wechat.pay.java.service.payments.model.Transaction.TradeStateEnum.SUCCESS.equals(stateEnum)) {
                wechatOrders.setTradeState(com.wechat.pay.java.service.payments.model.Transaction.TradeStateEnum.SUCCESS.toString());
                wechatOrders.setTransactionId(transaction.getTransactionId());
                wechatOrders.setPayerOpenid(transaction.getPayer().getOpenid());
                wechatOrders.setPayerTotal(transaction.getAmount().getPayerTotal());
                wechatOrders.setPayerCurrency(transaction.getAmount().getPayerCurrency());
                wechatOrders.setBankType(transaction.getBankType());
                wechatOrders.setTradeType(transaction.getTradeType().name());
                wechatOrders.setSuccessTime(transaction.getSuccessTime());
                baseMapper.updateById(wechatOrders);
                return true;
            } else if (stateEnum != null) {
                wechatOrders.setTradeState(stateEnum.toString());
                baseMapper.updateById(wechatOrders);
                return false;
            }
        }
        return false;

    }

    /**
     * 微信支付回调
     * @param wechatSignature
     * @param wechatPaySerial
     * @param wechatpayNonce
     * @param wechatTimestamp
     * @param requestBody
     */
    @Override
    public HttpStatus notificationParser(String wechatSignature, String wechatPaySerial, String wechatpayNonce, String wechatTimestamp, String requestBody) {
        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                .body(requestBody)
                .build();

        // 初始化 NotificationParser
        NotificationParser parser = new NotificationParser(docusCuiteMerchantConfig.getNotificationConfig());
        Transaction transaction = null;
        try {
            // 以支付通知回调为例，验签、解密并转换成 Transaction
            transaction = parser.parse(requestParam, Transaction.class);
        } catch (ValidationException e) {
            // 签名验证失败，返回 401 UNAUTHORIZED 状态码
            log.error("sign verification failed", e);
            return HttpStatus.UNAUTHORIZED;
        }

        // 如果处理失败，应返回 4xx/5xx 的状态码，例如 500 INTERNAL_SERVER_ERROR
        if (transaction == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        log.error(" wechat order notificationParser {}", JSON.toJSONString(transaction));
        String transactionId = transaction.getTransactionId();  // 微信支付系统生成的订单号
        String outTradeNo = transaction.getOutTradeNo();    // 商户系统内部订单号
        TransactionAmount amount = transaction.getAmount(); // 订单金额信息
        Integer payerTotal = amount.getPayerTotal();    // 用户支付金额
        String payerCurrency = amount.getPayerCurrency(); // 用户支付币种
        TransactionPayer payer = transaction.getPayer();    // 支付者
        String spOpenid = payer.getSpOpenid();
        String subOpenid = payer.getSubOpenid();

        Transaction.TradeTypeEnum tradeType = transaction.getTradeType();   // 支付类型
        Transaction.TradeStateEnum tradeState = transaction.getTradeState();    // 支付状态
        String bankType = transaction.getBankType();    // 银行信息
        String successTime = transaction.getSuccessTime();  // 成功时间

        WechatOrders wechatOrders = baseMapper.selectById(Long.parseLong(outTradeNo));
        if (wechatOrders == null) {
            log.error(" wechat order not find {}", outTradeNo);
            return HttpStatus.OK;
        }
        wechatOrders.setNotifyCreateTime(LocalDateTime.now());
        if (spOpenid != null) {
            wechatOrders.setPayerOpenid(spOpenid);
        } else if (subOpenid != null) {
            wechatOrders.setPayerOpenid(subOpenid);
        }
        wechatOrders.setTransactionId(transactionId);
        wechatOrders.setPayerTotal(payerTotal);
        wechatOrders.setPayerCurrency(payerCurrency);
        wechatOrders.setBankType(bankType);
        wechatOrders.setTradeType(tradeType.name());
        wechatOrders.setTradeState(tradeState.name());
        wechatOrders.setSuccessTime(successTime);
        baseMapper.updateById(wechatOrders);
        return HttpStatus.OK;

    }
}
