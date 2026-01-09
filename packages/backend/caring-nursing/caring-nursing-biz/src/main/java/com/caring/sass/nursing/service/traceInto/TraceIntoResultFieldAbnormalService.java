package com.caring.sass.nursing.service.traceInto;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.traceInto.TraceIntoOptionStatisticsDTO;
import com.caring.sass.nursing.entity.traceInto.TraceIntoResultFieldAbnormal;

import java.time.LocalDate;

/**
 * <p>
 * 业务接口
 * 选项跟踪异常题目明细记录表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
public interface TraceIntoResultFieldAbnormalService extends SuperService<TraceIntoResultFieldAbnormal> {


    TraceIntoOptionStatisticsDTO statistics(LocalDate localDate, Long nursingId, Long formId);




}
