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
 * 精神病患者 用药跟踪 表单结果解析
 */
@Data
public class MentalMedicationTracking {

    private static final String VALUES = "values";
    private static final String LABEL = "label";
    private static final String ATTR_VALUE = "attrValue";
    private static final String VALUE_TEXT = "valueText";

    private static final String YONG_YAO_1_YUE_LABEL = "用药后跟踪1个月";
    private static final String YONG_YAO_2_YUE_LABEL = "用药后跟踪2个月";
    private static final String YONG_YAO_3_YUE_LABEL = "用药后跟踪3个月";
    private static final String YONG_YAO_4_YUE_LABEL = "用药后跟踪4个月";
    private static final String YONG_YAO_5_YUE_LABEL = "用药后跟踪5个月";
    private static final String YONG_YAO_6_YUE_LABEL = "用药后跟踪6个月";
    private static final String TING_YAO_SHI_JIAN_LABEL = "停药时间";
    private static final String TING_YAO_YUAN_YIN_LABEL = "停药原因";
    private static final String BEI_ZHU_LABEL = "备注";

    private  String YONG_YAO_1_YUE_VALUE;

    private  String YONG_YAO_2_YUE_VALUE;

    private String YONG_YAO_3_YUE_VALUE;

    private String YONG_YAO_4_YUE_VALUE;

    private String YONG_YAO_5_YUE_VALUE;

    private String YONG_YAO_6_YUE_VALUE;

    private String TING_YAO_SHI_JIAN_VALUE;

    private String TING_YAO_YUAN_YIN_VALUE;

    private String BEI_ZHU_VALUE;


    public static MentalMedicationTracking parse(List<JSONObject> jsonObjects) {
        if (CollUtil.isEmpty(jsonObjects)) {
            return null;
        }
        MentalMedicationTracking excelEntity = new MentalMedicationTracking();
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
            if (YONG_YAO_1_YUE_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.YONG_YAO_1_YUE_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else if (YONG_YAO_2_YUE_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.YONG_YAO_2_YUE_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else if (YONG_YAO_3_YUE_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.YONG_YAO_3_YUE_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else if (YONG_YAO_4_YUE_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.YONG_YAO_4_YUE_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else if (YONG_YAO_5_YUE_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.YONG_YAO_5_YUE_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else if (YONG_YAO_6_YUE_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.YONG_YAO_6_YUE_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else if (TING_YAO_SHI_JIAN_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.TING_YAO_SHI_JIAN_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else if (TING_YAO_YUAN_YIN_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.TING_YAO_YUAN_YIN_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            } else if (BEI_ZHU_LABEL.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.BEI_ZHU_VALUE = jsonObjectValue.getString(ATTR_VALUE);
            }
        }
        return excelEntity;
    }


    public static List<MentalMedicationTracking> parseFromResult(List<FormResult> formResults) {
        if (CollUtil.isEmpty(formResults)) {
            return new ArrayList<>();
        }
        List<MentalMedicationTracking> list = new ArrayList<>();

        // 提供一个Comparator来根据createTime字段进行排序
        formResults.sort(Comparator.comparing(FormResult::getCreateTime).reversed());
        for (FormResult formResult : formResults) {
            String jsonContent = formResult.getJsonContent();
            if (StrUtil.isEmpty(jsonContent)) {
                continue;
            }
            List<JSONObject> jsonArray = JSONObject.parseArray(jsonContent, JSONObject.class);
            MentalMedicationTracking excelEntity = MentalMedicationTracking.parse(jsonArray);
            list.add(excelEntity);
        }
        return list;
    }
}
