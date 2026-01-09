package com.caring.sass.nursing.service.follow.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.ChannelContentApi;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.follow.FollowTaskMapper;
import com.caring.sass.nursing.dao.plan.PlanDetailMapper;
import com.caring.sass.nursing.dao.plan.PlanDetailTimeMapper;
import com.caring.sass.nursing.dao.plan.PlanTagMapper;
import com.caring.sass.nursing.dao.tag.AssociationMapper;
import com.caring.sass.nursing.dto.follow.*;
import com.caring.sass.nursing.dto.plan.PlanDetailDTO;
import com.caring.sass.nursing.dto.plan.SubscribeDTO;
import com.caring.sass.nursing.entity.follow.FollowContentShow;
import com.caring.sass.nursing.entity.follow.FollowContentTimeFrame;
import com.caring.sass.nursing.entity.follow.FollowTask;
import com.caring.sass.nursing.entity.follow.FollowTaskContent;
import com.caring.sass.nursing.entity.plan.*;
import com.caring.sass.nursing.entity.tag.Association;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.follow.FollowTaskContentService;
import com.caring.sass.nursing.service.follow.FollowTaskService;
import com.caring.sass.nursing.service.follow.FunctionConfigurationService;
import com.caring.sass.nursing.service.plan.PatientNursingPlanService;
import com.caring.sass.nursing.service.plan.PlanService;
import com.caring.sass.nursing.service.plan.ReminderLogService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.service.task.PushAlgorithm;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 随访任务
 * </p>
 *
 * @author 杨帅
 * @date 2023-01-04
 */
@Slf4j
@Service
public class FollowTaskServiceImpl extends SuperServiceImpl<FollowTaskMapper, FollowTask> implements FollowTaskService {

    @Autowired
    TenantApi tenantApi;

    @Autowired
    PatientApi patientApi;

    @Autowired
    FollowTaskContentService followTaskContentService;

    PlanService planService;

    @Autowired
    ChannelContentApi channelContentApi;

    @Autowired
    PlanTagMapper planTagMapper;

    @Autowired
    ReminderLogService reminderLogService;

    @Autowired
    NursingStaffApi nursingStaffApi;

    @Autowired
    FunctionConfigurationService functionConfigurationService;

    @Autowired
    AssociationMapper associationMapper;

    PatientNursingPlanService patientNursingPlanService;

    @Autowired
    PlanDetailMapper planDetailMapper;

    @Autowired
    PlanDetailTimeMapper planDetailTimeMapper;

    public PlanService getPlanService() {
        if (planService == null) {
            planService = SpringUtils.getBean(PlanService.class);
        }
        return planService;
    }

    public PatientNursingPlanService getPatientNursingPlanService() {
        if (patientNursingPlanService == null) {
            patientNursingPlanService = SpringUtils.getBean(PatientNursingPlanService.class);
        }
        return patientNursingPlanService;
    }

    @Override
    public void initAllTenantFollow() {
        R<List<Object>> allTenantCode = tenantApi.getAllTenantCode();
        List<String> tenantCode;
        if (allTenantCode.getIsSuccess() != null && allTenantCode.getIsSuccess() && allTenantCode.getData() != null) {
            tenantCode = allTenantCode.getData().stream().map(Object::toString).collect(Collectors.toList());
        } else {
            throw new BizException("没有查询到项目code");
        }
        for (String s : tenantCode) {
            BaseContextHandler.setTenant(s);
            initOneTenantFollow();
        }
    }

    @Override
    public FollowTask initOneTenantFollow() {
        clearFollow();
        FollowTask followTask = new FollowTask();
        followTask.setName("随访");
        followTask.setShowOutline(0);
        followTask.setUseDefault(1);
        baseMapper.insert(followTask);
        followTaskContentService.initAllTenantPlan();
        return followTask;
    }


    public void clearFollow() {
        baseMapper.delete(Wraps.lbQ());
        followTaskContentService.remove(Wraps.lbQ());
    }


    @Transactional
    @Override
    public void updateById(FollowTask followTask, List<FollowTaskContent> contentList) {

        baseMapper.updateById(followTask);
        followTaskContentService.updateBatchById(contentList);
    }


