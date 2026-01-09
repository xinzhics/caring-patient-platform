package com.caring.sass.nursing.service.unfinished;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.traceInto.AppTracePlanList;
import com.caring.sass.nursing.dto.unfinished.UnfinishedListResult;
import com.caring.sass.nursing.entity.unfinished.UnfinishedFormSetting;
import com.caring.sass.nursing.entity.unfinished.UnfinishedPatientRecord;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 未完成推送的患者记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-05-27
 */
public interface UnfinishedPatientRecordService extends SuperService<UnfinishedPatientRecord> {

    /**
     * 统计医助下患者有多少有未完成的提醒
     * @param nursingId
     * @return
     */
    int countNursingHandleNumber(Long nursingId);


    void unFinishedTask(List<UnfinishedFormSetting> formSettingList, String tenantCode, Boolean inTime);

    List<AppTracePlanList> getAppUnFinishedPlanList(Long nursingId);

    IPage<UnfinishedListResult> appPage(IPage<UnfinishedPatientRecord> page, LbqWrapper<UnfinishedPatientRecord> lbqWrapper);
}
