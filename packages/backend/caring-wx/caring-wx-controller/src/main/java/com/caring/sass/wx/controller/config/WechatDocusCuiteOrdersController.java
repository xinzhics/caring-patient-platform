package com.caring.sass.wx.controller.config;


import com.caring.sass.base.R;
import com.caring.sass.wx.dto.config.WechatOrdersSaveDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import com.caring.sass.wx.service.config.WechatMiniAppOrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 微信支付订单
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-20
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/wechatOrders")
@Api(value = "WechatOrders", tags = "微信哆咔叽支付订单")
public class WechatDocusCuiteOrdersController{


    @Autowired
    WechatMiniAppOrdersService baseService;

    @ApiOperation("业务下单")
    @PostMapping("createWechatOrders")
    public R<WechatOrders> createWechatOrders(@RequestBody @Validated WechatOrdersSaveDTO dto) {

        WechatOrders wechatOrders = baseService.createWechatOrders(dto);
        return R.success(wechatOrders);
    }


    @ApiOperation("判断订单是否支付")
    @GetMapping("checkOrderSuccess/{orderId}")
    public R<Boolean> checkOrderSuccess(@PathVariable(name = "orderId") Long orderId) {

        boolean status = baseService.checkOrderSuccess(orderId);
        return R.success(status);
    }



}
