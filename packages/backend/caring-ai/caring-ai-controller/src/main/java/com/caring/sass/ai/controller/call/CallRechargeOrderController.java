package com.caring.sass.ai.controller.call;


import com.caring.sass.ai.call.service.CallRechargeOrderService;
import com.caring.sass.ai.dto.call.CallRechargeOrderPageDTO;
import com.caring.sass.ai.dto.call.CallRechargeOrderSaveDTO;
import com.caring.sass.ai.dto.call.CallRechargeOrderUpdateDTO;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.dto.know.UserMemberPayOrder;
import com.caring.sass.ai.entity.call.CallConfig;
import com.caring.sass.ai.entity.call.CallRechargeOrder;
import com.caring.sass.ai.entity.know.KnowledgeUserOrder;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 * 前端控制器
 * 通话充值订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/callRechargeOrder")
@Api(value = "CallRechargeOrder", tags = "通话充值订单")
public class CallRechargeOrderController extends SuperController<CallRechargeOrderService, Long, CallRechargeOrder, CallRechargeOrderPageDTO, CallRechargeOrderSaveDTO, CallRechargeOrderUpdateDTO> {


    @ApiOperation("创建一个SEE链接，用于接收用户支付后的消息")
    @GetMapping("createdSee")
    public SseEmitter createdSee(@RequestParam String uid) {

        return baseService.createdSee(uid);
    }


    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    @ApiOperation("H5下单，返回微信支付链接")
    @PostMapping("createdH5Order/{userId}")
    public R<CallRechargeOrder> createdH5Order(
            HttpServletRequest resp,
            @PathVariable Long userId, @RequestParam String uid,
            @RequestBody @Validated CallConfig callConfig) {
        String clientIp = getClientIp(resp);
        CallRechargeOrder callRechargeOrder = baseService.createdH5WechatOrder(userId, uid, callConfig, clientIp);
        return R.success(callRechargeOrder);
    }



    @ApiOperation("查询订单支付情况")
    @PostMapping("queryOrderStatus/{orderId}")
    public R<CallRechargeOrder> queryOrderStatus( @PathVariable Long orderId) {
        CallRechargeOrder userOrder = baseService.queryOrderStatus(orderId);
        return R.success(userOrder);
    }



    @ApiOperation("订单已支付微信回调")
    @GetMapping("anno/{orderId}/status")
    public R<String> callUpdateWechatOrder(@PathVariable Long orderId,
                                           @RequestParam PaymentStatus status) {

        log.info("updateWechatOrder orderId {}, status {}", orderId, status);
        baseService.callUpdateWechatOrder(orderId, status);
        return R.success("success");
    }

    @ApiOperation("关闭连接")
    @DeleteMapping("closeSse")
    public R<String> closeSse(@RequestParam String uid) {

        baseService.closeSse(uid);
        return R.success("SUCCESS");
    }



}
