package com.caring.sass.nursing.service.traceInto.impl;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoResultFieldAbnormalMapper;
import com.caring.sass.nursing.dao.traceInto.TraceIntoResultMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormOptions;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.dto.traceInto.FormFieldInfoAbnormal;
import com.caring.sass.nursing.dto.traceInto.TraceIntoOptionStatisticsDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResult;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResultFieldAbnormal;
import com.caring.sass.nursing.service.traceInto.TraceIntoResultFieldAbnormalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * <p>
 * 业务实现类
 * 选项跟踪异常题目明细记录表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
@Slf4j
@Service

public class TraceIntoResultFieldAbnormalServiceImpl extends SuperServiceImpl<TraceIntoResultFieldAbnormalMapper, TraceIntoResultFieldAbnormal> implements TraceIntoResultFieldAbnormalService {


    @Autowired
    FormResultMapper formResultMapper;

    @Autowired
    TraceIntoResultMapper traceIntoResultMapper;

    @Autowired
    FormMapper formMapper;

    @Override
    public TraceIntoOptionStatisticsDTO statistics(LocalDate localDate, Long nursingId, Long formId) {
        LocalDateTime startTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), 1, 0, 0, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1);
        Integer count = formResultMapper.selectCount(Wraps.<FormResult>lbQ()
                .eq(FormResult::getFormId, formId)
                .lt(FormResult::getCreateTime, endTime)
                .gt(FormResult::getCreateTime, startTime)
                .apply(true, "user_id in (select id from u_user_patient where service_advisor_id = " + nursingId + ")"));

        Integer errorNumber = traceIntoResultMapper.selectCount(Wraps.<TraceIntoResult>lbQ()
                .eq(TraceIntoResult::getFormId, formId)
                .eq(TraceIntoResult::getNursingId, nursingId)
                .lt(TraceIntoResult::getPushTime, endTime)
                .gt(TraceIntoResult::getPushTime, startTime));

        QueryWrapper<TraceIntoResultFieldAbnormal> fieldWrapper = new QueryWrapper<>();
        fieldWrapper.select("form_field_option_id as formFieldOptionId", "count(*) as total")
                .eq("form_id", formId)
                .eq("nursing_id", nursingId)
                .lt("create_time", endTime)
                .gt("create_time", startTime)
                .groupBy("form_field_option_id");
        List<Map<String, Object>> mapList = baseMapper.selectMaps(fieldWrapper);
        Map<String, Integer> countMap = new HashMap<>();
        String key = "formFieldOptionId";
        String countName = "total";
        for (Map<String, Object> objectMap : mapList) {
            Object o = objectMap.get(key);
            if (Objects.isNull(o)) {
                continue;
            }
            Object total = objectMap.get(countName);
            if (Objects.isNull(total)) {
                continue;
            }
            countMap.put(o.toString(), Integer.parseInt(total.toString()));
        }

        TraceIntoOptionStatisticsDTO statisticsDTO = new TraceIntoOptionStatisticsDTO();
        statisticsDTO.setAllFormResultNumber(count);
        statisticsDTO.setAbnormalNumber(errorNumber);
        if (CollUtil.isEmpty(countMap)) {
            return statisticsDTO;
        }
        Form form = formMapper.selectById(formId);
        String fieldsJson = form.getFieldsJson();
        List<FormField> formFields = JSONArray.parseArray(fieldsJson, FormField.class);

        Set<String> strings = countMap.keySet();
        List<FormFieldInfoAbnormal> formFieldInfo = findFormFieldInfo(formFields, strings, countMap);

        statisticsDTO.setFormFieldInfoAbnormalList(formFieldInfo);
        return statisticsDTO;
    }


    private List<FormFieldInfoAbnormal> findFormFieldInfo(List<FormField> formFields, Set<String> strings, Map<String, Integer> countMap) {
        List<FormFieldInfoAbnormal> infoAbnormals = new ArrayList<>();
        if (CollUtil.isEmpty(formFields)) {
            return infoAbnormals;
        }
        if (CollUtil.isEmpty(strings)) {
            return infoAbnormals;
        }
        if (CollUtil.isEmpty(countMap)) {
            return infoAbnormals;
        }
        FormFieldInfoAbnormal abnormal;
        for (FormField field : formFields) {
            if (FormWidgetType.RADIO.equals(field.getWidgetType()) ||
                    FormWidgetType.CHECK_BOX.equals(field.getWidgetType()) ||
                    FormWidgetType.DROPDOWN_SELECT.equals(field.getWidgetType())) {
                List<FormOptions> options = field.getOptions();
                abnormal = new FormFieldInfoAbnormal();
                boolean add = false;
                if (CollUtil.isNotEmpty(options)) {
                    for (FormOptions option : options) {
                        if (strings.contains(option.getId())) {
                            Integer count = countMap.get(option.getId());
                            abnormal.addFieldOptionAbnormalCount(option.getId(), option.getAttrValue(), count);
                            if (!add) {
                                add = true;
                                infoAbnormals.add(abnormal);
                                abnormal.setFieldId(field.getId());
                                abnormal.setFieldName(field.getLabel());
                            }
                        }
                        List<FormField> questions = option.getQuestions();
                        List<FormFieldInfoAbnormal> formFieldInfo = findFormFieldInfo(questions, strings, countMap);
                        if (CollUtil.isNotEmpty(formFieldInfo)) {
                            infoAbnormals.addAll(formFieldInfo);
                        }
                    }
                }
            }
        }
        return infoAbnormals;

    }




}
