package com.caring.sass.ai.ckd.server.impl;


import com.caring.sass.ai.ckd.dao.CkdLaboratoryTestReportMapper;
import com.caring.sass.ai.ckd.server.CkdLaboratoryTestReportService;
import com.caring.sass.ai.ckd.server.CkdUserGfrService;
import com.caring.sass.ai.ckd.server.CkdUserInfoService;
import com.caring.sass.ai.dto.ckd.CkdLaboratoryTestReportSaveDTO;
import com.caring.sass.ai.entity.ckd.CkdLaboratoryTestReport;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * ckd用户化验单
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Service

public class CkdLaboratoryTestReportServiceImpl extends SuperServiceImpl<CkdLaboratoryTestReportMapper, CkdLaboratoryTestReport> implements CkdLaboratoryTestReportService {


    @Autowired
    CkdUserGfrService ckdUserGfrService;

    @Autowired
    CkdUserInfoService ckdUserInfoService;

    /**
     * 提交化验单。
     * 更新化验单里面的数据
     * @param ckdLaboratoryTestReportSaveDTO
     * @return
     */
    @Override
    public CkdLaboratoryTestReport submitLaboratoryTestReport(CkdLaboratoryTestReportSaveDTO ckdLaboratoryTestReportSaveDTO) {


        CkdUserInfo userInfo = ckdUserInfoService.getByOpenId(ckdLaboratoryTestReportSaveDTO.getOpenId());
        if (userInfo.getSerumCreatinine() != null) {
            userInfo.setSerumCreatinine(ckdLaboratoryTestReportSaveDTO.getSerumCreatinine());
        }
        if (userInfo.getTcOfBloodLipid() != null) {
            userInfo.setTcOfBloodLipid(ckdLaboratoryTestReportSaveDTO.getTcOfBloodLipid());
        }
        if (userInfo.getKOfelectrolyte() != null) {
            userInfo.setKOfelectrolyte(ckdLaboratoryTestReportSaveDTO.getKOfelectrolyte());
        }
        if (userInfo.getNaOfelectrolyte() != null) {
            userInfo.setNaOfelectrolyte(ckdLaboratoryTestReportSaveDTO.getNaOfelectrolyte());
        }
        if (userInfo.getCaOfelectrolyte() != null) {
            userInfo.setCaOfelectrolyte(ckdLaboratoryTestReportSaveDTO.getCaOfelectrolyte());
        }
        if (userInfo.getPOfelectrolyte() != null) {
            userInfo.setPOfelectrolyte(ckdLaboratoryTestReportSaveDTO.getPOfelectrolyte());
        }
        if (userInfo.getUricAcid() != null) {
            userInfo.setUricAcid(ckdLaboratoryTestReportSaveDTO.getUricAcid());
        }

        ckdUserInfoService.updateById(userInfo);

        CkdLaboratoryTestReport testReport = new CkdLaboratoryTestReport();
        BeanUtils.copyProperties(ckdLaboratoryTestReportSaveDTO, testReport);
        baseMapper.insert(testReport);
        return testReport;
    }
}
