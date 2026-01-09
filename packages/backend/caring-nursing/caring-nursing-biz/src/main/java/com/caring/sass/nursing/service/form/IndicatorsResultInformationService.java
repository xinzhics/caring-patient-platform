package com.caring.sass.nursing.service.form;


import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.form.EliminateDto;
import com.caring.sass.nursing.dto.form.MonitorUnprocessedDTO;
import com.caring.sass.nursing.entity.form.IndicatorsResultInformation;
import com.caring.sass.nursing.vo.*;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者监测数据结果及处理表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-14
 */
public interface IndicatorsResultInformationService extends SuperService<IndicatorsResultInformation> {
    /**
     * 各检测计划的异常数据
     * @param tenantCode
     * @return
     */
    List<AbnormalDataVo> abnormalData(String tenantCode);

    /**
     * 异常数据未处理列表
     * @param params
     * @return
     */
    MonitorUnprocessedAndTotal dataUnprocessedList(PageParams<MonitorUnprocessedDTO> params);

    /**
     * 异常数据已处理列表
     * @param params
     * @return
     */
    MonitorProcessedAndTotal dataProcessedList(PageParams<MonitorUnprocessedDTO> params);


    /**
     * 异常数据单个处理或全部处理
     * @param dto
     * @return
     */
    R UnprocessedEliminate(EliminateDto dto);

    /**
     * 已处理异常数据全部清除
     * @return
     */
    R ProcessedEliminate(Long planId);

    /**
     * 统计异常数量
     * @param date
     * @param planId
     * @return
     */
    SubmittedQuantity statisticsAbnormalQuantity(String date, Long planId);

    /**
     * 监测计划说明
     * @param planId
     * @return
     */
    List<ExplainVo> explain(Long planId);

    /**
     * 监测数据异常患者数
     * @param tenantCode
     * @return
     */
    Integer monitorUnusualPatients(String tenantCode);
}
