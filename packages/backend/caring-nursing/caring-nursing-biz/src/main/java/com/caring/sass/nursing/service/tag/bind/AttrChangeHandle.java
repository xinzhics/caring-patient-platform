package com.caring.sass.nursing.service.tag.bind;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.constant.TagBindRedisKey;
import com.caring.sass.nursing.dao.drugs.PatientDrugsMapper;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.tag.TagMapper;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.entity.tag.Tag;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.tag.AttrService;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * 项目新增标签后， 处理项目的患者标签
 *
 */
@Slf4j
@Component
public class AttrChangeHandle extends AttrHandle {

    @Autowired
    AttrService attrService;

    @Autowired
    TagMapper tagMapper;

    @Autowired
    FormResultMapper formResultMapper;

    @Autowired
    PatientDrugsMapper patientDrugsMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    /**
     * 可能会出现 次任务在执行过程中，有患者跟这个标签绑定。
     * @param attrBindChangeDto
     */
    @Override
    public void handle(AttrBindChangeDto attrBindChangeDto) {

        String tenantCode = attrBindChangeDto.getTenantCode();
        Long tagId = attrBindChangeDto.getTagId();

        if (StrUtil.isEmpty(tenantCode)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        if (Objects.isNull(tagId)) {
            return;
        }
        Tag tag = tagMapper.selectById(tagId);
        if (Objects.isNull(tag)) {
            return;
        }

        // 获取这个标签的条件
        List<Attr> attrList = attrService.list(Wraps.<Attr>lbQ().eq(Attr::getTagId, tagId));
        if (CollUtil.isEmpty(attrList)) {
            return;
        }
        // 清除这个标签之前绑定的 用户

        // 基本信息 和 疾病信息 中的患者只统计一次
        boolean baseInfoOrHealthInfo = false;
        for (Attr attr : attrList) {
            // 先将 这个标签可能绑定的患者 id 查询出来 ，并放入到 redis 的一个set集合中去。
            String attrSource = attr.getAttrSource();
            if (!baseInfoOrHealthInfo &&
                    (StrUtil.isEmpty(attrSource) || Attr.BASE_INFO.equals(attrSource) || Attr.HEALTH_RECORD.equals(attrSource))) {
                baseInfoOrHealthInfo = true;
                // 统计 改项目下的 添加基本信息的患者人员 ID
                queryFormResultPatient(tenantCode, tagId);
            }
            if (Attr.MONITORING_PLAN.equals(attrSource)) {
                monitoringPlanFormResult(attr.getPlanId(), tenantCode, tagId);
            }
            if (Attr.DRUG.equals(attrSource)) {
                drugsPatient(attr.getAttrId(), tenantCode, tagId);
            }
        }
        tag.setTagAttr(attrList);

        // 已经将 项目中 所有跟这个标签属性会发送关系的 患者id 都已经找到。
        // 考虑 jvm 线程问题。
        // 此处 往 公共线程池中 加入最多三个任务 进行执行
        SaasGlobalThreadPool.execute(() -> threadExecuteBindTag(tenantCode, tag, tagId));
//        SaasGlobalThreadPool.execute(() -> threadExecuteBindTag(tenantCode, tag, tagId));
//        SaasGlobalThreadPool.execute(() -> threadExecuteBindTag(tenantCode, tag, tagId));

    }

    private String getRedisKey(String tenantCode, Long tagId) {
        return TagBindRedisKey.getTagBindKey(tenantCode, tagId);
    }

    private String getPatientOverRedisKey(String tenantCode, Long tagId) {
        return "tenant:" + tenantCode + "tag:" + tagId + "no_patient_wait_bind";
    }


    /**
     * 标签绑定任务已经完成， 清除redis中的key
     * @param tenantCode
     * @param tagId
     */
    public void finishBindCleanRedis(String tenantCode, Long tagId) {
        UpdateWrapper<Tag> tagUpdateWrapper = new UpdateWrapper<>();
        tagUpdateWrapper.eq("id", tagId);
        tagUpdateWrapper.eq("handle_attr_bind", 2);
        tagUpdateWrapper.set("handle_attr_bind", 1);
        int update = tagMapper.update(new Tag(), tagUpdateWrapper);
        if (update > 0) {
            List<String> keys = new ArrayList<>();
            keys.add(getRedisKey(tenantCode, tagId));
            keys.add(getPatientOverRedisKey(tenantCode, tagId));
            redisTemplate.delete(keys);
        }

    }

    /**
     * 从 redis 中拉取租户下的标签下 可能需要绑定的患者
     * @param tenantCode
     * @param tagId
     * @return
     */
    public String getPatientIdForBindTag(String tenantCode, Long tagId) {

        String patientId = redisTemplate.opsForSet().pop(getRedisKey(tenantCode, tagId));
        if (StrUtil.isEmpty(patientId)) {
            // 说明本线程没有获取到新的患者
            Long increment = redisTemplate.opsForValue().increment(getPatientOverRedisKey(tenantCode, tagId), 1);
            log.info("patient bind tag no patient wait bind {} ", increment);
            if (increment != null && increment.equals(1L)) {
                // 说明有三个线程都认为set中没有患者要等待绑定标签了。
                // 修改 标签 为 已经执行完毕。
                // 移除 redis中的 getRedisKey 和 getPatientOverRedisKey
                finishBindCleanRedis(tenantCode, tagId);
            }
            return null;
        } else {
            return patientId;
        }

    }

    /**
     * 检查 这个标签 和 从redis 中拉取到的患者id 是否可以绑定
     * @param tenantCode
     * @param tag
     * @param tagId
     */
    public void threadExecuteBindTag(String tenantCode, Tag tag, Long tagId) {
        String patientId = null;
        BaseContextHandler.setTenant(tenantCode);
        try {
            patientId = getPatientIdForBindTag(tenantCode, tagId);
            log.info("threadExecuteBindTag getPatientIdForBindTag {}", patientId );
        } catch (Exception e){
        }
        if (StringUtils.isNotEmptyString(patientId)) {
            try {
                // 一个任务执行完毕。
                long patientIdLong = Long.parseLong(patientId);
                TagComponent component = SpringUtils.getBean(TagComponent.class);
                bindPatientTag(tag, component, patientIdLong);
            } finally {
                // 此线程 尝试再去拉一个任务过来执行。 当没有任务时，此线程退出
                threadExecuteBindTag(tenantCode, tag, tagId);
            }
        } else {

        }
    }


    /**
     * 患者的用药
     * @param drugsId
     * @param tenantCode
     * @param tagId
     */
    private void drugsPatient(String drugsId, String tenantCode, Long tagId) {

        if (StringUtils.isEmpty(drugsId)) {
            return;
        }
        long drugsLong = Long.parseLong(drugsId);
        int current = 1;
        IPage<PatientDrugs> page = new Page<>();
        page.setSize(200);
        LbqWrapper<PatientDrugs> select = Wraps.<PatientDrugs>lbQ()
                .eq(PatientDrugs::getDrugsId, drugsLong)
                .groupBy(PatientDrugs::getPatientId)
                .select(SuperEntity::getId, PatientDrugs::getPatientId);
        while (true) {
            page.setCurrent(current);
            IPage<PatientDrugs> selectPage = patientDrugsMapper.selectPage(page, select);
            current++;
            List<PatientDrugs> records = selectPage.getRecords();
            addDrugsPatientIdsToRedisSet(records, tenantCode, tagId);
            // 退出循环
            if (CollUtil.isEmpty(records)) {
                break;
            }
            if (current > selectPage.getPages()) {
                break;
            }
        }

    }

    /**
     * 查询项目下的 监测计划对应的表单结果 中的患者iD
     * @param planId 监测计划ID
     * @param tenantCode
     * @param tagId
     */
    private void monitoringPlanFormResult(Long planId, String tenantCode, Long tagId) {

        if (planId == null) {
            return;
        }
        int current = 1;
        IPage<FormResult> page = new Page<>();
        page.setSize(200);
        LbqWrapper<FormResult> select = Wraps.<FormResult>lbQ()
                .eq(FormResult::getBusinessId, planId.toString())
                .groupBy(FormResult::getUserId)
                .select(SuperEntity::getId, FormResult::getUserId);
        while (true) {
            page.setCurrent(current);
            IPage<FormResult> selectPage = formResultMapper.selectPage(page, select);
            current++;
            List<FormResult> records = selectPage.getRecords();
            addPatientIdsToRedisSet(records, tenantCode, tagId);
            // 退出循环
            if (CollUtil.isEmpty(records)) {
                break;
            }
            if (current > selectPage.getPages()) {
                break;
            }
        }

    }

    /**
     * 添加用药的患者id 到redis中的
     * @param patientDrugs
     * @param tenantCode
     * @param tagId
     */
    private void addDrugsPatientIdsToRedisSet(List<PatientDrugs> patientDrugs, String tenantCode, Long tagId) {
        if (CollUtil.isNotEmpty(patientDrugs)) {
            List<String> patientIds = patientDrugs.stream()
                    .filter(item -> item.getPatientId() != null)
                    .map(item -> item.getPatientId().toString())
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(patientIds)) {
                String[] patientIdsStr = patientIds.toArray(new String[0]);
                redisTemplate.boundSetOps(getRedisKey(tenantCode, tagId)).add(patientIdsStr);
            }
        }

    }

