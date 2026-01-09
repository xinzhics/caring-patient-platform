package com.caring.sass.nursing.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.nursing.dto.form.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表单工具 待优化
 *
 * @author xinzh
 */
public class FormUtil {

    /**
     * 字符串转表单属性
     *
     * @param customFormFields 自定义表单属性json
     * @return 表单属性
     */
    public static List<FormField> str2FormField(String customFormFields) {
        if (StrUtil.isBlank(customFormFields)) {
            return new ArrayList<>();
        }
        try {
            return JSONArray.parseArray(customFormFields, FormField.class);
        } catch (Exception e) {
            throw new RuntimeException("解析表单字段失败",e);
        }
    }

    public static String reWriteFormField(String customFormFields) {
        return JSONArray.toJSONString(reWriteFormField2List(customFormFields));
    }

    public static List<FormField> reWriteFormField2List(String customFormFields) {
        if (StrUtil.isBlank(customFormFields)) {
            return new ArrayList<>();
        }
        List<FormField> formFields;
        try {
            formFields = JSONArray.parseArray(customFormFields, FormField.class);
        } catch (Exception e) {
            throw new RuntimeException("解析表单字段失败", e);
        }
        if (formFields != null) {
            return reWriteFormField2List(formFields, false);
        }
        return new ArrayList<>();
    }

