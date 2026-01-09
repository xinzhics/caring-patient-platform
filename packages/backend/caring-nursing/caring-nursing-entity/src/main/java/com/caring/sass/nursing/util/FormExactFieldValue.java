package com.caring.sass.nursing.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 确定的表单属性，如数字、性别，获取这些值
 *
 * @author xinzh
 */
@Data
public class FormExactFieldValue {

    /**
     * 前端定义的确切类型key
     */
    private static final String EXACT_TYPE = "exactType";
    /**
     * 前端定义的确切类型存值key
     */
    private static final String VALUES = "values";
    private static final String DIAGNOSE_ID = "diagnoseId";
    private static final String FOLLOW_STAGE_ID = "followStageId";
    private static final String ATTR_VALUE = "attrValue";
    private static final String VALUE_TEXT = "valueText";

    /**
     * 需要解析的确切类型字段
     */
    private static List<String> EXACT_TYPE_LIST = new ArrayList<String>() {{
        add(FormFieldExactType.HEIGHT);
        add(FormFieldExactType.WEIGHT);
        add(FormFieldExactType.BMI);
        add(FormFieldExactType.SCR);
        add(FormFieldExactType.GFR);
        add(FormFieldExactType.CCR);
        add(FormFieldExactType.COURSE_OF_DISEASE);
        add(FormFieldExactType.AGE);
        add(FormFieldExactType.GENDER);
        add(FormFieldExactType.BIRTHDAY);
        add(FormFieldExactType.NAME);
        add(FormFieldExactType.MOBILE);
        add(FormFieldExactType.EMAIL);
        add(FormFieldExactType.PHONE);
        add(FormFieldExactType.ADDRESS);
        add(FormFieldExactType.DIAGNOSE);
        add(FormFieldExactType.AVATAR);
        add(FormFieldExactType.FollowUpStage);
    }};

    private Integer height;
    private Integer weight;
    private Integer gender;
    private Integer age;
    private String bmi;
    private String scr;
    private String gfr;
    private String ccr;
    private String courseOfDisease;
    private String birthday;
    private String name;
    private String mobile;
    private String email;
    private String phone;
    private String address;
    private String diagnose;
    private String diagnoseId;
    private String avatar;
    private LocalDate followUpTime;

    // 用户自定义的随访开始日期。 优先级高于 followUpTime
    private LocalDate customerFollowUpTime;

    private String followStageId;
    private String followStageName;

    public static FormExactFieldValue parse(String formResultContent, String oldFormResultContent) {
        if (StrUtil.isBlank(formResultContent)) {
            return null;
        }
        List<JSONObject> o = JSONObject.parseArray(formResultContent, JSONObject.class);
        return parse(o, oldFormResultContent);
    }

