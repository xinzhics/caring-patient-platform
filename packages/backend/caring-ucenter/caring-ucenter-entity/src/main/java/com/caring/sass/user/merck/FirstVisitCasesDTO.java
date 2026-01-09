package com.caring.sass.user.merck;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@ApiModel("初诊病历实体FirstVisitCasesDTO")
public class FirstVisitCasesDTO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("患者id")
    private Long personId;

    @ApiModelProperty("机构Id")
    private Long businessOrgId;

    @ApiModelProperty("是否归档")
    private Integer archive;

    @ApiModelProperty("初诊疾病(常量分组：const_first_visit_disease")
    private Set<String> firstVisitDisease;

    @ApiModelProperty("并发症备注: 用户选择初诊疾病后，可能伴有并发症")
    private String complicationsNote;

    @ApiModelProperty("诊断日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date diagnoseDate;

    @ApiModelProperty("临床症状(常量分组：const_clinical_symptoms_nose,const_clinical_symptoms_eyes,const_clinical_symptoms_chest）")
    private Set<String> clinicalSymptoms;

    @ApiModelProperty("出现过敏症状和加重的病史特点,与季节相关选项(常量分组：const_allergy_symptoms_season）")
    private String seasonAllergySymptoms;

    @ApiModelProperty("出现过敏症状和加重的病史特点,时间相关选项(常量分组：const_allergy_symptoms_time）")
    private String timeAllergySymptoms;

    @ApiModelProperty("出现过敏症状和加重的病史特点,与场所相关(常量分组：const_allergy_symptoms_env）")
    private String envAllergySymptoms;

    @ApiModelProperty("出现过敏症状和加重的病史特点,发作持续时间(常量分组：const_allergy_symptoms_duration_time）")
    private String durationTimeAllergySymptoms;

    @ApiModelProperty("出现过敏症状和加重的病史特点,其他")
    private String otherAllergySymptoms;

    @ApiModelProperty("患者有无食物过敏史(常量分组：const_foodallergy_history）")
    private String foodAllergyHistory;

    @ApiModelProperty("请填写过敏食物(常量分组：const_allergenic_food）")
    private Set<String> allergenicFoods;

    @ApiModelProperty("患者食物过敏史备注")
    private String foodAllergyHistoryRemark;

    @ApiModelProperty("患者有无药物过敏史(常量分组：const_has_medicine_allergy_history）")
    private String hasMedicineAllergyHistory;

    @ApiModelProperty("请填写过敏药物(常量分组：const_medicine_allergy_history）")
    private Set<String> medicineAllergyHistories;

    @ApiModelProperty("其他过敏药物文本填写")
    private String otherMedicineAllergyHistory;

    @ApiModelProperty("患者药物过敏史备注")
    private String medicineAllergyHistoryRemark;

    @ApiModelProperty("既往患者有无严重过敏反应病史(常量分组：const_allergy_history）")
    private String hasAllergyHistory;

    @ApiModelProperty("出现次数")
    private Integer allergyOccursNumber;

    @ApiModelProperty("每次出现时间和类型")
    private String allergyOccursTimeAndTypeEveryTime;

    @ApiModelProperty("最近出现时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastAllergyOccursDate;

    @ApiModelProperty("过去一年中，患者哮喘症状平均出现的频繁情况(常量分组：const_asthma_symptoms_frequency）")
    private String lastYearAsthmaSymptomsFrequency;

    @ApiModelProperty("过去一年中，因哮喘症状在夜间或凌晨醒来的平均出现频率(常量分组：const_asthma_symptoms_frequency）")
    private String lastYearNightEarlyMorningAsthmaSymptomsFrequency;

    @ApiModelProperty("哮喘发作时限制正常的体力活动吗(常量分组：const_asthma_symptoms_activities_limited）")
    private String asthmaSymptomsActivitiesLimited;

    @ApiModelProperty("患者在锻炼或者体力活动时间可诱发哮喘症状吗(常量分组：const_activities_raise_asthma_symptoms）")
    private String activitiesRaiseAsthmaSymptoms;

    @ApiModelProperty("过去一年中，因哮喘影响工作和学习吗(常量分组：const_asthma_symptoms_work_learning_effect）")
    private String asthmaSymptomsWorkLearningEffect;

    @ApiModelProperty("您有无接受治疗（常量分组：const_asthma_treatment）")
    private String asthmaTreatment;

    @ApiModelProperty("过去一年中，因哮喘加重就诊次数：门诊")
    private Integer outPatientServiceCount;

    @ApiModelProperty("过去一年中，因哮喘加重就诊次数：住院")
    private Integer inHospitalCount;

    @ApiModelProperty("过去一年中，患者因为哮喘加重而需口服糖皮质激素（如强的松）的频繁程度 疗程")
    private Integer glucocorticoidTreatmentCount;

    @ApiModelProperty("过去一年中，患者因为哮喘加重而需口服糖皮质激素（如强的松）的频繁程度 天数")
    private Integer glucocorticoidUsageDays;

    @ApiModelProperty("鼻炎相关症状的发作时间——在一年内持续发作的时间(常量分组：const_rhinitis_time）")
    private String rhinitisTimeInfo;

    @ApiModelProperty("鼻炎相关症状的发作时间——在一周内持续发作的时间 天数")
    private Integer rhinitisDaysOfWeek;

    @ApiModelProperty("您的鼻部不适主要包括以下哪些症状(常量分组：const_nose_symptoms）")
    private Set<String> noseSymptoms;

    @ApiModelProperty("是否伴有其他鼻腔疾病(常量分组：const_has_others_nasal_diseases）")
    private String hasOthersNasalDiseases;

    @ApiModelProperty("伴有的其他鼻腔疾病(常量分组：const_others_nasal_diseases）")
    private Set<String> othersNasalDiseases;

    @ApiModelProperty("其他填写的鼻炎治疗药物备注")
    private String otherNasalDiseasesMedicineRemark;

    @ApiModelProperty("鼻部手术次数")
    private Integer nasalOperationCount;

    @ApiModelProperty("以前是否接受过鼻部手术(常量分组：const_has_nose_operation_before）")
    private String hasNoseOperationBefore;


    @ApiModelProperty("在治疗过敏性疾病中，您是否使用过药物，常量分组：(const_nasal_diseases_medicine)")
    private String hasNasalDiseasesMedicines;

    @ApiModelProperty("在鼻炎治疗过程中，您使用过以下药物的哪几种" +
                      "(常量分组：" +
                      "无 const_nasal_diseases_medicine," +
                      "口服 const_nasal_diseases_medicine_oral," +
                      "吸入或雾化 const_nasal_diseases_medicine_inhalation," +
                      "喷入或滴入 const_nasal_diseases_medicine_spray）")
    private Set<String> nasalDiseasesMedicines;

    @ApiModelProperty("其他过食物文本填写")
    private String otherFoodAllergyHistory;

    @ApiModelProperty("其他填写的鼻炎治疗药物")
    private String otherNasalDiseasesMedicine;

    @ApiModelProperty("您有过过敏性皮炎史吗（常量：const_has_allergic_dermatitis）")
    private String hasAllergic_dermatitis;

    @ApiModelProperty("上述痒疹是否在下列部位出现过" +
                      "(常量分组：const_allergic_dermatitis_part")
    private List<String> allergicDermatitisParts;

    @ApiModelProperty("上述痒疹是否在下列部位出现过 选择其他时，此字段必填")
    private String allergicDermatitisOther;

    @ApiModelProperty("近一年上述痒疹有发作吗（常量：const_has_prurigo）")
    private String hasPrurigo;

    @ApiModelProperty("近一年上述痒疹有过完全消失吗（常量：const_prurigo_disapear）")
    private String hasPrurigoDisapear;

    @ApiModelProperty("近一年内你夜间有多少次因疹子发痒而难以入睡（常量：const_prurigo_sleep_effect）")
    private String prurigoSleepEffect;

    @ApiModelProperty("过敏源报告")
    private Long allergySourceReportImageId;

    @ApiModelProperty("检查报告图片列表")
    private List<ImageDTO> reportImgList;

}
