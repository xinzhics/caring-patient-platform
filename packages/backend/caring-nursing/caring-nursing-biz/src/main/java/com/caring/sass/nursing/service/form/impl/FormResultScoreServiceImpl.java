package com.caring.sass.nursing.service.form.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.form.FormFieldsGroupMapper;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.form.FormResultScoreMapper;
import com.caring.sass.nursing.dao.form.FormScoreRuleMapper;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormOptions;
import com.caring.sass.nursing.entity.form.*;
import com.caring.sass.nursing.service.form.FormResultScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 表单结果的成绩计算
 * </p>
 *
 * @author 杨帅
 * @date 2023-10-11
 */
@Slf4j
@Service

public class FormResultScoreServiceImpl extends SuperServiceImpl<FormResultScoreMapper, FormResultScore> implements FormResultScoreService {

    @Autowired
    FormScoreRuleMapper formScoreRuleMapper;

    @Autowired
    FormFieldsGroupMapper formFieldsGroupMapper;

    @Autowired
    FormMapper formMapper;

    /**
     * 使用表单结果ID查询分数，分数规则，分组
     * @param formResultId
     * @return
     */
    @Override
    public FormResultScore queryFormResultScore(Long formResultId) {

        FormResultScore formResultScore = baseMapper.selectById(formResultId);
        if (Objects.nonNull(formResultScore)) {
            // 查询积分规则
            FormScoreRule scoreRule = formScoreRuleMapper.selectOne(Wraps.<FormScoreRule>lbQ()
                    .eq(FormScoreRule::getFormId, formResultScore.getFormId())
                    .last(" limit 0, 1 "));

            List<FormFieldsGroup> fieldsGroups = formFieldsGroupMapper.selectList(Wraps.<FormFieldsGroup>lbQ()
                    .orderByAsc(FormFieldsGroup::getGroupSort)
                    .eq(FormFieldsGroup::getFormId, formResultScore.getFormId()));
            formResultScore.setScoreRule(scoreRule);
            formResultScore.setFieldsGroups(fieldsGroups);
            LbqWrapper<Form> select = Wraps.<Form>lbQ()
                    .eq(Form::getId, formResultScore.getFormId())
                    .select(SuperEntity::getId, Form::getShowScoreQuestionnaireAnalysis, Form::getScoreQuestionnaireAnalysis);
            Form form = formMapper.selectOne(select);
            formResultScore.setScoreQuestionnaireAnalysis(form.getScoreQuestionnaireAnalysis());
            formResultScore.setShowScoreQuestionnaireAnalysis(form.getShowScoreQuestionnaireAnalysis());
            return formResultScore;
        }
        return null;

    }

    /**
     * 使用表单结果ID批量查询表单结果分数
     * @param formResultIds
     * @return
     */
    @Override
    public List<FormResultScore> queryFormResultScore(List<Long> formResultIds) {
        if (CollUtil.isNotEmpty(formResultIds)) {
            return baseMapper.selectBatchIds(formResultIds);
        }
        return null;
    }

    /**
     * 计算表单结果的得分
     * @param formResult
     * @return
     */
    @Override
    public boolean countFormResult(FormResult formResult) {

        String jsonContent = formResult.getJsonContent();
        if (StrUtil.isEmpty(jsonContent)) {
            return true;
        }
        @NotNull(message = "表单Id不能为空") Long formId = formResult.getFormId();
        // 查询分组设置
        List<FormFieldsGroup> fieldsGroups = formFieldsGroupMapper.selectList(Wraps.<FormFieldsGroup>lbQ().eq(FormFieldsGroup::getFormId, formId));

        countFormResult(formResult.getId(), formResult.getFormId(), formResult.getUserId(), jsonContent, fieldsGroups);
        return true;
    }

