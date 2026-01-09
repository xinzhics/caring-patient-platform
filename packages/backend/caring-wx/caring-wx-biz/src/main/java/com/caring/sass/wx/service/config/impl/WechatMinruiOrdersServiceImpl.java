package com.caring.sass.wx.service.config.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.wx.dao.config.WechatOrdersMapper;
import com.caring.sass.wx.dto.config.PrepayWithRequestPaymentResponseDTO;
import com.caring.sass.wx.dto.config.WechatMinruiOrdersSaveDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import com.caring.sass.wx.service.config.WechatMinruiOrderService;
import com.caring.sass.wx.service.config.wxPay.*;
import com.caring.sass.wx.wxUtil.HttpUtils;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.model.TransactionAmount;
import com.wechat.pay.java.service.partnerpayments.model.TransactionPayer;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.*;
import com.wechat.pay.java.service.payments.h5.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 该微信支付页面用的 哆咔叽DoucusCuite
 *
 * @author 杨帅
 * @date 2024-06-20
 */
@Slf4j
@Service

public class WechatMinruiOrdersServiceImpl extends SuperServiceImpl<WechatOrdersMapper, WechatOrders> implements WechatMinruiOrderService {



    @Autowired
    MinRuiMerchantConfig merchantConfig;

    @Autowired
    CKDMerchantConfig ckdMerchantConfig;

    @Autowired
    DocusCuiteMerchantConfig docusCuiteMerchantConfig;

    @Autowired
    DocuCardMerchantConfig docuCardMerchantConfig;

    @Autowired
    CaringArticleMerchantConfig caringArticleMerchantConfig;

    @Autowired
    CaringDocKnowMerchantConfig caringDocKnowMerchantConfig;

    /**
     * 根据商户号。 获取 商户支付配置
     * @param merchantId
     * @return
     */
    protected Config getConfig(String merchantId) {
        Config config = null;
        if (StrUtil.isEmpty(merchantId)) {
            config = merchantConfig.getConfig();
        } else if (merchantId.equals(merchantConfig.getMerchantId())) {
            config = merchantConfig.getConfig();
        } else if (merchantId.equals(docuCardMerchantConfig.getMerchantId())) {
            config = docuCardMerchantConfig.getConfig();
        } else if (merchantId.equals(ckdMerchantConfig.getMerchantId())) {
            config = ckdMerchantConfig.getConfig();
        } else if (merchantId.equals(docusCuiteMerchantConfig.getMerchantId())) {
            config = docusCuiteMerchantConfig.getConfig();
        } else if (merchantId.equals(caringArticleMerchantConfig.getMerchantId())) {
            config = caringArticleMerchantConfig.getConfig();
        } else if (merchantId.equals(caringDocKnowMerchantConfig.getMerchantId())) {
            config = caringDocKnowMerchantConfig.getConfig();
        }
        if (config == null) {
            throw new BizException("微信商户配置异常，下单失败");
        }
        return config;
    }

    /**
     * 获取商户支付后的回调URL
     * @param merchantId
     * @return
     */
    private String getNotifyUrl(String merchantId) {

        if (StrUtil.isEmpty(merchantId)) {

            return merchantConfig.getNewNotifyUrl();
        } else if (merchantId.equals(merchantConfig.getMerchantId())) {

            return merchantConfig.getNewNotifyUrl();
        } else if (merchantId.equals(docuCardMerchantConfig.getMerchantId())) {

            return docuCardMerchantConfig.getNewNotifyUrl();
        } else if (merchantId.equals(ckdMerchantConfig.getMerchantId())) {

            return ckdMerchantConfig.getNewNotifyUrl();
        } else if (merchantId.equals(docusCuiteMerchantConfig.getMerchantId())) {

            return docusCuiteMerchantConfig.getNewNotifyUrl();
        } else if (merchantId.equals(caringArticleMerchantConfig.getMerchantId())) {

            return caringArticleMerchantConfig.getNewNotifyUrl();
        } else if (merchantId.equals(caringDocKnowMerchantConfig.getMerchantId())) {

            return caringDocKnowMerchantConfig.getNewNotifyUrl();
        } else {
            throw new BizException("微信商户配置异常，下单失败");
        }
    }

