package com.caring.sass.ai.controller.article;

import com.caring.sass.ai.article.service.ArticleUserOrderService;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.article.ArticleUserOrder;
import com.caring.sass.ai.entity.article.ArticleUserOrderGoods;
import com.caring.sass.base.R;
import com.caring.sass.wx.entity.config.WechatOrders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * <p>
 * 前端控制器
 * 科普创作用户支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleUserOrder")
@Api(value = "ArticleUserOrder", tags = "科普创作用户支付订单")
public class ArticleUserOrderController  {


    @Autowired
    ArticleUserOrderService baseService;


    @ApiOperation("创建一个SEE链接，用于接收用户支付后的消息")
    @GetMapping("createdSee")
    public SseEmitter createdSee(@RequestParam String uid) {

        return baseService.createdSee(uid);
    }

    @ApiOperation("关闭连接")
    @DeleteMapping("closeSse")
    public R<String> closeSse(@RequestParam String uid) {

        baseService.closeSse(uid);
        return R.success("SUCCESS");
    }


    @ApiOperation("手机端JSAPI下单，返回订单信息")
    @PostMapping("createdJSAPIOrder")
    public R<WechatOrders> createdJSAPIOrder(@RequestBody @Validated ArticleUserOrderGoods memberPayOrder) {
        WechatOrders wechatOrders = baseService.createdJSAPIOrder(memberPayOrder);
        return R.success(wechatOrders);
    }


    @ApiOperation("web端创建一个购买会员订单，返回微信支付链接")
    @PostMapping("createdWechatOrder")
    public R<String> createdWechatOrder(@RequestBody @Validated ArticleUserOrderGoods memberPayOrder) {

        String string = baseService.createdWechatOrder(memberPayOrder);
        return R.success(string);
    }



    @ApiOperation("查询订单支付情况")
    @PostMapping("queryOrderStatus")
    public R<ArticleUserOrder> queryOrderStatus(@RequestBody @Validated ArticleUserOrderGoods memberPayOrder) {
        ArticleUserOrder userOrder = baseService.queryOrderStatus(memberPayOrder);
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
