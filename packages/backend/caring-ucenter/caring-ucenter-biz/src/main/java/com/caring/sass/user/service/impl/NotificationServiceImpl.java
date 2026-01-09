package com.caring.sass.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.enums.NotificationTarget;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.user.constant.NotificationSendStatus;
import com.caring.sass.user.dao.BulkNotificationMapper;
import com.caring.sass.user.dto.NotificationSaveDto;
import com.caring.sass.user.entity.BulkNotification;
import com.caring.sass.user.service.NotificationService;
import com.caring.sass.wx.TemplateMsgApi;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsSaveDTO;
import com.caring.sass.wx.dto.template.TemplateMsgSaveDTO;
import com.caring.sass.wx.entity.config.Config;
import com.caring.sass.wx.entity.template.TemplateMsg;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName NotificationServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2021/11/2 11:27
 * @Version 1.0
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    TemplateMsgApi templateMsgApi;

    @Autowired
    BulkNotificationMapper bulkNotificationMapper;

    @Autowired
    NotificationSendServiceImpl notificationSendService;

    @Autowired
    WeiXinApi weiXinApi;


    /**
     * 创建一个消息
     * 生成消息未发送记录
     *
     */
    @Override
    public void createNotification(NotificationSaveDto notificationSaveDto) {

        List<NotificationSaveDto.FieldsDto> fieldsDtoList = notificationSaveDto.getFieldsDtoList();
        String templateId = notificationSaveDto.getTemplateId();
        String templateName = notificationSaveDto.getTemplateName();

        // 封装微信的模板dto
        TemplateMsgSaveDTO templateMsgSaveDTO = getTemplateMsgSaveDTO(fieldsDtoList, templateId, templateName);
        R<TemplateMsg> templateMsgR = templateMsgApi.saveTemplateMsgAndFields(templateMsgSaveDTO);

        if (templateMsgR.getIsSuccess() != null && templateMsgR.getIsSuccess()) {
            TemplateMsg templateMsg = templateMsgR.getData();

            BulkNotification notification = initBulkNotification(templateMsg, notificationSaveDto);
            bulkNotificationMapper.insert(notification);

        } else {
            throw new BizException("保存微信模板失败");
        }

    }

    /**
     * 分页查询消息
     * @param iPage
     * @param lbqWrapper
     */
    @Override
    public void page(IPage<BulkNotification> iPage, LbqWrapper<BulkNotification> lbqWrapper) {
        iPage = bulkNotificationMapper.selectPage(iPage, lbqWrapper);
        List<BulkNotification> records = iPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<Long> collect = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
        Map<Long, Integer> countSend = notificationSendService.countSend(collect);
        for (BulkNotification record : records) {
            Integer count = countSend.get(record.getId());
            if (Objects.nonNull(count)) {
                record.setSendNumber(count);
            } else {
                record.setSendNumber(0);
            }
        }
    }

    /**
     * 删除 消息通知
     * @param notificationId
     */
    @Override
    public void deleteNotification(Long notificationId) {
        BulkNotification bulkNotification = bulkNotificationMapper.selectById(notificationId);
        if (Objects.nonNull(bulkNotification)) {
            bulkNotificationMapper.deleteById(notificationId);
            templateMsgApi.deleteTemplate(bulkNotification.getSassTemplateMsgId(), BaseContextHandler.getTenant());
            notificationSendService.deleteAll(notificationId);
        }
    }


    /**
     * 发送消息通知
     * 1. 查询接收人群体的信息
     * 2. 封装 消息体
     * 3. 推送消息到 redis 消息 队列
     *
     * @param notificationId
     */
    @Override
    public void sendNotification(Long notificationId) {

        BulkNotification notification = bulkNotificationMapper.selectById(notificationId);
        notification.setSendTime(LocalDateTime.now());
        notification.setNotificationSendStatus(NotificationSendStatus.SENDING);
        bulkNotificationMapper.updateById(notification);
        String tenant = BaseContextHandler.getTenant();
        // 获取当前项目的 wxAppId
        R<Config> config = weiXinApi.getConfig(new Config());
        if (config.getIsSuccess() == null || !config.getIsSuccess()) {
            throw new BizException("获取微信公众号信息失败");
        }
        Config data = config.getData();
        if (Objects.isNull(data)) {
            return;
        }
        String appId = data.getAppId();
        if (StringUtils.isEmpty(appId)) {
            return;
        }
        // 查询推送的模板消息
        R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getOneById(notification.getSassTemplateMsgId());
        if (templateMsgDtoR.getIsSuccess() == null || !templateMsgDtoR.getIsSuccess()) {
            throw new BizException("获取微信模板失败");
        }
        TemplateMsgDto msgDtoData = templateMsgDtoR.getData();
        if (Objects.isNull(msgDtoData)) {
            return;
        }
        SaasGlobalThreadPool.execute(() -> notificationSendService.initMessageSendRecord(tenant, notification, appId, msgDtoData));

    }


    /**
     * 初始化通知消息对象
     * @param templateMsg
     * @param notificationSaveDto
     * @return
     */
    private BulkNotification initBulkNotification(TemplateMsg templateMsg, NotificationSaveDto notificationSaveDto) {
        String code = getCode();

        // 尽量保证code数据库这个项目中是唯一的
        while (true) {
            Integer count = bulkNotificationMapper.selectCount(Wraps.<BulkNotification>lbQ().eq(BulkNotification::getCode, code));
            if (count == null || count > 0) {
                code = getCode();
            } else {
                break;
            }
        }
        BulkNotification selectOne = bulkNotificationMapper.selectOne(Wraps.<BulkNotification>lbQ()
                .last(" order by message_id desc limit 0, 1")
        );
        Long messageId = 1000L;
        if (Objects.nonNull(selectOne)) {
            messageId = selectOne.getMessageId() + 1;
        }
        return BulkNotification.builder()
                .sassTemplateMsgId(templateMsg.getId())
                .code(code)
                .jumpBusinessContent(notificationSaveDto.getJumpBusinessContent())
                .notificationJumpType(notificationSaveDto.getNotificationJumpType())
                .notificationTarget(notificationSaveDto.getNotificationTarget())
                .notificationSendStatus(NotificationSendStatus.WAIT_SEND)
                .messageId(messageId)
                .templateName(notificationSaveDto.getTemplateName())
                .notificationName(notificationSaveDto.getNotificationName())
                .build();
    }

    /**
     * 封装微信模板参数
     * @param fieldsDtoList
     * @param templateId
     * @param templateName
     * @return
     */
    private TemplateMsgSaveDTO getTemplateMsgSaveDTO(List<NotificationSaveDto.FieldsDto> fieldsDtoList, String templateId, String templateName) {

        TemplateMsgSaveDTO templateMsgSaveDTO = new TemplateMsgSaveDTO();
        templateMsgSaveDTO.setIndefiner(TemplateMessageIndefiner.MASS_MAILING_NOTIFY);
        templateMsgSaveDTO.setTemplateId(templateId);
        templateMsgSaveDTO.setTitle(templateName);

        TemplateMsgFieldsSaveDTO fieldsSaveDTO;
        List<TemplateMsgFieldsSaveDTO> msgFieldsSaveDTOS = new ArrayList<>(fieldsDtoList.size());
        for (NotificationSaveDto.FieldsDto fieldsDto : fieldsDtoList) {
            fieldsSaveDTO = new TemplateMsgFieldsSaveDTO();
            fieldsSaveDTO.setAttr(fieldsDto.getAttr());
            fieldsSaveDTO.setLabel(fieldsDto.getLabel());
            fieldsSaveDTO.setValue(fieldsDto.getValue());
            fieldsSaveDTO.setColor(fieldsDto.getColor());
            fieldsSaveDTO.setType(0);
            msgFieldsSaveDTOS.add(fieldsSaveDTO);
        }
        templateMsgSaveDTO.setFields(msgFieldsSaveDTOS);
        return templateMsgSaveDTO;
    }


    /**
     * 统计 关注 公众号的 患者，医生数量。
     * @param notificationTarget
     * @return
     */
    @Override
    public Map<NotificationTarget, Integer> countSendMessageNumber(NotificationTarget notificationTarget) {
        if (Objects.isNull(notificationTarget)) {
            return null;
        }
        Map<NotificationTarget, Integer> map = new HashMap<>();
        R<Map<NotificationTarget, Integer>> wxTagUser = weiXinApi.countWxTagUser(notificationTarget);
        if (wxTagUser.getIsSuccess()) {
            Map<NotificationTarget, Integer> tagUserData = wxTagUser.getData();
            return tagUserData;
        }
        return map;
    }

    @Override
    public NotificationSaveDto getNotification(Long notificationId) {

        NotificationSaveDto notificationSaveDto = new NotificationSaveDto();
        BulkNotification notification = bulkNotificationMapper.selectById(notificationId);
        if (Objects.isNull(notification)) {
            return null;
        }
        notificationSaveDto.setJumpBusinessContent(notification.getJumpBusinessContent());
        notificationSaveDto.setNotificationJumpType(notification.getNotificationJumpType());
        notificationSaveDto.setNotificationName(notification.getNotificationName());
        notificationSaveDto.setNotificationTarget(notification.getNotificationTarget());
        notificationSaveDto.setTemplateName(notification.getTemplateName());

        R<TemplateMsgDto> templateMsgDtoR = templateMsgApi.getOneById(notification.getSassTemplateMsgId());
        if (templateMsgDtoR.getIsSuccess() != null && templateMsgDtoR.getIsSuccess()) {
            TemplateMsgDto msgDtoRData = templateMsgDtoR.getData();
            List<TemplateMsgFields> fields = msgDtoRData.getFields();
            notificationSaveDto.setTemplateId(msgDtoRData.getTemplateId());
            List<NotificationSaveDto.FieldsDto> fieldsDtoList = new ArrayList<>(fields.size());
            NotificationSaveDto.FieldsDto fieldsDto;
            for (TemplateMsgFields msgFields : fields) {
                fieldsDto = new NotificationSaveDto.FieldsDto();
                fieldsDto.setAttr(msgFields.getAttr());
                fieldsDto.setColor(msgFields.getColor());
                fieldsDto.setLabel(msgFields.getLabel());
                fieldsDto.setValue(msgFields.getValue());
                fieldsDtoList.add(fieldsDto);
            }
            notificationSaveDto.setFieldsDtoList(fieldsDtoList);
        }

        return notificationSaveDto;


    }

    /**
     *  返回一个 6位数的code
     * @return
     */
    public static String getCode() {
        String mark = "";
        for (int i = 0; i < 6; i++) {
            int random = (int) (Math.random() * 10);
            mark = mark + random;
        }
        return mark;
    }


    @Override
    public NotificationSendStatus getNotificationStatus(Long notificationId) {

        BulkNotification notification = bulkNotificationMapper.selectById(notificationId);
        if (Objects.nonNull(notification)) {
            return notification.getNotificationSendStatus();
        }
        return null;

    }
}

