package com.caring.sass.nursing.service.drugs;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.drugs.DrugsConditionMonitoring;
import com.caring.sass.nursing.entity.drugs.PatientDrugs;
import com.caring.sass.nursing.vo.DrugsConditionMonitoringVO;
import com.caring.sass.tenant.entity.Tenant;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者管理-用药预警-预警条件表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
public interface DrugsConditionMonitoringService extends SuperService<DrugsConditionMonitoring> {

    /**
     * 查询患者管理-用药预警-预警药品及条件
     * @param tenantCode
     * @param drugsId
     * @return
     */
    List<DrugsConditionMonitoringVO> getDrugsConditionMonitoring(String tenantCode, Long drugsId);

    /**
     * 患者管理-用药预警-预警药品及条件-立即生效
     * @param tenantCode
     * @param flag true|false 立即执行|定时执行
     * @return
     */
    Boolean synDrugsConditionMonitoringTask(String tenantCode,Boolean flag);

    /**
     * 患者管理-用药预警-预警药品及条件 -患者更新药箱中药品
     * @param model
     * @param tenantCode
     * @return
     */
    Boolean synDrugsConditionMonitoringTaskPatientDrugsChange(PatientDrugs model, String tenantCode);
    /**
     * 患者管理-用药预警-患者停药删除相关预警信息
     * @param patientDrugs
     * @param tenantCode
     * @return
     */
    public Boolean synDeleteDrugsResultInformation(List<PatientDrugs> patientDrugs, String tenantCode);

    /**
     * 患者管理-用药预警-推送购药|逾期预警模板消息
     * @param tenant
     * @return
     */
    public Boolean pushBuyDrugsWarningMsgTask( Tenant tenant);

    /**
     * 将没有处理的余药不足数据， 剩余用药天数 -1 。 购药逾期未处理的天数 + 1
     * 余药不足天数 变更为 购药逾期
     * @param code
     */
    void synUpdateBuyDrugsWarningTask(String code);
}
