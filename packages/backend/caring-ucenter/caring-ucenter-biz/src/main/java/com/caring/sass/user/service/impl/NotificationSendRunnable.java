package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.constant.NotificationSendStatus;
import com.caring.sass.user.dao.BulkNotificationMapper;
import com.caring.sass.user.dao.BulkNotificationSendRecordMapper;
import com.caring.sass.user.entity.BulkNotification;
import com.caring.sass.user.entity.BulkNotificationSendRecord;
import com.caring.sass.user.util.NotificationTemplateMsgUtil;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SendTemplateMessageForm;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName NotificationSendCompont
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 16:45
 * @Version 1.0
 */
@Slf4j
@Component
public class NotificationSendRunnable {

    private BulkNotificationMapper notificationMapper;

    private BulkNotificationSendRecordMapper sendRecordMapper;

    private WeiXinApi weiXinApi;

    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("notification-send-", false);

    private static final ExecutorService PACKAGE_EXECUTOR = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS,
            new SaasLinkedBlockingQueue<>(500), THREAD_FACTORY);


    public NotificationSendRunnable(BulkNotificationMapper notificationMapper,
                                    BulkNotificationSendRecordMapper sendRecordMapper,
                                    WeiXinApi weiXinApi) {
        this.sendRecordMapper = sendRecordMapper;
        this.weiXinApi = weiXinApi;
        this.notificationMapper = notificationMapper;
    }

    public void pushMessage(String tenantCode, String appId, String url, TemplateMsgDto templateMsgDto, BulkNotification bulkNotification) {
        PACKAGE_EXECUTOR.execute(() -> sendMessage(tenantCode, appId, url, templateMsgDto, bulkNotification));
    }

    /**
     * 由一个线程对 一个群发任务进行推送。
     * @param tenantCode
     * @param appId
     * @param url
     * @param templateMsgDto
     * @param bulkNotification
     */
    public void sendMessage(String tenantCode, String appId, String url, TemplateMsgDto templateMsgDto, BulkNotification bulkNotification) {
        BaseContextHandler.setTenant(tenantCode);
        Long bulkNotificationId = bulkNotification.getId();
        int current = 1, size = 100;
        IPage<BulkNotificationSendRecord> page;
        LbqWrapper<BulkNotificationSendRecord> lbqWrapper = Wraps.<BulkNotificationSendRecord>lbQ()
                .eq(BulkNotificationSendRecord::getNotificationId, bulkNotificationId)
                .eq(BulkNotificationSendRecord::getNotificationSendStatus, NotificationSendStatus.WAIT_SEND);
        page = new Page(current, size);
        IPage<BulkNotificationSendRecord> selectPage = sendRecordMapper.selectPage(page, lbqWrapper);
        List<BulkNotificationSendRecord> sendRecords = selectPage.getRecords();
        if (CollUtil.isNotEmpty(sendRecords)) {
            for (BulkNotificationSendRecord record : sendRecords) {
                try {
                    send(tenantCode, record, appId, url, templateMsgDto);
                } catch (Exception e) {
                    record.setErrorMsg("发送异常， {"+e.getMessage() + "}");
                    record.setNotificationSendStatus(NotificationSendStatus.ERROR);
                    updateSendRecord(record, tenantCode);
                }
            }
        }
        // 统计一下还有没有 等待发送的 记录
        Integer count = sendRecordMapper.selectCount(Wraps.<BulkNotificationSendRecord>lbQ()
                .eq(BulkNotificationSendRecord::getNotificationId, bulkNotificationId)
                .eq(BulkNotificationSendRecord::getNotificationSendStatus, NotificationSendStatus.WAIT_SEND));
        if (count == null || count == 0) {
            BulkNotification notification = notificationMapper.selectById(bulkNotificationId);
            if (Objects.nonNull(notification)) {
                notification.setNotificationSendStatus(NotificationSendStatus.FINISH);
                try {
                    // 防止更新时, 记录已经不存在数据库导致更新失败
                    notificationMapper.updateById(notification);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        } else if (count > 0){
            sendMessage(tenantCode, appId, url, templateMsgDto, bulkNotification);
        }

    }


    private void send(String tenantCode, BulkNotificationSendRecord sendRecord,
                    String appId, String url, TemplateMsgDto msgDtoData) {
        BaseContextHandler.setTenant(tenantCode);
        sendRecord.setNotificationSendStatus(NotificationSendStatus.SENDING);
        sendRecordMapper.updateById(sendRecord);
        try {
            if (StringUtils.isEmpty(sendRecord.getOpenId())) {
                sendRecord.setErrorMsg("openId不存在");
                sendRecord.setNotificationSendStatus(NotificationSendStatus.ERROR);
            } else {
                // 创建微信消息
                SendTemplateMessageForm messageForm = NotificationTemplateMsgUtil.create(appId, url,
                        sendRecord.getOpenId(), sendRecord.getTargetName(), msgDtoData);
                if (Objects.isNull(messageForm)) {
                    sendRecord.setNotificationSendStatus(NotificationSendStatus.ERROR);
                    sendRecord.setErrorMsg("微信模板不存在");
                } else {
                    R<String> templateMessage = weiXinApi.sendTemplateMessage(messageForm);
                    if (templateMessage.getIsSuccess() != null && templateMessage.getIsSuccess()) {
                        sendRecord.setNotificationSendStatus(NotificationSendStatus.FINISH);
                        sendRecord.setErrorMsg("发送成功");
                    } else {
                        String messageData = templateMessage.getMsg();
                        if (StringUtils.isNotEmptyString(messageData)) {
                            try {
                                sendRecord.setErrorMsg("发送消息异常");
                                sendRecord.setWxError(messageData);
                            } catch (Exception e) {
                                sendRecord.setErrorMsg("发送消息异常");
                            }
                        }
                        sendRecord.setNotificationSendStatus(NotificationSendStatus.ERROR);
                    }
                }
            }
            // 更新推送状态
            updateSendRecord(sendRecord, tenantCode);
        } catch (Exception e) {
            sendRecord.setErrorMsg("发送异常， {"+e.getMessage() + "}");
            sendRecord.setNotificationSendStatus(NotificationSendStatus.ERROR);
            updateSendRecord(sendRecord, tenantCode);
        }
    }

    /**
     * 更新 消息发送记录的状态。
     * 检查是否有 可以更新 消息通知为 发送完成
     */
    public void updateSendRecord(BulkNotificationSendRecord sendRecord, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        try {
            if (sendRecord.getId() != null) {
                sendRecordMapper.updateById(sendRecord);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}