    public static FormExactFieldValue parse(List<JSONObject> jsonObjects, String oldFormResultContent) {
        if (CollUtil.isEmpty(jsonObjects)) {
            return null;
        }
        FormExactFieldValue formExactFieldValue = new FormExactFieldValue();
        Map<String, Object> values = new HashMap<>(20);
        for (JSONObject j : jsonObjects) {
            String exactType = j.getString(EXACT_TYPE);
            if (!EXACT_TYPE_LIST.contains(exactType)) {
                // 不在需要解析的字段列表中，直接返回
                continue;
            }

            JSONArray v = j.getJSONArray(VALUES);
            // 没有值，不需要解析
            if (CollUtil.isEmpty(v)) {
                continue;
            }
            JSONObject jsonObjectValue = v.getJSONObject(0);
            // 诊断症状特殊处理
            String valueText = jsonObjectValue.getString(VALUE_TEXT);
            if (exactType.equals(FormFieldExactType.DIAGNOSE)) {
                values.put(FormFieldExactType.DIAGNOSE, valueText);
                values.put(DIAGNOSE_ID, jsonObjectValue.getString(ATTR_VALUE));
            } else if (exactType.equals(FormFieldExactType.GENDER)) {
                if ("男".equals(valueText)) {
                    values.put(FormFieldExactType.GENDER, 0);
                }
                if ("女".equals(valueText)) {
                    values.put(FormFieldExactType.GENDER, 1);
                }
            } else if (exactType.equals(FormFieldExactType.FollowUpStage)) {
                values.put(FormFieldExactType.FollowUpStage, valueText);
                values.put(FOLLOW_STAGE_ID, jsonObjectValue.getString(ATTR_VALUE));

                // 之前没有添加过
                LocalDate nursingTime = null;
                if (StrUtil.isEmpty(oldFormResultContent)) {
                    nursingTime = LocalDate.now();
                    Object questions = jsonObjectValue.get("questions");
                    if (Objects.nonNull(questions)) {
                        LocalDate followUpDate = getCustomFollowUpDate(JSON.toJSONString(questions));
                        if (Objects.nonNull(followUpDate)) {
                            nursingTime = followUpDate;
                        }
                    }
                } else {
                    // 之前已经添加过。
                    // 如果用户自己设置的本次随访阶段的开始时间。那么直接使用随访时间
                    // 获取本次的选项，和本次选项下设置的随访开始日期
                    String value = jsonObjectValue.getString(ATTR_VALUE);
                    Object questions = jsonObjectValue.get("questions");
                    LocalDate followUpDate = getCustomFollowUpDate(JSON.toJSONString(questions));
                    if (Objects.nonNull(followUpDate)) {
                        nursingTime = followUpDate;
                    } else {
                        // 获取上次的选项，和上次选项下设置的随访开始日期
                        FormField lastFollowUpField = getLastFollowUpField(oldFormResultContent);
                        if (Objects.nonNull(lastFollowUpField)) {
                            String lastFollowUpFieldValues = lastFollowUpField.getValues();
                            if (Objects.nonNull(lastFollowUpFieldValues)) {
                                JSONArray jsonArray = JSON.parseArray(lastFollowUpFieldValues);
                                JSONObject lastFollowUpValue = jsonArray.getJSONObject(0);
                                String lastValue = lastFollowUpValue.getString(ATTR_VALUE);
                                if (Objects.nonNull(value) && !value.equals(lastValue)) {
                                    nursingTime = LocalDate.now();
                                }
                            }
                        }
                    }
                }
                formExactFieldValue.setFollowUpTime(nursingTime);
            } else if (exactType.equals(FormFieldExactType.FollowStartDate)) {

                String valueString = jsonObjectValue.getString(ATTR_VALUE);
                LocalDate parseDate = parseDate(valueString);
                formExactFieldValue.setCustomerFollowUpTime(parseDate);

            } else {
                values.put(exactType, jsonObjectValue.getString(ATTR_VALUE));
            }
        }

        formExactFieldValue.setHeight(Convert.toInt(values.get(FormFieldExactType.HEIGHT)));
        formExactFieldValue.setWeight(Convert.toInt(values.get(FormFieldExactType.WEIGHT)));
        formExactFieldValue.setBmi(Convert.toStr(values.get(FormFieldExactType.BMI)));
        formExactFieldValue.setScr(Convert.toStr(values.get(FormFieldExactType.SCR)));
        formExactFieldValue.setGfr(Convert.toStr(values.get(FormFieldExactType.GFR)));
        formExactFieldValue.setCcr(Convert.toStr(values.get(FormFieldExactType.CCR)));
        formExactFieldValue.setCourseOfDisease(Convert.toStr(values.get(FormFieldExactType.COURSE_OF_DISEASE)));
        formExactFieldValue.setAge(Convert.toInt(values.get(FormFieldExactType.AGE)));
        formExactFieldValue.setGender(Convert.toInt(values.get(FormFieldExactType.GENDER)));
        formExactFieldValue.setBirthday(Convert.toStr(values.get(FormFieldExactType.BIRTHDAY)));
        formExactFieldValue.setName(Convert.toStr(values.get(FormFieldExactType.NAME)));
        formExactFieldValue.setMobile(Convert.toStr(values.get(FormFieldExactType.MOBILE)));
        formExactFieldValue.setEmail(Convert.toStr(values.get(FormFieldExactType.EMAIL)));
        formExactFieldValue.setPhone(Convert.toStr(values.get(FormFieldExactType.PHONE)));
        formExactFieldValue.setAddress(Convert.toStr(values.get(FormFieldExactType.ADDRESS)));
        formExactFieldValue.setDiagnose(Convert.toStr(values.get(FormFieldExactType.DIAGNOSE)));
        formExactFieldValue.setDiagnoseId(Convert.toStr(values.get(DIAGNOSE_ID)));
        formExactFieldValue.setAvatar(Convert.toStr(values.get(FormFieldExactType.AVATAR)));
        formExactFieldValue.setFollowStageId(Convert.toStr(values.get(FOLLOW_STAGE_ID)));
        formExactFieldValue.setFollowStageName(Convert.toStr(values.get(FormFieldExactType.FollowUpStage)));

        return formExactFieldValue;
    }


