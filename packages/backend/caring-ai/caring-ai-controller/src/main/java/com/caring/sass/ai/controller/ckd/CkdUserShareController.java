package com.caring.sass.ai.controller.ckd;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.ai.ckd.server.CkdUserInfoService;
import com.caring.sass.ai.ckd.server.CkdUserOrderService;
import com.caring.sass.ai.ckd.server.CkdUserShareService;
import com.caring.sass.ai.dto.ckd.CkdUserSharePageDTO;
import com.caring.sass.ai.dto.ckd.CkdUserSharePageModel;
import com.caring.sass.ai.dto.ckd.CkdUserShareSaveDTO;
import com.caring.sass.ai.dto.know.PaymentStatus;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import com.caring.sass.ai.entity.ckd.CkdUserOrder;
import com.caring.sass.ai.entity.ckd.CkdUserShare;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * ckd会员分享成功转换记录
 * </p>
 *
 * @author 杨帅
 * @date 2025-03-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/ckdUserShare")
@Api(value = "CkdUserShare", tags = "ckd会员分享成功转换记录")
public class CkdUserShareController {

    @Autowired
    CkdUserShareService ckdUserShareService;


    @Autowired
    CkdUserInfoService ckdUserInfoService;


    @Autowired
    CkdUserOrderService ckdUserOrderService;



    @ApiOperation("ckd用户分享")
    @PostMapping("save")
    public R<Boolean> save(@RequestBody @Validated CkdUserShareSaveDTO data) {


        int count = ckdUserShareService.count(Wraps.<CkdUserShare>lbQ()
                .eq(CkdUserShare::getFanUsers, data.getFanUsers()));
        if (count > 0) {
            return R.success(true);
        }

        CkdUserShare userShare = new CkdUserShare();
        userShare.setShareUserId(data.getShareUserId());
        userShare.setFanUsers(data.getFanUsers());
        ckdUserShareService.save(userShare);

        return R.success(true);
    }


    @ApiOperation("查询我的粉丝")
    @PostMapping("page")
    public R<IPage<CkdUserSharePageModel>> page(@RequestBody @Validated PageParams<CkdUserSharePageDTO> params) {


        IPage<CkdUserShare> buildPage = params.buildPage();
        CkdUserSharePageDTO model = params.getModel();

        LbqWrapper<CkdUserShare> lbqWrapper = Wraps.<CkdUserShare>lbQ()
                .orderByDesc(SuperEntity::getCreateTime)
                .eq(CkdUserShare::getShareUserId, model.getShareUserId());


        ckdUserShareService.page(buildPage, lbqWrapper);

        List<CkdUserShare> records = buildPage.getRecords();
        IPage<CkdUserSharePageModel> modelIPage = new Page<>();
        modelIPage.setPages(buildPage.getPages());
        modelIPage.setCurrent(buildPage.getCurrent());
        modelIPage.setTotal(buildPage.getTotal());
        modelIPage.setSize(buildPage.getSize());
        if (records.isEmpty()) {
            return R.success(modelIPage);
        }


        List<CkdUserSharePageModel> ckdUserSharePageModels = new ArrayList<>();

        CkdUserSharePageModel sharePageModel = new CkdUserSharePageModel();
        // 封装用户的信息， 用户订单
        List<Long> longList = records.stream().map(CkdUserShare::getFanUsers).collect(Collectors.toList());
        List<CkdUserInfo> userInfos = ckdUserInfoService.list(Wraps.<CkdUserInfo>lbQ().in(SuperEntity::getCreateUser, longList));
        List<String> openIds = userInfos.stream().map(CkdUserInfo::getOpenId).collect(Collectors.toList());
        Map<Long, CkdUserInfo> userInfoMap = userInfos.stream().collect(Collectors.toMap(SuperEntity::getCreateUser, item -> item));

        List<CkdUserOrder> userOrders = ckdUserOrderService.list(Wraps.<CkdUserOrder>lbQ()
                .in(CkdUserOrder::getOpenId, openIds)
                .in(CkdUserOrder::getPaymentStatus, PaymentStatus.SUCCESS, PaymentStatus.REFUNDED, PaymentStatus.WAITING_FOR_REFUND));

        Map<String, List<CkdUserOrder>> listMap = userOrders.stream().collect(Collectors.groupingBy(CkdUserOrder::getOpenId));

        for (Long id : longList) {
            sharePageModel = new CkdUserSharePageModel();
            CkdUserInfo userInfo = userInfoMap.get(id);
            sharePageModel.setFanUserId(id);
            if (Objects.isNull(userInfo)) {
                sharePageModel.setUserName("未注册用户");
            } else {
                sharePageModel.setUserName(userInfo.getNickname());
                sharePageModel.setCreateTime(userInfo.getCreateTime());
                sharePageModel.setMembershipLevel(userInfo.getMembershipLevel());
                sharePageModel.setExpirationDate(userInfo.getExpirationDate());

                List<CkdUserOrder> orderList = listMap.get(userInfo.getOpenId());
                if (orderList != null) {
                    orderList.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
                    sharePageModel.setOrderList(orderList);
                } else {
                    sharePageModel.setOrderList(new ArrayList<>()); // 或者其他处理方式
                }
                sharePageModel.setOrderList(orderList);

            }
            ckdUserSharePageModels.add(sharePageModel);

        }
        modelIPage.setRecords(ckdUserSharePageModels);

        return R.success(modelIPage);
    }








}
