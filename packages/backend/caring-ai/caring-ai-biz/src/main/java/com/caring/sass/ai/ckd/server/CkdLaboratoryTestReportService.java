package com.caring.sass.ai.ckd.server;

import com.caring.sass.ai.dto.ckd.CkdLaboratoryTestReportSaveDTO;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.ckd.CkdLaboratoryTestReport;

/**
 * <p>
 * 业务接口
 * ckd用户化验单
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
public interface CkdLaboratoryTestReportService extends SuperService<CkdLaboratoryTestReport> {



    CkdLaboratoryTestReport submitLaboratoryTestReport(CkdLaboratoryTestReportSaveDTO ckdLaboratoryTestReportSaveDTO);


}
