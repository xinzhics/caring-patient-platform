package com.caring.sass.wx.service.template.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import com.caring.sass.lock.DistributedLock;
import com.caring.sass.wx.dao.template.TemplateMsgMapper;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.*;
import com.caring.sass.wx.entity.template.TemplateMsg;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import com.caring.sass.wx.service.config.ConfigService;
import com.caring.sass.wx.service.template.TemplateMsgFieldsService;
import com.caring.sass.wx.service.template.TemplateMsgService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 模板消息
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class TemplateMsgServiceImpl extends SuperServiceImpl<TemplateMsgMapper, TemplateMsg> implements TemplateMsgService {

    private final TemplateMsgFieldsService templateMsgFieldsService;

    private final DatabaseProperties databaseProperties;

    private final RedisTemplate<String, String> redisTemplate;

    private final ConfigService configService;

    @Autowired
    DistributedLock distributedLock;

    public TemplateMsgServiceImpl(TemplateMsgFieldsService templateMsgFieldsService, DatabaseProperties databaseProperties,
                                  RedisTemplate<String, String> redisTemplate, ConfigService configService) {
        this.templateMsgFieldsService = templateMsgFieldsService;
        this.databaseProperties = databaseProperties;
        this.redisTemplate = redisTemplate;
        this.configService = configService;
    }


    /**
     * 根据indefiner获取模板消息
     * 优先从redis中获取
     * redis中不存在，从数据库中获取
     * @param indefiner
     * @return
     */
    @Override
    public TemplateMsgDto getTemplateMsgDtoByIndefiner(String indefiner) {

        String field = BaseContextHandler.getTenant() + "_" + indefiner;
        Object object = redisTemplate.opsForHash().get(TemplateMsgService.TEMPLATE_MSG_INDERFINDER_KEY, field);
        if (object == null) {
            List<TemplateMsgDto> templateMsgDtoList = findBusinessTemplateMessage(null, null, indefiner);
            if (CollUtil.isNotEmpty(templateMsgDtoList)) {
                String jsonString = JSON.toJSONString(templateMsgDtoList.get(0));
                redisTemplate.opsForHash().put(TemplateMsgService.TEMPLATE_MSG_INDERFINDER_KEY, field, jsonString);
                return templateMsgDtoList.get(0);
            }
        } else {
            TemplateMsgDto templateMsgDto = JSON.parseObject(object.toString(), TemplateMsgDto.class);
            return templateMsgDto;
        }
        return null;
    }



    /**
     * 检查微信公众号的 模版中 是否还存在当前 传递的模版。
     * 最多每小时检测一次。
     * 如果模版不存在，则删除本地的这个模版，重新去微信公众号申请模板
     * @param templateMsg
     * @return
     */
    @Override
    public Boolean checkTemplateExist(TemplateMsgDto templateMsg) {
        String tenant = BaseContextHandler.getTenant();
        String key = "tenant:" + tenant + templateMsg.getTemplateId();
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey != null && hasKey) {
            return true;
        }
        GeneralForm generalForm = new GeneralForm();
        generalForm.setTenantCode(tenant);
        // 查询微信公众号的模版列表
        List<WxMpTemplate> wxMpTemplates = configService.loadTemplateMessage(generalForm);
        boolean templateExist = false;
        for (WxMpTemplate template : wxMpTemplates) {
            String templateId = template.getTemplateId();
            if (templateMsg.getTemplateId().equals(templateId)) {
                templateExist = true;
                break;
            }
        }
        if (templateExist) {
            return templateExist;
        }

        List<Long> longList = ListUtil.of(templateMsg.getId());
        removeByIds(longList);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        return false;

    }


    /**
     * 初始化 医生对待办消息 评论后的推送的模板消息
     * @return
     */
    @Override
    public TemplateMsgDto initDoctorCommentReminderMsg() {

        String tenant = BaseContextHandler.getTenant();
        String lock = "DOCTOR_COMMENT_REMINDER" + tenant;
        boolean lockBoolean = false;
        TemplateMsg templateMsg;
        try {
            lockBoolean = distributedLock.lock(lock, 5000L, 5, 1000);
            if (lockBoolean) {
                // 查询当前 待就诊 和已就诊 的数量
                LbqWrapper<TemplateMsg> queryWrapper = new LbqWrapper<>();
                queryWrapper.last("limit 0,1");
                queryWrapper.eq(TemplateMsg::getIndefiner, TemplateMessageIndefiner.DOCTOR_COMMENT_REMINDER);
                templateMsg = baseMapper.selectOne(queryWrapper);
                if (Objects.isNull(templateMsg) || StrUtil.isEmpty(templateMsg.getTemplateId())) {
                    // 获取微信公众号的模板
                    GeneralForm generalForm = new GeneralForm();
                    generalForm.setTenantCode(tenant);
                    List<WxMpTemplate> wxMpTemplates = configService.loadTemplateMessage(generalForm);
                    // 查询 其中是否有 服务工单已推送提醒
                    WxMpTemplate wxMpTemplate = null;
                    for (WxMpTemplate template : wxMpTemplates) {
                        String title = template.getTitle();
                        if (title.equals("服务工单已接收提醒")) {
                            wxMpTemplate = template;
                            break;
                        }
                    }
                    String templateId;
                    if (Objects.isNull(wxMpTemplate) || StrUtil.isEmpty(wxMpTemplate.getTemplateId())) {
                        List<String> keywordNameList = new ArrayList<>();
                        keywordNameList.add("服务日期");
                        keywordNameList.add("姓名");
                        keywordNameList.add("服务类型");
                        templateId = configService.apiAddTemplate("44619", keywordNameList);
                        if (templateId == null) {
                            log.error("add fuwu gongdan error tenant code: {}", tenant);
                            throw new BizException("add fuwu gongdan error tenant code: " + tenant);
                        }
                    } else {
                        templateId = wxMpTemplate.getTemplateId();
                    }
                    if (Objects.isNull(templateMsg)) {
                        templateMsg = new TemplateMsg();
                        templateMsg.setIndefiner(TemplateMessageIndefiner.DOCTOR_COMMENT_REMINDER);
                        templateMsg.setTemplateId(templateId);
                        templateMsg.setTitle("服务工单已接收提醒");
                        baseMapper.insert(templateMsg);
                    } else if (Objects.nonNull(templateMsg.getId())) {
                        templateMsg.setTemplateId(templateId);
                        removeByRedis(templateMsg.getIndefiner());
                        baseMapper.updateById(templateMsg);
                    }
                }
            } else {
                throw new BizException("添加公众号模板超时");
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }
        TemplateMsgDto msgDto = new TemplateMsgDto();
        BeanUtils.copyProperties(templateMsg, msgDto);
        msgDto.setCommonCategory(true);
        return msgDto;
    }


    /**
     * 初始化 服务工单已推送提醒
     * @return
     */
    @Override
    public TemplateMsgDto initCommonCategoryServiceWorkOrderMsg() {

        String tenant = BaseContextHandler.getTenant();
        String lock = "initCommonCategoryServiceWorkOrder" + tenant;
        boolean lockBoolean = false;
        TemplateMsg templateMsg;
        try {
            lockBoolean = distributedLock.lock(lock, 5000L, 5, 1000);
            if (lockBoolean) {
                // 查询当前 待就诊 和已就诊 的数量
                LbqWrapper<TemplateMsg> queryWrapper = new LbqWrapper<>();
                queryWrapper.last("limit 0,1");
                queryWrapper.eq(TemplateMsg::getIndefiner, TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
                templateMsg = baseMapper.selectOne(queryWrapper);
                if (Objects.isNull(templateMsg) || StrUtil.isEmpty(templateMsg.getTemplateId())) {
                    // 获取微信公众号的模板
                    GeneralForm generalForm = new GeneralForm();
                    generalForm.setTenantCode(tenant);
                    List<WxMpTemplate> wxMpTemplates = configService.loadTemplateMessage(generalForm);
                    // 查询 其中是否有 服务工单已推送提醒
                    WxMpTemplate wxMpTemplate = null;
                    for (WxMpTemplate template : wxMpTemplates) {
                        String title = template.getTitle();
                        if (title.equals("服务工单已推送提醒")) {
                            wxMpTemplate = template;
                            break;
                        }
                    }
                    String templateId;
                    if (Objects.isNull(wxMpTemplate) || StrUtil.isEmpty(wxMpTemplate.getTemplateId())) {
                        List<String> keywordNameList = new ArrayList<>();
                        keywordNameList.add("姓名");
                        keywordNameList.add("服务类型");
                        keywordNameList.add("日期");
                        templateId = configService.apiAddTemplate("44618", keywordNameList);
                        if (templateId == null) {
                            log.error("add fuwu gongdan error tenant code: {}", tenant);
                            throw new BizException("add fuwu gongdan error tenant code: " + tenant);
                        }
                    } else {
                        templateId = wxMpTemplate.getTemplateId();
                    }
                    if (Objects.isNull(templateMsg)) {
                        templateMsg = new TemplateMsg();
                        templateMsg.setIndefiner(TemplateMessageIndefiner.COMMON_CATEGORY_SERVICE_WORK_ORDER);
                        templateMsg.setTemplateId(templateId);
                        templateMsg.setTitle("服务工单已推送提醒");
                        baseMapper.insert(templateMsg);
                    } else if (Objects.nonNull(templateMsg.getId())) {
                        templateMsg.setTemplateId(templateId);
                        removeByRedis(templateMsg.getIndefiner());
                        baseMapper.updateById(templateMsg);
                    }
                }
            } else {
                throw new BizException("添加公众号模板超时");
            }
        } finally {
            if (lockBoolean) {
                distributedLock.releaseLock(lock);
            }
        }
        TemplateMsgDto msgDto = new TemplateMsgDto();
        BeanUtils.copyProperties(templateMsg, msgDto);
        msgDto.setCommonCategory(true);
        return msgDto;
    }

    /**
     * @param businessId 业务Id
     * @param templateId 微信模板消息Id
     * @param indefiner  业务类型
     * @return java.util.List<com.caring.sass.wx.dto.template.TemplateMsgDto>
     * @Author yangShuai
     * @Description 查询业务的模板消息. 群发通知模板，不需要 超管端去管理。所以此处将其排除。
     * @Date 2020/9/16 18:19
     */
    @Override
    public List<TemplateMsgDto> findBusinessTemplateMessage(String businessId, Long templateId, String indefiner) {

        LbqWrapper<TemplateMsg> queryWrapper = new LbqWrapper<>();

        if (!StringUtils.isEmpty(businessId)) {
            queryWrapper.eq(TemplateMsg::getBusinessId, businessId);
        }

        if (null != indefiner) {
            queryWrapper.eq(TemplateMsg::getIndefiner, indefiner);
        } else {
            queryWrapper.notIn(TemplateMsg::getIndefiner, TemplateMessageIndefiner.MASS_MAILING_NOTIFY,
                    TemplateMessageIndefiner.COMPLETENESS_INFORMATION);
        }


        if (null != templateId) {
            queryWrapper.eq(TemplateMsg::getTemplateId, templateId);
        }
        List<TemplateMsg> templateMsgs = baseMapper.selectList(queryWrapper);
        List<TemplateMsgDto> templateMsgDtos = new ArrayList<>(templateMsgs.size());
        TemplateMsgDto templateMsgDto;
        if (!CollectionUtils.isEmpty(templateMsgs)) {
            for (TemplateMsg templateMsg : templateMsgs) {
                templateMsgDto = BeanUtil.toBean(templateMsg, TemplateMsgDto.class);
                templateMsgDto.setId(templateMsg.getId());
                List<TemplateMsgFields> templateMsgFields = templateMsgFieldsService.findByMsgTemplateId(templateMsg.getId());
                templateMsgDto.setFields(templateMsgFields);
                templateMsgDtos.add(templateMsgDto);
            }
        }
        return templateMsgDtos;
    }

    /**
     * @return boolean
     * @Author yangShuai
     * @Description 删除模板消息时， 删除模板消息带有的属性
     * @Date 2020/9/17 10:46
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        List<TemplateMsg> templateMsgs = baseMapper.selectBatchIds(idList);
        if (Objects.nonNull(templateMsgs)) {
            for (TemplateMsg templateMsg : templateMsgs) {
                this.templateMsgFieldsService.deleteByTemplateId(templateMsg.getId());
                removeByRedis(templateMsg.getIndefiner());
                baseMapper.deleteById(templateMsg.getId());
            }
        }
        return true;
    }


    /**
     * 将此关键词对应的模版。从redis中移除
     * @param indefiner
     */
    private void removeByRedis(String indefiner) {

        String field = BaseContextHandler.getTenant() + "_" + indefiner;
        redisTemplate.opsForHash().delete(TemplateMsgService.TEMPLATE_MSG_INDERFINDER_KEY, field);
    }


    /**
     * @return boolean
     * @Author yangShuai
     * @Description 通过模板消息Id 设置 微信公众号模板Id
     * @Date 2020/9/17 10:45
     */
    @Override
    public boolean updateWeiXinTemplateId(Long templateMsgId, String weiXinTemplateId) {

        TemplateMsg templateMsg = baseMapper.selectById(templateMsgId);
        if (Objects.nonNull(templateMsg)) {
            removeByRedis(templateMsg.getIndefiner());
            UpdateWrapper<TemplateMsg> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("template_id", weiXinTemplateId);
            updateWrapper.eq("id", templateMsgId);
            baseMapper.update(templateMsg, updateWrapper);
            return true;
        }
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateMsg saveTemplateMsg(TemplateMsgSaveDTO templateMsgSaveDTO) {
        TemplateMsg templateMsg = new TemplateMsg();
        List<TemplateMsgFieldsSaveDTO> saveDTOFields = templateMsgSaveDTO.getFields();
        BeanUtil.copyProperties(templateMsgSaveDTO, templateMsg);
        int insert = baseMapper.insert(templateMsg);
        if (insert > 0) {
            templateMsgFieldsService.save(saveDTOFields, templateMsg.getId());
            return templateMsg;
        }
        return templateMsg;
    }

    /**
     * 更新模板信息属性
     * @param templateMsgUpdateDTO
     * @return
     */
    @Override
    public TemplateMsg updateTemplateMsgResultMsg(TemplateMsgUpdateDTO templateMsgUpdateDTO) {
        TemplateMsg templateMsg = new TemplateMsg();
        List<TemplateMsgFieldsUpdateDTO> updateDTOFields = templateMsgUpdateDTO.getFields();
        BeanUtil.copyProperties(templateMsgUpdateDTO, templateMsg);
        int insert;
        if (templateMsgUpdateDTO.getId() != null) {
            templateMsg.setId(templateMsgUpdateDTO.getId());
            removeByRedis(templateMsg.getIndefiner());
            insert = baseMapper.updateById(templateMsg);
        } else {
            insert = baseMapper.insert(templateMsg);
        }
        if (insert > 0) {
            if (!CollectionUtils.isEmpty(updateDTOFields)) {
                for (TemplateMsgFieldsUpdateDTO updateDTOField : updateDTOFields) {
                    updateDTOField.setTemplateId(templateMsg.getId());
                    updateDTOField.setId(null);
                }
                templateMsgFieldsService.deleteByTemplateId(templateMsg.getId());
                templateMsgFieldsService.update(updateDTOFields);
            } else {
                templateMsgFieldsService.deleteByTemplateId(templateMsg.getId());
            }
        }
        return templateMsg;
    }

    @Override
    public boolean updateTemplateMsg(TemplateMsgUpdateDTO templateMsgUpdateDTO) {

        updateTemplateMsgResultMsg(templateMsgUpdateDTO);
        return true;

    }


    /**
     * @return com.caring.sass.wx.dto.template.TemplateMsgDto
     * @Author yangShuai
     * @Description 获取一个模板消息的信息和 属性
     * @Date 2020/9/17 11:25
     */
    @Override
    public TemplateMsgDto getOneById(Long id) {
        TemplateMsg templateMsg = baseMapper.selectById(id);
        TemplateMsgDto templateMsgDto = BeanUtil.toBean(templateMsg, TemplateMsgDto.class);
        List<TemplateMsgFields> byMsgTemplateId = templateMsgFieldsService.findByMsgTemplateId(id);
        templateMsgDto.setFields(byMsgTemplateId);
        return templateMsgDto;
    }


    /**
     * @return boolean
     * @Author yangShuai
     * @Description 初始化模板消息
     * @Date 2020/9/29 11:06
     */
    @Override
    public boolean initTemplateMsg() {
        List<TemplateMsg> templateMsgList = ListUtil.list(false,
                TemplateMsg.builder().title("用药提醒").indefiner(TemplateMessageIndefiner.MEDICINE_TAKE_ALERT_TEMPLATE_MESSAGE).build(),
                TemplateMsg.builder().title("医生消息未读通知").indefiner(TemplateMessageIndefiner.UN_READ_MESSAGE_ALERT_TEMPLATE_MESSAGE).build(),
                TemplateMsg.builder().title("助理消息提醒通知").indefiner(TemplateMessageIndefiner.COMMISSIONER_MESSAGE_UN_READ_TEMPLATE_MESSAGE).build(),
                TemplateMsg.builder().title("入组提醒").indefiner(TemplateMessageIndefiner.ENTRY_GROUP_ALERT_TEMPLATE_MESSAGE).build(),
                TemplateMsg.builder().title("预约结果通知").indefiner(TemplateMessageIndefiner.RESERVATION_TEMPLATE_MESSAGE).build(),
                TemplateMsg.builder().title("待审核预约").indefiner(TemplateMessageIndefiner.RESERVATION_UPDATE_TEMPLATE_MESSAGE).build(),
                TemplateMsg.builder().title("咨询回复").indefiner(TemplateMessageIndefiner.CONSULTATION_RESPONSE).build(),
                TemplateMsg.builder().title("聊天提醒(患者)").indefiner(TemplateMessageIndefiner.CONSULTATION_RESPONSE_PATIENT).build(),
                TemplateMsg.builder().title("聊天提醒(医生)").indefiner(TemplateMessageIndefiner.CONSULTATION_RESPONSE_NURSING).build(),
                TemplateMsg.builder().title("病例讨论开始通知").indefiner(TemplateMessageIndefiner.CONSULTATION_NOTICE).build(),
                TemplateMsg.builder().title("病例讨论消息通知").indefiner(TemplateMessageIndefiner.CONSULTATION_PROCESSING).build(),
                TemplateMsg.builder().title("病例讨论结束通知").indefiner(TemplateMessageIndefiner.CONSULTATION_END).build(),
                TemplateMsg.builder().title("血压测量提醒（第一次）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("血压测量提醒（第二次）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("学习计划通知").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("复查通知").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("健康日志").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("第一次血糖测量提醒（06:30）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("第二次血糖测量提醒血糖（09:00）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("第三次血糖测量提醒血糖（11:30）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("第四次血糖测量提醒血糖（14:00）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("第五次血糖测量提醒血糖（17:30）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("第六次血糖测量提醒血糖（20:00）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("第七次血糖测量提醒血糖（22:00）").indefiner(TemplateMessageIndefiner.NURSING_PLAN).build(),
                TemplateMsg.builder().title("转诊卡").indefiner(TemplateMessageIndefiner.REFERRAL_CARD).build()
        );
        super.saveBatch(templateMsgList);
        return true;
    }

    @Override
    public Map<Long, Long> copyTemplateMsgAndFields(String fromTenantCode, String toTenantCode) {
        String currentTenant = BaseContextHandler.getTenant();
        // 查找出需要复制的项目数据
        BaseContextHandler.setTenant(fromTenantCode);
        List<TemplateMsg> templateMsgs = baseMapper.selectList(Wrappers.emptyWrapper());
        List<TemplateMsgFields> templateMsgFields = templateMsgFieldsService.list();
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        // 模板id匹配
        Map<Long, Long> templateMsgIdMaps = new HashMap<>();
        templateMsgs.forEach(t -> templateMsgIdMaps.put(t.getId(), snowflake.nextId()));

        // 修改栏目数据
        List<TemplateMsg> toSaveTemplateMsgs = templateMsgs.stream().peek(c -> {
            c.setId(templateMsgIdMaps.get(c.getId()));
            c.setTemplateId(null);
        }).collect(Collectors.toList());

        for (TemplateMsgFields fields : templateMsgFields) {
            fields.setTemplateId(templateMsgIdMaps.get(fields.getTemplateId()));
            fields.setId(snowflake.nextId());
        }

        // 保存修改后的数据
        BaseContextHandler.setTenant(toTenantCode);
        saveBatchSomeColumn(toSaveTemplateMsgs);
        templateMsgFieldsService.saveBatch(templateMsgFields);
        BaseContextHandler.setTenant(currentTenant);
        return templateMsgIdMaps;
    }
}
