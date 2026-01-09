package com.caring.sass.wx.controller.config;


import com.caring.sass.base.R;
import com.caring.sass.wx.dto.config.WechatMinruiOrdersSaveDTO;
import com.caring.sass.wx.dto.config.WechatOrdersPageDTO;
import com.caring.sass.wx.entity.config.WechatOrders;
import com.caring.sass.wx.service.config.WechatMinruiOrderService;
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
@Api(value = "WechatOrders", tags = "微信敏瑞商户支付订单")
public class WechatMinruiOrdersController {


    @Autowired
    WechatMinruiOrderService baseService;

    @ApiOperation("native支付下单")
    @PostMapping("createWechatNativeOrders")
    public R<String> createWechatNativeOrders(@RequestBody @Validated WechatMinruiOrdersSaveDTO dto) {

        String nativeOrders = baseService.createWechatNativeOrders(dto);
        return R.success(nativeOrders);
    }

    @ApiOperation("H5支付下单")
    @PostMapping("createWechatH5Orders")
    public R<String> createWechatH5Orders(@RequestBody @Validated WechatMinruiOrdersSaveDTO dto) {

        String nativeOrders = baseService.createWechatH5Orders(dto);
        return R.success(nativeOrders);
    }

    @ApiOperation("JSAPI支付下单")
    @PostMapping("createWechatJSAPIOrders")
    public R<WechatOrders> createWechatJSAPIOrders(@RequestBody @Validated WechatMinruiOrdersSaveDTO dto) {

        WechatOrders wechatOrders = baseService.createWechatJSAPIOrders(dto);
        return R.success(wechatOrders);
    }



    @ApiOperation("支付订单状态查询")
    @PostMapping("queryWechatNativeOrders")
    public R<Boolean> queryWechatNativeOrders(@RequestBody @Validated WechatOrdersPageDTO dto) {

        String businessType = dto.getBusinessType();
        Long businessId = dto.getBusinessId();
        boolean status = baseService.checkOrderSuccess(businessType, businessId);
        return R.success(status);
    }

    @ApiOperation("申请退款")
    @PostMapping("refund")
    public R<WechatOrders> refund(@RequestBody @Validated WechatMinruiOrdersSaveDTO dto) {

        WechatOrders wechatOrders = baseService.refund(dto);
        return R.success(wechatOrders);
    }




}
