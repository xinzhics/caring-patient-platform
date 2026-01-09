package com.caring.sass.nursing.service.plan.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.nursing.dao.plan.PlanDetailMapper;
import com.caring.sass.nursing.dao.plan.PlanDetailTimeMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dao.plan.ReminderLogMapper;
import com.caring.sass.nursing.dao.unfinished.UnfinishedPatientRecordMapper;
import com.caring.sass.nursing.dto.follow.CmsPushReadDetail;
import com.caring.sass.nursing.dto.follow.FollowCountOtherPlanDTO;
import com.caring.sass.nursing.dto.follow.PatientFollowLearnPlanStatisticsDTO;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsResult;
import com.caring.sass.nursing.dto.statistics.TenantStatisticsYData;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.entity.plan.PlanDetail;
import com.caring.sass.nursing.entity.plan.PlanDetailTime;
import com.caring.sass.nursing.entity.plan.ReminderLog;
import com.caring.sass.nursing.entity.unfinished.UnfinishedPatientRecord;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.plan.ReminderLogService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @ClassName ReminderLogServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2020/10/27 13:39
 * @Version 1.0
 */
@Service
public class ReminderLogServiceImpl extends SuperServiceImpl<ReminderLogMapper, ReminderLog> implements ReminderLogService {


    @Autowired
    PatientApi patientApi;

    @Autowired
    PlanDetailTimeMapper planDetailTimeMapper;

    @Autowired
    PlanDetailMapper planDetailMapper;

    @Autowired
    PlanMapper planMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UnfinishedPatientRecordMapper unfinishedPatientRecordMapper;

    // 由于 一个 护理计划的推送时间 一天 设定为 只给患者推送一次。
    // 此业务可以根据 planDetailTimeId + PatientId 记录到redis中。增加随访计划推送时，判断是否已打卡的效率
    // 采用 hash 设计。 租户+日期 作为key。  planDetailTimeId + PatientId 为field ， value 默认 1

    @Override
    public void sendSuccess(Long id) {
        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setStatus(1);
        reminderLog.setPushTimeSuccess(LocalDateTime.now());
        reminderLog.setId(id);
        baseMapper.updateById(reminderLog);
    }


    @Override
    public void submitSuccess(Long id) {
        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setFinishThisCheckIn(1);
        reminderLog.setId(id);
        baseMapper.updateById(reminderLog);

        // 完成打卡，清除 未完成任务跟踪的待处理记录
        unfinishedPatientRecordMapper.delete(Wraps.<UnfinishedPatientRecord>lbQ()
                .eq(UnfinishedPatientRecord::getRemindMessageId, id)
                .eq(UnfinishedPatientRecord::getHandleStatus, CommonStatus.NO));
    }

    /**
     * 将 给患者推送打卡消息  记录到redis中。防止每日多次推送打卡
     * @param tenantCode
     * @param workId
     * @param patientId
     */
    private void setReminderLogRedis(String tenantCode, String workId, Long patientId) {
        LocalDate now = LocalDate.now();
        String string = now.toString();
        String hashKey = tenantCode + string;
        Boolean hasKey = redisTemplate.hasKey(hashKey);
        // key 存在。
        String fieldKey = workId + ":" + patientId;
        redisTemplate.opsForHash().put(hashKey, fieldKey, "1");
        if (hasKey == null || !hasKey) {
            redisTemplate.expire(hashKey, 24, TimeUnit.HOURS);
        }
    }

