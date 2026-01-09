package com.caring.sass.nursing.service.exoprt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormWidgetType;

import java.util.List;
import java.util.Objects;

/**
 * 解析 表单结果字段 中的结果。用于 导出表格中使用
 */
public class ExportFormFieldResultUtil {

    private static String valueText = "valueText";

    private static String questions = "questions";

    private static String valueTextOther = "其他";



    private static String desc = "desc";

    private static String chooseValue(FormFieldDto dto) {
        String values = dto.getValues();
        if (StrUtil.isEmpty(values)) {
            return "";
        }
        List<JSONObject> jsonObjects = JSON.parseArray(values, JSONObject.class);
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        for (JSONObject jsonObject : jsonObjects) {
            i++;
            // 答案的文本
            Object text = jsonObject.get(valueText);
            if (Objects.isNull(text)) {
                break;
            }
            // 答案的描述
            String valueDesc = null;
            // 如果选项描述是 其他，那么判断是否设置了 其他选项显示时的备注。
            if (valueTextOther.equals(text.toString())) {
                String labelRemark = dto.getOtherLabelRemark();
                // 导出时，显示其他为备注
                if (StrUtil.isNotEmpty(labelRemark)) {
                    stringBuffer.append(labelRemark);
                } else {
                    stringBuffer.append(text);
                }
                valueDesc = dto.getOtherValue();
            } else {
                stringBuffer.append(text);
                Object o = jsonObject.get(desc);
                if (Objects.nonNull(o)) {
                    valueDesc = o.toString();
                }
            }
            if (StrUtil.isNotEmpty(valueDesc)) {
                stringBuffer.append("(").append(valueDesc).append(")");
            }
            // 备注
            // 子题目
            JSONArray questionObj = jsonObject.getJSONArray(questions);

            if (CollUtil.isNotEmpty(questionObj)) {
                List<FormFieldDto> formFieldDtos = JSON.parseArray(questionObj.toJSONString(), FormFieldDto.class);
                if (CollUtil.isNotEmpty(formFieldDtos)) {
                    int questionIdx = 1;
                    for (FormFieldDto fieldDto : formFieldDtos) {
                        stringBuffer.append("\n");
                        String fieldValue = getFieldValue(fieldDto);
                        stringBuffer.append("【题目").append(questionIdx).append("】").append(fieldDto.getLabel()).append("\n");
                        stringBuffer.append("【答案】").append(fieldValue);
                    }
                }
                stringBuffer.append("\n");
            } else {
                if (i< jsonObjects.size()) {
                    stringBuffer.append("、");
                }
            }

        }
        return stringBuffer.toString();
    }


    /**
     * 敏宁项目获取患者 基本信息中的 筛选号。
     * @return
     */
    public static String getShaiXuanHao(String jsonContent) {

        if (StrUtil.isEmpty(jsonContent)) {
            return "";
        }
        List<FormFieldDto> formFieldDtos = JSONArray.parseArray(jsonContent, FormFieldDto.class);
        if (CollUtil.isEmpty(formFieldDtos)) {
            return "";
        }
        String result = null;
        for (FormFieldDto fieldDto : formFieldDtos) {
            String label = fieldDto.getLabel();
            if (StrUtil.isNotBlank(label) && label.contains("筛选")) {
                result = fieldDto.parseResultValues();
                break;
            }
        }
        return result;

    }


    /**
     * 获取字段的结果
     * @param dto
     * @return
     */
    public static String getFieldValue(FormFieldDto dto) {
        String widgetType = dto.getWidgetType();

        switch (widgetType) {
            case FormWidgetType.SINGLE_LINE_TEXT:
            case FormWidgetType.MULTI_LINE_TEXT:
            case FormWidgetType.NUMBER:
            case FormWidgetType.TIME:
            case FormWidgetType.DATE:
            case FormWidgetType.FULL_NAME:
            case FormWidgetType.MULTI_IMAGE_UPLOAD:
            case FormWidgetType.AVATAR: {
                return dto.parseResultValues();
            }
            case FormWidgetType.RADIO:
            case FormWidgetType.CHECK_BOX:
            case FormWidgetType.DROPDOWN_SELECT: {
                return chooseValue(dto);
            }
            case FormWidgetType.ADDRESS: {
                // 先获取 选择的 地址。
                // 在获取 地址的详细描述
                String resultValues = dto.parseResultValues();
                String value = dto.getValue();
                if (StrUtil.isNotEmpty(value)) {
                    return resultValues + value;
                } else {
                    return resultValues;
                }
            }

            default: {
                return "";
            }
        }

    }


}
