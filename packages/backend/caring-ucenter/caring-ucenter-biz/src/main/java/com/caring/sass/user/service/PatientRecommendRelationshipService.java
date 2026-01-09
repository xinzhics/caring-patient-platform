package com.caring.sass.user.service;


import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.dto.GeneratePatientInvitationQRcodeDTO;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.entity.PatientRecommendRelationship;
import com.caring.sass.user.entity.PatientRecommendSetting;

/**
 * <p>
 * 业务接口
 * 运营配置-患者推荐关系
 * </p>
 *
 * @author lixiang
 * @date 2022-07-14
 */
public interface PatientRecommendRelationshipService extends SuperService<PatientRecommendRelationship> {
    /**
     * 推荐人生成邀请二维码
     * @param patientId
     * @return
     */
    public GeneratePatientInvitationQRcodeDTO generatePatientInvitationQRcode(Long patientId);

    /**
     * 创建患者推荐关系
     * @param inviterPatient
     * @param subscribe
     * @param patientRecommendSetting
     * @param tenantCode
     */
    public void createPatientRecommendRelationship(Patient inviterPatient, Patient subscribe,Long createTime,PatientRecommendSetting patientRecommendSetting,String tenantCode);
}