    /**
     * 添加患者id 到redis 的set 中去。
     * @param records
     * @param tenantCode
     * @param tagId
     */
    private void addPatientIdsToRedisSet(List<FormResult> records, String tenantCode, Long tagId) {
        try {
            if (CollUtil.isNotEmpty(records)) {
                List<String> patientIds = records.stream()
                        .filter(item -> item.getUserId() != null)
                        .map(item -> item.getUserId().toString())
                        .collect(Collectors.toList());
                if (CollUtil.isNotEmpty(patientIds)) {
                    String[] patientIdsStr = patientIds.toArray(new String[0]);
                    redisTemplate.boundSetOps(getRedisKey(tenantCode, tagId)).add(patientIdsStr);
                }
            }
        } catch (Exception e) {
            log.info("attr change, add patient id to redis error {}", e.getMessage());
        }

    }

    /**
     * 查询项目下的 基本信息 的患者ID。 并上传到 redis中去。
     * @param tenantCode
     * @param tagId
     */
    private void queryFormResultPatient(String tenantCode, Long tagId) {

        int current = 1;
        IPage<FormResult> page = new Page<>();
        page.setSize(200);
        LbqWrapper<FormResult> select = Wraps.<FormResult>lbQ()
                .eq(FormResult::getCategory, FormEnum.BASE_INFO)
                .select(SuperEntity::getId, FormResult::getUserId);
        while (true) {
            page.setCurrent(current);
            IPage<FormResult> selectPage = formResultMapper.selectPage(page, select);
            current++;
            List<FormResult> records = selectPage.getRecords();
            addPatientIdsToRedisSet(records, tenantCode, tagId);
            // 退出循环
            if (CollUtil.isEmpty(records)) {
                break;
            }
            if (current > selectPage.getPages()) {
                break;
            }
        }

    }


}
