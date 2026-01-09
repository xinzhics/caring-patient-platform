package com.caring.sass.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.constant.NotificationSendStatus;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.user.dto.NotificationPageDto;
import com.caring.sass.user.dto.NotificationSaveDto;
import com.caring.sass.user.entity.BulkNotification;
import com.caring.sass.user.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * @ClassName NotificationController
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 11:15
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("notification")
@Api(value = "NotificationController", tags = "群发通知")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @ApiOperation("创建一个通知等待发送")
    @PostMapping("createNotification")
    public R<Boolean> createNotification(@RequestBody @Validated NotificationSaveDto notificationSaveDto) {

        notificationService.createNotification(notificationSaveDto);
        return R.success(true);

    }

    @ApiOperation("发送通知")
    @PostMapping("sendNotification/{notificationId}")
    public R<Boolean> sendNotification(@PathVariable Long notificationId) {

        notificationService.sendNotification(notificationId);
        return R.success(true);

    }


    @ApiOperation("查询任务的状态是否已经完成")
    @PostMapping("getNotificationStatus/{notificationId}")
    public R<NotificationSendStatus> getNotificationStatus(@PathVariable Long notificationId) {

        NotificationSendStatus notificationStatus = notificationService.getNotificationStatus(notificationId);
        return R.success(notificationStatus);

    }


    @ApiOperation("获取一个通知的详情")
    @PostMapping("getNotification/{notificationId}")
    public R<NotificationSaveDto> getNotification(@PathVariable Long notificationId) {

        NotificationSaveDto notification = notificationService.getNotification(notificationId);
        return R.success(notification);

    }


    @ApiOperation("统计发送消息预计人数")
    @Deprecated
    @PostMapping("countSendMessageNumber")
    public R<Map<NotificationTarget, Integer>> countSendMessageNumber(@RequestParam NotificationTarget notificationTarget) {

        Map<NotificationTarget, Integer> integerMap = notificationService.countSendMessageNumber(notificationTarget);
        return R.success(integerMap);

    }

    @ApiOperation("删除通知")
    @PostMapping("deleteNotification/{notificationId}")
    public R<Boolean> deleteNotification(@PathVariable Long notificationId) {

        notificationService.deleteNotification(notificationId);
        return R.success(true);

    }

    @ApiOperation("分页查询通知")
    @PostMapping("page")
    public R<IPage<BulkNotification>> page(@RequestBody PageParams<NotificationPageDto> params) {
        IPage<BulkNotification> iPage = params.buildPage();
        NotificationPageDto model = params.getModel();
        String notificationName = model.getNotificationName();
        NotificationSendStatus sendStatus = model.getNotificationSendStatus();
        NotificationTarget target = model.getNotificationTarget();
        LbqWrapper<BulkNotification> lbqWrapper = Wraps.<BulkNotification>lbQ();
        if (StringUtils.isNotEmptyString(notificationName)) {
            lbqWrapper.likeLeft(BulkNotification::getNotificationName, notificationName);
        }
        if (Objects.nonNull(sendStatus)) {
            lbqWrapper.eq(BulkNotification::getNotificationSendStatus, sendStatus);
        }
        if (Objects.nonNull(target)) {
            lbqWrapper.eq(BulkNotification::getNotificationTarget, target);
        }
        lbqWrapper.orderByAsc(BulkNotification::getSendTime);
        notificationService.page(iPage, lbqWrapper);


        return R.success(iPage);

    }

}
