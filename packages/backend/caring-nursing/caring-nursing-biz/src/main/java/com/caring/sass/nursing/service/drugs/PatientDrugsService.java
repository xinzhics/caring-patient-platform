package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTimeSetting;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者添加的用药
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface PatientDrugsService extends SuperService<PatientDrugs> {

    /**
     * 保存患者的用药设置。 和用药时间
     * @param patientDrugs
     * @param timeSettingList
     */
    void savePatientDrugs(PatientDrugs patientDrugs, List<PatientDrugsTimeSetting> timeSettingList);

    /**
     * 单独更新患者药品的 用药时间设置 和 下次推送用药推送时间
     * @param model
     * @param timeSettings
     */
    void updatePatientDrugsTimeSetting(PatientDrugs model, List<PatientDrugsTimeSetting> timeSettings);

    /**
     * 更新患者的用药设置，和用药时间
     * @param patientDrugs
     * @param timeSettingList
     */
    void updatePatientDrugs(PatientDrugs patientDrugs, List<PatientDrugsTimeSetting> timeSettingList);

    /**
     * 设置用药时间
     * @param drugsList
     */
    void setDrugsTimeSetting(List<PatientDrugs> drugsList);


    void updateAllPatientDrugsTime();



}
