package com.caring.sass.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.constant.KeyWordEnum;
import com.caring.sass.user.dao.KeywordReplyMapper;
import com.caring.sass.user.dao.KeywordReplyTriggerRecordMapper;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.KeywordReply;
import com.caring.sass.user.entity.KeywordReplyTriggerRecord;
import com.caring.sass.user.entity.KeywordSetting;
import com.caring.sass.user.redis.KeywordReplyRedisDTO;
import com.caring.sass.user.redis.UcenterRedisKeyConstant;
import com.caring.sass.user.service.KeywordReplyService;
import com.caring.sass.user.service.KeywordSettingService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 关键字回复内容
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Service

public class KeywordReplyServiceImpl extends SuperServiceImpl<KeywordReplyMapper, KeywordReply> implements KeywordReplyService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    KeywordSettingService keywordSettingService;

    @Autowired
    KeywordReplyTriggerRecordMapper keywordReplyTriggerRecordMapper;


    @Autowired
    TenantApi tenantApi;

    /**
     * 保存回复规则
     * 保存关键字
     * 缓存 回复规则 关键字 到redis
     * @param keywordReplySaveDTO
     * @return
     */
    @Override
    public KeywordReply save(KeywordReplySaveDTO keywordReplySaveDTO) {

        List<KeywordSettingSaveDTO> settingDTOList = keywordReplySaveDTO.getKeywordSettingDTOList();
        if (CollUtil.isEmpty(settingDTOList)) {
            throw new BizException("关键字设置不能为空");
        }
        KeywordReply keywordReply = new KeywordReply();
        BeanUtils.copyProperties(keywordReplySaveDTO, keywordReply);
        keywordReply.setStatus(KeyWordEnum.open.toString());
        baseMapper.insert(keywordReply);
        KeywordSetting setting;
        for (KeywordSettingSaveDTO settingSaveDTO : settingDTOList) {
            setting = new KeywordSetting();
            BeanUtils.copyProperties(settingSaveDTO, setting);
            setting.setKeywordReplyId(keywordReply.getId());
            keywordSettingService.save(setting);
        }
        updateKeywordReplyRedis(keywordReply);
        return keywordReply;

    }

    /**
     * 修改回复规则
     * 修改关键字
     *
     * @param keywordReplyUpdateDTO
     * @return
     */
    @Override
    public KeywordReply update(KeywordReplyUpdateDTO keywordReplyUpdateDTO) {
        List<KeywordSettingUpdateDTO> settingUpdateDTOS = keywordReplyUpdateDTO.getKeywordSettingDTOList();
        if (CollUtil.isEmpty(settingUpdateDTOS)) {
            throw new BizException("关键字设置不能为空");
        }
        KeywordReply keywordReply = new KeywordReply();
        BeanUtils.copyProperties(keywordReplyUpdateDTO, keywordReply);
        baseMapper.updateById(keywordReply);
        KeywordSetting setting;
        List<KeywordSetting> settings = keywordSettingService.list(Wraps.<KeywordSetting>lbQ()
                .eq(KeywordSetting::getKeywordReplyId, keywordReplyUpdateDTO.getId())
                .select(SuperEntity::getId, KeywordSetting::getKeywordName));
        Set<Long> keywordSettingIds = settings.stream().map(SuperEntity::getId).collect(Collectors.toSet());
        for (KeywordSettingUpdateDTO settingUpdateDTO : settingUpdateDTOS) {
            if (settingUpdateDTO.getId() != null) {
                keywordSettingIds.remove(settingUpdateDTO.getId());
            }
            setting = new KeywordSetting();
            BeanUtils.copyProperties(settingUpdateDTO, setting);
            setting.setKeywordReplyId(keywordReply.getId());
            keywordSettingService.saveOrUpdate(setting);
        }
        keywordSettingService.removeByIds(keywordSettingIds);
        updateKeywordReplyRedis(keywordReply);
        return keywordReply;
    }

    /**
     * 更新回复规则的状态。
     *
     * 开启时， 将关键字缓存到redis
     * 关闭时， 将关键字从redis中移出
     * @param params
     * @return
     */
    @Override
    public boolean updateStatus(KeywordReplyStatusUpdateDto params) {

        Long id = params.getId();
        String status = params.getStatus();
        KeywordReply keywordReply = new KeywordReply();
        keywordReply.setId(id);
        if (KeyWordEnum.open.toString().equals(status)) {
            keywordReply.setStatus(status);
            // 开启规则。
            baseMapper.updateById(keywordReply);
            List<KeywordSetting> keywordSettings = keywordSettingService.listByReplyId(id);
            // 将关键字存储到项目redis上
            if (CollUtil.isNotEmpty(keywordSettings)) {
                for (KeywordSetting setting : keywordSettings) {
                    keywordSettingService.updateKeywordSettingRedis(setting);
                }
            }
        }
        if (KeyWordEnum.close.toString().equals(status)) {
            // 关闭回复规则。
            keywordReply.setStatus(status);
            baseMapper.updateById(keywordReply);
            // 将关键字缓存从redis中移出
            List<KeywordSetting> keywordSettings = keywordSettingService.listByReplyId(id);
            if (CollUtil.isNotEmpty(keywordSettings)) {
                for (KeywordSetting setting : keywordSettings) {
                    keywordSettingService.removeKeywordSettingRedis(setting.getId());
                }
            }

        }
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            removeById(id);
        }
        return true;
    }

    /**
     *
     * 移除回复规则
     * 更新redis上回复规则的触发次数
     *
     * 移除 回复规则下的 关键字列表
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        baseMapper.deleteById(id);
        // 回复规则被移除。
        // 更新redis上回复规则的触发次数

        Long keywordReplyId = Long.parseLong(id.toString());
        deleteKeywordReplyRedis(keywordReplyId);

        // 此规则生命周期内的触发次数
        Object o = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordTenantKeyWordReplyTriggerFrequency).get(keywordReplyId.toString());
        if (o != null) {
            int i = Integer.parseInt(o.toString());
            if (i > 0) {
                // 项目的规则总触发次数 减去 此回复规则一共的触发次数
                String tenantAllTriggerRecordFrequencyKey = getRedisTenantAllTriggerRecordFrequencyKey();
                BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(tenantAllTriggerRecordFrequencyKey);
                boundHashOps.increment(BaseContextHandler.getTenant(), -i);
            }
            redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordTenantKeyWordReplyTriggerFrequency).delete(keywordReplyId.toString());
        }

        // 获取此规则 最近7天的触发次数。
        // 今日触发次数
        LocalDate localDate = LocalDate.now();
        // 此回复规则 的每日触发次数
        String everyDayTriggerFrequency = getRedisEveryReplyEveryDayTriggerFrequency(keywordReplyId);
        BoundHashOperations<String, Object, Object> objectBoundHashOperations = redisTemplate.boundHashOps(everyDayTriggerFrequency);

        // 项目回复规则的 每日触发次数
        String triggerFrequency = getRedisEveryDayTriggerFrequency();
        BoundHashOperations<String, Object, Object> boundHashOperations = redisTemplate.boundHashOps(triggerFrequency);
        for (int i = 0; i > -7; i--) {
            LocalDate plusDays = localDate.plusDays(i);
            Object o1 = objectBoundHashOperations.get(plusDays.toString());
            if (o1 != null) {
                int number = Integer.parseInt(o1.toString());
                if (number > 0) {
                    boundHashOperations.increment(plusDays.toString(), -number);
                }
                objectBoundHashOperations.delete(plusDays.toString());
            }
        }

        // 项目 总的触发次数 要
        keywordSettingService.removeByReplyId(id);
        return true;
    }



    /**
     * 从redis中获取 关键字对应的回复规则设置
     *
     * @param keywordReplyId
     * @return
     */
    @Override
    public KeywordReplyRedisDTO getKeywordReplyRedis(Long keywordReplyId) {
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordProjectReply);
        String tenant = BaseContextHandler.getTenant();
        KeywordReplyRedisDTO replyRedisDTO = new KeywordReplyRedisDTO();
        String fieldName = String.format(UcenterRedisKeyConstant.KeywordProjectReplyFieldName, tenant, keywordReplyId);
        Object o = hashOperations.get(fieldName);
        if (Objects.isNull(o)) {
            KeywordReply keywordReply = baseMapper.selectById(keywordReplyId);
            updateKeywordReplyRedis(keywordReply);
            BeanUtils.copyProperties(keywordReply, replyRedisDTO);
            return replyRedisDTO;
        } else {
            return JSON.parseObject(o.toString(), KeywordReplyRedisDTO.class);
        }
    }

    /**
     * 移除redis缓存中的回复规则
     * @param keywordReplyId
     */
    private void deleteKeywordReplyRedis(Long keywordReplyId) {
        try {
            BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordProjectReply);
            String tenant = BaseContextHandler.getTenant();
            String fieldName = String.format(UcenterRedisKeyConstant.KeywordProjectReplyFieldName, tenant, keywordReplyId);
            hashOperations.delete(fieldName);
        } catch (Exception e) {
            log.info("移除redis缓存中的回复规则 失败 {}", keywordReplyId);
        }

    }


    /**
     * 缓存回复规则内容
     * @param keywordReply
     */
    private void updateKeywordReplyRedis(KeywordReply keywordReply) {

        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordProjectReply);
        String tenant = BaseContextHandler.getTenant();
        KeywordReplyRedisDTO replyRedisDTO = new KeywordReplyRedisDTO();
        BeanUtils.copyProperties(keywordReply, replyRedisDTO);
        String fieldName = String.format(UcenterRedisKeyConstant.KeywordProjectReplyFieldName, tenant, keywordReply.getId());
        hashOperations.put(fieldName, JSON.toJSONString(replyRedisDTO));

    }



    @Override
    public String getCmsContentLink(Long replyId){

        KeywordReply reply = baseMapper.selectById(replyId);
        String replyContent = reply.getReplyContent();

        R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
        Boolean success = tenantR.getIsSuccess();
        if (!success) {
            throw new BizException("服务繁忙");
        }
        Tenant tenant = tenantR.getData();

        String url = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()), H5Router.CMS_DETAIL, "id=" + replyContent);
        return url;
    }

    /**
     * 获取项目的所有规则 总的触发次数 的redis key
     * @return
     */
    private String getRedisTenantAllTriggerRecordFrequencyKey() {
        return UcenterRedisKeyConstant.KeywordTenantTriggerFrequency;
    }

    /**
     * 项目 每个规则 每日触发次数
     * @param replyId
     * @return
     */
    private String getRedisEveryReplyEveryDayTriggerFrequency(Long replyId) {
        return String.format(UcenterRedisKeyConstant.KeywordTenantEveryReplyEveryDayTriggerFrequency,
                BaseContextHandler.getTenant(), replyId);
    }

    /**
     * 项目 所有规则 每日触发次数
     * key 有效期最多 10天
     * %s tenantCode ,filed 是日期
     */
    private String getRedisEveryDayTriggerFrequency() {
        return String.format(UcenterRedisKeyConstant.KeywordTenantEveryDayTriggerFrequency, BaseContextHandler.getTenant());
    }
    /**
     * 记录一条 关键字规则的 触发记录。
     * 并在redis中增加计数
     * 回复规则的触发次数不需要排序。 所有每个规则 每天的触发次数保存在 hash 中
     * @param keywordReplyId
     * @param patientId
     */
    @Override
    public void createPatientTriggerRecord(Long keywordReplyId, Long patientId) {

        keywordReplyTriggerRecordMapper.insert(KeywordReplyTriggerRecord.builder()
                .keywordReplyId(keywordReplyId)
                .patientId(patientId)
                .triggerDate(LocalDate.now())
                .triggerDateTime(LocalDateTime.now())
                .build());
        // 项目的历史总 回复规则 总的触发次数 +1
        String tenantAllTriggerRecordFrequencyKey = getRedisTenantAllTriggerRecordFrequencyKey();
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(tenantAllTriggerRecordFrequencyKey);
        boundHashOps.increment(BaseContextHandler.getTenant(), 1);

        // 今日项目 所有规则 回复规则的次数 +1
        String triggerFrequency = getRedisEveryDayTriggerFrequency();
        BoundHashOperations<String, Object, Object> boundHashOperations = redisTemplate.boundHashOps(triggerFrequency);
        boundHashOperations.increment(LocalDate.now().toString(), 1);

        //  此规则 今日 回复次数 +1
        String everyDayTriggerFrequency = getRedisEveryReplyEveryDayTriggerFrequency(keywordReplyId);
        BoundHashOperations<String, Object, Object> objectBoundHashOperations = redisTemplate.boundHashOps(everyDayTriggerFrequency);
        objectBoundHashOperations.increment(LocalDate.now().toString(), 1);

        // 此规则生命周期中 的回复次数 +1
        BoundHashOperations<String, Object, Object> boundHashOperations1 = redisTemplate.boundHashOps(UcenterRedisKeyConstant.KeywordTenantKeyWordReplyTriggerFrequency);
        boundHashOperations1.increment(keywordReplyId.toString(), 1);

    }

    @Override
    public void setTodayAndYesterdayTriggerTimes(KeywordReplyPageList replyPageList, Long replyId) {

        String everyDayTriggerFrequency = getRedisEveryReplyEveryDayTriggerFrequency(replyId);
        BoundHashOperations<String, Object, Object> objectBoundHashOperations = redisTemplate.boundHashOps(everyDayTriggerFrequency);
        Object o = objectBoundHashOperations.get(LocalDate.now().toString());
        if (o == null) {
            replyPageList.setTodayTrigger(0);
        } else {
            int anInt = Integer.parseInt(o.toString());
            replyPageList.setTodayTrigger(anInt);
        }
        Object o1 = objectBoundHashOperations.get(LocalDate.now().plusDays(-1).toString());
        if (o1 == null) {
            replyPageList.setYesterdayTrigger(0);
        } else {
            int anInt = Integer.parseInt(o1.toString());
            replyPageList.setYesterdayTrigger(anInt);
        }
    }
}