    /**
     * 获取商户支付后的回调URL
     * @param merchantId
     * @return
     */
    private String getRefundsNotifyUrl(String merchantId) {

        if (StrUtil.isEmpty(merchantId)) {

            return merchantConfig.getRefundsNotifyUrl();
        } else if (merchantId.equals(merchantConfig.getMerchantId())) {
            return merchantConfig.getRefundsNotifyUrl();

        } else if (merchantId.equals(docuCardMerchantConfig.getMerchantId())) {
            return docuCardMerchantConfig.getRefundsNotifyUrl();

        } else if (merchantId.equals(ckdMerchantConfig.getMerchantId())) {
            return ckdMerchantConfig.getRefundsNotifyUrl();

        } else if (merchantId.equals(docusCuiteMerchantConfig.getMerchantId())) {
            return docusCuiteMerchantConfig.getRefundsNotifyUrl();

        } else if (merchantId.equals(caringArticleMerchantConfig.getMerchantId())) {
            return caringArticleMerchantConfig.getRefundsNotifyUrl();
        } else if (merchantId.equals(caringDocKnowMerchantConfig.getMerchantId())) {
            return caringDocKnowMerchantConfig.getRefundsNotifyUrl();
        } else {
            throw new BizException("微信商户配置异常，下单失败");
        }
    }

    /**
     * H5下单
     * @param dto
     * @return
     */
    @Override
    public String createWechatH5Orders(WechatMinruiOrdersSaveDTO dto) {

        String merchantId = dto.getMerchantId();
        Config config = getConfig(merchantId);


        WechatOrders orders = new WechatOrders();
        BeanUtil.copyProperties(dto, orders);
        orders.setMchid(merchantId);
        if (StrUtil.isEmpty(orders.getAmountCurrency())) {
            orders.setAmountCurrency("CNY");
        }
        orders.setPayWay("H5");
        baseMapper.insert(orders);
        H5Service h5Service = new H5Service.Builder().config(config).build();
        com.wechat.pay.java.service.payments.h5.model.PrepayRequest h5PrepayRequest = new com.wechat.pay.java.service.payments.h5.model.PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(dto.getAmount());
        h5PrepayRequest.setAmount(amount);
        h5PrepayRequest.setAppid(dto.getAppId());
        h5PrepayRequest.setMchid(merchantId);
        h5PrepayRequest.setDescription(dto.getDescription());
        String string = ApplicationDomainUtil.apiUrl();
        h5PrepayRequest.setNotifyUrl(string + getNotifyUrl(merchantId));
        h5PrepayRequest.setOutTradeNo(orders.getId().toString());
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setPayerClientIp(dto.getPayerClientIp());
        H5Info h5Info = new H5Info();
        h5Info.setType("WEB");
        sceneInfo.setH5Info(h5Info);
        h5PrepayRequest.setSceneInfo(sceneInfo);
        PrepayResponse prepay = h5Service.prepay(h5PrepayRequest);
        return prepay.getH5Url();
    }