    private boolean existReminderLogRedis(String tenantCode, String workId, Long patientId) {
        LocalDate now = LocalDate.now();
        String string = now.toString();
        String hashKey = tenantCode + string;
        // key 存在。
        String fieldKey = workId + ":" + patientId;
        Object o = redisTemplate.opsForHash().get(hashKey, fieldKey);
        if (Objects.isNull(o)) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * 打开外部连接时， 完成打卡，完成查看消息
     * @param messageId
     */
    @Override
    public void externalMessages(Long messageId) {
        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setFinishThisCheckIn(1);
        reminderLog.setOpenThisMessage(1);
        reminderLog.setId(messageId);
        baseMapper.updateById(reminderLog);
    }

    @Override
    public Long createOrUpdateReminderLog(Long planDetailTimeId, Long patientId, Long messageId) {

        return createOrUpdateReminderLog(planDetailTimeId, null, patientId, messageId);

    }


    /**
     * 检查这个随访计划的 推送时间，今天是否给患者推送过。
     * @param planDetailTimeId
     * @param patientId
     * @return
     */
    @Override
    public boolean checkReminderLogExits(String tenantCode, Long planDetailTimeId, String workId, Long patientId) {
        if (StrUtil.isEmpty(workId)) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(planDetailTimeId.toString());
            workId = JSONUtil.toJsonStr(jsonArray);
        }
        if (existReminderLogRedis(tenantCode, workId, patientId)) {
            return true;
        };

        LocalDate now = LocalDate.now();
        Integer count = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .eq(ReminderLog::getPatientId, patientId)
                .eq(ReminderLog::getStatus, 1)
                .eq(ReminderLog::getWorkId, workId)
                .lt(SuperEntity::getCreateTime, LocalDateTime.of(now, LocalTime.of(23, 59, 59)))
                .gt(SuperEntity::getCreateTime, LocalDateTime.of(now, LocalTime.of(0, 0, 0))));
        if (count == null || count == 0) {
            return false;
        } else {
            return true;
        }

    }


    @Override
    public Long queryRecentlyUnReadMessage(Long patientId, Long planId) {

        ReminderLog reminderLog = baseMapper.selectOne(Wraps.<ReminderLog>lbQ()
                .select(SuperEntity::getId, ReminderLog::getPatientId)
                .eq(ReminderLog::getPatientId, patientId)
                .eq(ReminderLog::getFinishThisCheckIn, CommonStatus.NO)
                .eq(ReminderLog::getPlanId, planId)
                .orderByDesc(SuperEntity::getCreateTime)
                .last("limit 1"));
        if (reminderLog == null) {
            return null;
        }
        return reminderLog.getId();
    }

    /**
     * 患者提前打卡。或者通过 待办 进入业务后完成打卡
     * @param planDetailTimeId
     * @param planEnum
     * @param patientId
     * @param messageId
     */
    @Override
    public Long createOrUpdateReminderLog(Long planDetailTimeId, PlanEnum planEnum, Long patientId, Long messageId) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(planDetailTimeId.toString());
        String workIdJson = JSONUtil.toJsonStr(jsonArray);
        ReminderLog reminderLog = null;
        if (Objects.nonNull(messageId)) {
            reminderLog = baseMapper.selectById(messageId);
        }
        String tenant = BaseContextHandler.getTenant();
        LocalDate now = LocalDate.now();
        if (reminderLog == null) {
            reminderLog = baseMapper.selectOne(Wraps.<ReminderLog>lbQ()
                    .eq(ReminderLog::getPatientId, patientId)
                    .eq(ReminderLog::getWorkId, workIdJson)
                    .lt(SuperEntity::getCreateTime, LocalDateTime.of(now, LocalTime.of(23,59,59)))
                    .gt(SuperEntity::getCreateTime, LocalDateTime.of(now, LocalTime.of(0,0,0))));
        }
        if (reminderLog == null) {
            R<Patient> patientR = patientApi.getBaseInfoForStatisticsData(patientId);
            if (patientR.getIsSuccess()) {
                Patient patient = patientR.getData();
                if (Objects.nonNull(patient)) {
                    PlanDetailTime detailTime = planDetailTimeMapper.selectById(planDetailTimeId);
                    if (detailTime == null) {
                        return null;
                    }
                    PlanDetail planDetail = planDetailMapper.selectById(detailTime.getNursingPlanDetailId());
                    if (planDetail == null) {
                        return null;
                    }
                    Integer planType = null;
                    if (planEnum == null) {
                        Plan plan = planMapper.selectById(planDetail.getNursingPlanId());
                        planType = plan.getPlanType();
                    }
                    setReminderLogRedis(tenant, workIdJson, patientId);
                    reminderLog = new ReminderLog();
                    reminderLog.setPatientId(patientId);
                    reminderLog.setDoctorId(patient.getDoctorId());
                    reminderLog.setNursingId(patient.getServiceAdvisorId());
                    reminderLog.setComment("提前打卡");
                    reminderLog.setWorkId(workIdJson);
                    if (planType == null) {
                        reminderLog.setType(0);
                    } else {
                        reminderLog.setType(planType);
                    }
                    reminderLog.setStatus(1);
                    reminderLog.setFinishThisCheckIn(1);
                    reminderLog.setCreateTime(LocalDateTime.now());
                    reminderLog.setOrgId(patient.getOrganId());
                    reminderLog.setClassCode(patient.getClassCode());
                    reminderLog.setOpenThisMessage(1);
                    reminderLog.setPlanId(planDetail.getNursingPlanId());
                    baseMapper.insert(reminderLog);
                    return reminderLog.getId();
                }
            }
        } else {
            reminderLog.setFinishThisCheckIn(1);
            reminderLog.setOpenThisMessage(1);
            baseMapper.updateById(reminderLog);
            return reminderLog.getId();
        }
        return null;
    }

    /**
     * 创建一个 将来执行的 推送任务
     * @param patientId
     * @param comment
     * @param workId
     * @param type
     * @param planId
     * @param orgId
     * @param classCode
     * @param doctorId
     * @param nursingId
     * @param waitPushTime
     * @return
     */
    @Override
    public ReminderLog createReminderLog(Long patientId, String comment, String workId, Integer type, Long planId, Long orgId, String classCode,
                                         Long doctorId, Long nursingId, LocalDateTime waitPushTime, Long planDetailId) {

        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setPatientId(patientId);
        reminderLog.setDoctorId(doctorId);
        reminderLog.setNursingId(nursingId);
        reminderLog.setComment(comment);
        reminderLog.setWorkId(workId);
        reminderLog.setType(type);
        reminderLog.setStatus(-1);
        reminderLog.setFinishThisCheckIn(0);
        reminderLog.setCreateTime(LocalDateTime.now());
        reminderLog.setOrgId(orgId);
        reminderLog.setClassCode(classCode);
        reminderLog.setOpenThisMessage(0);
        reminderLog.setPlanId(planId);
        reminderLog.setPlanDetailId(planDetailId);
        reminderLog.setWaitPushTime(waitPushTime.withSecond(0).withNano(0));
        baseMapper.insert(reminderLog);
        return reminderLog;
    }

    @Override
    public ReminderLog createReminderLog(Long patientId, String comment, String workId, Integer type, Long planId,
                                         Long orgId, String classCode, Long doctorId, Long nursingId) {
        String tenant = BaseContextHandler.getTenant();
        setReminderLogRedis(tenant, workId, patientId);
        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setPatientId(patientId);
        reminderLog.setDoctorId(doctorId);
        reminderLog.setNursingId(nursingId);
        reminderLog.setComment(comment);
        reminderLog.setWorkId(workId);
        reminderLog.setType(type);
        reminderLog.setStatus(0);
        reminderLog.setFinishThisCheckIn(0);
        reminderLog.setCreateTime(LocalDateTime.now());
        reminderLog.setOrgId(orgId);
        reminderLog.setClassCode(classCode);
        reminderLog.setOpenThisMessage(0);
        reminderLog.setPlanId(planId);
        reminderLog.setWaitPushTime(LocalDateTime.now().withSecond(0).withNano(0));
        baseMapper.insert(reminderLog);
        return reminderLog;
    }


    @Override
    public void openMessage(Long messageId) {

        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setId(messageId);
        reminderLog.setOpenThisMessage(1);
        baseMapper.updateById(reminderLog);
    }


    /**
     * 根据推送时间。去查询存在推送任务的租户
     * @param localDateTime
     * @return
     */
    @Override
    public List<String> queryNeedSendLog(LocalDateTime localDateTime) {
        return baseMapper.selectTenantCodeByNeedPushTime(localDateTime);
    }

    /**
     * 医生统计名下患者 学习计划的推送打卡情况
     * @param doctorId
     * @param map
     */
    @Override
    public void doctorStatisticsData(Long doctorId, Map<String, CmsPushReadDetail> map) {
        // 统计 map 中 key 的送达人数
        if (CollUtil.isEmpty(map)) {
            return;
        }
        Set<String> wordIds = map.keySet();
        QueryWrap<ReminderLog> pushUserNumberWrap = Wraps.<ReminderLog>q()
                .in("work_id", wordIds)
                .eq("status_", 1)
                .eq("doctor_id", doctorId)
                .groupBy("work_id")
                .select("work_id, count(id) as countNum");

        QueryWrap<ReminderLog> readUserNumberWrap = Wraps.<ReminderLog>q()
                .in("work_id", wordIds)
                .eq("finish_this_check_in", 1)
                .eq("doctor_id", doctorId)
                .groupBy("work_id")
                .select("work_id, count(id) as countNum");
        // 送达人数统计
        List<Map<String, Object>> pushResultMapList = baseMapper.selectMaps(pushUserNumberWrap);
        // 阅读人数统计
        List<Map<String, Object>> readResultMapList = baseMapper.selectMaps(readUserNumberWrap);
        getStatisticsResult(pushResultMapList, readResultMapList, wordIds, map);

    }

    /**
     * 患者统计多个work的推送打开情况
     * @param patientId 患者ID
     * @param workIds 工作ID
     * @param statisticsDTO 统计数据
     */
    @Override
    public void patientStatistics(Long patientId, List<String> workIds, PatientFollowLearnPlanStatisticsDTO statisticsDTO) {

        // 已推送数量
        QueryWrap<ReminderLog> pushUserNumberWrap = Wraps.<ReminderLog>q()
                .in("work_id", workIds)
                .eq("status_", 1)
                .eq("patient_Id", patientId)
                .select("DISTINCT work_id");

        QueryWrap<ReminderLog> readUserNumberWrap = Wraps.<ReminderLog>q()
                .in("work_id", workIds)
                .eq("finish_this_check_in", 1)
                .eq("patient_Id", patientId)
                .select("DISTINCT work_id");
        // 已经推送了几篇
        Integer workPushCount = baseMapper.selectCount(pushUserNumberWrap);

        // 阅读了几篇
        Integer readCount = baseMapper.selectCount(readUserNumberWrap);

        statisticsDTO.setReceiveNumber(workPushCount);
        statisticsDTO.setReadNumber(readCount);
        if (workPushCount >= readCount) {
            statisticsDTO.setNoReadNumber(workPushCount - readCount);
            int proportion = BigDecimalUtils.proportion(new BigDecimal(readCount), new BigDecimal(workPushCount));
            statisticsDTO.setReadingRate(proportion);
        }
    }

    /**
     * 患者统计其他的护理计划的推送
     * @param patientId
     * @param workIds
     * @param planDTO
     */
    @Override
    public void patientStatistics(Long patientId, List<String> workIds, FollowCountOtherPlanDTO planDTO) {

        QueryWrap<ReminderLog> pushUserNumberWrap = Wraps.<ReminderLog>q()
                .in("work_id", workIds)
                .eq("status_", 1)
                .eq("patient_Id", patientId);
        Integer workPushCount = baseMapper.selectCount(pushUserNumberWrap);

        QueryWrap<ReminderLog> readUserNumberWrap = Wraps.<ReminderLog>q()
                .in("work_id", workIds)
                .eq("finish_this_check_in", 1)
                .eq("patient_Id", patientId);

        Integer readCount = baseMapper.selectCount(readUserNumberWrap);
        planDTO.setPushNumber(workPushCount);
        planDTO.setCompleteNumber(readCount);
        if (workPushCount >= readCount) {
            planDTO.setNoCompleteNumber(workPushCount - readCount);
            int proportion = BigDecimalUtils.proportion(new BigDecimal(readCount), new BigDecimal(workPushCount));
            planDTO.setCompleteRate(proportion);
        }
    }



    /**
     * 处理 医生 医助 统计的学习计划推送情况
     * @param pushResultMapList
     * @param readResultMapList
     * @param wordIds
     * @param map
     */
    private void getStatisticsResult(List<Map<String, Object>> pushResultMapList, List<Map<String, Object>> readResultMapList,
                                     Set<String> wordIds,
                                     Map<String, CmsPushReadDetail> map) {
        Map<String, Object> allPushTmpMap = new HashMap<>();
        Map<String, Object> readPushTmpMap = new HashMap<>();
        for (Map<String, Object> t : pushResultMapList) {
            allPushTmpMap.put(Convert.toStr(t.get("work_id")), t.get("countNum"));
        }
        for (Map<String, Object> t : readResultMapList) {
            readPushTmpMap.put(Convert.toStr(t.get("work_id")), t.get("countNum"));
        }
        for (String wordId : wordIds) {
            Object pushNumberObj = allPushTmpMap.get(wordId);
            Object readNumberObj = readPushTmpMap.get(wordId);
            CmsPushReadDetail pushReadDetail = map.get(wordId);
            if (pushNumberObj != null) {
                // 送达人数
                pushReadDetail.setPushUserNumber(Integer.parseInt(pushNumberObj.toString()));
            }
            if (readNumberObj != null) {
                // 阅读人数
                pushReadDetail.setReadUserNumber(Integer.parseInt(readNumberObj.toString()));
            }
            int pushUserNumber = pushReadDetail.getPushUserNumber();
            int readUserNumber = pushReadDetail.getReadUserNumber();
            if (pushUserNumber >= readUserNumber) {
                pushReadDetail.setNoReadUserNumber(pushUserNumber - readUserNumber);
                int proportion = BigDecimalUtils.proportion(new BigDecimal(readUserNumber), new BigDecimal(pushUserNumber));
                pushReadDetail.setReadingRate(proportion);
            }

        }
    }

    /**
     * 医助统计 机构下 学习计划 的推送 打卡 情况
     * @param orgIds
     * @param map
     */
    @Override
    public void statisticsData(List<Long> orgIds, Map<String, CmsPushReadDetail> map) {

        // 统计 map 中 key 的送达人数
        if (CollUtil.isEmpty(map)) {
            return;
        }
        Set<String> wordIds = map.keySet();
        if (CollUtil.isEmpty(orgIds)) {
            return;
        }
        QueryWrap<ReminderLog> pushUserNumberWrap = Wraps.<ReminderLog>q()
                .in("work_id", wordIds)
                .in("org_id", orgIds)
                .eq("status_", 1)
                .groupBy("work_id")
                .select("work_id, count(id) as countNum");

        QueryWrap<ReminderLog> readUserNumberWrap = Wraps.<ReminderLog>q()
                .in("work_id", wordIds)
                .eq("finish_this_check_in", 1)
                .in("org_id", orgIds)
                .groupBy("work_id")
                .select("work_id, count(id) as countNum");
        // 送达人数统计
        List<Map<String, Object>> pushResultMapList = baseMapper.selectMaps(pushUserNumberWrap);
        // 阅读人数统计
        List<Map<String, Object>> readResultMapList = baseMapper.selectMaps(readUserNumberWrap);
        getStatisticsResult(pushResultMapList, readResultMapList, wordIds, map);

    }

    /**
     * 医生统计其他护理计划推送的 次数 打卡情况
     * @param doctorId
     * @param reminderLogWorkId
     * @param followCountOtherPlanDTO
     */
    @Override
    public void doctorStatisticsData(Long doctorId, String reminderLogWorkId, FollowCountOtherPlanDTO followCountOtherPlanDTO) {
        // 送达人数 根据人数分组
        Integer pushUserAll = baseMapper.selectCount(Wraps.<ReminderLog>q()
                .eq("work_id", reminderLogWorkId)
                .eq("doctor_id", doctorId)
                .eq("status_", 1)
                .select("DISTINCT patient_Id"));
        followCountOtherPlanDTO.setPushUserNumber(pushUserAll == null ? 0 : pushUserAll);

        Integer pushUserOpenMessage = baseMapper.selectCount(Wraps.<ReminderLog>q()
                .eq("work_id", reminderLogWorkId)
                .eq("open_this_message", 1)
                .eq("doctor_id", doctorId)
                .select("DISTINCT patient_Id"));
        followCountOtherPlanDTO.setOpenUserNumber(pushUserOpenMessage == null ? 0 : pushUserOpenMessage);

        // 推送的次数
        Integer pushFrequencyAll = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .eq(ReminderLog::getWorkId, reminderLogWorkId)
                .eq(ReminderLog::getDoctorId, doctorId));
        followCountOtherPlanDTO.setPushNumber(pushFrequencyAll == null ? 0 : pushFrequencyAll);

        // 已打完打卡的次数
        Integer pushFrequencyCompleteNumber = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .eq(ReminderLog::getWorkId, reminderLogWorkId)
                .eq(ReminderLog::getFinishThisCheckIn, 1)
                .eq(ReminderLog::getDoctorId, doctorId));
        followCountOtherPlanDTO.setCompleteNumber(pushFrequencyCompleteNumber);
        calculationRate(followCountOtherPlanDTO);

    }

    /**
     * 医生 自己的患者 血压血糖打开人数，推送人数， 完成人数，
     * @param doctorId
     * @param reminderLogWorkIdList
     * @param followCountOtherPlanDTO
     */
    @Override
    public void doctorStatisticsData(Long doctorId, List<String> reminderLogWorkIdList, FollowCountOtherPlanDTO followCountOtherPlanDTO) {
        // 送达人数 根据人数分组
        Integer pushUserAll = baseMapper.selectCount(Wraps.<ReminderLog>q()
                .in("work_id", reminderLogWorkIdList)
                        .eq("status_", 1)
                .eq("doctor_id", doctorId)
                .select("DISTINCT patient_Id"));
        followCountOtherPlanDTO.setPushUserNumber(pushUserAll == null ? 0 : pushUserAll);

        Integer pushUserOpenMessage = baseMapper.selectCount(Wraps.<ReminderLog>q()
                .in("work_id", reminderLogWorkIdList)
                .eq("open_this_message", 1)
                .eq("doctor_id", doctorId)
                .select("DISTINCT patient_Id"));
        followCountOtherPlanDTO.setOpenUserNumber(pushUserOpenMessage == null ? 0 : pushUserOpenMessage);

        // 推送的次数
        Integer pushFrequencyAll = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .in(ReminderLog::getWorkId, reminderLogWorkIdList)
                .eq(ReminderLog::getDoctorId, doctorId));
        followCountOtherPlanDTO.setPushNumber(pushFrequencyAll == null ? 0 : pushFrequencyAll);

        // 已打完打卡的次数
        Integer pushFrequencyCompleteNumber = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .in(ReminderLog::getWorkId, reminderLogWorkIdList)
                .eq(ReminderLog::getFinishThisCheckIn, 1)
                .eq(ReminderLog::getDoctorId, doctorId));
        followCountOtherPlanDTO.setCompleteNumber(pushFrequencyCompleteNumber);
        calculationRate(followCountOtherPlanDTO);
    }

    /**
     * 医助统计机构下 血压血糖打开人数，推送人数， 完成人数，
     * @param orgIds
     * @param reminderLogWorkIdList
     * @param followCountOtherPlanDTO
     */
    @Override
    public void statisticsData(List<Long> orgIds, List<String> reminderLogWorkIdList, FollowCountOtherPlanDTO followCountOtherPlanDTO) {
        // 送达人数 根据人数分组
        Integer pushUserAll = baseMapper.selectCount(Wraps.<ReminderLog>q()
                .in("work_id", reminderLogWorkIdList)
                .eq("status_", 1)
                .in("org_id", orgIds)
                .select("DISTINCT patient_Id"));
        followCountOtherPlanDTO.setPushUserNumber(pushUserAll == null ? 0 : pushUserAll);

        Integer pushUserOpenMessage = baseMapper.selectCount(Wraps.<ReminderLog>q()
                .in("work_id", reminderLogWorkIdList)
                .eq("open_this_message", 1)
                .in("org_id", orgIds)
                .select("DISTINCT patient_Id"));
        followCountOtherPlanDTO.setOpenUserNumber(pushUserOpenMessage == null ? 0 : pushUserOpenMessage);

        // 推送的次数
        Integer pushFrequencyAll = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .in(ReminderLog::getWorkId, reminderLogWorkIdList)
                .in(ReminderLog::getOrgId, orgIds));
        followCountOtherPlanDTO.setPushNumber(pushFrequencyAll == null ? 0 : pushFrequencyAll);

        // 已打完打卡的次数
        Integer pushFrequencyCompleteNumber = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .in(ReminderLog::getWorkId, reminderLogWorkIdList)
                .eq(ReminderLog::getFinishThisCheckIn, 1)
                .in(ReminderLog::getOrgId, orgIds));
        followCountOtherPlanDTO.setCompleteNumber(pushFrequencyCompleteNumber);
        calculationRate(followCountOtherPlanDTO);
    }

    /**
     * 根据送达人数，打开人数， 推送人数，打卡人数 计算占比
     * @param followCountOtherPlanDTO
     */
    private void calculationRate(FollowCountOtherPlanDTO followCountOtherPlanDTO) {

        int pushUserNumber = followCountOtherPlanDTO.getPushUserNumber();
        int openUserNumber = followCountOtherPlanDTO.getOpenUserNumber();
        if (pushUserNumber >= openUserNumber) {
            // 未打开数量
            int noOpenUserNumber = pushUserNumber - openUserNumber;
            followCountOtherPlanDTO.setNoOpenUserNumber(noOpenUserNumber);

            // 打开率
            int proportion = BigDecimalUtils.proportion(new BigDecimal(openUserNumber), new BigDecimal(pushUserNumber));
            followCountOtherPlanDTO.setOpeningRate(proportion);
        }
        int pushNumber = followCountOtherPlanDTO.getPushNumber();
        int completeNumber = followCountOtherPlanDTO.getCompleteNumber();
        if (pushNumber >= completeNumber) {
            // 未完成数量
            int noCompleteNumber = pushNumber - completeNumber;
            followCountOtherPlanDTO.setNoCompleteNumber(noCompleteNumber);

            // 完成率
            int proportion = BigDecimalUtils.proportion(new BigDecimal(completeNumber), new BigDecimal(pushNumber));
            followCountOtherPlanDTO.setCompleteRate(proportion);
        }

    }

    /**
     * 统计这个 reminderLogWorkId 的推送记录数量。
     * 已打开数量， 已完成数量， 推送次数
     * @param orgIds
     * @param reminderLogWorkId
     * @param followCountOtherPlanDTO
     */
    @Override
    public void statisticsData(List<Long> orgIds, String reminderLogWorkId, FollowCountOtherPlanDTO followCountOtherPlanDTO) {

        // 送达人数 根据人数分组
        Integer pushUserAll = baseMapper.selectCount(Wraps.<ReminderLog>q()
                .eq("work_id", reminderLogWorkId)
                .eq("status_", 1)
                .in("org_id", orgIds)
                .select("DISTINCT patient_Id"));
        followCountOtherPlanDTO.setPushUserNumber(pushUserAll == null ? 0 : pushUserAll);

        Integer pushUserOpenMessage = baseMapper.selectCount(Wraps.<ReminderLog>q()
                .eq("work_id", reminderLogWorkId)
                .eq("open_this_message", 1)
                .in("org_id", orgIds)
                .select("DISTINCT patient_Id"));
        followCountOtherPlanDTO.setOpenUserNumber(pushUserOpenMessage == null ? 0 : pushUserOpenMessage);

        // 推送的次数
        Integer pushFrequencyAll = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .eq(ReminderLog::getWorkId, reminderLogWorkId)
                .in(ReminderLog::getOrgId, orgIds));
        followCountOtherPlanDTO.setPushNumber(pushFrequencyAll == null ? 0 : pushFrequencyAll);

        // 已打完打卡的次数
        Integer pushFrequencyCompleteNumber = baseMapper.selectCount(Wraps.<ReminderLog>lbQ()
                .eq(ReminderLog::getWorkId, reminderLogWorkId)
                .eq(ReminderLog::getFinishThisCheckIn, 1)
                .in(ReminderLog::getOrgId, orgIds));
        followCountOtherPlanDTO.setCompleteNumber(pushFrequencyCompleteNumber);

        calculationRate(followCountOtherPlanDTO);

    }

    /**
     * 只 统计复查提醒
     * @param startTime
     * @param endTime
     * @param period
     * @param orgIds
     * @param doctorId
     * @param nursingId
     * @return
     */
    @Override
    public TenantStatisticsResult statisticsData(LocalDate startTime, LocalDate endTime, Period period, List<Long> orgIds, Long doctorId, Long nursingId) {
        boolean showInYear = period.getMonths() > 1 || period.getYears() > 0;
        List<LocalDate> xData = new ArrayList<>(31);
        // 统计推送次数 按月 或者按日分组
        QueryWrap<ReminderLog> wrapAllPush = Wraps.<ReminderLog>q()
                .eq("type_", PlanEnum.REVIEW_REMIND.getCode())
                .eq("status_", 1)
                .between("create_time", startTime, LocalDateTime.of(endTime,LocalTime.of(23, 59, 59)));

        // 统计 推送后，患者回执 的消息
        QueryWrap<ReminderLog> wrapReceiptPush = Wraps.<ReminderLog>q()
                .eq("type_", PlanEnum.REVIEW_REMIND.getCode())
                .eq("finish_this_check_in", 1)
                .between("create_time", startTime, LocalDateTime.of(endTime,LocalTime.of(23, 59, 59)));
        if (CollUtil.isNotEmpty(orgIds)) {
            wrapAllPush.in("org_id", orgIds);
            wrapReceiptPush.in("org_id", orgIds);
        }
        if (Objects.nonNull(doctorId)) {
            wrapAllPush.in("doctor_id", doctorId);
            wrapReceiptPush.in("doctor_id", doctorId);
        }
        if (Objects.nonNull(nursingId)) {
            wrapAllPush.in("nursing_id", nursingId);
            wrapReceiptPush.in("nursing_id", nursingId);
        }
        if (showInYear) {
            wrapAllPush.select("MONTH(create_time) as createTime", "count(id) countNum")
                    .groupBy("MONTH(create_time)");

            wrapReceiptPush.select("MONTH(create_time) as createTime", "count(id) countNum")
                    .groupBy("MONTH(create_time)");
        } else {
            wrapAllPush.select("DATE(create_time) as createTime", "count(id) countNum")
                    .groupBy("DATE(create_time)");

            wrapReceiptPush.select("DATE(create_time) as createTime", "count(id) countNum")
                    .groupBy("DATE(create_time)");
        }
        List<Map<String, Object>> allPush = baseMapper.selectMaps(wrapAllPush);
        List<Map<String, Object>> receiptPush = baseMapper.selectMaps(wrapReceiptPush);

        Map<String, Object> allPushTmpMap = new HashMap<>();
        Map<String, Object> receiptPushTmpMap = new HashMap<>();
        for (Map<String, Object> t : allPush) {
            allPushTmpMap.put(Convert.toStr(t.get("createTime")), t.get("countNum"));
        }
        for (Map<String, Object> t : receiptPush) {
            receiptPushTmpMap.put(Convert.toStr(t.get("createTime")), t.get("countNum"));
        }
        List<TenantStatisticsYData> yData;
        // 按年份统计
        if (showInYear) {
            long distance = ChronoUnit.MONTHS.between(startTime, endTime);
            Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> xData.add(f));
            yData = showInYearStatistics(startTime, endTime, allPushTmpMap, receiptPushTmpMap);
        } else {
            long distance = ChronoUnit.DAYS.between(startTime, endTime);
            Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> xData.add(f));
            yData = showInMonthStatistics(startTime, endTime, allPushTmpMap, receiptPushTmpMap);
        }
        TenantStatisticsResult result = new TenantStatisticsResult();
        result.setXData(xData);
        result.setYData(yData);
        return result;
    }

    private List<TenantStatisticsYData> showInMonthStatistics(LocalDate startTime, LocalDate endTime,
                                                              Map<String, Object> allPushTmpMap,
                                                              Map<String, Object> receiptPushTmpMap) {
        // 计算相差的月份
        List<TenantStatisticsYData> yData = new ArrayList<>(3);
        // 推送数量
        List<Integer> allPushYData;
        // 回执数量
        List<Integer> receiptPushYData;
        // 比率
        List<Integer> ratio;

        TenantStatisticsYData statisticsYData;

        long distance = ChronoUnit.DAYS.between(startTime, endTime);
        allPushYData = new ArrayList<>(allPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            String thisDay = Convert.toStr(f.toString());
            allPushYData.add(allPushTmpMap.get(thisDay) == null ? 0 : Integer.parseInt(allPushTmpMap.get(thisDay).toString()));
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(allPushYData);
        statisticsYData.setName("推送人数");
        yData.add(statisticsYData);

        // 提交人数
        receiptPushYData = new ArrayList<>(receiptPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            String thisDay = Convert.toStr(f.toString());
            receiptPushYData.add(receiptPushTmpMap.get(thisDay) == null ? 0 : Integer.parseInt(receiptPushTmpMap.get(thisDay).toString()));
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(receiptPushYData);
        statisticsYData.setName("提交人数");
        yData.add(statisticsYData);

        // 复诊率
        ratio = new ArrayList<>(receiptPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            String thisDay = Convert.toStr(f.toString());
            // 提交数量
            Object receiptMember = receiptPushTmpMap.get(thisDay);
            // 推送数量
            Object pushMember = allPushTmpMap.get(thisDay);
            if (receiptMember == null || Integer.parseInt(receiptMember.toString()) == 0) {
                ratio.add(0);
            } else if (pushMember == null || Integer.parseInt(pushMember.toString()) == 0) {
                ratio.add(0);
            } else {
                // 计算两个值的商
                BigDecimal receiptMemberDecimal = new BigDecimal(receiptMember.toString());
                BigDecimal pushMemberDecimal = new BigDecimal(pushMember.toString());
                ratio.add(BigDecimalUtils.proportion(receiptMemberDecimal, pushMemberDecimal));
            }
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(ratio);
        statisticsYData.setName("复诊率");
        yData.add(statisticsYData);
        return yData;
    }

    /**
     * 按照年统计
     * @param startTime
     * @param endTime
     * @param allPushTmpMap
     * @param receiptPushTmpMap
     * @return
     */
    private List<TenantStatisticsYData> showInYearStatistics(LocalDate startTime, LocalDate endTime,
                                                            Map<String, Object> allPushTmpMap,
                                                             Map<String, Object> receiptPushTmpMap) {
        // 计算相差的月份
        List<TenantStatisticsYData> yData = new ArrayList<>(3);
        // 推送数量
        List<Integer> allPushYData;
        // 回执数量
        List<Integer> receiptPushYData;
        // 比率
        List<Integer> ratio;

        TenantStatisticsYData statisticsYData;

        long distance = ChronoUnit.MONTHS.between(startTime, endTime);
        allPushYData = new ArrayList<>(allPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
            String thisMonth = Convert.toStr(f.getMonthValue());
            allPushYData.add(allPushTmpMap.get(thisMonth) == null ? 0 : Integer.parseInt(allPushTmpMap.get(thisMonth).toString()));
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(allPushYData);
        statisticsYData.setName("推送人数");
        yData.add(statisticsYData);

        // 提交人数
        receiptPushYData = new ArrayList<>(receiptPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
            String thisMonth = Convert.toStr(f.getMonthValue());
            receiptPushYData.add(receiptPushTmpMap.get(thisMonth) == null ? 0 : Integer.parseInt(receiptPushTmpMap.get(thisMonth).toString()));
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(receiptPushYData);
        statisticsYData.setName("提交人数");
        yData.add(statisticsYData);

        // 复诊率
        ratio = new ArrayList<>(receiptPushTmpMap.size());
        Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
            String thisMonth = Convert.toStr(f.getMonthValue());
            // 提交数量
            Object receiptMember = receiptPushTmpMap.get(thisMonth);
            // 推送数量
            Object pushMember = allPushTmpMap.get(thisMonth);
            if (receiptMember == null || Integer.parseInt(receiptMember.toString()) == 0) {
                ratio.add(0);
            } else if (pushMember == null || Integer.parseInt(pushMember.toString()) == 0) {
                ratio.add(0);
            } else {
                // 计算两个值的商
                BigDecimal receiptMemberDecimal = new BigDecimal(receiptMember.toString());
                BigDecimal pushMemberDecimal = new BigDecimal(pushMember.toString());
                ratio.add(BigDecimalUtils.proportion(receiptMemberDecimal, pushMemberDecimal));
            }
        });
        statisticsYData = new TenantStatisticsYData();
        statisticsYData.setyData(ratio);
        statisticsYData.setName("复诊率");
        yData.add(statisticsYData);
        return yData;
    }

}
