package com.caring.sass.ai.controller.card;

import com.caring.sass.ai.card.service.BusinessCardUserOrderService;
import com.caring.sass.ai.dto.card.BusinessCardUserOrderSaveDTO;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.card.BusinessCardUserOrder;
import com.caring.sass.base.R;
import com.caring.sass.wx.entity.config.WechatOrders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 科普名片会员支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-01-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessCardUserOrder")
@Api(value = "BusinessCardUserOrder", tags = "科普名片会员支付订单")
public class BusinessCardUserOrderController {

    @Autowired
    BusinessCardUserOrderService baseService;

    @ApiOperation("创建一个微信订单")
    @PostMapping("createdWechatOrder")
    public R<WechatOrders> createdWechatOrder(@RequestBody @Validated BusinessCardUserOrderSaveDTO orderSaveDTO) {


        WechatOrders wechatOrders = baseService.createdWechatOrder(orderSaveDTO);
        return R.success(wechatOrders);
    }


    @ApiOperation("查询订单支付情况")
    @GetMapping("queryOrderStatus")
    public R<BusinessCardUserOrder> queryOrderStatus(@RequestBody @Validated Long businessCardId) {
        BusinessCardUserOrder userOrder = baseService.queryOrderStatus(businessCardId);
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


}
