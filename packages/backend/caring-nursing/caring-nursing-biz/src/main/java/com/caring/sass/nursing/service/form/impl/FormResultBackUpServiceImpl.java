package com.caring.sass.nursing.service.form.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.form.FormResultBackUpMapper;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.FormResultBackUp;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.form.FormResultBackUpService;
import com.caring.sass.tenant.api.TenantApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName FormResultBackUpServiceImpl
 * @Description
 * @Author yangShuai
 * @Date 2022/4/6 10:49
 * @Version 1.0
 */
@Slf4j
@Service

public class FormResultBackUpServiceImpl extends SuperServiceImpl<FormResultBackUpMapper, FormResultBackUp> implements FormResultBackUpService {


    private static final String ATTR_VALUE = "attrValue";

    private static final String QUESTIONS = "questions";

    private static final String VALUES_DESC = "desc";

    private static final String VALUES_TEXT = "valueText";

    private static final String OTHER = "其他";

    @Autowired
    TenantApi tenantApi;

    @Autowired
    FormResultMapper formResultMapper;

    @Autowired
    FormMapper formMapper;

    @Autowired
    FormResultMerge formResultMerge;

    @Override
    public void createHealthRecordByHistory() {
        R<List<Object>> tenantCode = tenantApi.getAllTenantCode();
        List<Object> codeData = tenantCode.getData();
        if (CollUtil.isEmpty(codeData)) {
            return;
        }
        for (Object codeDatum : codeData) {
            String code = codeDatum.toString();
            BaseContextHandler.setTenant(code);
            Form form = formMapper.selectOne(Wraps.<Form>lbQ()
                    .eq(Form::getCategory, FormEnum.HEALTH_RECORD)
                    .orderByDesc(SuperEntity::getCreateTime)
                    .last(" limit 0,1 "));
            if (Objects.nonNull(form)) {
                mergeFormResultByPage(1, 100, form);
            }
        }
    }

    private void mergeFormResultByPage(int currentPage, int pageSize, Form form) {
        Page<FormResult> formResultPage = formResultMapper.selectPage(new Page<>(currentPage, pageSize), Wraps.<FormResult>lbQ()
                .select(SuperEntity::getId, FormResult::getFormId, FormResult::getUserId, Entity::getUpdateTime)
                .eq(FormResult::getFormId, form.getId()));

        long total = formResultPage.getTotal();
        // 没有记录，递归出口
        if (total == 0) {
            return;
        }
        mergeCustomerFromResult(form, formResultPage.getRecords());
        long totalPage = formResultPage.getPages();

        // 分页完成，递归出口
        if (currentPage >= totalPage) {
            return;
        }
        mergeFormResultByPage(++currentPage, pageSize, form);
    }

    private void mergeCustomerFromResult(Form form, List<FormResult> formResults) {
        if (CollectionUtils.isEmpty(formResults)) {
            return;
        }

        try {
            List<FormFieldDto> formFields = JSON.parseArray(form.getFieldsJson(), FormFieldDto.class);
            List<Long> collect = formResults.stream().map(SuperEntity::getId).collect(Collectors.toList());
            List<FormResultBackUp> backUpList = baseMapper.selectList(Wraps.<FormResultBackUp>lbQ().in(FormResultBackUp::getFormResultId, collect));
            if (CollUtil.isEmpty(backUpList)) {
                return;
            }
            Map<Long, List<FormResultBackUp>> formResultBackUpGroupResultId = backUpList.stream()
                    .collect(Collectors.groupingBy(FormResultBackUp::getFormResultId));
            for (FormResult formResult : formResults) {
                formResult.setCreateTime(formResult.getUpdateTime());
                formResultMapper.updateById(formResult);

                // 查询表单结果是否有历史修改记录
                List<FormResultBackUp> formResultBackUps = formResultBackUpGroupResultId.get(formResult.getId());
                if (CollUtil.isEmpty(formResultBackUps)) {
                    continue;
                }
                for (FormResultBackUp resultBackUp : formResultBackUps) {
                    String jsonContent = resultBackUp.getJsonContent();
                    List<FormFieldDto> resultFields = JSON.parseArray(jsonContent, FormFieldDto.class);
                    resultFields = formResultMerge.mergeFields(formFields, resultFields);
                    FormResult result = new FormResult();
                    result.setCreateTime(resultBackUp.getCreateTime().plusHours(-1));
                    result.setDeleteMark(0);
                    result.setFillInIndex(-1);
                    result.setBusinessId(form.getBusinessId());
                    result.setUserId(formResult.getUserId());
                    result.setFormId(formResult.getFormId());
                    result.setShowTrend(form.getShowTrend());
                    result.setOneQuestionPage(form.getOneQuestionPage());
                    result.setName(form.getName());
                    result.setCategory(form.getCategory());
                    result.setUpdateTime(result.getCreateTime());
                    result.setJsonContent(JSON.toJSONString(resultFields));
                    result.setFormHistory(1);
                    formResultMapper.insert(result);
                }
            }
        } catch (Exception e) {
            log.error("合并表单异常", e);
        }
    }