    /**
     * 获取上次提交的随访阶段题型
     * @param oldFormResultContent
     * @return
     */
    public static FormField getLastFollowUpField(String oldFormResultContent) {
        if (StrUtil.isEmpty(oldFormResultContent)) {
            return null;
        }
        List<FormField> formFields = JSON.parseArray(oldFormResultContent, FormField.class);
        FormField field = null;
        for (FormField formField : formFields) {
            String exactType = formField.getExactType();
            if (FormFieldExactType.FollowUpStage.equals(exactType)) {
                field = formField;
                break;
            }
        }
        return field;
    }

    /**
     * 从子题目中
     * 获取用户自定义的随访时间
     * @param questions
     * @return
     */
    public static LocalDate getCustomFollowUpDate(String questions) {
        if (StrUtil.isEmpty(questions)) {
            return null;
        }
        List<FormField> formFields = JSON.parseArray(questions, FormField.class);
        if (CollUtil.isEmpty(formFields)) {
            return null;
        }
        for (FormField field : formFields) {
            if (FormWidgetType.DATE.equals(field.getWidgetType())) {
                String childFieldValues = field.getValues();
                if (StrUtil.isNotEmpty(childFieldValues)) {
                    JSONArray jsonArray = JSON.parseArray(childFieldValues);
                    if (CollUtil.isNotEmpty(jsonArray)) {
                        Object o = jsonArray.get(0);
                        JSONObject object = JSON.parseObject(o.toString());
                        String value = object.getString(ATTR_VALUE);
                        return parseDate(value);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 格式化随访的日期
     * @param value
     * @return
     */
    public static LocalDate parseDate(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        if (value.contains("-")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(value, formatter);
        }
        if (value.contains("/")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            return LocalDate.parse(value, formatter);
        }
        return LocalDate.now();
    }

    /**
     * 重写jsonContent的值
     *
     * @param jsonContent
     * @param pairs       键值对，key为明确的类型，value为值
     * @return
     */
    public static String setValue(String jsonContent, Map<String, String> pairs) {
        if (StrUtil.isEmpty(jsonContent)) {
            return jsonContent;
        }

        if (pairs == null || pairs.entrySet().size() == 0) {
            return jsonContent;
        }

        List<FormField> formFields;
        try {
            formFields = JSONArray.parseArray(jsonContent, FormField.class);
        } catch (Exception e) {
            return jsonContent;
        }

        if (formFields == null) {
            return jsonContent;
        }

        pairs.entrySet().removeIf(entry -> !EXACT_TYPE_LIST.contains(entry.getKey()));
        if (pairs.entrySet().size() == 0) {
            return jsonContent;
        }

        for (FormField formField : formFields) {
            String value = pairs.get(formField.getExactType());
            if (StrUtil.isEmpty(value)) {
                continue;
            }
            JSONArray values = new JSONArray();
            JSONObject v = new JSONObject();
            v.put("attrValue",value);
            values.add(v);
            formField.setValues(values.toJSONString());
        }
        return JSONArray.toJSONString(formFields);
    }

}