    /**
     * JSAPI的下单
     * @param dto
     * @return
     */
    @Override
    public WechatOrders createWechatJSAPIOrders(WechatMinruiOrdersSaveDTO dto) {
        String merchantId = dto.getMerchantId();
        Config config = getConfig(merchantId);

        WechatOrders orders = new WechatOrders();
        BeanUtil.copyProperties(dto, orders);
        orders.setMchid(merchantId);
        if (StrUtil.isEmpty(orders.getAmountCurrency())) {
            orders.setAmountCurrency("CNY");
        }
        orders.setPayWay("JSAPI");
        baseMapper.insert(orders);
        com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request = new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
        com.wechat.pay.java.service.payments.jsapi.model.Amount amount = new com.wechat.pay.java.service.payments.jsapi.model.Amount();
        amount.setTotal(dto.getAmount());
        request.setAmount(amount);
        request.setAppid(dto.getAppId());
        request.setDescription(dto.getDescription());
        request.setMchid(merchantId);
        request.setOutTradeNo(orders.getId().toString());
        String string = ApplicationDomainUtil.apiUrl();
        request.setNotifyUrl(string + getNotifyUrl(merchantId));
        Payer payer = new Payer();
        payer.setOpenid(dto.getOpenId());
        request.setPayer(payer);

        JsapiServiceExtension jsapiServiceExtension = new JsapiServiceExtension.Builder().config(config).build();
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
     * 创建一个native预支付订单
     * @param dto
     * @return
     */
    public String createWechatNativeOrders(WechatMinruiOrdersSaveDTO dto) {
        String merchantId = dto.getMerchantId();
        Config config = getConfig(merchantId);
        if (config == null) {
            throw new BizException("微信商户配置异常，下单失败");
        }
        WechatOrders orders = new WechatOrders();
        BeanUtil.copyProperties(dto, orders);
        orders.setMchid(merchantId);
        if (StrUtil.isEmpty(orders.getAmountCurrency())) {
            orders.setAmountCurrency("CNY");
        }
        orders.setPayWay("native");
        baseMapper.insert(orders);
        // 构建service
        NativePayService service = new NativePayService.Builder().config(config).build();

        com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest request = new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();
        com.wechat.pay.java.service.payments.nativepay.model.Amount amount = new com.wechat.pay.java.service.payments.nativepay.model.Amount();
        amount.setTotal(dto.getAmount());
        request.setAmount(amount);
        request.setAppid(dto.getAppId());
        request.setMchid(merchantId);
        request.setDescription(dto.getDescription());
        String string = ApplicationDomainUtil.apiUrl();
        request.setNotifyUrl(string + getNotifyUrl(merchantId));
        request.setOutTradeNo(orders.getId().toString());
        System.out.println(request.toString());
        // 调用下单方法，得到应答
        com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse response = service.prepay(request);
        // 使用微信扫描 code_url 对应的二维码，即可体验Native支付
        System.out.println(response.getCodeUrl());
        return response.getCodeUrl();
    }


    /**
     * 查询的native订单。
     * @param businessType
     * @param businessId
     * @return
     */
    @Override
    public boolean checkOrderSuccess(String businessType, Long businessId) {
        WechatOrders wechatOrders = baseMapper.selectOne(Wraps.<WechatOrders>lbQ()
                .eq(WechatOrders::getBusinessType, businessType)
                .eq(WechatOrders::getBusinessId, businessId)
                .last(" limit 1 "));
        if (wechatOrders == null) {
            log.error(" wechat order not find  businessType {} businessId {}", businessType, businessId);
            return false;
        }
        String tradeState = wechatOrders.getTradeState();
        String merchantId = wechatOrders.getMchid();
        if (Transaction.TradeStateEnum.SUCCESS.name().equals(tradeState)) {
            return true;
        } else if (StrUtil.isEmpty(tradeState)) {
            Config config = getConfig(merchantId);
            String payWay = wechatOrders.getPayWay();
            if (payWay == null || payWay.equals("native")) {
                NativePayService service = new NativePayService.Builder().config(config).build();
                com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest request =
                        new com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest();
                request.setMchid(merchantId);
                request.setOutTradeNo(wechatOrders.getId().toString());
                com.wechat.pay.java.service.payments.model.Transaction transaction = service.queryOrderByOutTradeNo(request);
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
                    paySuccessSendBusiness(wechatOrders.getNotifyUrl());
                    return true;
                } else if (stateEnum != null) {
                    wechatOrders.setTradeState(stateEnum.toString());
                    baseMapper.updateById(wechatOrders);
                    return false;
                }
            } else if (payWay.equals("H5")) {
                H5Service h5Service = new H5Service.Builder().config(config).build();
                QueryOrderByOutTradeNoRequest outTradeNoRequest = new QueryOrderByOutTradeNoRequest();
                outTradeNoRequest.setMchid(merchantId);
                outTradeNoRequest.setOutTradeNo(wechatOrders.getId().toString());
                com.wechat.pay.java.service.payments.model.Transaction transaction = h5Service.queryOrderByOutTradeNo(outTradeNoRequest);
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
                    paySuccessSendBusiness(wechatOrders.getNotifyUrl());
                    return true;
                } else if (stateEnum != null) {
                    wechatOrders.setTradeState(stateEnum.toString());
                    baseMapper.updateById(wechatOrders);
                    return false;
                }
            } else if (payWay.equals("JSAPI")) {

                JsapiServiceExtension jsapiServiceExtension = new JsapiServiceExtension.Builder().config(config).build();
                com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest request = new com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest();
                request.setMchid(merchantId);
                request.setOutTradeNo(wechatOrders.getId().toString());
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
                    paySuccessSendBusiness(wechatOrders.getNotifyUrl());
                    return true;
                } else if (stateEnum != null) {
                    wechatOrders.setTradeState(stateEnum.toString());
                    baseMapper.updateById(wechatOrders);
                    return false;
                }
            }
        }
        return false;
    }


    @Override
    public WechatOrders refund(WechatMinruiOrdersSaveDTO dto) {
        String businessType = dto.getBusinessType();
        Long businessId = dto.getBusinessId();
        WechatOrders wechatOrders = baseMapper.selectOne(Wraps.<WechatOrders>lbQ()
                .eq(WechatOrders::getBusinessType, businessType)
                .eq(WechatOrders::getBusinessId, businessId)
                .last(" limit 1 "));

        Config config = getConfig(wechatOrders.getMchid());
        RefundService refundService = new RefundService.Builder().config(config).build();

        CreateRequest request = new CreateRequest();

        request.setTransactionId(wechatOrders.getTransactionId());
        request.setOutTradeNo(wechatOrders.getId().toString());
        // 目前只支持全额退款。所以这里直接用UUID生产随机单号
        request.setOutRefundNo(wechatOrders.getBusinessId().toString());
//        request.setOutRefundNo(UUID.randomUUID().toString().replace("-", ""));
        request.setReason("用户申请退款");
        String string = ApplicationDomainUtil.apiUrl();
        request.setNotifyUrl(string + getRefundsNotifyUrl(wechatOrders.getMchid()));
        AmountReq amount = new AmountReq();
        amount.setRefund(Long.parseLong(dto.getAmount().toString()));
        amount.setCurrency("CNY");
        amount.setTotal(Long.parseLong(wechatOrders.getAmount().toString()));
        request.setAmount(amount);

        Refund refund = refundService.create(request);
        wechatOrders.setRefundId(refund.getRefundId());
        wechatOrders.setRefundAmount(JSON.toJSONString(refund.getAmount()));
        wechatOrders.setRefundChannel(refund.getChannel().toString());
        wechatOrders.setRefundStatus(refund.getStatus().toString());
        wechatOrders.setRefundSuccessTime(refund.getSuccessTime());
        wechatOrders.setUserReceivedAccount(refund.getUserReceivedAccount());
        wechatOrders.setRefundsAccount(refund.getFundsAccount().toString());
        baseMapper.updateById(wechatOrders);
        return wechatOrders;
    }


    @Override
    public HttpStatus refundNotificationParser(String wechatSignature, String wechatPaySerial, String wechatpayNonce, String wechatTimestamp, String requestBody, String merchantId) {

        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                .body(requestBody)
                .build();

        // 初始化 NotificationParser
        NotificationParser parser;
        if (merchantId.equals(merchantConfig.getMerchantId())) {
            parser = new NotificationParser(merchantConfig.getNotificationConfig());
        } else if (merchantId.equals(docuCardMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(docuCardMerchantConfig.getNotificationConfig());
        } else if (merchantId.equals(ckdMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(ckdMerchantConfig.getNotificationConfig());
        } else if (merchantId.equals(docusCuiteMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(docusCuiteMerchantConfig.getNotificationConfig());
        } else if (merchantId.equals(caringArticleMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(caringArticleMerchantConfig.getNotificationConfig());
        } else if (merchantId.equals(caringDocKnowMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(caringDocKnowMerchantConfig.getNotificationConfig());
        } else {
            return HttpStatus.UNAUTHORIZED;
        }

        RefundNotification notification = null;
        try {
            notification = parser.parse(requestParam, RefundNotification.class);
        } catch (ValidationException e) {
            log.error("sign verification failed", e);
            return HttpStatus.UNAUTHORIZED;
        }

        WechatOrders wechatOrders = baseMapper.selectOne(Wraps.<WechatOrders>lbQ()
                .eq(WechatOrders::getRefundId, notification.getRefundId())
                .last(" limit 0,1 "));
        if (Objects.isNull(wechatOrders)) {
            return HttpStatus.OK;
        }
        if (Objects.nonNull(notification.getRefundStatus())) {
            wechatOrders.setRefundStatus(notification.getRefundStatus().toString());
            if (Status.SUCCESS.equals(notification.getRefundStatus())) {
                SaasGlobalThreadPool.execute(() -> refundSuccessSendBusiness(wechatOrders.getNotifyUrl()));;
            }
        }

        return HttpStatus.OK;
    }

    /**
     * 发现订单支付成功了。
     * 发送通知给业务。
     * @param notifyUrl
     */
    public void refundSuccessSendBusiness(String notifyUrl) {
        // 业务处理
        String string = ApplicationDomainUtil.apiUrl();
        HttpUtils.get(string + notifyUrl + "REFUNDED");
    }

    /**
     * 发现订单支付成功了。
     * 发送通知给业务。
     * @param notifyUrl
     */
    public void paySuccessSendBusiness(String notifyUrl) {
        // 业务处理
        String string = ApplicationDomainUtil.apiUrl();
        HttpUtils.get(string + notifyUrl + "SUCCESS");
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
    public HttpStatus notificationParser(String wechatSignature, String wechatPaySerial, String wechatpayNonce,
                                         String wechatTimestamp, String requestBody, String merchantId) {
        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                .body(requestBody)
                .build();

        // 初始化 NotificationParser
        NotificationParser parser;
        if (merchantId.equals(merchantConfig.getMerchantId())) {
             parser = new NotificationParser(merchantConfig.getNotificationConfig());
        } else if (merchantId.equals(docuCardMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(docuCardMerchantConfig.getNotificationConfig());
        } else if (merchantId.equals(ckdMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(ckdMerchantConfig.getNotificationConfig());
        } else if (merchantId.equals(docusCuiteMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(docusCuiteMerchantConfig.getNotificationConfig());
        } else if (merchantId.equals(caringArticleMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(caringArticleMerchantConfig.getNotificationConfig());
        } else if (merchantId.equals(caringDocKnowMerchantConfig.getMerchantId())) {
            parser = new NotificationParser(caringDocKnowMerchantConfig.getNotificationConfig());
        } else {
            return HttpStatus.UNAUTHORIZED;
        }

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

        if (tradeState == Transaction.TradeStateEnum.SUCCESS) {
            SaasGlobalThreadPool.execute(() -> paySuccessSendBusiness(wechatOrders.getNotifyUrl()));;
        }
        return HttpStatus.OK;

    }

}
