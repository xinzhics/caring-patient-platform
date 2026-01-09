package com.caring.sass.nursing.service.form.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.constant.AttrBindEvent;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.constant.TagBindRedisKey;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.form.FormScoreRuleMapper;
import com.caring.sass.nursing.dto.blood.BloodPressDTO;
import com.caring.sass.nursing.dto.blood.BloodPressTrendResult;
import com.caring.sass.nursing.dto.form.*;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import com.caring.sass.nursing.entity.form.*;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.enumeration.FormResultCountScoreEnum;
import com.caring.sass.nursing.service.form.*;
import com.caring.sass.nursing.service.information.InformationIntegrityMonitoringService;
import com.caring.sass.nursing.service.plan.PatientNursingPlanService;
import com.caring.sass.nursing.service.plan.PlanService;
import com.caring.sass.nursing.service.plan.ReminderLogService;
import com.caring.sass.nursing.service.statistics.StatisticsDataService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.service.traceInto.TraceIntoResultService;
import com.caring.sass.nursing.util.FormExactFieldValue;
import com.caring.sass.nursing.util.FormUtil;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 表单填写结果表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class FormResultServiceImpl extends SuperServiceImpl<FormResultMapper, FormResult> implements FormResultService {

    private final PlanService planService;

    private final PatientApi patientApi;

    private final ReminderLogService reminderLogService;

    private final PatientFormFieldReferenceService patientFormFieldReferenceService;

    private final StatisticsDataService statisticsDataService;

    @Autowired
    FormResultBackUpService formResultBackUpService;

    @Autowired
    TraceIntoResultService traceIntoResultService;

    @Autowired
    FormResultScoreService formResultScoreService;

    @Autowired
    IndicatorsConditionMonitoringService indicatorsConditionMonitoringService;
    @Autowired
    InformationIntegrityMonitoringService informationIntegrityMonitoringService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    FormScoreRuleMapper formScoreRuleMapper;

    @Autowired
    ImApi imApi;

    @Autowired
    MsgPatientSystemMessageApi msgPatientSystemMessageApi;

    FormService formService;


    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("form-result-tagBind-", false);

    private static final ExecutorService BIND_PATIENT_TAG_EXECUTOR = new ThreadPoolExecutor(3, 5,
            0L, TimeUnit.MILLISECONDS,
            new SaasLinkedBlockingQueue<>(600), THREAD_FACTORY);

    public FormResultServiceImpl(PlanService planService, PatientApi patientApi,
                                 PatientFormFieldReferenceService patientFormFieldReferenceService,
                                 ReminderLogService reminderLogService, StatisticsDataService statisticsDataService
                                 ) {
        this.planService = planService;
        this.patientApi = patientApi;
        this.patientFormFieldReferenceService = patientFormFieldReferenceService;
        this.reminderLogService = reminderLogService;
        this.statisticsDataService = statisticsDataService;
    }

    public FormService getFormService() {
        if (formService == null) {
            formService = SpringUtils.getBean(FormService.class);
        }
        return formService;

    }



    @Override
    public FormResult getById(Serializable id) {
        FormResult result = baseMapper.selectById(id);
        if (Objects.nonNull(result) && StringUtils.isNotEmptyString(result.getJsonContent())) {
            List<FormField> formFields = JSON.parseArray(result.getJsonContent(), FormField.class);
            // 处理一下。多选题的 options 和 values 比对。 存在于values中的options需要设置select属性
            handleCheckBoxResult(formFields);
            String jsonString = JSON.toJSONString(formFields);
            result.setJsonContent(jsonString);
            return result;
        }
        return result;
    }

    /**
     * 一条推送消息只有一个表单结果
     * @param messageId
     * @return
     */
    @Override
    public FormResult getFormResultByMessageId(Long messageId) {
        LbqWrapper<FormResult> lbqWrapper = Wraps.<FormResult>lbQ().eq(FormResult::getMessageId, messageId).last(" limit 0,1");
        FormResult formResult = baseMapper.selectOne(lbqWrapper);
        if (Objects.nonNull(formResult)) {
            List<FormField> formFields = JSON.parseArray(formResult.getJsonContent(), FormField.class);
            // 处理一下。多选题的 options 和 values 比对。 存在于values中的options需要设置select属性
            handleCheckBoxResult(formFields);
            String jsonString = JSON.toJSONString(formFields);
            formResult.setFieldList(JSON.parseArray(jsonString));
            formResult.setJsonContent(jsonString);
            return formResult;
        }
        return null;
    }

    /**
     * 使用 businessId 封装一个表单给前端
     * @param planId
     * @param messageId
     * @return
     */
    @Override
    public FormResult getFormResultByBusinessId(Long planId, Long messageId) {
        FormService formService = getFormService();
        Form form = formService.getFormByRedis(null, planId.toString());
        if (Objects.nonNull(form)) {
            return FormResult.builder()
                    .businessId(form.getBusinessId())
                    .category(form.getCategory())
                    .oneQuestionPage(form.getOneQuestionPage())
                    .scoreQuestionnaire(form.getScoreQuestionnaire())
                    .formId(form.getId())
                    .jsonContent(form.getFieldsJson())
                    .messageId(messageId == null ? "" : messageId.toString())
                    .name(form.getName())
                    .build();
        }
        return null;
    }

    @Override
    public List<FormResult> getBasicAndLastHealthFormResult(Long patientId) {

        List<FormResult> list = new ArrayList<>();
        FormResult formResult = getBasicOrHealthFormResult(patientId, FormEnum.BASE_INFO);
        if (Objects.nonNull(formResult)) {
            list.add(formResult);
        }
        FormResult healthRecord = getBasicOrHealthFormResult(patientId, FormEnum.HEALTH_RECORD);
        if (Objects.nonNull(healthRecord)) {
            list.add(healthRecord);
        }
        return list;
    }

    /**
     * @return com.caring.sass.nursing.entity.form.FormResult
     * @Author yangShuai
     * @Description 只适用 疾病信息 和 基本信息
     * @Date 2020/11/10 16:33
     */
    @Override
    public FormResult getBasicOrHealthFormResult(Long patientId, FormEnum formEnum) {
        if (FormEnum.BASE_INFO.eq(formEnum)) {
            LbqWrapper<FormResult> query = Wraps.<FormResult>lbQ()
                    .eq(FormResult::getCategory, formEnum)
                    .eq(FormResult::getUserId, patientId);
            List<FormResult> results = baseMapper.selectList(query);
            FormResult result;
            if (CollUtil.isNotEmpty(results)) {
                if (results.size() > 1) {
                    result = results.get(0);
                    for (int i = 1; i < results.size(); i++) {
                        baseMapper.deleteById(results.get(i));
                    }
                } else {
                    result = results.get(0);
                }
                return result;
            }
        } else if (FormEnum.HEALTH_RECORD == formEnum) {
            LbqWrapper<FormResult> query = Wraps.<FormResult>lbQ()
                    .last(" limit 0,1 ")
                    .orderByDesc(FormResult::getCreateTime)
                    .eq(FormResult::getCategory, formEnum)
                    .eq(FormResult::getUserId, patientId)
                    .eq(FormResult::getDeleteMark, 0);
            return baseMapper.selectOne(query);

        }


        return null;
    }

    private static String[] DESC = new String[]{"高压", "低压", "心率"};

    /**
     * 获取血压心率走势（匹配数据）
     * @param patientId
     * @return
     */
    @Override
    public List<BloodPressDTO> getPatientBloodPress(Long patientId) {
        List<BloodPressDTO> bloodPressDTOS = new ArrayList<>();
        LbqWrapper<Plan> planLbqWrapper = Wraps.<Plan>lbQ()
                .eq(Plan::getPlanType, 1);
        List<Plan> plans = planService.list(planLbqWrapper);
        if (CollUtil.isEmpty(plans)) {
            return bloodPressDTOS;
        }

        Plan plan = plans.get(0);
        // 日期默认选本周的
        Date now = new Date();
        Date beginOfWeek = DateUtil.beginOfWeek(now).toJdkDate();
        Date endOfWeek = DateUtil.endOfWeek(now).toJdkDate();

        LbqWrapper<FormResult> lbqWrapper = Wraps.<FormResult>lbQ()
                .eq(FormResult::getBusinessId, plan.getId())
                .eq(FormResult::getUserId, patientId)
                .ge(FormResult::getCreateTime, LocalDateTimeUtil.of(beginOfWeek))
                .lt(FormResult::getCreateTime, LocalDateTimeUtil.of(endOfWeek));
        List<FormResult> results = baseMapper.selectList(lbqWrapper);

        // 先梳理所有的数据，然后按日期分组，拿平均值
        for (FormResult result : results) {
            String jsonContent = result.getJsonContent();
            List<FormFieldDto> formFields = JSONArray.parseArray(jsonContent, FormFieldDto.class);
            for (FormFieldDto formField : formFields) {
                String value = formField.parseValue();
                if (!FormFieldDto.isNumeric(value)) {
                    continue;
                }
                String label = formField.getLabel();
                LocalDateTime createTime = result.getCreateTime();
                BloodPressDTO bloodPressDTO = new BloodPressDTO();
                bloodPressDTO.setCreateTime(createTime);
                bloodPressDTO.setValue(Convert.toDouble(value));
                bloodPressDTO.setType(label);
                bloodPressDTOS.add(bloodPressDTO);
            }
        }

        Map<String, Double> m = bloodPressDTOS.stream().peek(
                x -> x.setDay(DateUtil.format(x.getCreateTime(), "yyyy-MM-dd"))
        ).collect(Collectors.groupingBy(
                (item -> (item.getDay() + StrPool.UNDERSCORE + item.getType())), Collectors.averagingDouble(BloodPressDTO::getValue)));

        List<BloodPressDTO> bloodPressDTOArrayList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : m.entrySet()) {
            if (StrUtil.isNotBlank(entry.getKey())) {
                if (!ArrayUtil.contains(DESC, entry.getKey().split(StrPool.UNDERSCORE)[1])) {
                    continue;
                }

                BloodPressDTO b = new BloodPressDTO();
                String[] t = entry.getKey().split(StrPool.UNDERSCORE);
                b.setDay(t[0]);
                b.setType(t[1]);
                b.setValue(entry.getValue());
                bloodPressDTOArrayList.add(b);
            }
        }
        // 日期下没有数据填充0
        List<String> weekDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDays.add(LocalDateTimeUtil.format(LocalDateTimeUtil.of(beginOfWeek).plusDays(i), "yyyy-MM-dd"));
        }

        List<BloodPressDTO> ret = new ArrayList<>();
        for (String weekDay : weekDays) {
            for (String type : DESC) {
                BloodPressDTO b = hasValue(weekDay, type, bloodPressDTOArrayList);
                if (null != b) {
                    ret.add(b);
                    continue;
                }
                ret.add(new BloodPressDTO().setDay(weekDay).setType(type).setValue(0.0));
            }
        }
        return ret;
    }

    private BloodPressDTO hasValue(String day, String type, List<BloodPressDTO> bloodPressDTOS) {
        for (BloodPressDTO bloodPressDTO : bloodPressDTOS) {
            String d1 = bloodPressDTO.getDay();
            String t1 = bloodPressDTO.getType();
            boolean same = Objects.equals(day, d1) && Objects.equals(t1, type);
            if (same) {
                return bloodPressDTO;
            }
        }
        return null;
    }

    private static final Map<String, String> DESC_MAP = new HashMap<>();

    static {
        DESC_MAP.put("高压", "highBlood");
        DESC_MAP.put("低压", "lowBlood");
        DESC_MAP.put("心率", "heartRate");
        DESC_MAP.put("备注", "remark");
    }

    @Override
    public List<Map<String, Object>> patientBloodPressList(Long patientId) {
        LbqWrapper<Plan> planLbqWrapper = Wraps.<Plan>lbQ()
                .eq(Plan::getPlanType, 1);
        List<Plan> plans = planService.list(planLbqWrapper);
        if (CollUtil.isEmpty(plans)) {
            return new ArrayList<>();
        }

        Plan plan = plans.get(0);
        LbqWrapper<FormResult> lbqWrapper = Wraps.<FormResult>lbQ()
                .eq(FormResult::getBusinessId, plan.getId())
                .eq(FormResult::getUserId, patientId).orderByDesc(FormResult::getCreateTime);
        List<FormResult> results = baseMapper.selectList(lbqWrapper);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (FormResult result : results) {
            String jsonContent = result.getJsonContent();
            Map<String, Object> tmp = new HashMap<>();
            List<FormFieldDto> formFields = JSONArray.parseArray(jsonContent, FormFieldDto.class);
            for (FormFieldDto formField : formFields) {
                String value = formField.parseValue();
                String label = DESC_MAP.get(formField.getLabel());
                if (StrUtil.isNotBlank(label)) {
                    tmp.put(label, value);
                }
            }
            maps.add(tmp);
        }
        return maps;
    }


    @Override
    public BloodPressTrendResult getPatientBloodPressTrandDatas(Long patientId, boolean needNew) {
        TreeMap<String, String> mpHighBloodPress = new TreeMap();
        TreeMap<String, String> mpLowBloodPress = new TreeMap();
        TreeMap<String, String> mpHeartRate = new TreeMap();
        LbqWrapper<Plan> planLbqWrapper = new LbqWrapper<>();
        planLbqWrapper.eq(Plan::getPlanType, 1);
        List<Plan> plans = planService.list(planLbqWrapper);
        if (CollectionUtils.isNotEmpty(plans)) {
            Plan plan = plans.get(0);
            LbqWrapper<FormResult> lbqWrapper = Wraps.<FormResult>lbQ()
                    .eq(FormResult::getBusinessId, plan.getId())
                    .eq(FormResult::getUserId, patientId);
            List<FormResult> results = new ArrayList<>();
            if (needNew) {
                results = baseMapper.selectList(lbqWrapper);
            } else {
                // 查询 7 天的记录。 每天多条情况只按最后一条算
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime lastTime;
                LocalDateTime startTime;
                for (int i = 0; i < 7; i++) {
                    if (results.size() > 0) {
                        now = now.minusDays(1);
                    }
                    lastTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);
                    startTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);

                    lbqWrapper = Wraps.<FormResult>lbQ()
                            .eq(FormResult::getBusinessId, plan.getId())
                            .eq(FormResult::getUserId, patientId)
                            .between(FormResult::getCreateTime, startTime, lastTime)
                            .orderByDesc(FormResult::getCreateTime);
                    Page<FormResult> objectPage = new Page<>(1, 1);
                    Page<FormResult> selectPage = baseMapper.selectPage(objectPage, lbqWrapper);
                    List<FormResult> records = selectPage.getRecords();
                    if (CollectionUtils.isEmpty(records)) {
                        FormResult formResult = new FormResult();
                        formResult.setCreateTime(lastTime);
                        results.add(formResult);
                    } else {
                        results.add(records.get(0));
                    }
                }
            }
            for (FormResult result : results) {
                String value = "0";
                LocalDateTime createTime = result.getCreateTime();
                String fillTime = DateUtil.format(createTime, "yyyy-MM-dd");
                String jsonContent = result.getJsonContent();
                List<FormFieldDto> formFields = JSONArray.parseArray(jsonContent, FormFieldDto.class);
                boolean gaoYa = false;
                boolean diYa = false;
                boolean xinLv = false;
                if (CollectionUtils.isNotEmpty(formFields)) {
                    for (FormFieldDto formField : formFields) {
                        String label = formField.getLabel();
                        if (StrUtil.isBlank(label)) {
                            continue;
                        }

                        if (label.equals("高压")) {
                            try {
                                value = formField.parseValue();
                                if (!FormFieldDto.isNumeric(value)) {
                                    continue;
                                }
                                gaoYa = true;
                                computeOneDay(mpHighBloodPress, fillTime, value);
                            } catch (Exception e) {
                                computeOneDay(mpHighBloodPress, fillTime, "0");
                            }
                        }
                        if (label.equals("低压")) {
                            try {
                                value = formField.parseValue();
                                if (!FormFieldDto.isNumeric(value)) {
                                    continue;
                                }
                                gaoYa = true;
                                computeOneDay(mpLowBloodPress, fillTime, value);
                            } catch (Exception e) {
                                computeOneDay(mpLowBloodPress, fillTime, "0");
                            }
                        }
                        if (label.equals("心率")) {
                            try {
                                value = formField.parseValue();
                                if (!FormFieldDto.isNumeric(value)) {
                                    continue;
                                }
                                gaoYa = true;
                                computeOneDay(mpHeartRate, fillTime, value);
                            } catch (Exception e) {
                                computeOneDay(mpHeartRate, fillTime, "0");
                            }
                        }
                    }
                }
                if (!gaoYa) {
                    computeOneDay(mpHighBloodPress, fillTime, "0");
                }
                if (!diYa) {
                    computeOneDay(mpLowBloodPress, fillTime, "0");
                }
                if (!xinLv) {
                    computeOneDay(mpHeartRate, fillTime, "0");
                }
            }
        }

        Collection<String> highBloodPress = mpHighBloodPress.values();
        Collection<String> lowBloodPress = mpLowBloodPress.values();
        Collection<String> heartRate = mpHeartRate.values();
        Collection<String> xDatas = mpHeartRate.keySet();
        BloodPressTrendResult results = new BloodPressTrendResult(highBloodPress, lowBloodPress, heartRate, xDatas);
        return results;
    }


    void computeOneDay(Map<String, String> mp, String day, String value) {
        if (StringUtils.isEmptyAfterTrim(value)) {
            value = "0";
        }

        String s = mp.get(day);
        if (s == null) {
            mp.put(day, value);
        } else {
            try {
                int i = Integer.parseInt(value);
                if (i != 0) {
                    Integer f = (Integer.parseInt(s) + Integer.parseInt(value)) / 2;
                    mp.put(day, f.toString());
                }
            } catch (Exception var7) {
            }
        }
    }

    /**
     * 清除重复的 field
     * @param jsonContent
     * @return
     */
    private String cleanFieldRepeat(String jsonContent) {
        if(StringUtils.isNotEmptyString(jsonContent)){
            List<FormField> jsonArray = JSON.parseArray(jsonContent, FormField.class);
            List<FormField> resultField = new ArrayList<>(jsonArray.size());
            Set<String> fieldIdSet = new HashSet<>(jsonArray.size());
            for (FormField field : jsonArray) {
                if (fieldIdSet.contains(field.getId() + field.getWidgetType())) {
                    continue;
                }
                fieldIdSet.add(field.getId() + field.getWidgetType());
                resultField.add(field);
            }
            return JSON.toJSONString(resultField);
        } else {
            return jsonContent;
        }
    }

    /**
     * 重新设置 基本信息 疾病信息 保存后的表单结果字段
     * @param fieldRepeat
     * @param model
     * @return
     */
    protected String resetBaseInfoOrHealthRecordField(String fieldRepeat, FormResult model) {
        // 把之前不需要注册流程中填写的字段重新设置到表单结果去
        Form form = getFormService().getFormByRedis(model.getCategory(), null);
        List<FormField> formResultFields = JSON.parseArray(fieldRepeat, FormField.class);
        String fieldsJson = form.getFieldsJson();
        List<FormField> formFields = JSON.parseArray(fieldsJson, FormField.class);
        if (formFields.size() != formResultFields.size()) {
            Map<String, FormField> fieldMap = formResultFields.stream().collect(Collectors.toMap(FormField::getId, item -> item, (o1, o2) -> o2));
            for (FormField formField : formFields) {
                FormField resultField = fieldMap.get(formField.getId());
                if (Objects.nonNull(resultField)) {
                    formField.setValues(resultField.getValues());
                    formField.setValue(resultField.getValue());
                    formField.setOtherValue(resultField.getOtherValue());
                    formField.setCheckBoxValues(resultField.getCheckBoxValues());
                } else {
                    // 如果用户提交的结果中没有。那么检查是否有需要设置默认值。
                    // 如果 随访阶段题型被隐藏，用户没有提交。 那么给他设置默认值。
                    if (FormFieldExactType.FollowUpStage.equals(formField.getExactType())) {
                        if (StrUtil.isNotEmpty(formField.getDefaultValue())) {
                            List<FormOptions> options = formField.getOptions();
                            FormOptions formOptions = null;
                            for (FormOptions option : options) {
                                if (formField.getDefaultValue().equals(option.getId())) {
                                    formOptions = option;
                                    break;
                                }
                            }
                            if (Objects.nonNull(formOptions)) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("valueText", formOptions.getAttrValue());
                                jsonObject.put("desc", "");
                                jsonObject.put("attrValue", formOptions.getId());
                                jsonObject.put("questions", formOptions.getQuestions());
                                jsonObject.put("score", formOptions.getScore());
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.add(jsonObject);
                                formField.setValues(jsonArray.toJSONString());
                            }
                        }
                    }
                }
                fieldRepeat = JSON.toJSONString(formFields);
            }
        }
        return fieldRepeat;
    }

    /**
     * 一道题一道题的提交
     * @param formResult
     */
    @Override
    public void saveFormResultStage(FormResult formResult) {
        // 如果是基本信息 或 疾病信息
        if (!FormEnum.HEALTH_RECORD.eq(formResult.getCategory())
                && !FormEnum.BASE_INFO.eq(formResult.getCategory())) {
            throw new BizException("非基本信息疾病信息不可使用此业务");
        }
        Integer fillInIndex = formResult.getFillInIndex();
        if (fillInIndex == null) {
            throw new BizException("请设置答题进度或传-1表示完成答题");
        }
        String fieldRepeat = cleanFieldRepeat(formResult.getJsonContent());
        // 当表单在入组的时候填写完成的时候。恢复表单中移除掉的 入组时不需要填写的组件。
        if (fillInIndex.equals(-1)) {
            fieldRepeat = resetBaseInfoOrHealthRecordField(fieldRepeat, formResult);
        }
        formResult.setJsonContent(fieldRepeat);
        formResultParsing(formResult);
        if (formResult.getCreateTime() == null) {
            formResult.setCreateTime(LocalDateTime.now());
        }
        @Length(max = 32, message = "对微信应消息回执中的Id长度不能超过32") String messageId = formResult.getMessageId();
        Integer recommendReceipt = formResult.getImRecommendReceipt();
        @NotNull(message = "填写人Id不能为空") Long userId = formResult.getUserId();
        FormResult oldFormResult;
        if (formResult.getId() == null) {
            oldFormResult = baseMapper.selectOne(Wraps.<FormResult>lbQ()
                    .select(SuperEntity::getId, FormResult::getUserId, FormResult::getJsonContent)
                    .eq(FormResult::getUserId, userId)
                    .eq(FormResult::getCategory, formResult.getCategory())
                    .last(" limit 0,1 "));
            if (oldFormResult == null) {
                baseMapper.insert(formResult);
            } else {
                formResult.setId(oldFormResult.getId());
                baseMapper.updateById(formResult);
            }
        } else {
            oldFormResult = baseMapper.selectById(formResult.getId());
            baseMapper.updateById(formResult);
        }
        if (fillInIndex.equals(-1)) {
            // 基本信息表示患者入组了。
            String tenant = BaseContextHandler.getTenant();
            if (StrUtil.isNotEmpty(messageId)) {
                Long chatId = Long.parseLong(messageId);
                if (recommendReceipt != null && recommendReceipt.equals(CommonStatus.YES)) {
                    SaasGlobalThreadPool.execute(() -> updateImReceipt(chatId, tenant));
                }
            }

            // 疾病信息则需要设置患者疾病信息为已填。
            if (FormEnum.HEALTH_RECORD.eq(formResult.getCategory())) {
                patientApi.diseaseInformationStatus(userId);
                String userType = BaseContextHandler.getUserType();
                // 患者添加了疾病信息， 给医生发送一个提醒。
                SaasGlobalThreadPool.execute(() -> sendPatientCreateFormResult(formResult.getId(), tenant, userType));
            }
            formResultChange(formResult, oldFormResult, tenant);

            if (FormEnum.BASE_INFO.eq(formResult.getCategory())) {
                patientApi.completeEnterGroup(userId);
            }
        }
    }

    /**
     * 异步更新表单回执IM状态
     * @param chatId
     * @param tenantCode
     */
    public void updateImReceipt(Long chatId, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        imApi.updateImRemind(chatId);
    }


    /**
     * 患者添加一个表单结果后。 给医生发送互动消息
     * @param formResult
     * @param tenantCode
     */
    public void sendPatientCreateFormResult(Long id, String tenantCode, String userType) {

        BaseContextHandler.setTenant(tenantCode);
        if (UserType.PATIENT.equals(userType)) {
            FormResult formResult = baseMapper.selectOne(Wraps.<FormResult>lbQ()
                    .eq(SuperEntity::getId, id)
                    .select(SuperEntity::getId, FormResult::getCategory, FormResult::getBusinessId, FormResult::getName, FormResult::getUserId));
            Long patientId = formResult.getUserId();
            if (Objects.isNull(patientId)) {
                return;
            }
            NursingPlanPatientDTO patientDTO = new NursingPlanPatientDTO();
            List<Long> patientIds = new ArrayList<>();
            patientIds.add(patientId);
            patientDTO.setIds(patientIds);
            patientDTO.setTenantCode(tenantCode);
            R<List<NursingPlanPatientBaseInfoDTO>> patientApiBaseInfoForNursingPlan = patientApi.getBaseInfoForNursingPlan(patientDTO);
            FormEnum category = formResult.getCategory();
            for (NursingPlanPatientBaseInfoDTO planPatientBaseInfoDTO : patientApiBaseInfoForNursingPlan.getData()) {
                MsgPatientSystemMessageSaveDTO saveDTO = new MsgPatientSystemMessageSaveDTO();
                saveDTO.setPatientId(patientId);
                saveDTO.setPatientOpenId(planPatientBaseInfoDTO.getOpenId());
                saveDTO.setDoctorId(planPatientBaseInfoDTO.getDoctorId());
                saveDTO.setBusinessId(formResult.getId());
                saveDTO.setPatientCanSee(CommonStatus.NO);
                saveDTO.setReadStatus(CommonStatus.NO);
                saveDTO.setTenantCode(tenantCode);
                saveDTO.setDoctorCommentStatus(CommonStatus.NO);
                saveDTO.setDoctorReadStatus(CommonStatus.NO);
                saveDTO.setFunctionType(PlanFunctionTypeEnum.INTERACTIVE_MESSAGE.getCode());
                if (FormEnum.HEALTH_RECORD.eq(category)) {
                    String name = formResult.getName();
                    if (StrUtil.isNotBlank(name)) {
                        saveDTO.setPlanName(name);
                    } else {
                        saveDTO.setPlanName(FormEnum.HEALTH_RECORD.getDesc());
                    }
                    saveDTO.setInteractiveFunctionType(FormEnum.HEALTH_RECORD.getCode());
                } else {
                    Plan plan = planService.getById(Long.parseLong(formResult.getBusinessId()));
                    if (Objects.nonNull(plan)) {
                        saveDTO.setPlanName(plan.getName());
                        PlanFunctionTypeEnum enumByPlanType = PlanFunctionTypeEnum.getEnumByPlanType(plan.getPlanType(), plan.getFollowUpPlanType());
                        if (enumByPlanType != null) {
                            saveDTO.setInteractiveFunctionType(enumByPlanType.getCode());
                        }
                    }
                }
                saveDTO.setPushTime(LocalDateTime.now());
                saveDTO.setPushPerson(planPatientBaseInfoDTO.getName());
                msgPatientSystemMessageApi.saveSystemMessage(saveDTO);
            }
        }
    }

    /**
     * 检查这个创建日期所在的范围是否还可以提交。
     * @param formId
     * @param userId
     * @param createTime
     * @return
     */
    public boolean checkCanSubmit(Long formId, Long userId, LocalDateTime createTime) {
        Integer dailySubmissionLimit = getFormService().queryFormDailySubmissionLimit(formId);
        if (dailySubmissionLimit == null || dailySubmissionLimit.equals(0)) {
            return true;
        } else {
            LocalDateTime startTime = LocalDateTime.of(createTime.toLocalDate(), LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(createTime.toLocalDate(), LocalTime.MAX);
            Integer count = baseMapper.selectCount(Wraps.<FormResult>lbQ()
                    .gt(FormResult::getCreateTime, startTime)
                    .lt(FormResult::getCreateTime, endTime)
                    .eq(FormResult::getFormId, formId)
                    .eq(FormResult::getUserId, userId));
            if (count != null && count >= dailySubmissionLimit) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 新增一个表单
     * @param model
     * @return
     */
    @Override
    protected R<Boolean> handlerSave(FormResult model) {
        model.setCreateTime(LocalDateTime.now());
        String fieldRepeat = cleanFieldRepeat(model.getJsonContent());
        // 如果是基本信息 或 疾病信息
        if (FormEnum.HEALTH_RECORD.eq(model.getCategory())
                || FormEnum.BASE_INFO.eq(model.getCategory())) {
            fieldRepeat = resetBaseInfoOrHealthRecordField(fieldRepeat, model);
        }
        model.setJsonContent(fieldRepeat);
        // 用户提交的表单中有消息ID。 设置消息已打卡
        String messageId = model.getMessageId();
        Long planDetailTimeId = model.getPlanDetailTimeId();
        Long messageLog = null;
        if (StrUtil.isNotBlank(messageId)) {
            messageLog = Long.parseLong(messageId);
        }
        String tenant = BaseContextHandler.getTenant();
        // 解析表单结果
        formResultParsing(model);
        if (model.getCreateTime() == null) {
            model.setCreateTime(LocalDateTime.now());
        }
        boolean canSubmit = checkCanSubmit(model.getFormId(), model.getUserId(), model.getCreateTime());
        if (!canSubmit) {
            LocalDateTime localDate = model.getCreateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            String format = localDate.format(formatter);
            throw new BizException(format + "记录已存在，请删除记录后重新提交");
        }
        @NotNull(message = "填写人Id不能为空") Long userId = model.getUserId();
        if (Objects.nonNull(planDetailTimeId)) {
            Long reminderLog = reminderLogService.createOrUpdateReminderLog(planDetailTimeId, userId, messageLog);
            if (Objects.nonNull(reminderLog)) {
                model.setMessageId(reminderLog.toString());
            }
        } else if (Objects.nonNull(messageLog)) {
            if (model.getImRecommendReceipt() != null && model.getImRecommendReceipt().equals(CommonStatus.YES)) {
                // 更新聊天记录。 为填写状态，并发送通知给消息发送人。
                Long chatId = messageLog;
                SaasGlobalThreadPool.execute(() -> updateImReceipt(chatId, tenant));
            } else {
                reminderLogService.submitSuccess(messageLog);
            }
        } else {
            // 检查最近是否有系统提醒的随访表单, 如果有，咋将系统推送的消息设置为已打卡。并和此表单关联
            if (!FormEnum.HEALTH_RECORD.eq(model.getCategory()) && !FormEnum.BASE_INFO.eq(model.getCategory())) {
                if (StrUtil.isNotEmpty(model.getBusinessId())) {
                    Long message = reminderLogService.queryRecentlyUnReadMessage(model.getUserId(), Long.parseLong(model.getBusinessId()));
                    if (Objects.nonNull(message)) {
                        model.setMessageId(message.toString());
                        reminderLogService.submitSuccess(message);
                    }
                }
            }
        }

        model.setFirstSubmitTime(LocalDateTime.now());
        // 如果是 评分问卷。 设置结果展示的参数
        if (Objects.nonNull(model.getScoreQuestionnaire()) && model.getScoreQuestionnaire().equals(1)) {
            Form form = formService.getOne(Wraps.<Form>lbQ().eq(SuperEntity::getId, model.getFormId())
                    .select(SuperEntity::getId, Form::getShowScoreQuestionnaireAnalysis, Form::getScoreQuestionnaire));
            model.setShowScoreQuestionnaireAnalysis(form.getShowScoreQuestionnaireAnalysis());
            model.setScoreQuestionnaireAnalysis(form.getScoreQuestionnaireAnalysis());
        }
        baseMapper.insert(model);
        // 如果是 评分问卷。 开始计算用户提交的表单成绩。
        if (Objects.nonNull(model.getScoreQuestionnaire()) && model.getScoreQuestionnaire().equals(1)) {
            formResultScoreService.countFormResult(model);
        }
        String userType = BaseContextHandler.getUserType();
        SaasGlobalThreadPool.execute(() -> sendPatientCreateFormResult(model.getId(), tenant, userType));
        formResultChange(model, null, tenant);
        return R.success(true);
    }

    /**
     * 表单结果更新后，异步更新信息完整度
     * @param tenantCode
     * @param userId
     * @param formId
     * @param formResultId
     */
    public void syncUpdatePatientMonitoringTask(String tenantCode, Long userId, Long formId, Long formResultId) {

        BaseContextHandler.setTenant(tenantCode);
        try {
            R<Patient> patientR = patientApi.get(userId);
            if (patientR.getIsSuccess() && !Objects.isNull(patientR.getData())) {
                informationIntegrityMonitoringService.doSingleSynInformationIntegrityMonitoringTask(tenantCode ,patientR.getData(), formId, formResultId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【患者新增表单结果】执行同步任务信息完整度的逻辑(指定患者触发)失败！",e);
        }
    }

    /**
     * 从 result 中提取出来 监测指标字段
     * @param tenant
     * @param result
     */
    private void extractFieldValue(String tenant, FormResult result) {
        String jsonContent = result.getJsonContent();
        if (StringUtils.isNotEmptyString(jsonContent)) {
            List<FormFieldDto> formFields = JSON.parseArray(jsonContent, FormFieldDto.class);
            if (CollectionUtils.isNotEmpty(formFields)) {
                for (FormFieldDto field : formFields) {
                    // 监测指标
                    if (FormFieldExactType.MONITORING_INDICATORS.equals(field.getExactType())) {
                        Boolean firstWriteAsReferenceValue = field.getFirstWriteAsReferenceValue();
                        if (firstWriteAsReferenceValue != null && firstWriteAsReferenceValue) {
                            String parseValue = field.parseValue();
                            BaseContextHandler.setTenant(tenant);
                            Integer count = patientFormFieldReferenceService.count(Wraps.<PatientFormFieldReference>lbQ()
                                    .eq(PatientFormFieldReference::getBusinessId, result.getBusinessId())
                                    .eq(PatientFormFieldReference::getFieldId, field.getId())
                                    .eq(PatientFormFieldReference::getPatientId, result.getUserId()));
                            if (count != null && count > 0) {
                                return;
                            } else {
                                try {
                                    PatientFormFieldReference reference = PatientFormFieldReference.builder().patientId(result.getUserId())
                                            .businessId(result.getBusinessId())
                                            .fieldId(field.getId())
                                            .referenceValue(Double.parseDouble(parseValue))
                                            .targetValue(field.getTargetValue()).build();
                                    patientFormFieldReferenceService.save(reference);
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }
            }
        }

    }


    /**
     * 表单新增或修改之后。需要异步处理的事情
     * @param model
     * @param tenant
     */
    private void formResultChange(FormResult model, FormResult oldFormResult, String tenant ) {

        // 分析患者是否需要绑定标签
        @NotNull(message = "填写人Id不能为空") Long userId = model.getUserId();
        AttrBindChangeDto changeDto = new AttrBindChangeDto();
        changeDto.setFormResultId(model.getId());
        changeDto.setTenantCode(tenant);
        changeDto.setPatientId(userId);
        if (FormEnum.BASE_INFO.eq(model.getCategory()) ) {
            changeDto.setEvent(AttrBindEvent.BASE_INFO);
            String string = JSON.toJSONString(changeDto);
            String oldJsonContent = null;
            if (Objects.nonNull(oldFormResult)) {
                oldJsonContent = oldFormResult.getJsonContent();
            }
            redisTemplate.opsForList().leftPush(TagBindRedisKey.TENANT_ATTR_BIND, string);
            syncFormResultUpdatePatientInfo(FormEnum.BASE_INFO, model.getJsonContent(), oldJsonContent, userId, tenant);
        } else if (FormEnum.HEALTH_RECORD.eq(model.getCategory())) {
            syncFormResultUpdatePatientInfo(FormEnum.HEALTH_RECORD, model.getJsonContent(), null, userId, tenant);
            changeDto.setEvent(AttrBindEvent.HEALTH_RECORD);
            String string = JSON.toJSONString(changeDto);
            // 标签绑定事件 。吧标签绑定事件 放入redis 去执行
            redisTemplate.opsForList().leftPush(TagBindRedisKey.TENANT_ATTR_BIND, string);
        } else {
            // 选项跟踪业务监听
            SaasGlobalThreadPool.execute(() -> traceIntoResultService.traceIntoTask(model, tenant));

            // 监测数据的基线值目标值监听
            BIND_PATIENT_TAG_EXECUTOR.execute(() -> extractFieldValue(tenant, model));

            // 表单关联的统计任务监听
            SaasGlobalThreadPool.execute(() -> statisticsDataService.formResultChange(model, tenant));

            // 监测数据-监测计划-监测指标 -监控表单触发
            SaasGlobalThreadPool.execute(() -> indicatorsConditionMonitoringService.synIndicatorsConditionMonitoringTaskByFormChange(model, tenant));

            // 更新表单字段关联的标签监听
            changeDto.setEvent(AttrBindEvent.MONITORING_PLAN);
            String string = JSON.toJSONString(changeDto);
            redisTemplate.opsForList().leftPush(TagBindRedisKey.TENANT_ATTR_BIND, string);

            // 异步检查表单是否是 注射模式随访的 表单，并执行业务
            SaasGlobalThreadPool.execute(() -> injectionFormBusiness(model, tenant));
        }

        // 信息完整度的监听
        if (model.getNoUpdatePatientInformation() == null || model.getNoUpdatePatientInformation().equals(false)) {
            @NotNull(message = "表单Id不能为空") Long formId = model.getFormId();
            Long formResultId = model.getId();
            SaasGlobalThreadPool.execute(() -> syncUpdatePatientMonitoringTask(tenant, userId, formId, formResultId));
        }

    }

    /**
     * 注射的表单业务
     * 当表单是一个注射模式的计划推送的表单时，
     * 用户提交之后。需要根据计划生成下次提醒时间
     *
     */
    private void injectionFormBusiness(FormResult formResult, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        @Length(max = 100, message = "业务Id长度不能超过100") String businessId = formResult.getBusinessId();
        if (StrUtil.isEmpty(businessId)) {
            return;
        }
        Plan plan = planService.getById(Long.parseLong(businessId));
        Integer planModel = plan.getPlanModel();
        if (Objects.nonNull(planModel) && planModel.equals(1)) {
            FormResult result = baseMapper.selectOne(Wraps.<FormResult>lbQ()
                    .select(SuperEntity::getId, FormResult::getBusinessId, FormResult::getCreateTime, FormResult::getUserId)
                    .eq(FormResult::getUserId, formResult.getUserId())
                    .eq(FormResult::getBusinessId, businessId)
                    .orderByDesc(FormResult::getCreateTime)
                    .last(" limit 0,1 "));
            PatientNursingPlanService planService = SpringUtils.getBean(PatientNursingPlanService.class);
            planService.handleInjectionForm(plan, result);
        }
    }



    /**
     * 表单更新的业务
     * @param model
     * @return
     */
    @Override
    protected R<Boolean> handlerUpdateById(FormResult model) {
        // 解析表单结果
        FormResult oldFormResult = getById(model.getId());
        model.setCreateTime(oldFormResult.getCreateTime());
        String fieldRepeat = cleanFieldRepeat(model.getJsonContent());
        if (FormEnum.HEALTH_RECORD.eq(model.getCategory())
                || FormEnum.BASE_INFO.eq(model.getCategory())) {
            fieldRepeat = resetBaseInfoOrHealthRecordField(fieldRepeat, model);
        }
        model.setJsonContent(fieldRepeat);
        formResultParsing(model);
        String tenant = BaseContextHandler.getTenant();
        if (model.getImRecommendReceipt() != null && model.getImRecommendReceipt().equals(CommonStatus.YES)) {
            // 更新聊天记录。 为填写状态，并发送通知给消息发送人。
            String messageId = model.getMessageId();
            if (StrUtil.isNotEmpty(messageId)) {
                Long chatId = Long.parseLong(messageId);
                if (FormEnum.BASE_INFO.eq(model.getCategory())) {
                    SaasGlobalThreadPool.execute(() -> updateImReceipt(chatId, tenant));
                }
            }
        }
        baseMapper.updateById(model);
        formResultChange(model, oldFormResult,  tenant);

        // 表单结果修改后。产生 表单修改记录
        Long userId = BaseContextHandler.getUserId();
        String userType = BaseContextHandler.getUserType();
        // 当不是疾病信息时， 才需要记录历史记录。
        BIND_PATIENT_TAG_EXECUTOR.execute(() -> formResultBackUpService.backUp(userType, userId, tenant, oldFormResult, model));

        return R.success();
    }



    /**
     * 表单结果解析
     */
    private void formResultParsing(FormResult formResult) {
        String jsonContent = formResult.getJsonContent();
        if (StrUtil.isNotEmpty(jsonContent)) {
            formResult.setScoreQuestionnaire(FormUtil.isScoreQuestionnaire(jsonContent));
            formResult.setFormErrorResult(FormUtil.formErrorResult(jsonContent));
        }
        if (jsonContent.contains(FormWidgetType.NUMBER)) {
            formResult.setDataFeedBack(FormUtil.getPromptText(jsonContent));
        }
        // 判断控件里面有没有 checkTime 组件
        if (jsonContent.contains(FormFieldExactType.CHECK_TIME)) {

            // 使用checkTime重置表单结果的创建时间和更新时间，
            List<FormField> formFields = JSON.parseArray(jsonContent, FormField.class);
            for (FormField item : formFields) {
                if (FormFieldExactType.CHECK_TIME.equals(item.getExactType())) {
                    String values = item.getValues();
                    if (StrUtil.isEmpty(values)) {
                        continue;
                    }
                    JSONArray array = JSON.parseArray(values);
                    if (!array.isEmpty()) {
                        Object o = array.get(0);
                        JSONObject parse = JSON.parseObject(o.toString());
                        Object attrValue = parse.get("attrValue");
                        if (Objects.nonNull(attrValue)) {
                            String checkTimeString = attrValue.toString();
                            if (checkTimeString.contains("-")) {
                                Date date = DateUtils.date2str(checkTimeString, DateUtils.Y_M_D);
                                if (Objects.nonNull(date)) {
                                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    formResult.setCreateTime(LocalDateTime.of(localDate, LocalTime.now()));
                                    formResult.setCreateTime(LocalDateTime.of(localDate, LocalTime.now()));
                                }
                            }
                            if (checkTimeString.contains("/")) {
                                Date date = DateUtils.date2str(checkTimeString, DateUtils.Y_M_D_);
                                if (Objects.nonNull(date)) {
                                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    formResult.setCreateTime(LocalDateTime.of(localDate, LocalTime.now()));
                                    formResult.setCreateTime(LocalDateTime.of(localDate, LocalTime.now()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String jsonContent = "[{\"cancelRegistrationProcessAppear\":true,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"Avatar\",\"hasOtherOption\":2,\"id\":\"3a8db2cc2f344d0dad771604034b84dc\",\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"头像\",\"labelDesc\":\"\",\"modifyTags\":true,\"otherEnterRequired\":2,\"otherValueSize\":120,\"required\":false,\"values\":[{\"attrValue\":\"\"}],\"widgetType\":\"Avatar\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"errorWhenEmpty\":\"\",\"exactType\":\"Name\",\"hasOtherOption\":2,\"id\":\"ddfb78c94f784a31aada103143b2072c\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"姓名\",\"labelDesc\":\"\",\"maxEnterNumber\":30,\"maxValue\":\"0\",\"minValue\":\"0\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"请填写您的真实姓名\",\"required\":true,\"values\":[{\"attrValue\":\"陈蔚哲\"}],\"widgetType\":\"FullName\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"errorWhenEmpty\":\"\",\"exactType\":\"Mobile\",\"hasOtherOption\":2,\"id\":\"cc289f567b934007a7f5f44d4393366b\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"联系方式\",\"labelDesc\":\"\",\"maxEnterNumber\":11,\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"请输入手机号码\",\"required\":true,\"values\":[{\"attrValue\":\"15067778802\"}],\"widgetType\":\"SingleLineText\"},{\"defineChooseDate\":2,\"enableSmsCheck\":false,\"hasOtherOption\":2,\"id\":\"a692fa20e06c41b3ae2c627edc2c0ee3\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"职业\",\"labelDesc\":\"\",\"modifyTags\":false,\"options\":[{\"attrValue\":\"久站工作：如教师，厨师，服务员，导购员，乘务员，交警，工厂流水线等工人，美容美发\",\"desc\":\"\",\"id\":\"f2e219ddfc544c2ca233f7feb205952e\",\"notScored\":false,\"score\":1.0,\"scoreHide\":false,\"select\":false},{\"attrValue\":\"久坐工作：如办公室文职类，设计类，编辑类，金融类，IT类\",\"desc\":\"\",\"id\":\"1a8713a931364d8cbf2f09e69824522e\",\"notScored\":false,\"score\":2.0,\"scoreHide\":false,\"select\":true},{\"attrValue\":\"均不是\",\"desc\":\"\",\"id\":\"3be943917bcc4d5b91a9bfbf1da7a416\",\"notScored\":false,\"scoreHide\":false,\"select\":false}],\"otherEnterRequired\":2,\"otherValueSize\":120,\"required\":false,\"values\":[{\"valueText\":\"久坐工作：如办公室文职类，设计类，编辑类，金融类，IT类\",\"attrValue\":\"1a8713a931364d8cbf2f09e69824522e\"}],\"widgetType\":\"CheckBox\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"Gender\",\"hasOtherOption\":2,\"id\":\"37464017ea6d44db8cecb2e99f987d32\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"性别\",\"modifyTags\":false,\"options\":[{\"attrValue\":\"男\",\"id\":\"bbd0a25b1c6f4ff6bdd5c3e71a4a59c6\",\"notScored\":false,\"scoreHide\":false},{\"attrValue\":\"女\",\"id\":\"983a4d8095554459babeb15baf601e45\",\"notScored\":false,\"scoreHide\":false}],\"otherEnterRequired\":2,\"otherValueSize\":120,\"required\":true,\"values\":[{\"valueText\":\"男\",\"attrValue\":\"bbd0a25b1c6f4ff6bdd5c3e71a4a59c6\"}],\"widgetType\":\"Radio\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":1,\"defineChooseDateType\":1,\"enableSmsCheck\":false,\"exactType\":\"Birthday\",\"hasOtherOption\":2,\"id\":\"e93085c79e674a2fab3136ef3da8a073\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"出生年月\",\"labelDesc\":\"\",\"minValue\":\"1945-01-01\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"\",\"required\":true,\"values\":[{\"attrValue\":\"1995-08-14\",\"attrText\":\"1995-08-14\"}],\"widgetType\":\"Date\"},{\"addressDetailSize\":120,\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"hasAddressDetail\":1,\"hasAddressDetailRequired\":2,\"hasOtherOption\":2,\"id\":\"203d6414dbbb4fe6b175945ab3ee279f\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"居住所在地\",\"labelDesc\":\"\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"\",\"required\":false,\"value\":\"雁荡镇白溪街村\",\"values\":[{\"attrValue\":[\"浙江省\",\"温州市\",\"乐清市\"]}],\"widgetType\":\"Address\"},{\"cancelRegistrationProcessAppear\":false,\"defaultValue\":\"手术时间已确定\",\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"FollowUpStage\",\"hasOtherOption\":2,\"id\":\"11af34afcd204cdeb0a648be07c60ac5\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"手术时间\",\"labelDesc\":\"\",\"modifyTags\":false,\"options\":[{\"attrValue\":\"暂未预约手术\",\"desc\":\"\",\"id\":\"336c9225f4a34f1db107f9f2fed91d08\",\"notScored\":false,\"score\":1.0,\"scoreHide\":false},{\"attrValue\":\"已预约手术\",\"desc\":\"\",\"id\":\"fbc6e52b4e5c4008aec51095d247d866\",\"notScored\":false,\"questions\":[{\"defineChooseDate\":2,\"enableSmsCheck\":false,\"hasOtherOption\":2,\"id\":\"e08cc62c35f94f14aff377988abc903d\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"手术具体时间\",\"labelDesc\":\"\",\"minValue\":\"1945-01-01\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"请选择时间\",\"required\":true,\"values\":[{}],\"widgetType\":\"Date\"}],\"score\":2.0,\"scoreHide\":false}],\"otherEnterRequired\":2,\"otherValueSize\":120,\"required\":true,\"values\":[{\"score\":\"1.0\",\"valueText\":\"暂未预约手术\",\"attrValue\":\"336c9225f4a34f1db107f9f2fed91d08\"}],\"widgetType\":\"Radio\"}]\n";
        String oldJsonContent = "[{\"cancelRegistrationProcessAppear\":true,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"Avatar\",\"hasOtherOption\":2,\"id\":\"3a8db2cc2f344d0dad771604034b84dc\",\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"头像\",\"labelDesc\":\"\",\"modifyTags\":true,\"otherEnterRequired\":2,\"otherValueSize\":120,\"required\":false,\"values\":[{\"attrValue\":\"\"}],\"widgetType\":\"Avatar\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"errorWhenEmpty\":\"\",\"exactType\":\"Name\",\"hasOtherOption\":2,\"id\":\"ddfb78c94f784a31aada103143b2072c\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"姓名\",\"labelDesc\":\"\",\"maxEnterNumber\":30,\"maxValue\":\"0\",\"minValue\":\"0\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"请填写您的真实姓名\",\"required\":true,\"values\":[{\"attrValue\":\"陈蔚哲\"}],\"widgetType\":\"FullName\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"errorWhenEmpty\":\"\",\"exactType\":\"Mobile\",\"hasOtherOption\":2,\"id\":\"cc289f567b934007a7f5f44d4393366b\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"联系方式\",\"labelDesc\":\"\",\"maxEnterNumber\":11,\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"请输入手机号码\",\"required\":true,\"values\":[{\"attrValue\":\"15067778802\"}],\"widgetType\":\"SingleLineText\"},{\"defineChooseDate\":2,\"enableSmsCheck\":false,\"hasOtherOption\":2,\"id\":\"a692fa20e06c41b3ae2c627edc2c0ee3\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"职业\",\"labelDesc\":\"\",\"modifyTags\":false,\"options\":[{\"attrValue\":\"久站工作：如教师，厨师，服务员，导购员，乘务员，交警，工厂流水线等工人，美容美发\",\"desc\":\"\",\"id\":\"f2e219ddfc544c2ca233f7feb205952e\",\"notScored\":false,\"score\":1.0,\"scoreHide\":false,\"select\":false},{\"attrValue\":\"久坐工作：如办公室文职类，设计类，编辑类，金融类，IT类\",\"desc\":\"\",\"id\":\"1a8713a931364d8cbf2f09e69824522e\",\"notScored\":false,\"score\":2.0,\"scoreHide\":false,\"select\":true},{\"attrValue\":\"均不是\",\"desc\":\"\",\"id\":\"3be943917bcc4d5b91a9bfbf1da7a416\",\"notScored\":false,\"scoreHide\":false,\"select\":false}],\"otherEnterRequired\":2,\"otherValueSize\":120,\"required\":false,\"values\":[{\"valueText\":\"久坐工作：如办公室文职类，设计类，编辑类，金融类，IT类\",\"attrValue\":\"1a8713a931364d8cbf2f09e69824522e\"}],\"widgetType\":\"CheckBox\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"Gender\",\"hasOtherOption\":2,\"id\":\"37464017ea6d44db8cecb2e99f987d32\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"性别\",\"modifyTags\":false,\"options\":[{\"attrValue\":\"男\",\"id\":\"bbd0a25b1c6f4ff6bdd5c3e71a4a59c6\",\"notScored\":false,\"scoreHide\":false},{\"attrValue\":\"女\",\"id\":\"983a4d8095554459babeb15baf601e45\",\"notScored\":false,\"scoreHide\":false}],\"otherEnterRequired\":2,\"otherValueSize\":120,\"required\":true,\"values\":[{\"valueText\":\"男\",\"attrValue\":\"bbd0a25b1c6f4ff6bdd5c3e71a4a59c6\"}],\"widgetType\":\"Radio\"},{\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":1,\"defineChooseDateType\":1,\"enableSmsCheck\":false,\"exactType\":\"Birthday\",\"hasOtherOption\":2,\"id\":\"e93085c79e674a2fab3136ef3da8a073\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"出生年月\",\"labelDesc\":\"\",\"minValue\":\"1945-01-01\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"\",\"required\":true,\"values\":[{\"attrValue\":\"1995-08-14\",\"attrText\":\"1995-08-14\"}],\"widgetType\":\"Date\"},{\"addressDetailSize\":120,\"cancelRegistrationProcessAppear\":false,\"defineChooseDate\":2,\"enableSmsCheck\":false,\"hasAddressDetail\":1,\"hasAddressDetailRequired\":2,\"hasOtherOption\":2,\"id\":\"203d6414dbbb4fe6b175945ab3ee279f\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"居住所在地\",\"labelDesc\":\"\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"\",\"required\":false,\"value\":\"雁荡镇白溪街村\",\"values\":[{\"attrValue\":[\"浙江省\",\"温州市\",\"乐清市\"]}],\"widgetType\":\"Address\"},{\"cancelRegistrationProcessAppear\":false,\"defaultValue\":\"手术时间已确定\",\"defineChooseDate\":2,\"enableSmsCheck\":false,\"exactType\":\"FollowUpStage\",\"hasOtherOption\":2,\"id\":\"11af34afcd204cdeb0a648be07c60ac5\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"手术时间\",\"labelDesc\":\"\",\"modifyTags\":false,\"options\":[{\"attrValue\":\"暂未预约手术\",\"desc\":\"\",\"id\":\"336c9225f4a34f1db107f9f2fed91d08\",\"notScored\":false,\"score\":1.0,\"scoreHide\":false},{\"attrValue\":\"已预约手术\",\"desc\":\"\",\"id\":\"fbc6e52b4e5c4008aec51095d247d866\",\"notScored\":false,\"questions\":[{\"defineChooseDate\":2,\"enableSmsCheck\":false,\"hasOtherOption\":2,\"id\":\"e08cc62c35f94f14aff377988abc903d\",\"isUpdatable\":1,\"itemAfterHasEnter\":2,\"itemAfterHasEnterRequired\":2,\"itemAfterHasEnterSize\":120,\"label\":\"手术具体时间\",\"labelDesc\":\"\",\"minValue\":\"1945-01-01\",\"modifyTags\":false,\"otherEnterRequired\":2,\"otherValueSize\":120,\"placeholder\":\"请选择时间\",\"required\":true,\"values\":[{}],\"widgetType\":\"Date\"}],\"score\":2.0,\"scoreHide\":false}],\"otherEnterRequired\":2,\"otherValueSize\":120,\"required\":true,\"values\":[{\"score\":\"1.0\",\"valueText\":\"暂未预约手术\",\"attrValue\":\"336c9225f4a34f1db107f9f2fed91d08\"}],\"widgetType\":\"Radio\"}]\n";

        FormExactFieldValue formExactFieldValue = FormExactFieldValue.parse(jsonContent, oldJsonContent);
        LocalDate followUpTime = formExactFieldValue.getFollowUpTime();
        System.out.println(followUpTime);
    }

    /**
     * 基本信息 疾病信息更新后。异步同步患者信息
     */
    public void syncFormResultUpdatePatientInfo(FormEnum category, String jsonContent, String oldJsonContent, Long userId, String tenantCode) {

        boolean doParsing = (FormEnum.BASE_INFO.eq(category) || FormEnum.HEALTH_RECORD.eq(category));
        if (!doParsing) {
            return;
        }
        if (Objects.isNull(userId)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        R<Patient> patientR = patientApi.get(userId);
        if (patientR.getIsError() || Objects.isNull(patientR.getData())) {
            return;
        }
        if (FormEnum.HEALTH_RECORD.eq(category)) {
            // 查询最新的疾病信息表单去 同步患者数据
            FormResult result = getBasicOrHealthFormResult(userId, FormEnum.HEALTH_RECORD);
            if (Objects.isNull(result)) {
                return;
            }
            jsonContent = result.getJsonContent();
        }
        Patient patient = patientR.getData();
        FormExactFieldValue formExactFieldValue = FormExactFieldValue.parse(jsonContent, oldJsonContent);
        if (formExactFieldValue != null) {
            patient.setHeight(formExactFieldValue.getHeight());
            patient.setWeight(formExactFieldValue.getWeight());
            patient.setSex(formExactFieldValue.getGender());
            patient.setMobile(formExactFieldValue.getMobile());
            patient.setDiagnosisId(formExactFieldValue.getDiagnoseId());
            patient.setDiagnosisName(formExactFieldValue.getDiagnose());
            patient.setName(formExactFieldValue.getName());
            patient.setNickName(formExactFieldValue.getName());
            patient.setAvatar(StrUtil.isNotEmpty(formExactFieldValue.getAvatar()) ? formExactFieldValue.getAvatar() : null);
            patient.setBirthday(formExactFieldValue.getBirthday());
            LocalDate followUpTime = formExactFieldValue.getFollowUpTime();
            LocalDate customerFollowUpTime = formExactFieldValue.getCustomerFollowUpTime();
            if (Objects.nonNull(customerFollowUpTime)) {
                patient.setNursingTime(LocalDateTime.of(customerFollowUpTime, LocalTime.now()));
            } else if (Objects.nonNull(followUpTime)) {
                patient.setNursingTime(LocalDateTime.of(followUpTime, LocalTime.now()));
            }
            patient.setFollowStageId(formExactFieldValue.getFollowStageId());
            patient.setFollowStageName(formExactFieldValue.getFollowStageName());
        }
        patient.setCompleteEnterGroupTime(null);
        patient.setDiseaseInformationStatus(null);
        patient.setAgreeAgreement(null);
        patientApi.update(patient);
    }

    /**
     * 用户没有填写表单结果时。 从表单中获取 X轴 和Y轴的数据
     * @param formFields
     * @param yDataMap
     * @param xDataList
     * @param lineChart
     * @param referenceMap
     */
    private void formResultEmptyHandle(List<FormFieldDto> formFields,
                                       Map<String, MonitorYData> yDataMap,
                                       List<LocalDateTime> xDataList,
                                       MonitorLineChart lineChart,
                                       Map<String, PatientFormFieldReference> referenceMap){
        for (FormFieldDto field : formFields) {

            // 判断是否 展示在 趋势图中去
            if (Objects.nonNull(field.getShowTrend()) && field.getShowTrend().equals(1)) {
                MonitorYData yData = yDataMap.get(field.getId());
                if (Objects.isNull(yData)) {
                    yData = new MonitorYData();
                    PatientFormFieldReference reference = referenceMap.get(field.getId());
                    yData.addField(field, reference);
                }
                yData.addNullYData(xDataList.size());
                yData.setyAxis();
                yDataMap.put(field.getId(), yData);
            }
        }
        Collection<MonitorYData> values = yDataMap.values();
        lineChart.setYDataList(new ArrayList<>(values));
        lineChart.setXDataList(xDataList);

    }

    /**
     * 查询患者的目标值。基准值
     * @param patientId
     * @param businessId
     * @return
     */
    private Map<String, PatientFormFieldReference> queryPatientReference(Long patientId, String businessId) {
        List<PatientFormFieldReference> references = patientFormFieldReferenceService.list(Wraps.<PatientFormFieldReference>lbQ()
                .eq(PatientFormFieldReference::getPatientId, patientId)
                .eq(PatientFormFieldReference::getBusinessId, businessId));
        Map<String, PatientFormFieldReference> referenceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(references)) {
            referenceMap = references.stream().collect(Collectors.toMap(PatientFormFieldReference::getFieldId, item -> item));
        }
        return referenceMap;
    }

    /**
     * 查询用户的单日曲线
     * @param patientId
     * @param businessId
     * @param monitorDayParams
     * @param lineChart
     */
    private void oneDayCurve(Long patientId, String businessId, MonitorDayParams monitorDayParams, List<FormFieldDto> formFields,
                             Map<String, FormFieldDto> formFieldDtoMap, MonitorLineChart lineChart) {
        LocalDate oneDayDate = monitorDayParams.getOneDayDate();

        LocalDateTime startDateTime = LocalDateTime.of(oneDayDate, LocalTime.of(0,0,0,0));
        LocalDateTime endDateTime = LocalDateTime.of(oneDayDate, LocalTime.of(23,59,59));

        List<FormResult> resultList = baseMapper.selectList(Wraps.<FormResult>lbQ()
                .eq(FormResult::getUserId, patientId)
                .eq(FormResult::getBusinessId, businessId)
                .gt(FormResult::getCreateTime, startDateTime)
                .le(FormResult::getCreateTime, endDateTime)
                .orderByAsc(FormResult::getCreateTime));

        if (CollUtil.isEmpty(resultList)) {
            resultList = new ArrayList<>();
        }
        // 一天最多一条数据。应该也不会很多。
        Map<String, MonitorYData> yDataMap = new HashMap<>(10);
        Map<String, MonitorYData> yMonitorEvent = new HashMap<>(10);
        Map<String, PatientFormFieldReference> referenceMap = queryPatientReference(patientId, businessId);
        List<LocalDateTime> xDataList = new ArrayList<>();
        if (CollUtil.isEmpty(resultList)) {
            formResultEmptyHandle(formFields, yDataMap, xDataList, lineChart, referenceMap);
            return;
        } else {
            String value;
            for (FormResult result : resultList) {
                String jsonContent = result.getJsonContent();
                List<FormFieldDto> resultFormFields = JSON.parseArray(jsonContent, FormFieldDto.class);
                xDataList.add(result.getCreateTime());
                for (FormFieldDto field : resultFormFields) {
                    FormFieldDto fieldDto = formFieldDtoMap.get(field.getId());
                    // 判断是否 展示在 趋势图中去
                    if (Objects.nonNull(fieldDto.getShowTrend()) && fieldDto.getShowTrend().equals(1)) {
                        value = field.parseResultValues();
                        MonitorYData yData = yDataMap.get(field.getId());
                        if (Objects.isNull(yData)) {
                            yData = new MonitorYData();
                            PatientFormFieldReference reference = referenceMap.get(field.getId());
                            yData.addField(fieldDto, reference);
                        }
                        if (StringUtils.isEmpty(value)) {
                            yData.addYData(null);
                        } else {
                            yData.addYData(value);
                        }
                        yDataMap.put(field.getId(), yData);
                    } else {
                        // 判断是否是 监控事件
                        if (FormFieldExactType.MonitoringEvents.equals(field.getExactType())) {
                            value = field.parseResultValues();
                            MonitorYData monitorYData = yMonitorEvent.get(field.getId());
                            if (Objects.isNull(monitorYData)) {
                                monitorYData = new MonitorYData();
                                monitorYData.addField(fieldDto, null);
                            }
                            if (StrUtil.isEmpty(value)) {
                                monitorYData.addYData(null);
                                // 由于是单选事件。所以直接使用前缀匹配其他
                            } else if (value.startsWith("其他")) {
                                if (StrUtil.isEmpty(field.getOtherValue())) {
                                    monitorYData.addYData("-");
                                } else {
                                    monitorYData.addYData(field.getOtherValue());
                                }
                            } else {
                                monitorYData.addYData(value);
                            }
                            yMonitorEvent.put(field.getId(), monitorYData);
                        }
                    }
                }
            }
        }

        Collection<MonitorYData> values = yDataMap.values();
        Collection<MonitorYData> monitorYData = yMonitorEvent.values();
        ArrayList<MonitorYData> yData = new ArrayList<>(values);
        ArrayList<MonitorYData> monitorYDataArrayList = new ArrayList<>(monitorYData);
        for (MonitorYData yDatum : yData) {
            yDatum.setyAxis();
        }
        lineChart.setYDataList(yData);
        lineChart.setXDataList(xDataList);
        lineChart.setYMonitorEvent(monitorYDataArrayList);
    }

    /**
     * 新版趋势图
     * 添加了监测事件
     * @param patientId
     * @param businessId
     * @param monitorDayParams
     * @return
     */
    @Override
    public MonitorLineChart monitorLineChart(Long patientId, String businessId, MonitorDayParams monitorDayParams) {
        MonitorLineChart lineChart = new MonitorLineChart();
        Form form = getFormService().getFormByRedis(null, businessId);
        if (Objects.isNull(form)) {
            lineChart.setShowTrend(false);
            return lineChart;
        }
        if (form.getShowTrend() == null) {
            lineChart.setShowTrend(false);
        } else {
            if (form.getShowTrend() == 1) {
                lineChart.setShowTrend(true);
            } else {
                lineChart.setShowTrend(false);
                return lineChart;
            }
        }
        // 查询患者信息。得出患者的入组时间
        MonitorDayType monitorDayType = monitorDayParams.getMonitorDayType();
        LocalDateTime enterGroupTime = null;
        if (MonitorDayType.All.equals(monitorDayType)) {
            R<Patient> patientR = patientApi.get(patientId);
            Patient patient = patientR.getData();
            if (Objects.isNull(patient)) {
                throw new BizException("请求用户信息失败");
            }
            enterGroupTime = patient.getCompleteEnterGroupTime();
            if (Objects.isNull(enterGroupTime)) {
                enterGroupTime = LocalDateTime.now();
            }
        }

        Integer defaultQuery = monitorDayParams.getDefaultQuery();
        if (defaultQuery != null && defaultQuery == 1) {
            // 如果表单支持单日曲线，则默认为单日曲线模式。
            if (form.getOneDayCurve() != null && form.getOneDayCurve() == 1) {
                monitorDayParams.setMonitorDayType(MonitorDayType.OneDay);
                monitorDayParams.setOneDayDate(LocalDate.now());
            }
        }
        String fieldsJson = form.getFieldsJson();
        List<FormFieldDto> formFields = JSON.parseArray(fieldsJson, FormFieldDto.class);
        Map<String, FormFieldDto> formFieldDtoMap = formFields.stream().collect(Collectors.toMap(FormField::getId, item -> item));
        if (MonitorDayType.OneDay.equals(monitorDayParams.getMonitorDayType())) {
            // 走单日曲线的 折线图查询逻辑
            oneDayCurve(patientId, businessId, monitorDayParams,  formFields, formFieldDtoMap, lineChart);
            lineChart.setOneDayCurve(1);
            return lineChart;
        }

        // 得出一个趋势图的开始时间和结束时间
        List<LocalDateTime> startDateEndDate = MonitorDayType.getStartDateEndDate(enterGroupTime, monitorDayType,
                monitorDayParams.getCustomizeStartDate(), monitorDayParams.getCustomizeEndDate());
        if (CollUtil.isEmpty(startDateEndDate)) {
            return lineChart;
        }
        LocalDateTime startDateTime = startDateEndDate.get(0);
        LocalDateTime endDateTime = startDateEndDate.get(1);
        QueryWrapper<FormResult> queryWrapper = Wrappers.query();

        queryWrapper.select("id", "create_time");
        queryWrapper.eq("user_id", patientId);
        queryWrapper.eq("business_id", businessId);
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(" create_time in ( SELECT a.create_time from (SELECT  max( create_time ) AS create_time, " +
                " date_format( create_time, '%Y-%m-%d' ) AS createDay FROM  t_custom_form_result  WHERE ( user_id =")
                .append(patientId).append(" AND business_id = '")
                .append(businessId).append("'");
        if (MonitorDayType.All.equals(monitorDayType)) {
            String endDateTimeString = endDateTime.toString();
            stringBuffer.append(" AND create_time < '").append(endDateTimeString.replace("T", " ")).append("' ) ");
        } else {
            String startDateTimeString = startDateTime.toString();
            String endDateTimeString = endDateTime.toString();
            stringBuffer.append(" AND create_time BETWEEN '")
                    .append(startDateTimeString.replace("T", " "))
                    .append("' AND '")
                    .append(endDateTimeString.replace("T", " "))
                    .append("' ) ");
        }
        stringBuffer.append(" AND tenant_code = '").append(BaseContextHandler.getTenant()).append("' ").append(" GROUP BY ").append(" createDay) as a)");
        queryWrapper.apply(stringBuffer.toString());
        queryWrapper.orderByDesc("create_time");
        List<Map<String, Object>> selectMaps = baseMapper.selectMaps(queryWrapper);
        List<Long> formResultIds = new ArrayList<>();
        // 展示全部数据时， 最早时间取最早的表单创建时间。 无数据时默认为患者入组时间

        if (CollUtil.isNotEmpty(selectMaps)) {
            // 保证一天内只有一条表单结果
            Set<LocalDateTime> createDateTime = new HashSet<>();
            for (Map<String, Object> selectMap : selectMaps) {
                Object id = selectMap.get("id");
                Object create_time = selectMap.get("create_time");
                LocalDateTime minLocalDateTime = LocalDateTime.parse(create_time.toString().substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                if (MonitorDayType.All.equals(monitorDayType)) {
                    if (minLocalDateTime.isBefore(startDateTime)) {
                        startDateTime = minLocalDateTime;
                    }
                }
                if (createDateTime.contains(minLocalDateTime)) {
                    continue;
                }
                createDateTime.add(minLocalDateTime);
                if (Objects.nonNull(id)) {
                    long formResultId = Long.parseLong(id.toString());
                    formResultIds.add(formResultId);
                }
            }
        }
        List<FormResult> resultList;
        if (CollUtil.isEmpty(formResultIds)) {
            resultList = new ArrayList<>();
        } else {
            // 根据分组后的列表id。查询id对应的表单结果
            resultList = baseMapper.selectList(Wraps.<FormResult>lbQ()
                    .in(SuperEntity::getId, formResultIds)
                    .orderByDesc(Entity::getCreateTime));
        }
        // 一天最多一条数据。应该也不会很多。
        Map<String, MonitorYData> yDataMap = new HashMap<>(10);
        Map<String, MonitorYData> yMonitorEvent = new HashMap<>(1);

        Map<String, PatientFormFieldReference> referenceMap = queryPatientReference(patientId, businessId);

        List<LocalDateTime> xDataList = MonitorDayType.getXDate(startDateTime, endDateTime);
        if (CollUtil.isEmpty(resultList)) {

            formResultEmptyHandle(formFields, yDataMap, xDataList, lineChart, referenceMap);

            return lineChart;
        } else {
            FormResult result;
            // 当日期没有表单时， 给Y轴填 null
            int needNullNumber = 0;
            Map<LocalDate, FormResult> collect = resultList.stream().collect(Collectors.toMap(item -> item.getCreateTime().toLocalDate(), item -> item, (o1, o2) -> o2));
            for (LocalDateTime dateTime : xDataList) {
                result = collect.get(dateTime.toLocalDate());
                if (Objects.isNull(result)) {
                    needNullNumber++;
                    continue;
                }
                String jsonContent = result.getJsonContent();
                List<FormFieldDto> resultFormFields = JSON.parseArray(jsonContent, FormFieldDto.class);
                for (FormFieldDto field : resultFormFields) {
                    FormFieldDto fieldDto = formFieldDtoMap.get(field.getId());
                    // 判断是否 展示在 趋势图中去
                    if (Objects.nonNull(fieldDto.getShowTrend()) && fieldDto.getShowTrend().equals(1)) {
                        String value = field.parseResultValues();
                        MonitorYData yData = yDataMap.get(field.getId());
                        if (Objects.isNull(yData)) {
                            yData = new MonitorYData();
                            PatientFormFieldReference reference = referenceMap.get(field.getId());
                            yData.addField(fieldDto, reference);
                        }
                        if (needNullNumber > 0) {
                            yData.addNullYData(needNullNumber);
                        }
                        if (StringUtils.isEmpty(value)) {
                            yData.addYData(null);
                        } else {
                            yData.addYData(value);
                        }
                        yDataMap.put(field.getId(), yData);
                    } else {
                        // 判断是否是 监控事件
                        if (FormFieldExactType.MonitoringEvents.equals(fieldDto.getExactType())) {
                            String value = field.parseResultValues();
                            MonitorYData monitorYData = yMonitorEvent.get(field.getId());
                            if (Objects.isNull(monitorYData)) {
                                monitorYData = new MonitorYData();
                                monitorYData.addField(fieldDto, null);
                            }
                            if (needNullNumber > 0) {
                                monitorYData.addNullYData(needNullNumber);
                            }
                            if (StrUtil.isEmpty(value)) {
                                monitorYData.addYData(null);
                            } else if (value.startsWith("其他")) {
                                if (StrUtil.isEmpty(field.getOtherValue())) {
                                    monitorYData.addYData("-");
                                } else {
                                    monitorYData.addYData(field.getOtherValue());
                                }
                            } else {
                                monitorYData.addYData(value);
                            }
                            yMonitorEvent.put(field.getId(), monitorYData);
                        }
                    }
                }
                needNullNumber = 0;
            }
            Collection<MonitorYData> values = yDataMap.values();
            Collection<MonitorYData> monitorYData = yMonitorEvent.values();
            ArrayList<MonitorYData> yData = new ArrayList<>(values);
            ArrayList<MonitorYData> monitorYDataArrayList = new ArrayList<>(monitorYData);
            if (needNullNumber > 0) {
                for (MonitorYData yDatum : yData) {
                    yDatum.addNullYData(needNullNumber);
                }
                for (MonitorYData data : monitorYDataArrayList) {
                    data.addNullYData(needNullNumber);
                }
            }
            for (MonitorYData yDatum : yData) {
                yDatum.setyAxis();
            }
            lineChart.setYDataList(yData);
            lineChart.setXDataList(xDataList);
            lineChart.setYMonitorEvent(monitorYDataArrayList);
            return lineChart;
        }
    }

    /**
     * 监测计划 折线图数据
     * @param patientId
     * @param businessId
     * @param monitorDateLineType
     * @return
     */
    @Deprecated
    @Override
    public MonitorLineChart monitorLineChart(Long patientId, String businessId, MonitorDateLineType monitorDateLineType) {

        MonitorLineChart lineChart = new MonitorLineChart();

        Form form = getFormService().getFormByRedis(null, businessId);
        lineChart.setShowTrend(true);
        if (Objects.isNull(form) || Objects.isNull(form.getShowTrend()) || form.getShowTrend().equals(0)) {
            lineChart.setShowTrend(false);
            return lineChart;
        }
        // 获取折线图数据所在的时间维度
        List<LocalDateTime> startDateEndDate = MonitorDateLineType.getStartDateEndDate(monitorDateLineType);
        if (CollUtil.isEmpty(startDateEndDate)) {
            return lineChart;
        }

        // 查询 from 结果
        List<FormResult> resultList = baseMapper.selectList(Wraps.<FormResult>lbQ()
                .eq(FormResult::getUserId, patientId)
                .eq(FormResult::getBusinessId, businessId)
                .between(Entity::getCreateTime, startDateEndDate.get(0), startDateEndDate.get(1))
                .orderByDesc(Entity::getCreateTime));

        // 过滤 resultList 数据。 周 月。只保留每日最后一次监测. 此时数据表单按时间从正序 排列
        Map<LocalDateTime, FormResult> filterResultByDate = filterResultByDate(resultList, monitorDateLineType);
        Map<String, MonitorYData> yDataMap = new HashMap<>(10);
        List<PatientFormFieldReference> references = patientFormFieldReferenceService.list(Wraps.<PatientFormFieldReference>lbQ()
                .eq(PatientFormFieldReference::getPatientId, patientId)
                .eq(PatientFormFieldReference::getBusinessId, businessId));
        Map<String, PatientFormFieldReference> referenceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(references)) {
            referenceMap = references.stream().collect(Collectors.toMap(PatientFormFieldReference::getFieldId, item -> item));
        }
        // 用户没有添加表单时 。
        if (CollUtil.isEmpty(filterResultByDate)) {
            List<LocalDateTime> xDataList =  monitorXDataList(startDateEndDate, monitorDateLineType);
            String jsonContent = form.getFieldsJson();
            List<FormFieldDto> formFields = JSON.parseArray(jsonContent, FormFieldDto.class);
            for (FormFieldDto field : formFields) {

                // 判断是否 展示在 趋势图中去
                if (Objects.nonNull(field.getShowTrend()) && field.getShowTrend().equals(1)) {
                    MonitorYData yData = yDataMap.get(field.getId());
                    if (Objects.isNull(yData)) {
                        yData = new MonitorYData();
                        PatientFormFieldReference reference = referenceMap.get(field.getId());
                        yData.addField(field, reference);
                    }
                    yData.addNullYData(xDataList.size());
                    yDataMap.put(field.getId(), yData);
                }
            }
            Collection<MonitorYData> values = yDataMap.values();
            lineChart.setYDataList(new ArrayList<>(values));
            lineChart.setXDataList(xDataList);
            return lineChart;
        } else {
            List<LocalDateTime> xDataList =  monitorXDataList(startDateEndDate, monitorDateLineType);;
            FormResult result;
            // 当日期没有表单时， 给Y轴填 null
            int needNullNumber = 0;
            for (LocalDateTime dateTime : xDataList) {
                result = filterResultByDate.get(dateTime);
                if (Objects.isNull(result)) {
                    needNullNumber++;
                    continue;
                }
                String jsonContent = result.getJsonContent();
                List<FormFieldDto> formFields = JSON.parseArray(jsonContent, FormFieldDto.class);
                for (FormFieldDto field : formFields) {

                    // 判断是否 展示在 趋势图中去
                    if (Objects.nonNull(field.getShowTrend()) && field.getShowTrend().equals(1)) {
                        String value = field.parseResultValues();
                        MonitorYData yData = yDataMap.get(field.getId());
                        if (Objects.isNull(yData)) {
                            yData = new MonitorYData();
                            PatientFormFieldReference reference = referenceMap.get(field.getId());
                            yData.addField(field, reference);
                        }
                        if (needNullNumber > 0) {
                            yData.addNullYData(needNullNumber);
                        }
                        yData.addYData(value);
                        yDataMap.put(field.getId(), yData);
                    }
                }
                needNullNumber = 0;
            }
            Collection<MonitorYData> values = yDataMap.values();
            lineChart.setYDataList(new ArrayList<>(values));
            lineChart.setXDataList(xDataList);
            return lineChart;
        }

    }

    /**
     * 当折线图是 周或者月。 设置折线图的X轴日期
     * @param startDateEndDate
     * @param monitorDateLineType
     * @return
     */
    private List<LocalDateTime> monitorXDataList(List<LocalDateTime> startDateEndDate, MonitorDateLineType monitorDateLineType) {
        LocalDateTime startTime = startDateEndDate.get(0);
        LocalDateTime endTime = startDateEndDate.get(1);
        LocalDateTime endDate = endTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<LocalDateTime> localDateTimes = new ArrayList<>(31);
        if (MonitorDateLineType.MONTH.equals(monitorDateLineType) ||
            MonitorDateLineType.WEEK.equals(monitorDateLineType)) {
            for (int i = 0; i < 31; i++) {
                if (!startTime.isAfter(endDate)) {
                    localDateTimes.add(startTime);
                    startTime = startTime.plusDays(1);
                } else {
                    break;
                }
            }
            return localDateTimes;
        } else if(MonitorDateLineType.DAY.equals(monitorDateLineType)) {
            for (int i = 0; i < 24; i++) {
                if (!startTime.isAfter(endTime)) {
                    localDateTimes.add(startTime);
                    startTime = startTime.plusHours(1);
                } else {
                    break;
                }
            }
            return localDateTimes;
        }
        return startDateEndDate;
    }

    /**
     * 过滤 创建时间， 周和月 需要保留每天最晚的一条。
     * 日 需要保留 每个小时 最晚的一条
     * @param resultList
     * @param monitorDateLineType
     * @return
     */
    private Map<LocalDateTime, FormResult> filterResultByDate(List<FormResult> resultList, MonitorDateLineType monitorDateLineType) {

        Map<LocalDateTime, FormResult> passFromResult = new HashMap<>();
        switch (monitorDateLineType) {
            case DAY: {
                for (FormResult result : resultList) {
                    LocalDateTime dateTime = result.getCreateTime().withMinute(0).withSecond(0).withNano(0);
                    if (passFromResult.containsKey(dateTime)) {
                        continue;
                    }
                    passFromResult.put(dateTime, result);
                }
                break;
            }
            case WEEK:
            case MONTH: {
                for (FormResult result : resultList) {
                    LocalDateTime dateTime = result.getCreateTime().withHour(0).withMinute(0).withSecond(0).withNano(0);
                    if (passFromResult.containsKey(dateTime)) {
                        continue;
                    }
                    passFromResult.put(dateTime, result);
                }
                break;
            }
            default:
        }
        return passFromResult;
    }

    @Override
    public void handleCheckBoxResult(List<FormField> formFields) {

        for (FormField formField : formFields) {
            if (Objects.isNull(formField)) {
                continue;
            }
            if (FormWidgetType.CHECK_BOX.equals(formField.getWidgetType())) {
                String values = formField.getValues();
                if (StringUtils.isEmpty(values)) {
                    continue;
                }
                List<FormOptions> options = formField.getOptions();
                if (CollUtil.isEmpty(options)) {
                    continue;
                }
                for (FormOptions option : options) {
                    if (values.contains(option.getId())) {
                        option.setSelect(true);
                    } else {
                        option.setSelect(false);
                    }
                }
            }
        }

    }

    @Autowired
    TenantApi tenantApi;

    @Override
    public void updateForDeleteFormResult(Long formResultId) {
        FormResult result = new FormResult();
        result.setId(formResultId);
        result.setDeleteMark(1);
        baseMapper.updateById(result);
    }

    @Override
    public boolean checkNeedDesensitization() {
        String userType = BaseContextHandler.getUserType();
        if (UserType.ORGAN_ADMIN.equals(userType) || UserType.ADMIN_OPERATION.equals(userType)) {
            String tenant = BaseContextHandler.getTenant();
            R<Boolean> securitySettings = tenantApi.queryDataSecuritySettings(tenant);
            if (securitySettings.getIsSuccess()) {
                Boolean data = securitySettings.getData();
                if (data == null || !data) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 加密题目中 姓名 和 手机号字段的结果
     * @param fieldList
     */
    @Override
    public void desensitization(List<FormField> fieldList) {

        for (FormField field : fieldList) {
            String exactType = field.getExactType();
            if (FormFieldExactType.MOBILE.equals(exactType) ||
                FormFieldExactType.NAME.equals(exactType)) {
                String values = field.getValues();
                JSONArray array = JSONArray.parseArray(values);
                if (array.isEmpty()) {
                    continue;
                }
                JSONObject jsonObject = array.getJSONObject(0);
                String value = jsonObject.getString("attrValue");

                if (FormFieldExactType.NAME.equals(exactType)) {
                    value = SensitiveInfoUtils.desensitizeName(value);

                }
                if (FormFieldExactType.MOBILE.equals(exactType)) {
                    value = SensitiveInfoUtils.desensitizePhone(value);
                }
                jsonObject.put("attrValue", value);
                array = new JSONArray();
                array.add(0, jsonObject);
                field.setValues(JSON.toJSONString(array));
            }
        }


    }

    /**
     * 检查表单是否是评分问卷。 是评分问卷，给设置评分结果。
     */
    @Override
    public void checkFormResultSetScore(List<FormResult> formResults, Boolean needContent) {
        FormScoreRule formScoreRule = null;
        if (Objects.nonNull(needContent) && needContent) {
            return;
        }
        boolean queryScore = false;
        List<Long> resultIds = new ArrayList<>();
        for (FormResult result : formResults) {
            if (Objects.nonNull(result.getScoreQuestionnaire()) && result.getScoreQuestionnaire().equals(1)) {
                queryScore = true;
                if (formScoreRule == null) {
                    formScoreRule = formScoreRuleMapper.selectOne(Wraps.<FormScoreRule>lbQ().eq(FormScoreRule::getFormId, result.getFormId()).last(" limit 0,1 "));
                }
                resultIds.add(result.getId());
            }
        }
        if (!queryScore) {
            return;
        }
        String formResultCountWay = "";
        Integer showResultSum = 0;
        Integer showAverage = 0;
        if (Objects.nonNull(formScoreRule)) {
            formResultCountWay = formScoreRule.getFormResultCountWay();
            showResultSum = formScoreRule.getShowResultSum();
            showAverage = formScoreRule.getShowAverage();
        }
        // 使用 resultIds 查询表单结果的成绩
        List<FormResultScore> formResultScores = formResultScoreService.queryFormResultScore(resultIds);
        if (CollUtil.isNotEmpty(formResultScores)) {
            Map<Long, FormResultScore> resultScoreMap = formResultScores.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item));
            for (FormResult item : formResults) {
                Long id = item.getId();
                FormResultScore resultScore = resultScoreMap.get(id);
                item.setShowFormResultAverageScore(showAverage);
                item.setShowFormResultSumScore(showResultSum);
                if (Objects.isNull(resultScore)) {
                    continue;
                }
                if (FormResultCountScoreEnum.sum_average_score.type.equals(formResultCountWay)) {
                    item.setFormResultSumScore(resultScore.getFormResultSumAverageScore());
                } else if (FormResultCountScoreEnum.sum_score.type.equals(formResultCountWay)) {
                    item.setFormResultSumScore(resultScore.getFormResultSumScore());
                } else if (FormResultCountScoreEnum.average_score.type.equals(formResultCountWay)) {
                    item.setFormResultSumScore(resultScore.getFormResultAverageScore());
                }
                item.setFormResultAverageScore(resultScore.getFormResultAverageScore());
            }
        }
    }


}