    @Override
    public void backUp(String userType, Long userId, String tenantCode, FormResult oldFormResult, FormResult newFormResult) {

        BaseContextHandler.setToken(tenantCode);
        FormResultBackUp resultBackUp = new FormResultBackUp();
        resultBackUp.setUserType(userType);
        resultBackUp.setUpdateUserId(userId);
        resultBackUp.setPatientId(oldFormResult.getUserId());
        resultBackUp.setTenantCode(tenantCode);
        resultBackUp.setFormResultId(newFormResult.getId());
        String oldJsonContent = oldFormResult.getJsonContent();
        String newJsonContent = newFormResult.getJsonContent();
        String newFormFields = checkFieldUpdate(oldJsonContent, newJsonContent);
        if (StringUtils.isNotEmptyString(newFormFields)) {
            resultBackUp.setJsonContent(newFormFields);
            baseMapper.insert(resultBackUp);
        } else {
            log.info("表单结果没有发生变化。 不进行备份保存");
        }
    }

    /**
     * 检查表单中的字段。哪些被修改过。
     * TODO：没脸看都。以后在封装吧
     * @param oldJsonContent
     * @param newJsonContent
     * @return
     */
    public String checkFieldUpdate(String oldJsonContent, String newJsonContent) {
        // 比对 那些是用户改了的。那些是用户没有改的。
        boolean fieldUpdateState = false;
        Map<String, FormField> fieldMap = new HashMap<>();
        if (StringUtils.isNotEmptyString(oldJsonContent)) {
            List<FormField> oldFields = JSON.parseArray(oldJsonContent, FormField.class);
            if (CollUtil.isNotEmpty(oldFields)) {
                fieldMap = oldFields.stream().collect(Collectors.toMap(FormField::getId, item -> item, (oldVal, currVal) -> currVal));
            }
        }
        if (StringUtils.isEmpty(newJsonContent)) {
            return null;
        }
        List<FormField> newFormFields = JSON.parseArray(newJsonContent, FormField.class);
        Set<String> existFormFieldId = new HashSet<>(newFormFields.size());
        FormField oldFormField;

        if (CollUtil.isNotEmpty(newFormFields)) {
            for (FormField field : newFormFields) {
                if (existFormFieldId.contains(field.getId())) {
                    continue;
                }
                existFormFieldId.add(field.getId());
                String fieldWidgetType = field.getWidgetType();
                String fieldExactType = field.getExactType();
                if (FormWidgetType.DESC.equals(fieldWidgetType) ||
                        FormWidgetType.PAGE.equals(fieldWidgetType) ||
                        FormWidgetType.SPLIT_LINE.equals(fieldWidgetType)) {
                    continue;
                }

                String exactType = field.getExactType();

                oldFormField = fieldMap.get(field.getId());
                if (oldFormField == null) {
                    field.setModifyTags(true);
                    fieldUpdateState = true;
                    continue;
                }
                String values = oldFormField.getValues();
                String value = oldFormField.getValue();

                String fieldValues = field.getValues();
                String fieldValue = field.getValue();

                // 单选，多选，诊断类型 会触发新题目
                if (FormWidgetType.RADIO.equals(fieldWidgetType) ||
                    FormWidgetType.CHECK_BOX.equals(fieldWidgetType) ||
                    FormWidgetType.DROPDOWN_SELECT.equals(fieldWidgetType) ||
                    FormFieldExactType.DIAGNOSE.equals(exactType)) {
                    // 解析表单结果。 判断有 新题的答案
                    JSONArray oldValueJsonArray = null,
                            newValueJsonArray = null;
                    if (StringUtils.isNotEmptyString(values)) {
                        oldValueJsonArray = JSON.parseArray(values);
                    }
                    if (StringUtils.isNotEmptyString(fieldValues)) {
                        newValueJsonArray = JSON.parseArray(fieldValues);
                    }

                    if (FormWidgetType.RADIO.equals(fieldWidgetType) ||
                            FormWidgetType.DROPDOWN_SELECT.equals(fieldWidgetType) ||
                            FormFieldExactType.DIAGNOSE.equals(exactType)) {
                        Object oldValues = null;
                        Object newValues = null;
                        if (oldValueJsonArray != null && CollUtil.isNotEmpty(oldValueJsonArray)) {
                            oldValues = oldValueJsonArray.get(0);
                        }
                        if (newValueJsonArray != null && CollUtil.isNotEmpty(newValueJsonArray)) {
                            newValues = newValueJsonArray.get(0);
                        }
                        JSONObject oldValue = null,
                                newValue = null;
                        if (Objects.nonNull(oldValues) && StringUtils.isNotEmptyString(oldValues.toString())) {
                            try {
                                oldValue = JSON.parseObject(oldValues.toString());
                            } catch (Exception e) {
                                log.error("解析单选题 oldValues 答案失败， {}", oldValues.toString());
                                continue;
                            }
                        }
                        if (Objects.nonNull(newValues) && StringUtils.isNotEmptyString(newValues.toString())) {
                            try {
                                newValue = JSON.parseObject(newValues.toString());
                            } catch (Exception e) {
                                log.error("解析单选题 newValues 答案失败， {}", newValues.toString());
                                continue;
                            }
                        }
                        String oldAttrValue = null,
                                newAttrValue = null,
                                oldDesc = null,
                                newDesc = null,
                                newAttrText = null,
                                oldQuestions = null,
                                newQuestions = null;

                        if (oldValue != null) {
                            // 选中的选项
                            oldAttrValue = oldValue.getString(ATTR_VALUE);
                            oldDesc = oldValue.getString(VALUES_DESC);
                            oldQuestions = oldValue.getString(QUESTIONS);
                        }
                        if (newValue != null) {
                            newAttrValue = newValue.getString(ATTR_VALUE);
                            // 选中选项的名词
                            newAttrText = newValue.getString(VALUES_TEXT);
                            newDesc = newValue.getString(VALUES_DESC);
                            newQuestions = newValue.getString(QUESTIONS);
                        }
                        if (notEquals(newAttrValue, oldAttrValue)) {
                            fieldUpdateState = true;
                            field.setModifyTags(true);
                        } else if (notEquals(newDesc, oldDesc)) {
                            fieldUpdateState = true;
                            field.setModifyTags(true);
                        } else if (OTHER.equals(newAttrText)) {
                            String newOtherValue = field.getOtherValue();
                            String oldOtherValue = oldFormField.getOtherValue();
                            if (notEquals(oldOtherValue,  newOtherValue)) {
                                fieldUpdateState = true;
                                field.setModifyTags(true);
                            }
                        }
                        if (Objects.nonNull(newQuestions)) {
                            String fieldUpdate = checkFieldUpdate(oldQuestions,
                                    newQuestions);
                            if (StringUtils.isNotEmptyString(fieldUpdate)) {
                                fieldUpdateState = true;
                                newValue.put(QUESTIONS, JSON.parseArray(fieldUpdate));
                            }
                        }
                        if (newValueJsonArray != null) {
                            newValueJsonArray = new JSONArray();
                            newValueJsonArray.add(newValue);
                            fieldValues = newValueJsonArray.toJSONString();
                            field.setValues(fieldValues);
                        }

                    } else {
                        // 多选题

                        // 判断 ATTR_VALUE 是否修改。 判断 otherValue 是否修改  判断desc 是否修改

                        // 取出 旧 的 ATTR_VALUE
                        Map<String, JSONObject> oldCheckBoxValues = new HashMap<>();
                        if (oldValueJsonArray != null) {
                            for (Object o : oldValueJsonArray) {
                                if (Objects.nonNull(o)) {
                                    JSONObject jsonObject = JSON.parseObject(o.toString());
                                    oldCheckBoxValues.put(jsonObject.getString(ATTR_VALUE), jsonObject);
                                }
                            }
                        }
                        JSONArray jsonArray = new JSONArray();

                        if (newValueJsonArray != null) {
                            JSONObject oldValue = null;
                            if (oldCheckBoxValues.size() != newValueJsonArray.size()) {
                                fieldUpdateState = true;
                                field.setModifyTags(true);
                            }
                            for (Object o : newValueJsonArray) {
                                if (Objects.nonNull(o)) {
                                    JSONObject jsonObject = JSON.parseObject(o.toString());
                                    String attrValue = jsonObject.getString(ATTR_VALUE);
                                    oldValue = oldCheckBoxValues.get(attrValue);
                                    String oldQuestions = null;
                                    if (Objects.isNull(oldValue)) {
                                        field.setModifyTags(true);
                                        fieldUpdateState = true;
                                    } else {
                                        oldQuestions = oldValue.getString(QUESTIONS);
                                    }
                                    String newQuestions = jsonObject.getString(QUESTIONS);
                                    if (field.getModifyTags() == null || !field.getModifyTags()) {
                                        if (StringUtils.isNotEmptyString(attrValue)) {
                                            if (Objects.nonNull(oldValue)) {
                                                // 比对 一下 选项内容有没有什么变化
                                                String oldValueDesc = oldValue.getString(VALUES_DESC);

                                                String oldValuesText = oldValue.getString(VALUES_TEXT);
                                                String newCheckBoxValuesDesc = jsonObject.getString(VALUES_DESC);
                                                if (notEquals(oldValueDesc, newCheckBoxValuesDesc)) {
                                                    fieldUpdateState = true;
                                                    field.setModifyTags(true);
                                                } else if (OTHER.equals(oldValuesText)) {
                                                    String otherValue = field.getOtherValue();
                                                    String fieldOtherValue = oldFormField.getOtherValue();
                                                    if (notEquals(otherValue, fieldOtherValue)) {
                                                        fieldUpdateState = true;
                                                        field.setModifyTags(true);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (Objects.nonNull(newQuestions)) {
                                        String fieldUpdate = checkFieldUpdate(oldQuestions,
                                                newQuestions);
                                        if (StringUtils.isNotEmptyString(fieldUpdate)) {
                                            fieldUpdateState = true;
                                            jsonObject.put(QUESTIONS, JSON.parseArray(fieldUpdate));
                                        }
                                    }
                                    jsonArray.add(jsonObject);
                                }
                            }
                            if (CollUtil.isNotEmpty(jsonArray)) {
                                field.setValues(jsonArray.toJSONString());
                            }
                        }
                    }

                } else if (FormFieldExactType.BMI.equals(fieldExactType) ||
                        FormWidgetType.NUMBER.equals(fieldWidgetType) ||
                        FormFieldExactType.GFR.equals(fieldExactType) ||
                        FormFieldExactType.CCR.equals(fieldExactType)) {
                    String newValues = parseResultValues(fieldValues);
                    String oldValues = parseResultValues(values);
                    if (notEqualsNumber(newValues, oldValues)) {
                        fieldUpdateState = true;
                        field.setModifyTags(true);
                    }
                } else if (
                        FormFieldExactType.COURSE_OF_DISEASE.equals(fieldExactType) ||
                        FormWidgetType.SINGLE_LINE_TEXT.equals(fieldWidgetType) ||
                        FormWidgetType.MULTI_LINE_TEXT.equals(fieldWidgetType) ||
                        FormWidgetType.FULL_NAME.equals(fieldWidgetType)) {
                    String newValues = parseResultValues(fieldValues);
                    String oldValues = parseResultValues(values);
                    if (notEquals(newValues, oldValues)) {
                        fieldUpdateState = true;
                        field.setModifyTags(true);
                    }
                } else if (FormWidgetType.DATE.equals(fieldWidgetType) ||
                        FormWidgetType.TIME.equals(fieldWidgetType)) {
                    String newValues = parseResultValues(fieldValues);
                    String oldValues = parseResultValues(values);
                    if (notEqualsDateOrTime(newValues, oldValues, fieldWidgetType)) {
                        fieldUpdateState = true;
                        field.setModifyTags(true);
                    }
                } else if (FormWidgetType.ADDRESS.equals(fieldWidgetType)) {
                    if (value == null) {
                        value = "";
                    }
                    if (notEquals(fieldValues, values)) {
                        fieldUpdateState = true;
                        field.setModifyTags(true);
                    }

                    if (Objects.nonNull(fieldValue)) {
                        if (!fieldValue.equals(value)) {
                            fieldUpdateState = true;
                            field.setModifyTags(true);
                        }
                    }
                } else {
                    if (notEquals(fieldValues, values)) {
                        fieldUpdateState = true;
                        field.setModifyTags(true);
                    }
                    if (Objects.nonNull(fieldValue)) {
                        if (!fieldValue.equals(value)) {
                            fieldUpdateState = true;
                            field.setModifyTags(true);
                        }
                    }
                }
            }
        }

        if (fieldUpdateState) {
            return JSON.toJSONString(newFormFields);
        } else {
            return null;
        }

    }

    /**
     * 判断两个字符串是否相等
     * @param value
     * @param value2
     * @return
     */
    public boolean notEquals(String value, String value2) {

        if (Objects.nonNull(value) && !value.equals(value2)) {
            return true;
        }
        if (Objects.nonNull(value2) && !value2.equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个数字是否相同
     * @param value
     * @param value2
     * @return
     */
    private static boolean notEqualsNumber(String value, String value2) {
        if (StrUtil.isNotEmpty(value)) {
            value = value.replace("-", "");
        }
        if (StrUtil.isNotEmpty(value2)) {
            value2 = value2.replace("-", "");
        }
        if (StringUtils.isEmpty(value) && StringUtils.isEmpty(value2)) {
            return false;
        }
        if (StrUtil.isEmpty(value) && StrUtil.isNotEmpty(value2)) {
            return true;
        }
        if (StrUtil.isNotEmpty(value) && StrUtil.isEmpty(value2)) {
            return true;
        }
        BigDecimal valueDecimal = new BigDecimal(value);
        BigDecimal value2Decimal = new BigDecimal(value2);
        if (valueDecimal.compareTo(value2Decimal) == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断两个 日期 或者 时间 是否相等
     */
    public boolean notEqualsDateOrTime(String value, String value2, String fieldWidgetType) {
        if (StringUtils.isNotEmptyString(value) && StringUtils.isNotEmptyString(value2)) {
            if (FormWidgetType.DATE.equals(fieldWidgetType)) {
                // 去掉日期中的连接符
                value = value.replace("-", "");
                value = value.replace("/", "");

                value2 = value2.replace("-", "");
                value2 = value2.replace("/", "");
                return notEquals(value, value2);
            } else {
                return notEquals(value, value2);
            }
        } else if (StringUtils.isNotEmptyString(value) || StringUtils.isNotEmptyString(value2)) {
            return true;
        } else {
            return false;
        }
    }


    public String parseResultValues(String values) {
        if (StringUtils.isEmpty(values)) {
            return null;
        }
        JSONArray array = JSON.parseArray(values);
        String resultValues = "";
        if (CollUtil.isNotEmpty(array)) {
            for (Object o : array) {
                JSONObject jsonObject = JSON.parseObject(o.toString());
                Object attrValue = jsonObject.get(ATTR_VALUE);
                if (Objects.nonNull(attrValue)) {
                    resultValues = attrValue.toString();
                }
            }
        }
        return resultValues;
    }

}