    /**
     * 获取随访任务和随访内容
     * @return
     */
    @Override
    public FollowTaskUpdateDTO getFollowTask() {

        FollowTask followTask = baseMapper.selectOne(Wraps.<FollowTask>lbQ().last(" limit 0,1 "));
        if (Objects.isNull(followTask)) {
            followTask = initOneTenantFollow();
        }
        List<FollowTaskContent> taskContents = followTaskContentService.list(Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getPlanStatus, 1)
                .orderByDesc(FollowTaskContent::getContentSort));
        FollowTaskUpdateDTO followTaskUpdateDTO = new FollowTaskUpdateDTO();
        BeanUtils.copyProperties(followTask, followTaskUpdateDTO);
        followTaskUpdateDTO.setId(followTask.getId());
        List<Long> removePlanButOpenId = functionConfigurationService.getRemovePlanButOpenId();
        List<FollowTaskContent> resultTaskContent = new ArrayList<>();
        if (CollUtil.isNotEmpty(taskContents)) {
            // 把 指标监测 自定义随访 状态关闭，但是护理计划状态依然是开启的 随访 过滤掉。
            if (CollUtil.isNotEmpty(removePlanButOpenId)) {
                taskContents.forEach(item -> {
                    if (Objects.isNull(item.getPlanId())) {
                        resultTaskContent.add(item);
                    } else if (!removePlanButOpenId.contains(item.getPlanId())) {
                        resultTaskContent.add(item);
                    }
                });
            } else {
                resultTaskContent.addAll(taskContents);
            }
        }
        for (FollowTaskContent taskContent : resultTaskContent) {
            if (PlanEnum.LEARN_PLAN.getCode().equals(taskContent.getPlanType())) {
                LbqWrapper<Plan> planLbqWrapper = Wraps.<Plan>lbQ()
                        .eq(Plan::getPlanType, PlanEnum.LEARN_PLAN.getCode())
                        .eq(Plan::getStatus, 1)
                        .eq(Plan::getLearnPlanRole, UserType.UCENTER_PATIENT);
                int count = getPlanService().count(planLbqWrapper);
                taskContent.setPlanNumber(count);
            }
        }
        followTaskUpdateDTO.setContentList(resultTaskContent);
        return followTaskUpdateDTO;
    }

    /**
     * 更新随访内容中 子项的 显示和隐藏
     * @param followTaskContentUpdateDTO
     */
    @Override
    public void updateFollowContentShowStatus(FollowTaskContentUpdateDTO followTaskContentUpdateDTO) {
        FollowTaskContent followTaskContent = new FollowTaskContent();
        followTaskContent.setId(followTaskContentUpdateDTO.getId());
        followTaskContent.setShowContent(followTaskContentUpdateDTO.getShowContent());
        followTaskContentService.updateById(followTaskContent);

    }

    /**
     * web端超管查看的随访简介
     * @return
     */
    @Override
    public FollowBriefIntroductionDTO findFollowBriefIntroduction() {

        FollowBriefIntroductionDTO followBriefIntroductionDTO = buildFollowBriefIntroduction(null);
        R<Integer> patientNumber = patientApi.countTenantPatientNumber();
        Integer patientNumberData = patientNumber.getData();
        followBriefIntroductionDTO.setManagePatientNumber(patientNumberData);
        return followBriefIntroductionDTO;
    }

    /**
     * 查询随访统计页面的 数据范围 和 栏目列表
     * @return
     */
    @Override
    public FollowCountDetailDTO queryFollowCountDetail() {
        FollowCountDetailDTO followCountDetailDTO = new FollowCountDetailDTO();
        String tenant = BaseContextHandler.getTenant();
        R<Tenant> tenantR = tenantApi.getByCode(tenant);
        if (tenantR.getIsSuccess()) {
            Tenant data = tenantR.getData();
            followCountDetailDTO.setStartTime(data.getCreateTime());
            followCountDetailDTO.setEndTime(LocalDateTime.now());
        }
        LbqWrapper<FollowTaskContent> lbqWrapper = Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getPlanStatus, 1)
                .eq(FollowTaskContent::getShowContent, FollowContentShow.FOLLOW_CONTENT_SHOW)
                .orderByDesc(FollowTaskContent::getContentSort);
        List<FollowTaskContent> taskContents = followTaskContentService.list(lbqWrapper);
        followCountDetailDTO.setFollowTaskContentList(taskContents);
        return followCountDetailDTO;
    }

    /**
     * 医生 对随访计划 推送打卡的统计
     * @param followTaskContentId
     * @return
     */
    @Override
    public FollowCountOtherPlanDTO doctorQueryCountOtherPush(Long followTaskContentId) {

        FollowTaskContent taskContent = followTaskContentService.getById(followTaskContentId);
        Long planId = taskContent.getPlanId();
        Long doctorId = BaseContextHandler.getUserId();
        Plan planAndDetails = getPlanService().getPlanAndDetails(planId);

        List<PlanDetailDTO> planDetailList = planAndDetails.getPlanDetailList();
        FollowCountOtherPlanDTO followCountOtherPlanDTO = new FollowCountOtherPlanDTO();
        // 血压血糖的统计方式不同于下面的
        if (CollUtil.isNotEmpty(planDetailList)) {
            PlanDetailDTO detailDTO = planDetailList.get(0);
            Integer pushType = detailDTO.getPushType();
            List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
            if (CollUtil.isNotEmpty(planDetailTimes)) {
                followCountOtherPlanDTO.setPlanId(planId);
                followCountOtherPlanDTO.setPlanName(planAndDetails.getName());
                followCountOtherPlanDTO.setPlanType(planAndDetails.getPlanType());
                if (PlanEnum.BLOOD_PRESSURE.getCode().equals(planAndDetails.getPlanType()) || PlanEnum.BLOOD_SUGAR.getCode().equals(planAndDetails.getPlanType())) {
                    List<String> reminderLogWorkIdList = new ArrayList<>();
                    for (PlanDetailTime detailTime : planDetailTimes) {
                        String reminderLogWorkId = createReminderLogWorkId(detailTime.getId());
                        reminderLogWorkIdList.add(reminderLogWorkId);
                    }
                    if (CollUtil.isNotEmpty(reminderLogWorkIdList)) {
                        // 统计打开人数，推送人数， 完成人数，
                        reminderLogService.doctorStatisticsData(doctorId, reminderLogWorkIdList, followCountOtherPlanDTO);
                    }
                    return followCountOtherPlanDTO;
                } else {
                    if (pushType != null && pushType == 2) {
                        followCountOtherPlanDTO.setUrlLink(detailDTO.getContent());
                    }
                    PlanDetailTime detailTime = planDetailTimes.get(0);
                    String reminderLogWorkId = createReminderLogWorkId(detailTime.getId());
                    // 统计打开人数，推送人数， 完成人数，
                    reminderLogService.doctorStatisticsData(doctorId, reminderLogWorkId, followCountOtherPlanDTO);
                    return followCountOtherPlanDTO;
                }
            }
        }
        return followCountOtherPlanDTO;
    }

    /**
     * 目前限医助使用
     * 查询 其他计划推送的统计的详细情况
     *
     * @return
     * @param followTaskContentId
     */
    public FollowCountOtherPlanDTO queryCountOtherPush(Long followTaskContentId) {

        FollowTaskContent taskContent = followTaskContentService.getById(followTaskContentId);
        Long planId = taskContent.getPlanId();
        Plan planAndDetails = getPlanService().getPlanAndDetails(planId);
        R<List<Long>> nursingOrgIds = nursingStaffApi.getNursingOrgIds(BaseContextHandler.getUserId());
        List<Long> orgIds = new ArrayList<>();
        if (nursingOrgIds.getIsSuccess()) {
            orgIds = nursingOrgIds.getData();
        }
        List<PlanDetailDTO> planDetailList = planAndDetails.getPlanDetailList();
        FollowCountOtherPlanDTO followCountOtherPlanDTO = new FollowCountOtherPlanDTO();
        // 血压血糖的统计方式不同于下面的
        if (CollUtil.isNotEmpty(planDetailList)) {
            PlanDetailDTO detailDTO = planDetailList.get(0);
            Integer pushType = detailDTO.getPushType();
            List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
            if (CollUtil.isNotEmpty(planDetailTimes)) {
                followCountOtherPlanDTO.setPlanId(planId);
                followCountOtherPlanDTO.setPlanName(planAndDetails.getName());
                followCountOtherPlanDTO.setPlanType(planAndDetails.getPlanType());
                if (PlanEnum.BLOOD_PRESSURE.getCode().equals(planAndDetails.getPlanType()) || PlanEnum.BLOOD_SUGAR.getCode().equals(planAndDetails.getPlanType())) {
                    List<String> reminderLogWorkIdList = new ArrayList<>();
                    for (PlanDetailTime detailTime : planDetailTimes) {
                        String reminderLogWorkId = createReminderLogWorkId(detailTime.getId());
                        reminderLogWorkIdList.add(reminderLogWorkId);
                    }
                    if (CollUtil.isNotEmpty(reminderLogWorkIdList)) {
                        // 统计打开人数，推送人数， 完成人数，
                        reminderLogService.statisticsData(orgIds, reminderLogWorkIdList, followCountOtherPlanDTO);
                    }
                    return followCountOtherPlanDTO;
                } else {
                    if (pushType != null && pushType == 2) {
                        followCountOtherPlanDTO.setUrlLink(detailDTO.getContent());
                    }
                    PlanDetailTime detailTime = planDetailTimes.get(0);
                    String reminderLogWorkId = createReminderLogWorkId(detailTime.getId());
                    // 统计打开人数，推送人数， 完成人数，
                    reminderLogService.statisticsData(orgIds, reminderLogWorkId, followCountOtherPlanDTO);
                    return followCountOtherPlanDTO;
                }
            }
        }
        return followCountOtherPlanDTO;
    }

    /**
     * 医生 查询 学习计划随访统计
     * @return
     */
    public List<FollowCountLearnPlanDto> doctorQueryCountLearnPlanPush() {

        List<Plan> planAndDetailsByPlanType = getPlanService().getPlanAndDetailsByPlanType(PlanEnum.LEARN_PLAN.getCode(), UserType.UCENTER_PATIENT, 1);
        List<FollowCountLearnPlanDto> dtos = new ArrayList<>();
        Long doctorId = BaseContextHandler.getUserId();

        FollowCountLearnPlanDto planDto;
        for (Plan plan : planAndDetailsByPlanType) {
            List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
            if (CollUtil.isNotEmpty(planDetailList)) {
                PlanDetailDTO detailDTO = planDetailList.get(0);
                List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                if (CollUtil.isNotEmpty(planDetailTimes)) {
                    planDto = new FollowCountLearnPlanDto();
                    planDto.setPlanName(plan.getName());
                    List<CmsPushReadDetail> details = new ArrayList<>();
                    Map<String, CmsPushReadDetail> map = new HashMap<>();
                    for (PlanDetailTime detailTime : planDetailTimes) {
                        String reminderLogWorkId = createReminderLogWorkId(detailTime.getId());
                        CmsPushReadDetail cmsPushReadDetail = new CmsPushReadDetail();
                        if (detailTime.getSendUrl() != null && detailTime.getSendUrl() == 1) {
                            cmsPushReadDetail.setCmsLink(detailTime.getTemplateMessageId());
                            cmsPushReadDetail.setCmsTitle(detailTime.getCmsTitle());
                        } else {
                            String templateMessageId = detailTime.getTemplateMessageId();
                            if (StrUtil.isEmpty(templateMessageId)) {
                                continue;
                            }
                            long cmsId = Long.parseLong(templateMessageId);
                            R<ChannelContent> channelContentR = channelContentApi.getTitle(cmsId);
                            if (channelContentR.getIsSuccess()) {
                                ChannelContent channelContent = channelContentR.getData();
                                if (Objects.nonNull(channelContent)) {
                                    cmsPushReadDetail.setCmsId(cmsId);
                                    cmsPushReadDetail.setCmsTitle(channelContent.getTitle());
                                } else {
                                    continue;
                                }
                            }
                        }
                        details.add(cmsPushReadDetail);
                        map.put(reminderLogWorkId, cmsPushReadDetail);
                    }
                    reminderLogService.doctorStatisticsData(doctorId, map);
                    planDto.setCmsPushReadDetails(details);
                    dtos.add(planDto);
                }
            }
        }
        return dtos;
    }

    /**
     * 统计学习计划下的文章 阅读情况
     * @return
     */
    @Override
    public List<FollowCountLearnPlanDto> queryCountLearnPlanPush() {

        List<Plan> planAndDetailsByPlanType = getPlanService().getPlanAndDetailsByPlanType(PlanEnum.LEARN_PLAN.getCode(), UserType.UCENTER_PATIENT, 1);
        List<FollowCountLearnPlanDto> dtos = new ArrayList<>();
        List<Long> orgIds = new ArrayList<>();
        R<List<Long>> nursingOrgIds = nursingStaffApi.getNursingOrgIds(BaseContextHandler.getUserId());
        if (nursingOrgIds.getIsSuccess()) {
            orgIds = nursingOrgIds.getData();
        }
        FollowCountLearnPlanDto planDto;
        for (Plan plan : planAndDetailsByPlanType) {
            List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
            if (CollUtil.isNotEmpty(planDetailList)) {
                PlanDetailDTO detailDTO = planDetailList.get(0);
                List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                if (CollUtil.isNotEmpty(planDetailTimes)) {
                    planDto = new FollowCountLearnPlanDto();
                    planDto.setPlanName(plan.getName());
                    List<CmsPushReadDetail> details = new ArrayList<>();
                    Map<String, CmsPushReadDetail> map = new HashMap<>();
                    for (PlanDetailTime detailTime : planDetailTimes) {
                        String reminderLogWorkId = createReminderLogWorkId(detailTime.getId());
                        CmsPushReadDetail cmsPushReadDetail = new CmsPushReadDetail();
                        if (detailTime.getSendUrl() != null && detailTime.getSendUrl() == 1) {
                            cmsPushReadDetail.setCmsLink(detailTime.getTemplateMessageId());
                            cmsPushReadDetail.setCmsTitle(detailTime.getCmsTitle());
                        } else {
                            String templateMessageId = detailTime.getTemplateMessageId();
                            if (StrUtil.isEmpty(templateMessageId)) {
                                continue;
                            }
                            long cmsId = Long.parseLong(templateMessageId);
                            R<ChannelContent> channelContentR = channelContentApi.getTitle(cmsId);
                            if (channelContentR.getIsSuccess()) {
                                ChannelContent channelContent = channelContentR.getData();
                                if (Objects.nonNull(channelContent)) {
                                    cmsPushReadDetail.setCmsId(cmsId);
                                    cmsPushReadDetail.setCmsTitle(channelContent.getTitle());
                                } else {
                                    continue;
                                }
                            }
                        }
                        details.add(cmsPushReadDetail);
                        map.put(reminderLogWorkId, cmsPushReadDetail);
                    }
                    reminderLogService.statisticsData(orgIds, map);
                    planDto.setCmsPushReadDetails(details);
                    dtos.add(planDto);
                }
            }
        }
        return dtos;
    }

    /**
     * 创建推送日志中workId
     * @param detailTimeId
     * @return
     */
    private String createReminderLogWorkId(Long detailTimeId) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(detailTimeId.toString());
        return JSONUtil.toJsonStr(jsonArray);
    }




    /**
     * 清除 标签关联的计划中 存在于 需要移除的计划id
     * @param tagPlanIds
     * @param removePlanButOpenIds
     */
    private void clearPLanId(List<Long> tagPlanIds, List<Long> removePlanButOpenIds) {
        if (CollUtil.isEmpty(tagPlanIds) || CollUtil.isEmpty(removePlanButOpenIds)) {
            return;
        }
        for (Long removePlanButOpenId : removePlanButOpenIds) {
            tagPlanIds.remove(removePlanButOpenId);
        }
    }

    /**
     * 查询 随访页面头部的 背景图设置，随访项目数量，持续时间
     * 当有标签时， 没有标签的护理计划 也算符合条件
     * @return
     */
    private FollowBriefIntroductionDTO buildFollowBriefIntroduction (List<Long> tagIdList) {
        FollowBriefIntroductionDTO followBriefIntroductionDTO = new FollowBriefIntroductionDTO();
        String tenant = BaseContextHandler.getTenant();
        R<Tenant> tenantR = tenantApi.getByCode(tenant);
        Tenant data = tenantR.getData();
        FollowTask followTask = baseMapper.selectOne(Wraps.<FollowTask>lbQ().last(" limit 0,1 "));
        if (Objects.nonNull(followTask)) {
            followBriefIntroductionDTO.setId(followTask.getId());
            followBriefIntroductionDTO.setName(followTask.getName());
            followBriefIntroductionDTO.setUrl(followTask.getUrl());
            followBriefIntroductionDTO.setUseDefault(followTask.getUseDefault());
            followBriefIntroductionDTO.setShowOutline(followTask.getShowOutline());
        }
        if (Objects.nonNull(data)) {
            followBriefIntroductionDTO.setCreateTime(data.getCreateTime());
            Duration duration = Duration.between(data.getCreateTime(), LocalDateTime.now());
            long days = duration.toDays();
            followBriefIntroductionDTO.setDays(days);
        }
        // 关联这些标签的计划的ID
        List<Long> hasTagPlanIds = new ArrayList<>();
        PlanService planService = getPlanService();
        if (CollUtil.isNotEmpty(tagIdList)) {
            hasTagPlanIds = getHasTagPlanIds(tagIdList);
        }
        List<Long> removePlanButOpenId = functionConfigurationService.getRemovePlanButOpenId();
        // 这里不把学习计划统计进来
        LbqWrapper<FollowTaskContent> wrapper = Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getPlanStatus, 1)
                .isNotNull(FollowTaskContent::getPlanId)
                .eq(FollowTaskContent::getShowContent, FollowContentShow.FOLLOW_CONTENT_SHOW);
        int count = 0;
        if (CollUtil.isNotEmpty(tagIdList)) {
            // 说明标签下没有护理计划
            // 会清除 hasTagPlanIds 中 是监测计划或者自定义随访的ID
            clearPLanId(hasTagPlanIds, removePlanButOpenId);
            if (CollUtil.isEmpty(hasTagPlanIds)) {
                followBriefIntroductionDTO.setFollowProjectNumber(0);
            } else {
                wrapper.in(FollowTaskContent::getPlanId, hasTagPlanIds);
                count = followTaskContentService.count(wrapper);
            }
        } else if (CollUtil.isNotEmpty(removePlanButOpenId)) {
            wrapper.notIn(FollowTaskContent::getPlanId, removePlanButOpenId);
            count = followTaskContentService.count(wrapper);
        } else {
            count = followTaskContentService.count(wrapper);
        }
        int tagLearnPlanCount = 0;
        // 查询学习计划是否显示，如果显示则统计有多少患者角色的学习计划
        int learnPlanCount = followTaskContentService.count(Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getShowContent, FollowContentShow.FOLLOW_CONTENT_SHOW)
                .eq(FollowTaskContent::getPlanStatus, 1)
                .eq(FollowTaskContent::getPlanType, PlanEnum.LEARN_PLAN.getCode()));
        if (learnPlanCount > 0) {
            // 统计患者的学习计划 学习计划要求打开。
            LbqWrapper<Plan> planLbqWrapper = Wraps.<Plan>lbQ()
                    .eq(Plan::getPlanType, PlanEnum.LEARN_PLAN.getCode())
                    .eq(Plan::getStatus, 1)
                    .eq(Plan::getLearnPlanRole, UserType.UCENTER_PATIENT);
            if (CollUtil.isNotEmpty(tagIdList)) {
                // 说明标签下可能有 学习计划
                if (CollUtil.isNotEmpty(hasTagPlanIds)) {
                    planLbqWrapper.in(SuperEntity::getId, hasTagPlanIds);
                    tagLearnPlanCount = planService.count(planLbqWrapper);
                    count += tagLearnPlanCount;
                }
            } else {
                tagLearnPlanCount = planService.count(planLbqWrapper);
                count += tagLearnPlanCount;
            }
        }
        followBriefIntroductionDTO.setFollowProjectNumber(count);

        // 查询并设置 随访列表
        LbqWrapper<FollowTaskContent> lbqWrapper = Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getPlanStatus, 1)
                .eq(FollowTaskContent::getShowContent, FollowContentShow.FOLLOW_CONTENT_SHOW)
                .orderByDesc(FollowTaskContent::getContentSort);
        // 查询所有显示的随访任务。 任务数量并不多
        List<FollowTaskContent> taskContents = followTaskContentService.list(lbqWrapper);
        List<FollowTaskContent> resultTaskContent = new ArrayList<>();
        if (CollUtil.isNotEmpty(taskContents)) {
            // 把 指标监测 自定义随访 状态关闭，但是护理计划状态依然是开启的 随访 过滤掉。
            if (CollUtil.isNotEmpty(removePlanButOpenId)) {
                taskContents.forEach(item -> {
                    if (Objects.isNull(item.getPlanId())) {
                        resultTaskContent.add(item);
                    } else if (!removePlanButOpenId.contains(item.getPlanId())) {
                        resultTaskContent.add(item);
                    }
                });
            } else {
                resultTaskContent.addAll(taskContents);
            }
        }
        if (CollUtil.isNotEmpty(resultTaskContent)) {
            if (CollUtil.isNotEmpty(tagIdList)) {
                // 查询护理计划和 标签的关联表，拿到标签关联的护理计划的id
                List<FollowTaskContent> resultList = new ArrayList<>();
                for (FollowTaskContent taskContent : resultTaskContent) {
                    if (taskContent.getPlanType() != null && PlanEnum.LEARN_PLAN.getCode().equals(taskContent.getPlanType())) {

                        // 查询一下是否有学习计划 并且学习计划是和标签关联的
                        if (tagLearnPlanCount > 0) {
                            resultList.add(taskContent);
                        }
                    } else {
                        // 判断 随访关联的护理计划是否绑定了标签。
                        if (hasTagPlanIds.contains(taskContent.getPlanId())) {
                            resultList.add(taskContent);
                        }
                    }
                }
                followBriefIntroductionDTO.setFollowTaskContentList(resultList);
            } else {
                followBriefIntroductionDTO.setFollowTaskContentList(resultTaskContent);
            }
        }
        return followBriefIntroductionDTO;
    }

    /**
     * app查看的随访的简介
     * @return
     */
    @Override
    public FollowBriefIntroductionDTO appFindFollowBriefIntroduction(List<Long> tagIdList) {
        FollowBriefIntroductionDTO followBriefIntroductionDTO = buildFollowBriefIntroduction(tagIdList);
        Long userId = BaseContextHandler.getUserId();
        R<Integer> patientNumber = patientApi.countNursingOrgPatientNumber(userId, tagIdList);
        if (patientNumber.getIsSuccess()) {
            Integer patientNumberData = patientNumber.getData();
            followBriefIntroductionDTO.setManagePatientNumber(patientNumberData);
        }
        return followBriefIntroductionDTO;
    }

    /**
     * 根据功能配置，计划状态，计划的触发对象
     * 返回患者可见 计划集合 (其中可能有患者没有订阅的)
     * @return
     */
    @Override
    public List<Plan> getPatientSeePlan(Long patientId) {

        PlanService planService = getPlanService();

        // 查询系统 所有开启的护理计划
        List<Plan> planList = planService.list(Wraps.<Plan>lbQ()
                .select(SuperEntity::getId, Plan::getExecute, Plan::getEffectiveTime, Plan::getName, Plan::getPlanType, Plan::getLearnPlanRole, Plan::getFollowUpPlanType)
                .eq(Plan::getStatus, 1));
        // 系统打开的护理计划， 需要过滤掉 功能配置 开关是关闭的。
        if (CollUtil.isEmpty(planList)) {
            return new ArrayList<>();
        }

        // 查询的是，功能配置中指标监测，自定义随访关闭后，护理计划仍然是打开的计划ID
        List<Long> removePlanButOpenId = functionConfigurationService.getRemovePlanButOpenId();
        if (CollUtil.isNotEmpty(removePlanButOpenId)) {
            // 移除掉 自定义随访 ，指标监测中还可以的计划
            planList = planList.stream().filter(item -> !removePlanButOpenId.contains(item.getId())).collect(Collectors.toList());
        }
        Integer functionStatus = functionConfigurationService.getFunctionStatus(BaseContextHandler.getTenant(), PlanFunctionTypeEnum.LEARNING_PLAN);
        if (functionStatus == 0) {
            // 移除掉 学习计划中还开启的 计划
            planList = planList.stream().filter(item -> !PlanEnum.LEARN_PLAN.getCode().equals(item.getPlanType())).collect(Collectors.toList());
        }
        // 移除掉 其中可能存在的医生的 学习计划
        planList = planList.stream().filter(item -> !UserType.UCENTER_DOCTOR.equals(item.getLearnPlanRole())).collect(Collectors.toList());
        // 过滤掉用药提醒
        planList = planList.stream().filter(item -> !PlanEnum.MEDICATION_REMIND.getCode().equals(item.getPlanType())).collect(Collectors.toList());

        // 查询这些护理计划的的触发对象。
        if(CollUtil.isEmpty(planList)) {
            return new ArrayList<>();
        }
        List<Long> planIds = planList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        List<PlanTag> planTags = planTagMapper.selectList(Wraps.<PlanTag>lbQ().in(PlanTag::getNursingPlanId, planIds).select(SuperEntity::getId, PlanTag::getTagId, PlanTag::getNursingPlanId));

        if (CollUtil.isEmpty(planTags)) {
            return planList;
        }
        Map<Long, Long> planTagMaps = planTags.stream().collect(Collectors.toMap(PlanTag::getNursingPlanId, PlanTag::getTagId));
        // 过滤出来触发对象是全部，或者触发对象和患者一样的 计划

        // 查询患者的标签
        List<Association> associations = associationMapper.selectList(Wraps.<Association>lbQ().select(SuperEntity::getId, Association::getTagId)
                .eq(Association::getAssociationId, patientId.toString()));
        Set<Long> patientTagIds = associations.stream().map(Association::getTagId).collect(Collectors.toSet());

        return planList.stream().filter(item -> {
            // 计划没有设置触发对象。 或者 触发对象 患者也拥有。
            Long tagId = planTagMaps.get(item.getId());
            if (Objects.isNull(tagId)) {
                return true;
            } else {
                return patientTagIds.contains(tagId);
            }
        }).collect(Collectors.toList());
    }


    /**
     * 根据患者学习计划，和患者订阅的所有计划集合。筛选患者订阅的学习计划
     * @param plans
     * @param patientNursingPLanIds
     * @return
     */
    private List<Plan> getPatientLearnPlan(long days, List<Plan> plans, List<Long> patientNursingPLanIds) {
        if (CollUtil.isEmpty(plans)) {
            return plans;
        }
        if (CollUtil.isEmpty(patientNursingPLanIds)) {
            return new ArrayList<>();
        }
        return plans.stream().filter(item ->
            patientNursingPLanIds.contains(item.getId()) && (item.getEffectiveTime() == 0 || days >= item.getEffectiveTime())
        ).collect(Collectors.toList());
    }

    /**
     * 患者查询随访简介
     * 患者订阅为主， 随访开关，功能配置开关， 计划的开关 等条件。计算出患者可见的随访
     * @param patientId
     * @return
     */
    @Override
    public FollowBriefIntroductionDTO patientFindFollowBriefIntroduction(Long patientId) {

        FollowBriefIntroductionDTO followBriefIntroductionDTO = new FollowBriefIntroductionDTO();
        FollowTask followTask = baseMapper.selectOne(Wraps.<FollowTask>lbQ().last(" limit 0,1 "));
        if (Objects.nonNull(followTask)) {
            followBriefIntroductionDTO.setId(followTask.getId());
            followBriefIntroductionDTO.setName(followTask.getName());
            followBriefIntroductionDTO.setUrl(followTask.getUrl());
            followBriefIntroductionDTO.setUseDefault(followTask.getUseDefault());
            followBriefIntroductionDTO.setShowOutline(followTask.getShowOutline());
        }
        LocalDateTime completeEnterGroupTime = getPatientCompleteEnterGroupTime(patientId);
        long days;
        followBriefIntroductionDTO.setCreateTime(completeEnterGroupTime);
        Duration duration = Duration.between(completeEnterGroupTime, LocalDateTime.now());
        days = duration.toDays();
        followBriefIntroductionDTO.setDays(days);

        List<PatientNursingPlan> patientNursingPlans = getPatientNursingPlanService().patientSubscribeList(patientId);
        List<Long> patientNursingPLanIds = patientNursingPlans.stream()
                .map(PatientNursingPlan::getNursingPlantId).collect(Collectors.toList());
        List<Plan> planList = getPatientSeePlan(patientId);
        // 根据 患者可见的 护理计划列表。 转化为 随访任务栏数据

        List<FollowTaskContent> resultTaskContent = new ArrayList<>();
        int count = 0;
        boolean canAddLearnPlan = true;
        for (Plan plan : planList) {
            int subscribe = 0;
            if (patientNursingPLanIds.contains(plan.getId())) {
                subscribe = 1;
                count++;
            }
            FollowTaskContent taskContent = new FollowTaskContent();
            if (PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
                if (canAddLearnPlan) {
                    taskContent.setPlanType(PlanEnum.LEARN_PLAN.getCode());
                    taskContent.setPlanName(PlanEnum.LEARN_PLAN.getDesc());
                    taskContent.setShowName(PlanEnum.LEARN_PLAN.getDesc());
                    taskContent.setSubscribe(-1);
                    resultTaskContent.add(taskContent);
                }
                canAddLearnPlan = false;
            } else {
                taskContent.setPlanType(plan.getPlanType());
                taskContent.setPlanName(plan.getName());
                taskContent.setPlanId(plan.getId());
                taskContent.setShowName(plan.getName());
                taskContent.setSubscribe(subscribe);
                resultTaskContent.add(taskContent);
            }
        }
        followBriefIntroductionDTO.setFollowProjectNumber(count);
        followBriefIntroductionDTO.setFollowTaskContentList(resultTaskContent);
        return followBriefIntroductionDTO;
    }

    /**
     * 医生端查看随访详细的简介
     * @param tagIdList
     * @return
     */
    @Override
    public FollowBriefIntroductionDTO doctorFindFollowBriefIntroduction(List<Long> tagIdList) {

        FollowBriefIntroductionDTO followBriefIntroductionDTO = buildFollowBriefIntroduction(tagIdList);
        Long doctorId = BaseContextHandler.getUserId();
        R<Integer> patientNumber = patientApi.countDoctorPatientNumber(doctorId, tagIdList);
        if (patientNumber.getIsSuccess()) {
            Integer patientNumberData = patientNumber.getData();
            followBriefIntroductionDTO.setManagePatientNumber(patientNumberData);
        }
        return followBriefIntroductionDTO;
    }

    /**
     * 获取最大的时间范围
     * @param effectiveTime
     * @param timeFrame
     * @return
     */
    private Integer getMaxEffectiveTime(Integer effectiveTime, String timeFrame) {
        // 长期
        if (effectiveTime == null) {
            effectiveTime = 0;
        }
        if (effectiveTime == 0) {
            int maxEffectiveTime = 180;
            switch (timeFrame) {
                case FollowContentTimeFrame.NOT_NEED_SET:
                case FollowContentTimeFrame.THREE_YEARS:
                    maxEffectiveTime = 365 * 3;
                    break;
                case FollowContentTimeFrame.ONE_MOUTH:
                    maxEffectiveTime = 30;
                    break;
                case FollowContentTimeFrame.THREE_MONTHS:
                    maxEffectiveTime = 30 * 3;
                    break;
                case FollowContentTimeFrame.HALF_YEAR:
                    maxEffectiveTime = 30 * 6;
                    break;
                case FollowContentTimeFrame.ONE_YEAR:
                    maxEffectiveTime = 365;
                    break;
                case FollowContentTimeFrame.NOT_SET:
                    maxEffectiveTime = 30 * 6;
                    break;
            }
            return maxEffectiveTime;
        } else {
            return effectiveTime;
        }
    }

    @Override
    public FollowPlanShowDTO findOtherPlan(Long planId, Integer currentPage) {
        PlanService planService = getPlanService();
        Plan planAndDetails = planService.getPlanAndDetails(planId);
        Integer execute = planAndDetails.getExecute();
        Integer effectiveTime = planAndDetails.getEffectiveTime();
        Integer size = 20;
        Integer startSize = (currentPage - 1) * size;
        Integer endSize = currentPage * size;
        // 当 createIndex = endSize || 计划的执行日期超过最大天数 时，停止计算。
        Integer createIndex = 0;
        // 计划是长期的，则需要查询随访设置。
        FollowTaskContent taskContent = followTaskContentService.getOne(Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getPlanId, planId).last(" limit 0,1 "));
        FollowPlanShowDTO followPlanShowDTO = new FollowPlanShowDTO();
        followPlanShowDTO.setPlanName(taskContent.getShowName());
        if (taskContent.getTimeFrame().equals(FollowContentTimeFrame.NOT_SET)) {
            followPlanShowDTO.setTimeFrame(FollowContentTimeFrame.HALF_YEAR);
        } else {
            followPlanShowDTO.setTimeFrame(taskContent.getTimeFrame());
        }
        followPlanShowDTO.setTimeFrame(taskContent.getTimeFrame());
        List<PlanDetailDTO> planDetailList = planAndDetails.getPlanDetailList();
        Integer maxEffectiveTime = getMaxEffectiveTime(effectiveTime, taskContent.getTimeFrame());
        if (CollUtil.isEmpty(planDetailList)) {
            return followPlanShowDTO;
        }
        Map<Integer, PlanExecutionCycleDTO> cycleDTOMap = new HashMap<>();
        List<PlanExecutionCycleDTO> cycleDTOList = new ArrayList<>();
        PlanExecutionCycleDTO cycleDTO;
        followPlanShowDTO.setPlanExecutionCycles(cycleDTOList);
        for (PlanDetailDTO detailDTO : planDetailList) {
            Integer type = detailDTO.getType();
            Integer pushType = detailDTO.getPushType();
            if (type != null && (type == 1 || type == 3)) {
                List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                if (CollUtil.isEmpty(planDetailTimes)) {
                    continue;
                }
                for (PlanDetailTime detailTime : planDetailTimes) {
                    createIndex = 0;
                    // 推送间隔多少天
                    Integer frequency = detailTime.getFrequency();
                    // 推送日期
                    Integer preTime = detailTime.getPreTime();
                    if (Objects.isNull(preTime)) {
                        continue;
                    }
                    int key = execute + preTime;
                    PlanDetailsDTO detailsDTO = new PlanDetailsDTO();
                    detailsDTO.setPlanId(planId);
                    detailsDTO.setPlanType(planAndDetails.getPlanType());
                    if (pushType != null && pushType == 2) {
                        detailsDTO.setHrefUrl(detailDTO.getContent());
                    }
                    detailsDTO.setFirstShowTitle(taskContent.getShowName());
                    detailsDTO.setPlanExecutionDate(detailTime.getTime());

                    cycleDTO = cycleDTOMap.get(key);
                    if (createIndex >= startSize) {
                        if (cycleDTO == null) {
                            cycleDTO = new PlanExecutionCycleDTO();
                            cycleDTOMap.put(key, cycleDTO);
                            cycleDTOList.add(cycleDTO);
                            cycleDTO.setPlanExecutionDay(key);
                        }
                        cycleDTO.addPlanDetails(detailsDTO);
                    }
                    createIndex++;
                    if (frequency > 0) {
                        int newKey =  key + frequency;
                        while (maxEffectiveTime >= newKey) {
                            if (createIndex >= endSize) {
                                break;
                            }
                            if (createIndex >= startSize) {
                                cycleDTO = cycleDTOMap.get(newKey);
                                if (cycleDTO == null) {
                                    cycleDTO = new PlanExecutionCycleDTO();
                                    cycleDTOMap.put(newKey, cycleDTO);
                                    cycleDTOList.add(cycleDTO);
                                    cycleDTO.setPlanExecutionDay(newKey);
                                }
                                cycleDTO.addPlanDetails(detailsDTO);
                            }
                            newKey += frequency;

                            createIndex++;
                        }
                    }
                }
            }
        }
        followPlanShowDTO.setMaxPlanDay(maxEffectiveTime);
        followPlanShowDTO.setPlanEffectiveTime(effectiveTime);
        return followPlanShowDTO;
    }

    /**
     * 查询学习计划的推送计划
     * @return
     * @param tagId
     */
    @Override
    public List<FollowPlanShowDTO> findLearnPlan(List<Long> tagId) {
        List<Long> hasTagPlanIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(tagId)) {
            hasTagPlanIds = getHasTagPlanIds(tagId);
        }
        List<Plan> learnPlan = getPlanService().getPlanAndDetailsByPlanType(PlanEnum.LEARN_PLAN.getCode(), UserType.UCENTER_PATIENT, 1, hasTagPlanIds);
        if (CollUtil.isEmpty(learnPlan)) {
            return new ArrayList<>();
        }
        List<FollowPlanShowDTO> planShowDTOS = new ArrayList<>();
        FollowPlanShowDTO showDTO;
        if (CollUtil.isNotEmpty(learnPlan)) {
            for (Plan plan : learnPlan) {
                showDTO = new FollowPlanShowDTO();
                int learnCmsNumber = 0;
                showDTO.setPlanName(plan.getName());
                List<PlanExecutionCycleDTO> planExecutionCycleDTOS = new ArrayList<>();
                showDTO.setPlanExecutionCycles(planExecutionCycleDTOS);
                List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
                if (CollUtil.isEmpty(planDetailList)) {
                    continue;
                }
                Integer execute = plan.getExecute();
                PlanExecutionCycleDTO cycleDTO;
                // 使用执行计划的天数作为key
                Map<Integer, PlanExecutionCycleDTO> cycleDTOMap = new HashMap<>();
                for (PlanDetailDTO planDetailDTO : planDetailList) {
                    List<PlanDetailTime> planDetailTimes = planDetailDTO.getPlanDetailTimes();
                    if (CollUtil.isEmpty(planDetailTimes)) {
                        continue;
                    }
                    for (PlanDetailTime detailTime : planDetailTimes) {
                        Integer preTime = detailTime.getPreTime();
                        if (Objects.isNull(preTime)) {
                            continue;
                        }
                        int key = execute + preTime;
                        PlanDetailsDTO detailsDTO = getCmsInfo(detailTime);
                        if (Objects.isNull(detailsDTO)) {
                            continue;
                        }
                        cycleDTO = cycleDTOMap.get(key);
                        if (Objects.isNull(cycleDTO)) {
                            cycleDTO = new PlanExecutionCycleDTO();
                            cycleDTOMap.put(key, cycleDTO);
                            planExecutionCycleDTOS.add(cycleDTO);
                        }
                        cycleDTO.setPlanExecutionDay(key);
                        learnCmsNumber++;
                        detailsDTO.setPlanType(PlanEnum.LEARN_PLAN.getCode());
                        detailsDTO.setPlanExecutionDate(detailTime.getTime());
                        cycleDTO.addPlanDetails(detailsDTO);
                    }
                }
                showDTO.setLearnCmsNumber(learnCmsNumber);
                planShowDTOS.add(showDTO);
            }
            return planShowDTOS;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取 全部计划中 最大的时间范围
     * @param timeFrame
     * @param tempTimeFrame
     * @return
     */
    public String getMaxTimeFrame(String timeFrame, String tempTimeFrame) {
        if (FollowContentTimeFrame.NOT_SET.equals(tempTimeFrame) || FollowContentTimeFrame.NOT_NEED_SET.equals(tempTimeFrame)) {
            if (StrUtil.isEmpty(timeFrame)) {
                return FollowContentTimeFrame.HALF_YEAR;
            } else {
                return timeFrame;
            }
        } else {
            Integer maxTempEffectiveTime = getMaxEffectiveTime(0, tempTimeFrame);
            Integer maxEffectiveTime = getMaxEffectiveTime(0, timeFrame);
            if (maxTempEffectiveTime > maxEffectiveTime) {
                return tempTimeFrame;
            } else {
                return timeFrame;
            }
        }
    }

    /**
     * 获取患者的入组时间
     * @param patientId
     * @return
     */
    private LocalDateTime getPatientCompleteEnterGroupTime(Long patientId) {
        // 患者入组时间
        R<Patient> patientR = patientApi.get(patientId);
        Patient data = patientR.getData();
        LocalDateTime nursingTime = data.getNursingTime();
        LocalDateTime now = LocalDateTime.now();
        now = now.withHour(0).withSecond(0).withMinute(0).withNano(0);
        if (nursingTime == null) {
            nursingTime = now;
        }
        return nursingTime;
    }

    /**
     * 患者全部随访的 待执行计划，或者某个随访的待执行计划
     * @param patientId
     * @param planId
     * @return
     */
    @Override
    public List<FollowAllExecutionCycleDTO> patientQueryFollowPlanUnExecuted(Long patientId, Long planId) {
        LocalDateTime completeEnterGroupTime = getPatientCompleteEnterGroupTime(patientId);
        PlanService planService = getPlanService();
        // 可见的随访列表
        List<Long> patientNursingPLanIds;
        List<Plan> planList = new ArrayList<>();
        List<PatientNursingPlan> patientNursingPlans = getPatientNursingPlanService().patientSubscribeList(patientId);
        if (Objects.nonNull(planId)) {
            // 查询患者订阅的随访
            Plan plan = planService.getById(planId);
            if (Objects.isNull(plan)) {
                return new ArrayList<>();
            }
            planList.add(plan);
        } else {
            List<Plan> patientSeePlan = getPatientSeePlan(patientId);
            patientNursingPLanIds = patientNursingPlans.stream()
                    .map(PatientNursingPlan::getNursingPlantId).collect(Collectors.toList());
            planList = patientSeePlan.stream().filter(item -> patientNursingPLanIds.contains(item.getId())).collect(Collectors.toList());
        }
        // 患者的随访 只看患者订阅, 功能配置开启的， 计划开启的
        if (CollUtil.isEmpty(planList)) {
            return new ArrayList<>();
        }
        Map<LocalDate, FollowAllExecutionCycleDTO> executionCycleDTOHashMap = new HashMap<>();

        computePatientPlanExecuted(patientId, completeEnterGroupTime, planList, executionCycleDTOHashMap);

        Collection<FollowAllExecutionCycleDTO> cycleDTOCollection = executionCycleDTOHashMap.values();
        List<FollowAllExecutionCycleDTO> allExecutionCycleDTOS = new ArrayList<>(cycleDTOCollection);
        for (FollowAllExecutionCycleDTO executionCycleDTO : allExecutionCycleDTOS) {
            executionCycleDTO.setDetailDTOMap(null);
            executionCycleDTO.sort();
        }
        allExecutionCycleDTOS.sort((o1, o2) -> {
            if (o1.getExecutionDate().isAfter(o2.getExecutionDate())) {
                return 1;
            } else {
                return -1;
            }
        });
        return allExecutionCycleDTOS;
    }

    /**
     * 根据 detailTime 中的信息，查询其中的cms信息
     * @param detailTime
     * @return
     */
    public PlanDetailsDTO getCmsInfo(PlanDetailTime detailTime) {
        PlanDetailsDTO detailsDTO = new PlanDetailsDTO();
        if (detailTime.getSendUrl() != null && detailTime.getSendUrl() == 1) {
            detailsDTO.setHrefUrl(detailTime.getTemplateMessageId());
            detailsDTO.setFirstShowTitle(detailTime.getCmsTitle());
        } else {
            String templateMessageId = detailTime.getTemplateMessageId();
            if (StrUtil.isEmpty(templateMessageId)) {
                return null;
            }
            long cmsId = Long.parseLong(templateMessageId);
            R<ChannelContent> channelContentR = channelContentApi.getTitle(cmsId);
            if (channelContentR.getIsSuccess()) {
                ChannelContent channelContent = channelContentR.getData();
                if (Objects.nonNull(channelContent)) {
                    detailsDTO.setCmsId(cmsId);
                    detailsDTO.setFirstShowTitle(channelContent.getTitle());
                } else {
                    return null;
                }
            }
        }
        return detailsDTO;
    }

    /**
     * 患者的 学习计划
     * @param patientId
     * @return
     */
    @Override
    public List<FollowPlanShowDTO> patientQueryLearnPlan(Long patientId) {
        List<Plan> learnList = getPlanService().getPlanAndDetailsByPlanType(PlanEnum.LEARN_PLAN.getCode(), UserType.UCENTER_PATIENT, 1);
        // 计算学习计划对于患者的推送日期。
        LocalDateTime completeEnterGroupTime = getPatientCompleteEnterGroupTime(patientId);
        // 可见的随访列表
        // 查询患者订阅的随访
        PatientNursingPlanService patientNursingPlanService = getPatientNursingPlanService();
        List<PatientNursingPlan> patientNursingPlans = patientNursingPlanService.patientSubscribeList(patientId);
        Set<Long> patientNursingPLanIds = patientNursingPlans.stream().map(PatientNursingPlan::getNursingPlantId)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(learnList)) {
            return new ArrayList<>();
        }
        List<FollowPlanShowDTO> planShowDTOS = new ArrayList<>();
        Map<String, PlanDetailsDTO> map = new HashMap<>();
        FollowPlanShowDTO showDTO;
        for (Plan plan : learnList) {
            showDTO = new FollowPlanShowDTO();
            int learnCmsNumber = 0;
            showDTO.setPlanName(plan.getName());
            List<PlanExecutionCycleDTO> planExecutionCycleDTOS = new ArrayList<>();
            showDTO.setPlanExecutionCycles(planExecutionCycleDTOS);
            if (patientNursingPLanIds.contains(plan.getId())) {
                showDTO.setSubscribe(1);
            } else {
                showDTO.setSubscribe(0);
            }
            showDTO.setPlanId(plan.getId());
            List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
            if (CollUtil.isEmpty(planDetailList)) {
                continue;
            }
            Integer execute = plan.getExecute();
            PlanExecutionCycleDTO cycleDTO;
            // 使用执行计划的天数作为key
            Map<LocalDate, PlanExecutionCycleDTO> cycleDTOMap = new HashMap<>();
            for (PlanDetailDTO planDetailDTO : planDetailList) {
                List<PlanDetailTime> planDetailTimes = planDetailDTO.getPlanDetailTimes();
                if (CollUtil.isEmpty(planDetailTimes)) {
                    continue;
                }
                for (PlanDetailTime detailTime : planDetailTimes) {
                    Integer preTime = detailTime.getPreTime();
                    if (Objects.isNull(preTime)) {
                        continue;
                    }
                    // 计算患者首次收到的日期
                    LocalDate localDate = patientNursingPlanService.getPatientFirstRemindDay(completeEnterGroupTime, detailTime, execute, detailTime.getFrequency());
                    PlanDetailsDTO detailsDTO = getCmsInfo(detailTime);
                    if (Objects.isNull(detailsDTO)) {
                        continue;
                    }
                    cycleDTO = cycleDTOMap.get(localDate);
                    if (Objects.isNull(cycleDTO)) {
                        cycleDTO = new PlanExecutionCycleDTO();
                        cycleDTOMap.put(localDate, cycleDTO);
                        planExecutionCycleDTOS.add(cycleDTO);
                    }
                    cycleDTO.setLocalDate(localDate);
                    learnCmsNumber++;
                    detailsDTO.setPlanType(PlanEnum.LEARN_PLAN.getCode());
                    detailsDTO.setPlanExecutionDate(detailTime.getTime());
                    detailsDTO.setPlanExExecutionDay(localDate);
                    String reminderLogWorkId = createReminderLogWorkId(detailTime.getId());
                    map.put(reminderLogWorkId, detailsDTO);
                    cycleDTO.addPlanDetails(detailsDTO);
                }
            }
            showDTO.setLearnCmsNumber(learnCmsNumber);
            planShowDTOS.add(showDTO);
        }

        Set<String> remindWorkIds = map.keySet();
        List<ReminderLog> reminderLogs = reminderLogService.list(Wraps.<ReminderLog>lbQ()
                .select(ReminderLog::getWorkId, ReminderLog::getFinishThisCheckIn, SuperEntity::getId, SuperEntity::getCreateTime)
                .eq(ReminderLog::getPatientId, patientId)
                .in(ReminderLog::getWorkId, remindWorkIds));
        Map<String, List<ReminderLog>> listMap = reminderLogs.stream().collect(Collectors.groupingBy(ReminderLog::getWorkId));
        for (String workId : remindWorkIds) {
            PlanDetailsDTO detailsDTO = map.get(workId);
            List<ReminderLog> logList = listMap.get(workId);
            if (Objects.isNull(detailsDTO)){
                continue;
            }
            if (CollUtil.isEmpty(logList)) {
                continue;
            }
            for (ReminderLog reminderLog : logList) {
                Integer finishThisCheckIn = reminderLog.getFinishThisCheckIn();
                LocalDateTime createTime = reminderLog.getCreateTime();
                LocalDate localDate = createTime.toLocalDate();
                if (localDate.equals(detailsDTO.getPlanExExecutionDay())) {
                    detailsDTO.setMessageId(reminderLog.getId());
                    if (finishThisCheckIn != null && finishThisCheckIn == 1) {
                        detailsDTO.setReadStatus(1);
                        break;
                    } else {
                        detailsDTO.setReadStatus(0);
                    }
                }
            }
        }

        return planShowDTOS;
    }


    /**
     * 查询患者 已经执行过的 推送计划。
     * 过往30天内的。
     * @param patientId
     * @param planId
     * @return
     */
    @Override
    public List<FollowAllExecutionCycleDTO> patientQueryFollowPlanExecuted(Long patientId, Long planId) {

        LocalDateTime now = LocalDateTime.now().withNano(0).withSecond(0).withHour(0).withMinute(0);
        LocalDateTime endTime = now.plusDays(-30);
        List<ReminderLog> reminderLogs = getReminderLogList(patientId, planId, null, endTime, now);
        if (CollUtil.isEmpty(reminderLogs)) {
            return new ArrayList<>();
        }
        Set<Long> nursingPLanDetailTimeIds = nursingPLanDetailTimeIds(reminderLogs);
        Map<Long, Plan> planMap = new HashMap<>();
        // 记录 time 和 detail 的关系
        Map<String, Long> planDetailTimeMap = new HashMap<>();
        Map<String, PlanDetailTime> planDetailTimeObjMap = new HashMap<>();
        // 记录 detail 和plan 的关系
        Map<Long, Long> planDetailMap = new HashMap<>();
        Map<Long, PlanDetail> planDetailObjMap = new HashMap<>();
        setPlanInfo(nursingPLanDetailTimeIds, planMap, planDetailTimeMap, planDetailTimeObjMap, planDetailMap, planDetailObjMap);
        if (CollUtil.isNotEmpty(planMap)) {
            Map<LocalDate, FollowAllExecutionCycleDTO> executionCycleDTOHashMap = new HashMap<>();
            Long detailId;
            String hrefUrl = null;
            for (ReminderLog reminderLog : reminderLogs) {
                LocalDateTime createTime = reminderLog.getCreateTime();
                LocalDate localDate = createTime.toLocalDate();
                LocalTime localTime = createTime.toLocalTime();
                detailId = planDetailTimeMap.get(reminderLog.getWorkId());
                if (Objects.isNull(detailId)) {
                    continue;
                }
                planId = planDetailMap.get(detailId);
                PlanDetail planDetail = planDetailObjMap.get(detailId);
                if (Objects.isNull(planId)) {
                    continue;
                }
                Plan plan = planMap.get(planId);
                if (Objects.isNull(plan)) {
                    continue;
                }
                FollowAllExecutionCycleDTO executionCycleDTO = executionCycleDTOHashMap.get(localDate);
                if (Objects.isNull(executionCycleDTO)) {
                    executionCycleDTO = new FollowAllExecutionCycleDTO();
                    executionCycleDTO.setExecutionDate(localDate);
                    executionCycleDTOHashMap.put(localDate, executionCycleDTO);
                }
                FollowAllPlanDetailDTO detailDTO = new FollowAllPlanDetailDTO();
                PlanDetailTime detailTime = planDetailTimeObjMap.get(reminderLog.getWorkId());
                if (PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
                    detailDTO.setPlanType(plan.getPlanType());
                    detailDTO.setFirstShowTitle(PlanEnum.LEARN_PLAN.getDesc());
                    PlanDetailsDTO cmsInfo = getCmsInfo(detailTime);
                    if (Objects.isNull(cmsInfo)) {
                        continue;
                    }
                    executionCycleDTO.addPLanDetailsNotLearnPlan(detailDTO, cmsInfo.getCmsId(), cmsInfo.getHrefUrl(),
                            cmsInfo.getFirstShowTitle(), localTime, reminderLog.getFinishThisCheckIn(), reminderLog.getId());
                } else {
                    detailDTO.setPlanId(plan.getId());
                    detailDTO.setPlanType(plan.getPlanType());
                    detailDTO.setFirstShowTitle(plan.getName());
                    detailDTO.setPlanFinishStatus(reminderLog.getFinishThisCheckIn());
                    Integer pushType = planDetail.getPushType();
                    if (pushType != null && pushType == 2) {
                        hrefUrl = planDetail.getContent();
                    } else {
                        hrefUrl = null;
                    }
                    executionCycleDTO.addPLanDetailsNotLearnPlan(detailDTO, null, hrefUrl, null,
                            localTime, reminderLog.getFinishThisCheckIn(), reminderLog.getId());
                }
            }
            Collection<FollowAllExecutionCycleDTO> cycleDTOCollection = executionCycleDTOHashMap.values();
            List<FollowAllExecutionCycleDTO> allExecutionCycleDTOS = new ArrayList<>(cycleDTOCollection);
            for (FollowAllExecutionCycleDTO executionCycleDTO : allExecutionCycleDTOS) {
                executionCycleDTO.setDetailDTOMap(null);
                executionCycleDTO.sort();
            }
            allExecutionCycleDTOS.sort((o1, o2) -> {
                if (o1.getExecutionDate().isAfter(o2.getExecutionDate())) {
                    return -1;
                } else {
                    return 1;
                }
            });
            return allExecutionCycleDTOS;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 根据推送记录。 反向查找当前 还存在的 护理计划
     * @param nursingPLanDetailTimeIds
     * @param planMap
     * @param planDetailTimeMap
     * @param planDetailTimeObjMap
     * @param planDetailMap
     */
    private void setPlanInfo(Set<Long> nursingPLanDetailTimeIds, Map<Long, Plan> planMap,
                             Map<String, Long> planDetailTimeMap, Map<String, PlanDetailTime> planDetailTimeObjMap,
                             Map<Long, Long> planDetailMap, Map<Long, PlanDetail> planDetailObjMap) {
        List<PlanDetailTime> planDetailTimes = planDetailTimeMapper.selectList(Wraps.<PlanDetailTime>lbQ().in(SuperEntity::getId, nursingPLanDetailTimeIds));
        if (CollUtil.isNotEmpty(planDetailTimes)) {


            List<Long> planDetailIds = planDetailTimes.stream().map(PlanDetailTime::getNursingPlanDetailId).collect(Collectors.toList());
            List<PlanDetail> planDetails = planDetailMapper.selectList(Wraps.<PlanDetail>lbQ().in(PlanDetail::getId, planDetailIds));
            if (CollUtil.isEmpty(planDetails)) {
                return;
            }
            List<Long> planIds = planDetails.stream().map(PlanDetail::getNursingPlanId).collect(Collectors.toList());
            if (CollUtil.isEmpty(planIds)) {
                return;
            }
            List<Plan> planList = planService.list(Wraps.<Plan>lbQ().in(SuperEntity::getId, planIds));


            planDetailTimes.forEach(item -> planDetailTimeMap.put(createReminderLogWorkId(item.getId()), item.getNursingPlanDetailId()));
            planDetailTimes.forEach(item -> planDetailTimeObjMap.put(createReminderLogWorkId(item.getId()), item));
            planDetails.forEach(item -> {
                planDetailMap.put(item.getId(), item.getNursingPlanId());
                planDetailObjMap.put(item.getId(), item);
            });
            planList.forEach(item -> planMap.put(item.getId(), item));


        }


    }

    /**
     * 查询患者 某些 护理计划 过去一段时间的推送记录
     * @param patientId
     * @param planId
     * @param planType 为1. 表示查询患者的学习计划
     * @param startTime
     * @param endTime
     * @return
     */
    private List<ReminderLog> getReminderLogList(Long patientId, Long planId, Integer planType, LocalDateTime startTime, LocalDateTime endTime) {
        String reminderLogWorkId = null;
        LbqWrapper<ReminderLog> lbqWrapper = Wraps.<ReminderLog>lbQ();
        PlanService planService = getPlanService();
        List<String> workIds = new ArrayList<>();
        if (Objects.nonNull(planId)) {
            Plan plan = planService.getPlanAndDetails(planId);
            List<PlanDetailDTO> detailList = plan.getPlanDetailList();
            if (CollUtil.isEmpty(detailList)) {
                return new ArrayList<>();
            }
            for (PlanDetailDTO detailDTO : detailList) {
                List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                if (CollUtil.isEmpty(planDetailTimes)) {
                    break;
                }
                for (PlanDetailTime time : planDetailTimes) {
                    reminderLogWorkId = createReminderLogWorkId(time.getId());
                    workIds.add(reminderLogWorkId);
                }
            }
            if (CollUtil.isNotEmpty(workIds)) {
                lbqWrapper.in(ReminderLog::getWorkId, workIds);
            }
        } else if (Objects.nonNull(planType) && PlanEnum.LEARN_PLAN.getCode().equals(planType)) {
            List<Plan> planList = planService.getPlanAndDetailsByPlanType(PlanEnum.LEARN_PLAN.getCode(), UserType.UCENTER_PATIENT, 1);
            for (Plan plan : planList) {
                List<PlanDetailDTO> detailList = plan.getPlanDetailList();
                if (CollUtil.isEmpty(detailList)) {
                    continue;
                }
                for (PlanDetailDTO detailDTO : detailList) {
                    List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                    if (CollUtil.isEmpty(planDetailTimes)) {
                        break;
                    }
                    for (PlanDetailTime time : planDetailTimes) {
                        workIds.add(createReminderLogWorkId(time.getId()));
                    }
                }
            }
            if (CollUtil.isEmpty(workIds)) {
                return new ArrayList<>();
            } else {
                lbqWrapper.in(ReminderLog::getWorkId, workIds);
            }
        }
        lbqWrapper.eq(ReminderLog::getPatientId, patientId);
        lbqWrapper.gt(SuperEntity::getCreateTime, startTime);
        lbqWrapper.lt(SuperEntity::getCreateTime, endTime);
        // 过来掉用药打卡
        lbqWrapper.apply(" (type_ <> 4 or type_ is null) ");
        lbqWrapper.orderByAsc(SuperEntity::getCreateTime);
        return reminderLogService.list(lbqWrapper);
    }


    /**
     * 转义 推送记录中的 工作ID
     * @param reminderLogs
     * @return
     */
    private Set<Long> nursingPLanDetailTimeIds(List<ReminderLog> reminderLogs) {
        Set<Long> nursingPLanDetailTimeIds = new HashSet<>();
        reminderLogs.forEach(item -> {
            String workId = item.getWorkId();
            JSONArray jsonArray = JSONUtil.parseArray(workId);
            if (CollUtil.isNotEmpty(jsonArray)) {
                Object timeId = jsonArray.get(0);
                if (Objects.nonNull(timeId)) {
                    nursingPLanDetailTimeIds.add(Long.parseLong(timeId.toString()));
                }
            }
        });
        return nursingPLanDetailTimeIds;
    }

    /**
     * 查询患者随访的 日历
     * @param patientId
     * @param planId
     * @param learnPlan
     * @param queryMonthly
     * @return
     */
    @Override
    public Set<LocalDate> patientQueryFollowPlanCalendar(Long patientId, Long planId, Integer learnPlan, LocalDate queryMonthly) {

        LocalDate now = LocalDate.now();
        LocalDate currentMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        PlanService planService = getPlanService();
        // 本月已经过去， 直接查询推送记录。并根据推送记录，和当前计划的存在状态 过滤日期
        if (queryMonthly.isBefore(currentMonth)) {

            // 使用开始时间和结束时间。查询 时间段内的 推送记录
            LocalDateTime startTime = LocalDateTime.of(queryMonthly, LocalTime.of(0,0,0,0));
            LocalDateTime endTime = LocalDateTime.of(queryMonthly.plusMonths(1), LocalTime.of(0,0,0,0));
            return getCalenderByReminderLog(patientId, planId, learnPlan, startTime, endTime);
        }

        // 找到患者 全部的 可推送的护理计划
        PatientNursingPlanService patientNursingPlanService = getPatientNursingPlanService();
        List<PatientNursingPlan> patientNursingPlans = patientNursingPlanService.patientSubscribeList(patientId);

        List<Long> patientNursingPLanIds;
        List<Plan> patientSeePlan = getPatientSeePlan(patientId);
        patientNursingPLanIds = patientNursingPlans.stream()
                .map(PatientNursingPlan::getNursingPlantId).collect(Collectors.toList());

        LocalDateTime completeEnterGroupTime = getPatientCompleteEnterGroupTime(patientId);

        Set<LocalDate> calender = new HashSet<>();
        // 本月正在度过。 使用当前可推送的计划， 预测本月哪几天有随访
        List<Plan> planList = patientSeePlan.stream().filter(item -> patientNursingPLanIds.contains(item.getId())).collect(Collectors.toList());
        LocalDateTime startTime = LocalDateTime.of(queryMonthly, LocalTime.of(0,0,0,0));
        LocalDateTime currentDay = LocalDateTime.now();
        // 下个月的 1号
        LocalDateTime nextMonthDay = startTime.plusMonths(1);
        if (currentDay.isAfter(startTime)) {
            calender = getCalenderByReminderLog(patientId, planId, learnPlan, startTime, currentDay);
            startTime = currentDay.plusDays(-1);
        } else {
            startTime = startTime.plusDays(-1);
        }
        // 使用 护理计划列表。计算 剩下的日子。哪些天推送
        List<Long> planIds = planList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        planList = planService.getPlanAndDetails(planIds);
        for (Plan plan : planList) {
            if (Objects.nonNull(planId)) {
                if (!plan.getId().equals(planId)) {
                    continue;
                }
            }
            if (Objects.nonNull(learnPlan)) {
                if (!PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
                    continue;
                }
            }
            Integer execute = plan.getExecute();
            Integer effectiveTime = plan.getEffectiveTime();
            List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
            if (CollUtil.isEmpty(planDetailList)) {
                continue;
            }
            for (PlanDetailDTO detailDTO : planDetailList) {
                List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                if (CollUtil.isEmpty(planDetailTimes)) {
                    continue;
                }
                for (PlanDetailTime planDetailTime : planDetailTimes) {
                    if (Objects.isNull(planDetailTime.getPreTime())) {
                        continue;
                    }
                    LocalDateTime firstPushTime = completeEnterGroupTime.plusDays(execute).plusDays(planDetailTime.getPreTime());
                    if (PlanEnum.REVIEW_REMIND.getCode().equals(plan.getPlanType())) {
                        PatientCustomPlanTime customPlan = getPatientNursingPlanService().getPatientCustomPlan(patientId, plan.getId(), detailDTO.getId(), planDetailTime.getId());
                        if (Objects.nonNull(customPlan)) {
                            LocalDateTime nextRemindTime = customPlan.getNextRemindTime();
                            if (checkRemindTimeRight(nextRemindTime, firstPushTime, 0, nextMonthDay)) {
                                calender.add(nextRemindTime.toLocalDate());
                            }
                            break;
                        }
                    }
                    Integer frequency = planDetailTime.getFrequency();
                    LocalDateTime nextRemindTime = getPatientNursingPlanService().getPatientNextRemindTime(completeEnterGroupTime, planDetailTime, execute, frequency, startTime.toLocalDate(), effectiveTime);
                    while (checkRemindTimeRight(nextRemindTime, firstPushTime, effectiveTime, nextMonthDay)) {
                        LocalDate localDate = nextRemindTime.toLocalDate();
                        calender.add(localDate);
                        // 如果是单次间隔，执行一次后，不再执行
                        if (frequency == 0) {
                            break;
                        }
                        nextRemindTime = nextRemindTime.plusDays(frequency);
                    }
                }
            }
        }
        return calender;
    }

    /**
     * 检查 推算出来的 计划的推送时间，是否在这个日期范围
     * @param nextRemindTime
     * @param effectiveTime
     * @return
     */
    public boolean checkRemindTimeRight(LocalDateTime nextRemindTime, LocalDateTime firstPushTime, Integer effectiveTime, LocalDateTime endDay) {
        if (nextRemindTime == null) {
            return false;
        }
        LocalDateTime nextRemind = nextRemindTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        if (!nextRemindTime.isBefore(endDay)) {
            return false;
        }
        if (effectiveTime > 0) {
            long days = DateUtils.getDays(nextRemind.toLocalDate(), firstPushTime.toLocalDate());
            if (days >= effectiveTime) {
                return false;
            }
        }
        return true;

    }



    /**
     * 查询日历中某一天 的随访任务
     * @param patientId
     * @param planId
     * @param learnPlan
     * @param queryMonthly
     * @return
     */
    @Override
    public FollowAllExecutionCycleDTO patientQueryFollowPlanCalendarDayPlanDetail(Long patientId, Long planId, Integer learnPlan, LocalDate queryMonthly) {

        FollowAllExecutionCycleDTO cycleDTO = new FollowAllExecutionCycleDTO();
        LocalDate now = LocalDate.now();
        // 日期在今天 或 今天之前。 根据推送记录查询
        if (queryMonthly.isBefore(now) || queryMonthly.isEqual(now)) {
            LocalDateTime startTime = LocalDateTime.of(queryMonthly, LocalTime.of(0,0,0,0));
            LocalDateTime endTime = LocalDateTime.of(queryMonthly, LocalTime.of(23,59,59));
            List<ReminderLog> reminderLogs;
            if (Objects.nonNull(learnPlan) && learnPlan == 1) {
                reminderLogs = getReminderLogList(patientId, planId, PlanEnum.LEARN_PLAN.getCode(), startTime, endTime);
            } else {
                reminderLogs = getReminderLogList(patientId, planId, null, startTime, endTime);
            }
            Set<Long> nursingPLanDetailTimeIds = nursingPLanDetailTimeIds(reminderLogs);
            Map<Long, Plan> planMap = new HashMap<>();
            // 记录 time 和 detail 的关系
            Map<String, Long> planDetailTimeMap = new HashMap<>();
            Map<String, PlanDetailTime> planDetailTimeObjMap = new HashMap<>();
            // 记录 detail 和plan 的关系
            Map<Long, Long> planDetailMap = new HashMap<>();
            Map<Long, PlanDetail> planDetailObjMap = new HashMap<>();
            setPlanInfo(nursingPLanDetailTimeIds, planMap, planDetailTimeMap, planDetailTimeObjMap, planDetailMap, planDetailObjMap);

            for (ReminderLog reminderLog : reminderLogs) {
                String workId = reminderLog.getWorkId();
                Long detailId = planDetailTimeMap.get(workId);
                LocalDateTime pushTime = reminderLog.getWaitPushTime();
                if (pushTime == null) {
                    pushTime = reminderLog.getCreateTime();
                }
                LocalTime localTime = pushTime.toLocalTime();
                if (Objects.isNull(detailId)) {
                    continue;
                }
                Long tempPlanId = planDetailMap.get(detailId);
                if (Objects.isNull(tempPlanId)) {
                    continue;
                }
                Plan plan = planMap.get(tempPlanId);
                String hrefUrl = null;
                if (Objects.nonNull(plan)) {
                    cycleDTO.setExecutionDate(queryMonthly);
                    FollowAllPlanDetailDTO detailDTO = new FollowAllPlanDetailDTO();
                    PlanDetailTime planDetailTime = planDetailTimeObjMap.get(workId);
                    if (PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
                        PlanDetailsDTO detailsDTO = getCmsInfo(planDetailTime);
                        if (Objects.isNull(detailsDTO)) {
                            continue;
                        }
                        detailDTO.setPlanId(plan.getId());
                        detailDTO.setPlanType(plan.getPlanType());
                        detailDTO.setFirstShowTitle(plan.getName());
                        detailDTO.setPlanFinishStatus(1);
                        cycleDTO.addPLanDetailsNotLearnPlan(detailDTO, detailsDTO.getCmsId(),
                                detailsDTO.getHrefUrl(), detailsDTO.getFirstShowTitle(), localTime, reminderLog.getFinishThisCheckIn(), reminderLog.getId());
                    } else {
                        PlanDetail planDetail = planDetailObjMap.get(detailId);
                        Integer pushType = planDetail.getPushType();
                        if (pushType != null && pushType == 2) {
                            hrefUrl = planDetail.getContent();
                        }
                        detailDTO.setPlanId(plan.getId());
                        detailDTO.setPlanType(plan.getPlanType());
                        detailDTO.setFirstShowTitle(plan.getName());
                        detailDTO.setPlanFinishStatus(1);
                        cycleDTO.addPLanDetailsNotLearnPlan(detailDTO, null, hrefUrl, null, localTime, reminderLog.getFinishThisCheckIn(), reminderLog.getId());
                    }
                }
            }
            cycleDTO.setDetailDTOMap(null);
            return cycleDTO;
        }
        LocalDateTime completeEnterGroupTime = getPatientCompleteEnterGroupTime(patientId);
        // 日期在今天 或者 今天之后。需要根据护理计划推算
        PlanService planService = getPlanService();
        // 可见的随访列表
        List<Long> patientNursingPLanIds;
        List<Plan> planList = new ArrayList<>();
        List<PatientNursingPlan> patientNursingPlans = getPatientNursingPlanService().patientSubscribeList(patientId);
        if (Objects.nonNull(planId)) {
            // 查询患者订阅的随访
            Plan plan = planService.getById(planId);
            if (Objects.isNull(plan)) {
                return cycleDTO;
            }
            planList.add(plan);
        } else {
            List<Plan> patientSeePlan = getPatientSeePlan(patientId);
            patientNursingPLanIds = patientNursingPlans.stream()
                    .map(PatientNursingPlan::getNursingPlantId).collect(Collectors.toList());
            planList = patientSeePlan.stream().filter(item -> patientNursingPLanIds.contains(item.getId())).collect(Collectors.toList());
        }
        // 患者的随访 只看患者订阅, 功能配置开启的， 计划开启的
        if (CollUtil.isEmpty(planList)) {
            return cycleDTO;
        }
        if (learnPlan != null && learnPlan == 1) {
            planList = planList.stream().filter(item -> PlanEnum.LEARN_PLAN.getCode().equals(item.getPlanType())).collect(Collectors.toList());
        }
        // 使用这些护理计划推算 推送时间是今天的 都有哪些
        if (CollUtil.isEmpty(planList)) {
            return cycleDTO;
        }
        List<Long> planIds = planList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        planList = planService.getPlanAndDetails(planIds);
        for (Plan plan : planList) {
            Integer planModel = plan.getPlanModel();
            Integer execute = plan.getExecute();
            Integer effectiveTime = plan.getEffectiveTime();
            List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
            if (CollUtil.isEmpty(planDetailList)) {
                continue;
            }
            for (PlanDetailDTO detailDTO : planDetailList) {
                List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                if (CollUtil.isEmpty(planDetailTimes)) {
                    continue;
                }
                Integer pushType = detailDTO.getPushType();
                String hrefUrl = null;
                if (pushType != null && pushType == 2) {
                    hrefUrl = detailDTO.getContent();
                }
                FollowAllPlanDetailDTO followAllPlanDetailDTO = new FollowAllPlanDetailDTO();
                int planFinishStatus = 1;
                for (PlanDetailTime planDetailTime : planDetailTimes) {
                    Integer preTime = planDetailTime.getPreTime();
                    Integer frequency = planDetailTime.getFrequency();
                    LocalTime time = planDetailTime.getTime();
                    if (Objects.isNull(preTime)) {
                        continue;
                    }
                    LocalDate localDate = null;
                    PatientCustomPlanTime customPlan = null;
                    if (PlanEnum.REVIEW_REMIND.getCode().equals(plan.getPlanType())) {
                        customPlan = getPatientNursingPlanService().getPatientCustomPlan(patientId, plan.getId(), detailDTO.getId(), planDetailTime.getId());
                    }
                    // 注册模式的随访计划 查询他的自定义提醒时间
                    if (Objects.nonNull(planModel) && planModel.equals(1)) {
                        customPlan = getPatientNursingPlanService().getPatientCustomPlan(patientId, plan.getId(), detailDTO.getId(), planDetailTime.getId());
                    }

                    if (Objects.nonNull(customPlan)) {
                        LocalDateTime nextRemindTime = customPlan.getNextRemindTime();
                        localDate = nextRemindTime.toLocalDate();
                        time = nextRemindTime.toLocalTime();
                    }
                    boolean whetherPush = PushAlgorithm.builder()
                            .completeEnterGroupTime(completeEnterGroupTime)
                            .execute(execute)
                            .preDays(preTime)
                            .frequency(frequency)
                            .effectiveTime(effectiveTime)
                            .currentDay(queryMonthly)
                            .build().whetherPush();
                    if (whetherPush || (Objects.nonNull(localDate) && queryMonthly.equals(localDate))) {
                        // 说明是今天推送
                        followAllPlanDetailDTO.setPlanType(plan.getPlanType());
                        followAllPlanDetailDTO.setFollowUpPlanType(plan.getFollowUpPlanType());
                        followAllPlanDetailDTO.setPlanId(plan.getId());
                        followAllPlanDetailDTO.setFirstShowTitle(plan.getName());
                        String reminderLogWorkId = createReminderLogWorkId(planDetailTime.getId());
                        ReminderLog reminderLog = reminderLogService.getOne(Wraps.<ReminderLog>lbQ()
                                .eq(ReminderLog::getPatientId, patientId)
                                .eq(ReminderLog::getWorkId, reminderLogWorkId)
                                .lt(SuperEntity::getCreateTime, LocalDateTime.of(queryMonthly, LocalTime.of(23,59,59)))
                                .gt(SuperEntity::getCreateTime, LocalDateTime.of(queryMonthly, LocalTime.of(0,0,0)))
                                .last(" limit 1 "));
                        Long reminderLogId = null;
                        int cmsReadStatus = 0;
                        if (Objects.nonNull(reminderLog)) {
                            reminderLogId = reminderLog.getId();
                            if (reminderLog.getFinishThisCheckIn() == 1) {
                                cmsReadStatus = 1;
                            }
                        } else {
                            planFinishStatus = 0;
                        }
                        if (PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
                            PlanDetailsDTO cmsInfo = getCmsInfo(planDetailTime);
                            if (Objects.isNull(cmsInfo)) {
                                continue;
                            }
                            hrefUrl = cmsInfo.getHrefUrl();
                            String firstShowTitle = cmsInfo.getFirstShowTitle();
                            Long cmsId = cmsInfo.getCmsId();
                            cycleDTO.addPLanDetailsNotLearnPlan(followAllPlanDetailDTO, cmsId, hrefUrl,  firstShowTitle, time, cmsReadStatus, reminderLogId, planDetailTime.getId());
                        } else {
                            cycleDTO.addPLanDetailsNotLearnPlan(followAllPlanDetailDTO, null, hrefUrl,  null, time, cmsReadStatus, reminderLogId, planDetailTime.getId());
                        }
                    }
                }
                followAllPlanDetailDTO.setPlanFinishStatus(planFinishStatus);
            }
        }
        cycleDTO.setDetailDTOMap(null);
        return cycleDTO;
    }


    /**
     * 患者统计页面的 随访任务栏
     * @param patientId
     * @return
     */
    @Override
    public FollowCountDetailDTO patientQueryFollowCountDetail(Long patientId) {

        FollowCountDetailDTO detailDTO = new FollowCountDetailDTO();
        LocalDateTime completeEnterGroupTime = getPatientCompleteEnterGroupTime(patientId);

        List<PatientNursingPlan> patientNursingPlans = getPatientNursingPlanService().patientSubscribeList(patientId);
        List<Long> patientNursingPLanIds = patientNursingPlans.stream()
                .map(PatientNursingPlan::getNursingPlantId).collect(Collectors.toList());
        List<Plan> planList = getPatientSeePlan(patientId);
        // 根据 患者可见的 护理计划列表。 转化为 随访任务栏数据

        List<FollowTaskContent> resultTaskContent = new ArrayList<>();
        boolean canAddLearnPlan = true;
        for (Plan plan : planList) {
            if (patientNursingPLanIds.contains(plan.getId())) {
                FollowTaskContent taskContent = new FollowTaskContent();
                if (PlanEnum.LEARN_PLAN.getCode().equals(plan.getPlanType())) {
                    if (canAddLearnPlan) {
                        canAddLearnPlan = false;
                        taskContent.setPlanType(PlanEnum.LEARN_PLAN.getCode());
                        taskContent.setPlanName(PlanEnum.LEARN_PLAN.getDesc());
                        taskContent.setShowName(PlanEnum.LEARN_PLAN.getDesc());
                        resultTaskContent.add(taskContent);
                    }
                } else {
                    taskContent.setPlanType(plan.getPlanType());
                    taskContent.setPlanName(plan.getName());
                    taskContent.setPlanId(plan.getId());
                    taskContent.setShowName(plan.getName());
                    resultTaskContent.add(taskContent);
                }
            }
        }
        detailDTO.setStartTime(completeEnterGroupTime);
        detailDTO.setEndTime(LocalDateTime.now());
        detailDTO.setFollowTaskContentList(resultTaskContent);
        return detailDTO;
    }


    /**
     * 患者统计 学习计划的推送情况
     *
     * @param patientId
     * @return
     */
    @Override
    public List<PatientFollowLearnPlanStatisticsDTO> patientLearnPlanStatistics(Long patientId) {

        // 查询患者关注的学习计划
        PatientNursingPlanService patientNursingPlanService = getPatientNursingPlanService();
        List<PatientNursingPlan> patientNursingPlans = patientNursingPlanService.patientSubscribeList(patientId);
        if (CollUtil.isEmpty(patientNursingPlans)) {
            return new ArrayList<>();
        }
        List<Long> patientPlanIds = patientNursingPlans.stream().map(PatientNursingPlan::getNursingPlantId).collect(Collectors.toList());
        PlanService planService = getPlanService();
        List<Plan> learnPlanList = planService.getPlanAndDetailsByPlanType(PlanEnum.LEARN_PLAN.getCode(), UserType.UCENTER_PATIENT, 1);
        if (CollUtil.isEmpty(learnPlanList)) {
            return new ArrayList<>();
        }

        learnPlanList = learnPlanList.stream().filter(item -> patientPlanIds.contains(item.getId())).collect(Collectors.toList());
        if (CollUtil.isEmpty(learnPlanList)) {
            return new ArrayList<>();
        }
        List<PatientFollowLearnPlanStatisticsDTO> statisticsDTOS = new ArrayList<>();
        learnPlanList.forEach(plan -> {
            PatientFollowLearnPlanStatisticsDTO statisticsDTO = new PatientFollowLearnPlanStatisticsDTO();
            PlanDetail planDetail = planDetailMapper.selectOne(Wraps.<PlanDetail>lbQ().eq(PlanDetail::getNursingPlanId, plan.getId()).last(" limit 1 "));
            if (Objects.nonNull(planDetail)) {
                List<PlanDetailTime> planDetailTimes = planDetailTimeMapper.selectList(Wraps.<PlanDetailTime>lbQ()
                        .eq(PlanDetailTime::getNursingPlanDetailId, planDetail.getId()));
                List<String> workIds = new ArrayList<>();
                planDetailTimes.forEach(item -> workIds.add(createReminderLogWorkId(item.getId())));
                if (CollUtil.isNotEmpty(planDetailTimes)) {
                    reminderLogService.patientStatistics(patientId, workIds, statisticsDTO);
                    statisticsDTO.setLearnPlanName(plan.getName());
                    statisticsDTO.setCmsNumber(planDetailTimes.size());
                    statisticsDTOS.add(statisticsDTO);
                }
            }
        });
        return statisticsDTOS;
    }


    /**
     * 患者端 其他计划的统计
     * @param patientId
     * @param planId
     * @return
     */
    @Override
    public FollowCountOtherPlanDTO patientFollowPlanStatistics(Long patientId, Long planId) {

        PlanService planService = getPlanService();
        Plan plan = planService.getPlanAndDetails(planId);
        FollowCountOtherPlanDTO planDTO = new FollowCountOtherPlanDTO();
        if (Objects.isNull(plan)) {
            return planDTO;
        }
        List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
        if (CollUtil.isEmpty(planDetailList)) {
            return planDTO;
        }
        PlanDetailDTO detailDTO = planDetailList.get(0);
        List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
        if (CollUtil.isEmpty(planDetailTimes)) {
            return planDTO;
        }
        List<String> workIds = new ArrayList<>();
        for (PlanDetailTime detailTime : planDetailTimes) {
            String reminderLogWorkId = createReminderLogWorkId(detailTime.getId());
            workIds.add(reminderLogWorkId);
        }
        planDTO.setPlanName(plan.getName());
        planDTO.setPlanId(planId);
        reminderLogService.patientStatistics(patientId, workIds, planDTO);
        return planDTO;
    }


    /**
     * 查询一段时间内， 哪些天执行 推送计划
     * @param patientId
     * @param planId
     * @param learnPlan
     * @param startTime
     * @param endTime
     * @return
     */
    private Set<LocalDate> getCalenderByReminderLog(Long patientId, Long planId, Integer learnPlan, LocalDateTime startTime, LocalDateTime endTime) {
        Set<LocalDate> calender = new HashSet<>();
        List<ReminderLog> reminderLogs;
        if (Objects.nonNull(learnPlan) && learnPlan == 1) {
            reminderLogs = getReminderLogList(patientId, planId, PlanEnum.LEARN_PLAN.getCode(), startTime, endTime);
        } else {
            reminderLogs = getReminderLogList(patientId, planId, null, startTime, endTime);
        }
        if (CollUtil.isEmpty(reminderLogs)) {
            return calender;
        }
        Set<Long> nursingPLanDetailTimeIds = nursingPLanDetailTimeIds(reminderLogs);
        Map<Long, Plan> planMap = new HashMap<>();
        // 记录 time 和 detail 的关系
        Map<String, Long> planDetailTimeMap = new HashMap<>();
        Map<String, PlanDetailTime> planDetailTimeObjMap = new HashMap<>();
        // 记录 detail 和plan 的关系
        Map<Long, Long> planDetailMap = new HashMap<>();
        Map<Long, PlanDetail> planDetailObjMap = new HashMap<>();
        setPlanInfo(nursingPLanDetailTimeIds, planMap, planDetailTimeMap, planDetailTimeObjMap, planDetailMap, planDetailObjMap);
        for (ReminderLog reminderLog : reminderLogs) {
            String workId = reminderLog.getWorkId();
            Long detailId = planDetailTimeMap.get(workId);
            if (Objects.nonNull(planDetailMap.get(detailId))) {
                calender.add(reminderLog.getCreateTime().toLocalDate());
            }
        }
        return calender;
    }


    /**
     * 计算随访对应的护理计划们。在未来30天，患者的日志计划
     * @param completeEnterGroupTime
     * @param executionCycleDTOHashMap
     */
    private void computePatientPlanExecuted(Long patientId, LocalDateTime completeEnterGroupTime,
                                            List<Plan> planList,
                                            Map<LocalDate, FollowAllExecutionCycleDTO> executionCycleDTOHashMap) {
        PlanService planService = getPlanService();
        if (CollUtil.isEmpty(planList)) {
            return;
        }
        List<Long> planIds = planList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        List<Plan> planAndDetails = planService.getPlanAndDetails(planIds);
        for (Plan plan : planAndDetails) {
            Integer execute = plan.getExecute();
            Integer planType = plan.getPlanType();
            Integer effectiveTime = plan.getEffectiveTime();
            List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
            if (CollUtil.isEmpty(planDetailList)) {
                return;
            }

            for (PlanDetailDTO detailDTO : planDetailList) {
                List<PlanDetailTime> planDetailTimes = detailDTO.getPlanDetailTimes();
                if (CollUtil.isEmpty(planDetailTimes)) {
                    return;
                }
                Integer pushType = detailDTO.getPushType();
                String hrefUrl = null;
                if (pushType != null && pushType == 2) {
                    hrefUrl = detailDTO.getContent();
                }
                String cmsTitle = null;
                Long cmsId = null;
                for (PlanDetailTime planDetailTime : planDetailTimes) {
                    Integer preTime = planDetailTime.getPreTime();
                    if (Objects.isNull(preTime)) {
                        continue;
                    }
                    LocalDateTime firstPushTime = completeEnterGroupTime.plusDays(execute).plusDays(preTime);
                    // 如果是 复查提醒。 由于复查提醒开启了 患者自定义护理计划推送时间。所以这里要处理一下
                    if (PlanEnum.REVIEW_REMIND.getCode().equals(plan.getPlanType())) {
                        PatientCustomPlanTime customPlan = getPatientNursingPlanService().getPatientCustomPlan(patientId, plan.getId(), detailDTO.getId(), planDetailTime.getId());
                        if (Objects.nonNull(customPlan)) {
                            LocalDateTime nextRemindTime = customPlan.getNextRemindTime();
                            // 判断自定义的推送时间是否 小于现在。 是否超过今天之后的30天
                            if (checkNextRemindTimeRight(nextRemindTime, firstPushTime, 0, 30)) {
                                LocalDate localDate = nextRemindTime.toLocalDate();
                                FollowAllExecutionCycleDTO executionCycleDTO = executionCycleDTOHashMap.get(localDate);
                                if (Objects.isNull(executionCycleDTO)) {
                                    executionCycleDTO = new FollowAllExecutionCycleDTO(localDate);
                                    executionCycleDTOHashMap.put(localDate, executionCycleDTO);
                                }
                                FollowAllPlanDetailDTO followAllPlanDetailDTO = new FollowAllPlanDetailDTO(planType, plan.getId(), plan.getName(), nextRemindTime.toLocalTime());
                                executionCycleDTO.addPLanDetailsNotLearnPlan(followAllPlanDetailDTO, cmsId, hrefUrl, cmsTitle, nextRemindTime.toLocalTime());
                            }
                            break;
                        }
                    }
                    Integer frequency = planDetailTime.getFrequency();
                    LocalDate currentDay = LocalDate.now();
                    LocalTime time = planDetailTime.getTime();
                    LocalDateTime nextRemindTime = getPatientNursingPlanService().getPatientNextRemindTime(completeEnterGroupTime, planDetailTime, execute, frequency, currentDay, effectiveTime);
                    while (checkNextRemindTimeRight(nextRemindTime, firstPushTime, effectiveTime, 30)) {
                        LocalDate localDate = nextRemindTime.toLocalDate();
                        FollowAllExecutionCycleDTO executionCycleDTO = executionCycleDTOHashMap.get(localDate);
                        if (Objects.isNull(executionCycleDTO)) {
                            executionCycleDTO = new FollowAllExecutionCycleDTO(localDate);
                            executionCycleDTOHashMap.put(localDate, executionCycleDTO);
                        }
                        FollowAllPlanDetailDTO followAllPlanDetailDTO = new FollowAllPlanDetailDTO(planType, plan.getId(), plan.getName(), time);
                        // 学习计划默认为单词推送。所以这里不优化
                        boolean exit = false;
                        if (planType != null && planType.equals(PlanEnum.LEARN_PLAN.getCode())) {
                            PlanDetailsDTO cmsInfo = getCmsInfo(planDetailTime);
                            if (Objects.isNull(cmsInfo)) {
                                exit = true;
                            } else {
                                hrefUrl = cmsInfo.getHrefUrl();
                                cmsTitle = cmsInfo.getFirstShowTitle();
                                cmsId = cmsInfo.getCmsId();
                            }
                        }
                        executionCycleDTO.addPLanDetailsNotLearnPlan(followAllPlanDetailDTO, cmsId, hrefUrl, cmsTitle, time);

                        // 如果是单次间隔，执行一次后，不再执行
                        if (frequency == 0 || exit) {
                            break;
                        }
                        nextRemindTime = nextRemindTime.plusDays(frequency);
                    }
                }
            }
        }



    }

    /**
     * 检查生产的 未来推送时间。是否符合要求
     *  1. 下次推送时间 必须是在今天 或者今天以后
     *  2. 下次推送时间不能超过当前日期30天。
     *  3. 下次推送时间 与 患者注册日期 之差不能超过计划的有效时间
     * @param nextRemindTime
     * @param effectiveTime
     * @return
     */
    public boolean checkNextRemindTimeRight(LocalDateTime nextRemindTime, LocalDateTime startPushDay, Integer effectiveTime, Integer maxDay) {
        // 1. 下次推送时间 必须是在今天 或者今天以后
        // 2. 下次推送时间不能超过当前日期30天。
        // 3. 下次推送时间 与 患者注册日期 之差不能超过计划的有效时间
        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        if (nextRemindTime == null) {
            return false;
        }
        LocalDateTime nextRemind = nextRemindTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

        if (now.isEqual(nextRemind) || nextRemind.isAfter(now)) {
        } else {
            return false;
        }
        Duration duration = Duration.between(now, nextRemind);
        long days = duration.toDays();
        if (Objects.nonNull(maxDay)) {
            if (days > 30) {
                return false;
            }
        }
        if (effectiveTime > 0) {
            days = DateUtils.getDays(nextRemind.toLocalDate(), startPushDay.toLocalDate());
            if (days >= effectiveTime) {
                return false;
            }
        }
        return true;

    }

    /**
     * 获取标签下的护理计划。
     * @param tagIdList
     * @return
     */
    public List<Long> getHasTagPlanIds(List<Long> tagIdList) {
        List<Long> hasTagPlanIds = new ArrayList<>();
        List<PlanTag> planTags = planTagMapper.selectList(Wraps.<PlanTag>lbQ().in(PlanTag::getTagId, tagIdList));
        if (CollUtil.isNotEmpty(planTags)) {
            for (PlanTag planTag : planTags) {
                hasTagPlanIds.add(planTag.getNursingPlanId());
            }
        }
        // 查询 没有设置触发对象的护理计划
        List<Plan> planList = planService.list(Wraps.<Plan>lbQ().apply(" id not in (select nursing_plan_id from t_nursing_plan_tag) ").select(SuperEntity::getId, Plan::getName));
        if (CollUtil.isNotEmpty(planList)) {
            planList.forEach(item -> hasTagPlanIds.add(item.getId()));
        }
        return hasTagPlanIds;
    }

    /**
     * 查询随访计划的 未来计划目录
     * @return
     */
    @Override
    public FollowAllPlanDTO findFollowPlan(Integer currentPage, List<Long> tagIdList) {

        LbqWrapper<FollowTaskContent> wrapper = Wraps.<FollowTaskContent>lbQ()
                .eq(FollowTaskContent::getPlanStatus, 1)
                .eq(FollowTaskContent::getShowContent, FollowContentShow.FOLLOW_CONTENT_SHOW)
                .orderByDesc(FollowTaskContent::getContentSort);
        List<Long> hasTagPlanIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(tagIdList)) {
            hasTagPlanIds = getHasTagPlanIds(tagIdList);
            // 这个条件会导致学习计划查不到
            if (CollUtil.isNotEmpty(hasTagPlanIds)) {
                wrapper.in(FollowTaskContent::getPlanId, hasTagPlanIds);
            }
        }
        FollowAllPlanDTO allPlanDTO = new FollowAllPlanDTO();
        List<FollowTaskContent> tempTaskContents = followTaskContentService.list(wrapper);
        List<FollowTaskContent> taskContents = new ArrayList<>();
        List<Long> removePlanButOpenId = functionConfigurationService.getRemovePlanButOpenId();
        if (CollUtil.isNotEmpty(tempTaskContents)) {
            tempTaskContents.forEach(item -> {
                if (!removePlanButOpenId.contains(item.getPlanId())) {
                    taskContents.add(item);
                }
            });
        }

        if (CollUtil.isNotEmpty(hasTagPlanIds)) {
            FollowTaskContent learnPlanTask = followTaskContentService.getOne(Wraps.<FollowTaskContent>lbQ()
                    .eq(FollowTaskContent::getShowContent, FollowContentShow.FOLLOW_CONTENT_SHOW)
                    .eq(FollowTaskContent::getPlanStatus, 1)
                    .eq(FollowTaskContent::getPlanType, PlanEnum.LEARN_PLAN.getCode()));
            // 查询学习计划的随访任务是否开启
            if (learnPlanTask != null) {
                taskContents.add(learnPlanTask);
            }
        }
        PlanService planService = getPlanService();
        Map<Long, Plan> planMap;
        List<Plan> planList = new ArrayList<>(taskContents.size());
        String timeFrame = "";
        Map<Long, FollowTaskContent> taskContentMap = new HashMap<>();
        FollowTaskContent learnPlanFollow = null;
        // 根据随访任务设置，查询对应的护理计划，监测计划，学习计划，
        Integer currentMaxDay = currentPage * 90;
        Integer currentMinDay = (currentPage - 1) * 90;
        Integer maxPlanDay = 0;
        if (CollUtil.isNotEmpty(taskContents)) {
            // 查询随访任务内容的护理计划
            planMap = new HashMap<>();
            List<Long> planIds = taskContents.stream().filter(item -> item.getPlanId() != null).map(FollowTaskContent::getPlanId).collect(Collectors.toList());
            List<Plan> planAndDetails = planService.getPlanAndDetails(planIds);
            Map<Long, Plan> longPlanMap = new HashMap<>();
            if (CollUtil.isNotEmpty(planAndDetails)) {
                longPlanMap = planAndDetails.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o2));
            }
            for (FollowTaskContent taskContent : taskContents) {
                String tempTimeFrame = taskContent.getTimeFrame();
                timeFrame = getMaxTimeFrame(timeFrame, tempTimeFrame);
                if (taskContent.getPlanId() != null) {
                    taskContentMap.put(taskContent.getPlanId(), taskContent);
                    Plan plan = longPlanMap.get(taskContent.getPlanId());
                    if (Objects.nonNull(plan)) {
                        planMap.put(plan.getId(), plan);
                        planList.add(plan);
                    }
                } else if (taskContent.getPlanType() != null){
                    List<Plan> learnPlan = planService.getPlanAndDetailsByPlanType(taskContent.getPlanType(), UserType.UCENTER_PATIENT, 1, hasTagPlanIds);
                    if (taskContent.getPlanType().equals(PlanEnum.LEARN_PLAN.getCode())) {
                        learnPlanFollow = taskContent;
                    }
                    if (CollUtil.isNotEmpty(learnPlan)) {
                        for (Plan plan : learnPlan) {
                            planMap.put(plan.getId(), plan);
                            planList.add(plan);
                        }
                    }
                }
            }
            if (CollUtil.isEmpty(planMap)) {
                return allPlanDTO;
            }
            // 计算这些计划在 随访设置的时间范围内，执行的日期 时间。
            // 循环这些计划， 计算这些计划在未来的执行日期 时间。
            Map<Integer, FollowAllExecutionCycleDTO> allExecutionCycleDTOMap = new HashMap<>();
            String hrefUrl = null;
            String cmsTitle = "";
            Long cmsId = null;
            for (Plan plan : planList) {
                Integer tempCurrentMaxDay = currentMaxDay;
                Integer planType = plan.getPlanType();
                Integer execute = plan.getExecute();
                Integer effectiveTime = plan.getEffectiveTime();
                List<PlanDetailDTO> planDetailList = plan.getPlanDetailList();
                if (CollUtil.isEmpty(planDetailList)) {
                    continue;
                }
                for (PlanDetailDTO planDetailDTO : planDetailList) {
                    hrefUrl = null;
                    Integer type = planDetailDTO.getType();
                    Integer pushType = planDetailDTO.getPushType();
                    if (pushType != null && pushType == 2) {
                        hrefUrl = planDetailDTO.getContent();
                    }
                    // 学习计划单独处理。
                    if (planType != null && planType.equals(PlanEnum.LEARN_PLAN.getCode())) {
                        List<PlanDetailTime> planDetailTimes = planDetailDTO.getPlanDetailTimes();
                        if (CollUtil.isEmpty(planDetailTimes)) {
                            continue;
                        }
                        // 学习计划 详细的推送设置
                        for (PlanDetailTime detailTime : planDetailTimes) {
                            Integer preTime = detailTime.getPreTime();
                            if (Objects.isNull(preTime)) {
                                continue;
                            }
                            LocalTime localTime = detailTime.getTime();
                            int key = execute + preTime;
                            if (key >= currentMinDay && key < tempCurrentMaxDay) {
                                // 学习计划如果是推送的外部的文章。取外部文章的标题，和url
                                PlanDetailsDTO detailsDTO = getCmsInfo(detailTime);
                                if (Objects.isNull(detailsDTO)) {
                                    continue;
                                } else {
                                    hrefUrl = detailsDTO.getHrefUrl();
                                    cmsTitle = detailsDTO.getFirstShowTitle();
                                    cmsId = detailsDTO.getCmsId();
                                }
                                FollowAllExecutionCycleDTO executionCycleDTO = allExecutionCycleDTOMap.get(key);
                                if (executionCycleDTO == null) {
                                    executionCycleDTO = new FollowAllExecutionCycleDTO();
                                    allExecutionCycleDTOMap.put(key, executionCycleDTO);
                                    executionCycleDTO.setPlanExecutionDay(key);
                                }
                                FollowAllPlanDetailDTO detailDTO = new FollowAllPlanDetailDTO();
                                detailDTO.setPlanType(PlanEnum.LEARN_PLAN.getCode());
                                detailDTO.setPlanId(plan.getId());
                                if (learnPlanFollow != null) {
                                    detailDTO.setFirstShowTitle(learnPlanFollow.getShowName());
                                } else {
                                    detailDTO.setFirstShowTitle(PlanEnum.LEARN_PLAN.getDesc());
                                }
                                detailDTO.setPlanExecutionDate(localTime);
                                executionCycleDTO.addPLanDetailsNotLearnPlan(detailDTO, cmsId, hrefUrl, cmsTitle, localTime);
                            }
                        }
                    } else {
                        FollowTaskContent taskContent = taskContentMap.get(plan.getId());
                        Integer maxEffectiveTime = getMaxEffectiveTime(effectiveTime, taskContent.getTimeFrame());
                        if (maxPlanDay < maxEffectiveTime) {
                            maxPlanDay = maxEffectiveTime;
                        }
                        // 非学习计划的。 只处理 推送类型为模板的，或者点击模板跳转链接的
                        if (type != null && (type == 1 || type == 3)) {
                            List<PlanDetailTime> planDetailTimes = planDetailDTO.getPlanDetailTimes();
                            for (PlanDetailTime detailTime : planDetailTimes) {
                                Integer frequency = detailTime.getFrequency();
                                Integer preTime = detailTime.getPreTime();
                                if (Objects.isNull(preTime)) {
                                    continue;
                                }
                                LocalTime localTime = detailTime.getTime();
                                // 将计划的执行 天 作为key。 处理天对应多少计划。
                                int key = execute + preTime;
                                FollowAllPlanDetailDTO detailDTO = new FollowAllPlanDetailDTO();
                                detailDTO.setPlanId(plan.getId());
                                detailDTO.setPlanType(plan.getPlanType());
                                detailDTO.setFirstShowTitle(taskContent.getShowName());
                                detailDTO.setPlanExecutionDate(localTime);
                                FollowAllExecutionCycleDTO executionCycleDTO;
                                if (key > currentMinDay && key <= tempCurrentMaxDay) {
                                    executionCycleDTO = allExecutionCycleDTOMap.get(key);
                                    if (executionCycleDTO == null) {
                                        executionCycleDTO = new FollowAllExecutionCycleDTO();
                                        allExecutionCycleDTOMap.put(key, executionCycleDTO);
                                        executionCycleDTO.setPlanExecutionDay(key);
                                    }
                                    executionCycleDTO.addPLanDetailsNotLearnPlan(detailDTO, null, hrefUrl, detailTime.getCmsTitle(), localTime);
                                }
                                // 如果推送不是单次的，计算在未来。有多少次推送
                                int newKey = key + frequency;
                                if (frequency > 0) {
                                    if (tempCurrentMaxDay > maxEffectiveTime) {
                                        tempCurrentMaxDay = maxEffectiveTime;
                                    }
                                    // 将 newKey 推算到 下一轮的执行计划日期
                                    while (newKey <= currentMinDay) {
                                        newKey+= frequency;
                                    }
                                    while (tempCurrentMaxDay >= newKey) {
                                        executionCycleDTO = allExecutionCycleDTOMap.get(newKey);
                                        if (executionCycleDTO == null) {
                                            executionCycleDTO = new FollowAllExecutionCycleDTO();
                                            allExecutionCycleDTOMap.put(newKey, executionCycleDTO);
                                            executionCycleDTO.setPlanExecutionDay(newKey);
                                        }
                                        detailDTO = new FollowAllPlanDetailDTO();
                                        detailDTO.setPlanId(plan.getId());
                                        detailDTO.setPlanType(plan.getPlanType());
                                        detailDTO.setFirstShowTitle(taskContent.getShowName());
                                        detailDTO.setPlanExecutionDate(localTime);
                                        executionCycleDTO.addPLanDetailsNotLearnPlan(detailDTO, null, hrefUrl, detailTime.getCmsTitle(), localTime);
                                        newKey += frequency;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (StrUtil.isEmpty(timeFrame)) {
                timeFrame = FollowContentTimeFrame.HALF_YEAR;
            }
            Collection<FollowAllExecutionCycleDTO> cycleDTOCollection = allExecutionCycleDTOMap.values();
            List<FollowAllExecutionCycleDTO> allExecutionCycleDTOS = new ArrayList<>(cycleDTOCollection);
            allPlanDTO.setTimeFrame(timeFrame);
            allPlanDTO.setMaxPlanDay(maxPlanDay);
            for (FollowAllExecutionCycleDTO executionCycleDTO : allExecutionCycleDTOS) {
                executionCycleDTO.setDetailDTOMap(null);
                executionCycleDTO.sort();
            }
            allExecutionCycleDTOS.sort((o1, o2) -> {
                if (o1.getPlanExecutionDay() > o2.getPlanExecutionDay()) {
                    return 1;
                } else {
                    return -1;
                }
            });
            allPlanDTO.setPlanExecutionCycles(allExecutionCycleDTOS);

            return allPlanDTO;
        } else {
            return allPlanDTO;
        }
    }
}
