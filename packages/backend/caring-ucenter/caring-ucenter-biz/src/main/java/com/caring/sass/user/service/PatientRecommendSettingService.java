package com.caring.sass.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.dto.GetPatientRecommendDoctorDTO;
import com.caring.sass.user.dto.GetPatientRecommendDoctorVo;
import com.caring.sass.user.dto.RecommendUrlVo;
import com.caring.sass.user.entity.PatientRecommendSetting;

/**
 * <p>
 * 业务接口
 * 运营配置-患者推荐配置
 * </p>
 *
 * @author lixiang
 * @date 2022-07-14
 */
public interface PatientRecommendSettingService extends SuperService<PatientRecommendSetting> {
    /**
     * 超管端-患者推荐-查询
     *
     * @param
     */
    PatientRecommendSetting getPatientRecommend(String tenantCode);

    /**
     * 超管端-患者推荐-查询医生
     *
     * @param
     */
    IPage<GetPatientRecommendDoctorVo> getPatientRecommendDoctor(GetPatientRecommendDoctorDTO dto);

    /**
     * 超管端-患者推荐-推荐页链接地址-复制
     *
     * @param
     */
    RecommendUrlVo recommendUrlCopy(String tenantCode);
}
