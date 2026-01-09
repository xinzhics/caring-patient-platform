package com.caring.sass.nursing.service.task.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.constant.AttrBindEvent;
import com.caring.sass.nursing.constant.TagBindRedisKey;
import com.caring.sass.nursing.dao.drugs.PatientDrugsHistoryLogMapper;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsHistoryLog;
import com.caring.sass.nursing.enumeration.DrugsOperateType;
import com.caring.sass.nursing.service.drugs.DrugsConditionMonitoringService;
import com.caring.sass.nursing.service.drugs.PatientDrugsService;
import com.caring.sass.nursing.service.task.SuperTask;
import com.caring.sass.tenant.entity.Tenant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 停药通知
 *
 * @author leizhi
 */
@Service
public class StopPatientDrugsTask extends SuperTask {

    private final PatientDrugsService patientDrugsService;

    @Autowired
    private DrugsConditionMonitoringService drugsConditionMonitoringService;

    private final PatientDrugsHistoryLogMapper patientDrugsHistoryLogMapper;

    RedisTemplate<String, String> redisTemplate;

    public StopPatientDrugsTask(PatientDrugsService patientDrugsService,
                                PatientDrugsHistoryLogMapper patientDrugsHistoryLogMapper,
                                RedisTemplate<String, String> redisTemplate) {
        this.patientDrugsService = patientDrugsService;
        this.patientDrugsHistoryLogMapper = patientDrugsHistoryLogMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 批量修改
     * 记录停止用药的信息
     * 停止endtime已经过期， 正在使用中的药
     */
    public void execute() {
        // 优化此处。 只处理 项目状态正常。且微信公众号验证通过的患者
        List<Tenant> tenantList = getAllNormalTenant();
        if (CollectionUtils.isEmpty(tenantList)) {
            return;
        }
        for (Tenant tenant : tenantList) {
            String tenantCode = tenant.getCode();
            BaseContextHandler.setTenant(tenantCode);
            LbqWrapper<PatientDrugs> wrapper = Wraps.<PatientDrugs>lbQ()
                    .eq(PatientDrugs::getCycle, 1)
                    .eq(PatientDrugs::getStatus, 0)
                    .lt(PatientDrugs::getEndTime, LocalDate.now())
                    .select(PatientDrugs::getPatientId, PatientDrugs::getDrugsId, PatientDrugs::getMedicineName, SuperEntity::getId);
            List<PatientDrugs> patientDrugs = patientDrugsService.list(wrapper);
            if (CollUtil.isNotEmpty(patientDrugs)) {
                // 记录这些停止用药
                List<PatientDrugsHistoryLog> logs = new ArrayList<>(patientDrugs.size());
                Map<Long, Long> patientDrugMap = new HashMap<>(patientDrugs.size());
                for (PatientDrugs patientDrug : patientDrugs) {
                    logs.add(PatientDrugsHistoryLog.builder()
                            .patientId(patientDrug.getPatientId())
                            .drugsId(patientDrug.getDrugsId())
                            .medicineName(patientDrug.getMedicineName())
                            .historyDate(LocalDate.now())
                            .operateType(DrugsOperateType.STOP.operateType)
                            .operateTypeSort(DrugsOperateType.STOP.operateTypeSort)
                            .build());
                    patientDrugMap.put(patientDrug.getPatientId(), patientDrug.getDrugsId());
                }
                patientDrugsHistoryLogMapper.insertBatchSomeColumn(logs);
                List<String> redisValues = new ArrayList<>(patientDrugMap.size());
                for (Map.Entry<Long, Long> longLongEntry : patientDrugMap.entrySet()) {
                    Long patientId = longLongEntry.getKey();
                    Long drugId = longLongEntry.getValue();
                    AttrBindChangeDto changeDto = new AttrBindChangeDto();
                    changeDto.setDrugsId(drugId);
                    changeDto.setTenantCode(tenantCode);
                    changeDto.setPatientId(patientId);
                    changeDto.setEvent(AttrBindEvent.STOP_DRUG);
                    String string = JSON.toJSONString(changeDto);
                    redisValues.add(string);
                }
                redisTemplate.opsForList().leftPushAll(TagBindRedisKey.TENANT_ATTR_BIND, redisValues);

                UpdateWrapper<PatientDrugs> lbqWrapper = new UpdateWrapper<>();
                lbqWrapper.set("status", 2);
                lbqWrapper.eq("cycle", 1);
                lbqWrapper.eq("status", 0);
                lbqWrapper.lt("end_time", LocalDate.now());
                patientDrugsService.update(lbqWrapper);
                //删除用药预警
                SaasGlobalThreadPool.execute(() -> drugsConditionMonitoringService.synDeleteDrugsResultInformation(patientDrugs, BaseContextHandler.getTenant()));
            }
        }

    }
}
