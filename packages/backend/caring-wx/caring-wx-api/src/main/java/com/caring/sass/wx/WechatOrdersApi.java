package com.caring.sass.wx;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.caring.sass.base.R;
import com.caring.sass.wx.dto.config.*;
import com.caring.sass.wx.entity.config.WechatOrders;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @ClassName com.caring.sass.wx.IWeiXinService
 * @Description 对外服务接口
 * @Author yangShuai
 * @Date 2020/9/16 9:49
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.oauth-server:caring-wx-server}", path = "/wechatOrders",
        qualifier = "WechatOrdersApi")
public interface WechatOrdersApi {


    @ApiOperation("业务下单")
    @PostMapping("createWechatOrders")
    R<WechatOrders> createWechatOrders(@RequestBody @Validated WechatOrdersSaveDTO dto);


    @ApiOperation("判断订单是否支付")
    @GetMapping("checkOrderSuccess/{orderId}")
    R<Boolean> checkOrderSuccess(@PathVariable(name = "orderId") Long orderId);

    @ApiOperation("native支付下单")
    @PostMapping("createWechatNativeOrders")
    R<String> createWechatNativeOrders(@RequestBody @Validated WechatMinruiOrdersSaveDTO dto);

    @ApiOperation("H5支付下单")
    @PostMapping("createWechatH5Orders")
    R<String> createWechatH5Orders(@RequestBody @Validated WechatMinruiOrdersSaveDTO dto);


    @ApiOperation("JSAPI支付下单")
    @PostMapping("createWechatJSAPIOrders")
    R<WechatOrders> createWechatJSAPIOrders(@RequestBody @Validated WechatMinruiOrdersSaveDTO dto);

    @ApiOperation("native支付订单状态查询")
    @PostMapping("queryWechatNativeOrders")
    R<Boolean> queryWechatNativeOrders(@RequestBody @Validated WechatOrdersPageDTO dto);

    @ApiOperation("申请退款")
    @PostMapping("refund")
    R<WechatOrders> refund(@RequestBody @Validated WechatMinruiOrdersSaveDTO dto);

}