    /**
     * 检查一级题目里面有没有评分单选
     * @param customFormFields
     * @return
     */
    public static Integer isScoreQuestionnaire(String customFormFields) {
        if (StrUtil.isBlank(customFormFields)) {
            return 0;
        }
        Integer isScoreQuestionnaire = 0;
        List<FormField> formFields;
        try {
            formFields = JSONArray.parseArray(customFormFields, FormField.class);
            for (FormField field : formFields) {
                String exactType = field.getExactType();
                if (FormFieldExactType.SCORING_SINGLE_CHOICE.equals(exactType)) {
                    isScoreQuestionnaire = 1;
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析表单字段失败", e);
        }
        return isScoreQuestionnaire;
    }

    /**
     * 检查表单结果是正常还是异常
     * @return
     */
    public static Integer formErrorResult(String customFormFields) {
        Integer formErrorResult = 0;
        List<FormField> formFields;
        try {
            formFields = JSONArray.parseArray(customFormFields, FormField.class);
            for (FormField field : formFields) {
                String exactType = field.getExactType();
                String widgetType = field.getWidgetType();
                if (FormFieldExactType.DIAGNOSE.equals(exactType) || FormWidgetType.CHECK_BOX.equals(widgetType) || FormWidgetType.RADIO.equals(widgetType)) {
                    String values = field.getValues();
                    if (StrUtil.isNotEmpty(values)) {
                        JSONArray jsonArray = JSONArray.parseArray(values);
                        for (Object o : jsonArray) {
                            JSONObject object = JSONObject.parseObject(o.toString());
                            Object questions = object.get("questions");
                            if (Objects.nonNull(questions)) {
                                List<FormFieldDto> formFieldDtos = JSONArray.parseArray(questions.toString(), FormFieldDto.class);
                                for (FormFieldDto fieldDto : formFieldDtos) {
                                    String dtoWidgetType = fieldDto.getWidgetType();
                                    if (FormWidgetType.NUMBER.equals(dtoWidgetType) && StrUtil.isNotEmpty(fieldDto.getScopeConditions())) {
                                        List<String> resultValues = FormFieldDto.parseResultValues(fieldDto.getValues());
                                        if (CollUtil.isNotEmpty(resultValues)) {
                                            assert resultValues != null;
                                            String s = resultValues.get(0);
                                            Integer errorStatus = getFormErrorStatus(fieldDto.getScopeConditions(), fieldDto.getScopeXNumber(), fieldDto.getScopeYNumber(), s);
                                            if (formErrorResult == 0 || (formErrorResult == 1 && errorStatus != 0)) {
                                                formErrorResult = errorStatus;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析表单结果", e);
        }
        return formErrorResult;
    }


    /**
     * 判断用户的结果是正常还是异常
     * @param scopeConditions 条件
     * @param scopeXNumber x值
     * @param scopeYNumber y值
     * @param result 用户的结果
     * @return
     */
    private static Integer getFormErrorStatus(String scopeConditions, Double scopeXNumber, Double scopeYNumber, String result) {
        if (StrUtil.isEmpty(result)) {
            return 0;
        }
        BigDecimal decimal = new BigDecimal(result);
        if (Objects.isNull(scopeXNumber)) {
            return 0;
        }
        BigDecimal x = new BigDecimal(scopeXNumber);
        BigDecimal y;

        // 大于
        if ("gt".equals(scopeConditions)) {
            if (decimal.compareTo(x) > 0) {
                return 1;
            } else {
                return 2;
            }
            // 大于等于
        } else if ("gte".equals(scopeConditions)) {
            if (decimal.compareTo(x) >= 0) {
                return 1;
            } else {
                return 2;
            }
            // 小于
        } else if ("lt".equals(scopeConditions)) {
            if (decimal.compareTo(x) < 0) {
                return 1;
            } else {
                return 2;
            }
            // 小于等于
        } else if ("lte".equals(scopeConditions)) {
            if (decimal.compareTo(x) <= 0) {
                return 1;
            } else {
                return 2;
            }
            // 在区间内
        } else if ("interval".equals(scopeConditions)) {
            if (Objects.isNull(scopeYNumber)) {
                return 0;
            }
            y = new BigDecimal(scopeYNumber);
            if (decimal.compareTo(x) >= 0 && decimal.compareTo(y) <= 0) {
                return 1;
            } else {
                return 2;
            }
        }
        return 0;
    }


    public static List<FormField> reWriteFormField2List(List<FormField> customFormFields, boolean isChildren) {
        if (CollUtil.isEmpty(customFormFields)) {
            return new ArrayList<>();
        }

        for (FormField formField : customFormFields) {
            if (Objects.isNull(formField)) {
                continue;
            }
            if (StrUtil.isEmpty(formField.getId())) {
                formField.setId(IdUtil.simpleUUID());
            }
            List<FormOptions> fieldOptions = formField.getOptions();
            if (CollUtil.isNotEmpty(fieldOptions)) {
                boolean noOther = true;
                for (FormOptions fieldOption : fieldOptions) {
                    if (Objects.nonNull(fieldOption)) {
                        if (StrUtil.isEmpty(fieldOption.getId())) {
                            fieldOption.setId(IdUtil.simpleUUID());
                        }
                        if (fieldOption.getDefaultValue() != null && fieldOption.getDefaultValue()) {
                            formField.setDefaultValue(fieldOption.getId());
                        }
                        if (fieldOption.getAttrValue().startsWith("其他")) {
                            noOther = false;
                        }
                        List<FormField> fieldOptionQuestions = fieldOption.getQuestions();
                        reWriteFormField2List(fieldOptionQuestions, true);
                        List<FormOptions> optionChildren = fieldOption.getChildren();
                        if (CollUtil.isNotEmpty(optionChildren)) {
                            for (FormOptions optionChild : optionChildren) {
                                if (Objects.nonNull(optionChild)) {
                                    if (StrUtil.isEmpty(optionChild.getId())) {
                                        optionChild.setId(IdUtil.simpleUUID());
                                    }
                                    List<FormField> childQuestions = optionChild.getQuestions();
                                    reWriteFormField2List(childQuestions, true);
                                    List<FormOptions> children = optionChild.getChildren();
                                    if (CollUtil.isNotEmpty(children)) {
                                        for (FormOptions child : children) {
                                            if (Objects.nonNull(child)) {
                                                if (StrUtil.isEmpty(child.getId())) {
                                                    child.setId(IdUtil.simpleUUID());
                                                }
                                                List<FormField> questions = child.getQuestions();
                                                if (CollUtil.isNotEmpty(questions)) {
                                                    reWriteFormField2List(questions, true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // 子题目不需要设置 程序来设置其他 或取消其他
                if (formField.getHasOtherOption() != null && formField.getHasOtherOption() == 1 && !isChildren) {
                    if (noOther) {
                        FormOptions code = new FormOptions();
                        code.setId(IdUtil.simpleUUID());
                        code.setAttrValue("其他");
                        fieldOptions.add(code);
                    }
                } else {
                    if (!noOther && !isChildren) {
                        for (FormOptions fieldOption : fieldOptions) {
                            if (fieldOption.getAttrValue().startsWith("其他")) {
                                fieldOptions.remove(fieldOption);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return customFormFields;
    }


    /**
     * 计算一个表单最终的数据反馈结果
     * @param jsonContent
     * @return
     */
    public static String getPromptText(String jsonContent) {
        DataFeedBack feedBackResult = null;
        if (StrUtil.isNotEmpty(jsonContent)) {
            List<FormField> formFields = JSONArray.parseArray(jsonContent, FormField.class);
            for (FormField field : formFields) {
                if (FormWidgetType.NUMBER.equals(field.getWidgetType())) {
                    String values = field.getValues();
                    // 不显示数据反馈时。不计算数据反馈结果。
                    if (field.getShowDataFeedback() == null || !field.getShowDataFeedback()) {
                        continue;
                    }
                    List<DataFeedBack> dataFeedBacks = field.getDataFeedBacks();
                    if (CollUtil.isEmpty(dataFeedBacks)) {
                        continue;
                    }
                    if (StrUtil.isNotEmpty(values)) {
                        JSONArray jsonArray = JSONArray.parseArray(values);
                        if (CollUtil.isNotEmpty(jsonArray)) {
                            Object o = jsonArray.get(0);
                            JSONObject object = JSONObject.parseObject(o.toString());
                            Object value = object.get("attrValue");
                            if (Objects.nonNull(value)) {
                                for (DataFeedBack feedBack : dataFeedBacks) {
                                    Integer errorStatus = getFormErrorStatus("interval", feedBack.getMinValue(), feedBack.getMaxValue(), value.toString());
                                    if (errorStatus.equals(CommonStatus.YES)) {
                                        if (feedBackResult == null) {
                                            feedBackResult = feedBack;
                                        } else {
                                            // 如果是正常的值就可以覆盖。
                                            if (feedBackResult.getNormalAnomaly() != null && feedBackResult.getNormalAnomaly().equals(CommonStatus.YES)) {
                                                feedBackResult = feedBack;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (feedBackResult == null) {
            return null;
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("normalAnomaly", feedBackResult.getNormalAnomaly());
            jsonObject.put("normalAnomalyText", feedBackResult.getNormalAnomalyText());
            return jsonObject.toJSONString();
        }
    }
}
