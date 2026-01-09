package com.caring.sass.ai.controller.knows;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.ai.dto.know.*;
import com.caring.sass.ai.entity.know.KnowDoctorType;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.entity.know.KnowledgeUserOrder;
import com.caring.sass.ai.know.dao.KnowledgeUserMapper;
import com.caring.sass.ai.know.service.KnowledgeUserOrderService;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.wx.entity.config.WechatOrders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 知识库用户购买会员订单
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeUserOrder")
@Api(value = "KnowledgeUserOrder", tags = "知识库-知识库用户购买会员订单")
public class KnowledgeUserOrderController {

    @Autowired
    KnowledgeUserOrderService baseService;

    @Autowired
    KnowledgeUserService knowledgeUserService;

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


    /**
     * 博主查询订阅者订单列表
     * @return
     */
    @ApiOperation("博主查询订阅者订单列表")
    @PostMapping("querySubscriberList")
    public R<IPage<SubscriberList>> querySubscriberList(@RequestBody PageParams<KnowledgeUserOrder> params) {

        IPage<KnowledgeUserOrder> builtPage = params.buildPage();
        KnowledgeUserOrder model = params.getModel();
        String userDomain = model.getUserDomain();
        LbqWrapper<KnowledgeUserOrder> wrapper = Wraps.<KnowledgeUserOrder>lbQ()
                .eq(KnowledgeUserOrder::getPaymentStatus, PaymentStatus.SUCCESS)
                .eq(KnowledgeUserOrder::getUserDomain, userDomain)
                .orderByDesc(SuperEntity::getCreateTime);

        baseService.page(builtPage, wrapper);

        List<KnowledgeUserOrder> records = builtPage.getRecords();
        IPage<SubscriberList> page = new Page(params.getCurrent(), params.getSize());
        page.setTotal(builtPage.getTotal());
        page.setPages(builtPage.getPages());
        if (records.isEmpty()) {
            return R.success(page);
        }
        List<Long> knowledgeUserIds = records.stream().map(KnowledgeUserOrder::getUserId).collect(Collectors.toList());
        List<KnowledgeUser> knowledgeUsers = knowledgeUserService.listByIds(knowledgeUserIds);
        if (knowledgeUsers.isEmpty()) {
            return R.success(page);
        }
        Map<Long, KnowledgeUser> knowledgeUserMap = knowledgeUsers.stream().collect(Collectors.toMap(KnowledgeUser::getId, knowledgeUser -> knowledgeUser));
        KnowledgeUser chiefPhysician = knowledgeUserService.getOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserDomain, userDomain)
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .last("limit 1"));
        List<SubscriberList> subscriberList = new ArrayList<>();
        page.setRecords(subscriberList);
        for (KnowledgeUserOrder record : records) {
            KnowledgeUser knowledgeUser = knowledgeUserMap.get(record.getUserId());
            if (knowledgeUser == null) {
                continue;
            }

            subscriberList.add(SubscriberList
                    .builder()
                    .userId(record.getUserId())
                    .userName(knowledgeUser.getUserName())
                    .userMobile(SensitiveInfoUtils.desensitizePhone(knowledgeUser.getUserMobile()))
                    .userAvatar(knowledgeUser.getUserAvatar())
                    .memberNickname(chiefPhysician.getSubscribeUserName())
                    .goodsType(record.getGoodsType())
                    .goodsPrice(record.getGoodsPrice())
                    .paymentTime(record.getPaymentTime())
                    .membershipExpiration(record.getMembershipExpiration())
                    .build());
        }

        return R.success(page);


    }


    /**
     * 订阅者查询自己订阅的博主订单
     * @return
     */
    @ApiOperation("订阅者查询自己订阅的博主订单")
    @GetMapping("querySubscriberBloggerList")
    public R<List<SubscriberBloggerList>> querySubscriberBloggerList(@RequestParam Long userId,
                                                                     @RequestParam String userName) {

        List<String> userDomain = null;
        if (StrUtil.isNotBlank(userName)) {
            List<KnowledgeUser> knowledgeUsers = knowledgeUserService.list(Wraps.<KnowledgeUser>lbQ()
                    .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                    .like(KnowledgeUser::getUserName, userName));
            userDomain = knowledgeUsers.stream().map(KnowledgeUser::getUserDomain).collect(Collectors.toList());
            if (userDomain.isEmpty()) {
                return R.success(new ArrayList<>());
            }
        }

        LbqWrapper<KnowledgeUserOrder> wrapper = Wraps.<KnowledgeUserOrder>lbQ()
                .eq(KnowledgeUserOrder::getPaymentStatus, PaymentStatus.SUCCESS)
                .eq(KnowledgeUserOrder::getUserId, userId)
                .orderByDesc(SuperEntity::getCreateTime);
        if (userDomain != null) {
            wrapper.in(KnowledgeUserOrder::getUserDomain, userDomain);
        }

        List<KnowledgeUserOrder> userOrders = baseService.list(wrapper);

        Set<String> collect = userOrders.stream().map(KnowledgeUserOrder::getUserDomain).collect(Collectors.toSet());
        List<KnowledgeUser> knowledgeUsers = knowledgeUserService.list(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .in(KnowledgeUser::getUserDomain, collect));

        if (knowledgeUsers.isEmpty()) {
            return R.success(new ArrayList<>());
        }
        Map<String, KnowledgeUser> userMap = knowledgeUsers.stream().collect(Collectors.toMap(KnowledgeUser::getUserDomain, knowledgeUser -> knowledgeUser));
        List<SubscriberBloggerList> records = new ArrayList<>();
        for (KnowledgeUserOrder userOrder : userOrders) {
            KnowledgeUser knowledgeUser = userMap.get(userOrder.getUserDomain());
            if (knowledgeUser == null) {
                continue;
            }
            records.add(SubscriberBloggerList
                    .builder()
                    .userId(knowledgeUser.getId())
                    .userName(knowledgeUser.getUserName())
                    .userAvatar(knowledgeUser.getUserAvatar())
                    .userHospital(knowledgeUser.getWorkUnit())
                    .userDepartment(knowledgeUser.getDepartment())
                    .userTitle(knowledgeUser.getDoctorTitle())
                    .memberNickname(knowledgeUser.getSubscribeUserName())
                    .goodsType(userOrder.getGoodsType())
                    .goodsPrice(userOrder.getGoodsPrice())
                    .paymentTime(userOrder.getPaymentTime())
                    .membershipExpiration(userOrder.getMembershipExpiration())
                    .build());
        }
        return R.success(records);

    }


    @ApiOperation("H5下单，返回微信支付链接")
    @PostMapping("createdH5Order")
    public R<String> createdH5Order(
            HttpServletRequest resp,
            @RequestBody @Validated UserMemberPayOrder memberPayOrder) {
        String clientIp = getClientIp(resp);
        String string = baseService.createdH5WechatOrder(memberPayOrder, clientIp);
        return R.success(string);
    }

    @ApiOperation("JSAPI下单，返回订单信息")
    @PostMapping("createdJSAPIOrder")
    public R<WechatOrders> createdJSAPIOrder(@RequestBody @Validated UserMemberPayOrder memberPayOrder) {
        WechatOrders wechatOrders = baseService.createdJSAPIOrder(memberPayOrder);
        return R.success(wechatOrders);
    }



    @ApiOperation("创建一个购买会员订单，返回微信支付链接")
    @PostMapping("createdWechatOrder")
    public R<String> createdWechatOrder(@RequestBody @Validated UserMemberPayOrder memberPayOrder) {

        String string = baseService.createdWechatOrder(memberPayOrder);
        return R.success(string);
    }



    @ApiOperation("查询订单支付情况")
    @PostMapping("queryOrderStatus")
    public R<KnowledgeUserOrder> queryOrderStatus(@RequestBody @Validated UserMemberPayOrder memberPayOrder) {
        KnowledgeUserOrder userOrder = baseService.queryOrderStatus(memberPayOrder);
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

    @Autowired
    KnowledgeUserMapper knowledgeUserMapper;

    @ApiOperation("订单已支付微信回调")
    @PostMapping("/freeOrder")
    public R<KnowledgeUserOrder> saveFreeOrder(@RequestParam String domain, @RequestParam Long userId) {

        // 检查博主订阅是否开启。
        KnowledgeUser knowledgeUser = knowledgeUserMapper.selectOne(Wraps.<KnowledgeUser>lbQ()
                .eq(KnowledgeUser::getUserType, KnowDoctorType.CHIEF_PHYSICIAN)
                .eq(KnowledgeUser::getUserDomain, domain));
        if (knowledgeUser == null) {
            throw new BizException("博主不存在");
        }
        if (knowledgeUser.getSubscribeSwitch()) {
            throw new BizException("博主开启订阅设置，不能购买免费订阅");
        }
        KnowledgeUserOrder userOrder = baseService.saveFreeOrder(userId, domain);
        return R.success(userOrder);



    }



}
