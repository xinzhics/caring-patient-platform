package com.caring.sass.nursing.service.traceInto;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.traceInto.PlanFormDTO;
import com.caring.sass.nursing.dto.traceInto.TraceIntoFieldOptionConfigUpdateDTO;
import com.caring.sass.nursing.dto.traceInto.TraceIntoFieldSettingConfigDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFieldOptionConfig;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 选项跟踪表单字段选项配置表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
public interface TraceIntoFieldOptionConfigService extends SuperService<TraceIntoFieldOptionConfig> {

    /**
     * web端查询选项跟踪的配置
     * @return
     */
    List<PlanFormDTO> webQueryConfig();

    /**
     * 更新选项跟踪配置
     * @param updateDTOList
     */
    void updateConfig(List<TraceIntoFieldOptionConfigUpdateDTO> updateDTOList);


    /**
     * 立即生效
     * @param tenantCode
     */
    void traceIntoTask(String tenantCode);


    /**
     * 表单被修改。清除异常选项配置
     * 并根据清除的异常选项配置，清除异常数据记录
     * @param model
     * @param tenant
     */
    void clearConfig(Form model, String tenant);

    /**
     * 清除 表单ID相关的所有的 异常选项配置
     * @param formId
     */
    void clearFormAllConfig(Long formId);
}
