package com.caring.sass.nursing.service.statistics.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsDataMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsDataMonthMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsDataNewMapper;
import com.caring.sass.nursing.dao.statistics.StatisticsTaskMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.statistics.StatisticsData;
import com.caring.sass.nursing.entity.statistics.StatisticsDataMonth;
import com.caring.sass.nursing.entity.statistics.StatisticsDataNew;
import com.caring.sass.nursing.entity.statistics.StatisticsTask;
import com.caring.sass.nursing.service.statistics.StatisticsDataService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 统计的业务数据 Mapper
 *
 */

@Slf4j
@Service
public class StatisticsDataServiceImpl  extends
        SuperServiceImpl<StatisticsDataMapper, StatisticsData>
        implements StatisticsDataService {

    @Autowired
    PatientApi patientApi;

    @Autowired
    StatisticsTaskMapper statisticsTaskMapper;

    @Autowired
    FormResultMapper formResultMapper;

    @Autowired
    StatisticsDataMonthMapper statisticsDataMonthMapper;

    @Autowired
    StatisticsDataNewMapper statisticsDataNewMapper;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static Map<Long, WeakReference<Patient>> patientMaps = new HashMap<>();


    /**
     * 清除 统计任务 之前产生的数据
     * @param statisticsTask
     */
    public void cleanTaskData(StatisticsTask statisticsTask) {

        baseMapper.delete(Wraps.<StatisticsData>lbQ()
                .eq(StatisticsData::getStatisticsTaskId, statisticsTask.getId())
                .eq(StatisticsData::getFormId, statisticsTask.getFormId())
                .eq(StatisticsData::getFormFieldId, statisticsTask.getFormFieldId()));

        statisticsDataMonthMapper.delete(Wraps.<StatisticsDataMonth>lbQ()
            .eq(StatisticsDataMonth::getStatisticsTaskId, statisticsTask.getId())
            .eq(StatisticsDataMonth::getFormId, statisticsTask.getFormId()));

        statisticsDataNewMapper.delete(Wraps.<StatisticsDataNew>lbQ()
                .eq(StatisticsDataNew::getStatisticsTaskId, statisticsTask.getId())
                .eq(StatisticsDataNew::getFormId, statisticsTask.getFormId()));
    }

    /**
     * 创建统计任务后。 根据统计任务 抓取统计数据到 本地
     * @param statisticsTask
     * @param tenant
     */
    @Override
    public void parseTaskResetData(StatisticsTask statisticsTask, String tenant) {

        BaseContextHandler.setTenant(tenant);
        StatisticsTask task = statisticsTaskMapper.selectById(statisticsTask.getId());
        if (Objects.nonNull(task) && task.getInitData() != null && task.getInitData().equals(true)) {
            return;
        }
        statisticsTask.setInitData(true);
        statisticsTaskMapper.updateById(statisticsTask);

        Long formId = statisticsTask.getFormId();
        String formFieldId = statisticsTask.getFormFieldId();

        if (formId == null) {
            return;
        }
        if (StringUtils.isEmpty(formFieldId)) {
            return;
        }

        // 清除数据库中 跟此统计任务相关的 数据。
        cleanTaskData(statisticsTask);
        int current = 1;
        int size = 50;
        IPage<FormResult> page;
        String taskId = statisticsTask.getId().toString();

        String taskKey = "task:";
        // 一个患者 一天 只有最新的表单结果 用于 任务统计
        // 患者ID + 任务统计 + 日期
        String statisticsDataRedisKey = taskKey + taskId + "DaySynchronousData";
        // 一个月只需要记录一次最新的
        String statisticsDataMonthRedisKey = taskKey + taskId + "MonthSynchronousData";
        //
        String statisticsDataNewRedisKey = taskKey + taskId + "NewSynchronousData";
        // 患者ID + 任务统计 + 年份 + 月份
        while (true) {
            page = new Page(current, size);
            IPage<FormResult> selectPage = formResultMapper.selectPage(page, Wraps.<FormResult>lbQ().eq(FormResult::getFormId, formId)
                    .select(SuperEntity::getId, FormResult::getJsonContent, SuperEntity::getCreateTime, FormResult::getUserId)
                    .orderByDesc(SuperEntity::getCreateTime));

            List<FormResult> pageRecords = selectPage.getRecords();

            List<StatisticsData> statisticsData = new ArrayList<>(50);
            // statisticsData 去重
            List<StatisticsDataMonth> statisticsDataMonths = new ArrayList<>(50);
            // 用户 表单某字段 最新的数据
            List<StatisticsDataNew> statisticsDataNew = new ArrayList<>(50);
            String dataSetKey;
            String dataMonthSetKey;
            String dataNewSetKey;
            StatisticsDataNew dataNew;
            StatisticsDataMonth month;
            if (CollUtil.isNotEmpty(pageRecords)) {
                for (FormResult formResult : pageRecords) {
                    LocalDateTime createTime = formResult.getCreateTime();
                    dataSetKey = formResult.getUserId().toString() +  taskId + createTime.toLocalDate().toString();
                    Boolean dataSetKeyExist = redisTemplate.opsForSet().isMember(statisticsDataRedisKey, dataSetKey);
                    if (dataSetKeyExist != null && dataSetKeyExist) {
                        continue;
                    }
                    redisTemplate.opsForSet().add(statisticsDataRedisKey, dataSetKey);
                    StatisticsData data = parseForm(formResult, statisticsTask);
                    if (Objects.nonNull(data)) {
                        statisticsData.add(data);
                        month = new StatisticsDataMonth();
                        // 患者 月度 数据 KEY
                        dataMonthSetKey = formResult.getUserId().toString() + taskId + createTime.getYear() + "" + createTime.getMonthValue();
                        Boolean dataMonthSetKeyExist = redisTemplate.opsForSet().isMember(statisticsDataMonthRedisKey, dataMonthSetKey);
                        if (dataMonthSetKeyExist != null && dataMonthSetKeyExist) {
                            continue;
                        } else {
                            redisTemplate.opsForSet().add(statisticsDataMonthRedisKey, dataMonthSetKey);
                            BeanUtils.copyProperties(data, month);
                            statisticsDataMonths.add(month);
                        }
                        // 患者 表单字段 最新 数据 KEY
                        dataNewSetKey = formResult.getUserId().toString() +  taskId;
                        Boolean dataNewSetKeyExist = redisTemplate.opsForSet().isMember(statisticsDataNewRedisKey, dataNewSetKey);
                        if (dataNewSetKeyExist != null && dataNewSetKeyExist) {
                            continue;
                        } else {
                            dataNew = new StatisticsDataNew();
                            redisTemplate.opsForSet().add(statisticsDataNewRedisKey, dataNewSetKey);
                            BeanUtils.copyProperties(data, dataNew);
                            statisticsDataNew.add(dataNew);
                        }

                    }

                }
            }
            if (CollUtil.isNotEmpty(statisticsData)) {
                baseMapper.insertBatchSomeColumn(statisticsData);
            }
            if (CollUtil.isNotEmpty(statisticsDataMonths)) {
                statisticsDataMonthMapper.insertBatchSomeColumn(statisticsDataMonths);
            }
            if (CollUtil.isNotEmpty(statisticsDataNew)) {
                statisticsDataNewMapper.insertBatchSomeColumn(statisticsDataNew);
            }

            current++;
            if (current > selectPage.getPages()) {
                break;
            }
            if (selectPage.getPages() == 0) {
                break;
            }
        }
        patientMaps = new HashMap<>();
        redisTemplate.delete(statisticsDataRedisKey);
        redisTemplate.delete(statisticsDataMonthRedisKey);
        redisTemplate.delete(statisticsDataNewRedisKey);

    }

    /**
     * 获取 患者的基本信息
     * @param userId
     * @return
     */
    public Patient getPatient(Long userId) {
        WeakReference<Patient> weakReference = patientMaps.get(userId);
        Patient patient = null;
        if (Objects.nonNull(weakReference)) {
            patient = weakReference.get();
        }
        if (Objects.isNull(patient)) {
            R<Patient> patientR = patientApi.getBaseInfoForStatisticsData(userId);
            if (patientR.getIsSuccess() != null && patientR.getIsSuccess()) {
                patient = patientR.getData();
                if (Objects.nonNull(patient)) {
                    patientMaps.put(userId, new WeakReference<> (patient));
                }
            }
        }
        return patient;

    }


    /**
     * 用户提交或者修改这个表单时。
     * @param formResult
     * @param tenantCode
     */
    @Override
    public void formResultChange(FormResult formResult, String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        List<StatisticsTask> taskList = statisticsTaskMapper.selectList(Wraps.<StatisticsTask>lbQ()
                .eq(StatisticsTask::getFormId, formResult.getFormId()));
        if (CollUtil.isEmpty(taskList)) {
            return;
        }

        // 清除用户这个表单产生的 统计数据
        baseMapper.delete(Wraps.<StatisticsData>lbQ().eq(StatisticsData::getFormResultId, formResult.getId()));
        // 清除 患者的这个表单 数据记录
        statisticsDataMonthMapper.delete(Wraps.<StatisticsDataMonth>lbQ()
                .eq(StatisticsDataMonth::getFormId, formResult.getFormId())
                .eq(StatisticsDataMonth::getPatientId, formResult.getUserId())
                .eq(StatisticsDataMonth::getFormResultId, formResult.getId()));
        // 清除
        List<StatisticsData> statisticsData = new ArrayList<>(taskList.size());
        List<StatisticsDataMonth> statisticsDataMonths = new ArrayList<>(taskList.size());
        List<StatisticsDataNew> statisticsDataNewSave = new ArrayList<>(taskList.size());
        for (StatisticsTask statisticsTask : taskList) {
            StatisticsData data = parseForm(formResult, statisticsTask);
            StatisticsDataMonth month = new StatisticsDataMonth();
            if (Objects.nonNull(data)) {
                statisticsData.add(data);
                BeanUtils.copyProperties(data, month);
                statisticsDataMonths.add(month);

                StatisticsDataNew statisticsDataNew = statisticsDataNewMapper.selectOne(Wraps.<StatisticsDataNew>lbQ()
                        .eq(StatisticsDataNew::getPatientId, data.getPatientId())
                        .eq(StatisticsDataNew::getFormId, data.getFormId())
                        .eq(StatisticsDataNew::getStatisticsTaskId, data.getStatisticsTaskId())
                        .eq(StatisticsDataNew::getFormFieldId, data.getFormFieldId()));
                if (Objects.isNull(statisticsDataNew)) {
                    statisticsDataNew = new StatisticsDataNew();
                    BeanUtils.copyProperties(data, statisticsDataNew);
                    statisticsDataNewSave.add(statisticsDataNew);
                } else {
                    if (statisticsDataNew.getCreateTime().isBefore(formResult.getCreateTime())) {
                        statisticsDataNew.setCreateTime(formResult.getCreateTime());
                        statisticsDataNew.setSubmitValue(data.getSubmitValue());
                        statisticsDataNew.setTargetValue(data.getTargetValue());
                        statisticsDataNew.setReachTheStandard(data.getReachTheStandard());
                        statisticsDataNewMapper.updateById(statisticsDataNew);
                    }
                }

            }
        }
        if (CollUtil.isNotEmpty(statisticsData)) {
            baseMapper.insertBatchSomeColumn(statisticsData);
        }
        if (CollUtil.isNotEmpty(statisticsDataMonths)) {
            statisticsDataMonthMapper.insertBatchSomeColumn(statisticsDataMonths);
        }
        if (CollUtil.isNotEmpty(statisticsDataNewSave)) {
            statisticsDataNewMapper.insertBatchSomeColumn(statisticsDataNewSave);
        }
    }

    /**
     * 解析表单
     * @param formResult
     */
    private StatisticsData parseForm(FormResult formResult, StatisticsTask statisticsTask) {

        Long userId = formResult.getUserId();
        String formFieldId = statisticsTask.getFormFieldId();
        Long formId = statisticsTask.getFormId();
        if (userId == null) {
            return null;
        }

        String jsonContent = formResult.getJsonContent();
        if (StrUtil.isEmpty(jsonContent)) {
            return null;
        }

        List<FormField> formFields = JSON.parseArray(jsonContent, FormField.class);
        FormField field = null;
        for (FormField formField : formFields) {
            if (formFieldId.equals(formField.getId())) {
                field = formField;
                break;
            }
        }
        if (Objects.isNull(field)) {
            return null;
        }
        Patient patient = getPatient(userId);
        if (Objects.isNull(patient)) {
            return null;
        }
        String classCode = patient.getClassCode();
        Long organId = patient.getOrganId();
        Long doctorId = patient.getDoctorId();
        Long serviceAdvisorId = patient.getServiceAdvisorId();

        // 解析 字段的结果。然后保存
        String values = field.getValues();
        if (StringUtils.isEmpty(values)) {
            return null;
        }
        JSONArray array = JSON.parseArray(values);
        String value = null;
        for (Object o : array) {
            JSONObject jsonObject = JSON.parseObject(o.toString());
            Object attrValue = jsonObject.get("attrValue");
            if (Objects.nonNull(attrValue)) {
                value = attrValue.toString();
            }
        }
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        LocalDateTime createTime = formResult.getCreateTime();
        Double targetValue = field.getTargetValue();
        BigDecimal valueDecimal;
        try {
            valueDecimal = new BigDecimal(value);
        } catch (Exception e) {
            log.error("NumberFormatException valueDecimal value = " + value);
            return null;
        }
        int reachTheStandard = 0;
        // 判断是否达标
        if (targetValue != null) {
            // 用户填写的值。大于 目标值。 则达标
            int compare = valueDecimal.compareTo(BigDecimal.valueOf(targetValue));
            if (compare > 0) {
                reachTheStandard = 1;
            }
        }
        return StatisticsData.builder()
                .patientId(userId)
                .doctorId(doctorId)
                .nursingId(serviceAdvisorId)
                .organId(organId)
                .classCode(classCode)
                .statisticsTaskId(statisticsTask.getId())
                .formResultId(formResult.getId())
                .formId(formId)
                .formFieldId(formFieldId)
                .submitYear(createTime.getYear() + "")
                .submitMonth(createTime.getMonthValue() + "")
                .submitDate(createTime.toLocalDate())
                .submitValue(value)
                .targetValue(field.getTargetValue())
                .widgetType(field.getWidgetType())
                .reachTheStandard(reachTheStandard)
                .build();
    }

    public static void main(String[] args) {

        BigDecimal valueDecimal = new BigDecimal(19);
        int compare = valueDecimal.compareTo(BigDecimal.valueOf(18));
        System.out.println(compare);

    }


}
