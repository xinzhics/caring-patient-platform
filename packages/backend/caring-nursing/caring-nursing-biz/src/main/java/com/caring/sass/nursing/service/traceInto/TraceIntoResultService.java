package com.caring.sass.nursing.service.traceInto;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFieldOptionConfig;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResult;
import org.springframework.scheduling.annotation.Async;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 选项跟踪结果异常表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
public interface TraceIntoResultService extends SuperService<TraceIntoResult> {

    /**
     * 当异常选项配置被删除时，清除对应的异常数据记录
     * @param configs
     */
    @Async
    void deleteByTraceIntoIds(Collection<TraceIntoFieldOptionConfig> configs, String tenantCode);


    void formDeleteEvent(Long formId, String tenantCode);

    /**
     * 处理这些异常选项相关表单结果 清洗异常数据
     * @param optionConfigList
     */
    void traceIntoTask(List<TraceIntoFieldOptionConfig> optionConfigList, String tenantCode);

    void traceIntoTask(List<FormResult> formResults, List<TraceIntoFieldOptionConfig> optionConfigList);

    /**
     * 一个表单结果发生变化。更新可能有的异常记录
     * @param formResult
     * @param tenantCode
     */
    void traceIntoTask(FormResult formResult, String tenantCode);
}
