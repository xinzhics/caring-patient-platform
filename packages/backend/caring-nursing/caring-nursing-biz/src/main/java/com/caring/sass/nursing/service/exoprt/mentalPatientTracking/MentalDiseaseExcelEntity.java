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
 * 精神病项目患者 疾病信息表单 跟踪
 */
@Data
public class MentalDiseaseExcelEntity {

    private static final String VALUES = "values";
    private static final String LABEL = "label";
    private static final String ATTR_VALUE = "attrValue";
    private static final String VALUE_TEXT = "valueText";
    private static final String QUESTIONS = "questions";

    /**
     * 对应表格中的 临床诊断
     */
    private static final String lin_chang_zhen_duan_label = "疾病诊断";

    private static final String huan_bing_shi_chang_label = "患病时长";


    private static final String ji_wang_yong_yao_label = "既往用药";
    private static final String ji_wang_yong_yao_ji_liang_label = "既往用药剂量";


    private static final String liao_xiao_ping_jia_label = "疗效评价";

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private static final String dang_qian_zhi_liao_zhuang_tai = "当前治疗状态";

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private static final String zhu_she_a_li_pai_zuo_zhi_liao = "注射阿立哌唑治疗";

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private static final String ben_ci_chu_fang_mai_da_yuan_yin_label = "本次处方迈达原因";

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private static final String chu_fang_shi_jian_label = "处方时间";

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private static final String zhu_she_shi_kou_fu_a_li_pai_zuo_liang_label = "注射时口服阿立哌唑剂量";

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private static final String start_kou_fu_a_li_pai_zuo_shi_jian_label = "开始口服阿立哌唑时间";


    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private static final String kou_fu_a_li_pai_zuo_pin_pai_label = "口服阿立哌唑品牌";


    private  String lin_chang_zhen_duan_value;

    private String huan_bing_shi_chang_value;


    private String ji_wang_yong_yao_value;
    private String ji_wang_yong_yao_ji_liang_value;


    private String liao_xiao_ping_jia_value;

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private String ben_ci_chu_fang_mai_da_yuan_yin_value;

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private String chu_fang_shi_jian_value;

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private String zhu_she_shi_kou_fu_a_li_pai_zuo_liang_value;

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private String start_kou_fu_a_li_pai_zuo_shi_jian_value;

    /**
     * 从  当前治疗状态  题目的 注射阿立哌唑治疗 选项下的子题目中获取
     */
    private String kou_fu_a_li_pai_zuo_pin_pai_value;

    public static MentalDiseaseExcelEntity parse(List<JSONObject> jsonObjects) {
        if (CollUtil.isEmpty(jsonObjects)) {
            return null;
        }
        MentalDiseaseExcelEntity excelEntity = new MentalDiseaseExcelEntity();
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
            // 获取 疾病诊断的值
            if (lin_chang_zhen_duan_label.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.lin_chang_zhen_duan_value = jsonObjectValue.getString(VALUE_TEXT);
            } else
            // 患病时长
            if (huan_bing_shi_chang_label.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.huan_bing_shi_chang_value = jsonObjectValue.getString(ATTR_VALUE);
            } else
            // 既往用药
            if (ji_wang_yong_yao_label.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.ji_wang_yong_yao_value = jsonObjectValue.getString(ATTR_VALUE);
            } else
            // 既往用药剂量
            if (ji_wang_yong_yao_ji_liang_label.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.ji_wang_yong_yao_ji_liang_value = jsonObjectValue.getString(ATTR_VALUE);
            } else
            // 疗效评价
            if (liao_xiao_ping_jia_label.equals(label)) {
                JSONObject jsonObjectValue = v.getJSONObject(0);
                if (Objects.isNull(jsonObjectValue)) {
                    continue;
                }
                excelEntity.liao_xiao_ping_jia_value = jsonObjectValue.getString(ATTR_VALUE);
            } else
            // 当前治疗状态
            if (dang_qian_zhi_liao_zhuang_tai.equals(label)) {
                for (Object object : v) {
                    JSONObject jsonObject = JSONObject.parseObject(object.toString());
                    String string = jsonObject.getString(VALUE_TEXT);
                    if (StrUtil.isEmpty(string)) {
                        continue;
                    }
                    // 找到 注射阿立哌唑治疗
                    if (string.equals(zhu_she_a_li_pai_zuo_zhi_liao)) {
                        JSONArray questions = jsonObject.getJSONArray("questions");
                        if (questions.isEmpty()) {
                            continue;
                        }
                        for (Object question : questions) {
                            JSONObject quesJson = JSONObject.parseObject(question.toString());
                            label = quesJson.getString(LABEL);
                            JSONArray quesValues = quesJson.getJSONArray(VALUES);
                            if (quesValues.isEmpty()) {
                                continue;
                            }
                            if (ben_ci_chu_fang_mai_da_yuan_yin_label.equals(label)) {
                                JSONObject jsonObjectValue = quesValues.getJSONObject(0);
                                excelEntity.ben_ci_chu_fang_mai_da_yuan_yin_value = jsonObjectValue.getString(ATTR_VALUE);
                            } else if (chu_fang_shi_jian_label.equals(label)) {
                                JSONObject jsonObjectValue = quesValues.getJSONObject(0);
                                excelEntity.chu_fang_shi_jian_value = jsonObjectValue.getString(ATTR_VALUE);
                            } else if (zhu_she_shi_kou_fu_a_li_pai_zuo_liang_label.equals(label)) {
                                JSONObject jsonObjectValue = quesValues.getJSONObject(0);
                                excelEntity.zhu_she_shi_kou_fu_a_li_pai_zuo_liang_value = jsonObjectValue.getString(VALUE_TEXT);
                            } else if (start_kou_fu_a_li_pai_zuo_shi_jian_label.equals(label)) {
                                JSONObject jsonObjectValue = quesValues.getJSONObject(0);
                                excelEntity.start_kou_fu_a_li_pai_zuo_shi_jian_value = jsonObjectValue.getString(ATTR_VALUE);
                            } else if (kou_fu_a_li_pai_zuo_pin_pai_label.equals(label)) {
                                JSONObject jsonObjectValue = quesValues.getJSONObject(0);
                                excelEntity.kou_fu_a_li_pai_zuo_pin_pai_value = jsonObjectValue.getString(VALUE_TEXT);
                            }
                        }
                    }
                }
            }
        }
        return excelEntity;
    }


    public static List<MentalDiseaseExcelEntity> parseFromResult(List<FormResult> formResults) {

        if (CollUtil.isEmpty(formResults)) {
            return new ArrayList<>();
        }
        List<MentalDiseaseExcelEntity> list = new ArrayList<>();

        // 提供一个Comparator来根据createTime字段进行排序
        formResults.sort(Comparator.comparing(FormResult::getCreateTime).reversed());
        for (FormResult formResult : formResults) {
            String jsonContent = formResult.getJsonContent();
            if (StrUtil.isEmpty(jsonContent)) {
                continue;
            }
            List<JSONObject> jsonArray = JSONObject.parseArray(jsonContent, JSONObject.class);
            MentalDiseaseExcelEntity excelEntity = MentalDiseaseExcelEntity.parse(jsonArray);
            list.add(excelEntity);
        }
        return list;
    }
}