    @Override
    public void countFormResult(List<FormFieldDto> formFields, Long formResultId, Long userId, Long formId, List<FormFieldsGroup> fieldsGroups) {
        Map<String, FormFieldsGroup> fieldsGroupMap = new HashMap<>();
        if (CollUtil.isNotEmpty(fieldsGroups)) {
            fieldsGroups.forEach(item -> item.setScoreSum(null));
            fieldsGroupMap = fieldsGroups.stream().collect(Collectors.toMap(FormFieldsGroup::getFieldGroupUUId, item -> item, (o1, o2) -> o2));
        }

        float formResultSumScore = 0;
        // 题目数量
        int fieldNumber = 0;
        FormFieldsGroup fieldsGroup = null;
        for (FormFieldDto formField : formFields) {
            String exactType = formField.getExactType();
            // 是积分单选题目
            if (FormFieldExactType.SCORING_SINGLE_CHOICE.equals(exactType)) {
                String values = formField.getValues();
                List<FormOptions> formOptions = formField.getOptions();
                Map<String, Float> floatMap = formOptions.stream()
                        .filter(item -> Objects.nonNull(item.getScore()))
                        .filter(item -> !item.getNotScored())
                        .collect(Collectors.toMap(FormOptions::getId, FormOptions::getScore));
                List<String> resultValues = FormFieldDto.parseResultValues(values);
                if (resultValues == null) {
                    continue;
                }
                // 题目所在的分组的UUID
                String fieldGroupUUId = formField.getFieldGroupUUId();
                if (StrUtil.isNotEmpty(fieldGroupUUId)) {
                    fieldsGroup = fieldsGroupMap.get(fieldGroupUUId);
                } else {
                    fieldsGroup = null;
                }
                for (String value : resultValues) {
                    if (StrUtil.isEmpty(value)) {
                        continue;
                    }
                    Float score = floatMap.get(value);
                    if (Objects.nonNull(score)) {
                        fieldNumber++;
                        formResultSumScore+=score;
                        if (Objects.nonNull(fieldsGroup)) {
                            if (fieldsGroup.getScoreSum() == null) {
                                fieldsGroup.setScoreSum(0f);
                            }
                            fieldsGroup.setScoreSum(fieldsGroup.getScoreSum() + score);
                        }
                    }
                }
            }
        }

        FormResultScore resultScore = new FormResultScore();
        resultScore.setFormResultSumScore(formResultSumScore);
        if (fieldNumber == 0) {
            resultScore.setFormResultAverageScore(0f);
            resultScore.setFormResultSumAverageScore(0f);
        } else {
            BigDecimal sumScore = new BigDecimal(formResultSumScore);
            BigDecimal fieldNum = new BigDecimal(fieldNumber);
            BigDecimal quotient = sumScore.divide(fieldNum, 1, RoundingMode.DOWN);
            float floatValue = quotient.floatValue();
            resultScore.setFormResultAverageScore(floatValue);

            BigDecimal add = sumScore.add(quotient);
            resultScore.setFormResultSumAverageScore(add.floatValue());
        }
        resultScore.setFormId(formId);
        resultScore.setFormResultId(formResultId);
        resultScore.setId(formResultId);
        resultScore.setPatientId(userId);

        Collection<FormFieldsGroup> groups = fieldsGroupMap.values();
        if (CollUtil.isNotEmpty(groups)) {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject;
            for (FormFieldsGroup group : groups) {
                if (group.getScoreSum() != null) {
                    jsonObject = new JSONObject();
                    jsonObject.put("groupId", group.getId());
                    jsonObject.put("uuid", group.getFieldGroupUUId());
                    jsonObject.put("score", group.getScoreSum());
                    jsonArray.add(jsonObject);
                }
            }
            resultScore.setFormFieldGroupSumInfo(jsonArray.toJSONString());
        }
        baseMapper.deleteById(formResultId);
        baseMapper.insert(resultScore);
    }

    /**
     * 根据积分规则和分组设置。计算成绩结果并保存
     * @param jsonContent
     * @param fieldsGroups
     * @return
     */
    public boolean countFormResult(Long formResultId, Long formId, Long patientId,
                                   String jsonContent, List<FormFieldsGroup> fieldsGroups) {

        List<FormFieldDto> formFields = JSONArray.parseArray(jsonContent, FormFieldDto.class);
        countFormResult(formFields, formResultId, patientId, formId, fieldsGroups);
        return true;
    }

}
