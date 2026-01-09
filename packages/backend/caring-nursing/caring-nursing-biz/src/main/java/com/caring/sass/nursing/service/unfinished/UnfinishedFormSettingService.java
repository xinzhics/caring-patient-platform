package com.caring.sass.nursing.service.unfinished;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.traceInto.PlanFormDTO;
import com.caring.sass.nursing.dto.unfinished.UnfinishedFormSettingUpdateDTO;
import com.caring.sass.nursing.entity.unfinished.UnfinishedFormSetting;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者管理-未完成表单跟踪设置
 * </p>
 *
 * @author 杨帅
 * @date 2024-05-27
 */
public interface UnfinishedFormSettingService extends SuperService<UnfinishedFormSetting> {

    List<PlanFormDTO> webQueryConfig();


    /**
     * 更新整个 未完成随访跟踪
     * @param updateDTOList
     */
    void updateConfig(List<UnfinishedFormSettingUpdateDTO> updateDTOList);

    void xxlJobUnFinishedTask(String tenantCode);

    /**
     * 未完成跟踪立即生效
     * @param tenantCode
     */
    void unFinishedTask(String tenantCode);
}
