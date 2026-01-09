package com.caring.sass.nursing.service.traceInto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.traceInto.AppTracePlanList;
import com.caring.sass.nursing.dto.traceInto.TraceFormPatientResultPageDTO;
import com.caring.sass.nursing.entity.traceInto.TraceIntoFormResultLastPushTime;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 选项跟踪患者最新上传时间记录表
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
public interface TraceIntoFormResultLastPushTimeService extends SuperService<TraceIntoFormResultLastPushTime> {

    /**
     * 查询随访计划列表和 随访计划下的未处理患者数量
     * @param nursingId
     * @return
     */
    List<AppTracePlanList> getAppTracePlanList(Long nursingId);


    /**
     * 统计随访下的 已处理患者和未处理患者
     * @param nursingId
     * @param planId
     * @return
     */
    AppTracePlanList countHandleNumber(Long nursingId, Long planId);


    /**
     * 统计医助 未处理的 患者数量
     * @param nursingId
     * @return
     */
    int countNursingHandleNumber(Long nursingId);


    /**
     * app处理某患者的异常数据
     * @param patientId
     * @param formId
     */
    void handleResult(Long patientId, Long formId);


    /**
     * 医助处理自己的全部患者数据
     * @param nursingId
     * @param formId
     */
    void allHandleResult(Long nursingId, Long formId);


    /**
     * 医助分页数据查询
     * @param page
     * @param lbqWrapper
     * @return
     */
    IPage<TraceFormPatientResultPageDTO> appPage(IPage<TraceIntoFormResultLastPushTime> page, LbqWrapper<TraceIntoFormResultLastPushTime> lbqWrapper);


    /**
     * 全部清空数据
     * @param nursingId
     * @param formId
     */
    void allClearData(Long nursingId, Long formId);
}
