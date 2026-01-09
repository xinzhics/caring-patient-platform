package com.caring.sass.nursing.service.exoprt.mentalPatientTracking;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.nursing.entity.form.FormResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 解析精神病患者的注射记录
 */
@Data
public class MentalInjectionExcelEntity {

    private static final String VALUES = "values";
    private static final String LABEL = "label";
    private static final String ATTR_VALUE = "attrValue";
    private static final String VALUE_TEXT = "valueText";

    private static final String ZHU_SHE_RI_QI_LABEL = "注射日期";

    private static final String LIAN_HE_YONG_YAO_LABEL = "联合用药";

    private static final String LIAN_HE_YONG_YAO_MING_CHEN_LABEL = "联合用药的名称";

    private static final String LIAN_HE_YONG_YAO_JI_LIANG_LABEL = "联合用药的剂量";

    private static final String LIAN_HE_YONG_YAO_YUAN_YIN_LABEL = "联合用药的原因";

    private static final String AOM_CHU_FANG_LABEL = "AOM处方";

    private static final String ZHEN_TOU_PEI_ZHI_LABEL = "针头配置";

    private static final String ZHU_SHE_DI_DIAN_LABEL = "注射地点";

    private static final String SHI_YONG_FAN_KUI_LABEL = "使用反馈";

    private String ZHU_SHE_RI_QI_VALUE;

    private String LIAN_HE_YONG_YAO_VALUE;

    private String LIAN_HE_YONG_YAO_MING_CHEN_VALUE;

    private String LIAN_HE_YONG_YAO_JI_LIANG_VALUE;

    private String LIAN_HE_YONG_YAO_YUAN_YIN_VALUE;

    private String AOM_CHU_FANG_VALUE;

    private String ZHEN_TOU_PEI_ZHI_VALUE;

    private String ZHU_SHE_DI_DIAN_VALUE;

    private String SHI_YONG_FAN_KUI_VALUE;


    public static MentalInjectionExcelEntity parse(List<JSONObject> jsonObjects) {
        if (CollUtil.isEmpty(jsonObjects)) {
            return null;
        }
        MentalInjectionExcelEntity excelEntity = new MentalInjectionExcelEntity();
        String label;
        for (JSONObject j : jsonObjects) {
            label = j.getString(LABEL);
            if (StrUtil.isEmpty(label)) {
                continue;
            }
            JSONArray v = j.getJSONArray(VALUES);
            // 没有值，不需要解析
            if (CollUtil.isEmpty(v)) {
                continue;
            }
            JSONObject jsonObjectValue = v.getJSONObject(0);
            if (Objects.isNull(jsonObjectValue)) {
                continue;
            }
            // 获取 联合用药 的值
            if (ZHU_SHE_RI_QI_LABEL.equals(label)) {
                excelEntity.ZHU_SHE_RI_QI_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else
            // 获取 联合用药 的值
            if (LIAN_HE_YONG_YAO_LABEL.equals(label)) {
                excelEntity.LIAN_HE_YONG_YAO_VALUE = jsonObjectValue.getString(VALUE_TEXT);
            } else
            // 联合用药的名称
            if (LIAN_HE_YONG_YAO_MING_CHEN_LABEL.equals(label)) {
                excelEntity.LIAN_HE_YONG_YAO_MING_CHEN_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else
            // 联合用药的剂量
            if (LIAN_HE_YONG_YAO_JI_LIANG_LABEL.equals(label)) {
                excelEntity.LIAN_HE_YONG_YAO_JI_LIANG_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else
            // 联合用药的原因
            if (LIAN_HE_YONG_YAO_YUAN_YIN_LABEL.equals(label)) {
                excelEntity.LIAN_HE_YONG_YAO_YUAN_YIN_VALUE = jsonObjectValue.getString(VALUE_TEXT);
            } else
            // AOM处方
            if (AOM_CHU_FANG_LABEL.equals(label)) {
                excelEntity.AOM_CHU_FANG_VALUE = jsonObjectValue.getString(VALUE_TEXT);
            } else
            // 针头配置
            if (ZHEN_TOU_PEI_ZHI_LABEL.equals(label)) {
                excelEntity.ZHEN_TOU_PEI_ZHI_VALUE = jsonObjectValue.getString(VALUE_TEXT);
            } else
            // 注射地点
            if (ZHU_SHE_DI_DIAN_LABEL.equals(label)) {
                excelEntity.ZHU_SHE_DI_DIAN_VALUE = jsonObjectValue.getString(VALUE_TEXT);
            } else
            // 使用反馈
            if (SHI_YONG_FAN_KUI_LABEL.equals(label)) {
                excelEntity.SHI_YONG_FAN_KUI_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            }
        }
        return excelEntity;
    }


    public static List<MentalInjectionExcelEntity> parseFromResult(List<FormResult> formResults) {

        if (CollUtil.isEmpty(formResults)) {
            return new ArrayList<>();
        }
        List<MentalInjectionExcelEntity> list = new ArrayList<>();

        // 提供一个Comparator来根据createTime字段进行排序
        formResults.sort(Comparator.comparing(FormResult::getCreateTime).reversed());
        for (FormResult formResult : formResults) {
            String jsonContent = formResult.getJsonContent();
            if (StrUtil.isEmpty(jsonContent)) {
                continue;
            }
            List<JSONObject> jsonArray = JSONObject.parseArray(jsonContent, JSONObject.class);
            MentalInjectionExcelEntity excelEntity = MentalInjectionExcelEntity.parse(jsonArray);
            list.add(excelEntity);
        }
        return list;
    }
}
