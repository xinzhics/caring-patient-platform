package com.caring.sass.ai.controller.ckd;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.ckd.server.CkdUserOrderService;
import com.caring.sass.ai.dto.card.BusinessCardUserOrderSaveDTO;
import com.caring.sass.ai.dto.ckd.CkdLaboratoryTestReportPageDTO;
import com.caring.sass.ai.dto.ckd.CkdUserOrderPageDTO;
import com.caring.sass.ai.dto.ckd.CkdUserOrderSaveDTO;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.card.BusinessCardUserOrder;
import com.caring.sass.ai.entity.ckd.CkdLaboratoryTestReport;
import com.caring.sass.ai.entity.ckd.CkdUserOrder;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
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
 * ckd会员订单
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdUserOrder")
@Api(value = "CkdUserOrder", tags = "ckd会员订单")
public class CkdUserOrderController {


    @Autowired
    CkdUserOrderService baseService;


    @ApiOperation("创建一个微信订单")
    @PostMapping("createdWechatOrder")
    public R<WechatOrders> createdWechatOrder(@RequestBody @Validated CkdUserOrderSaveDTO orderSaveDTO) {


        WechatOrders wechatOrders = baseService.createdWechatOrder(orderSaveDTO);
        return R.success(wechatOrders);
    }


    @ApiOperation("已支付的订单列表")
    @PostMapping("page")
    public R<IPage<CkdUserOrder>> page(@RequestBody @Validated PageParams<CkdUserOrderPageDTO> params) {

        IPage<CkdUserOrder> buildPage = params.buildPage();
        CkdUserOrderPageDTO model = params.getModel();
        String openId = model.getOpenId();
        if (StrUtil.isEmpty(openId)) {
            throw new RuntimeException("openId不能为空");
        }
        LbqWrapper<CkdUserOrder> wrapper = Wraps.<CkdUserOrder>lbQ()
                .eq(CkdUserOrder::getOpenId, openId)
                .in(CkdUserOrder::getPaymentStatus, PaymentStatus.SUCCESS, PaymentStatus.REFUNDED, PaymentStatus.WAITING_FOR_REFUND)
                .orderByDesc(CkdUserOrder::getCreateTime);
        baseService.page(buildPage, wrapper);
        return R.success(buildPage);
    }


    @ApiOperation("申请退款订单")
    @PutMapping("refund/{ckdOrderId}")
    public R<CkdUserOrder> refund(@PathVariable Long ckdOrderId) {

        CkdUserOrder ckdUserOrder = baseService.refund(ckdOrderId);

        return R.success(ckdUserOrder);
    }



    @ApiOperation("查询订单支付情况")
    @GetMapping("queryOrderStatus/{ckdOrderId}")
    public R<CkdUserOrder> queryOrderStatus(@PathVariable Long ckdOrderId) {
        CkdUserOrder userOrder = baseService.queryOrderStatus(ckdOrderId);
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
