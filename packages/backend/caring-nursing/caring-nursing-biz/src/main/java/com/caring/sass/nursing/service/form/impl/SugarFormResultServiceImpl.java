package com.caring.sass.nursing.service.form.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.api.ImApi;
import com.caring.sass.msgs.api.MsgPatientSystemMessageApi;
import com.caring.sass.msgs.dto.MsgPatientSystemMessageSaveDTO;
import com.caring.sass.nursing.constant.PlanFunctionTypeEnum;
import com.caring.sass.nursing.dao.form.SugarFormResultMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.dto.blood.SugarDTO;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.SugarFormResult;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.nursing.service.form.SugarFormResultService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.nursing.service.plan.PlanService;
import com.caring.sass.nursing.service.plan.ReminderLogService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.nursing.util.FormUtil;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.dto.NursingPlanPatientBaseInfoDTO;
import com.caring.sass.user.dto.NursingPlanPatientDTO;
import com.caring.sass.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 血糖表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class SugarFormResultServiceImpl extends SuperServiceImpl<SugarFormResultMapper, SugarFormResult> implements SugarFormResultService {

    @Autowired
    MsgPatientSystemMessageApi msgPatientSystemMessageApi;


    @Autowired
    PatientApi patientApi;

    @Autowired
    ReminderLogService reminderLogService;

    @Autowired
    ImApi imApi;

    @Autowired
    PlanMapper planMapper;

    @Override
    public R<Boolean> handlerSave(SugarFormResult model) {
        Long createDay = model.getCreateDay();
        if (createDay == null) {
            model.setCreateDay((DateUtil.beginOfDay(new Date()).getTime() / 1000L));
            int hour = DateUtil.thisHour(true);
            int minute = DateUtil.thisMinute();
            model.setTime(hour + StrPool.COLON + minute);
        } else {
            // 毫秒级时间戳长度为13为
            boolean isTimeMillis = NumberUtil.toStr(createDay).length() == 13;
            if (isTimeMillis) {
                model.setCreateDay(createDay / 1000L);
            }
        }
        Long messageId = model.getMessageId();
        Long planDetailTimeId = model.getPlanDetailTimeId();
        if (Objects.nonNull(planDetailTimeId)) {
            reminderLogService.createOrUpdateReminderLog(planDetailTimeId, PlanEnum.BLOOD_SUGAR, model.getPatientId(), messageId);
        } else if (Objects.nonNull(messageId)) {
            if (model.getImRecommendReceipt() != null && model.getImRecommendReceipt() == 1) {
                imApi.updateImRemind(messageId);
            } else {
                reminderLogService.submitSuccess(messageId);
            }
        } else {
            // 检查最近是否有系统提醒的血糖, 如果有，将系统推送的消息设置为已打卡。并和此表单关联
            Plan plan = planMapper.selectOne(Wraps.<Plan>lbQ().eq(Plan::getPlanType, PlanEnum.BLOOD_SUGAR.getCode()).last(" limit 1 "));
            if (Objects.nonNull(plan)) {
                Long message = reminderLogService.queryRecentlyUnReadMessage(model.getPatientId(), plan.getId());
                if (Objects.nonNull(message)) {
                    model.setMessageId(message);
                    reminderLogService.submitSuccess(message);
                }
            }
        }
        Float sugarValue = model.getSugarValue();
        String feedBack = getDataFeedBack(model.getType(), sugarValue);
        model.setDataFeedBack(feedBack);
        baseMapper.insert(model);

        // 患者填写血糖
        String tenantCode = BaseContextHandler.getTenant();
        String userType = BaseContextHandler.getUserType();
        if (UserType.PATIENT.equals(userType)) {
            SaasGlobalThreadPool.execute(() -> sendPatientCreateFormResult(model, tenantCode));
        }

        return R.success();
    }

    /**
     * 患者添加一个表单结果后。 给医生发送互动消息
     * @param formResult
     * @param tenantCode
     */
    public void sendPatientCreateFormResult(SugarFormResult formResult, String tenantCode) {

        Long patientId = formResult.getPatientId();
        if (Objects.isNull(patientId)) {
            return;
        }
        NursingPlanPatientDTO patientDTO = new NursingPlanPatientDTO();
        List<Long> patientIds = new ArrayList<>();
        patientIds.add(patientId);
        patientDTO.setIds(patientIds);
        patientDTO.setTenantCode(tenantCode);
        R<List<NursingPlanPatientBaseInfoDTO>> patientApiBaseInfoForNursingPlan = patientApi.getBaseInfoForNursingPlan(patientDTO);
        for (NursingPlanPatientBaseInfoDTO planPatientBaseInfoDTO : patientApiBaseInfoForNursingPlan.getData()) {
            MsgPatientSystemMessageSaveDTO saveDTO = new MsgPatientSystemMessageSaveDTO();
            saveDTO.setPatientId(patientId);
            saveDTO.setPatientOpenId(planPatientBaseInfoDTO.getOpenId());
            saveDTO.setDoctorId(planPatientBaseInfoDTO.getDoctorId());
            saveDTO.setBusinessId(formResult.getId());
            saveDTO.setReadStatus(CommonStatus.NO);
            saveDTO.setPatientCanSee(CommonStatus.NO);
            saveDTO.setTenantCode(tenantCode);
            saveDTO.setDoctorCommentStatus(CommonStatus.NO);
            saveDTO.setDoctorReadStatus(CommonStatus.NO);
            saveDTO.setFunctionType(PlanFunctionTypeEnum.INTERACTIVE_MESSAGE.getCode());

            Plan plan = planMapper.selectOne(Wraps.<Plan>lbQ().eq(Plan::getPlanType, PlanEnum.BLOOD_SUGAR.getCode()).last(" limit 1 "));
            if (Objects.nonNull(plan)) {
                saveDTO.setPlanName(plan.getName());
                PlanFunctionTypeEnum enumByPlanType = PlanFunctionTypeEnum.getEnumByPlanType(plan.getPlanType(), plan.getFollowUpPlanType());
                if (enumByPlanType != null) {
                    saveDTO.setInteractiveFunctionType(enumByPlanType.getCode());
                }
            }

            saveDTO.setPushTime(LocalDateTime.now());
            saveDTO.setPushPerson(planPatientBaseInfoDTO.getName());
            msgPatientSystemMessageApi.saveSystemMessage(saveDTO);
        }
    }



    @Override
    public boolean updateById(SugarFormResult model) {
        Float sugarValue = model.getSugarValue();
        String feedBack = getDataFeedBack(model.getType(), sugarValue);
        model.setDataFeedBack(feedBack);
        return super.updateById(model);
    }

    /**
     * 计算血糖值的结果
     * @param type
     * @param sugarValue
     * @return
     */
    public String getDataFeedBack(Integer type, Float sugarValue) {
        Integer normalAnomaly;
        String normalAnomalyText;
        JSONObject jsonObject = new JSONObject();
        if (Objects.nonNull(sugarValue)) {
            BigDecimal decimal = BigDecimal.valueOf(sugarValue);
            BigDecimal x = null;
            BigDecimal y = null;
            // 餐后
            if (type.equals(2) || type.equals(4) || type.equals(6)) {
                y = BigDecimal.valueOf(11.1);
                if (decimal.compareTo(y) > 0){
                    normalAnomaly = 2;
                    normalAnomalyText = "偏高";
                } else {
                    normalAnomaly = 1;
                    normalAnomalyText = "正常";
                }
                jsonObject.put("normalAnomaly", normalAnomaly);
                jsonObject.put("normalAnomalyText", normalAnomalyText);
                return jsonObject.toJSONString();
            } else
            // 早餐前 空腹 或凌晨
            if (type.equals(1) || type.equals(0)) {
                x = BigDecimal.valueOf(3.9);
                y = BigDecimal.valueOf(6.1);
            }
            // 午餐前 晚餐前
            else if (type.equals(3) || type.equals(5)) {
                x = BigDecimal.valueOf(4.4);
                y = BigDecimal.valueOf(7.0);
            }
            // 睡觉前
            else if (type.equals(7)) {
                x = BigDecimal.valueOf(4.4);
                y = BigDecimal.valueOf(6.1);
            }
            if (x == null) {
                return null;
            }
            if (decimal.compareTo(x) >= 0 && decimal.compareTo(y) <= 0) {
                normalAnomaly = 1;
                normalAnomalyText = "正常";
            } else if (decimal.compareTo(x) < 0){
                normalAnomaly = 2;
                normalAnomalyText = "偏低";
            } else if (decimal.compareTo(y) > 0){
                normalAnomaly = 2;
                normalAnomalyText = "偏高";
            } else {
                return null;
            }
            jsonObject.put("normalAnomaly", normalAnomaly);
            jsonObject.put("normalAnomalyText", normalAnomalyText);
            return jsonObject.toJSONString();
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> loadPatientBloodSugarTrendData(Integer type, Integer week, Long patientId) {
        List<Date> dates = DateUtils.thisMondayAndthisSunday(week);
        LbqWrapper<SugarFormResult> lbqWrapper = Wraps.<SugarFormResult>lbQ()
                .eq(SugarFormResult::getPatientId, patientId)
                .eq(SugarFormResult::getType, type)
                .between(SugarFormResult::getCreateDay, dates.get(0).getTime()/1000L, dates.get(1).getTime()/1000L)
                .orderByAsc(SugarFormResult::getType);
        List<SugarFormResult> sugarFormResults = baseMapper.selectList(lbqWrapper);

        // 生成默认数据
        List<Map<String, Object>> ret = new ArrayList<>();
        Date now = new Date();
        DateTime beginDate = DateUtil.beginOfWeek(dates.get(0));
        for (int i = 0; i < 7; i++) {
            DateTime tmpDate = DateUtil.offset(beginDate, DateField.DAY_OF_WEEK, i);
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("value", 0);
            tmp.put("type", "血糖");
            tmp.put("day", DateUtil.format(tmpDate.toJdkDate(), "YYYY/MM/dd"));
            ret.add(tmp);
        }

        if (CollUtil.isEmpty(sugarFormResults)) {
            return ret;
        }

        // 替换默认数据
        for (SugarFormResult x : sugarFormResults) {
            Long createDay = x.getCreateDay();
            if (Objects.isNull(createDay)) {
                continue;
            }
            String day = DateUtil.format(new Date(createDay * 1000 ), "YYYY/MM/dd");
            for (Map<String, Object> m : ret) {
                if (day.equals(Convert.toStr(m.get("day")))) {
                    m.put("value", x.getSugarValue());
                    m.put("type", "血糖");
                    m.put("day", day);
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * @Author yangShuai
     * @Description 根据日期进行分组
     * @Date 2020/11/11 13:35
     */
    public List<SugarDTO> insertBlankData(Date start, Date end, List<SugarFormResult> sugars) {
        ArrayList<SugarDTO> list;
        for (list = new ArrayList<>(); start.getTime() <= end.getTime(); start = DateUtils.addDay(start, 1)) {
            SugarDTO sugarDTO = new SugarDTO();
            Date date = DateUtils.addDay(start, 0);
            long seconds = date.getTime() / 1000;
            List<SugarFormResult> collect = new ArrayList<>();
            for (SugarFormResult sugar : sugars) {
                if (sugar.getCreateDay() >= seconds && sugar.getCreateDay() < seconds + 86400L) {
                    collect.add(sugar);
                }
            }
            sugarDTO.setSugars(collect);
            sugarDTO.setCreateDate(date);
            list.add(sugarDTO);
        }

        return list;
    }


    /**
     * @Author yangShuai
     * @Description 血糖填写记录
     * @Date 2020/11/11 13:47
     */
    @Override
    public List<SugarDTO> findSugarByTime(String patientId, Long startTime, Long endTime) {
        // 为空，默认本周
        Date now = new Date();
        if (startTime == null) {
            startTime = DateUtil.beginOfWeek(now).getTime();
        }
        if (endTime == null) {
            endTime = DateUtil.endOfWeek(now).getTime();
        }

        LbqWrapper<SugarFormResult> lbqWrapper = Wraps.<SugarFormResult>lbQ()
                .eq(SugarFormResult::getPatientId, patientId)
                .between(SugarFormResult::getCreateDay, startTime / 1000L, endTime / 1000L)
                .orderByAsc(SugarFormResult::getType);
        List<SugarFormResult> sugarFormResults = baseMapper.selectList(lbqWrapper);
        List<SugarDTO> sugars = insertBlankData(new Date(startTime), new Date(endTime), sugarFormResults);
        return sugars.stream().sorted(Comparator.comparing(SugarDTO::getCreateDate)).collect(Collectors.toList());
    }

    @Override
    public JSONObject list(Integer week, Long patientId) {
        List<Date> dates = DateUtils.thisMondayAndthisSunday(week);
        LbqWrapper<SugarFormResult> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(SugarFormResult::getPatientId, patientId);
        lbqWrapper.between(SugarFormResult::getCreateDay, dates.get(0).getTime(), dates.get(1).getTime());
        lbqWrapper.orderByAsc(SugarFormResult::getType);
        List<SugarFormResult> sugarFormResults = baseMapper.selectList(lbqWrapper);
        List<SugarDTO> sugars = this.insertBlankData(dates.get(0), dates.get(1), sugarFormResults);
        JSONObject json = new JSONObject();
        json.put("list", sugars);
        json.put("start", DateUtils.date2Str(dates.get(0), DateUtils.YMD_point));
        json.put("end", DateUtils.date2Str(dates.get(1), DateUtils.YMD_point));
        return json;
    }
}
