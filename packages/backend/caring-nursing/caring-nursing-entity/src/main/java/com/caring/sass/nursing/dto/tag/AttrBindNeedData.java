package com.caring.sass.nursing.dto.tag;

import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.form.FormResult;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AttrBindNeedData
 * @Description 绑定一个标签。可能需要的用户信息
 * @Author yangShuai
 * @Date 2022/4/25 13:29
 * @Version 1.0
 */
@Data
public class AttrBindNeedData {

    /**
     * 基本信息
     */
    FormResult baseInfo;

    /**
     * 疾病信息
     */
    FormResult healthRecord;
    /**
     * 监测计划表单的字段
     */
    List<FormField> monitoringPlanResult;

    /**
     * 用户的用药
     */
    List<PatientDrugs> patientDrugsList;


    public AttrBindNeedData(FormResult baseInfo, FormResult healthRecord, List<FormField> monitoringPlanResult, List<PatientDrugs> patientDrugsList) {

        this.baseInfo = baseInfo;
        this.healthRecord = healthRecord;
        this.monitoringPlanResult = monitoringPlanResult;
        this.patientDrugsList = patientDrugsList;

    }
}
