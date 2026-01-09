package com.caring.sass.nursing.service.form.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.form.FormFieldsGroupMapper;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormFieldsGroup;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.form.FormResultScoreService;
import com.caring.sass.nursing.service.traceInto.TraceIntoResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author xinzh
 */
@Slf4j
@Component
public class FormResultMerge {

    @Autowired
    private FormResultMapper formResultMapper;

    @Autowired
    FormFieldsGroupMapper formFieldsGroupMapper;

    @Autowired
    FormResultScoreService formResultScoreService;

    @Async
    public void syncCustomFromResult(Form form, String tenantCode) {
        if (StrUtil.isBlank(tenantCode)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        int currentPage = 1;
        int pageSize = 100;
        List<FormFieldsGroup> fieldsGroups = new ArrayList<>();
        if (form.getScoreQuestionnaire() != null && form.getScoreQuestionnaire() == 1) {
            fieldsGroups = formFieldsGroupMapper.selectList(Wraps.<FormFieldsGroup>lbQ().eq(FormFieldsGroup::getFormId, form.getId()));
        }
        try {
            mergeFormResultByPage(currentPage, pageSize, form, fieldsGroups);
        } catch (Exception e) {
            log.error("合并历史表单数据异常", e);
        }
    }

    /**
     * 分页合并表单结果
     *
     * @param currentPage 当前页面
     * @param pageSize    每页大小
     * @param form        表单
     */
    private void mergeFormResultByPage(int currentPage, int pageSize, Form form, List<FormFieldsGroup> fieldsGroups) {
        Page<FormResult> formResultPage = formResultMapper.selectPage(new Page<>(currentPage, pageSize), Wraps.<FormResult>lbQ()
                .eq(FormResult::getFormId, form.getId())
                .eq(FormResult::getBusinessId, form.getBusinessId()));

        long total = formResultPage.getTotal();
        // 没有记录，递归出口
        if (total == 0) {
            return;
        }
        mergeCustomerFromResult(form, formResultPage.getRecords(), fieldsGroups);
        long totalPage = formResultPage.getPages();

        // 分页完成，递归出口
        if (currentPage >= totalPage) {
            return;
        }
        mergeFormResultByPage(++currentPage, pageSize, form, fieldsGroups);
    }

    private void mergeCustomerFromResult(Form form, List<FormResult> formResults, List<FormFieldsGroup> fieldsGroups) {
        if (CollectionUtils.isEmpty(formResults)) {
            return;
        }

        try {
            List<FormFieldDto> formFields = JSON.parseArray(form.getFieldsJson(), FormFieldDto.class);
            for (FormResult formResult : formResults) {
                List<FormFieldDto> resultFields = JSON.parseArray(formResult.getJsonContent(), FormFieldDto.class);
                resultFields = mergeFields(formFields, resultFields);
                String formName = form.getName();
                if (StrUtil.isNotEmpty(formName)) {
                    formResult.setName(formName);
                }
                Integer oneQuestionPage = form.getOneQuestionPage();
                if (Objects.nonNull(oneQuestionPage)) {
                    formResult.setOneQuestionPage(oneQuestionPage);
                }
                Integer showTrend = form.getShowTrend();
                if (Objects.nonNull(showTrend)) {
                    formResult.setShowTrend(showTrend);
                }
                if (CollUtil.isNotEmpty(formFields)) {
                    formResult.setJsonContent(JSON.toJSONString(resultFields));
                }
                if (FormEnum.HEALTH_RECORD.eq(formResult.getCategory())
                        || FormEnum.BASE_INFO.eq(formResult.getCategory())) {
                    // 表单结果是基本信息或疾病信息时，当表单结果填写进度没有或 没完成时， 重置为0
                    if (formResult.getFillInIndex()== null || formResult.getFillInIndex() != -1) {
                        formResult.setFillInIndex(0);
                    }
                }
                formResultMapper.updateById(formResult);
                formResultScoreService.countFormResult(resultFields, formResult.getId(), formResult.getUserId(), formResult.getFormId(), fieldsGroups);
            }
        } catch (Exception e) {
            log.error("合并表单异常", e);
        }
    }

    public List<FormFieldDto> mergeFields(List<FormFieldDto> formFields, List<FormFieldDto> resultFields) {
        if (CollectionUtils.isEmpty(formFields)) {
            return formFields;
        }
        if (CollectionUtils.isEmpty(resultFields)) {
            return formFields;
        }
        Map<String, FormFieldDto> map = new HashMap<>();
        for (FormFieldDto resultField : resultFields) {
            map.put(resultField.getId(), resultField);
        }
        List<FormFieldDto> ret = new ArrayList<>();
        try {
            for (FormFieldDto field : formFields) {
                FormFieldDto oldField = map.get(field.getId());
                if (Objects.isNull(oldField)) {
                    ret.add(field);
                    continue;
                }
                FormFieldDto newField = BeanUtil.copyProperties(field, FormFieldDto.class);
                newField.setValues(oldField.getValues());
                newField.setValue(oldField.getValue());
                newField.setOtherValue(oldField.getOtherValue());

                // 合并时。 患者的基准值目标值 保留原始的结果
                newField.setReferenceValue(oldField.getReferenceValue());
                newField.setTargetValue(oldField.getTargetValue());
                ret.add(newField);
            }
        } catch (Exception e) {
            log.error("合并表单历史属性异常", e);
        }
        return ret;
    }
}
