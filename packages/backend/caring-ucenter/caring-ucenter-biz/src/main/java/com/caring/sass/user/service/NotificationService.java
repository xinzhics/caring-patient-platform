package com.caring.sass.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.constant.NotificationSendStatus;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.user.dto.NotificationSaveDto;
import com.caring.sass.user.entity.BulkNotification;

import java.util.Map;

/**
 * @ClassName NotificationService
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 11:22
 * @Version 1.0
 **/
public interface NotificationService {

    /**
     * 创建一个消息
     * 生成消息未发送记录
     *
     */
    void createNotification(NotificationSaveDto notificationSaveDto);

    /**
     * 直接删除。发送中的 消息体无视 有效性
     * @param notificationId
     */
    void deleteNotification(Long notificationId);

    /**
     * 发送消息通知
     * 1. 查询接收人群体的信息
     * 2. 封装 消息体
     * 3. 推送消息到 redis 消息 队列
     *
     * @param notificationId
     */
    void sendNotification(Long notificationId);

    /**
     * 分页查询消息
     * @param iPage
     * @param lbqWrapper
     */
    void page(IPage<BulkNotification> iPage, LbqWrapper<BulkNotification> lbqWrapper);

    /**
     * 统计发送消息的预计数量
     * @param notificationTarget
     * @return
     */
    Map<NotificationTarget, Integer> countSendMessageNumber(NotificationTarget notificationTarget);


    NotificationSaveDto getNotification(Long notificationId);


    NotificationSendStatus getNotificationStatus(Long notificationId);

}
